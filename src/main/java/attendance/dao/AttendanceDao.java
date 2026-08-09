package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import master.model.AttendanceType;

public class AttendanceDao {

	// 1. 전체 목록 조회 (master 패키지 DTO 수정 없이 기본 필드만 조회)
	public List<AttendanceType> selectAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(
				"SELECT attendance_type_id, attendance_type_name, unit, attendance_group_id, vacation_type_id, \"USAGE\" "
					+ "FROM attendance_type ORDER BY attendance_type_id ASC");
			rs = pstmt.executeQuery();

			List<AttendanceType> list = new ArrayList<>();
			while (rs.next()) {
				Integer groupId = rs.getObject("attendance_group_id") != null ? rs.getInt("attendance_group_id") : null;
				Integer vacationId = rs.getObject("vacation_type_id") != null ? rs.getInt("vacation_type_id") : null;

				AttendanceType att = new AttendanceType(
					rs.getInt("attendance_type_id"),
					rs.getString("attendance_type_name"),
					rs.getString("unit"),
					groupId,
					vacationId,
					rs.getString("USAGE"));
				list.add(att);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 근태항목 추가 (Insert)
	public void insert(Connection conn, AttendanceType att) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO attendance_type (attendance_type_id, attendance_type_name, unit, attendance_group_id, vacation_type_id, \"USAGE\") "
			+ "VALUES ((SELECT NVL(MAX(attendance_type_id), 0) + 1 FROM attendance_type), ?, ?, ?, ?, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, att.getAttendanceTypeName());
			pstmt.setString(2, att.getUnit());

			if (att.getAttendanceGroupId() != null) {
				pstmt.setInt(3, att.getAttendanceGroupId());
			} else {
				pstmt.setNull(3, java.sql.Types.INTEGER);
			}

			if (att.getVacationTypeId() != null) {
				pstmt.setInt(4, att.getVacationTypeId());
			} else {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			}

			pstmt.setString(5, att.getUsage());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 3. 근태항목 수정 (Update)
	public void update(Connection conn, AttendanceType att) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "UPDATE attendance_type "
			+ "SET attendance_type_name = ?, unit = ?, attendance_group_id = ?, vacation_type_id = ?, \"USAGE\" = ? "
			+ "WHERE attendance_type_id = ?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, att.getAttendanceTypeName());
			pstmt.setString(2, att.getUnit());

			if (att.getAttendanceGroupId() != null) {
				pstmt.setInt(3, att.getAttendanceGroupId());
			} else {
				pstmt.setNull(3, java.sql.Types.INTEGER);
			}

			if (att.getVacationTypeId() != null) {
				pstmt.setInt(4, att.getVacationTypeId());
			} else {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			}

			pstmt.setString(5, att.getUsage());
			pstmt.setInt(6, att.getAttendanceTypeId());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}