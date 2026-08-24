package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdbc.connection.ConnectionProvider;
import master.dao.DepartmentDao;
import master.dao.WageTypeDao;
import master.model.Department;
import master.model.WageTypeOption;
import wage.dao.WageDao;
import wage.model.WageLedgerDetailResult;
import wage.model.WageLedgerDetailRow;
import wage.model.WageLedgerEmployeeRow;
import wage.model.WageLedgerSummary;
import wage.model.WageLedgerSummaryResult;

// 給与台帳照会Service
public class WageLedgerService {

	private WageDao wageDao = new WageDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();
	private DepartmentDao departmentDao = new DepartmentDao();

	public WageLedgerSummaryResult getWageLedgerSummaries(String year) {

		if (year == null || !year.matches("\\d{4}")) {
			throw new IllegalArgumentException(
				"帰属年度はYYYY形式である必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageLedgerSummary> summaries = wageDao.selectWageLedgerSummaries(conn, year);

			long totalPayment = 0L;
			long totalDeduction = 0L;

			for (WageLedgerSummary summary : summaries) {

				if (summary.getTotalPayment() != null) {
					totalPayment += summary.getTotalPayment();
				}

				if (summary.getTotalDeduction() != null) {
					totalDeduction += summary.getTotalDeduction();
				}
			}

			return new WageLedgerSummaryResult(
				summaries,
				totalPayment,
				totalDeduction);

		} catch (SQLException e) {
			throw new RuntimeException(
				"給与台帳の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public WageLedgerDetailResult getWageLedgerDetail(
		String wageMonth, String wagePeriod) {

		return getWageLedgerDetail(
			wageMonth,
			wagePeriod,
			null,
			null,
			null);
	}

	public WageLedgerDetailResult getWageLedgerDetail(
		String wageMonth,
		String wagePeriod,
		String employmentType,
		String departmentId,
		String incomeType) {

		if (wageMonth == null || wagePeriod == null) {
			throw new IllegalArgumentException(
				"帰属年月と給与回次を入力する必要があります。");
		}

		wageMonth = wageMonth.trim();
		wagePeriod = wagePeriod.trim();

		if (wageMonth.isEmpty() || wagePeriod.isEmpty()) {
			throw new IllegalArgumentException(
				"帰属年月と給与回次を入力する必要があります。");
		}

		try {
			YearMonth.parse(wageMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		int period;

		try {
			period = Integer.parseInt(wagePeriod);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"給与回次は数値である必要があります。");
		}

		if (period <= 0) {
			throw new IllegalArgumentException(
				"給与回次は1以上である必要があります。");
		}

		wagePeriod = String.valueOf(period);

		employmentType = normalizeOptionalValue(employmentType);
		departmentId = normalizeOptionalValue(departmentId);
		incomeType = normalizeOptionalValue(incomeType);

		Integer parsedDepartmentId = null;

		if (departmentId != null) {
			try {
				parsedDepartmentId = Integer.valueOf(departmentId);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
					"部署情報が正しくありません。");
			}

			if (parsedDepartmentId <= 0) {
				throw new IllegalArgumentException(
					"部署情報が正しくありません。");
			}
		}

		if (incomeType != null
			&& !"worker".equals(incomeType)
			&& !"business".equals(incomeType)
			&& !"daily".equals(incomeType)) {

			throw new IllegalArgumentException(
				"所得者区分が正しくありません。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			// 選択した給与回次の基本情報
			WageLedgerSummary summary = wageDao.selectWageLedgerSummary(
				conn, wageMonth, wagePeriod);

			if (summary == null) {
				throw new IllegalArgumentException(
					"該当する給与回次が存在しません。");
			}

			List<Department> departments = departmentDao.selectDepartments(conn);

			// usageにかかわらずすべての給与項目を照会
			List<WageTypeOption> wageTypes = wageTypeDao.selectWageTypeOptions(conn);

			List<WageTypeOption> paymentTypes = new ArrayList<>();

			List<WageTypeOption> deductionTypes = new ArrayList<>();

			List<Integer> wageTypeIds = new ArrayList<>();

			Map<Integer, String> itemTypeMap = new LinkedHashMap<>();

			for (WageTypeOption wageType : wageTypes) {

				String itemType = wageType.getItemType();

				if ("P".equals(itemType)) {

					paymentTypes.add(wageType);

				} else if ("D".equals(itemType)) {

					deductionTypes.add(wageType);

				} else {
					continue;
				}

				wageTypeIds.add(wageType.getWageTypeId());

				itemTypeMap.put(
					wageType.getWageTypeId(),
					itemType);
			}

			// 社員 × 給与項目の縦持ち形式の元データを照会
			List<WageLedgerDetailRow> rawRows = wageDao.selectWageLedgerDetailRows(
				conn,
				wageMonth,
				wagePeriod,
				employmentType,
				parsedDepartmentId,
				incomeType);

			Map<Integer, WageLedgerEmployeeRow> employeeMap = new LinkedHashMap<>();

			for (WageLedgerDetailRow row : rawRows) {

				WageLedgerEmployeeRow employeeRow = employeeMap.get(row.getEmployeeId());

				if (employeeRow == null) {

					employeeRow = new WageLedgerEmployeeRow(
						row.getEmployeeId(),
						row.getEmploymentType(),
						row.getKoreanName(),
						row.getHireDate(),
						row.getDepartmentName(),
						row.getPositionName(),
						wageTypeIds);

					employeeMap.put(
						row.getEmployeeId(),
						employeeRow);
				}

				String itemType = itemTypeMap.get(row.getWageTypeId());

				if (itemType == null) {
					throw new IllegalStateException(
						"給与項目マスターに存在しない給与データが含まれています。");
				}

				employeeRow.addWageValue(
					row.getWageTypeId(),
					itemType,
					row.getWageValue());
			}

			List<WageLedgerEmployeeRow> employeeRows = new ArrayList<>(employeeMap.values());

			// 給与項目別の全体合計を計算
			Map<Integer, Long> itemTotals = new LinkedHashMap<>();

			for (Integer wageTypeId : wageTypeIds) {
				itemTotals.put(wageTypeId, 0L);
			}

			for (WageLedgerEmployeeRow employeeRow : employeeRows) {

				for (Integer wageTypeId : wageTypeIds) {

					Long value = employeeRow.getWageValues().get(wageTypeId);

					if (value == null) {
						value = 0L;
					}

					itemTotals.put(
						wageTypeId,
						itemTotals.get(wageTypeId) + value);
				}
			}

			long totalPayment = 0L;
			long totalDeduction = 0L;

			for (WageLedgerEmployeeRow employeeRow : employeeRows) {

				if (employeeRow.getTotalPayment() != null) {
					totalPayment += employeeRow.getTotalPayment();
				}

				if (employeeRow.getTotalDeduction() != null) {
					totalDeduction += employeeRow.getTotalDeduction();
				}
			}

			return new WageLedgerDetailResult(
				summary,
				paymentTypes,
				deductionTypes,
				employeeRows,
				itemTotals,
				totalPayment,
				totalDeduction,
				departments);

		} catch (SQLException e) {
			throw new RuntimeException(
				"給与台帳詳細の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	private String normalizeOptionalValue(String value) {

		if (value == null) {
			return null;
		}

		value = value.trim();

		return value.isEmpty() ? null : value;
	}
}