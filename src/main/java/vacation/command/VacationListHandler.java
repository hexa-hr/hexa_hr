package vacation.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler; // 프로젝트의 공통 핸들러 인터페이스 경로에 맞게 조정
// import vacation.service.VacationService; // 서비스 클래스가 있다면 임포트

public class VacationListHandler implements CommandHandler {

	// private VacationService vacationService = new VacationService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. 검색 파라미터 받기 (휴가항목 선택, 검색어 등)
		String vacationTypeId = request.getParameter("vacationTypeId");
		String keyword = request.getParameter("keyword");

		// 2. 서비스 또는 DAO를 통해 데이터 조회 (예시)
		// List<VacationDto> vacationList = vacationService.getVacationList(vacationTypeId, keyword);
		// List<VacationTypeDto> vacationTypeList = vacationService.getVacationTypeList();

		// 3. request에 결과 데이터 담기
		// request.setAttribute("vacationList", vacationList);
		// request.setAttribute("vacationTypeList", vacationTypeList);

		// 4. 보여줄 JSP 경로 리턴 (ControllerUsingURI가 forward 처리)
		return "/WEB-INF/view/vacation/vacationList.jsp";
	}
}