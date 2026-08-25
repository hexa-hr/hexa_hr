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
import wage.model.WagePaymentItemInput;
import wage.service.DailyWagePaymentInputService;
import wage.service.DailyWagePaymentSaveService;

// 給与入力・管理（日雇い）- 選択した社員の給与保存Handler
public class DailyWagePaymentInputSaveHandler
	implements CommandHandler {

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private DailyWagePaymentInputService dailyWagePaymentInputService = new DailyWagePaymentInputService();

	private DailyWagePaymentSaveService dailyWagePaymentSaveService = new DailyWagePaymentSaveService();

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

			Integer employeeId = parseEmployeeId(
				req.getParameter("employeeId"));

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(req);

			List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
				null,
				null,
				null);

			EmployeeSelectRow selectedEmployee = findEmployee(
				employeeRows,
				employeeId);

			if (selectedEmployee == null
				|| !"일용직".equals(
					selectedEmployee.getEmploymentType())) {

				throw new IllegalArgumentException(
					"日雇い社員のみ給与を保存できます。");
			}

			List<WagePaymentEmployeeRow> savedEmployees = dailyWagePaymentInputService.getSavedEmployees(
				wageMonth,
				wagePeriod);

			List<EmployeeSelectRow> pendingEmployees = buildPendingEmployees(
				employeeRows,
				savedEmployees,
				pendingEmployeeIds);

			boolean selectedEmployeeSaved = containsSavedEmployee(
				savedEmployees,
				employeeId);

			boolean selectedEmployeePending = containsEmployee(
				pendingEmployees,
				employeeId);

			if (!selectedEmployeeSaved
				&& !selectedEmployeePending) {

				throw new IllegalArgumentException(
					"現在の給与回次に登録されていない社員です。");
			}

			List<WagePaymentItemInput> deductionInputs = parseDeductionInputs(req);

			/*
			 * ServiceがDAILY_WORKと控除項目構成を
			 * サーバーで再照会し、計算後に保存する。
			 */
			dailyWagePaymentSaveService.save(
				employeeId,
				wageMonth,
				wagePeriod,
				deductionInputs);

			/*
			 * PRG(Post-Redirect-Get)
			 *
			 * 保存済みの社員はpendingから除外し、
			 * その他の未保存社員は維持する。
			 */
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

			redirectUrl.append(
				"&employeeId=");

			redirectUrl.append(
				employeeId);

			for (EmployeeSelectRow pendingEmployee : pendingEmployees) {

				Integer pendingEmployeeId = pendingEmployee.getEmployeeId();

				if (employeeId.equals(
					pendingEmployeeId)) {

					continue;
				}

				redirectUrl.append(
					"&pendingEmployeeId=");

				redirectUrl.append(
					pendingEmployeeId);
			}

			redirectUrl.append(
				"&saved=true");

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

	private List<WagePaymentItemInput> parseDeductionInputs(
		HttpServletRequest req) {

		String[] wageTypeIds = req.getParameterValues(
			"wageTypeId");

		String[] wageValues = req.getParameterValues(
			"wageValue");

		/*
		 * 控除項目が0件で固定されたワークスペースでは
		 * 両方のパラメータが存在しない状態が正常である。
		 */
		if (wageTypeIds == null
			&& wageValues == null) {

			return new ArrayList<>();
		}

		if (wageTypeIds == null
			|| wageValues == null
			|| wageTypeIds.length != wageValues.length) {

			throw new IllegalArgumentException(
				"控除項目情報が正しくありません。");
		}

		List<WagePaymentItemInput> result = new ArrayList<>();

		for (int i = 0; i < wageTypeIds.length; i++) {

			String wageTypeIdValue = trim(wageTypeIds[i]);

			String wageValue = trim(wageValues[i]);

			try {

				if (wageTypeIdValue == null) {
					throw new NumberFormatException();
				}

				Integer wageTypeId = Integer.valueOf(
					wageTypeIdValue);

				if (wageTypeId <= 0) {
					throw new NumberFormatException();
				}

				Long amount = wageValue == null
					? 0L
					: Long.valueOf(
						wageValue);

				if (amount < 0L) {
					throw new NumberFormatException();
				}

				result.add(
					new WagePaymentItemInput(
						wageTypeId,
						amount));

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"控除金額は0ウォン以上の整数で入力する必要があります。");
			}
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
					"未保存社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(
			result);
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
				|| !"일용직".equals(
					employee.getEmploymentType())
				|| containsSavedEmployee(
					savedEmployees,
					employeeId)
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

	private Integer parseEmployeeId(
		String value) {

		value = trim(
			value);

		try {

			if (value == null) {
				throw new NumberFormatException();
			}

			Integer employeeId = Integer.valueOf(
				value);

			if (employeeId <= 0) {
				throw new NumberFormatException();
			}

			return employeeId;

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}
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

	private boolean containsSavedEmployee(
		List<WagePaymentEmployeeRow> employees,
		Integer employeeId) {

		for (WagePaymentEmployeeRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return true;
			}
		}

		return false;
	}

	private boolean containsEmployee(
		List<EmployeeSelectRow> employees,
		Integer employeeId) {

		return findEmployee(
			employees,
			employeeId) != null;
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

			return YearMonth.parse(
				wageMonth).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		wagePeriod = trim(
			wagePeriod);

		if (wagePeriod == null) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		try {

			int wagePeriodNumber = Integer.parseInt(
				wagePeriod);

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