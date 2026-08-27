package dailywork.command;

import java.io.PrintWriter;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.model.DailyWorkVO;
import dailywork.service.DailyWorkService;
import mvc.command.CommandHandler;

public class DailyWorkSaveHandler implements CommandHandler {
	private DailyWorkService workService = new DailyWorkService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		try {
			// 1. パラメータの安全なパース (空値エラー防止)
			String empIdStr = req.getParameter("employee_id");
			if (empIdStr == null || empIdStr.isEmpty()) {
				out.println("<script>alert('社員が選択されていません。'); history.back();</script>");
				return null;
			}
			int employeeId = Integer.parseInt(empIdStr);

			String dateStr = req.getParameter("work_date");
			Date workDate = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr)
					: new Date(System.currentTimeMillis());

			String projIdStr = req.getParameter("field_or_project_id");
			int fieldProjectId = (projIdStr != null && !projIdStr.isEmpty()) ? Integer.parseInt(projIdStr) : 0;

			// カンマの除去および数値パース
			Long dailyWage = parseLongSafe(req.getParameter("daily_wage"));
			Double paymentRate = parseDoubleSafe(req.getParameter("payment_rate"));
			Long incomeTax = parseLongSafe(req.getParameter("income_tax"));
			Long localTax = parseLongSafe(req.getParameter("local_tax"));
			Long actualPayment = parseLongSafe(req.getParameter("actual_payment"));

			// 修正(UPDATE)のためのwork_id収集 (新規登録であれば0)
			String workIdStr = req.getParameter("work_id");
			Integer workId = (workIdStr != null && !workIdStr.trim().isEmpty()) ? Integer.parseInt(workIdStr) : null;

			// 2. VOオブジェクトの生成
			DailyWorkVO vo = new DailyWorkVO(workId, employeeId, workDate, fieldProjectId, dailyWage, paymentRate,
					incomeTax, localTax, actualPayment);

			// 3. Serviceの呼び出し
			boolean isSuccess = workService.saveDailyWork(vo);

			// 4. 結果出力
			if (isSuccess) {
				String msg = (workId != null && workId > 0) ? "勤務記録が修正されました。" : "勤務記録が登録されました。";
				out.println("<script>alert('" + msg + "'); location.href='" + req.getContextPath()
						+ "/dailywork/manage.do';</script>");
			} else {
				out.println("<script>alert('保存に失敗しました。'); history.back();</script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.println("<script>alert('エラー発生: " + e.getMessage().replace("'", "\\'") + "'); history.back();</script>");
		} finally {
			out.flush();
		}
		return null;
	}

	// カンマ(,)が含まれているか、空の文字列を安全にLongに変換するメソッド
	private Long parseLongSafe(String val) {
		if (val == null || val.trim().isEmpty())
			return 0L;
		return Long.parseLong(val.replaceAll(",", ""));
	}

	private Double parseDoubleSafe(String val) {
		if (val == null || val.trim().isEmpty())
			return 1.0;
		return Double.parseDouble(val);
	}
}
