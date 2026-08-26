package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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

	// 🌟 7단계 적용: 파라미터로 받은 conn을 직접 사용하도록 수정 (Connection 누수 차단)
	public List<Appointment> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Appointment> result = new ArrayList<>();
		try {
			// 🌟 변경 포인트: conn.prepareStatement() 사용
			pstmt = conn
				.prepareStatement("SELECT * FROM appointment WHERE employee_id = ? ORDER BY appointment_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Appointment(rs.getInt("appointment_id"), rs.getInt("employee_id"),
					rs.getString("appointment_type"),
					rs.getDate("appointment_date"), rs.getInt("department_id"), rs.getInt("position_id"),
					rs.getString("position_type"), rs.getString("remarks3")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}