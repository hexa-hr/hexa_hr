package wage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.service.WageService;

public class WageTypeDeleteHandler implements CommandHandler {

	private WageService wageService = new WageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("POST")) {
			String wageTypeIdStr = req.getParameter("wageTypeId");
			if (wageTypeIdStr != null && !wageTypeIdStr.trim().isEmpty()) {
				try {
					int wageTypeId = Integer.parseInt(wageTypeIdStr.trim());
					wageService.deleteWageType(wageTypeId);
				} catch (RuntimeException e) {
					// 💡 서비스에서 던진 예외 메시지를 캐치하여 세션에 저장
					String errorMessage = e.getMessage();
					if (errorMessage == null || errorMessage.trim().isEmpty()) {
						errorMessage = "삭제 중 오류가 발생했습니다.";
					}
					req.getSession().setAttribute("errorMessage", errorMessage);
				}
			}
			res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
			return null;
		}
		return null;
	}
}