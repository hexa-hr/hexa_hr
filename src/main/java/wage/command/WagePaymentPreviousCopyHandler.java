package wage.command;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import wage.service.WagePaymentPreviousCopyService;

// 給与入力画面 - 過去給与の読み込みHandler
public class WagePaymentPreviousCopyHandler
	implements CommandHandler {

	private WagePaymentPreviousCopyService wagePaymentPreviousCopyService = new WagePaymentPreviousCopyService();

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

		String sourceWageMonth = trim(
			req.getParameter(
				"sourceWageMonth"));

		String sourceWagePeriod = trim(
			req.getParameter(
				"sourceWagePeriod"));

		String targetWageMonth = trim(
			req.getParameter(
				"wageMonth"));

		String targetWagePeriod = trim(
			req.getParameter(
				"wagePeriod"));

		try {

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			boolean replaceConfirmed = "true".equals(
				req.getParameter(
					"replaceConfirmed"));

			wagePaymentPreviousCopyService.copy(
				sourceWageMonth,
				sourceWagePeriod,
				targetWageMonth,
				targetWagePeriod,
				replaceConfirmed);

			String normalizedTargetWagePeriod = String.valueOf(
				Integer.parseInt(
					targetWagePeriod));

			StringBuilder redirectUrl = new StringBuilder();

			redirectUrl.append(
				req.getContextPath());

			redirectUrl.append(
				"/wage/paymentInput.do");

			redirectUrl.append(
				"?wageMonth=");

			redirectUrl.append(
				encode(
					targetWageMonth));

			redirectUrl.append(
				"&wagePeriod=");

			redirectUrl.append(
				encode(
					normalizedTargetWagePeriod));

			redirectUrl.append(
				"&incomeType=");

			redirectUrl.append(
				encode(
					incomeType));

			redirectUrl.append(
				"&previousCopied=true");

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
			"正しい所得区分を選択する必要があります。");
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