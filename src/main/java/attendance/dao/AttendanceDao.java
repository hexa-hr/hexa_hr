package attendance.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import attendance.model.AttendanceVO;
import attendance.model.EmployeeVO;
import employee.model.Employee;
import jdbc.JdbcUtil;
import master.model.AttendanceType;

public class AttendanceDao {

	// Integer로 변환하는 헬퍼 메서드
	private Integer getInteger(ResultSet rs, String columnName) throws SQLException {
		Object obj = rs.getObject(columnName);
		if (obj == null)
			return null;
		if (obj instanceof Number) {
			return ((Number)obj).intValue();
		}
		return Integer.parseInt(obj.toString());
	}

	private Long getLong(
		ResultSet rs,
		String columnName)
		throws SQLException {

		Object value = rs.getObject(
			columnName);

		if (value == null) {
			return null;
		}

		if (value instanceof Number) {

			return ((Number)value).longValue();
		}

		return Long.valueOf(
			value.toString());
	}

	// 1. 전체 목록 조회 (master 패키지 DTO 수정 없이 기본 필드만 조회) - 유진님 코드
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

				AttendanceType att = new AttendanceType(rs.getInt("attendance_type_id"),
					rs.getString("attendance_type_name"), rs.getString("unit"), groupId, vacationId,
					rs.getString("USAGE"));
				list.add(att);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 1. 전체 사원 목록 조회 (JOIN department, position) - 나(에스더) 코드
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

	// 2. 근태항목 추가 (Insert) - 유진님 코드
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

	// 2. 특정 사원의 근태 기록 조회 (모달용) - 나(에스더) 코드
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

	// 3. 근태항목 수정 (Update) - 유진님 코드
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

	// 3. 근태 기록 저장 (신규 등록) - 나(에스더) 코드
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

	// 3. 근태 기록 수정 (UPDATE) - 나(에스더) 코드 추가
	public int updateAttendance(Connection conn, AttendanceVO vo) throws SQLException {
		String sql = "UPDATE attendance SET " + "attendance_type_id = ?, " + "start_date = ?, " + "end_date = ?, "
			+ "attendance_days = ?, " + "amount = ?, " + "summary = ? " + "WHERE attendance_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, vo.getAttendanceTypeId());
			pstmt.setDate(2, vo.getStartDate()); // java.sql.Date로 처리됨
			pstmt.setDate(3, vo.getEndDate());
			pstmt.setDouble(4, vo.getAttendanceDays());
			pstmt.setInt(5, vo.getAmount());
			pstmt.setString(6, vo.getSummary());
			pstmt.setInt(7, vo.getAttendanceId());

			return pstmt.executeUpdate();
		}
	}

	// 4. 근태항목 삭제 (Delete) - 유진님 코드
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

	// 4. 근태 기록 삭제 (attendance_id 기준) - 나(에스더) 코드
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

	// 5. 사원별 휴가일수 목록 조회 (LEFT JOIN) - 유진님 코드
	public List<Map<String, Object>> selectEmployeeVacationList(Connection conn, int attendanceTypeId)
		throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT e.*, d.department_name, p.position_name, a.attendance_days " + "FROM employee e "
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
				Employee emp = new Employee(rs.getInt("employee_id"), getInteger(rs, "account_id"),
					getInteger(rs, "company_id"), getInteger(rs, "person_id"), rs.getString("employment_type"),
					rs.getString("korean_name"), rs.getString("english_name"), rs.getDate("hire_date"),
					rs.getDate("resignation_date"), getInteger(rs, "department_id"), getInteger(rs, "position_id"),
					rs.getString("foreign_or_domestic"), rs.getString("resident_number1"),
					rs.getString("resident_number2"), rs.getString("address"), rs.getString("tel_phone"),
					rs.getString("mobile"), rs.getString("email"), rs.getString("sns"),
					rs.getString("other_details"), rs.getString("status"), getLong(rs, "basic_pay"));

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

	// 6. 사원별 휴가일수 저장 (MERGE 구문 사용) - 유진님 코드
	public void saveEmployeeVacationDays(Connection conn, int attendanceTypeId, int employeeId, int days)
		throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "MERGE INTO attendance a " + "USING DUAL ON (a.employee_id = ? AND a.attendance_type_id = ?) "
			+ "WHEN MATCHED THEN " + "  UPDATE SET a.attendance_days = ? " + "WHEN NOT MATCHED THEN "
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

	// 선택된 사원의 휴가일수 삭제 (DELETE 처리) - 유진님 코드
	public void deleteEmployeeVacationDays(Connection conn, int attendanceTypeId, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM attendance WHERE employee_id = ? AND attendance_type_id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, employeeId);
			pstmt.setInt(2, attendanceTypeId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 급여 연동용 - 사원별 근태연결 수당 합계 조회
	public long selectLinkedAllowanceAmount(
		Connection conn,
		int employeeId,
		String attendanceTypeName,
		Date settlementStartDate,
		Date settlementEndDate) throws SQLException {

		String sql = "SELECT NVL(SUM(NVL(a.amount, 0)), 0) AS total_amount "
			+ "FROM attendance a "
			+ "JOIN attendance_type t "
			+ "ON a.attendance_type_id = t.attendance_type_id "
			+ "WHERE a.employee_id = ? "
			+ "AND t.attendance_type_name = ? "
			+ "AND a.start_date BETWEEN ? AND ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setString(2, attendanceTypeName);
			pstmt.setDate(3, settlementStartDate);
			pstmt.setDate(4, settlementEndDate);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					return rs.getLong("total_amount");
				}
			}
		}

		return 0L;
	}

	// 일용직 사원만 조회 (employment_type이 '일용직'인 경우) -나(에스더)코드
	public List<EmployeeVO> selectDailyWorkers(Connection conn) throws SQLException {
		String sql = "SELECT e.employee_id, e.employment_type, e.korean_name, " + "d.department_name, p.position_name "
			+ "FROM employee e " + "LEFT JOIN department d ON e.department_id = d.department_id "
			+ "LEFT JOIN position p ON e.position_id = p.position_id " + "WHERE e.employment_type = '일용직' " // <--
																																																																													// 요렇게
																																																																													// 일용직만
																																																																													// 필터링!
			+ "ORDER BY e.employee_id ASC";

		List<EmployeeVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				list.add(new EmployeeVO(rs.getInt("employee_id"), rs.getString("employment_type"),
					rs.getString("korean_name"), rs.getString("department_name"), rs.getString("position_name")));
			}
		}
		return list;
	}
}