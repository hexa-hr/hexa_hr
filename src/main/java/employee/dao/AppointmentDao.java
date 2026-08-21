package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import employee.model.Appointment;
import jdbc.JdbcUtil;

public class AppointmentDao {
	public void insert(Connection conn, Appointment a) throws SQLException {
		String sql = "INSERT INTO appointment (appointment_id, employee_id, appointment_type, appointment_date, department_id, position_id, position_type, remarks3) VALUES (appointment_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, a.getEmployeeId());
			pstmt.setString(2, a.getAppointmentType());
			pstmt.setDate(3,
				a.getAppointmentDate() != null ? new java.sql.Date(a.getAppointmentDate().getTime()) : null);
			if (a.getDepartmentId() != null)
				pstmt.setInt(4, a.getDepartmentId());
			else
				pstmt.setNull(4, Types.NUMERIC);
			if (a.getPositionId() != null)
				pstmt.setInt(5, a.getPositionId());
			else
				pstmt.setNull(5, Types.NUMERIC);
			pstmt.setString(6, a.getPositionType());
			pstmt.setString(7, a.getRemarks3());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}