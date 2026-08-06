package wage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import wage.model.WageItemLedgerRow;

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

}
