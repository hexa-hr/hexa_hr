package master.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.CompanyInfo;
import master.service.CompanyInfoService;
import mvc.command.CommandHandler;

public class CompanyInfoHandler implements CommandHandler {

	private CompanyInfoService companyService = new CompanyInfoService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 서비스(가짜 DB)에서 현재 저장된 정보를 무조건 가져옴
		CompanyInfo company = companyService.getCompanyInfo();

		req.setAttribute("company", company);
		return "/WEB-INF/view/companyInfo.jsp";
	}
}