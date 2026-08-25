package wage.command;

import java.time.Year;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageMonthlyPersonalStatisticsResult;
import wage.service.WageMonthlyPersonalStatisticsService;

// 월별 개인급여 통계 조회 Handler
public class WageMonthlyPersonalStatisticsHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageMonthlyPersonalStatistics.jsp";

	private WageMonthlyPersonalStatisticsService wageMonthlyPersonalStatisticsService = new WageMonthlyPersonalStatisticsService();

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String year = trim(req.getParameter("year"));
		String employeeIdParam = trim(req.getParameter("employeeId"));
		boolean searchRequested = "true".equals(req.getParameter("search"));

		// 최초 진입 시 현재 연도를 기본값으로 사용
		if (year == null) {
			year = String.valueOf(Year.now().getValue());
		}

		req.setAttribute(
			"selectedYear",
			year);

		req.setAttribute(
			"selectedEmployeeId",
			employeeIdParam);

		// 사원 선택 모달에서 사용할 전체 사원 목록
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
			null,
			null,
			null);

		req.setAttribute(
			"employeeRows",
			employeeRows);

		// 최초 진입 시에는 통계를 조회하지 않음
		if (!searchRequested && employeeIdParam == null) {
			return FORM_VIEW;
		}

		// 조회 버튼을 눌렀지만 사원을 선택하지 않은 경우
		if (searchRequested && employeeIdParam == null) {

			req.setAttribute(
				"errorMessage",
				"社員を選択する必要があります。");

			return FORM_VIEW;
		}

		try {

			Integer employeeId = Integer.valueOf(employeeIdParam);

			// 전달된 employeeId가 실제 존재하는 사원인지 확인
			EmployeeSelectRow selectedEmployee = null;

			for (EmployeeSelectRow employeeRow : employeeRows) {

				if (employeeId.equals(
					employeeRow.getEmployeeId())) {

					selectedEmployee = employeeRow;
					break;
				}
			}

			if (selectedEmployee == null) {
				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			req.setAttribute(
				"selectedEmployeeName",
				selectedEmployee.getKoreanName());

			WageMonthlyPersonalStatisticsResult result = wageMonthlyPersonalStatisticsService
				.getMonthlyPersonalStatistics(
					employeeId,
					year);

			req.setAttribute(
				"monthlyPersonalStatistics",
				result);

		} catch (NumberFormatException e) {

			req.setAttribute(
				"errorMessage",
				"正しい社員を選択する必要があります。");

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