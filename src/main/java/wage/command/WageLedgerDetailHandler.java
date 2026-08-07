package wage.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageLedgerDetailResult;
import wage.service.WageLedgerService;

// 급여대장 상세 조회 Handler
public class WageLedgerDetailHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageLedgerDetail.jsp";

	private WageLedgerService wageLedgerService = new WageLedgerService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String wageMonth = req.getParameter("wageMonth");
		String wagePeriod = req.getParameter("wagePeriod");

		req.setAttribute("wageMonth", wageMonth);
		req.setAttribute("wagePeriod", wagePeriod);

		try {

			WageLedgerDetailResult result = wageLedgerService.getWageLedgerDetail(
				wageMonth, wagePeriod);

			req.setAttribute("ledgerDetail", result);

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}
}