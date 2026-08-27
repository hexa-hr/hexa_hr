package employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.service.EmployeeDeleteService;
import mvc.command.CommandHandler;

public class EmployeeDeleteHandler implements CommandHandler {

	private EmployeeDeleteService service = new EmployeeDeleteService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 체크박스에서 넘어온 사원번호 배열 받기
		String[] empIds = request.getParameterValues("empIds");

		// 삭제 서비스 호출
		service.deleteEmployees(empIds);

		// 삭제 완료 후 다시 리스트 페이지로 강제 이동 (새로고침 효과)
		response.sendRedirect(request.getContextPath() + "/employee/list.do");

		return null;
	}
}