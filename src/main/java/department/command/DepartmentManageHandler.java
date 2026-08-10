package department.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import department.model.Department;
import department.service.DepartmentService;
import mvc.command.CommandHandler;

public class DepartmentManageHandler implements CommandHandler {
	private DepartmentService departmentService = new DepartmentService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String cmd = req.getParameter("cmd");

		// 추가, 삭제, 초기화 명령이 들어왔을 때 처리
		if (cmd != null) {
			if (cmd.equals("add")) {
				String departmentName = req.getParameter("departmentName");
				if (departmentName != null && !departmentName.trim().isEmpty()) {
					departmentService.addDepartment(departmentName);
				}
			} else if (cmd.equals("delete")) {
				String departmentIdStr = req.getParameter("departmentId");
				if (departmentIdStr != null) {
					departmentService.removeDepartment(Integer.parseInt(departmentIdStr));
				}
			} else if (cmd.equals("clear")) {
				departmentService.clearAllDepartments();
			}
			// 처리가 끝나면 다시 팝업창 자신을 새로고침 (주소 풀네임으로 수정 완료!)
			res.sendRedirect(req.getContextPath() + "/departmentManage.do");
			return null;
		}

		// 명령(cmd)이 없으면 단순히 목록을 조회해서 팝업 화면을 띄워줌
		List<Department> departmentList = departmentService.getDepartmentList();
		req.setAttribute("departmentList", departmentList);

		return "/WEB-INF/view/departmentManageModal.jsp";
	}
}