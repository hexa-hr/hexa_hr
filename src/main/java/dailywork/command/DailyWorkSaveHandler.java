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
			// 1. 파라미터 안전한 파싱 (빈 값 에러 방지)
			String empIdStr = req.getParameter("employee_id");
			if (empIdStr == null || empIdStr.isEmpty()) {
				out.println("<script>alert('사원이 선택되지 않았습니다.'); history.back();</script>");
				return null;
			}
			int employeeId = Integer.parseInt(empIdStr);

			String dateStr = req.getParameter("work_date");
			Date workDate = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr)
					: new Date(System.currentTimeMillis());

			String projIdStr = req.getParameter("field_or_project_id");
			int fieldProjectId = (projIdStr != null && !projIdStr.isEmpty()) ? Integer.parseInt(projIdStr) : 0;

			// 콤마 제거 및 숫자 파싱
			Long dailyWage = parseLongSafe(req.getParameter("daily_wage"));
			Double paymentRate = parseDoubleSafe(req.getParameter("payment_rate"));
			Long incomeTax = parseLongSafe(req.getParameter("income_tax"));
			Long localTax = parseLongSafe(req.getParameter("local_tax"));
			Long actualPayment = parseLongSafe(req.getParameter("actual_payment"));

			// 수정(UPDATE)을 위한 work_id 수집 (신규 등록이면 0)
			String workIdStr = req.getParameter("work_id");
			Integer workId = (workIdStr != null && !workIdStr.trim().isEmpty()) ? Integer.parseInt(workIdStr) : null;

			// 2. VO 객체 생성
			DailyWorkVO vo = new DailyWorkVO(workId, employeeId, workDate, fieldProjectId, dailyWage, paymentRate,
					incomeTax, localTax, actualPayment);

			// 3. Service 호출
			boolean isSuccess = workService.saveDailyWork(vo);

			// 4. 결과 출력
			if (isSuccess) {
				String msg = (workId != null && workId > 0) ? "근무 기록이 수정되었습니다." : "근무 기록이 등록되었습니다.";
				out.println("<script>alert('" + msg + "'); location.href='" + req.getContextPath()
						+ "/dailywork/manage.do';</script>");
			} else {
				out.println("<script>alert('저장에 실패했습니다.'); history.back();</script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.println("<script>alert('오류 발생: " + e.getMessage().replace("'", "\\'") + "'); history.back();</script>");
		} finally {
			out.flush();
		}
		return null;
	}

	// 콤마(,)가 포함되었거나 비어있는 문자열을 안전하게 Long으로 변환하는 메서드
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