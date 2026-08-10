package vacation.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.service.VacationService;

public class VacationDaysDeleteHandler implements CommandHandler {

	private VacationService vacationService = new VacationService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		if (req.getMethod().equalsIgnoreCase("POST")) {
			int attendanceTypeId = Integer.parseInt(req.getParameter("attendanceTypeId"));
			String[] selectedEmpIds = req.getParameterValues("selectedEmpId");

			// 체크된 사원이 있을 경우 0일로 리셋 처리
			if (selectedEmpIds != null && selectedEmpIds.length > 0) {
				vacationService.resetVacationDays(attendanceTypeId, selectedEmpIds);
			}

			// 처리 완료 후 관리 화면으로 리다이렉트
			res.sendRedirect(
				req.getContextPath() + "/vacationDaysManage.do?attendanceTypeId=" + attendanceTypeId + "&deleted=true");
			return null;
		}

		return null;
	}
}