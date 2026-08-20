package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dailywork.dao.DailyWorkDao;
import dailywork.model.DailyWorkPayrollResult;
import dailywork.model.DailyWorkPayrollRow;
import employee.dao.EmployeeDao;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.dao.WageDao;
import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentCalculationRequest;
import wage.model.WagePaymentCalculationResult;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentItemInput;
import wage.model.WageTypeSystemIds;

// 일용직 급여입력 조회 Service
public class DailyWagePaymentInputService {

	private DailyWorkDao dailyWorkDao = new DailyWorkDao();
	private EmployeeDao employeeDao = new EmployeeDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();
	private WageDao wageDao = new WageDao();
	private WagePaymentCalculationService wagePaymentCalculationService = new WagePaymentCalculationService();

	public DailyWorkPayrollResult getWorkResult(
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate) {

		validateEmployeeId(employeeId);
		validateSettlementPeriod(
			settlementStartDate,
			settlementEndDate);

		try (Connection conn = ConnectionProvider.getConnection()) {

			validateDailyEmployee(
				conn,
				employeeId);

			return buildWorkResult(
				conn,
				employeeId,
				settlementStartDate,
				settlementEndDate);

		} catch (SQLException e) {

			throw new RuntimeException(
				"일용직 급여 근무기록 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WagePaymentEmployeeRow> getSavedEmployees(
		String wageMonth,
		String wagePeriod) {

		String normalizedWageMonth = normalizeWageMonth(wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(wagePeriod);

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectDailyWagePaymentEmployeeRows(
				conn,
				normalizedWageMonth,
				normalizedWagePeriod);

		} catch (SQLException e) {

			throw new RuntimeException(
				"일용직 급여 저장 사원 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WagePaymentInputViewItem> getDeductionViewItems(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate) {

		String normalizedWageMonth = normalizeWageMonth(wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(wagePeriod);

		if (employeeId != null) {

			validateEmployeeId(employeeId);

			validateSettlementPeriod(
				settlementStartDate,
				settlementEndDate);
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			Set<Integer> activeWageTypeIds = getActiveWageTypeIds(conn);

			if (employeeId != null) {

				validateDailyEmployee(
					conn,
					employeeId);

				List<WagePaymentCalculationItem> savedItems = wageDao.selectEmployeeWageItems(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod);

				/*
				 * 저장된 급여가 있으면 당시 사원의
				 * 공제항목 스냅샷과 저장 금액을 그대로 사용한다.
				 */
				if (!savedItems.isEmpty()) {

					return buildSavedDeductionViewItems(
						savedItems,
						activeWageTypeIds);
				}
			}

			List<WageType> deductionWageTypes = resolveDeductionWageTypes(
				conn,
				normalizedWageMonth,
				normalizedWagePeriod);

			DailyWorkPayrollResult workResult = null;

			if (employeeId != null) {

				workResult = buildWorkResult(
					conn,
					employeeId,
					settlementStartDate,
					settlementEndDate);
			}

			return buildNewDeductionViewItems(
				deductionWageTypes,
				activeWageTypeIds,
				workResult);

		} catch (SQLException e) {

			throw new RuntimeException(
				"일용직 급여 공제항목 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public WagePaymentAutoCalculationResult calculate(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate,
		List<WagePaymentItemInput> currentDeductionInputs) {

		String normalizedWageMonth = normalizeWageMonth(wageMonth);
		String normalizedWagePeriod = normalizeWagePeriod(wagePeriod);

		DailyWorkPayrollResult workResult = getWorkResult(
			employeeId,
			settlementStartDate,
			settlementEndDate);

		List<WagePaymentInputViewItem> baseItems = getDeductionViewItems(
			employeeId,
			normalizedWageMonth,
			normalizedWagePeriod,
			settlementStartDate,
			settlementEndDate);

		if (currentDeductionInputs == null) {
			throw new IllegalArgumentException(
				"공제항목 정보가 올바르지 않습니다.");
		}

		Map<Integer, WagePaymentInputViewItem> baseItemMap = new LinkedHashMap<>();

		for (WagePaymentInputViewItem item : baseItems) {

			if (item == null
				|| item.getWageTypeId() == null
				|| !"D".equals(item.getItemType())) {

				throw new IllegalStateException(
					"공제항목 기준정보가 올바르지 않습니다.");
			}

			if (baseItemMap.put(item.getWageTypeId(), item) != null) {
				throw new IllegalStateException(
					"중복된 공제항목 기준정보가 존재합니다.");
			}
		}

		Map<Integer, Long> currentValueMap = new LinkedHashMap<>();

		for (WagePaymentItemInput input : currentDeductionInputs) {

			if (input == null || input.getWageTypeId() == null) {
				throw new IllegalArgumentException(
					"공제항목 정보가 올바르지 않습니다.");
			}

			Integer wageTypeId = input.getWageTypeId();

			if (!baseItemMap.containsKey(wageTypeId)) {
				throw new IllegalArgumentException(
					"화면에 존재하지 않는 공제항목이 포함되어 있습니다.");
			}

			if (currentValueMap.containsKey(wageTypeId)) {
				throw new IllegalArgumentException(
					"중복된 공제항목이 포함되어 있습니다.");
			}

			long wageValue = input.getWageValue() == null
				? 0L
				: input.getWageValue();

			if (wageValue < 0L) {
				throw new IllegalArgumentException(
					"공제금액은 0원 이상이어야 합니다.");
			}

			currentValueMap.put(wageTypeId, wageValue);
		}

		if (currentValueMap.size() != baseItems.size()) {
			throw new IllegalArgumentException(
				"공제항목 일부가 누락되었습니다.");
		}

		long totalPayment = requireNonNegative(
			workResult.getTotalPayment(),
			"DAILY_WORK 지급총액");

		long totalIncomeTax = requireNonNegative(
			workResult.getTotalIncomeTax(),
			"DAILY_WORK 소득세");

		long totalLocalTax = requireNonNegative(
			workResult.getTotalLocalTax(),
			"DAILY_WORK 지방소득세");

		List<WagePaymentItemInput> calculationInputs = new ArrayList<>();

		calculationInputs.add(
			new WagePaymentItemInput(
				WageTypeSystemIds.BASIC_PAY_ID,
				totalPayment));

		for (WagePaymentInputViewItem baseItem : baseItems) {

			Integer wageTypeId = baseItem.getWageTypeId();
			long wageValue = currentValueMap.get(wageTypeId);

			if (Integer.valueOf(WageTypeSystemIds.INCOME_TAX_ID)
				.equals(wageTypeId)) {

				wageValue = totalIncomeTax;

			} else if (Integer.valueOf(
				WageTypeSystemIds.LOCAL_INCOME_TAX_ID)
				.equals(wageTypeId)) {

				wageValue = totalLocalTax;
			}

			calculationInputs.add(
				new WagePaymentItemInput(
					wageTypeId,
					wageValue));
		}

		WagePaymentCalculationResult calculationResult = wagePaymentCalculationService.calculate(
			new WagePaymentCalculationRequest(
				employeeId,
				normalizedWageMonth,
				calculationInputs));

		Map<Integer, Long> calculatedInsuranceMap = new LinkedHashMap<>();

		for (WagePaymentCalculationItem item : calculationResult.getDeductionItems()) {

			if (item == null || item.getWageTypeId() == null) {
				throw new IllegalStateException(
					"자동계산 결과가 올바르지 않습니다.");
			}

			if (!isSocialInsuranceId(item.getWageTypeId())) {
				continue;
			}

			long wageValue = item.getWageValue() == null
				? 0L
				: item.getWageValue();

			if (calculatedInsuranceMap.put(
				item.getWageTypeId(),
				wageValue) != null) {

				throw new IllegalStateException(
					"자동계산 결과에 중복된 보험항목이 있습니다.");
			}
		}

		List<WagePaymentInputViewItem> mergedItems = new ArrayList<>();

		long totalDeduction = 0L;

		for (WagePaymentInputViewItem baseItem : baseItems) {

			Integer wageTypeId = baseItem.getWageTypeId();
			long finalValue;

			if (isSocialInsuranceId(wageTypeId)) {

				Long calculatedValue = calculatedInsuranceMap.get(wageTypeId);

				finalValue = calculatedValue == null
					? 0L
					: calculatedValue;

			} else if (Integer.valueOf(
				WageTypeSystemIds.INCOME_TAX_ID)
				.equals(wageTypeId)) {

				finalValue = totalIncomeTax;

			} else if (Integer.valueOf(
				WageTypeSystemIds.LOCAL_INCOME_TAX_ID)
				.equals(wageTypeId)) {

				finalValue = totalLocalTax;

			} else {

				finalValue = currentValueMap.get(wageTypeId);
			}

			mergedItems.add(
				new WagePaymentInputViewItem(
					baseItem.getWageTypeId(),
					baseItem.getWageTypeName(),
					baseItem.getItemType(),
					baseItem.getTaxableYn(),
					finalValue,
					baseItem.isActive(),
					baseItem.isCalculable()));

			totalDeduction += finalValue;
		}

		long netPayment = totalPayment - totalDeduction;

		if (calculationResult.getTotalPayment() == null
			|| calculationResult.getTotalPayment() != totalPayment
			|| calculationResult.getTotalDeduction() == null
			|| calculationResult.getTotalDeduction() != totalDeduction
			|| calculationResult.getNetPayment() == null
			|| calculationResult.getNetPayment() != netPayment) {

			throw new IllegalStateException(
				"일용직 급여 자동계산 결과의 합계가 일치하지 않습니다.");
		}

		return new WagePaymentAutoCalculationResult(
			mergedItems,
			totalPayment,
			totalDeduction,
			netPayment);
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"사원 정보가 올바르지 않습니다.");
		}
	}

	private void validateSettlementPeriod(
		Date settlementStartDate,
		Date settlementEndDate) {

		if (settlementStartDate == null
			|| settlementEndDate == null) {

			throw new IllegalArgumentException(
				"정산기간이 올바르지 않습니다.");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalArgumentException(
				"정산 시작일은 종료일보다 늦을 수 없습니다.");
		}
	}

	private DailyWorkPayrollResult buildWorkResult(
		Connection conn,
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate)
		throws SQLException {

		List<DailyWorkPayrollRow> workRows = dailyWorkDao.selectPayrollRows(
			conn,
			employeeId,
			settlementStartDate,
			settlementEndDate);

		long totalPayment = 0L;
		long totalIncomeTax = 0L;
		long totalLocalTax = 0L;

		for (DailyWorkPayrollRow workRow : workRows) {

			totalPayment += workRow.getPaymentAmount();
			totalIncomeTax += workRow.getIncomeTax();
			totalLocalTax += workRow.getLocalTax();
		}

		return new DailyWorkPayrollResult(
			workRows,
			totalPayment,
			totalIncomeTax,
			totalLocalTax);
	}

	private List<WageType> resolveDeductionWageTypes(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		List<WagePaymentEmployeeRow> savedEmployees = wageDao.selectDailyWagePaymentEmployeeRows(
			conn,
			wageMonth,
			wagePeriod);

		List<WageType> workspaceWageTypes = wageTypeDao.selectDailyWorkspaceDeductionTypes(
			conn,
			wageMonth,
			wagePeriod);

		/*
		 * 저장된 일용직 사원이 있으면 기존 작업공간이다.
		 * 당시 공제항목이 0개였더라도 현재 활성항목을 새로 추가하지 않는다.
		 */
		if (!savedEmployees.isEmpty()) {
			return workspaceWageTypes;
		}

		/*
		 * 일용직 급여가 한 건도 없는 신규 작업공간일 때만
		 * 현재 usage='Y'인 공제항목을 사용한다.
		 */
		List<WageType> activeWageTypes = wageTypeDao.selectActiveWageTypes(conn);

		List<WageType> result = new ArrayList<>();

		for (WageType wageType : activeWageTypes) {

			if ("D".equals(wageType.getItemType())) {
				result.add(wageType);
			}
		}

		return result;
	}

	private Set<Integer> getActiveWageTypeIds(
		Connection conn)
		throws SQLException {

		List<WageType> activeWageTypes = wageTypeDao.selectActiveWageTypes(conn);

		Set<Integer> result = new HashSet<>();

		for (WageType wageType : activeWageTypes) {
			result.add(wageType.getWageTypeId());
		}

		return result;
	}

	private List<WagePaymentInputViewItem> buildSavedDeductionViewItems(
		List<WagePaymentCalculationItem> savedItems,
		Set<Integer> activeWageTypeIds) {

		List<WagePaymentInputViewItem> result = new ArrayList<>();

		for (WagePaymentCalculationItem item : savedItems) {

			if (!"D".equals(item.getItemType())) {
				continue;
			}

			result.add(
				new WagePaymentInputViewItem(
					item.getWageTypeId(),
					item.getWageTypeName(),
					item.getItemType(),
					item.getTaxableYn(),
					item.getWageValue(),
					activeWageTypeIds.contains(
						item.getWageTypeId()),
					true));
		}

		return result;
	}

	private List<WagePaymentInputViewItem> buildNewDeductionViewItems(
		List<WageType> deductionWageTypes,
		Set<Integer> activeWageTypeIds,
		DailyWorkPayrollResult workResult) {

		List<WagePaymentInputViewItem> result = new ArrayList<>();

		long incomeTax = workResult == null
			? 0L
			: workResult.getTotalIncomeTax();

		long localTax = workResult == null
			? 0L
			: workResult.getTotalLocalTax();

		for (WageType wageType : deductionWageTypes) {

			long wageValue = 0L;

			if (WageTypeSystemIds.INCOME_TAX_ID == wageType.getWageTypeId()) {

				wageValue = incomeTax;

			} else if (WageTypeSystemIds.LOCAL_INCOME_TAX_ID == wageType.getWageTypeId()) {

				wageValue = localTax;
			}

			result.add(
				new WagePaymentInputViewItem(
					wageType.getWageTypeId(),
					wageType.getWageTypeName(),
					wageType.getItemType(),
					wageType.getTaxableYn(),
					wageValue,
					activeWageTypeIds.contains(
						wageType.getWageTypeId()),
					true));
		}

		return result;
	}

	private void validateDailyEmployee(
		Connection conn,
		Integer employeeId)
		throws SQLException {

		String employmentType = employeeDao.selectEmploymentType(
			conn,
			employeeId);

		if (employmentType == null) {

			throw new IllegalArgumentException(
				"존재하지 않는 사원입니다.");
		}

		if (!"일용직".equals(employmentType)) {

			throw new IllegalArgumentException(
				"일용직 사원만 급여입력할 수 있습니다.");
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		try {

			return YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
		}

		try {

			int wagePeriodNumber = Integer.parseInt(wagePeriod.trim());

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

			return String.valueOf(wagePeriodNumber);

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
		}
	}

	private long requireNonNegative(
		Long value,
		String fieldName) {

		long normalizedValue = value == null
			? 0L
			: value;

		if (normalizedValue < 0L) {
			throw new IllegalArgumentException(
				fieldName + "은 0원 이상이어야 합니다.");
		}

		return normalizedValue;
	}

	private boolean isSocialInsuranceId(
		Integer wageTypeId) {

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
}