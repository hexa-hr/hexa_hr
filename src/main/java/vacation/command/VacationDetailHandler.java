package vacation.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationDetail;
import vacation.service.VacationDetailService;

public class VacationDetailHandler implements CommandHandler {
	private VacationDetailService service = new VacationDetailService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int employeeId = Integer.parseInt(request.getParameter("employeeId"));
		List<VacationDetail> detailList = service.getVacationDetail(employeeId);

		request.setAttribute("detailList", detailList);
		return "/WEB-INF/view/vacation/vacationDetail.jsp";
	}
}