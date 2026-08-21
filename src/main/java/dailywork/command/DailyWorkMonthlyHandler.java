package dailywork.command;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.model.DailyWorkMonthlyVO;
import dailywork.service.DailyWorkService;
import mvc.command.CommandHandler;

public class DailyWorkMonthlyHandler implements CommandHandler {

	private DailyWorkService dailyWorkService = new DailyWorkService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. 년, 월 파라미터 받기 (기본값: 현재 년월)
		String yearStr = request.getParameter("year");
		String monthStr = request.getParameter("month");

		int year = (yearStr != null) ? Integer.parseInt(yearStr) : LocalDate.now().getYear();
		int month = (monthStr != null) ? Integer.parseInt(monthStr) : LocalDate.now().getMonthValue();

		// DB 조회를 위한 YYYY-MM 문자열 포맷팅
		String yearMonth = String.format("%04d-%02d", year, month);

		// 2. 서비스 단 호출하여 데이터 가져오기
		List<DailyWorkMonthlyVO> summaryList = dailyWorkService.getMonthlySummary(yearMonth);

		// 3. 달력(토/일 색상) 및 말일 계산 로직
		YearMonth ym = YearMonth.of(year, month);
		int lastDay = ym.lengthOfMonth(); // 그 달의 마지막 날짜 (28, 30, 31 등)

		Map<Integer, String> dayColors = new HashMap<>();
		for (int i = 1; i <= lastDay; i++) {
			DayOfWeek dow = ym.atDay(i).getDayOfWeek();
			if (dow == DayOfWeek.SUNDAY) {
				dayColors.put(i, "color: red;");
			} else if (dow == DayOfWeek.SATURDAY) {
				dayColors.put(i, "color: blue;");
			} else {
				dayColors.put(i, "color: #333;"); // 평일 기본색
			}
		}

		// 4. JSP로 데이터 전달
		request.setAttribute("year", year);
		request.setAttribute("month", month);
		request.setAttribute("lastDay", lastDay);
		request.setAttribute("dayColors", dayColors);
		request.setAttribute("summaryList", summaryList);

		return "/WEB-INF/view/dailywork/dailywork_monthly_inquiry.jsp";
	}
}