package attendance.command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import attendance.model.AttendanceVO;
import attendance.model.EmployeeVO;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class MonthlyAttendanceHandler implements CommandHandler {

	private AttendanceDao attendanceDao = new AttendanceDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String year = req.getParameter("year");
		String month = req.getParameter("month");

		// 1. 단순 페이지 접속 시 JSP 화면으로 포워딩
		if (year == null || month == null || year.trim().isEmpty() || month.trim().isEmpty()) {
			return "/attendance/monthlyAttendance.jsp";
		}

		// 2. 월 두 자리 포맷팅 (예: "8" -> "08")
		if (month.length() == 1) {
			month = "0" + month;
		}
		String yearMonth = year + "-" + month;

		List<EmployeeVO> empList = null;
		List<AttendanceVO> attendanceList = null;

		try (Connection conn = ConnectionProvider.getConnection()) {
			empList = attendanceDao.selectAllEmployees(conn);
			attendanceList = attendanceDao.selectMonthlyAttendance(conn, yearMonth);
		}

		// 3. JSON 응답 생성 (Null 방지 처리)
		res.setContentType("application/json; charset=UTF-8");
		StringBuilder json = new StringBuilder();
		json.append("{");

		// 사원 목록 JSON
		json.append("\"employees\":[");
		if (empList != null) {
			for (int i = 0; i < empList.size(); i++) {
				EmployeeVO e = empList.get(i);
				json.append("{").append("\"employeeId\":").append(e.getEmployeeId()).append(",")
						.append("\"employmentType\":\"")
						.append(e.getEmploymentType() != null ? e.getEmploymentType() : "").append("\",")
						.append("\"koreanName\":\"").append(e.getKoreanName() != null ? e.getKoreanName() : "")
						.append("\",").append("\"departmentName\":\"")
						.append(e.getDepartmentName() != null ? e.getDepartmentName() : "").append("\",")
						.append("\"positionName\":\"").append(e.getPositionName() != null ? e.getPositionName() : "")
						.append("\"").append("}");
				if (i < empList.size() - 1)
					json.append(",");
			}
		}
		json.append("],");

		// 근태 목록 JSON
		json.append("\"attendances\":[");
		if (attendanceList != null) {
			for (int i = 0; i < attendanceList.size(); i++) {
				AttendanceVO a = attendanceList.get(i);
				json.append("{").append("\"employeeId\":").append(a.getEmployeeId()).append(",")
						.append("\"attendanceTypeName\":\"")
						.append(a.getAttendanceTypeName() != null ? a.getAttendanceTypeName() : "").append("\",")
						.append("\"startDate\":\"").append(a.getStartDate() != null ? a.getStartDate().toString() : "")
						.append("\",").append("\"endDate\":\"")
						.append(a.getEndDate() != null ? a.getEndDate().toString() : "").append("\",")
						.append("\"attendanceDays\":").append(a.getAttendanceDays()).append("}");
				if (i < attendanceList.size() - 1)
					json.append(",");
			}
		}
		json.append("]");

		json.append("}");

		res.getWriter().write(json.toString());
		return null;
	}
}