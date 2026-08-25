package wage.command;

import java.time.Year;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageLedgerSummaryResult;
import wage.service.WageLedgerService;

// 給与台帳の給与回次一覧照会Handler
public class WageLedgerHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageLedger.jsp";

	private WageLedgerService wageLedgerService = new WageLedgerService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String year = req.getParameter("year");

		// 初回アクセス時は現在の年度を初期値として使用
		if (year == null || year.trim().isEmpty()) {
			year = String.valueOf(Year.now().getValue());
		} else {
			year = year.trim();
		}

		req.setAttribute("selectedYear", year);

		String deleteResult = req.getParameter(
			"deleteResult");

		if ("success".equals(
			deleteResult)) {

			req.setAttribute(
				"deleteMessage",
				"削除されました。");

		} else if ("notFound".equals(
			deleteResult)) {

			req.setAttribute(
				"deleteMessage",
				"削除する給与台帳がありません。");
		}

		try {

			WageLedgerSummaryResult result = wageLedgerService.getWageLedgerSummaries(year);

			req.setAttribute("ledgerSummary", result);

		} catch (IllegalArgumentException e) {

			req.setAttribute("errorMessage", e.getMessage());
		}

		return FORM_VIEW;
	}
}