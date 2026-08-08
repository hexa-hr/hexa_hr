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

// 급여대장 조회 서비스
public class WageLedgerService {

	private WageDao wageDao = new WageDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();
	private DepartmentDao departmentDao = new DepartmentDao();

	public WageLedgerSummaryResult getWageLedgerSummaries(String year) {

		if (year == null || !year.matches("\\d{4}")) {
			throw new IllegalArgumentException(
				"귀속연도는 YYYY 형식이어야 합니다.");
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
				"급여대장 조회 중 데이터베이스 오류가 발생했습니다.",
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
				"귀속연월과 급여차수를 입력해야 합니다.");
		}

		wageMonth = wageMonth.trim();
		wagePeriod = wagePeriod.trim();

		if (wageMonth.isEmpty() || wagePeriod.isEmpty()) {
			throw new IllegalArgumentException(
				"귀속연월과 급여차수를 입력해야 합니다.");
		}

		try {
			YearMonth.parse(wageMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		int period;

		try {
			period = Integer.parseInt(wagePeriod);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"급여차수는 숫자여야 합니다.");
		}

		if (period <= 0) {
			throw new IllegalArgumentException(
				"급여차수는 1 이상이어야 합니다.");
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
					"부서 정보가 올바르지 않습니다.");
			}

			if (parsedDepartmentId <= 0) {
				throw new IllegalArgumentException(
					"부서 정보가 올바르지 않습니다.");
			}
		}

		if (incomeType != null
			&& !"worker".equals(incomeType)
			&& !"business".equals(incomeType)
			&& !"daily".equals(incomeType)) {

			throw new IllegalArgumentException(
				"소득자 구분이 올바르지 않습니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			// 선택한 급여차수의 기본 정보
			WageLedgerSummary summary = wageDao.selectWageLedgerSummary(
				conn, wageMonth, wagePeriod);

			if (summary == null) {
				throw new IllegalArgumentException(
					"해당 급여차수가 존재하지 않습니다.");
			}

			List<Department> departments = departmentDao.selectDepartments(conn);

			// usage 여부와 관계없이 전체 급여항목 조회
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

			// 사원 × 급여항목 세로형 원본 조회
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
						"급여항목 마스터에 존재하지 않는 급여 데이터가 포함되어 있습니다.");
				}

				employeeRow.addWageValue(
					row.getWageTypeId(),
					itemType,
					row.getWageValue());
			}

			List<WageLedgerEmployeeRow> employeeRows = new ArrayList<>(employeeMap.values());

			// 급여항목별 전체 합계 계산
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
				"급여대장 상세 조회 중 데이터베이스 오류가 발생했습니다.",
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