package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupDeleteHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		String idStr = req.getParameter("attendanceGroupId");
		if (idStr != null) {
			int id = Integer.parseInt(idStr);
			groupService.deleteGroup(id);
		}

		res.setStatus(HttpServletResponse.SC_OK);
		return null;
	}
}