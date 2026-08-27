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

// 給与入力・管理（日雇い）自動計算Handler
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
		 * 月・回次自体が不正なリクエストの場合は
		 * 画面を再構成できないため、400として処理する。
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
		 * 計算の成功・失敗にかかわらず、既存の照会Handlerが
		 * モーダル、社員一覧、日付、月間合計などを再構成する。
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
		 * 控除項目がないワークスペースでは
		 * 両方のパラメータが存在しない状態が正常である。
		 */
		if (wageTypeIds == null
			&& wageValues == null) {

			return new ArrayList<>();
		}

		if (wageTypeIds == null
			|| wageValues == null
			|| wageTypeIds.length != wageValues.length) {

			throw new IllegalArgumentException(
				"控除項目情報が正しくありません。");
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
					"控除金額は0ウォン以上の整数で入力する必要があります。");
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
				"正しい社員を選択する必要があります。");
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		wageMonth = trim(wageMonth);

		if (wageMonth == null) {
			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
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
			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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