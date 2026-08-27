package vacation.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.UpdateVacationTypeService;
import vacation.service.VacationTypeService; // リスト再取得のため追加

public class VacationTypeUpdateHandler implements CommandHandler {

	private UpdateVacationTypeService updateService = new UpdateVacationTypeService();
	private VacationTypeService vacationService = new VacationTypeService(); // リスト読み込み用

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// 1. パラメータ受信
		String idStr = req.getParameter("vacationTypeId");

		// 👉 [修正] レコードを選択していない場合、セッションに格納してリダイレクト
		if (idStr == null || idStr.trim().isEmpty()) {
			req.getSession().setAttribute("errorMessage", "休暇項目一覧からレコードを選択してください。");
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		String name = req.getParameter("vacationTypeName");
		String period1Str = req.getParameter("applyPeriod1");
		String period2Str = req.getParameter("applyPeriod2");
		String usage = req.getParameter("usage");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date applyPeriod1 = sdf.parse(period1Str);
		Date applyPeriod2 = sdf.parse(period2Str);

		VacationType vacation = new VacationType();
		vacation.setVacationTypeId(Integer.parseInt(idStr));
		vacation.setVacationTypeName(name);
		vacation.setApplyPeriod1(applyPeriod1);
		vacation.setApplyPeriod2(applyPeriod2);
		vacation.setUsage(usage);

		try {
			updateService.update(vacation);
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			if (e.getCause() != null && e.getCause().getMessage() != null) {
				errorMessage = e.getCause().getMessage();
			}

			if (errorMessage.contains("ORA-00001") || errorMessage.contains("重複") || errorMessage.contains("重複")) {
				errorMessage = "すでに存在する休暇項目名です。";
			}

			// 👉 [修正] 重複エラー発生時、セッションに格納してリダイレクト
			req.getSession().setAttribute("errorMessage", errorMessage);
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}