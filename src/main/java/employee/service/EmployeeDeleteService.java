package employee.service;

import java.sql.Connection;
import java.sql.SQLException;

import employee.dao.EmployeeDeleteDao;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeDeleteService {
	private EmployeeDeleteDao deleteDao = new EmployeeDeleteDao();

	public void deleteEmployees(String[] empIds) {
		if (empIds == null || empIds.length == 0)
			return;

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 여러 명 삭제를 위해 트랜잭션 시작

			for (String idStr : empIds) {
				int empId = Integer.parseInt(idStr);
				deleteDao.delete(conn, empId);
			}

			conn.commit(); // 모두 성공 시 커밋
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("사원 삭제 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}