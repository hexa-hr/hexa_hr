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
import wage.service.WagePaymentDeleteService;
import wage.service.WagePaymentInputService;

// 給与入力画面 - 選択社員の削除Handler
public class WagePaymentInputDeleteHandler
	implements CommandHandler {

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

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

			String normalizedWageMonth = normalizeWageMonth(
				req.getParameter(
					"wageMonth"));

			String normalizedWagePeriod = normalizeWagePeriod(
				req.getParameter(
					"wagePeriod"));

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			String deleteMode = normalizeDeleteMode(
				req.getParameter(
					"deleteMode"));

			boolean deleteConfirmed = "true".equals(
				req.getParameter(
					"deleteConfirmed"));

			if (!deleteConfirmed) {

				throw new IllegalArgumentException(
					"削除の確認が必要です。");
			}

			if ("all".equals(
				deleteMode)
				&& !"true".equals(
					req.getParameter(
						"deleteFinalConfirmed"))) {

				throw new IllegalArgumentException(
					"削除の最終確認が必要です。");
			}

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(
				req);

			List<EmployeeSelectRow> employeeRows = employeeSelectService
				.getEmployeeRows(
					null,
					null,
					null);

			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService
				.getSavedEmployees(
					normalizedWageMonth,
					normalizedWagePeriod);

			List<EmployeeSelectRow> allPendingEmployees = buildPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			List<EmployeeSelectRow> remainingPendingEmployees;

			if ("all".equals(
				deleteMode)) {

				remainingPendingEmployees = deleteAllEmployees(
					allSavedEmployees,
					allPendingEmployees,
					incomeType,
					normalizedWageMonth,
					normalizedWagePeriod);

			} else {

				Integer employeeId = parseEmployeeId(
					req.getParameter(
						"employeeId"));

				remainingPendingEmployees = deleteSelectedEmployee(
					allSavedEmployees,
					allPendingEmployees,
					employeeId,
					incomeType,
					normalizedWageMonth,
					normalizedWagePeriod);
			}

			StringBuilder redirectUrl = new StringBuilder();

			redirectUrl.append(
				req.getContextPath());

			redirectUrl.append(
				"/wage/paymentInput.do");

			redirectUrl.append(
				"?wageMonth=");

			redirectUrl.append(
				encode(
					normalizedWageMonth));

			redirectUrl.append(
				"&wagePeriod=");

			redirectUrl.append(
				encode(
					normalizedWagePeriod));

			redirectUrl.append(
				"&incomeType=");

			redirectUrl.append(
				encode(
					incomeType));

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
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<EmployeeSelectRow> allPendingEmployees,
		Integer employeeId,
		String incomeType,
		String wageMonth,
		String wagePeriod) {

		WagePaymentEmployeeRow selectedSavedEmployee = findSavedEmployee(
			allSavedEmployees,
			employeeId);

		EmployeeSelectRow selectedPendingEmployee = findEmployee(
			allPendingEmployees,
			employeeId);

		if (selectedSavedEmployee == null
			&& selectedPendingEmployee == null) {

			throw new IllegalArgumentException(
				"現在の給与一覧に存在しない社員です。");
		}

		String employmentType = selectedSavedEmployee != null
			? selectedSavedEmployee.getEmploymentType()
			: selectedPendingEmployee.getEmploymentType();

		if (!isAvailableEmploymentTypeForIncomeType(
			employmentType,
			incomeType)) {

			throw new IllegalArgumentException(
				"現在の所得区分では削除できない社員です。");
		}

		if (selectedSavedEmployee != null) {

			wagePaymentDeleteService.delete(
				employeeId,
				wageMonth,
				wagePeriod);
		}

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow pendingEmployee : allPendingEmployees) {

			if (!employeeId.equals(
				pendingEmployee.getEmployeeId())) {

				result.add(
					pendingEmployee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> deleteAllEmployees(
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<EmployeeSelectRow> allPendingEmployees,
		String incomeType,
		String wageMonth,
		String wagePeriod) {

		List<Integer> savedEmployeeIds = new ArrayList<>();

		for (WagePaymentEmployeeRow savedEmployee : allSavedEmployees) {

			if (isAvailableEmploymentTypeForIncomeType(
				savedEmployee.getEmploymentType(),
				incomeType)) {

				savedEmployeeIds.add(
					savedEmployee.getEmployeeId());
			}
		}

		List<EmployeeSelectRow> remainingPendingEmployees = new ArrayList<>();

		boolean hasCurrentPendingEmployee = false;

		for (EmployeeSelectRow pendingEmployee : allPendingEmployees) {

			if (isAvailableEmploymentTypeForIncomeType(
				pendingEmployee.getEmploymentType(),
				incomeType)) {

				hasCurrentPendingEmployee = true;
				continue;
			}

			remainingPendingEmployees.add(
				pendingEmployee);
		}

		if (savedEmployeeIds.isEmpty()
			&& !hasCurrentPendingEmployee) {

			throw new IllegalArgumentException(
				"追加された社員がいません。");
		}

		if (!savedEmployeeIds.isEmpty()) {

			wagePaymentDeleteService.deleteEmployees(
				savedEmployeeIds,
				wageMonth,
				wagePeriod);
		}

		return remainingPendingEmployees;
	}

	private String normalizeDeleteMode(
		String deleteMode) {

		deleteMode = trim(
			deleteMode);

		if (deleteMode == null) {

			return "selected";
		}

		if ("selected".equals(
			deleteMode)
			|| "all".equals(
				deleteMode)) {

			return deleteMode;
		}

		throw new IllegalArgumentException(
			"正しい削除方法を選択する必要があります。");
	}

	private Integer parseEmployeeId(
		String employeeIdValue) {

		employeeIdValue = trim(
			employeeIdValue);

		if (employeeIdValue == null) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}

		try {

			Integer employeeId = Integer.valueOf(
				employeeIdValue);

			if (employeeId <= 0) {

				throw new NumberFormatException();
			}

			return employeeId;

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}
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

			value = trim(
				value);

			if (value == null) {
				continue;
			}

			try {

				Integer employeeId = Integer.valueOf(
					value);

				if (employeeId <= 0) {

					throw new NumberFormatException();
				}

				result.add(
					employeeId);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"pending社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(
			result);
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

	private List<EmployeeSelectRow> buildPendingEmployees(
		List<EmployeeSelectRow> employeeRows,
		List<WagePaymentEmployeeRow> savedEmployees,
		List<Integer> pendingEmployeeIds) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (Integer employeeId : pendingEmployeeIds) {

			EmployeeSelectRow employee = findEmployee(
				employeeRows,
				employeeId);

			if (employee == null
				|| !isAvailableEmployeeForGeneralPayment(
					employee)
				|| findSavedEmployee(
					savedEmployees,
					employeeId) != null
				|| containsEmployee(
					result,
					employeeId)) {

				continue;
			}

			result.add(
				employee);
		}

		return result;
	}

	private boolean containsEmployee(
		List<EmployeeSelectRow> employees,
		Integer employeeId) {

		return findEmployee(
			employees,
			employeeId) != null;
	}

	private boolean isAvailableEmployeeForGeneralPayment(
		EmployeeSelectRow employee) {

		return !"일용직".equals(
			employee.getEmploymentType());
	}

	private boolean isAvailableEmploymentTypeForIncomeType(
		String employmentType,
		String incomeType) {

		if ("worker".equals(
			incomeType)) {

			return !"임시직".equals(
				employmentType)
				&& !"일용직".equals(
					employmentType);
		}

		if ("business".equals(
			incomeType)) {

			return "임시직".equals(
				employmentType);
		}

		return false;
	}

	private String normalizeWageMonth(
		String wageMonth) {

		wageMonth = trim(
			wageMonth);

		if (wageMonth == null) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		try {

			YearMonth.parse(
				wageMonth);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		return wageMonth;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		wagePeriod = trim(
			wagePeriod);

		if (wagePeriod == null) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		int period;

		try {

			period = Integer.parseInt(
				wagePeriod);

			if (period < 1
				|| period > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"給与回次は1以上10以下の数値である必要があります。");
		}

		return String.valueOf(
			period);
	}

	private String normalizeIncomeType(
		String incomeType) {

		incomeType = trim(
			incomeType);

		if (incomeType == null) {

			return "worker";
		}

		if ("worker".equals(
			incomeType)
			|| "business".equals(
				incomeType)) {

			return incomeType;
		}

		throw new IllegalArgumentException(
			"正しい所得区分を選択する必要があります。");
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