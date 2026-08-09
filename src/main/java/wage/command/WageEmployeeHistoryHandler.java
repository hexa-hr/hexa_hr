package wage.command;

import java.time.YearMonth;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageEmployeeHistoryResult;
import wage.service.WageEmployeeHistoryService;

// 사원별 급여내역 조회 Handler
public class WageEmployeeHistoryHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageEmployeeHistory.jsp";

	private WageEmployeeHistoryService wageEmployeeHistoryService = new WageEmployeeHistoryService();

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	@Override
	public String process(HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String startMonth = trim(req.getParameter("startMonth"));
		String endMonth = trim(req.getParameter("endMonth"));
		String employeeIdParam = trim(req.getParameter("employeeId"));
		boolean searchRequested = "true".equals(req.getParameter("search"));

		// 최초 진입 시 현재 연도의 1월부터 현재 월까지를 기본 조회기간으로 사용
		YearMonth currentMonth = YearMonth.now();

		if (startMonth == null) {
			startMonth = currentMonth.getYear() + "-01";
		}

		if (endMonth == null) {
			endMonth = currentMonth.toString();
		}

		req.setAttribute("startMonth", startMonth);
		req.setAttribute("endMonth", endMonth);
		req.setAttribute("selectedEmployeeId", employeeIdParam);

		// 사원 선택 화면에 사용할 전체 사원 목록
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(null, null, null);

		req.setAttribute("employeeRows", employeeRows);

		// 최초 진입 시에는 조회하지 않음
		if (!searchRequested && employeeIdParam == null) {
			return FORM_VIEW;
		}

		// 조회 버튼을 눌렀지만 사원을 선택하지 않은 경우
		if (searchRequested && employeeIdParam == null) {
			req.setAttribute(
				"errorMessage",
				"사원을 선택해야 합니다.");

			return FORM_VIEW;
		}

		try {

			Integer employeeId = Integer.valueOf(employeeIdParam);

			// 선택한 employeeId에 해당하는 실제 사원명 확인
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
					"올바른 사원을 선택해야 합니다.");
			}

			req.setAttribute(
				"selectedEmployeeName",
				selectedEmployee.getKoreanName());

			WageEmployeeHistoryResult result = wageEmployeeHistoryService
				.getWageEmployeeHistory(
					employeeId,
					startMonth,
					endMonth);

			req.setAttribute(
				"employeeHistory",
				result);

		} catch (NumberFormatException e) {

			req.setAttribute(
				"errorMessage",
				"올바른 사원을 선택해야 합니다.");

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