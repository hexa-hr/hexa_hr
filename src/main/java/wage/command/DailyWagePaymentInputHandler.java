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

// 급여입력/관리(일용직) 화면 조회 Handler
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

	// 월·차수 공통 날짜 조회용
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
						"일용직 급여 자동계산 결과가 올바르지 않습니다.");
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
			 * 저장·pending 여부와 관계없이
			 * 모든 일용직 사원을 모달에 표시한다.
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
				"미저장 사원 정보가 올바르지 않습니다.");

			List<Integer> addEmployeeIds;

			if (internalCalculationPost) {

				addEmployeeIds = new ArrayList<>();

			} else {

				addEmployeeIds = parseEmployeeIds(
					req,
					"addEmployeeId",
					"추가할 사원 정보가 올바르지 않습니다.");
			}

			/*
			 * 모달에서 받은 사원이 실제 일용직인지
			 * 먼저 전부 검증한다.
			 */
			for (Integer addEmployeeId : addEmployeeIds) {

				EmployeeSelectRow addEmployee = findEmployee(
					dailyEmployees,
					addEmployeeId);

				if (addEmployee == null) {

					throw new IllegalArgumentException(
						"일용직 사원만 추가할 수 있습니다.");
				}
			}

			Integer firstAddedEmployeeId = null;

			for (Integer addEmployeeId : addEmployeeIds) {

				/*
				 * 이미 저장됐거나 pending인 사원은
				 * 별도 반응 없이 건너뛴다.
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
			 * 실제 새로 추가된 첫 번째 사원을 선택한다.
			 * 전부 기존 사원이면 기존 선택을 유지한다.
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
			 * 같은 월·차수의 WAGE가 존재하면 저장된 날짜,
			 * 없으면 회사 급여설정의 기본 날짜를 사용한다.
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
			 * 사원 미선택 상태에서도 현재 월·차수의
			 * 공제항목 틀을 표시한다.
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
						"올바른 일용직 사원을 선택해야 합니다.");
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
						"현재 급여차수에 등록되지 않은 사원입니다.");
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
					 * 저장 사원의 합계는
					 * WAGE에 저장된 값을 사용한다.
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
							"일용직 급여 자동계산 결과가 올바르지 않습니다.");
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
					"급여가 저장되었습니다.");
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
				"올바른 사원을 선택해야 합니다.");
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
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
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
				"급여차수는 1 이상 10 이하의 숫자여야 창합니다.");
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