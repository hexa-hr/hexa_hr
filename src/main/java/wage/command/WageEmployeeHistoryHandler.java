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

// 社員別給与履歴照会Handler
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

		// 初回表示時は現在年の1月から現在月までをデフォルトの照会期間として使用
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

		// 社員選択画面で使用する全社員一覧
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(null, null, null);

		req.setAttribute("employeeRows", employeeRows);

		// 初回表示時は照会しない
		if (!searchRequested && employeeIdParam == null) {
			return FORM_VIEW;
		}

		// 照会ボタンを押したが社員を選択していない場合
		if (searchRequested && employeeIdParam == null) {
			req.setAttribute(
				"errorMessage",
				"社員を選択する必要があります。");

			return FORM_VIEW;
		}

		try {

			Integer employeeId = Integer.valueOf(employeeIdParam);

			// 選択したemployeeIdに該当する実際の社員名を確認
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