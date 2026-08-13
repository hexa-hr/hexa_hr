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
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		try {
			// 실제 파라미터 이름에 맞춰 수정해야 합니다. (폼 input의 name 속성 기준)
			int employeeId = Integer.parseInt(req.getParameter("employee_id"));
			Date workDate = Date.valueOf(req.getParameter("work_date"));
			int fieldProjectId = Integer.parseInt(req.getParameter("field_or_project_id"));

			// 콤마(,) 제거 후 파싱
			Long dailyWage = Long.parseLong(req.getParameter("daily_wage").replaceAll(",", ""));
			Double paymentRate = Double.parseDouble(req.getParameter("payment_rate"));
			Long incomeTax = Long.parseLong(req.getParameter("income_tax").replaceAll(",", ""));
			Long localTax = Long.parseLong(req.getParameter("local_tax").replaceAll(",", ""));
			Long actualPayment = Long.parseLong(req.getParameter("actual_payment").replaceAll(",", ""));

			// Model 객체 생성 (Setter 없이 생성자로 주입)
			DailyWorkVO vo = new DailyWorkVO(null, employeeId, workDate, fieldProjectId, dailyWage, paymentRate,
					incomeTax, localTax, actualPayment);

			// Service 호출
			boolean isSuccess = workService.saveDailyWork(vo);

			String redirectUrl = req.getContextPath() + "/dailywork/manage.do";

			if (isSuccess) {
				out.println("<script>alert('일용직 근무 기록이 등록되었습니다.'); location.href='" + redirectUrl + "';</script>");
			} else {
				out.println("<script>alert('등록 실패.'); history.back();</script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.println("<script>alert('오류 발생: " + e.getMessage().replace("'", "\\'") + "'); history.back();</script>");
		} finally {
			out.flush();
		}

		return null;
	}
}