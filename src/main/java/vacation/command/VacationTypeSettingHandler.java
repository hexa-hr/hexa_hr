package vacation.command;

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

public class VacationTypeSettingHandler implements CommandHandler {

	private VacationTypeService vacationService = new VacationTypeService();
	private AttendanceService attendanceService = new AttendanceService();
	private AttendanceGroupService groupService = new AttendanceGroupService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 1. 休暇項目リストの取得
		List<VacationType> vacationList = vacationService.getVacationList();

		// 2. 勤怠項目リストの取得
		List<AttendanceType> attendanceList = attendanceService.getAttendanceList();

		// 3. 勤怠グループリストの取得 (ドロップダウン用)
		List<AttendanceGroup> attendanceGroupList = groupService.getGroupList();

		// 4. リクエスト領域に保存
		req.setAttribute("vacationList", vacationList);
		req.setAttribute("attendanceList", attendanceList);
		req.setAttribute("attendanceGroupList", attendanceGroupList);

		return "/WEB-INF/view/attendance/vacationTypeSetting.jsp";
	}
}