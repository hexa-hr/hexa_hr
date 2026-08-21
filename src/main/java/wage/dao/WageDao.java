package wage.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import wage.model.WageEmployeeHistoryRow;
import wage.model.WageInsuranceDeductionRow;
import wage.model.WageItemCompositionStatisticsRow;
import wage.model.WageItemLedgerRow;
import wage.model.WageLedgerDetailRow;
import wage.model.WageLedgerSummary;
import wage.model.WageMonthlyPersonalStatisticsRow;
import wage.model.WageMonthlyTotalStatisticsRow;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentPreviousSourceOption;

public class WageDao {

	public List<WageItemLedgerRow> selectItemLedger(Connection conn,
		Integer wageTypeId, String startMonth, String endMonth)
		throws SQLException {

		String sql = "SELECT e.employee_id, "
			+ "       e.employment_type, "
			+ "       e.korean_name, "
			+ "       d.department_name, "
			+ "       p.position_name, "
			+ "       w.wage_month, "
			+ "       SUM(w.wage_value) AS wage_value " // 한 귀속연월에 여러 급여차수가 있을 수 있으므로 합산
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "LEFT JOIN department d " // 부서·직위가 없는 사원도 조회에 포함되도록 LEFT JOIN
			+ "  ON d.department_id = e.department_id "
			+ "LEFT JOIN position p "
			+ "  ON p.position_id = e.position_id "
			+ "WHERE w.wage_type_id = ? "
			+ "  AND w.wage_month BETWEEN ? AND ? "
			+ "GROUP BY e.employee_id, "
			+ "         e.employment_type, "
			+ "         e.korean_name, "
			+ "         d.department_name, "
			+ "         p.position_name, "
			+ "         w.wage_month "
			+ "ORDER BY e.employee_id, w.wage_month";

		List<WageItemLedgerRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, wageTypeId);
			pstmt.setString(2, startMonth);
			pstmt.setString(3, endMonth);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					WageItemLedgerRow row = new WageItemLedgerRow(
						rs.getInt("employee_id"),
						rs.getString("employment_type"),
						rs.getString("korean_name"),
						rs.getString("department_name"),
						rs.getString("position_name"),
						rs.getString("wage_month"),
						rs.getLong("wage_value"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageLedgerSummary> selectWageLedgerSummaries(
		Connection conn, String year) throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       w.wage_period, "
			+ "       MIN(w.settlement_period_start_date) AS settlement_start, "
			+ "       MIN(w.settlement_period_end_date) AS settlement_end, "
			+ "       MIN(w.wage_payment_date) AS payment_date, "
			+ "       COUNT(DISTINCT w.employee_id) AS employee_count, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE SUBSTR(w.wage_month, 1, 4) = ? "
			+ "GROUP BY w.wage_month, w.wage_period "
			+ "ORDER BY w.wage_month, TO_NUMBER(w.wage_period)";

		List<WageLedgerSummary> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, year);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					result.add(mapWageLedgerSummary(rs));
				}
			}
		}

		return result;
	}

	public List<WageLedgerDetailRow> selectWageLedgerDetailRows(
		Connection conn, String wageMonth, String wagePeriod)
		throws SQLException {

		return selectWageLedgerDetailRows(
			conn,
			wageMonth,
			wagePeriod,
			null,
			null,
			null);
	}

	public List<WageLedgerDetailRow> selectWageLedgerDetailRows(
		Connection conn,
		String wageMonth,
		String wagePeriod,
		String employmentType,
		Integer departmentId,
		String incomeType)
		throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT e.employee_id, ");
		sql.append("       e.employment_type, ");
		sql.append("       e.korean_name, ");
		sql.append("       e.hire_date, ");
		sql.append("       d.department_name, ");
		sql.append("       p.position_name, ");
		sql.append("       w.wage_type_id, ");
		sql.append("       SUM(NVL(w.wage_value, 0)) AS wage_value ");
		sql.append("FROM wage w ");
		sql.append("JOIN employee e ");
		sql.append("  ON e.employee_id = w.employee_id ");
		sql.append("LEFT JOIN department d ");
		sql.append("  ON d.department_id = e.department_id ");
		sql.append("LEFT JOIN position p ");
		sql.append("  ON p.position_id = e.position_id ");
		sql.append("WHERE w.wage_month = ? ");
		sql.append("  AND w.wage_period = ? ");

		if (employmentType != null) {
			sql.append("  AND e.employment_type = ? ");
		}

		if (departmentId != null) {
			sql.append("  AND e.department_id = ? ");
		}

		if ("worker".equals(incomeType)) {

			sql.append(
				"  AND e.employment_type IN "
					+ "('정규직', '계약직', '파견직', '위촉직') ");

		} else if ("business".equals(incomeType)) {

			sql.append(
				"  AND e.employment_type = '임시직' ");

		} else if ("daily".equals(incomeType)) {

			sql.append(
				"  AND e.employment_type = '일용직' ");
		}

		sql.append("GROUP BY e.employee_id, ");
		sql.append("         e.employment_type, ");
		sql.append("         e.korean_name, ");
		sql.append("         e.hire_date, ");
		sql.append("         d.department_name, ");
		sql.append("         p.position_name, ");
		sql.append("         w.wage_type_id ");
		sql.append("ORDER BY e.employee_id, w.wage_type_id");

		List<WageLedgerDetailRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

			int parameterIndex = 1;

			pstmt.setString(parameterIndex++, wageMonth);
			pstmt.setString(parameterIndex++, wagePeriod);

			if (employmentType != null) {
				pstmt.setString(parameterIndex++, employmentType);
			}

			if (departmentId != null) {
				pstmt.setInt(parameterIndex++, departmentId);
			}

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageLedgerDetailRow row = new WageLedgerDetailRow(
						rs.getInt("employee_id"),
						rs.getString("employment_type"),
						rs.getString("korean_name"),
						rs.getDate("hire_date"),
						rs.getString("department_name"),
						rs.getString("position_name"),
						rs.getInt("wage_type_id"),
						rs.getLong("wage_value"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public WageLedgerSummary selectWageLedgerSummary(
		Connection conn, String wageMonth, String wagePeriod)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       w.wage_period, "
			+ "       MIN(w.settlement_period_start_date) AS settlement_start, "
			+ "       MIN(w.settlement_period_end_date) AS settlement_end, "
			+ "       MIN(w.wage_payment_date) AS payment_date, "
			+ "       COUNT(DISTINCT w.employee_id) AS employee_count, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "GROUP BY w.wage_month, w.wage_period";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, wageMonth);
			pstmt.setString(2, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return mapWageLedgerSummary(rs);
				}
			}
		}

		return null;
	}

	private WageLedgerSummary mapWageLedgerSummary(ResultSet rs)
		throws SQLException {

		return new WageLedgerSummary(
			rs.getString("wage_month"),
			rs.getString("wage_period"),
			rs.getDate("settlement_start"),
			rs.getDate("settlement_end"),
			rs.getDate("payment_date"),
			rs.getInt("employee_count"),
			rs.getLong("total_payment"),
			rs.getLong("total_deduction"));
	}

	public List<WageEmployeeHistoryRow> selectWageEmployeeHistoryRows(
		Connection conn,
		Integer employeeId,
		String startMonth,
		String endMonth)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       w.wage_period, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) "
			+ "       - SUM(CASE WHEN wt.item_type = 'P' "
			+ "                       AND wt.taxable_yn = 'N' "
			+ "                  THEN LEAST(NVL(w.wage_value, 0), "
			+ "                             NVL(wt.tax_free_limit, 0)) "
			+ "                  ELSE 0 END) AS monthly_remuneration, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS total_deduction, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '국민연금' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS national_pension, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '건강보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS health_insurance, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '장기요양보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS long_term_care_insurance, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '고용보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS employment_insurance, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '소득세' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS income_tax, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '지방소득세' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS local_income_tax "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.employee_id = ? "
			+ "  AND w.wage_month BETWEEN ? AND ? "
			+ "GROUP BY w.wage_month, w.wage_period "
			+ "ORDER BY w.wage_month, TO_NUMBER(w.wage_period)";

		List<WageEmployeeHistoryRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, startMonth);
			pstmt.setString(3, endMonth);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageEmployeeHistoryRow row = new WageEmployeeHistoryRow(
						rs.getString("wage_month"),
						rs.getString("wage_period"),
						rs.getLong("monthly_remuneration"),
						rs.getLong("total_payment"),
						rs.getLong("total_deduction"),
						rs.getLong("national_pension"),
						rs.getLong("health_insurance"),
						rs.getLong("long_term_care_insurance"),
						rs.getLong("employment_insurance"),
						rs.getLong("income_tax"),
						rs.getLong("local_income_tax"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageInsuranceDeductionRow> selectWageInsuranceDeductionRows(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "SELECT e.employee_id, "
			+ "       e.employment_type, "
			+ "       e.korean_name, "
			+ "       e.hire_date, "
			+ "       d.department_name, "
			+ "       p.position_name, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '국민연금' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS national_pension, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '건강보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS health_insurance, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '장기요양보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS long_term_care_insurance, "
			+ "       SUM(CASE WHEN wt.wage_type_name = '고용보험' "
			+ "                THEN NVL(w.wage_value, 0) ELSE 0 END) AS employment_insurance "
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "LEFT JOIN department d "
			+ "  ON d.department_id = e.department_id "
			+ "LEFT JOIN position p "
			+ "  ON p.position_id = e.position_id "
			+ "WHERE w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "GROUP BY e.employee_id, "
			+ "         e.employment_type, "
			+ "         e.korean_name, "
			+ "         e.hire_date, "
			+ "         d.department_name, "
			+ "         p.position_name "
			+ "ORDER BY e.employee_id";

		List<WageInsuranceDeductionRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, wageMonth);
			pstmt.setString(2, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageInsuranceDeductionRow row = new WageInsuranceDeductionRow(
						rs.getInt("employee_id"),
						rs.getString("employment_type"),
						rs.getString("korean_name"),
						rs.getDate("hire_date"),
						rs.getString("department_name"),
						rs.getString("position_name"),
						rs.getLong("national_pension"),
						rs.getLong("health_insurance"),
						rs.getLong("long_term_care_insurance"),
						rs.getLong("employment_insurance"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageMonthlyTotalStatisticsRow> selectMonthlyTotalStatisticsRows(
		Connection conn,
		String year)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       COUNT(DISTINCT w.employee_id) AS employee_count "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE SUBSTR(w.wage_month, 1, 4) = ? "
			+ "GROUP BY w.wage_month "
			+ "ORDER BY w.wage_month";

		List<WageMonthlyTotalStatisticsRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, year);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageMonthlyTotalStatisticsRow row = new WageMonthlyTotalStatisticsRow(
						rs.getString("wage_month"),
						rs.getLong("total_payment"),
						rs.getInt("employee_count"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageMonthlyTotalStatisticsRow> selectMonthlyTotalStatisticsRows(
		Connection conn,
		String startYear,
		String endYear)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       COUNT(DISTINCT w.employee_id) AS employee_count "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE SUBSTR(w.wage_month, 1, 4) BETWEEN ? AND ? "
			+ "GROUP BY w.wage_month "
			+ "ORDER BY w.wage_month";

		List<WageMonthlyTotalStatisticsRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, startYear);
			pstmt.setString(2, endYear);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageMonthlyTotalStatisticsRow row = new WageMonthlyTotalStatisticsRow(
						rs.getString("wage_month"),
						rs.getLong("total_payment"),
						rs.getInt("employee_count"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageMonthlyPersonalStatisticsRow> selectMonthlyPersonalStatisticsRows(
		Connection conn,
		Integer employeeId,
		String year)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.employee_id = ? "
			+ "  AND SUBSTR(w.wage_month, 1, 4) = ? "
			+ "GROUP BY w.wage_month "
			+ "ORDER BY w.wage_month";

		List<WageMonthlyPersonalStatisticsRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, year);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageMonthlyPersonalStatisticsRow row = new WageMonthlyPersonalStatisticsRow(
						rs.getString("wage_month"),
						rs.getLong("total_payment"),
						rs.getLong("total_deduction"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageMonthlyPersonalStatisticsRow> selectMonthlyPersonalStatisticsRows(
		Connection conn,
		Integer employeeId,
		String startYear,
		String endYear)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.employee_id = ? "
			+ "  AND SUBSTR(w.wage_month, 1, 4) BETWEEN ? AND ? "
			+ "GROUP BY w.wage_month "
			+ "ORDER BY w.wage_month";

		List<WageMonthlyPersonalStatisticsRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, startYear);
			pstmt.setString(3, endYear);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageMonthlyPersonalStatisticsRow row = new WageMonthlyPersonalStatisticsRow(
						rs.getString("wage_month"),
						rs.getLong("total_payment"),
						rs.getLong("total_deduction"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public List<WageItemCompositionStatisticsRow> selectItemCompositionStatisticsRows(
		Connection conn,
		Integer employeeId,
		String wageMonth)
		throws SQLException {

		String sql = "SELECT wt.wage_type_name, "
			+ "       wt.item_type, "
			+ "       SUM(NVL(w.wage_value, 0)) AS amount "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.employee_id = ? "
			+ "  AND w.wage_month = ? "
			+ "GROUP BY wt.wage_type_name, wt.item_type "
			+ "ORDER BY wt.item_type DESC, wt.wage_type_name";

		List<WageItemCompositionStatisticsRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, wageMonth);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					WageItemCompositionStatisticsRow row = new WageItemCompositionStatisticsRow(
						rs.getString("wage_type_name"),
						rs.getString("item_type"),
						rs.getLong("amount"));

					result.add(row);
				}
			}
		}

		return result;
	}

	// 지난급여 불러오기 - 원본 귀속연월/급여차수 목록 조회
	public List<WagePaymentPreviousSourceOption> selectWagePaymentPreviousSourceOptions(
		Connection conn,
		String targetWageMonth,
		String targetWagePeriod)
		throws SQLException {

		String sql = "SELECT w.wage_month, "
			+ "       w.wage_period, "
			+ "       COUNT(DISTINCT CASE "
			+ "           WHEN e.employment_type IN "
			+ "               ('정규직', '계약직', '파견직', '위촉직') "
			+ "           THEN w.employee_id "
			+ "       END) AS worker_employee_count, "
			+ "       COUNT(DISTINCT CASE "
			+ "           WHEN e.employment_type = '임시직' "
			+ "           THEN w.employee_id "
			+ "       END) AS business_employee_count "
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "WHERE e.employment_type IN "
			+ "      ('정규직', '계약직', '파견직', '위촉직', '임시직') "
			+ "  AND NOT ( "
			+ "      w.wage_month = ? "
			+ "      AND w.wage_period = ? "
			+ "  ) "
			+ "GROUP BY w.wage_month, "
			+ "         w.wage_period "
			+ "ORDER BY w.wage_month DESC, "
			+ "         TO_NUMBER(w.wage_period) DESC";

		List<WagePaymentPreviousSourceOption> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(
				1,
				targetWageMonth);

			pstmt.setString(
				2,
				targetWagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					result.add(
						new WagePaymentPreviousSourceOption(
							rs.getString("wage_month"),
							rs.getString("wage_period"),
							rs.getInt("worker_employee_count"),
							rs.getInt("business_employee_count")));
				}
			}
		}

		return result;
	}

	// 급여입력용 - 사원별 저장된 급여항목 조회
	public List<WagePaymentCalculationItem> selectEmployeeWageItems(
		Connection conn,
		Integer employeeId,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "SELECT wt.wage_type_id, "
			+ "       wt.wage_type_name, "
			+ "       wt.item_type, "
			+ "       wt.taxable_yn, "
			+ "       SUM(NVL(w.wage_value, 0)) AS wage_value "
			+ "FROM wage w "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "WHERE w.employee_id = ? "
			+ "  AND w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "GROUP BY wt.wage_type_id, "
			+ "         wt.wage_type_name, "
			+ "         wt.item_type, "
			+ "         wt.taxable_yn "
			+ "ORDER BY CASE wt.item_type "
			+ "           WHEN 'P' THEN 1 "
			+ "           WHEN 'D' THEN 2 "
			+ "           ELSE 3 "
			+ "         END, "
			+ "         wt.wage_type_id";

		List<WagePaymentCalculationItem> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, wageMonth);
			pstmt.setString(3, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					result.add(
						new WagePaymentCalculationItem(
							rs.getInt("wage_type_id"),
							rs.getString("wage_type_name"),
							rs.getString("item_type"),
							rs.getString("taxable_yn"),
							rs.getLong("wage_value")));
				}
			}
		}

		return result;
	}

	// 급여입력용 - 귀속연월/급여차수별 저장 사원 목록 조회
	public List<WagePaymentEmployeeRow> selectWagePaymentEmployeeRows(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "SELECT e.employee_id, "
			+ "       e.employment_type, "
			+ "       e.korean_name, "
			+ "       d.department_name, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "LEFT JOIN department d "
			+ "  ON d.department_id = e.department_id "
			+ "WHERE w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "GROUP BY e.employee_id, "
			+ "         e.employment_type, "
			+ "         e.korean_name, "
			+ "         d.department_name "
			+ "ORDER BY e.employee_id";

		List<WagePaymentEmployeeRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, wageMonth);
			pstmt.setString(2, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					long totalPayment = rs.getLong("total_payment");

					long totalDeduction = rs.getLong("total_deduction");

					result.add(
						new WagePaymentEmployeeRow(
							rs.getInt("employee_id"),
							rs.getString("employment_type"),
							rs.getString("korean_name"),
							rs.getString("department_name"),
							totalPayment,
							totalDeduction,
							totalPayment - totalDeduction));
				}
			}
		}

		return result;
	}

	// 일용직 급여입력용 - 귀속연월/급여차수별 저장 사원 목록 조회
	public List<WagePaymentEmployeeRow> selectDailyWagePaymentEmployeeRows(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "SELECT e.employee_id, "
			+ "       e.employment_type, "
			+ "       e.korean_name, "
			+ "       d.department_name, "
			+ "       SUM(CASE WHEN wt.item_type = 'P' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_payment, "
			+ "       SUM(CASE WHEN wt.item_type = 'D' "
			+ "                THEN NVL(w.wage_value, 0) "
			+ "                ELSE 0 END) AS total_deduction "
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "JOIN wage_type wt "
			+ "  ON wt.wage_type_id = w.wage_type_id "
			+ "LEFT JOIN department d "
			+ "  ON d.department_id = e.department_id "
			+ "WHERE w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "  AND e.employment_type = '일용직' "
			+ "GROUP BY e.employee_id, "
			+ "         e.employment_type, "
			+ "         e.korean_name, "
			+ "         d.department_name "
			+ "ORDER BY e.employee_id";

		List<WagePaymentEmployeeRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, wageMonth);
			pstmt.setString(2, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					long totalPayment = rs.getLong("total_payment");

					long totalDeduction = rs.getLong("total_deduction");

					result.add(
						new WagePaymentEmployeeRow(
							rs.getInt("employee_id"),
							rs.getString("employment_type"),
							rs.getString("korean_name"),
							rs.getString("department_name"),
							totalPayment,
							totalDeduction,
							totalPayment - totalDeduction));
				}
			}
		}

		return result;
	}

	// 지난급여 불러오기 - 대상 월/차수의 일반·사업 급여 전체 삭제
	public int deleteWagePaymentWorkspaceRows(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "DELETE FROM wage "
			+ "WHERE wage_month = ? "
			+ "  AND wage_period = ? "
			+ "  AND employee_id IN ( "
			+ "      SELECT employee_id "
			+ "      FROM employee "
			+ "      WHERE employment_type IN "
			+ "            ('정규직', '계약직', '파견직', '위촉직', '임시직') "
			+ "  )";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(
				1,
				wageMonth);

			pstmt.setString(
				2,
				wagePeriod);

			return pstmt.executeUpdate();
		}
	}

	// 지난급여 불러오기 - 원본 월/차수 급여를 대상 작업공간으로 복사
	public int insertWagePaymentWorkspaceFromSource(
		Connection conn,
		String sourceWageMonth,
		String sourceWagePeriod,
		String targetWageMonth,
		String targetWagePeriod,
		Date settlementStartDate,
		Date settlementEndDate,
		Date wagePaymentDate)
		throws SQLException {

		String sql = "INSERT INTO wage ( "
			+ "    wage_id, "
			+ "    employee_id, "
			+ "    wage_period, "
			+ "    wage_month, "
			+ "    wage_type_id, "
			+ "    wage_value, "
			+ "    settlement_period_start_date, "
			+ "    settlement_period_end_date, "
			+ "    wage_payment_date "
			+ ") "
			+ "SELECT wage_seq.nextval, "
			+ "       w.employee_id, "
			+ "       ?, "
			+ "       ?, "
			+ "       w.wage_type_id, "
			+ "       NVL(w.wage_value, 0), "
			+ "       ?, "
			+ "       ?, "
			+ "       ? "
			+ "FROM wage w "
			+ "JOIN employee e "
			+ "  ON e.employee_id = w.employee_id "
			+ "WHERE w.wage_month = ? "
			+ "  AND w.wage_period = ? "
			+ "  AND e.employment_type IN "
			+ "      ('정규직', '계약직', '파견직', '위촉직', '임시직')";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(
				1,
				targetWagePeriod);

			pstmt.setString(
				2,
				targetWageMonth);

			pstmt.setDate(
				3,
				settlementStartDate);

			pstmt.setDate(
				4,
				settlementEndDate);

			pstmt.setDate(
				5,
				wagePaymentDate);

			pstmt.setString(
				6,
				sourceWageMonth);

			pstmt.setString(
				7,
				sourceWagePeriod);

			return pstmt.executeUpdate();
		}
	}

	// 급여입력용 - 사원의 해당 귀속연월/급여차수 급여 삭제
	public int deleteEmployeeWages(
		Connection conn,
		Integer employeeId,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "DELETE FROM wage "
			+ "WHERE employee_id = ? "
			+ "  AND wage_month = ? "
			+ "  AND wage_period = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(
				1,
				employeeId);

			pstmt.setString(
				2,
				wageMonth);

			pstmt.setString(
				3,
				wagePeriod);

			return pstmt.executeUpdate();
		}
	}

	// 급여입력용 - 사원의 급여항목 한 건 저장
	public void insertEmployeeWage(
		Connection conn,
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Integer wageTypeId,
		Long wageValue,
		Date settlementStartDate,
		Date settlementEndDate,
		Date wagePaymentDate)
		throws SQLException {

		String sql = "INSERT INTO wage ( "
			+ "    wage_id, "
			+ "    employee_id, "
			+ "    wage_period, "
			+ "    wage_month, "
			+ "    wage_type_id, "
			+ "    wage_value, "
			+ "    settlement_period_start_date, "
			+ "    settlement_period_end_date, "
			+ "    wage_payment_date "
			+ ") VALUES ( "
			+ "    wage_seq.nextval, "
			+ "    ?, ?, ?, ?, ?, ?, ?, ? "
			+ ")";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(
				1,
				employeeId);

			pstmt.setString(
				2,
				wagePeriod);

			pstmt.setString(
				3,
				wageMonth);

			pstmt.setInt(
				4,
				wageTypeId);

			pstmt.setLong(
				5,
				wageValue);

			pstmt.setDate(
				6,
				settlementStartDate);

			pstmt.setDate(
				7,
				settlementEndDate);

			pstmt.setDate(
				8,
				wagePaymentDate);

			pstmt.executeUpdate();
		}
	}

}
