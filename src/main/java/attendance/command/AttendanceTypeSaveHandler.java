package attendance.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.service.AttendanceService;
import master.model.AttendanceType;
import mvc.command.CommandHandler;

public class AttendanceTypeSaveHandler implements CommandHandler {

	private AttendanceService attendanceService = new AttendanceService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		// 1. 폼 파라미터 수집
		String name = req.getParameter("name");
		String unit = req.getParameter("unit");

		String groupStr = req.getParameter("attendanceGroupId");
		// int 대신 Integer를 사용하여 선택 안 함 시 null 처리
		Integer attendanceGroupId = (groupStr != null && !groupStr.trim().isEmpty()) ? Integer.parseInt(groupStr)
			: null;

		String vacStr = req.getParameter("vacationTypeId");
		// int 대신 Integer를 사용하여 선택 안 함 시 null 처리
		Integer vacationTypeId = (vacStr != null && !vacStr.trim().isEmpty()) ? Integer.parseInt(vacStr) : null;

		String usage = req.getParameter("usage");

		// 2. DTO 생성 (PK는 MAX+1로 채번하므로 null 전달)
		AttendanceType att = new AttendanceType(null, name, unit, attendanceGroupId, vacationTypeId, usage);

		// 3. DB 저장
		try {
			attendanceService.addAttendance(att);
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
			return null;
			// ... 에러 발생 시 catch 블록 내부 ...
		} catch (RuntimeException e) {
			String errorMessage = e.getMessage();
			// (중복 에러 체크 로직 동일)

			// 1. 세션에 메시지 저장
			req.getSession().setAttribute("errorMessage", errorMessage);

			// 2. 리다이렉트 (PRG 패턴: Post-Redirect-Get)
			res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");

			// 3. 컨트롤러가 포워딩하지 않도록 null 리턴
			return null;
		}
	}
}