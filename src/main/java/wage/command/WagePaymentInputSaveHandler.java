package wage.command;

import java.net.URLEncoder;
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
import wage.service.WagePaymentInputService;
import wage.service.WagePaymentSaveService;

// 給与入力画面 - 選択社員の給与保存Handler
public class WagePaymentInputSaveHandler
	implements CommandHandler {

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private WagePaymentSaveService wagePaymentSaveService = new WagePaymentSaveService();

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

		String employeeIdParam = trim(
			req.getParameter(
				"employeeId"));

		String wageMonth = trim(
			req.getParameter(
				"wageMonth"));

		String wagePeriod = trim(
			req.getParameter(
				"wagePeriod"));

		try {

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			if (employeeIdParam == null) {

				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			Integer employeeId;

			try {

				employeeId = Integer.valueOf(
					employeeIdParam);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(
				req);

			/*
			 * 社員の存在有無と現在の雇用形態を
			 * サーバーDBを基準に確認する。
			 */
			List<EmployeeSelectRow> employeeRows = employeeSelectService
				.getEmployeeRows(
					null,
					null,
					null);

			EmployeeSelectRow selectedEmployee = findEmployee(
				employeeRows,
				employeeId);

			if (selectedEmployee == null) {

				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			/*
			 * 現在のincomeTypeタブに属さない社員と
			 * 日雇い社員の保存を防ぐ。
			 */
			if (!isAvailableEmployeeForIncomeType(
				selectedEmployee,
				incomeType)) {

				throw new IllegalArgumentException(
					"現在の所得区分では保存できない社員です。");
			}

			/*
			 * 現在の帰属年月 + 給与回次の保存済み社員全体。
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService.getSavedEmployees(
				wageMonth,
				wagePeriod);

			/*
			 * 存在しない社員、すでに保存済みの社員、日雇いを除外した
			 * 全体のpending状態。
			 */
			List<EmployeeSelectRow> allPendingEmployees = buildPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			boolean selectedEmployeeSaved = containsSavedEmployee(
				allSavedEmployees,
				employeeId);

			boolean selectedEmployeePending = containsEmployee(
				allPendingEmployees,
				employeeId);

			if (!selectedEmployeeSaved
				&& !selectedEmployeePending) {

				throw new IllegalArgumentException(
					"現在の給与回次に登録されていない社員です。");
			}

			List<WagePaymentItemInput> currentItemInputs = parseItemInputs(
				req);

			/*
			 * 実際の保存
			 */
			wagePaymentSaveService.save(
				employeeId,
				wageMonth,
				wagePeriod,
				currentItemInputs);

			/*
			 * PRG(Post-Redirect-Get)
			 *
			 * 保存完了後に再読み込みしても
			 * 保存POSTが再実行されないように
			 * 給与入力GET画面へ移動する。
			 */
			StringBuilder redirectUrl = new StringBuilder();

			redirectUrl.append(
				req.getContextPath());

			redirectUrl.append(
				"/wage/paymentInput.do");

			redirectUrl.append(
				"?wageMonth=");

			redirectUrl.append(
				encode(wageMonth));

			redirectUrl.append(
				"&wagePeriod=");

			redirectUrl.append(
				encode(wagePeriod));

			redirectUrl.append(
				"&incomeType=");

			redirectUrl.append(
				encode(incomeType));

			redirectUrl.append(
				"&employeeId=");

			redirectUrl.append(
				employeeId);

			/*
			 * 現在保存されている社員は
			 * これ以上pendingとして渡さない。
			 *
			 * 他の未保存社員のみ維持する。
			 */
			for (EmployeeSelectRow pendingEmployee : allPendingEmployees) {

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

			/*
			 * サーバー検証に失敗。
			 *
			 * 正常なUIでは発生しないはずであり、
			 * 不正なPOSTデータは保存しない。
			 */
			res.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());

			return null;
		}
	}

	private List<WagePaymentItemInput> parseItemInputs(
		HttpServletRequest req) {

		String[] wageTypeIds = req.getParameterValues(
			"wageTypeId");

		String[] wageValues = req.getParameterValues(
			"wageValue");

		if (wageTypeIds == null
			|| wageValues == null
			|| wageTypeIds.length != wageValues.length) {

			throw new IllegalArgumentException(
				"給与項目情報が正しくありません。");
		}

		List<WagePaymentItemInput> result = new ArrayList<>();

		for (int i = 0; i < wageTypeIds.length; i++) {

			String wageTypeIdValue = trim(
				wageTypeIds[i]);

			String wageValue = trim(
				wageValues[i]);

			if (wageTypeIdValue == null) {

				throw new IllegalArgumentException(
					"給与項目情報が正しくありません。");
			}

			try {

				Integer wageTypeId = Integer.valueOf(
					wageTypeIdValue);

				Long amount = wageValue == null
					? 0L
					: Long.valueOf(
						wageValue);

				result.add(
					new WagePaymentItemInput(
						wageTypeId,
						amount));

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"給与金額は整数で入力する必要があります。");
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

			value = trim(value);

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

	private EmployeeSelectRow findEmployee(
		List<EmployeeSelectRow> employeeRows,
		Integer employeeId) {

		for (EmployeeSelectRow employee : employeeRows) {

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

		for (EmployeeSelectRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return true;
			}
		}

		return false;
	}

	private boolean isAvailableEmployeeForIncomeType(
		EmployeeSelectRow employee,
		String incomeType) {

		return isAvailableEmploymentTypeForIncomeType(
			employee.getEmploymentType(),
			incomeType);
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