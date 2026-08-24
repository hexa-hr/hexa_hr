package retirement.command;

import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;
import retirement.dao.RetirementDao;
import retirement.model.RetirementVO;

public class RetirementManagementHandler implements CommandHandler {

	private RetirementDao retirementDao = new RetirementDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String action = req.getParameter("action");

		// AJAX: 給与内訳読み込み (直近3ヶ月の給与期間ごとの詳細を取得)
		if ("getSalary".equals(action)) {
			return processAjaxSalary(req, res);
		}

		// 基本画面ローディング (GET/POST 共通)
		return processForm(req, res);
	}

	// 画面ローディング
	private String processForm(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try (Connection conn = ConnectionProvider.getConnection()) {
			List<RetirementVO> retiredList = retirementDao.selectRetiredEmployees(conn);
			req.setAttribute("retiredList", retiredList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/WEB-INF/view/retirement/retirementManagement.jsp";
	}

	// AJAXリクエスト処理: 3ヶ月の給与詳細をJSON形式で返却
	private String processAjaxSalary(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int employeeId = Integer.parseInt(req.getParameter("employeeId"));
		String resigDateStr = req.getParameter("resignationDate");

		// 退職日の1日前を算定終了日とし、その3ヶ月前を算定開始日とする
		LocalDate resigDate = LocalDate.parse(resigDateStr);
		LocalDate endDate = resigDate.minusDays(1);
		LocalDate startDate = resigDate.minusMonths(3);

		StringBuilder json = new StringBuilder();
		json.append("{ \"periods\": [");

		LocalDate current = startDate;
		long totalDays = 0;
		long totalSalary = 0;
		boolean isFirst = true;

		try (Connection conn = ConnectionProvider.getConnection()) {
			while (!current.isAfter(endDate)) {
				// 現在の月の末日を計算
				LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth());
				// 期間の終了日が月の末日か、全体の終了日(endDate)のどちらか早い方になる
				LocalDate periodEnd = endOfMonth.isBefore(endDate) ? endOfMonth : endDate;

				// 算定日数
				long days = ChronoUnit.DAYS.between(current, periodEnd) + 1;
				String monthStr = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));

				// DBから該当月の給与総額を取得
				long monthWage = retirementDao.getWageByMonth(conn, employeeId, monthStr);
				long proratedWage = monthWage;

				// 該当月が満たない場合(中間日付の計算日)は日割り計算 (日割り計算式: 月給 / その月の日数 * 算定日数)
				if (days < current.lengthOfMonth()) {
					proratedWage = Math.round((double) monthWage / current.lengthOfMonth() * days);
				}

				if (!isFirst)
					json.append(",");
				json.append(String.format("{\"startDate\":\"%s\", \"endDate\":\"%s\", \"days\":%d, \"amount\":%d}",
						current.toString(), periodEnd.toString(), days, proratedWage));

				totalDays += days;
				totalSalary += proratedWage;
				isFirst = false;

				// 次の月の1日へ移動
				current = endOfMonth.plusDays(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		json.append("], \"totalDays\":").append(totalDays).append(", \"totalSalary\":").append(totalSalary).append("}");

		res.setContentType("application/json; charset=UTF-8");
		PrintWriter out = res.getWriter();
		out.print(json.toString());
		out.flush();

		return null; // AJAXリクエストのためJSPへのフォワードは不要
	}
}