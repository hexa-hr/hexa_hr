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

		try {
			// 1. 넘어온 데이터 받기 (비어있으면 에러 방어)
			String action = request.getParameter("action");
			if (action == null || action.isEmpty())
				action = "save";

			String empIdStr = request.getParameter("employeeId");
			if (empIdStr == null || empIdStr.trim().isEmpty()) {
				throw new Exception("선택된 사원번호(employeeId)가 없습니다. 팝업창 전달값을 확인하세요.");
			}
			int empId = Integer.parseInt(empIdStr);

			if ("save".equals(action)) {
				// 퇴직 처리
				String type = request.getParameter("retirementType");
				String reason = request.getParameter("retirementReason");
				String contact = request.getParameter("contactAfterRetirement");

				String dateStr = request.getParameter("retirementDate");
				Date date = null;
				if (dateStr != null && !dateStr.trim().isEmpty()) {
					date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
				} else {
					throw new Exception("퇴직일자를 반드시 선택해주세요.");
				}

				// 🌟 DB 업데이트 실행!
				service.executeRetirement(empId, type, date, reason, contact);

			} else if ("cancel".equals(action)) {
				// 퇴직 취소
				service.cancelRetirement(empId);
			}

			// 처리가 끝나면 다시 사원 목록이나 퇴직자 목록 화면으로 강제 새로고침
			// (사원목록으로 보내시려면 /employee/list.do 로 바꾸시면 됩니다)
			response.sendRedirect(request.getContextPath() + "/employee/retirement.do");
			return null;

		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>alert('처리 실패: " + e.getMessage() + "'); history.back();</script>");
			return null;
		}
	}
}