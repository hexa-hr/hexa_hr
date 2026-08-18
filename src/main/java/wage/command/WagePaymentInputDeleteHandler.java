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

// 급여입력 화면 - 선택 사원 삭제 Handler
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

			boolean deleteConfirmed = "true".equals(
				req.getParameter(
					"deleteConfirmed"));

			if (!deleteConfirmed) {

				throw new IllegalArgumentException(
					"삭제 확인이 필요합니다.");
			}

			Integer employeeId = parseEmployeeId(
				req.getParameter(
					"employeeId"));

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

			WagePaymentEmployeeRow selectedSavedEmployee = findSavedEmployee(
				allSavedEmployees,
				employeeId);

			EmployeeSelectRow selectedPendingEmployee = findEmployee(
				allPendingEmployees,
				employeeId);

			if (selectedSavedEmployee == null
				&& selectedPendingEmployee == null) {

				throw new IllegalArgumentException(
					"현재 급여 목록에 존재하지 않는 사원입니다.");
			}

			String employmentType = selectedSavedEmployee != null
				? selectedSavedEmployee.getEmploymentType()
				: selectedPendingEmployee.getEmploymentType();

			if (!isAvailableEmploymentTypeForIncomeType(
				employmentType,
				incomeType)) {

				throw new IllegalArgumentException(
					"현재 소득구분에서 삭제할 수 없는 사원입니다.");
			}

			/*
			 * 저장된 사원은 DB 급여 snapshot 전체를 삭제한다.
			 *
			 * 미저장 사원은 DB를 변경하지 않고
			 * pending 목록에서만 제거한다.
			 */
			if (selectedSavedEmployee != null) {

				wagePaymentDeleteService.delete(
					employeeId,
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

			/*
			 * 선택삭제 대상만 pending에서 제거하고
			 * 나머지 미저장 사원은 현재 요청에서 유지한다.
			 */
			for (EmployeeSelectRow pendingEmployee : allPendingEmployees) {

				Integer pendingEmployeeId = pendingEmployee
					.getEmployeeId();

				if (employeeId.equals(
					pendingEmployeeId)) {

					continue;
				}

				redirectUrl.append(
					"&pendingEmployeeId=");

				redirectUrl.append(
					pendingEmployeeId);
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

	private Integer parseEmployeeId(
		String employeeIdValue) {

		employeeIdValue = trim(
			employeeIdValue);

		if (employeeIdValue == null) {

			throw new IllegalArgumentException(
				"올바른 사원을 선택해야 합니다.");
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
				"올바른 사원을 선택해야 합니다.");
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
					"pending 사원 정보가 올바르지 않습니다.");
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
				"귀속연월을 입력해야 합니다.");
		}

		try {

			YearMonth.parse(
				wageMonth);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		return wageMonth;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		wagePeriod = trim(
			wagePeriod);

		if (wagePeriod == null) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
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
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
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
			"올바른 소득구분을 선택해야 합니다.");
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