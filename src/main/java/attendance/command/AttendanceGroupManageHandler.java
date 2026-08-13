package attendance.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.model.AttendanceGroup;
import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;

public class AttendanceGroupManageHandler implements CommandHandler {

	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 1. 등록된 근태그룹 목록 조회
		List<AttendanceGroup> groupList = groupService.getGroupList();
		req.setAttribute("groupList", groupList);

		// 2. 팝업 JSP 경로 반환
		return "/WEB-INF/view/attendance/attendanceGroupManage.jsp";
	}
}