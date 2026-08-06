package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import attendance.model.AttendanceVO;
import attendance.model.EmployeeVO;

public class AttendanceDao {

	// 1. 전체 사원 목록 조회 (JOIN department, position)
	public List<EmployeeVO> selectAllEmployees(Connection conn) throws SQLException {
		String sql = "SELECT e.employee_id, e.employment_type, e.korean_name, "
				+ "       d.department_name, p.position_name " + "FROM employee e "
				+ "LEFT JOIN department d ON e.department_id = d.department_id "
				+ "LEFT JOIN position p ON e.position_id = p.position_id " + "ORDER BY e.employee_id ASC";

		List<EmployeeVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				list.add(new EmployeeVO(rs.getInt("employee_id"), rs.getString("employment_type"),
						rs.getString("korean_name"), rs.getString("department_name"), rs.getString("position_name")));
			}
		}
		return list;
	}

	// 2. 특정 사원의 근태 기록 조회 (모달용)
	public List<AttendanceVO> selectAttendanceByEmpId(Connection conn, int employeeId) throws SQLException {
		String sql = "SELECT a.attendance_id, a.employee_id, a.input_date, "
				+ "       t.attendance_type_name, a.start_date, a.end_date, "
				+ "       a.attendance_days, a.amount, a.summary " + "FROM attendance a "
				+ "JOIN attendance_type t ON a.attendance_type_id = t.attendance_type_id " + "WHERE a.employee_id = ? "
				+ "ORDER BY a.input_date DESC, a.attendance_id DESC";

		List<AttendanceVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					AttendanceVO vo = new AttendanceVO();
					vo.setAttendanceId(rs.getInt("attendance_id"));
					vo.setEmployeeId(rs.getInt("employee_id"));
					vo.setInputDate(rs.getDate("input_date"));
					vo.setAttendanceTypeName(rs.getString("attendance_type_name"));
					vo.setStartDate(rs.getDate("start_date"));
					vo.setEndDate(rs.getDate("end_date"));
					vo.setAttendanceDays(rs.getDouble("attendance_days"));
					vo.setAmount(rs.getInt("amount"));
					vo.setSummary(rs.getString("summary"));
					list.add(vo);
				}
			}
		}
		return list;
	}

	// 3. 근태 기록 저장 (신규 등록)
	public int insertAttendance(Connection conn, AttendanceVO vo) throws SQLException {
		String sql = "INSERT INTO attendance (attendance_id, employee_id, input_date, attendance_type_id, "
				+ "                        start_date, end_date, attendance_days, amount, summary) "
				+ "VALUES (attendance_seq.NEXTVAL, ?, SYSDATE, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, vo.getEmployeeId());
			pstmt.setInt(2, vo.getAttendanceTypeId());
			pstmt.setDate(3, vo.getStartDate());
			pstmt.setDate(4, vo.getEndDate());
			pstmt.setDouble(5, vo.getAttendanceDays());
			pstmt.setInt(6, vo.getAmount());
			pstmt.setString(7, vo.getSummary());
			return pstmt.executeUpdate();
		}
	}

	// 4. 근태 기록 삭제 (attendance_id 기준)
	public int deleteAttendance(Connection conn, int attendanceId) throws SQLException {
		String sql = "DELETE FROM attendance WHERE attendance_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, attendanceId);
			return pstmt.executeUpdate(); // 삭제 성공 시 1 이상 반환
		}
	}

}