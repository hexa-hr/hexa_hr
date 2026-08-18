package wage.command;

import java.time.Year;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageYearlyTotalStatisticsResult;
import wage.service.WageYearlyTotalStatisticsService;

// 연도별 전체급여 통계 조회 Handler
public class WageYearlyTotalStatisticsHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageYearlyTotalStatistics.jsp";

	private WageYearlyTotalStatisticsService wageYearlyTotalStatisticsService = new WageYearlyTotalStatisticsService();

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

			WageYearlyTotalStatisticsResult result = wageYearlyTotalStatisticsService
				.getYearlyTotalStatistics(year);

			req.setAttribute(
				"yearlyTotalStatistics",
				result);

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}
}