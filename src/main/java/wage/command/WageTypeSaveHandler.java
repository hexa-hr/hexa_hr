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

		String numberCut = req.getParameter("numberCut");
		String usage = req.getParameter("usage");
		String taxFreeName = req.getParameter("taxFreeName");

		// 💡 핵심: 근태연결 vs 일괄지급 데이터 매핑 분기 처리
		String selectedOption = req.getParameter("attendanceOrLumpsum"); // 셀렉트박스 선택값
		String rawLumpsumContent = req.getParameter("attendanceOrLumpsumContent"); // 일괄지급액 입력값

		String attendanceOrLumpsum = "";
		String attendanceOrLumpsumContent = "";

		if ("一括支給".equals(selectedOption)) {
			attendanceOrLumpsum = "一括支給";
			attendanceOrLumpsumContent = rawLumpsumContent; // 금액
		} else if (selectedOption != null && !selectedOption.trim().isEmpty()) {
			attendanceOrLumpsum = "勤怠連動"; // 대분류 고정
			attendanceOrLumpsumContent = selectedOption; // 상세 근태명 (예: 잔업 등)
		}

		// DTO 객체 생성
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
				errorMessage = "既に存在する支払/控除項目の名前です。";
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