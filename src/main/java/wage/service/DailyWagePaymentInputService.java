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

// 日雇い給与入力照会Service
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
				"日雇い給与の勤務記録照会中にデータベースエラーが発生しました。",
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
				"日雇い給与の保存済み社員照会中にデータベースエラーが発生しました。",
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
				 * 保存済み給与がある場合は、その時点の社員の
				 * 控除項目スナップショットと保存金額をそのまま使用する。
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
				"日雇い給与の控除項目照会中にデータベースエラーが発生しました。",
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
				"控除項目情報が正しくありません。");
		}

		Map<Integer, WagePaymentInputViewItem> baseItemMap = new LinkedHashMap<>();

		for (WagePaymentInputViewItem item : baseItems) {

			if (item == null
				|| item.getWageTypeId() == null
				|| !"D".equals(item.getItemType())) {

				throw new IllegalStateException(
					"控除項目の基準情報が正しくありません。");
			}

			if (baseItemMap.put(item.getWageTypeId(), item) != null) {
				throw new IllegalStateException(
					"重複した控除項目の基準情報が存在します。");
			}
		}

		Map<Integer, Long> currentValueMap = new LinkedHashMap<>();

		for (WagePaymentItemInput input : currentDeductionInputs) {

			if (input == null || input.getWageTypeId() == null) {
				throw new IllegalArgumentException(
					"控除項目情報が正しくありません。");
			}

			Integer wageTypeId = input.getWageTypeId();

			if (!baseItemMap.containsKey(wageTypeId)) {
				throw new IllegalArgumentException(
					"画面に存在しない控除項目が含まれています。");
			}

			if (currentValueMap.containsKey(wageTypeId)) {
				throw new IllegalArgumentException(
					"重複した控除項目が含まれています。");
			}

			long wageValue = input.getWageValue() == null
				? 0L
				: input.getWageValue();

			if (wageValue < 0L) {
				throw new IllegalArgumentException(
					"控除金額は0ウォン以上である必要があります。");
			}

			currentValueMap.put(wageTypeId, wageValue);
		}

		if (currentValueMap.size() != baseItems.size()) {
			throw new IllegalArgumentException(
				"控除項目の一部が欠落しています。");
		}

		long totalPayment = requireNonNegative(
			workResult.getTotalPayment(),
			"DAILY_WORKの支給総額");

		long totalIncomeTax = requireNonNegative(
			workResult.getTotalIncomeTax(),
			"DAILY_WORKの所得税");

		long totalLocalTax = requireNonNegative(
			workResult.getTotalLocalTax(),
			"DAILY_WORKの住民税");

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
				settlementStartDate,
				settlementEndDate,
				calculationInputs));

		Map<Integer, Long> calculatedInsuranceMap = new LinkedHashMap<>();

		for (WagePaymentCalculationItem item : calculationResult.getDeductionItems()) {

			if (item == null || item.getWageTypeId() == null) {
				throw new IllegalStateException(
					"自動計算結果が正しくありません。");
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
					"自動計算結果に重複した保険項目が含まれています。");
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
				"日雇い給与の自動計算結果の合計が一致しません。");
		}

		return new WagePaymentAutoCalculationResult(
			mergedItems,
			totalPayment,
			totalDeduction,
			netPayment);
	}

	public WagePaymentAutoCalculationResult prepareSaveResult(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate,
		List<WagePaymentItemInput> currentDeductionInputs) {

		String normalizedWageMonth = normalizeWageMonth(
			wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(
			wagePeriod);

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

		if (baseItems == null) {
			throw new IllegalStateException(
				"控除項目の基準情報がありません。");
		}

		if (currentDeductionInputs == null) {
			throw new IllegalArgumentException(
				"控除項目情報が正しくありません。");
		}

		Map<Integer, WagePaymentInputViewItem> baseItemMap = new LinkedHashMap<>();

		for (WagePaymentInputViewItem item : baseItems) {

			if (item == null
				|| item.getWageTypeId() == null
				|| item.getWageTypeId() <= 0
				|| !"D".equals(item.getItemType())) {

				throw new IllegalStateException(
					"控除項目の基準情報が正しくありません。");
			}

			if (baseItemMap.put(
				item.getWageTypeId(),
				item) != null) {

				throw new IllegalStateException(
					"重複した控除項目の基準情報が存在します。");
			}
		}

		Map<Integer, Long> currentValueMap = new LinkedHashMap<>();

		for (WagePaymentItemInput input : currentDeductionInputs) {

			if (input == null
				|| input.getWageTypeId() == null
				|| input.getWageTypeId() <= 0) {

				throw new IllegalArgumentException(
					"控除項目情報が正しくありません。");
			}

			Integer wageTypeId = input.getWageTypeId();

			if (!baseItemMap.containsKey(wageTypeId)) {
				throw new IllegalArgumentException(
					"画面に存在しない控除項目が含まれています。");
			}

			long wageValue = input.getWageValue() == null
				? 0L
				: input.getWageValue();

			if (wageValue < 0L) {
				throw new IllegalArgumentException(
					"控除金額は0ウォン以上である必要があります。");
			}

			if (currentValueMap.put(
				wageTypeId,
				wageValue) != null) {

				throw new IllegalArgumentException(
					"重複した控除項目が含まれています。");
			}
		}

		if (currentValueMap.size() != baseItemMap.size()) {

			throw new IllegalArgumentException(
				"控除項目の一部が欠落しています。");
		}

		long totalPayment = requireNonNegative(
			workResult.getTotalPayment(),
			"DAILY_WORKの支給総額");

		long totalIncomeTax = requireNonNegative(
			workResult.getTotalIncomeTax(),
			"DAILY_WORKの所得税");

		long totalLocalTax = requireNonNegative(
			workResult.getTotalLocalTax(),
			"DAILY_WORKの住民税");

		List<WagePaymentInputViewItem> saveItems = new ArrayList<>();

		long totalDeduction = 0L;

		for (WagePaymentInputViewItem baseItem : baseItems) {

			Integer wageTypeId = baseItem.getWageTypeId();

			long finalValue = currentValueMap.get(wageTypeId);

			/*
			 * 所得税と住民税は画面の値ではなく
			 * DAILY_WORKの合計を使用する。
			 */
			if (Integer.valueOf(
				WageTypeSystemIds.INCOME_TAX_ID)
				.equals(wageTypeId)) {

				finalValue = totalIncomeTax;

			} else if (Integer.valueOf(
				WageTypeSystemIds.LOCAL_INCOME_TAX_ID)
				.equals(wageTypeId)) {

				finalValue = totalLocalTax;
			}

			/*
			 * 4大保険とその他の控除は
			 * 現在画面に表示されている値を維持する。
			 */
			saveItems.add(
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

		return new WagePaymentAutoCalculationResult(
			saveItems,
			totalPayment,
			totalDeduction,
			netPayment);
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"社員情報が正しくありません。");
		}
	}

	private void validateSettlementPeriod(
		Date settlementStartDate,
		Date settlementEndDate) {

		if (settlementStartDate == null
			|| settlementEndDate == null) {

			throw new IllegalArgumentException(
				"精算期間が正しくありません。");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalArgumentException(
				"精算開始日は終了日より後にすることはできません。");
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
		 * 保存済みの日雇い社員がいる場合は既存のワークスペースである。
		 * 当時の控除項目が0件であっても、現在使用中の項目を新たに追加しない。
		 */
		if (!savedEmployees.isEmpty()) {
			return workspaceWageTypes;
		}

		/*
		 * 日雇い給与が1件もない新規ワークスペースの場合のみ
		 * 現在usage='Y'の控除項目を使用する。
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
				"存在しない社員です。");
		}

		if (!"일용직".equals(employmentType)) {

			throw new IllegalArgumentException(
				"日雇い社員のみ給与を入力できます。");
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		try {

			return YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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
				"給与回次は1以上10以下の数値である必要があります。");
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
				fieldName + "は0ウォン以上である必要があります。");
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