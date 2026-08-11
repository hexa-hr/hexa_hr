package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupDeleteHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String idStr = req.getParameter("attendanceGroupId");
		if (idStr != null) {
			int id = Integer.parseInt(idStr);
			groupService.deleteGroup(id);
		}

		// 삭제 후 목록 화면으로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/attendanceGroupManage.do");
		return null;
	}
}