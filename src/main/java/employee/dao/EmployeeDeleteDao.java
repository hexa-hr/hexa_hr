package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jdbc.JdbcUtil;

public class EmployeeDeleteDao {
	public void delete(Connection conn, int employeeId) throws SQLException {
		// ON DELETE CASCADE 덕분에 사원만 지우면 하위 테이블 데이터도 다 지워짐!
		String sql = "DELETE FROM employee WHERE employee_id = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, employeeId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}