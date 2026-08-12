package wage.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.WageTypeOption;
import mvc.command.CommandHandler;
import wage.model.WageItemLedgerResult;
import wage.service.WageItemLedgerService;

//항목별 대장 화면 요청 처리
public class WageItemLedgerHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageItemLedger.jsp";

	private WageItemLedgerService wageItemLedgerService = new WageItemLedgerService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		// 검색 화면의 급여항목 선택 목록
		List<WageTypeOption> wageTypeOptions = wageItemLedgerService.getWageTypeOptions();

		req.setAttribute("wageTypeOptions", wageTypeOptions);

		String wageTypeIdParam = trim(req.getParameter("wageTypeId"));
		String startMonth = trim(req.getParameter("startMonth"));
		String endMonth = trim(req.getParameter("endMonth"));

		// 검색 후에도 사용자가 선택한 조건을 유지
		req.setAttribute(
			"selectedWageTypeId", wageTypeIdParam);
		req.setAttribute("startMonth", startMonth);
		req.setAttribute("endMonth", endMonth);

		// 처음 화면에 들어온 경우에는 검색하지 않음
		boolean hasSearchCondition = wageTypeIdParam != null
			|| startMonth != null
			|| endMonth != null;

		if (!hasSearchCondition) {
			return FORM_VIEW;
		}

		try {
			if (wageTypeIdParam == null) {
				throw new IllegalArgumentException(
					"급여항목을 선택해야 합니다.");
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
				"올바른 급여항목을 선택해야 합니다.");

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
