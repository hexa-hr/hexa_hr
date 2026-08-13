package employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;

public class EmployeeRegistrationFormHandler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 화면을 띄워달라는 GET 요청이 오든, 
		// 나중에 데이터를 저장하라는 POST 요청이 오든 일단 화면(View) 경로를 리턴하도록 기본 세팅을 합니다.

		if (req.getMethod().equalsIgnoreCase("GET")) {
			// 1. 단순 화면 출력 요청 시
			return "/WEB-INF/view/employeeRegistration.jsp";
		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			// 2. 폼 데이터를 입력하고 [저장] 버튼을 눌렀을 때의 처리는 나중에 이곳에 추가합니다.
			// 지금은 일단 똑같이 화면을 띄우게 해둡니다.
			return "/WEB-INF/view/employeeRegistration.jsp";
		}

		return null;
	}
}