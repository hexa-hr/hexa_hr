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
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentCalculationRequest;
import wage.model.WagePaymentCalculationResult;
import wage.model.WagePaymentItemInput;

// 급여 자동계산 서비스
public class WagePaymentCalculationService {

	private static final double NATIONAL_PENSION_RATE = 0.0475;
	private static final double HEALTH_INSURANCE_RATE = 0.03595;
	private static final double LONG_TERM_CARE_RATE = 0.13136;
	private static final double EMPLOYMENT_INSURANCE_RATE = 0.009;

	private WageTypeDao wageTypeDao = new WageTypeDao();
	private EmployeeDao employeeDao = new EmployeeDao();

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

			List<WageType> wageTypes = wageTypeDao.selectActiveWageTypes(conn);

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
			long businessIncome = 0L;

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
						"현재 사용 중이지 않거나 존재하지 않는 급여항목입니다.");
				}

				long wageValue = 0L;

				if (input.getWageValue() != null) {
					wageValue = input.getWageValue();
				}

				if (wageValue < 0) {
					throw new IllegalArgumentException(
						"급여금액은 0원 이상이어야 합니다.");
				}

				// 일반 근로소득자의 허용되지 않는 지급항목 검증
				if ("WORKER".equals(wageCategory)
					&& "P".equals(wageType.getItemType())
					&& ("사업소득".equals(wageType.getWageTypeName())
						|| "일용급여".equals(wageType.getWageTypeName()))) {

					if (wageValue == 0L) {
						continue;
					}

					throw new IllegalArgumentException(
						"일반급여 대상자에게 사용할 수 없는 지급항목입니다: "
							+ wageType.getWageTypeName());
				}

				// 사업소득자의 허용 급여항목 검증
				if ("BUSINESS".equals(wageCategory)
					&& !isBusinessWageType(wageType)) {

					// 화면에서 0원 항목까지 전달되는 경우는 무시
					if (wageValue == 0L) {
						continue;
					}

					throw new IllegalArgumentException(
						"사업소득자에게 사용할 수 없는 급여항목입니다: "
							+ wageType.getWageTypeName());
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

					if ("BUSINESS".equals(wageCategory)
						&& "사업소득".equals(wageType.getWageTypeName())) {

						businessIncome = wageValue;
					}

					if ("N".equals(wageType.getTaxableYn())) {

						long taxFreeLimit = 0L;

						if (wageType.getTaxFreeLimit() != null) {
							taxFreeLimit = wageType.getTaxFreeLimit();
						}

						taxFreeAmount += Math.min(wageValue, taxFreeLimit);
					}

				} else if ("D".equals(wageType.getItemType())) {

					if ("BUSINESS".equals(wageCategory)
						&& ("소득세".equals(wageType.getWageTypeName())
							|| "지방소득세".equals(wageType.getWageTypeName()))) {

						continue;
					}

					if ("WORKER".equals(wageCategory)
						&& isWorkerInsuranceType(wageType)) {

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

			if ("BUSINESS".equals(wageCategory)) {

				WageType incomeTaxType = findWageTypeByName(
					wageTypes,
					"소득세",
					"D");

				WageType localTaxType = findWageTypeByName(
					wageTypes,
					"지방소득세",
					"D");

				// 사업소득 소득세 3%
				long incomeTax = businessIncome * 3 / 100;

				// 지방소득세 = 소득세의 10%
				long localTax = roundToTen(incomeTax * 0.1);

				deductionItems.add(
					new WagePaymentCalculationItem(
						incomeTaxType.getWageTypeId(),
						incomeTaxType.getWageTypeName(),
						incomeTaxType.getItemType(),
						incomeTaxType.getTaxableYn(),
						incomeTax));

				deductionItems.add(
					new WagePaymentCalculationItem(
						localTaxType.getWageTypeId(),
						localTaxType.getWageTypeName(),
						localTaxType.getItemType(),
						localTaxType.getTaxableYn(),
						localTax));

				totalDeduction += incomeTax + localTax;
			}

			if ("WORKER".equals(wageCategory)) {

				WageType nationalPensionType = findWageTypeByName(
					wageTypes,
					"국민연금",
					"D");

				WageType healthInsuranceType = findWageTypeByName(
					wageTypes,
					"건강보험",
					"D");

				WageType longTermCareType = findWageTypeByName(
					wageTypes,
					"장기요양보험",
					"D");

				WageType employmentInsuranceType = findWageTypeByName(
					wageTypes,
					"고용보험",
					"D");

				long nationalPension = roundToTen(
					monthlyRemuneration
						* NATIONAL_PENSION_RATE);

				long healthInsurance = roundToTen(
					monthlyRemuneration
						* HEALTH_INSURANCE_RATE);

				long longTermCare = roundToTen(
					healthInsurance
						* LONG_TERM_CARE_RATE);

				long employmentInsurance = roundToTen(
					monthlyRemuneration
						* EMPLOYMENT_INSURANCE_RATE);

				deductionItems.add(
					new WagePaymentCalculationItem(
						nationalPensionType.getWageTypeId(),
						nationalPensionType.getWageTypeName(),
						nationalPensionType.getItemType(),
						nationalPensionType.getTaxableYn(),
						nationalPension));

				deductionItems.add(
					new WagePaymentCalculationItem(
						healthInsuranceType.getWageTypeId(),
						healthInsuranceType.getWageTypeName(),
						healthInsuranceType.getItemType(),
						healthInsuranceType.getTaxableYn(),
						healthInsurance));

				deductionItems.add(
					new WagePaymentCalculationItem(
						longTermCareType.getWageTypeId(),
						longTermCareType.getWageTypeName(),
						longTermCareType.getItemType(),
						longTermCareType.getTaxableYn(),
						longTermCare));

				deductionItems.add(
					new WagePaymentCalculationItem(
						employmentInsuranceType.getWageTypeId(),
						employmentInsuranceType.getWageTypeName(),
						employmentInsuranceType.getItemType(),
						employmentInsuranceType.getTaxableYn(),
						employmentInsurance));

				totalDeduction += nationalPension
					+ healthInsurance
					+ longTermCare
					+ employmentInsurance;
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

	// 10원 단위 반올림
	private long roundToTen(double value) {
		return Math.round(value / 10.0) * 10L;
	}

	private boolean isBusinessWageType(
		WageType wageType) {

		String itemType = wageType.getItemType();
		String wageTypeName = wageType.getWageTypeName();

		if ("P".equals(itemType)) {
			return "사업소득".equals(wageTypeName);
		}

		if ("D".equals(itemType)) {
			return "소득세".equals(wageTypeName)
				|| "지방소득세".equals(wageTypeName);
		}

		return false;
	}

	private WageType findWageTypeByName(
		List<WageType> wageTypes,
		String wageTypeName,
		String itemType) {

		for (WageType wageType : wageTypes) {

			if (wageTypeName.equals(
				wageType.getWageTypeName())
				&& itemType.equals(
					wageType.getItemType())) {

				return wageType;
			}
		}

		throw new IllegalStateException(
			"필수 급여항목이 존재하지 않습니다: "
				+ wageTypeName);
	}

	private boolean isWorkerInsuranceType(
		WageType wageType) {

		if (!"D".equals(wageType.getItemType())) {
			return false;
		}

		String wageTypeName = wageType.getWageTypeName();

		return "국민연금".equals(wageTypeName)
			|| "건강보험".equals(wageTypeName)
			|| "장기요양보험".equals(wageTypeName)
			|| "고용보험".equals(wageTypeName);
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