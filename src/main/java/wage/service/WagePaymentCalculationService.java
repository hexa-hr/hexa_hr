package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import employee.dao.EmployeeDao;
import employee.dao.InsuranceDao;
import employee.model.EmployeeInsurance;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentCalculationRequest;
import wage.model.WagePaymentCalculationResult;
import wage.model.WagePaymentItemInput;
import wage.model.WageTypeSystemIds;

// 급여 자동계산 서비스
public class WagePaymentCalculationService {

	private static final double NATIONAL_PENSION_RATE = 0.0475;
	private static final double HEALTH_INSURANCE_RATE = 0.03595;
	private static final double LONG_TERM_CARE_RATE = 0.13136;
	private static final double EMPLOYMENT_INSURANCE_RATE = 0.009;

	private WageTypeDao wageTypeDao = new WageTypeDao();
	private EmployeeDao employeeDao = new EmployeeDao();
	private InsuranceDao insuranceDao = new InsuranceDao();

	public WagePaymentCalculationResult calculate(
		WagePaymentCalculationRequest request) {

		validateRequest(request);

		try (Connection conn = ConnectionProvider.getConnection()) {

			// 사원의 고용형태 조회
			String employmentType = employeeDao.selectEmploymentType(
				conn,
				request.getEmployeeId());

			if (employmentType == null) {
				throw new IllegalArgumentException(
					"존재하지 않는 사원입니다.");
			}

			// 고용형태에 따른 급여 유형 판정
			String wageCategory = determineWageCategory(employmentType);

			List<WageType> wageTypes = wageTypeDao.selectAllWageTypes(conn);

			Map<Integer, WageType> wageTypeMap = new LinkedHashMap<>();

			for (WageType wageType : wageTypes) {
				wageTypeMap.put(
					wageType.getWageTypeId(),
					wageType);
			}

			List<WagePaymentCalculationItem> paymentItems = new ArrayList<>();

			List<WagePaymentCalculationItem> deductionItems = new ArrayList<>();

			Set<Integer> inputWageTypeIds = new HashSet<>();

			long totalPayment = 0L;
			long taxFreeAmount = 0L;
			long totalDeduction = 0L;

			for (WagePaymentItemInput input : request.getItemInputs()) {

				if (input == null || input.getWageTypeId() == null) {
					throw new IllegalArgumentException(
						"급여항목 정보가 올바르지 않습니다.");
				}

				Integer wageTypeId = input.getWageTypeId();

				if (!inputWageTypeIds.add(wageTypeId)) {
					throw new IllegalArgumentException(
						"중복된 급여항목이 포함되어 있습니다.");
				}

				WageType wageType = wageTypeMap.get(wageTypeId);

				if (wageType == null) {
					throw new IllegalArgumentException(
						"존재하지 않는 급여항목입니다.");
				}

				long wageValue = 0L;

				if (input.getWageValue() != null) {
					wageValue = input.getWageValue();
				}

				if (wageValue < 0) {
					throw new IllegalArgumentException(
						"급여금액은 0원 이상이어야 합니다.");
				}

				WagePaymentCalculationItem item = new WagePaymentCalculationItem(
					wageType.getWageTypeId(),
					wageType.getWageTypeName(),
					wageType.getItemType(),
					wageType.getTaxableYn(),
					wageValue);

				if ("P".equals(wageType.getItemType())) {

					paymentItems.add(item);
					totalPayment += wageValue;

					if ("N".equals(wageType.getTaxableYn())) {

						long taxFreeLimit = 0L;

						if (wageType.getTaxFreeLimit() != null) {
							taxFreeLimit = wageType.getTaxFreeLimit();
						}

						taxFreeAmount += Math.min(wageValue, taxFreeLimit);
					}

				} else if ("D".equals(wageType.getItemType())) {

					if (("WORKER".equals(wageCategory)
						|| "BUSINESS".equals(wageCategory))
						&& isIncomeTaxType(wageTypeId)) {

						continue;
					}

					if (isSocialInsuranceType(wageType)) {
						continue;
					}

					deductionItems.add(item);
					totalDeduction += wageValue;

				} else {

					throw new IllegalStateException(
						"급여항목의 지급·공제 구분이 올바르지 않습니다.");
				}
			}

			long monthlyRemuneration = totalPayment - taxFreeAmount;

			if ("WORKER".equals(wageCategory)) {

				long incomeTax = calculateWorkerIncomeTax(
					monthlyRemuneration);

				long localTax = truncateToTen(
					incomeTax / 10L);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.INCOME_TAX_ID),
					incomeTax);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.LOCAL_INCOME_TAX_ID),
					localTax);
			}

			if ("BUSINESS".equals(wageCategory)) {

				// 사업소득 소득세 = 비과세 지급항목을 포함한 지급총액의 3%
				long incomeTax = totalPayment * 3 / 100;

				// 지방소득세 = 소득세의 10%
				long localTax = roundToTen(
					incomeTax * 0.1);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.INCOME_TAX_ID),
					incomeTax);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.LOCAL_INCOME_TAX_ID),
					localTax);
			}

			long healthInsurance = 0L;

			List<EmployeeInsurance> employeeInsurances = insuranceDao.selectByEmployeeId(
				conn,
				request.getEmployeeId());

			Map<String, Long> insuranceAmountMap = new LinkedHashMap<>();

			for (EmployeeInsurance insurance : employeeInsurances) {

				String insuranceAgency = insurance.getInsuranceAgency();

				if (insuranceAmountMap.containsKey(
					insuranceAgency)) {

					throw new IllegalStateException(
						"중복된 보험 가입정보가 존재합니다: "
							+ insuranceAgency);
				}

				insuranceAmountMap.put(
					insuranceAgency,
					insurance.getInsuranceAmount());
			}

			// 국민연금
			if (insuranceAmountMap.containsKey("국민연금")) {

				long nationalPensionBase = resolveInsuranceBaseAmount(
					insuranceAmountMap.get("국민연금"),
					monthlyRemuneration);

				long nationalPension = roundToTen(
					nationalPensionBase
						* NATIONAL_PENSION_RATE);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.NATIONAL_PENSION_ID),
					nationalPension);
			}

			// 건강보험
			if (insuranceAmountMap.containsKey("건강보험")) {

				long healthInsuranceBase = resolveInsuranceBaseAmount(
					insuranceAmountMap.get("건강보험"),
					monthlyRemuneration);

				healthInsurance = roundToTen(
					healthInsuranceBase
						* HEALTH_INSURANCE_RATE);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.HEALTH_INSURANCE_ID),
					healthInsurance);
			}

			// 장기요양보험
			if (insuranceAmountMap.containsKey("장기요양보험")) {

				long longTermCare = roundToTen(
					healthInsurance
						* LONG_TERM_CARE_RATE);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.LONG_TERM_CARE_ID),
					longTermCare);
			}

			// 고용보험
			if (insuranceAmountMap.containsKey("고용보험")) {

				long employmentInsuranceBase = resolveInsuranceBaseAmount(
					insuranceAmountMap.get("고용보험"),
					monthlyRemuneration);

				long employmentInsurance = roundToTen(
					employmentInsuranceBase
						* EMPLOYMENT_INSURANCE_RATE);

				totalDeduction += addCalculatedDeduction(
					deductionItems,
					findInputWageTypeById(
						wageTypeMap,
						inputWageTypeIds,
						WageTypeSystemIds.EMPLOYMENT_INSURANCE_ID),
					employmentInsurance);
			}

			long netPayment = totalPayment - totalDeduction;

			return new WagePaymentCalculationResult(
				paymentItems,
				deductionItems,
				totalPayment,
				taxFreeAmount,
				monthlyRemuneration,
				totalDeduction,
				netPayment);

		} catch (SQLException e) {
			throw new RuntimeException(
				"급여 자동계산 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	// 고용형태에 따른 급여 유형 판정
	private String determineWageCategory(
		String employmentType) {

		if ("임시직".equals(employmentType)) {
			return "BUSINESS";
		}

		if ("일용직".equals(employmentType)) {
			return "DAILY";
		}

		return "WORKER";
	}

	private long calculateWorkerIncomeTax(
		long monthlyTaxablePay) {

		if (monthlyTaxablePay <= 0L) {
			return 0L;
		}

		long rate;
		long progressiveDeduction;

		if (monthlyTaxablePay <= 1_000_000L) {
			rate = 6L;
			progressiveDeduction = 0L;

		} else if (monthlyTaxablePay <= 4_000_000L) {
			rate = 15L;
			progressiveDeduction = 90_000L;

		} else if (monthlyTaxablePay <= 7_000_000L) {
			rate = 24L;
			progressiveDeduction = 450_000L;

		} else if (monthlyTaxablePay <= 12_000_000L) {
			rate = 35L;
			progressiveDeduction = 1_220_000L;

		} else if (monthlyTaxablePay <= 25_000_000L) {
			rate = 38L;
			progressiveDeduction = 1_580_000L;

		} else if (monthlyTaxablePay <= 40_000_000L) {
			rate = 40L;
			progressiveDeduction = 2_080_000L;

		} else if (monthlyTaxablePay <= 80_000_000L) {
			rate = 42L;
			progressiveDeduction = 2_880_000L;

		} else {
			rate = 45L;
			progressiveDeduction = 5_280_000L;
		}

		long incomeTax = monthlyTaxablePay * rate / 100L
			- progressiveDeduction;

		return truncateToTen(
			Math.max(incomeTax, 0L));
	}

	private long truncateToTen(long value) {

		if (value <= 0L) {
			return 0L;
		}

		return value / 10L * 10L;
	}

	private boolean isIncomeTaxType(
		Integer wageTypeId) {

		return Integer.valueOf(
			WageTypeSystemIds.INCOME_TAX_ID)
			.equals(wageTypeId)
			|| Integer.valueOf(
				WageTypeSystemIds.LOCAL_INCOME_TAX_ID)
				.equals(wageTypeId);
	}

	// 10원 단위 반올림
	private long roundToTen(double value) {
		return Math.round(value / 10.0) * 10L;
	}

	// 보험별 기준금액 결정
	private long resolveInsuranceBaseAmount(
		Long insuranceAmount,
		long monthlyRemuneration) {

		if (insuranceAmount != null
			&& insuranceAmount > 0L) {

			return insuranceAmount;
		}

		return monthlyRemuneration;
	}

	private WageType findInputWageTypeById(
		Map<Integer, WageType> wageTypeMap,
		Set<Integer> inputWageTypeIds,
		int wageTypeId) {

		if (!inputWageTypeIds.contains(
			wageTypeId)) {

			return null;
		}

		return wageTypeMap.get(
			wageTypeId);
	}

	private long addCalculatedDeduction(
		List<WagePaymentCalculationItem> deductionItems,
		WageType wageType,
		long wageValue) {

		if (wageType == null) {
			return 0L;
		}

		deductionItems.add(
			new WagePaymentCalculationItem(
				wageType.getWageTypeId(),
				wageType.getWageTypeName(),
				wageType.getItemType(),
				wageType.getTaxableYn(),
				wageValue));

		return wageValue;
	}

	private boolean isSocialInsuranceType(
		WageType wageType) {

		if (!"D".equals(wageType.getItemType())) {
			return false;
		}

		Integer wageTypeId = wageType.getWageTypeId();

		return Integer.valueOf(
			WageTypeSystemIds.NATIONAL_PENSION_ID)
			.equals(wageTypeId)
			|| Integer.valueOf(
				WageTypeSystemIds.HEALTH_INSURANCE_ID)
				.equals(wageTypeId)
			|| Integer.valueOf(
				WageTypeSystemIds.LONG_TERM_CARE_ID)
				.equals(wageTypeId)
			|| Integer.valueOf(
				WageTypeSystemIds.EMPLOYMENT_INSURANCE_ID)
				.equals(wageTypeId);
	}

	private void validateRequest(
		WagePaymentCalculationRequest request) {

		if (request == null) {
			throw new IllegalArgumentException(
				"급여 계산 요청 정보가 없습니다.");
		}

		if (request.getEmployeeId() == null
			|| request.getEmployeeId() <= 0) {

			throw new IllegalArgumentException(
				"사원 정보가 올바르지 않습니다.");
		}

		String wageMonth = request.getWageMonth();

		if (wageMonth == null) {
			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		wageMonth = wageMonth.trim();

		try {
			YearMonth.parse(wageMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		if (request.getItemInputs() == null) {
			throw new IllegalArgumentException(
				"급여항목 정보가 없습니다.");
		}
	}
}