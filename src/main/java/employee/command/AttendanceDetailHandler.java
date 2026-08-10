package employee.command;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class AttendanceDetailHandler implements CommandHandler {

	private AttendanceDao dao = new AttendanceDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String isAjax = req.getHeader("X-Requested-With");

		try (Connection conn = ConnectionProvider.getConnection()) {

			// 1. AJAX 검색 요청 시 동작 (체크된 항목만 파라미터로 수집)
			if ("XMLHttpRequest".equals(isAjax)) {
				Map<String, String> params = new HashMap<>();

				if ("true".equals(req.getParameter("chkInputDate")))
					params.put("inputDate", req.getParameter("inputDate"));

				if ("true".equals(req.getParameter("chkAttPeriod"))) {
					params.put("startDate", req.getParameter("startDate"));
					params.put("endDate", req.getParameter("endDate"));
				}

				if ("true".equals(req.getParameter("chkDept")))
					params.put("deptId", req.getParameter("deptId"));
				if ("true".equals(req.getParameter("chkName")))
					params.put("empName", req.getParameter("empName"));
				if ("true".equals(req.getParameter("chkAttType")))
					params.put("attTypeId", req.getParameter("attTypeId"));
				if ("true".equals(req.getParameter("chkSummary")))
					params.put("summary", req.getParameter("summary"));

				// JSON 텍스트 그대로 반환
				String jsonResult = dao.searchAttendanceDetailsJson(conn, params);
				res.setContentType("application/json; charset=UTF-8");
				res.getWriter().write(jsonResult);
				return null;
			}

			// 2. 일반 접속 시 (페이지 로드) 드롭다운 목록 데이터를 전달하고 화면 포워딩
			else {
				req.setAttribute("deptList", dao.getDepartments(conn));
				req.setAttribute("attGroupList", dao.getAttendanceGroups(conn));
				req.setAttribute("attTypeList", dao.getAttendanceTypes(conn));
				req.setAttribute("vacTypeList", dao.getVacationTypes(conn));

				return "/attendance/attendanceDetail.jsp";
			}
		}
	}
}