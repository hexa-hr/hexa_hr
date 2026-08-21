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

// 급여입력/관리(일용직) - 선택/전체 삭제 Handler
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
					"삭제 확인이 필요합니다.");
			}

			if ("all".equals(deleteMode)
				&& !"true".equals(
					req.getParameter(
						"deleteFinalConfirmed"))) {

				throw new IllegalArgumentException(
					"최종 삭제 확인이 필요합니다.");
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
				"현재 급여 목록에 존재하지 않는 사원입니다.");
		}

		if (savedEmployee != null) {

			/*
			 * 지정 사원의 해당 월·차수 WAGE만 삭제한다.
			 * DAILY_WORK는 변경하지 않는다.
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
				"추가된 사원이 없습니다.");
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
		 * 현재 일용직 작업공간의 pending 사원도
		 * 전부 제거한다.
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
					"미저장 사원 정보가 올바르지 않습니다.");
			}
		}

		return new ArrayList<>(result);
	}

	private Integer parseEmployeeId(
		String value) {

		value = trim(value);

		if (value == null) {

			throw new IllegalArgumentException(
				"올바른 사원을 선택해야 합니다.");
		}

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
			"올바른 삭제 방식을 선택해야 합니다.");
	}

	private String normalizeWageMonth(
		String wageMonth) {

		wageMonth = trim(wageMonth);

		if (wageMonth == null) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
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

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
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
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
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