package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeSelectRow;

public class EmployeeDao {

	public List<EmployeeSelectRow> selectEmployeeRows(
		Connection conn,
		String keyword,
		Integer departmentId,
		String status)
		throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT e.employee_id, ");
		sql.append("       e.employment_type, ");
		sql.append("       e.korean_name, ");
		sql.append("       d.department_name, ");
		sql.append("       p.position_name, ");
		sql.append("       e.status ");
		sql.append("FROM employee e ");
		sql.append("LEFT JOIN department d "); // 부서·직위가 없는 사원도 조회에 포함되도록 LEFT JOIN
		sql.append("  ON d.department_id = e.department_id ");
		sql.append("LEFT JOIN position p ");
		sql.append("  ON p.position_id = e.position_id ");
		sql.append("WHERE 1 = 1 ");

		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.trim().isEmpty()) {
			sql.append("AND e.korean_name LIKE ? ");
			params.add("%" + keyword.trim() + "%");
		}

		if (departmentId != null && departmentId > 0) {
			sql.append("AND e.department_id = ? ");
			params.add(departmentId);
		}

		if (status != null && !status.trim().isEmpty()) {
			sql.append("AND e.status = ? ");
			params.add(status.trim());
		}

		sql.append("ORDER BY e.employee_id");

		List<EmployeeSelectRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					EmployeeSelectRow row = new EmployeeSelectRow(
						rs.getInt("employee_id"),
						rs.getString("employment_type"),
						rs.getString("korean_name"),
						rs.getString("department_name"),
						rs.getString("position_name"),
						rs.getString("status"));

					result.add(row);
				}
			}
		}

		return result;
	}

	// 사원의 고용형태 조회
	public String selectEmploymentType(
		Connection conn,
		Integer employeeId)
		throws SQLException {

		String sql = "SELECT employment_type "
			+ "FROM employee "
			+ "WHERE employee_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getString("employment_type");
				}
			}
		}

		return null;
	}
}
