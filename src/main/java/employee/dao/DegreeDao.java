package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.Degree;
import jdbc.JdbcUtil;

public class DegreeDao {

	public void insert(Connection conn, Degree degree) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO degree (degree_id, employee_id, graduate, admission_date, graduation_date, school_name, major, completion) VALUES (degree_seq.nextval, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, degree.getEmployeeId());
			pstmt.setString(2, degree.getGraduate());
			if (degree.getAdmissionDate() != null)
				pstmt.setDate(3, new java.sql.Date(degree.getAdmissionDate().getTime()));
			else
				pstmt.setNull(3, java.sql.Types.DATE);
			if (degree.getGraduationDate() != null)
				pstmt.setDate(4, new java.sql.Date(degree.getGraduationDate().getTime()));
			else
				pstmt.setNull(4, java.sql.Types.DATE);
			pstmt.setString(5, degree.getSchoolName());
			pstmt.setString(6, degree.getMajor());
			pstmt.setString(7, degree.getCompletion());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 새로 추가: 학력 리스트 조회
	public List<Degree> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Degree> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM degree WHERE employee_id = ? ORDER BY degree_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Degree(rs.getInt("degree_id"), rs.getInt("employee_id"), rs.getString("graduate"),
					rs.getDate("admission_date"), rs.getDate("graduation_date"), rs.getString("school_name"),
					rs.getString("major"), rs.getString("completion")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}