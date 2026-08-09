package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupSaveHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");
		String groupName = req.getParameter("groupName");

		if (groupName != null && !groupName.trim().isEmpty()) {
			groupService.addGroup(groupName.trim());
		}

		// 저장 후 목록 화면으로 리다이렉트
		res.sendRedirect(req.getContextPath() + "/attendanceGroupManage.do");
		return null;
	}
}