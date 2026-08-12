package attendance.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.model.AttendanceGroup;
import attendance.service.AttendanceGroupService;
import attendance.service.AttendanceService;
import master.model.AttendanceType;
import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.VacationTypeService;

public class AttendanceTypeSettingHandler implements CommandHandler {

	private VacationTypeService vacationService = new VacationTypeService();
	private AttendanceService attendanceService = new AttendanceService();
	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 1. 휴가항목 목록 조회
		List<VacationType> vacationList = vacationService.getVacationList();

		// 2. 근태항목 목록 조회
		List<AttendanceType> attendanceList = attendanceService.getAttendanceList();

		// 3. 근태그룹 목록 조회
		List<AttendanceGroup> attendanceGroupList = groupService.getGroupList();

		req.setAttribute("vacationList", vacationList);
		req.setAttribute("attendanceList", attendanceList);
		req.setAttribute("attendanceGroupList", attendanceGroupList);

		return "/WEB-INF/view/attendance/vacationTypeSetting.jsp";
	}
}