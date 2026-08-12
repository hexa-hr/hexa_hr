package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceService;
import mvc.command.CommandHandler;

public class AttendanceTypeDeleteHandler implements CommandHandler {

	private AttendanceService attendanceService = new AttendanceService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		// 1. 삭제 대상 PK 파라미터 확인
		String idStr = req.getParameter("attendanceTypeId");
		if (idStr != null && !idStr.trim().isEmpty()) {
			int attendanceTypeId = Integer.parseInt(idStr);

			// 2. 삭제 서비스 실행
			attendanceService.removeAttendance(attendanceTypeId);
		}

		// 3. 삭제 완료 후 메인 화면으로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}