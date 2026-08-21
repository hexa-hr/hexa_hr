package dailywork.command;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.model.FieldOrProjectVO;
import dailywork.service.DailyWorkService;
import dailywork.service.FieldOrProjectService;
import mvc.command.CommandHandler;

public class DailyWorkDetailHandler implements CommandHandler {

	private DailyWorkService dailyWorkService = new DailyWorkService();
	private FieldOrProjectService fieldOrProjectService = new FieldOrProjectService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. JSP에서 넘긴 검색 조건 파라미터들 받기
		// (체크 안 한 항목은 null로 들어옵니다!)
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String empName = request.getParameter("empName");
		String deptId = request.getParameter("deptId");
		String projectId = request.getParameter("projectId");

		// 2. 검색 조건들을 Service에 넘겨서 조건에 맞는 데이터만 리스트로 받아오기
		List<Map<String, Object>> detailList = dailyWorkService.getDailyWorkDetailList(startDate, endDate, empName,
				deptId, projectId);

		// 3. 왼쪽 검색창의 "현장/프로젝트" 드롭다운(select)에 띄워줄 현장 목록 가져오기
		List<FieldOrProjectVO> projectList = fieldOrProjectService.getVisibleProjects();

		// 4. 받아온 데이터들을 JSP 화면으로 넘겨주기
		request.setAttribute("detailList", detailList);
		request.setAttribute("projectList", projectList);

		// 5. 상세조회 JSP 파일 경로 지정
		return "/WEB-INF/view/dailywork/dailywork_detail_inquiry.jsp";
	}
}