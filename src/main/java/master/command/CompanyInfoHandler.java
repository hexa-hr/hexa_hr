package master.command; // 본인의 패키지 경로에 맞게 수정

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import department.model.Department;
import department.service.DepartmentService;
import mvc.command.CommandHandler;

public class CompanyInfoHandler implements CommandHandler {
	private DepartmentService departmentService = new DepartmentService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// 1. DB에서 부서 목록을 가져옴
		List<Department> departmentList = departmentService.getDepartmentList();

		// 2. 바구니(request)에 담음 (이 이름이 jsp의 ${departmentList}와 매칭됨)
		req.setAttribute("departmentList", departmentList);

		// 3. 데이터를 품은 채로 JSP 화면으로 이동함 (포워드)
		return "/WEB-INF/view/companyInfo.jsp";
	}
}