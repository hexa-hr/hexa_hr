package vacation.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.service.DeleteVacationTypeService;

public class VacationTypeDeleteHandler implements CommandHandler {

	private DeleteVacationTypeService deleteService = new DeleteVacationTypeService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// 1. 파라미터 수신 (PK 값)
		String idStr = req.getParameter("vacationTypeId");

		// 2. 파라미터 검증 및 삭제 처리
		if (idStr != null && !idStr.trim().isEmpty()) {
			int vacationTypeId = Integer.parseInt(idStr);
			deleteService.delete(vacationTypeId);
		}

		// 3. 처리 후 목록 페이지로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}