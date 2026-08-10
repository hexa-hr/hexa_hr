package master.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.CompanyInfo;
import master.service.ModifyCompanyInfoService;
import mvc.command.CommandHandler;

public class ModifyCompanyInfoHandler implements CommandHandler {

	private ModifyCompanyInfoService modifyService = new ModifyCompanyInfoService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// POST 요청 처리
		if (req.getMethod().equalsIgnoreCase("POST")) {

			// 1. 파라미터 값 읽어오기
			CompanyInfo info = new CompanyInfo();
			info.setAddress(req.getParameter("address"));

			// ★ 기존 setPhone 대신 phone2, phone3으로 분리해서 받기
			info.setPhone2(req.getParameter("phone2"));
			info.setPhone3(req.getParameter("phone3"));
			// ... 나머지 파라미터 세팅 ...

			// 2. 서비스 클래스 호출하여 DB 수정
			modifyService.modify(info);

			// 3. 수정 완료 후 원래 정보 조회 페이지로 리다이렉트 (PRG 패턴)
			res.sendRedirect(req.getContextPath() + "/companyInfo.do");
			return null;

		} else {
			// GET 방식 접근 금지 처리
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
}