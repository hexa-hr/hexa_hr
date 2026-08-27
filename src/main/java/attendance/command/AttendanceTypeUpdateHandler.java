package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.service.AttendanceService;
import master.model.AttendanceType;
import mvc.command.CommandHandler;

public class AttendanceTypeUpdateHandler implements CommandHandler {

	private AttendanceService attendanceService = new AttendanceService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		String idStr = req.getParameter("attendanceTypeId");

		// 👉 1. 레코드를 선택하지 않은 경우 세션에 메시지 담고 리다이렉트
		if (idStr == null || idStr.trim().isEmpty()) {
			HttpSession session = req.getSession();
			session.setAttribute("errorMessage", "근태항목 목록에서 레코드를 선택해 주세요.");
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		int attendanceTypeId = Integer.parseInt(idStr);
		String name = req.getParameter("name");
		String unit = req.getParameter("unit");

		String groupStr = req.getParameter("attendanceGroupId");
		Integer attendanceGroupId = (groupStr != null && !groupStr.trim().isEmpty()) ? Integer.parseInt(groupStr)
			: null;

		String vacStr = req.getParameter("vacationTypeId");
		Integer vacationTypeId = (vacStr != null && !vacStr.trim().isEmpty()) ? Integer.parseInt(vacStr) : null;

		String usage = req.getParameter("usage");

		AttendanceType att = new AttendanceType(attendanceTypeId, name, unit, attendanceGroupId, vacationTypeId, usage);

		try {
			attendanceService.modifyAttendance(att);
			// ... 에러 발생 시 catch 블록 내부 ...
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			// (중복 에러 체크 로직 동일)

			// 1. 세션에 메시지 저장
			req.getSession().setAttribute("errorMessage", errorMessage);

			// 2. 리다이렉트 (PRG 패턴: Post-Redirect-Get)
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");

			// 3. 컨트롤러가 포워딩하지 않도록 null 리턴
			return null;
		}

		// 정상 수정 후 리다이렉트
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}