package wage.command;

import java.time.YearMonth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageInsuranceDeductionResult;
import wage.service.WageInsuranceDeductionService;

// 4대보험 공제내역 조회 Handler
public class WageInsuranceDeductionHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageInsuranceDeduction.jsp";

	private WageInsuranceDeductionService wageInsuranceDeductionService = new WageInsuranceDeductionService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String wageMonth = trim(req.getParameter("wageMonth"));
		String wagePeriod = trim(req.getParameter("wagePeriod"));

		// 최초 진입 시 전월 귀속연월과 1차를 기본값으로 사용
		if (wageMonth == null) {
			wageMonth = YearMonth.now().minusMonths(1).toString();
		}

		if (wagePeriod == null) {
			wagePeriod = "1";
		}

		req.setAttribute("selectedWageMonth", wageMonth);
		req.setAttribute("selectedWagePeriod", wagePeriod);

		try {

			WageInsuranceDeductionResult result = wageInsuranceDeductionService
				.getWageInsuranceDeduction(
					wageMonth,
					wagePeriod);

			req.setAttribute(
				"insuranceDeduction",
				result);

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