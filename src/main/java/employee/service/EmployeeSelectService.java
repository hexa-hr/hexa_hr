package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import employee.dao.EmployeeDao;
import employee.model.EmployeeSelectRow;
import jdbc.connection.ConnectionProvider;

// 사원 선택 목록 조회 서비스
public class EmployeeSelectService {

	private EmployeeDao employeeDao = new EmployeeDao();

	public List<EmployeeSelectRow> getEmployeeRows(
		String keyword,
		Integer departmentId,
		String status) {

		String normalizedKeyword = keyword == null ? null : keyword.trim();

		String normalizedStatus = status == null ? null : status.trim();

		Integer normalizedDepartmentId = departmentId != null && departmentId > 0
			? departmentId
			: null;

		try (Connection conn = ConnectionProvider.getConnection()) {

			return employeeDao.selectEmployeeRows(
				conn,
				normalizedKeyword,
				normalizedDepartmentId,
				normalizedStatus);

		} catch (SQLException e) {
			throw new RuntimeException(
				"사원 목록 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}
}