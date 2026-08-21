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

		try {
			// 중복 예외 발생 지점
			vacationService.addVacationType(vacation);
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				errorMessage = e.getCause().getMessage();
			}

			// 👉 [수정] request가 아닌 session에 에러 메시지 저장
			req.getSession().setAttribute("errorMessage", errorMessage);

			// 👉 [수정] setting.do로 리다이렉트 후 종료
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}