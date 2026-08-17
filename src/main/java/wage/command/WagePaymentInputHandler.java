package wage.command;

import java.sql.Date;
import java.util.List;

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

		// 사원 선택 목록
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
			null,
			null,
			null);

		req.setAttribute(
			"employeeRows",
			employeeRows);

		String employeeIdParam = trim(req.getParameter("employeeId"));

		String wageMonth = trim(req.getParameter("wageMonth"));

		String wagePeriod = trim(req.getParameter("wagePeriod"));

		String settlementStartDateParam = trim(req.getParameter(
			"settlementStartDate"));

		String settlementEndDateParam = trim(req.getParameter(
			"settlementEndDate"));

		String wagePaymentDate = trim(req.getParameter(
			"wagePaymentDate"));

		boolean searchRequested = "true".equals(
			req.getParameter("search"));

		// 입력값을 화면에 다시 유지
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
			"settlementStartDate",
			settlementStartDateParam);

		req.setAttribute(
			"settlementEndDate",
			settlementEndDateParam);

		req.setAttribute(
			"wagePaymentDate",
			wagePaymentDate);

		/*
		 * 최초 진입
		 * 아직 사원을 선택하지 않았다면
		 * 사원목록만 전달하고 화면 표시
		 */
		if (!searchRequested
			&& employeeIdParam == null) {

			return FORM_VIEW;
		}

		if (employeeIdParam == null
			|| wageMonth == null
			|| wagePeriod == null) {

			req.setAttribute(
				"errorMessage",
				"사원, 귀속연월, 급여차수를 "
					+ "모두 입력해야 합니다.");

			return FORM_VIEW;
		}

		try {

			Integer employeeId = Integer.valueOf(
				employeeIdParam);

			// 실제 존재하는 사원인지 확인
			EmployeeSelectRow selectedEmployee = findEmployee(
				employeeRows,
				employeeId);

			if (selectedEmployee == null) {

				throw new IllegalArgumentException(
					"올바른 사원을 선택해야 합니다.");
			}

			req.setAttribute(
				"selectedEmployeeName",
				selectedEmployee
					.getKoreanName());

			List<WagePaymentEmployeeRow> savedEmployees = wagePaymentInputService.getSavedEmployees(
				wageMonth,
				wagePeriod);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			/*
			 * 같은 귀속연월·급여차수의
			 * 저장된 급여차수 정보 조회
			 */
			WageLedgerSummary periodSummary = wagePaymentInputService
				.getPeriodSummary(
					wageMonth,
					wagePeriod);

			Date settlementStartDate;
			Date settlementEndDate;

			if (periodSummary != null) {

				/*
				 * 기존 급여차수
				 *
				 * 사용자가 전달한 날짜가 아니라
				 * DB에 저장된 정산기간을 사용한다.
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
				 *
				 * 회사의 급여지급정보 설정을 기준으로
				 * 정산기간과 급여지급일 기본값을 생성한다.
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

			List<WagePaymentInputViewItem> wageItems = wagePaymentInputService
				.getViewItems(
					employeeId,
					wageMonth,
					wagePeriod,
					settlementStartDate,
					settlementEndDate);

			req.setAttribute(
				"wageItems",
				wageItems);

		} catch (NumberFormatException e) {

			req.setAttribute(
				"errorMessage",
				"올바른 사원을 선택해야 합니다.");

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
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