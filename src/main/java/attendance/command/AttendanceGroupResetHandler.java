package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupResetHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		groupService.resetGroups();

		res.setStatus(HttpServletResponse.SC_OK);
		return null;
	}
}