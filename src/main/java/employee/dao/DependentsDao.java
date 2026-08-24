package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.Dependents;
import jdbc.JdbcUtil;

public class DependentsDao {

	// 🌟 기존에 있던 INSERT 메서드
	public void insert(Connection conn, Dependents dependent) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO dependents " +
					"(dependent_id, employee_id, relationship, parents_name, foreign_or_domestic1, parents_number1, parents_number2) "
					+ "VALUES (dependents_seq.nextval, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, dependent.getEmployeeId());
			pstmt.setString(2, dependent.getRelationship());
			pstmt.setString(3, dependent.getParentsName());
			pstmt.setString(4, dependent.getForeignOrDomestic1());
			pstmt.setString(5, dependent.getParentsNumber1());
			pstmt.setString(6, dependent.getParentsNumber2());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 새로 추가된 SELECT 메서드 (사원번호로 부양가족 목록 불러오기)
	public List<Dependents> selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Dependents> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM dependents WHERE employee_id = ? ORDER BY dependent_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Dependents dep = new Dependents(
					rs.getInt("dependent_id"),
					rs.getInt("employee_id"),
					rs.getString("relationship"),
					rs.getString("parents_name"),
					rs.getString("foreign_or_domestic1"),
					rs.getString("parents_number1"),
					rs.getString("parents_number2"));
				result.add(dep);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}