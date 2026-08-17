package employee.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeListDto;
import employee.service.EmployeeListService;
import mvc.command.CommandHandler;

public class EmployeeListHandler implements CommandHandler {

	private EmployeeListService service = new EmployeeListService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. 화면에서 넘어온 limit 값 받기 (기본값은 사진처럼 30개!)
		String limitStr = request.getParameter("limit");
		int limit = 30; // 기본값
		if (limitStr != null && !limitStr.isEmpty()) {
			try {
				limit = Integer.parseInt(limitStr);
			} catch (Exception e) {}
		}

		// 2. Service에 limit 값을 넣어서 데이터 가져오기
		List<EmployeeListDto> employeeList = service.getEmployeeList(limit);

		// 3. JSP로 데이터 넘겨주기
		request.setAttribute("employeeList", employeeList);
		request.setAttribute("limit", limit); // 선택된 드롭다운 유지를 위해 limit도 넘김

		return "/WEB-INF/view/employee/employeeList.jsp"; // 🌟 본인 경로에 맞게 확인!
	}
}