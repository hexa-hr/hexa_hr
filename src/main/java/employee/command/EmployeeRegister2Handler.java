package employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;

public class EmployeeRegister2Handler implements CommandHandler {

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// GET 요청: 사원정보 1에서 넘어왔을 때 또는 메뉴를 클릭했을 때 화면 보여주기
		if (request.getMethod().equalsIgnoreCase("GET")) {
			String employeeId = request.getParameter("employeeId");
			String tab = request.getParameter("tab");

			// 처음 넘어왔을 때는 탭 정보가 없으므로 기본값 '자격면허(cert)'로 세팅
			if (tab == null || tab.trim().isEmpty()) {
				tab = "cert";
			}

			// JSP 화면에서 쓸 수 있도록 request에 담아줌
			request.setAttribute("employeeId", employeeId);
			request.setAttribute("tab", tab);

			return "/WEB-INF/view/employee/employeeRegister2.jsp";
		}

		// 나중에 자격증이나 경력을 '추가(POST)'하는 로직은 이 아래에 작성하게 될 거야!
		return null;
	}
}