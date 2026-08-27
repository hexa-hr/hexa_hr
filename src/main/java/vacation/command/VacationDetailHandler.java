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

		// 💡 1. 요청 파라미터에서 vacationTypeId를 가져옵니다.
		String vacationTypeId = request.getParameter("vacationTypeId");

		// 💡 2. 서비스로 vacationTypeId를 함께 전달합니다.
		List<VacationDetail> detailList = service.getVacationDetail(employeeId, vacationTypeId);

		request.setAttribute("detailList", detailList);
		return "/WEB-INF/view/vacation/vacationDetail.jsp";
	}
}