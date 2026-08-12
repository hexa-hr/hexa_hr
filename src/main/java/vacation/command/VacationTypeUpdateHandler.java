package vacation.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.UpdateVacationTypeService;

public class VacationTypeUpdateHandler implements CommandHandler {

	private UpdateVacationTypeService updateService = new UpdateVacationTypeService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// 1. 파라미터 수신
		String idStr = req.getParameter("vacationTypeId");
		String name = req.getParameter("vacationTypeName");
		String period1Str = req.getParameter("applyPeriod1"); // "2026-01-01"
		String period2Str = req.getParameter("applyPeriod2"); // "2026-12-31"
		String usage = req.getParameter("usage");

		// 2. 날짜 문자열(String) -> Date 변환
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date applyPeriod1 = sdf.parse(period1Str);
		Date applyPeriod2 = sdf.parse(period2Str);

		// 3. VO 객체 생성 및 값 세팅
		VacationType vacation = new VacationType();
		if (idStr != null && !idStr.trim().isEmpty()) {
			vacation.setVacationTypeId(Integer.parseInt(idStr));
		}
		vacation.setVacationTypeName(name);
		vacation.setApplyPeriod1(applyPeriod1); // Date 타입으로 세팅
		vacation.setApplyPeriod2(applyPeriod2); // Date 타입으로 세팅
		vacation.setUsage(usage);

		// 4. 수정 서비스 실행
		updateService.update(vacation);

		// 5. 처리 후 목록으로 이동
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}