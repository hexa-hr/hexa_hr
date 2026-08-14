package wage.command;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.WageType;
import mvc.command.CommandHandler;
import wage.service.WageService;

public class WageTypeSettingHandler implements CommandHandler {

	private WageService wageService = new WageService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 급여 항목 목록 조회
		List<WageType> wageList = wageService.getWageList();
		req.setAttribute("wageList", wageList);

		// 공제항목 리스트 조회 추가
		List<WageType> deductionList = wageService.getDeductionList();
		req.setAttribute("deductionList", deductionList);

		// JSP 화면 포워딩 (JSP 파일 경로에 맞춰 수정하세요)
		return "/WEB-INF/view/wage/wageType.jsp";
	}
}