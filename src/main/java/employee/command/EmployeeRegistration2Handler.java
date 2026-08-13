package employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;

public class EmployeeRegistration2Handler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// GET 요청이든 POST 요청이든 우선 사원정보 2 화면을 보여주도록 설정합니다.

		if (req.getMethod().equalsIgnoreCase("GET")) {
			return "/WEB-INF/view/employeeRegistration2.jsp";
		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			// 데이터 저장 로직은 추후 이곳에 구현합니다.
			return "/WEB-INF/view/employeeRegistration2.jsp";
		}

		return null;
	}
}