package employee.command;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.dao.UserInfoDao;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class DeptPosManageHandler implements CommandHandler {

	private UserInfoDao dao = new UserInfoDao();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");

		String type = request.getParameter("type");
		String action = request.getParameter("action");

		int id = 0;
		if (request.getParameter("id") != null) {
			id = Integer.parseInt(request.getParameter("id"));
		}
		String name = request.getParameter("name");

		try (Connection conn = ConnectionProvider.getConnection()) {
			if ("dept".equals(type)) {
				if ("add".equals(action))
					dao.insertDepartment(conn, name);
				else if ("edit".equals(action))
					dao.updateDepartment(conn, id, name);
				else if ("delete".equals(action))
					dao.deleteDepartment(conn, id);
			} else if ("pos".equals(type)) {
				if ("add".equals(action))
					dao.insertPosition(conn, name);
				else if ("edit".equals(action))
					dao.updatePosition(conn, id, name);
				else if ("delete".equals(action))
					dao.deletePosition(conn, id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter()
				.println("<script>alert('처리 중 오류 발생: " + e.getMessage() + "'); history.back();</script>");
			return null;
		}

		// 🌟 마법의 꼬리표 달기! (부서를 고쳤으면 부서창 열기, 직위를 고쳤으면 직위창 열기)
		String modalToOpen = "dept".equals(type) ? "deptModal" : "posModal";
		response.sendRedirect(request.getContextPath() + "/employee/userInfo.do?openModal=" + modalToOpen);

		return null;
	}
}