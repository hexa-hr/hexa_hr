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

// 급여입력 화면 - 선택 사원 급여 저장 Handler
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
					"올바른 사원을 선택해야 합니다.");
			}

			Integer employeeId;

			try {

				employeeId = Integer.valueOf(
					employeeIdParam);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"올바른 사원을 선택해야 합니다.");
			}

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(
				req);

			/*
			 * 사원 존재 여부와 현재 고용형태를
			 * 서버 DB 기준으로 확인한다.
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
					"올바른 사원을 선택해야 합니다.");
			}

			/*
			 * 현재 incomeType 탭에 속하지 않는 사원과
			 * 일용직 사원의 저장을 막는다.
			 */
			if (!isAvailableEmployeeForIncomeType(
				selectedEmployee,
				incomeType)) {

				throw new IllegalArgumentException(
					"현재 소득구분에서 저장할 수 없는 사원입니다.");
			}

			/*
			 * 현재 귀속연월 + 급여차수의 전체 저장 사원.
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService.getSavedEmployees(
				wageMonth,
				wagePeriod);

			/*
			 * 미존재 사원, 이미 저장된 사원, 일용직을 제거한
			 * 전체 pending 상태.
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
					"현재 급여차수에 등록되지 않은 사원입니다.");
			}

			List<WagePaymentItemInput> currentItemInputs = parseItemInputs(
				req);

			/*
			 * 실제 저장
			 */
			wagePaymentSaveService.save(
				employeeId,
				wageMonth,
				wagePeriod,
				currentItemInputs);

			/*
			 * PRG(Post-Redirect-Get)
			 *
			 * 저장 완료 후 새로고침해도
			 * 저장 POST가 다시 실행되지 않도록
			 * 급여입력 GET 화면으로 이동한다.
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
			 * 현재 저장된 사원은
			 * 더 이상 pending으로 전달하지 않는다.
			 *
			 * 다른 미저장 사원만 유지한다.
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
			 * 서버 검증 실패.
			 *
			 * 정상 UI에서는 발생하지 않아야 하며,
			 * 잘못된 POST 데이터의 저장은 수행하지 않는다.
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
				"급여항목 정보가 올바르지 않습니다.");
		}

		List<WagePaymentItemInput> result = new ArrayList<>();

		for (int i = 0; i < wageTypeIds.length; i++) {

			String wageTypeIdValue = trim(
				wageTypeIds[i]);

			String wageValue = trim(
				wageValues[i]);

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
					"pending 사원 정보가 올바르지 않습니다.");
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