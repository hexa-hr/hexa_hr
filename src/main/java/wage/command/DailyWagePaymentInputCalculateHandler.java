package wage.command;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentItemInput;
import wage.model.WagePaymentPeriodDefault;
import wage.service.DailyWagePaymentInputService;
import wage.service.WagePaymentInputService;

// 급여입력/관리(일용직) 자동계산 Handler
public class DailyWagePaymentInputCalculateHandler
	implements CommandHandler {

	private DailyWagePaymentInputService dailyWagePaymentInputService = new DailyWagePaymentInputService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private DailyWagePaymentInputHandler dailyWagePaymentInputHandler = new DailyWagePaymentInputHandler();

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

		String wageMonth;
		String wagePeriod;

		/*
		 * 월·차수 자체가 잘못된 요청은
		 * 화면을 다시 만들 수 없으므로 400으로 처리한다.
		 */
		try {

			wageMonth = normalizeWageMonth(
				req.getParameter("wageMonth"));

			wagePeriod = normalizeWagePeriod(
				req.getParameter("wagePeriod"));

		} catch (IllegalArgumentException e) {

			res.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());

			return null;
		}

		WagePaymentAutoCalculationResult calculationResult = null;

		try {

			Integer employeeId = parseEmployeeId(
				req.getParameter("employeeId"));

			Date[] settlementPeriod = resolveSettlementPeriod(
				wageMonth,
				wagePeriod);

			List<WagePaymentItemInput> deductionInputs = parseDeductionInputs(req);

			calculationResult = dailyWagePaymentInputService.calculate(
				employeeId,
				wageMonth,
				wagePeriod,
				settlementPeriod[0],
				settlementPeriod[1],
				deductionInputs);

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		/*
		 * 계산 성공·실패 모두 기존 조회 Handler가
		 * 모달, 사원목록, 날짜, 월 합계 등을 다시 구성한다.
		 */
		DailyWagePaymentInputHandler
			.prepareInternalCalculationRender(
				req,
				calculationResult);

		return dailyWagePaymentInputHandler.process(
			req,
			res);
	}

	private Date[] resolveSettlementPeriod(
		String wageMonth,
		String wagePeriod) {

		WageLedgerSummary periodSummary = wagePaymentInputService.getPeriodSummary(
			wageMonth,
			wagePeriod);

		if (periodSummary != null) {

			return new Date[] {
				toSqlDate(
					periodSummary
						.getSettlementPeriodStartDate()),
				toSqlDate(
					periodSummary
						.getSettlementPeriodEndDate())
			};
		}

		WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
			wageMonth);

		return new Date[] {
			defaultPeriod.getSettlementStartDate(),
			defaultPeriod.getSettlementEndDate()
		};
	}

	private List<WagePaymentItemInput> parseDeductionInputs(
		HttpServletRequest req) {

		String[] wageTypeIds = req.getParameterValues("wageTypeId");

		String[] wageValues = req.getParameterValues("wageValue");

		/*
		 * 공제항목이 없는 작업공간에서는
		 * 두 파라미터가 모두 없는 것이 정상이다.
		 */
		if (wageTypeIds == null
			&& wageValues == null) {

			return new ArrayList<>();
		}

		if (wageTypeIds == null
			|| wageValues == null
			|| wageTypeIds.length != wageValues.length) {

			throw new IllegalArgumentException(
				"공제항목 정보가 올바르지 않습니다.");
		}

		List<WagePaymentItemInput> result = new ArrayList<>();

		for (int i = 0; i < wageTypeIds.length; i++) {

			String wageTypeIdValue = trim(wageTypeIds[i]);

			String wageValue = trim(wageValues[i]);

			try {

				if (wageTypeIdValue == null) {
					throw new NumberFormatException();
				}

				Integer wageTypeId = Integer.valueOf(wageTypeIdValue);

				if (wageTypeId <= 0) {
					throw new NumberFormatException();
				}

				Long amount = wageValue == null
					? 0L
					: Long.valueOf(wageValue);

				result.add(
					new WagePaymentItemInput(
						wageTypeId,
						amount));

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"공제금액은 0원 이상의 정수로 입력해야 합니다.");
			}
		}

		return result;
	}

	private Integer parseEmployeeId(
		String value) {

		value = trim(value);

		try {

			if (value == null) {
				throw new NumberFormatException();
			}

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

	private Date toSqlDate(
		java.util.Date value) {

		return value == null
			? null
			: new Date(value.getTime());
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