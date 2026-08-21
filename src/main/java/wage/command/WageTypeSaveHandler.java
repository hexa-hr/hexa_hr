package wage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.WageType;
import mvc.command.CommandHandler;
import wage.service.WageService;

public class WageTypeSaveHandler implements CommandHandler {

	private WageService wageService = new WageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// POST 요청 파라미터 한글 처리
		req.setCharacterEncoding("UTF-8");

		// 폼 입력값 추출
		String wageTypeName = req.getParameter("wageTypeName");
		String taxableYn = req.getParameter("taxableYn");
		String itemType = req.getParameter("itemType");

		String taxFreeLimitStr = req.getParameter("taxFreeLimit");
		Long taxFreeLimit = 0L;
		if (taxFreeLimitStr != null && !taxFreeLimitStr.trim().isEmpty()) {
			try {
				taxFreeLimit = Long.parseLong(taxFreeLimitStr.trim());
			} catch (NumberFormatException e) {
				taxFreeLimit = 0L;
			}
		}

		String attendanceOrLumpsumContent = req.getParameter("attendanceOrLumpsumContent");
		String numberCut = req.getParameter("numberCut");
		String attendanceOrLumpsum = req.getParameter("attendanceOrLumpsum");
		String usage = req.getParameter("usage");

		// 비과세 항목명 수신 처리
		String taxFreeName = req.getParameter("taxFreeName");

		// DTO 객체 생성 (wageTypeId는 Auto Increment 또는 DB 시퀀스 처리)
		WageType wageType = new WageType(
			null,
			wageTypeName,
			numberCut,
			attendanceOrLumpsum,
			attendanceOrLumpsumContent,
			usage,
			itemType,
			taxableYn,
			taxFreeLimit,
			taxFreeName);

		try {
			wageService.addWageType(wageType);
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			if (errorMessage == null || errorMessage.trim().isEmpty()) {
				errorMessage = "이미 존재하는 지급/공제 항목 이름입니다.";
			}
			req.getSession().setAttribute("errorMessage", errorMessage);
			res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
			return null;
		}

		// 저장 완료 후 목록 화면으로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
		return null;
	}

}