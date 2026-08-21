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

		// PK 식별자 확인
		String wageTypeIdStr = req.getParameter("wageTypeId");
		if (wageTypeIdStr == null || wageTypeIdStr.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
			return null;
		}

		Integer wageTypeId = Integer.parseInt(wageTypeIdStr.trim());
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
		String taxFreeName = req.getParameter("taxFreeName");

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

		// 수정 처리
		wageService.modifyWageType(wageType);

		// 목록 화면으로 이동
		res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
		return null;
	}
}