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

// 급여입력 화면 자동계산 Handler
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

		// 같은 JSP에서 사원 선택 목록을 다시 표시하기 위해 조회
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

			// 화면의 검색조건 유지
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
					"사원, 귀속연월, 급여차수를 "
						+ "모두 입력해야 합니다.");
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
					"올바른 사원을 선택해야 합니다.");
			}

			EmployeeSelectRow selectedEmployee = findEmployee(
				employeeRows,
				employeeId);

			if (selectedEmployee == null) {

				throw new IllegalArgumentException(
					"올바른 사원을 선택해야 합니다.");
			}

			/*
			 * 현재 귀속연월/급여차수의 전체 저장 사원.
			 *
			 * 현재 탭 표시 목록과 별도로 유지한다.
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			/*
			 * 요청 전체에서 유지할 pending 사원.
			 *
			 * 전체 저장 사원과 일용직은 제외한다.
			 */
			List<EmployeeSelectRow> allPendingEmployees = buildAllPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			req.setAttribute(
				"allPendingEmployees",
				allPendingEmployees);

			/*
			 * 현재 소득구분에 표시할 저장 사원
			 */
			List<WagePaymentEmployeeRow> savedEmployees = filterSavedEmployeesByIncomeType(
				allSavedEmployees,
				incomeType);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			/*
			 * 현재 소득구분에 표시할 pending 사원
			 */
			List<EmployeeSelectRow> pendingEmployees = filterEmployeesByIncomeType(
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"pendingEmployees",
				pendingEmployees);

			/*
			 * 현재 소득구분의 신규추가 후보.
			 *
			 * 제외 판단은 현재 탭 목록이 아니라
			 * 전체 저장/pending 목록을 기준으로 한다.
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
					"현재 급여차수에 등록되지 않은 사원입니다.");
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
			 * 기존 급여차수인지 다시 DB에서 확인한다.
			 *
			 * 기존 급여라면 브라우저가 보낸 날짜 대신
			 * DB에 저장된 정산기간을 사용한다.
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
					"정산 시작일");

				settlementEndDate = parseRequiredDate(
					settlementEndDateParam,
					"정산 종료일");

				if (wagePaymentDate != null) {

					parseRequiredDate(
						wagePaymentDate,
						"급여 지급일");
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
				"급여항목 정보가 올바르지 않습니다.");
		}

		List<WagePaymentItemInput> result = new ArrayList<>();

		for (int i = 0; i < wageTypeIds.length; i++) {

			String wageTypeIdValue = trim(wageTypeIds[i]);

			String wageValue = trim(wageValues[i]);

			if (wageTypeIdValue == null) {

				throw new IllegalArgumentException(
					"급여항목 정보가 올바르지 않습니다.");
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
					"급여금액은 정수로 입력해야 합니다.");
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
					"pending 사원 정보가 올바르지 않습니다.");
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
			 * 일용직 급여는 별도 화면에서 처리하므로
			 * 전체 pending 상태에도 포함하지 않는다.
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
			"올바른 소득구분을 선택해야 합니다.");
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
				fieldName + "을 입력해야 합니다.");
		}

		try {

			return Date.valueOf(value);

		} catch (IllegalArgumentException e) {

			throw new IllegalArgumentException(
				fieldName
					+ "은 YYYY-MM-DD 형식이어야 합니다.");
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