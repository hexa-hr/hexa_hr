package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import employee.model.Employee;
import jdbc.JdbcUtil;
import master.model.AttendanceType;

public class AttendanceDao {

	// Oracle NUMBER 타입을 안전하게 Integer로 변환하는 헬퍼 메서드
	private Integer getInteger(ResultSet rs, String columnName) throws SQLException {
		Object obj = rs.getObject(columnName);
		if (obj == null)
			return null;
		if (obj instanceof Number) {
			return ((Number)obj).intValue();
		}
		return Integer.parseInt(obj.toString());
	}

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

	// 4. 근태항목 삭제 (Delete)
	public void delete(Connection conn, int attendanceTypeId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM attendance_type WHERE attendance_type_id = ?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, attendanceTypeId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 5. 사원별 휴가일수 목록 조회 (LEFT JOIN)
	public List<Map<String, Object>> selectEmployeeVacationList(Connection conn, int attendanceTypeId)
		throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT e.*, d.department_name, p.position_name, a.attendance_days "
			+ "FROM employee e "
			+ "LEFT JOIN department d ON e.department_id = d.department_id "
			+ "LEFT JOIN position p ON e.position_id = p.position_id "
			+ "LEFT JOIN attendance a ON e.employee_id = a.employee_id AND a.attendance_type_id = ? "
			+ "ORDER BY e.employee_id ASC";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, attendanceTypeId);
			rs = pstmt.executeQuery();

			List<Map<String, Object>> list = new ArrayList<>();
			while (rs.next()) {
				Employee emp = new Employee(
					rs.getInt("employee_id"),
					getInteger(rs, "account_id"),
					getInteger(rs, "company_id"),
					getInteger(rs, "person_id"),
					rs.getString("employment_type"),
					rs.getString("korean_name"),
					rs.getString("english_name"),
					rs.getDate("hire_date"),
					rs.getDate("resignation_date"),
					getInteger(rs, "department_id"),
					getInteger(rs, "position_id"),
					rs.getString("foreign_or_domestic"),
					rs.getString("resident_number1"),
					rs.getString("resident_number2"),
					rs.getString("address"),
					rs.getString("tel_phone"),
					rs.getString("mobile"),
					rs.getString("email"),
					rs.getString("sns"),
					rs.getString("other_details"),
					rs.getString("status"));

				Integer days = getInteger(rs, "attendance_days");
				int attendanceDays = (days != null) ? days : 0;

				Map<String, Object> map = new HashMap<>();
				map.put("emp", emp);
				map.put("departmentName", rs.getString("department_name"));
				map.put("positionName", rs.getString("position_name"));
				map.put("attendanceDays", attendanceDays);
				list.add(map);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 6. 사원별 휴가일수 저장 (MERGE 구문 사용)
	public void saveEmployeeVacationDays(Connection conn, int attendanceTypeId, int employeeId, int days)
		throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "MERGE INTO attendance a "
			+ "USING DUAL ON (a.employee_id = ? AND a.attendance_type_id = ?) "
			+ "WHEN MATCHED THEN "
			+ "  UPDATE SET a.attendance_days = ? "
			+ "WHEN NOT MATCHED THEN "
			+ "  INSERT (attendance_id, employee_id, attendance_type_id, attendance_days) "
			+ "  VALUES ((SELECT NVL(MAX(attendance_id), 0) + 1 FROM attendance), ?, ?, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, attendanceTypeId);
			pstmt.setInt(3, days);
			pstmt.setInt(4, employeeId);
			pstmt.setInt(5, attendanceTypeId);
			pstmt.setInt(6, days);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}