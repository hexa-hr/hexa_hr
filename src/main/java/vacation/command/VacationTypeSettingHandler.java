package vacation.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.model.AttendanceGroup;
import attendance.service.AttendanceGroupService;
import mvc.command.CommandHandler;
import vacation.service.VacationTypeService;

public class VacationTypeSettingHandler implements CommandHandler {

	private VacationTypeService vacationService = new VacationTypeService();
	private AttendanceGroupService groupService = new AttendanceGroupService(); // 추가

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 1. 휴가항목 목록
		req.setAttribute("vacationList", vacationService.getVacationList());

		// 2. 근태그룹 목록 (셀렉트 박스용 추가)
		List<AttendanceGroup> attendanceGroupList = groupService.getGroupList();
		req.setAttribute("attendanceGroupList", attendanceGroupList);

		return "/WEB-INF/view/attendance/vacationTypeSetting.jsp";
	}
}