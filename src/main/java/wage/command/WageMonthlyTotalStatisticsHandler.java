package wage.command;

import java.time.Year;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageMonthlyTotalStatisticsResult;
import wage.service.WageMonthlyTotalStatisticsService;

// 월별 전체급여 통계 조회 Handler
public class WageMonthlyTotalStatisticsHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageMonthlyTotalStatistics.jsp";

	private WageMonthlyTotalStatisticsService wageMonthlyTotalStatisticsService = new WageMonthlyTotalStatisticsService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String year = req.getParameter("year");

		// 최초 진입 시 현재 연도를 기본값으로 사용
		if (year == null || year.trim().isEmpty()) {
			year = String.valueOf(Year.now().getValue());
		} else {
			year = year.trim();
		}

		req.setAttribute(
			"selectedYear",
			year);

		try {

			WageMonthlyTotalStatisticsResult result = wageMonthlyTotalStatisticsService
				.getMonthlyTotalStatistics(year);

			req.setAttribute(
				"monthlyTotalStatistics",
				result);

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}
}