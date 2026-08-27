package wage.command;

import java.net.URLEncoder;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WagePaymentEmployeeRow;
import wage.service.DailyWagePaymentInputService;
import wage.service.WagePaymentDeleteService;

// 給与入力・管理（日雇い）- 選択・全件削除Handler
public class DailyWagePaymentInputDeleteHandler
	implements CommandHandler {

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private DailyWagePaymentInputService dailyWagePaymentInputService = new DailyWagePaymentInputService();

	private WagePaymentDeleteService wagePaymentDeleteService = new WagePaymentDeleteService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		if (!"POST".equalsIgnoreCase(
			req.getMethod())) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		try {

			String wageMonth = normalizeWageMonth(
				req.getParameter("wageMonth"));

			String wagePeriod = normalizeWagePeriod(
				req.getParameter("wagePeriod"));

			String deleteMode = normalizeDeleteMode(
				req.getParameter("deleteMode"));

			boolean deleteConfirmed = "true".equals(
				req.getParameter("deleteConfirmed"));

			if (!deleteConfirmed) {

				throw new IllegalArgumentException(
					"削除の確認が必要です。");
			}

			if ("all".equals(deleteMode)
				&& !"true".equals(
					req.getParameter(
						"deleteFinalConfirmed"))) {

				throw new IllegalArgumentException(
					"最終削除の確認が必要です。");
			}

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(req);

			List<EmployeeSelectRow> allEmployeeRows = employeeSelectService.getEmployeeRows(
				null,
				null,
				null);

			List<EmployeeSelectRow> dailyEmployees = filterDailyEmployees(
				allEmployeeRows);

			List<WagePaymentEmployeeRow> savedEmployees = dailyWagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			List<EmployeeSelectRow> pendingEmployees = buildPendingEmployees(
				dailyEmployees,
				savedEmployees,
				pendingEmployeeIds);

			List<EmployeeSelectRow> remainingPendingEmployees;

			if ("all".equals(deleteMode)) {

				remainingPendingEmployees = deleteAllEmployees(
					savedEmployees,
					pendingEmployees,
					wageMonth,
					wagePeriod);

			} else {

				Integer employeeId = parseEmployeeId(
					req.getParameter("employeeId"));

				remainingPendingEmployees = deleteSelectedEmployee(
					savedEmployees,
					pendingEmployees,
					employeeId,
					wageMonth,
					wagePeriod);
			}

			StringBuilder redirectUrl = new StringBuilder();

			redirectUrl.append(
				req.getContextPath());

			redirectUrl.append(
				"/wage/dailyPaymentInput.do");

			redirectUrl.append(
				"?wageMonth=");

			redirectUrl.append(
				encode(wageMonth));

			redirectUrl.append(
				"&wagePeriod=");

			redirectUrl.append(
				encode(wagePeriod));

			for (EmployeeSelectRow pendingEmployee : remainingPendingEmployees) {

				redirectUrl.append(
					"&pendingEmployeeId=");

				redirectUrl.append(
					pendingEmployee.getEmployeeId());
			}

			res.sendRedirect(
				redirectUrl.toString());

			return null;

		} catch (IllegalArgumentException
			| IllegalStateException e) {

			res.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());

			return null;
		}
	}

	private List<EmployeeSelectRow> deleteSelectedEmployee(
		List<WagePaymentEmployeeRow> savedEmployees,
		List<EmployeeSelectRow> pendingEmployees,
		Integer employeeId,
		String wageMonth,
		String wagePeriod) {

		WagePaymentEmployeeRow savedEmployee = findSavedEmployee(
			savedEmployees,
			employeeId);

		EmployeeSelectRow pendingEmployee = findEmployee(
			pendingEmployees,
			employeeId);

		if (savedEmployee == null
			&& pendingEmployee == null) {

			throw new IllegalArgumentException(
				"現在の給与一覧に存在しない社員です。");
		}

		if (savedEmployee != null) {

			/*
			 * 指定した社員の該当月・回次のWAGEのみ削除する。
			 * DAILY_WORKは変更しない。
			 */
			wagePaymentDeleteService.delete(
				employeeId,
				wageMonth,
				wagePeriod);
		}

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : pendingEmployees) {

			if (!employeeId.equals(
				employee.getEmployeeId())) {

				result.add(employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> deleteAllEmployees(
		List<WagePaymentEmployeeRow> savedEmployees,
		List<EmployeeSelectRow> pendingEmployees,
		String wageMonth,
		String wagePeriod) {

		if (savedEmployees.isEmpty()
			&& pendingEmployees.isEmpty()) {

			throw new IllegalArgumentException(
				"追加された社員がいません。");
		}

		if (!savedEmployees.isEmpty()) {

			List<Integer> savedEmployeeIds = new ArrayList<>();

			for (WagePaymentEmployeeRow employee : savedEmployees) {

				savedEmployeeIds.add(
					employee.getEmployeeId());
			}

			wagePaymentDeleteService.deleteEmployees(
				savedEmployeeIds,
				wageMonth,
				wagePeriod);
		}

		/*
		 * 現在の日雇いワークスペースのpending社員も
		 * すべて削除する。
		 */
		return new ArrayList<>();
	}

	private List<EmployeeSelectRow> filterDailyEmployees(
		List<EmployeeSelectRow> employees) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employees) {

			if ("일용직".equals(
				employee.getEmploymentType())) {

				result.add(employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> buildPendingEmployees(
		List<EmployeeSelectRow> dailyEmployees,
		List<WagePaymentEmployeeRow> savedEmployees,
		List<Integer> pendingEmployeeIds) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (Integer employeeId : pendingEmployeeIds) {

			EmployeeSelectRow employee = findEmployee(
				dailyEmployees,
				employeeId);

			if (employee == null
				|| findSavedEmployee(
					savedEmployees,
					employeeId) != null
				|| findEmployee(
					result,
					employeeId) != null) {

				continue;
			}

			result.add(employee);
		}

		return result;
	}

	private List<Integer> parsePendingEmployeeIds(
		HttpServletRequest req) {

		String[] values = req.getParameterValues(
			"pendingEmployeeId");

		Set<Integer> result = new LinkedHashSet<>();

		if (values == null) {

			return new ArrayList<>();
		}

		for (String value : values) {

			value = trim(value);

			if (value == null) {
				continue;
			}

			try {

				Integer employeeId = Integer.valueOf(value);

				if (employeeId <= 0) {
					throw new NumberFormatException();
				}

				result.add(employeeId);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"未保存社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(result);
	}

	private Integer parseEmployeeId(
		String value) {

		value = trim(value);

		if (value == null) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}

		try {

			Integer employeeId = Integer.valueOf(value);

			if (employeeId <= 0) {
				throw new NumberFormatException();
			}

			return employeeId;

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}
	}

	private WagePaymentEmployeeRow findSavedEmployee(
		List<WagePaymentEmployeeRow> employees,
		Integer employeeId) {

		for (WagePaymentEmployeeRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return employee;
			}
		}

		return null;
	}

	private EmployeeSelectRow findEmployee(
		List<EmployeeSelectRow> employees,
		Integer employeeId) {

		for (EmployeeSelectRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return employee;
			}
		}

		return null;
	}

	private String normalizeDeleteMode(
		String deleteMode) {

		deleteMode = trim(deleteMode);

		if (deleteMode == null) {
			return "selected";
		}

		if ("selected".equals(deleteMode)
			|| "all".equals(deleteMode)) {

			return deleteMode;
		}

		throw new IllegalArgumentException(
			"正しい削除方法を選択する必要があります。");
	}

	private String normalizeWageMonth(
		String wageMonth) {

		wageMonth = trim(wageMonth);

		if (wageMonth == null) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		try {

			return YearMonth.parse(
				wageMonth).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		wagePeriod = trim(wagePeriod);

		if (wagePeriod == null) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		try {

			int wagePeriodNumber = Integer.parseInt(wagePeriod);

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

			return String.valueOf(
				wagePeriodNumber);

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"給与回次は1以上10以下の数値である必要があります。");
		}
	}

	private String encode(
		String value)
		throws Exception {

		return URLEncoder.encode(
			value,
			"UTF-8");
	}

	private String trim(
		String value) {

		if (value == null) {
			return null;
		}

		value = value.trim();

		return value.isEmpty()
			? null
			: value;
	}
}