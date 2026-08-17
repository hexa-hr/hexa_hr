package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import employee.dao.EmployeeListDao;
import employee.model.EmployeeListDto;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeListService {

	private EmployeeListDao employeeListDao = new EmployeeListDao();

	// 🌟 파라미터로 limit 추가
	public List<EmployeeListDto> getEmployeeList(int limit) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeListDao.selectList(conn, limit); // 🌟 DAO에 limit 전달
		} catch (SQLException e) {
			throw new RuntimeException("사원 목록 조회 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}