package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import master.model.Department;

public class DepartmentDao {

	public List<Department> selectDepartments(Connection conn)
		throws SQLException {

		String sql = "SELECT department_id, department_name "
			+ "FROM department "
			+ "ORDER BY department_id";

		List<Department> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				Department department = new Department(
					rs.getInt("department_id"),
					rs.getString("department_name"));

				result.add(department);
			}
		}

		return result;
	}
}