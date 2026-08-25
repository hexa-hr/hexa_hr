package wage.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.WageTypeOption;
import mvc.command.CommandHandler;
import wage.model.WageItemLedgerResult;
import wage.service.WageItemLedgerService;

// 項目別台帳画面のリクエスト処理
public class WageItemLedgerHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageItemLedger.jsp";

	private WageItemLedgerService wageItemLedgerService = new WageItemLedgerService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		// 検索画面の給与項目選択リスト
		List<WageTypeOption> wageTypeOptions = wageItemLedgerService.getWageTypeOptions();

		req.setAttribute("wageTypeOptions", wageTypeOptions);

		String wageTypeIdParam = trim(req.getParameter("wageTypeId"));
		String startMonth = trim(req.getParameter("startMonth"));
		String endMonth = trim(req.getParameter("endMonth"));

		// 検索後もユーザーが選択した条件を維持
		req.setAttribute(
			"selectedWageTypeId", wageTypeIdParam);
		req.setAttribute("startMonth", startMonth);
		req.setAttribute("endMonth", endMonth);

		// 初回アクセス時は検索しない
		boolean hasSearchCondition = wageTypeIdParam != null
			|| startMonth != null
			|| endMonth != null;

		if (!hasSearchCondition) {
			return FORM_VIEW;
		}

		try {
			if (wageTypeIdParam == null) {
				throw new IllegalArgumentException(
					"給与項目を選択する必要があります。");
			}

			Integer wageTypeId = Integer.valueOf(wageTypeIdParam);

			WageItemLedgerResult ledgerResult = wageItemLedgerService.getItemLedger(
				wageTypeId,
				startMonth,
				endMonth);

			req.setAttribute(
				"ledgerResult", ledgerResult);

		} catch (NumberFormatException e) {
			req.setAttribute(
				"errorMessage",
				"正しい給与項目を選択する必要があります。");

		} catch (IllegalArgumentException e) {
			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}

	private String trim(String value) {

		if (value == null) {
			return null;
		}

		String trimmedValue = value.trim();

		return trimmedValue.isEmpty()
			? null
			: trimmedValue;
	}
}
