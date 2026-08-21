package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Degree;
import jdbc.JdbcUtil;

public class DegreeDao {

	public void insert(Connection conn, Degree degree) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO degree (degree_id, employee_id, graduate, admission_date, graduation_date, school_name, major, completion) "
					+
					"VALUES (degree_seq.nextval, ?, ?, ?, ?, ?, ?, ?)");

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
}