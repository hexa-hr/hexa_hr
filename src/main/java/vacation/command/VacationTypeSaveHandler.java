package vacation.command;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.VacationTypeService;

public class VacationTypeSaveHandler implements CommandHandler {

	private VacationTypeService vacationService = new VacationTypeService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("GET")) {
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		// 1. 파라미터 수신
		String vacationTypeName = req.getParameter("vacationTypeName");
		String applyPeriod1 = req.getParameter("applyPeriod1");
		String applyPeriod2 = req.getParameter("applyPeriod2");
		String usage = req.getParameter("usage");

		// 2. 유효성 검증
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);

		if (vacationTypeName == null || vacationTypeName.trim().isEmpty()) {
			errors.put("vacationTypeName", Boolean.TRUE);
		}
		if (applyPeriod1 == null || applyPeriod1.trim().isEmpty() ||
			applyPeriod2 == null || applyPeriod2.trim().isEmpty()) {
			errors.put("applyPeriod", Boolean.TRUE);
		}

		// 검증 에러 시 기존 페이지로 돌아감
		if (!errors.isEmpty()) {
			req.setAttribute("vacationList", vacationService.getVacationList());
			return "/WEB-INF/view/attendance/vacationTypeSetting.jsp"; // 작성하신 JSP 경로 지정
		}

		// VacationTypeSaveHandler.java 내부 process 메소드 파트
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		VacationType vacation = new VacationType();
		vacation.setVacationTypeName(vacationTypeName);
		vacation.setApplyPeriod1(sdf.parse(applyPeriod1));
		vacation.setApplyPeriod2(sdf.parse(applyPeriod2));
		vacation.setUsage(usage != null ? usage : "Y");

		vacationService.addVacationType(vacation);

		// 4. 저장 완료 후 목록 URL로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}