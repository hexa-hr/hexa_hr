package wage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import wage.model.WageEmployeeHistoryRow;
import wage.model.WageInsuranceDeductionRow;
import wage.model.WageItemLedgerRow;
import wage.model.WageLedgerDetailRow;
import wage.model.WageLedgerSummary;
import wage.model.WageMonthlyTotalStatisticsRow;

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

}
