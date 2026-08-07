package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupSaveHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		req.setCharacterEncoding("UTF-8");
		String groupName = req.getParameter("groupName");

		if (groupName != null && !groupName.trim().isEmpty()) {
			groupService.addGroup(groupName.trim());
		}

		// AJAX 응답 처리 (성공 200 OK)
		res.setStatus(HttpServletResponse.SC_OK);
		return null;
	}
}