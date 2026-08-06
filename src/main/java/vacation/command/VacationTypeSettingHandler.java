package vacation.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.service.VacationTypeService;

public class VacationTypeSettingHandler implements CommandHandler {

	private VacationTypeService vacationService = new VacationTypeService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setAttribute("vacationList", vacationService.getVacationList());
		return "/WEB-INF/view/attendance/vacationTypeSetting.jsp"; // 본인 JSP 경로
	}
}