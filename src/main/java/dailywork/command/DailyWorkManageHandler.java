package dailywork.command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import attendance.model.EmployeeVO;
import dailywork.model.FieldOrProjectVO;
import dailywork.service.FieldOrProjectService;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class DailyWorkManageHandler implements CommandHandler {
	private FieldOrProjectService projectService = new FieldOrProjectService();
	private AttendanceDao attendanceDao = new AttendanceDao(); // 기존 사원 목록 조회용 DAO 재사용

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 1. 좌측 사원 목록 가져오기 (AttendanceDao 활용)
		try (Connection conn = ConnectionProvider.getConnection()) {
			List<EmployeeVO> empList = attendanceDao.selectAllEmployees(conn);
			req.setAttribute("empList", empList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 2. 우측 현장/프로젝트 드롭다운 목록 가져오기
		List<FieldOrProjectVO> projectList = projectService.getVisibleProjectList();
		req.setAttribute("projectList", projectList);

		// 3. 메인 화면 JSP로 포워딩
		return "/WEB-INF/view/dailywork/dailyworkManagement.jsp";
	}
}