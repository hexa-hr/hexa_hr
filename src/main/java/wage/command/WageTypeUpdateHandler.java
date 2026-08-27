package wage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.WageType;
import mvc.command.CommandHandler;
import wage.service.WageService;

public class WageTypeUpdateHandler implements CommandHandler {

	private WageService wageService = new WageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		String wageTypeIdStr = req.getParameter("wageTypeId");
		Integer wageTypeId = null;
		if (wageTypeIdStr != null && !wageTypeIdStr.trim().isEmpty()) {
			wageTypeId = Integer.parseInt(wageTypeIdStr.trim());
		}

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

		// 💡 수정 시에도 동일한 분기 로직 적용
		String selectedOption = req.getParameter("attendanceOrLumpsum");
		String rawLumpsumContent = req.getParameter("attendanceOrLumpsumContent");

		String attendanceOrLumpsum = "";
		String attendanceOrLumpsumContent = "";

		if ("일괄지급".equals(selectedOption)) {
			attendanceOrLumpsum = "일괄지급";
			attendanceOrLumpsumContent = rawLumpsumContent;
		} else if (selectedOption != null && !selectedOption.trim().isEmpty()) {
			attendanceOrLumpsum = "근태연동";
			attendanceOrLumpsumContent = selectedOption;
		}

		WageType wageType = new WageType(
			wageTypeId,
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
			// 💡 updateWageType 대신 기존에 존재하는 modifyWageType을 호출하도록 변경합니다.
			wageService.modifyWageType(wageType);
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			if (errorMessage == null || errorMessage.trim().isEmpty()) {
				errorMessage = "수정 중 오류가 발생했습니다.";
			}
			req.getSession().setAttribute("errorMessage", errorMessage);
		}

		res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
		return null;
	}
}