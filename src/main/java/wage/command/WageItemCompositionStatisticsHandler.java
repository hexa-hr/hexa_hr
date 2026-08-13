package wage.command;

import java.time.YearMonth;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageItemCompositionStatisticsResult;
import wage.service.WageItemCompositionStatisticsService;

// 급여항목 구성 통계 조회 Handler
public class WageItemCompositionStatisticsHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wageItemCompositionStatistics.jsp";

	private WageItemCompositionStatisticsService wageItemCompositionStatisticsService = new WageItemCompositionStatisticsService();

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res) throws Exception {

		String wageMonth = trim(req.getParameter("wageMonth"));

		String employeeIdParam = trim(req.getParameter("employeeId"));

		boolean searchRequested = "true".equals(req.getParameter("search"));

		// 최초 진입 시 현재 귀속년월을 기본값으로 사용
		if (wageMonth == null) {
			wageMonth = YearMonth.now().toString();
		}

		req.setAttribute(
			"selectedWageMonth",
			wageMonth);

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
		if (!searchRequested
			&& employeeIdParam == null) {

			return FORM_VIEW;
		}

		// 조회 버튼을 눌렀지만 사원을 선택하지 않은 경우
		if (searchRequested
			&& employeeIdParam == null) {

			req.setAttribute(
				"errorMessage",
				"사원을 선택해야 합니다.");

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
					"올바른 사원을 선택해야 합니다.");
			}

			req.setAttribute(
				"selectedEmployeeName",
				selectedEmployee.getKoreanName());

			WageItemCompositionStatisticsResult result = wageItemCompositionStatisticsService
				.getItemCompositionStatistics(
					employeeId,
					wageMonth);

			req.setAttribute(
				"itemCompositionStatistics",
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