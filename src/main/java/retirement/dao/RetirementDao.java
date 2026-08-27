package retirement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import retirement.model.RetirementVO;

public class RetirementDao {

	// 1. 退職状態の社員リストを照会 (ステータスが '退職' の社員)
	public List<RetirementVO> selectRetiredEmployees(Connection conn) throws SQLException {
		String sql = "SELECT e.employee_id, e.korean_name, d.department_name, p.position_name, "
				+ "       e.hire_date, e.resignation_date, a.bank_name, a.account_number " + "FROM employee e "
				+ "LEFT JOIN department d ON e.department_id = d.department_id "
				+ "LEFT JOIN position p ON e.position_id = p.position_id "
				+ "LEFT JOIN employee_salary_account a ON e.account_id = a.account_id " + "WHERE e.status = '퇴직' "
				+ "ORDER BY e.resignation_date DESC";

		List<RetirementVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				RetirementVO vo = new RetirementVO();
				vo.setEmployeeId(rs.getInt("employee_id"));
				vo.setEmpName(rs.getString("korean_name"));
				vo.setDeptName(rs.getString("department_name"));
				vo.setPositionName(rs.getString("position_name"));
				vo.setHireDate(rs.getDate("hire_date"));
				vo.setResignationDate(rs.getDate("resignation_date"));
				vo.setBankName(rs.getString("bank_name"));
				vo.setAccountNumber(rs.getString("account_number"));
				list.add(vo);
			}
		}
		return list;
	}

	// 2. 特定の月(YYYY-MM)の給与合計を照会するメソッド (追加)
	public long getWageByMonth(Connection conn, int employeeId, String wageMonth) throws SQLException {
		String sql = "SELECT SUM(wage_value) as val FROM wage WHERE employee_id = ? AND wage_month = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			pstmt.setString(2, wageMonth);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getLong("val");
				}
			}
		}
		return 0;
	}
}