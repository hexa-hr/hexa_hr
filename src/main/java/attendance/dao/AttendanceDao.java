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
		String sql = "SELECT e.employee_id, e.employment_type, e.korean_name, " + "d.department_name, p.position_name "
				+ "FROM employee e " + "LEFT JOIN department d ON e.department_id = d.department_id "
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
				+ "t.attendance_type_name, a.start_date, a.end_date, " + "a.attendance_days, a.amount, a.summary "
				+ "FROM attendance a " + "JOIN attendance_type t ON a.attendance_type_id = t.attendance_type_id "
				+ "WHERE a.employee_id = ? " + "ORDER BY a.input_date DESC, a.attendance_id DESC";

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
				+ "start_date, end_date, attendance_days, amount, summary) "
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
			return pstmt.executeUpdate();
		}
	}

	// 5. 월별 전체 사원 근태 기록 조회 (YYYY-MM 기준)
	public List<AttendanceVO> selectMonthlyAttendance(Connection conn, String yearMonth) throws SQLException {
		String sql = "SELECT a.attendance_id, a.employee_id, a.input_date, "
				+ "t.attendance_type_name, a.start_date, a.end_date, " + "a.attendance_days, a.amount, a.summary "
				+ "FROM attendance a " + "JOIN attendance_type t ON a.attendance_type_id = t.attendance_type_id "
				+ "WHERE TO_CHAR(a.start_date, 'YYYY-MM') <= ? " + "AND TO_CHAR(a.end_date, 'YYYY-MM') >= ? "
				+ "ORDER BY a.employee_id ASC, a.start_date ASC";

		List<AttendanceVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, yearMonth);
			pstmt.setString(2, yearMonth);
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

	// ==========================================================
	// [상세 조회용] 드롭다운 데이터 불러오기 및 동적 검색 메서드 추가
	// ==========================================================

	// 1. 부서 목록 조회
	public List<java.util.Map<String, String>> getDepartments(Connection conn) throws SQLException {
		List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
		String sql = "SELECT department_id, department_name FROM department ORDER BY department_id";
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("id", rs.getString("department_id"));
				map.put("name", rs.getString("department_name"));
				list.add(map);
			}
		}
		return list;
	}

	// 2. 근태그룹 목록 조회
	public List<java.util.Map<String, String>> getAttendanceGroups(Connection conn) throws SQLException {
		List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
		// 실제 DB 컬럼명으로 수정 완료
		String sql = "SELECT attendance_group_id, attendance_group_name FROM attendance_group ORDER BY attendance_group_id";
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("id", rs.getString("attendance_group_id"));
				map.put("name", rs.getString("attendance_group_name"));
				list.add(map);
			}
		}
		return list;
	}

	// 3. 근태항목 목록 조회
	public List<java.util.Map<String, String>> getAttendanceTypes(Connection conn) throws SQLException {
		List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
		String sql = "SELECT attendance_type_id, attendance_type_name FROM attendance_type ORDER BY attendance_type_id";
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("id", rs.getString("attendance_type_id"));
				map.put("name", rs.getString("attendance_type_name"));
				list.add(map);
			}
		}
		return list;
	}

	// 4. 휴가항목 목록 조회
	public List<java.util.Map<String, String>> getVacationTypes(Connection conn) throws SQLException {
		List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
		// 실제 DB 컬럼명으로 수정 완료
		String sql = "SELECT vacation_type_id, vacation_type_name FROM vacation_type ORDER BY vacation_type_id";
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				java.util.Map<String, String> map = new java.util.HashMap<>();
				map.put("id", rs.getString("vacation_type_id"));
				map.put("name", rs.getString("vacation_type_name"));
				list.add(map);
			}
		}
		return list;
	}

	// 5. 체크박스 조건에 따른 동적 검색 쿼리 (JSON 문자열 반환)
	public String searchAttendanceDetailsJson(Connection conn, java.util.Map<String, String> params)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TO_CHAR(a.input_date, 'YYYY-MM-DD') as input_date, ")
				.append("e.employment_type, e.korean_name, d.department_name, p.position_name, ")
				.append("t.attendance_type_name, ")
				.append("TO_CHAR(a.start_date, 'YY-MM-DD') || ' ~ ' || TO_CHAR(a.end_date, 'YY-MM-DD') as att_period, ")
				.append("a.attendance_days, NVL(a.amount, 0) as amount, NVL(a.summary, '') as summary ")
				.append("FROM attendance a ").append("JOIN employee e ON a.employee_id = e.employee_id ")
				.append("LEFT JOIN department d ON e.department_id = d.department_id ")
				.append("LEFT JOIN position p ON e.position_id = p.position_id ")
				.append("LEFT JOIN attendance_type t ON a.attendance_type_id = t.attendance_type_id ")
				.append("WHERE 1=1 ");

		// 동적 WHERE 조건 추가 (체크박스가 선택된 항목만 params에 들어옴)
		if (params.containsKey("inputDate"))
			sql.append(" AND TO_CHAR(a.input_date, 'YYYY-MM-DD') = ? ");
		if (params.containsKey("startDate"))
			sql.append(" AND TO_CHAR(a.start_date, 'YYYY-MM-DD') >= ? ");
		if (params.containsKey("endDate"))
			sql.append(" AND TO_CHAR(a.end_date, 'YYYY-MM-DD') <= ? ");
		if (params.containsKey("deptId"))
			sql.append(" AND e.department_id = ? ");
		if (params.containsKey("empName"))
			sql.append(" AND e.korean_name LIKE ? ");
		if (params.containsKey("attTypeId"))
			sql.append(" AND a.attendance_type_id = ? ");
		if (params.containsKey("summary"))
			sql.append(" AND a.summary LIKE ? ");

		sql.append("ORDER BY a.input_date DESC, a.attendance_id DESC");

		StringBuilder json = new StringBuilder();
		json.append("[");

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			// 조건 값 세팅
			if (params.containsKey("inputDate"))
				pstmt.setString(idx++, params.get("inputDate"));
			if (params.containsKey("startDate"))
				pstmt.setString(idx++, params.get("startDate"));
			if (params.containsKey("endDate"))
				pstmt.setString(idx++, params.get("endDate"));
			if (params.containsKey("deptId"))
				pstmt.setInt(idx++, Integer.parseInt(params.get("deptId")));
			if (params.containsKey("empName"))
				pstmt.setString(idx++, "%" + params.get("empName") + "%");
			if (params.containsKey("attTypeId"))
				pstmt.setInt(idx++, Integer.parseInt(params.get("attTypeId")));
			if (params.containsKey("summary"))
				pstmt.setString(idx++, "%" + params.get("summary") + "%");

			try (ResultSet rs = pstmt.executeQuery()) {
				boolean first = true;
				while (rs.next()) {
					if (!first)
						json.append(",");
					json.append("{").append("\"inputDate\":\"").append(rs.getString("input_date")).append("\",")
							.append("\"empType\":\"")
							.append(rs.getString("employment_type") == null ? "" : rs.getString("employment_type"))
							.append("\",").append("\"empName\":\"").append(rs.getString("korean_name")).append("\",")
							.append("\"deptName\":\"")
							.append(rs.getString("department_name") == null ? "" : rs.getString("department_name"))
							.append("\",").append("\"positionName\":\"")
							.append(rs.getString("position_name") == null ? "" : rs.getString("position_name"))
							.append("\",").append("\"attTypeName\":\"")
							.append(rs.getString("attendance_type_name") == null ? ""
									: rs.getString("attendance_type_name"))
							.append("\",").append("\"attPeriod\":\"").append(rs.getString("att_period")).append("\",")
							.append("\"attDays\":\"").append(rs.getDouble("attendance_days")).append("\",")
							.append("\"amount\":\"").append(String.format("%,d", rs.getInt("amount"))).append("\",")
							.append("\"summary\":\"").append(rs.getString("summary")).append("\"}");
					first = false;
				}
			}
		}
		json.append("]");
		return json.toString();
	}

}