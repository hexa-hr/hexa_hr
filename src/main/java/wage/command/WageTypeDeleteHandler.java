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
				int wageTypeId = Integer.parseInt(wageTypeIdStr.trim());
				wageService.deleteWageType(wageTypeId);
			}
			res.sendRedirect(req.getContextPath() + "/wageTypeSetting.do");
			return null;
		}
		return null;
	}
}