package master.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.CompanyInfo;
import master.model.ContactPersonInfo;
import master.service.ModifyCompanyInfoService;
import mvc.command.CommandHandler;

public class ModifyCompanyInfoHandler implements CommandHandler {

	private ModifyCompanyInfoService modifyService = new ModifyCompanyInfoService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("POST")) {
			// 한글 깨짐 방지
			request.setCharacterEncoding("UTF-8");

			// --- 1. 회사 정보 파라미터 파싱 ---
			String compIdStr = request.getParameter("companyId");
			// 회사 정보가 아예 없을 경우를 대비해 기본값 1로 세팅
			int companyId = (compIdStr != null && !compIdStr.isEmpty()) ? Integer.parseInt(compIdStr) : 1;

			String companyName = request.getParameter("companyName");
			String representativeTitle = request.getParameter("representativeTitle");
			String representativeName = request.getParameter("representativeName");
			String businessNumber = request.getParameter("businessNumber");
			String corporationNumber = request.getParameter("corporationNumber");

			// 날짜 파싱
			String estDateStr = request.getParameter("establishmentDate");
			Date establishmentDate = null;
			if (estDateStr != null && !estDateStr.trim().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				establishmentDate = sdf.parse(estDateStr);
			}

			String website = request.getParameter("website");
			String officeAddress = request.getParameter("officeAddress");
			String phoneNumber = request.getParameter("phoneNumber");
			String faxNumber = request.getParameter("faxNumber");
			String businessType = request.getParameter("businessType");
			String businessItem = request.getParameter("businessItem");

			CompanyInfo companyInfo = new CompanyInfo(
				companyId, companyName, representativeTitle, representativeName,
				businessNumber, corporationNumber, establishmentDate, website,
				officeAddress, phoneNumber, faxNumber, businessType, businessItem);

			// --- 2. 담당자 정보 파라미터 파싱 (약어 변수명 적용) ---
			String personIdStr = request.getParameter("personId");
			int personId = (personIdStr != null && !personIdStr.isEmpty()) ? Integer.parseInt(personIdStr) : 0;

			String contName = request.getParameter("contName");

			String deptIdStr = request.getParameter("deptId");
			int deptId = (deptIdStr != null && !deptIdStr.isEmpty()) ? Integer.parseInt(deptIdStr) : 0;

			String posIdStr = request.getParameter("posId");
			int posId = (posIdStr != null && !posIdStr.isEmpty()) ? Integer.parseInt(posIdStr) : 0;

			String conPhone = request.getParameter("conPhone");
			String mobile = request.getParameter("mobile");
			String email = request.getParameter("email");

			ContactPersonInfo contactInfo = new ContactPersonInfo(
				personId, companyId, contName, deptId, posId, conPhone, mobile, email);

			// --- 3. 서비스 호출 및 DB 업데이트 ---
			try {
				modifyService.modify(companyInfo, contactInfo);

				// 성공 시, 다시 조회 화면으로 리다이렉트 (회사 정보 페이지 컨트롤러 주소)
				response.sendRedirect(request.getContextPath() + "/master/companyInfo.do");
				return null;

			} catch (Exception e) {
				e.printStackTrace();
				// 실패 시 에러 페이지 (임시 설정)
				return "/WEB-INF/view/error.jsp";
			}
		}

		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}
}