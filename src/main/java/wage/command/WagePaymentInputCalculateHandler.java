package wage.command;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentItemInput;
import wage.service.WagePaymentAutoCalculationService;
import wage.service.WagePaymentInputService;
import wage.service.WagePaymentPreviousCopyService;

// 給与入力画面自動計算Handler
public class WagePaymentInputCalculateHandler
	implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wagePaymentInput.jsp";

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private WagePaymentAutoCalculationService wagePaymentAutoCalculationService = new WagePaymentAutoCalculationService();

	private WagePaymentPreviousCopyService wagePaymentPreviousCopyService = new WagePaymentPreviousCopyService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		if (!"POST".equalsIgnoreCase(req.getMethod())) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		// 同じJSPで社員選択一覧を再表示するために照会
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
			null,
			null,
			null);

		req.setAttribute(
			"employeeRows",
			employeeRows);

		String employeeIdParam = trim(req.getParameter("employeeId"));

		try {

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(req);

			String wageMonth = trim(req.getParameter("wageMonth"));

			String wagePeriod = trim(req.getParameter("wagePeriod"));

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			String settlementStartDateParam = trim(req.getParameter(
				"settlementStartDate"));

			String settlementEndDateParam = trim(req.getParameter(
				"settlementEndDate"));

			String wagePaymentDate = trim(req.getParameter(
				"wagePaymentDate"));

			// 画面の検索条件を維持
			req.setAttribute(
				"selectedEmployeeId",
				employeeIdParam);

			req.setAttribute(
				"wageMonth",
				wageMonth);

			req.setAttribute(
				"wagePeriod",
				wagePeriod);

			req.setAttribute(
				"incomeType",
				incomeType);

			List<EmployeeSelectRow> modalEmployees = filterEmployeesByIncomeType(
				employeeRows,
				incomeType);

			req.setAttribute(
				"modalEmployees",
				modalEmployees);

			req.setAttribute(
				"settlementStartDate",
				settlementStartDateParam);

			req.setAttribute(
				"settlementEndDate",
				settlementEndDateParam);

			req.setAttribute(
				"wagePaymentDate",
				wagePaymentDate);

			if (employeeIdParam == null
				|| wageMonth == null
				|| wagePeriod == null) {

				throw new IllegalArgumentException(
					"社員、帰属年月、給与回次を"
						+ "すべて入力する必要があります。");
			}

			req.setAttribute(
				"previousWageSourceOptions",
				wagePaymentPreviousCopyService.getSourceOptions(
					wageMonth,
					wagePeriod));

			Integer employeeId;

			try {

				employeeId = Integer.valueOf(
					employeeIdParam);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			EmployeeSelectRow selectedEmployee = findEmployee(
				employeeRows,
				employeeId);

			if (selectedEmployee == null) {

				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			/*
			 * 現在の帰属年月 / 給与回次の保存済み社員全体。
			 *
			 * 現在のタブ表示一覧とは別に維持する。
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			/*
			 * リクエスト全体で維持する未保存社員。
			 *
			 * 保存済み社員全体と日雇いは除外する。
			 */
			List<EmployeeSelectRow> allPendingEmployees = buildAllPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			req.setAttribute(
				"allPendingEmployees",
				allPendingEmployees);

			/*
			 * 現在の所得区分に表示する保存済み社員
			 */
			List<WagePaymentEmployeeRow> savedEmployees = filterSavedEmployeesByIncomeType(
				allSavedEmployees,
				incomeType);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			/*
			 * 現在の所得区分に表示する未保存社員
			 */
			List<EmployeeSelectRow> pendingEmployees = filterEmployeesByIncomeType(
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"pendingEmployees",
				pendingEmployees);

			/*
			 * 現在の所得区分の新規追加候補。
			 *
			 * 除外判定は現在のタブ一覧ではなく
			 * 全体のsaved / pending一覧を基準にする。
			 */
			List<EmployeeSelectRow> availableEmployees = buildAvailableEmployees(
				employeeRows,
				allSavedEmployees,
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"availableEmployees",
				availableEmployees);

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

			req.setAttribute(
				"selectedEmployeeSaved",
				selectedEmployeeSaved);

			req.setAttribute(
				"selectedEmployeePending",
				selectedEmployeePending);

			req.setAttribute(
				"selectedEmployeeName",
				selectedEmployee.getKoreanName());

			/*
			 * 既存の給与回次であるかをDBで再確認する。
			 *
			 * 既存給与の場合は、ブラウザから送信された日付ではなく
			 * DBに保存された精算期間を使用する。
			 */
			WageLedgerSummary periodSummary = wagePaymentInputService
				.getPeriodSummary(
					wageMonth,
					wagePeriod);

			Date settlementStartDate;
			Date settlementEndDate;

			if (periodSummary != null) {

				settlementStartDate = toSqlDate(
					periodSummary
						.getSettlementPeriodStartDate());

				settlementEndDate = toSqlDate(
					periodSummary
						.getSettlementPeriodEndDate());

				Date savedPaymentDate = toSqlDate(
					periodSummary
						.getWagePaymentDate());

				req.setAttribute(
					"settlementStartDate",
					toDateString(
						settlementStartDate));

				req.setAttribute(
					"settlementEndDate",
					toDateString(
						settlementEndDate));

				req.setAttribute(
					"wagePaymentDate",
					toDateString(
						savedPaymentDate));

				req.setAttribute(
					"existingPeriod",
					true);

			} else {

				settlementStartDate = parseRequiredDate(
					settlementStartDateParam,
					"精算開始日");

				settlementEndDate = parseRequiredDate(
					settlementEndDateParam,
					"精算終了日");

				if (wagePaymentDate != null) {

					parseRequiredDate(
						wagePaymentDate,
						"給与支給日");
				}

				req.setAttribute(
					"existingPeriod",
					false);
			}

			List<WagePaymentItemInput> currentItemInputs = parseItemInputs(req);

			WagePaymentAutoCalculationResult result = wagePaymentAutoCalculationService
				.calculate(
					employeeId,
					wageMonth,
					wagePeriod,
					settlementStartDate,
					settlementEndDate,
					currentItemInputs);

			req.setAttribute(
				"wageItems",
				result.getWageItems());

			req.setAttribute(
				"totalPayment",
				result.getTotalPayment());

			req.setAttribute(
				"totalDeduction",
				result.getTotalDeduction());

			req.setAttribute(
				"netPayment",
				result.getNetPayment());

			req.setAttribute(
				"autoCalculated",
				true);

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
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

			String wageTypeIdValue = trim(wageTypeIds[i]);

			String wageValue = trim(wageValues[i]);

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

	private EmployeeSelectRow findEmployee(
		List<EmployeeSelectRow> employeeRows,
		Integer employeeId) {

		for (EmployeeSelectRow employeeRow : employeeRows) {

			if (employeeId.equals(
				employeeRow.getEmployeeId())) {

				return employeeRow;
			}
		}

		return null;
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
					"pending社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(result);
	}

	private List<EmployeeSelectRow> buildAllPendingEmployees(
		List<EmployeeSelectRow> employeeRows,
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<Integer> pendingEmployeeIds) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (Integer employeeId : pendingEmployeeIds) {

			if (containsSavedEmployee(
				allSavedEmployees,
				employeeId)) {

				continue;
			}

			EmployeeSelectRow employee = findEmployee(
				employeeRows,
				employeeId);

			if (employee == null) {
				continue;
			}

			/*
			 * 日雇給与は別画面で処理するため
			 * 全体のpending状態にも含めない。
			 */
			if ("일용직".equals(
				employee.getEmploymentType())) {

				continue;
			}

			if (!containsEmployee(
				result,
				employeeId)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<WagePaymentEmployeeRow> filterSavedEmployeesByIncomeType(
		List<WagePaymentEmployeeRow> allSavedEmployees,
		String incomeType) {

		List<WagePaymentEmployeeRow> result = new ArrayList<>();

		for (WagePaymentEmployeeRow employee : allSavedEmployees) {

			if (isAvailableEmploymentTypeForIncomeType(
				employee.getEmploymentType(),
				incomeType)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> filterEmployeesByIncomeType(
		List<EmployeeSelectRow> employees,
		String incomeType) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employees) {

			if (isAvailableEmployeeForIncomeType(
				employee,
				incomeType)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> buildAvailableEmployees(
		List<EmployeeSelectRow> employeeRows,
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<EmployeeSelectRow> allPendingEmployees,
		String incomeType) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employeeRows) {

			if (!isAvailableEmployeeForIncomeType(
				employee,
				incomeType)) {

				continue;
			}

			Integer employeeId = employee.getEmployeeId();

			if (containsSavedEmployee(
				allSavedEmployees,
				employeeId)
				|| containsEmployee(
					allPendingEmployees,
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

	private boolean isAvailableEmployeeForIncomeType(
		EmployeeSelectRow employee,
		String incomeType) {

		return isAvailableEmploymentTypeForIncomeType(
			employee.getEmploymentType(),
			incomeType);
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

	private Date parseRequiredDate(
		String value,
		String fieldName) {

		if (value == null) {

			throw new IllegalArgumentException(
				fieldName + "を入力する必要があります。");
		}

		try {

			return Date.valueOf(value);

		} catch (IllegalArgumentException e) {

			throw new IllegalArgumentException(
				fieldName
					+ "はYYYY-MM-DD形式である必要があります。");
		}
	}

	private Date toSqlDate(
		java.util.Date value) {

		if (value == null) {
			return null;
		}

		return new Date(
			value.getTime());
	}

	private String toDateString(
		Date value) {

		return value == null
			? null
			: value.toString();
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