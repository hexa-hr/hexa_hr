package vacation.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.UpdateVacationTypeService;
import vacation.service.VacationTypeService; // 목록 재조회를 위해 추가

public class VacationTypeUpdateHandler implements CommandHandler {

	private UpdateVacationTypeService updateService = new UpdateVacationTypeService();
	private VacationTypeService vacationService = new VacationTypeService(); // 목록 불러오기용

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// 1. 파라미터 수신
		String idStr = req.getParameter("vacationTypeId");

		// 👉 [수정] 레코드를 선택하지 않은 경우 세션에 담고 리다이렉트
		if (idStr == null || idStr.trim().isEmpty()) {
			req.getSession().setAttribute("errorMessage", "휴가항목 목록에서 레코드를 선택해 주세요.");
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		String name = req.getParameter("vacationTypeName");
		String period1Str = req.getParameter("applyPeriod1");
		String period2Str = req.getParameter("applyPeriod2");
		String usage = req.getParameter("usage");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date applyPeriod1 = sdf.parse(period1Str);
		Date applyPeriod2 = sdf.parse(period2Str);

		VacationType vacation = new VacationType();
		vacation.setVacationTypeId(Integer.parseInt(idStr));
		vacation.setVacationTypeName(name);
		vacation.setApplyPeriod1(applyPeriod1);
		vacation.setApplyPeriod2(applyPeriod2);
		vacation.setUsage(usage);

		try {
			updateService.update(vacation);
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				errorMessage = e.getCause().getMessage();
			}

			if (errorMessage.contains("ORA-00001") || errorMessage.contains("중복")) {
				errorMessage = "이미 존재하는 휴가 항목 이름입니다.";
			}

			// 👉 [수정] 중복 에러 발생 시 세션에 담고 리다이렉트
			req.getSession().setAttribute("errorMessage", errorMessage);
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}