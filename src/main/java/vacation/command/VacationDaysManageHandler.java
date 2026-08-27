package vacation.command;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.service.VacationService;

public class VacationDaysManageHandler implements CommandHandler {

	private VacationService vacationService = new VacationService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");
		String method = req.getMethod();

		if (method.equalsIgnoreCase("GET")) {
			int attendanceTypeId = Integer.parseInt(req.getParameter("attendanceTypeId"));
			List<Map<String, Object>> list = vacationService.getEmployeeVacationList(attendanceTypeId);

			req.setAttribute("attendanceTypeId", attendanceTypeId);
			req.setAttribute("empVacationList", list);

			return "/WEB-INF/view/vacation/vacationDaysManage.jsp";

		} else if (method.equalsIgnoreCase("POST")) {
			int attendanceTypeId = Integer.parseInt(req.getParameter("attendanceTypeId"));
			String[] employeeIds = req.getParameterValues("employeeId");
			String[] vacationDays = req.getParameterValues("vacationDays");

			// 1. 휴가일수 저장 처리
			vacationService.saveVacationDays(attendanceTypeId, employeeIds, vacationDays);

			// 2. 자바스크립트 없이 처리: 저장 완료 후 자기 자신(GET)으로 리다이렉트 (저장 완료 상태 표시용)
			res.sendRedirect(
				req.getContextPath() + "/vacationDaysManage.do?attendanceTypeId=" + attendanceTypeId + "&saved=true");
			return null;
		}

		return null;
	}
}