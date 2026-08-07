package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupUpdateHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		req.setCharacterEncoding("UTF-8");
		String idStr = req.getParameter("attendanceGroupId");
		String groupName = req.getParameter("groupName");

		if (idStr != null && groupName != null && !groupName.trim().isEmpty()) {
			int id = Integer.parseInt(idStr);
			groupService.updateGroup(id, groupName.trim());
		}

		res.setStatus(HttpServletResponse.SC_OK);
		return null;
	}
}