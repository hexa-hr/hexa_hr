package wage.command;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.model.DailyWorkPayrollResult;
import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentPeriodDefault;
import wage.service.DailyWagePaymentInputService;
import wage.service.WagePaymentInputService;

// 給与入力・管理（日雇い）画面照会Handler
public class DailyWagePaymentInputHandler implements CommandHandler {

	private static final String INTERNAL_CALCULATION_ATTRIBUTE = DailyWagePaymentInputHandler.class.getName()
		+ ".internalCalculation";

	private static final String CALCULATION_RESULT_ATTRIBUTE = DailyWagePaymentInputHandler.class.getName()
		+ ".calculationResult";

	private static final Object INTERNAL_CALCULATION_TOKEN = new Object();

	static void prepareInternalCalculationRender(
		HttpServletRequest req,
		WagePaymentAutoCalculationResult result) {

		req.setAttribute(
			INTERNAL_CALCULATION_ATTRIBUTE,
			INTERNAL_CALCULATION_TOKEN);

		if (result == null) {
			req.removeAttribute(CALCULATION_RESULT_ATTRIBUTE);
		} else {
			req.setAttribute(
				CALCULATION_RESULT_ATTRIBUTE,
				result);
		}
	}

	private static final String FORM_VIEW = "/WEB-INF/view/wage/dailyWagePaymentInput.jsp";

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private DailyWagePaymentInputService dailyWagePaymentInputService = new DailyWagePaymentInputService();

	// 月・回次共通の日付照会用
	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		boolean getRequest = "GET".equalsIgnoreCase(req.getMethod());

		boolean internalCalculationPost = "POST".equalsIgnoreCase(req.getMethod())
			&& req.getAttribute(
				INTERNAL_CALCULATION_ATTRIBUTE) == INTERNAL_CALCULATION_TOKEN;

		req.setAttribute(
			"calculationAttempted",
			internalCalculationPost);

		if (!getRequest && !internalCalculationPost) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		WagePaymentAutoCalculationResult internalCalculationResult = null;

		if (internalCalculationPost) {

			req.removeAttribute(
				INTERNAL_CALCULATION_ATTRIBUTE);

			Object value = req.getAttribute(
				CALCULATION_RESULT_ATTRIBUTE);

			req.removeAttribute(
				CALCULATION_RESULT_ATTRIBUTE);

			if (value != null) {

				if (!(value instanceof WagePaymentAutoCalculationResult)) {

					throw new IllegalStateException(
						"日雇給与の自動計算結果が正しくありません。");
				}

				internalCalculationResult = (WagePaymentAutoCalculationResult)value;
			}
		}

		String wageMonth = normalizeWageMonth(
			req.getParameter("wageMonth"));

		String wagePeriod = normalizeWagePeriod(
			req.getParameter("wagePeriod"));

		String employeeIdParam = trim(
			req.getParameter("employeeId"));

		req.setAttribute(
			"wageMonth",
			wageMonth);

		req.setAttribute(
			"wagePeriod",
			wagePeriod);

		try {

			List<EmployeeSelectRow> allEmployeeRows = employeeSelectService.getEmployeeRows(
				null,
				null,
				null);

			/*
			 * 保存済み・pendingの状態に関係なく
			 * すべての日雇い社員をモーダルに表示する。
			 */
			List<EmployeeSelectRow> dailyEmployees = filterDailyEmployees(
				allEmployeeRows);

			req.setAttribute(
				"modalEmployees",
				dailyEmployees);

			List<WagePaymentEmployeeRow> savedEmployees = dailyWagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			List<Integer> pendingEmployeeIds = parseEmployeeIds(
				req,
				"pendingEmployeeId",
				"未保存社員情報が正しくありません。");

			List<Integer> addEmployeeIds;

			if (internalCalculationPost) {

				addEmployeeIds = new ArrayList<>();

			} else {

				addEmployeeIds = parseEmployeeIds(
					req,
					"addEmployeeId",
					"追加する社員情報が正しくありません。");
			}

			/*
			 * モーダルから受け取った社員が実際に日雇い社員であるか
			 * 先にすべて検証する。
			 */
			for (Integer addEmployeeId : addEmployeeIds) {

				EmployeeSelectRow addEmployee = findEmployee(
					dailyEmployees,
					addEmployeeId);

				if (addEmployee == null) {

					throw new IllegalArgumentException(
						"日雇い社員のみ追加できます。");
				}
			}

			Integer firstAddedEmployeeId = null;

			for (Integer addEmployeeId : addEmployeeIds) {

				/*
				 * すでに保存済み、またはpendingの社員は
				 * 別途処理せずにスキップする。
				 */
				if (findSavedEmployee(
					savedEmployees,
					addEmployeeId) != null
					|| pendingEmployeeIds.contains(
						addEmployeeId)) {

					continue;
				}

				pendingEmployeeIds.add(
					addEmployeeId);

				if (firstAddedEmployeeId == null) {

					firstAddedEmployeeId = addEmployeeId;
				}
			}

			/*
			 * 実際に新しく追加された最初の社員を選択する。
			 * すべて既存の社員であれば、現在の選択を維持する。
			 */
			if (firstAddedEmployeeId != null) {

				employeeIdParam = String.valueOf(
					firstAddedEmployeeId);
			}

			List<EmployeeSelectRow> pendingEmployees = buildPendingEmployees(
				dailyEmployees,
				savedEmployees,
				pendingEmployeeIds);

			req.setAttribute(
				"allPendingEmployees",
				pendingEmployees);

			req.setAttribute(
				"pendingEmployees",
				pendingEmployees);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			req.setAttribute(
				"visibleEmployeeCount",
				savedEmployees.size()
					+ pendingEmployees.size());

			setMonthlySummaryAttributes(
				req,
				savedEmployees);

			/*
			 * 同じ月・回次のWAGEが存在する場合は保存済みの日付を、
			 * 存在しない場合は会社の給与設定の基本日付を使用する。
			 */
			WageLedgerSummary periodSummary = wagePaymentInputService.getPeriodSummary(
				wageMonth,
				wagePeriod);

			Date settlementStartDate;
			Date settlementEndDate;
			Date wagePaymentDate;

			if (periodSummary != null) {

				settlementStartDate = toSqlDate(
					periodSummary
						.getSettlementPeriodStartDate());

				settlementEndDate = toSqlDate(
					periodSummary
						.getSettlementPeriodEndDate());

				wagePaymentDate = toSqlDate(
					periodSummary
						.getWagePaymentDate());

				req.setAttribute(
					"existingPeriod",
					true);

			} else {

				WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
					wageMonth);

				settlementStartDate = defaultPeriod.getSettlementStartDate();

				settlementEndDate = defaultPeriod.getSettlementEndDate();

				wagePaymentDate = defaultPeriod.getWagePaymentDate();

				req.setAttribute(
					"existingPeriod",
					false);
			}

			req.setAttribute(
				"settlementStartDate",
				toDateString(settlementStartDate));

			req.setAttribute(
				"settlementEndDate",
				toDateString(settlementEndDate));

			req.setAttribute(
				"wagePaymentDate",
				toDateString(wagePaymentDate));

			/*
			 * 社員未選択の状態でも現在の月・回次の
			 * 控除項目の枠を表示する。
			 */
			List<WagePaymentInputViewItem> deductionItems = dailyWagePaymentInputService
				.getDeductionViewItems(
					null,
					wageMonth,
					wagePeriod,
					null,
					null);

			req.setAttribute(
				"deductionItems",
				deductionItems);

			req.setAttribute(
				"selectedEmployeeSaved",
				false);

			req.setAttribute(
				"selectedEmployeePending",
				false);

			req.setAttribute(
				"wageInputEnabled",
				false);

			req.setAttribute(
				"currentTotalPayment",
				0L);

			req.setAttribute(
				"currentTotalDeduction",
				0L);

			req.setAttribute(
				"currentNetPayment",
				0L);

			if (employeeIdParam != null) {

				Integer employeeId = parseEmployeeId(
					employeeIdParam);

				EmployeeSelectRow selectedEmployee = findEmployee(
					dailyEmployees,
					employeeId);

				if (selectedEmployee == null) {

					throw new IllegalArgumentException(
						"正しい日雇い社員を選択する必要があります。");
				}

				WagePaymentEmployeeRow savedEmployee = findSavedEmployee(
					savedEmployees,
					employeeId);

				boolean selectedEmployeeSaved = savedEmployee != null;

				boolean selectedEmployeePending = containsEmployee(
					pendingEmployees,
					employeeId);

				if (!selectedEmployeeSaved
					&& !selectedEmployeePending) {

					throw new IllegalArgumentException(
						"現在の給与回次に登録されていない社員です。");
				}

				DailyWorkPayrollResult workResult = dailyWagePaymentInputService
					.getWorkResult(
						employeeId,
						settlementStartDate,
						settlementEndDate);

				deductionItems = dailyWagePaymentInputService
					.getDeductionViewItems(
						employeeId,
						wageMonth,
						wagePeriod,
						settlementStartDate,
						settlementEndDate);

				long currentTotalPayment;
				long currentTotalDeduction;
				long currentNetPayment;

				if (selectedEmployeeSaved) {

					/*
					 * 保存済み社員の合計は
					 * WAGEに保存された値を使用する。
					 */
					currentTotalPayment = safe(
						savedEmployee.getTotalPayment());

					currentTotalDeduction = safe(
						savedEmployee.getTotalDeduction());

					currentNetPayment = safe(
						savedEmployee.getNetPayment());

				} else {

					currentTotalPayment = safe(
						workResult.getTotalPayment());

					currentTotalDeduction = sumDeductionItems(
						deductionItems);

					currentNetPayment = currentTotalPayment
						- currentTotalDeduction;
				}

				if (internalCalculationResult != null) {

					if (internalCalculationResult.getWageItems() == null
						|| internalCalculationResult.getTotalPayment() == null
						|| internalCalculationResult.getTotalDeduction() == null
						|| internalCalculationResult.getNetPayment() == null) {

						throw new IllegalStateException(
							"日雇給与の自動計算結果が正しくありません。");
					}

					deductionItems = internalCalculationResult.getWageItems();

					currentTotalPayment = internalCalculationResult.getTotalPayment();

					currentTotalDeduction = internalCalculationResult.getTotalDeduction();

					currentNetPayment = internalCalculationResult.getNetPayment();

					req.setAttribute(
						"autoCalculated",
						true);
				}

				req.setAttribute(
					"selectedEmployeeId",
					employeeId);

				req.setAttribute(
					"selectedEmployeeName",
					selectedEmployee.getKoreanName());

				req.setAttribute(
					"selectedEmployeeSaved",
					selectedEmployeeSaved);

				req.setAttribute(
					"selectedEmployeePending",
					selectedEmployeePending);

				req.setAttribute(
					"wageInputEnabled",
					true);

				req.setAttribute(
					"workResult",
					workResult);

				req.setAttribute(
					"deductionItems",
					deductionItems);

				req.setAttribute(
					"currentTotalPayment",
					currentTotalPayment);

				req.setAttribute(
					"currentTotalDeduction",
					currentTotalDeduction);

				req.setAttribute(
					"currentNetPayment",
					currentNetPayment);
			}

			if (!internalCalculationPost
				&& "true".equals(
					req.getParameter("saved"))) {

				req.setAttribute(
					"successMessage",
					"給与が保存されました。");
			}

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}

	private List<EmployeeSelectRow> filterDailyEmployees(
		List<EmployeeSelectRow> employeeRows) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employeeRows) {

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

			if (findSavedEmployee(
				savedEmployees,
				employeeId) != null) {

				continue;
			}

			EmployeeSelectRow employee = findEmployee(
				dailyEmployees,
				employeeId);

			if (employee == null) {
				continue;
			}

			if (!containsEmployee(
				result,
				employeeId)) {

				result.add(employee);
			}
		}

		return result;
	}

	private List<Integer> parseEmployeeIds(
		HttpServletRequest req,
		String parameterName,
		String errorMessage) {

		String[] values = req.getParameterValues(
			parameterName);

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
					errorMessage);
			}
		}

		return new ArrayList<>(result);
	}

	private Integer parseEmployeeId(
		String value) {

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

	private boolean containsEmployee(
		List<EmployeeSelectRow> employees,
		Integer employeeId) {

		return findEmployee(
			employees,
			employeeId) != null;
	}

	private long sumDeductionItems(
		List<WagePaymentInputViewItem> items) {

		long result = 0L;

		for (WagePaymentInputViewItem item : items) {

			if ("D".equals(item.getItemType())) {

				result += safe(
					item.getWageValue());
			}
		}

		return result;
	}

	private void setMonthlySummaryAttributes(
		HttpServletRequest req,
		List<WagePaymentEmployeeRow> savedEmployees) {

		long totalPayment = 0L;
		long totalDeduction = 0L;
		long netPayment = 0L;

		for (WagePaymentEmployeeRow employee : savedEmployees) {

			totalPayment += safe(
				employee.getTotalPayment());

			totalDeduction += safe(
				employee.getTotalDeduction());

			netPayment += safe(
				employee.getNetPayment());
		}

		req.setAttribute(
			"monthlyEmployeeCount",
			savedEmployees.size());

		req.setAttribute(
			"monthlyTotalPayment",
			totalPayment);

		req.setAttribute(
			"monthlyTotalDeduction",
			totalDeduction);

		req.setAttribute(
			"monthlyNetPayment",
			netPayment);
	}

	private long safe(
		Long value) {

		return value == null
			? 0L
			: value;
	}

	private String normalizeWageMonth(
		String wageMonth) {

		wageMonth = trim(wageMonth);

		if (wageMonth == null) {
			return YearMonth.now().toString();
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
			return "1";
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