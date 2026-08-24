package employee.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeRetirementListDto;
import employee.service.EmployeeRetirementService;
import mvc.command.CommandHandler;

public class EmployeeRetirementHandler implements CommandHandler {
	private EmployeeRetirementService service = new EmployeeRetirementService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. 넘어온 파라미터 받기 (상태 필터, 현재 페이지)
		String statusFilter = request.getParameter("statusFilter");
		String pageStr = request.getParameter("page");

		int page = 1; // 기본 1페이지
		if (pageStr != null && !pageStr.isEmpty()) {
			try {
				page = Integer.parseInt(pageStr);
			} catch (Exception e) {}
		}

		int size = 20; // 🌟 한 페이지에 보여줄 개수 (20명)

		// 2. 데이터 가져오기
		int totalCount = service.getRetirementCount(statusFilter);
		List<EmployeeRetirementListDto> list = service.getRetirementList(statusFilter, page, size);

		// 3. 페이징 계산
		int totalPages = (int)Math.ceil((double)totalCount / size);
		if (totalPages == 0)
			totalPages = 1;

		int startPage = ((page - 1) / 5) * 5 + 1; // 페이징 블록 시작 (예: 1, 6, 11)
		int endPage = startPage + 4; // 페이징 블록 끝 (예: 5, 10, 15)
		if (endPage > totalPages)
			endPage = totalPages;

		// 4. JSP에 데이터 전달
		request.setAttribute("retireList", list);
		request.setAttribute("statusFilter", statusFilter);
		request.setAttribute("currentPage", page);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("startPage", startPage);
		request.setAttribute("endPage", endPage);

		return "/WEB-INF/view/employee/employeeRetirement.jsp";
	}
}