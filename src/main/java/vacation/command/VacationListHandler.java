package vacation.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.model.VacationType;
import vacation.service.VacationListService;

public class VacationListHandler implements CommandHandler {

	// 서비스 객체 생성 (주석 해제 및 알맞은 서비스 클래스 사용)
	private VacationListService vacationService = new VacationListService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 1. 검색 파라미터 받기
		String vacationTypeId = request.getParameter("vacationTypeId");
		String keyword = request.getParameter("keyword");

		// 2. 셀렉트 박스용 활성화된 휴가 항목 리스트 가져오기
		List<VacationType> activeVacationList = vacationService.getActiveVacationTypes();

		// 3. (추가) 조건에 맞는 메인 휴가 현황 리스트 가져오기
		List<VacationType> vacationList = vacationService.getVacationList(vacationTypeId, keyword);

		// 4. request에 바인딩
		request.setAttribute("activeVacationTypeList", activeVacationList);
		request.setAttribute("vacationList", vacationList); // 테이블 출력을 위해 꼭 필요함

		return "/WEB-INF/view/vacation/vacationList.jsp";
	}
}