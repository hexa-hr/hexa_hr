package wage.command;

import java.sql.Date;
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
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentPeriodDefault;
import wage.service.WagePaymentInputService;

// 급여입력 화면 조회 Handler
public class WagePaymentInputHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wagePaymentInput.jsp";

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		if (!"GET".equalsIgnoreCase(req.getMethod())) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		/*
		 * 신규추가 기능에서 사용할 사원 선택 목록.
		 * 현재 단계에서는 JSP에서 직접 사용하지 않지만
		 * 다음 단계 연결을 위해 유지한다.
		 */
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
			null,
			null,
			null);

		req.setAttribute(
			"employeeRows",
			employeeRows);

		String employeeIdParam = trim(req.getParameter("employeeId"));

		String addEmployeeIdParam = trim(req.getParameter("addEmployeeId"));

		String wageMonth = trim(req.getParameter("wageMonth"));

		String wagePeriod = trim(req.getParameter("wagePeriod"));

		try {

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(
				req);

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			req.setAttribute(
				"incomeType",
				incomeType);

			if ("true".equals(
				req.getParameter("saved"))) {

				req.setAttribute(
					"successMessage",
					"급여가 저장되었습니다.");
			}

			/*
			 * 최초 진입
			 * → 현재 귀속연월 / 1차를 기본 작업공간으로 사용
			 */
			if (wageMonth == null) {

				wageMonth = YearMonth.now().toString();
			}

			if (wagePeriod == null) {

				wagePeriod = "1";
			}

			req.setAttribute(
				"selectedEmployeeId",
				employeeIdParam);

			req.setAttribute(
				"wageMonth",
				wageMonth);

			req.setAttribute(
				"wagePeriod",
				wagePeriod);

			/*
			 * 현재 귀속연월 + 급여차수에
			 * 실제 저장된 사원 목록
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			/*
			 * 신규추가로 전달된 사원을
			 * 현재 요청 흐름의 pending 사원으로 추가한다.
			 *
			 * wage에는 아직 저장하지 않는다.
			 */
			if (addEmployeeIdParam != null) {

				Integer addEmployeeId;

				try {

					addEmployeeId = Integer.valueOf(
						addEmployeeIdParam);

				} catch (NumberFormatException e) {

					throw new IllegalArgumentException(
						"올바른 사원을 선택해야 합니다.");
				}

				EmployeeSelectRow addEmployee = findEmployee(
					employeeRows,
					addEmployeeId);

				if (addEmployee == null) {

					throw new IllegalArgumentException(
						"올바른 사원을 선택해야 합니다.");
				}

				if (!isAvailableEmployeeForIncomeType(
					addEmployee,
					incomeType)) {

					throw new IllegalArgumentException(
						"현재 소득구분에서 추가할 수 없는 사원입니다.");
				}

				if (!containsSavedEmployee(
					allSavedEmployees,
					addEmployeeId)
					&& !pendingEmployeeIds.contains(
						addEmployeeId)) {

					pendingEmployeeIds.add(
						addEmployeeId);
				}

				/*
				 * 신규추가한 사원을 바로 선택한다.
				 */
				employeeIdParam = String.valueOf(
					addEmployeeId);

				req.setAttribute(
					"selectedEmployeeId",
					employeeIdParam);
			}

			/*
			 * 요청 전체에서 유지할 미저장 사원.
			 *
			 * 소득구분 탭과 관계없이 유지하되
			 * 저장 사원과 일용직은 제외한다.
			 */
			List<EmployeeSelectRow> allPendingEmployees = buildAllPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			req.setAttribute(
				"allPendingEmployees",
				allPendingEmployees);

			/*
			 * 현재 소득구분 탭에 표시할 저장 사원
			 */
			List<WagePaymentEmployeeRow> savedEmployees = filterSavedEmployeesByIncomeType(
				allSavedEmployees,
				incomeType);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			/*
			 * 현재 소득구분 탭에 표시할 미저장 사원
			 */
			List<EmployeeSelectRow> pendingEmployees = filterPendingEmployeesByIncomeType(
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"pendingEmployees",
				pendingEmployees);

			/*
			 * 신규추가 후보는 현재 탭 사원 중
			 * 전체 saved / pending 어디에도 없는 사원만 사용한다.
			 */
			List<EmployeeSelectRow> availableEmployees = buildAvailableEmployees(
				employeeRows,
				allSavedEmployees,
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"availableEmployees",
				availableEmployees);

			/*
			 * 현재 귀속연월 + 급여차수의
			 * 저장된 기본정보 확인
			 */
			WageLedgerSummary periodSummary = wagePaymentInputService.getPeriodSummary(
				wageMonth,
				wagePeriod);

			Date settlementStartDate;
			Date settlementEndDate;

			if (periodSummary != null) {

				/*
				 * 기존 급여차수
				 * → wage에 저장된 날짜 사용
				 */
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

				/*
				 * 신규 급여차수
				 * → 회사 급여지급정보의 기본 날짜 사용
				 */
				WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
					wageMonth);

				settlementStartDate = defaultPeriod.getSettlementStartDate();

				settlementEndDate = defaultPeriod.getSettlementEndDate();

				Date defaultPaymentDate = defaultPeriod.getWagePaymentDate();

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
						defaultPaymentDate));

				req.setAttribute(
					"existingPeriod",
					false);
			}

			/*
			 * 사원이 선택된 경우에만
			 * 오른쪽 급여입력 항목을 조회한다.
			 *
			 * employeeId는 작업공간의 필수키가 아니다.
			 */
			if (employeeIdParam != null) {

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

				List<WagePaymentInputViewItem> wageItems = wagePaymentInputService.getViewItems(
					employeeId,
					wageMonth,
					wagePeriod,
					settlementStartDate,
					settlementEndDate);

				req.setAttribute(
					"wageItems",
					wageItems);
			}

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}

	// --- Helper 메서드 ---

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

	private List<EmployeeSelectRow> filterPendingEmployeesByIncomeType(
		List<EmployeeSelectRow> allPendingEmployees,
		String incomeType) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : allPendingEmployees) {

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