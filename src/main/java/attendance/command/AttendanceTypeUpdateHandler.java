package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceService;
import master.model.AttendanceType;
import mvc.command.CommandHandler;

public class AttendanceTypeUpdateHandler implements CommandHandler {

	private AttendanceService attendanceService = new AttendanceService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		// 1. PK 파라미터 확인 (수정 대상 ID)
		String idStr = req.getParameter("attendanceTypeId");
		if (idStr == null || idStr.trim().isEmpty()) {
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
		}

		int attendanceTypeId = Integer.parseInt(idStr);
		String name = req.getParameter("name");
		String unit = req.getParameter("unit");

		String groupStr = req.getParameter("attendanceGroupId");
		Integer attendanceGroupId = (groupStr != null && !groupStr.trim().isEmpty()) ? Integer.parseInt(groupStr)
			: null;

		String vacStr = req.getParameter("vacationTypeId");
		Integer vacationTypeId = (vacStr != null && !vacStr.trim().isEmpty()) ? Integer.parseInt(vacStr) : null;

		String usage = req.getParameter("usage");

		// 2. DTO 객체 생성 (PK 포함)
		AttendanceType att = new AttendanceType(attendanceTypeId, name, unit, attendanceGroupId, vacationTypeId, usage);

		// 3. 수정 서비스 실행
		attendanceService.modifyAttendance(att);

		// 4. 목록 화면으로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}
}