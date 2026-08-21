package dailywork.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.service.FieldOrProjectService;
import mvc.command.CommandHandler;

public class ProjectManageHandler implements CommandHandler {
	private FieldOrProjectService projectService = new FieldOrProjectService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// Ajax 통신이므로 한글 깨짐 방지 및 응답 타입 설정
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = res.getWriter();

		String action = req.getParameter("action"); // 동작 구분 (add, delete, reset)

		try {
			if ("add".equals(action)) {
				String projectName = req.getParameter("projectName");
				boolean success = projectService.addProject(projectName);
				out.write(success ? "success" : "fail");

			} else if ("delete".equals(action)) {
				int projectId = Integer.parseInt(req.getParameter("projectId"));
				boolean success = projectService.removeProject(projectId);
				out.write(success ? "success" : "fail");

			} else if ("reset".equals(action)) {
				boolean success = projectService.resetProjects();
				out.write(success ? "success" : "fail");

			} else {
				out.write("invalid_action");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		} finally {
			out.flush();
		}

		// 화면(JSP) 이동을 하지 않으므로 null 반환
		return null;
	}
}