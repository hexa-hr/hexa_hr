package employee.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.service.EmployeeRetirementService;
import mvc.command.CommandHandler;

public class EmployeeRetirementProcessHandler implements CommandHandler {

	private EmployeeRetirementService service = new EmployeeRetirementService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");

		// 팝업창에서 넘겨준 데이터들 받기
		String action = request.getParameter("action"); // "save" 또는 "cancel"
		int empId = Integer.parseInt(request.getParameter("employeeId"));

		try {
			if ("save".equals(action)) {
				// 퇴직 처리 (저장)
				String type = request.getParameter("retirementType");
				String reason = request.getParameter("retirementReason");
				String contact = request.getParameter("contactAfterRetirement");

				String dateStr = request.getParameter("retirementDate");
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

				service.executeRetirement(empId, type, date, reason, contact);
			} else if ("cancel".equals(action)) {
				// 퇴직 취소
				service.cancelRetirement(empId);
			}

			// 처리가 끝나면 다시 목록 화면으로 강제 새로고침
			response.sendRedirect(request.getContextPath() + "/employee/retirement.do");
			return null;

		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>alert('처리 실패: " + e.getMessage() + "'); history.back();</script>");
			return null;
		}
	}
}