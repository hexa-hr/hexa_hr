package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeListDto;
import jdbc.JdbcUtil;

public class EmployeeListDao {

	// 🌟 파라미터로 limit(가져올 갯수)를 받도록 추가!
	public List<EmployeeListDto> selectList(Connection conn, int limit) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 🌟 ROWNUM을 이용해 원하는 갯수만큼만 가져오도록 SQL 수정
		String sql = "SELECT * FROM ("
			+ "    SELECT e.employee_id, e.korean_name, d.department_name, p.position_name, "
			+ "           e.employment_type, e.status, e.hire_date "
			+ "    FROM employee e "
			+ "    LEFT JOIN department d ON e.department_id = d.department_id "
			+ "    LEFT JOIN position p ON e.position_id = p.position_id "
			+ "    ORDER BY e.employee_id DESC"
			+ ") WHERE ROWNUM <= ?";

		List<EmployeeListDto> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, limit); // 🌟 사용자가 선택한 갯수(10, 30, 50, 100) 세팅
			rs = pstmt.executeQuery();

			while (rs.next()) {
				EmployeeListDto dto = new EmployeeListDto(
					rs.getInt("employee_id"),
					rs.getString("korean_name"),
					rs.getString("department_name"),
					rs.getString("position_name"),
					rs.getString("employment_type"),
					rs.getString("status"),
					rs.getDate("hire_date"));
				result.add(dto);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}