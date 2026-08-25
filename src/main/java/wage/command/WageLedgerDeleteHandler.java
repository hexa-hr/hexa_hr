package wage.command;

import java.time.YearMonth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.service.WageLedgerDeleteService;

// 給与台帳 - 帰属年月・給与回次の全給与削除Handler
public class WageLedgerDeleteHandler
	implements CommandHandler {

	private WageLedgerDeleteService wageLedgerDeleteService = new WageLedgerDeleteService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		if (!"POST".equalsIgnoreCase(
			req.getMethod())) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		String wageMonth = req.getParameter(
			"wageMonth");

		String wagePeriod = req.getParameter(
			"wagePeriod");

		try {

			if (!"true".equals(
				req.getParameter(
					"deleteConfirmed"))) {

				throw new IllegalArgumentException(
					"削除の確認が必要です。");
			}

			if (!"true".equals(
				req.getParameter(
					"deleteFinalConfirmed"))) {

				throw new IllegalArgumentException(
					"最終削除の確認が必要です。");
			}

			wageLedgerDeleteService.delete(
				wageMonth,
				wagePeriod);

			redirectToLedger(
				req,
				res,
				wageMonth,
				"success");

			return null;

		} catch (IllegalStateException e) {

			redirectToLedger(
				req,
				res,
				wageMonth,
				"notFound");

			return null;

		} catch (IllegalArgumentException e) {

			res.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());

			return null;
		}
	}

	private void redirectToLedger(
		HttpServletRequest req,
		HttpServletResponse res,
		String wageMonth,
		String deleteResult)
		throws Exception {

		String year = String.valueOf(
			YearMonth.parse(
				wageMonth.trim()).getYear());

		res.sendRedirect(
			req.getContextPath()
				+ "/wage/ledger.do?year="
				+ year
				+ "&deleteResult="
				+ deleteResult);
	}
}