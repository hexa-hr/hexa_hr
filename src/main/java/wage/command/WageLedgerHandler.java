package wage.command;

import java.time.Year;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageLedgerSummaryResult;
import wage.service.WageLedgerService;

// 급여대장 급여차수 목록 조회 Handler
public class WageLedgerHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageLedger.jsp";

	private WageLedgerService wageLedgerService = new WageLedgerService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String year = req.getParameter("year");

		// 최초 진입 시 현재 연도를 기본값으로 사용
		if (year == null || year.trim().isEmpty()) {
			year = String.valueOf(Year.now().getValue());
		} else {
			year = year.trim();
		}

		req.setAttribute("selectedYear", year);

		try {

			WageLedgerSummaryResult result = wageLedgerService.getWageLedgerSummaries(year);

			req.setAttribute("ledgerSummary", result);

		} catch (IllegalArgumentException e) {

			req.setAttribute("errorMessage", e.getMessage());
		}

		return FORM_VIEW;
	}
}