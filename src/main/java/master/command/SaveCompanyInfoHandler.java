package master.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import master.model.CompanyInfo;
import master.model.ContactPersonInfo;
import master.service.SaveCompanyInfoService; // ★ 저장용 서비스 클래스 임포트
import mvc.command.CommandHandler;

public class SaveCompanyInfoHandler implements CommandHandler {

	// ★ 저장 로직을 처리할 서비스 클래스 인스턴스
	private SaveCompanyInfoService saveService = new SaveCompanyInfoService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// POST 요청 (폼 전송) 일 때만 처리
		if (request.getMethod().equalsIgnoreCase("POST")) {

			// 1. CompanyInfo 데이터 받기 및 형변환
			// 신규 등록이므로 companyId는 사용자에게 받지 않고, DB에서 시퀀스나 auto_increment로 처리하는 것이 일반적이야.
			// 하지만 화면 기획상 입력받거나 숨겨서 넘어온다면 아래처럼 처리.
			String companyIdStr = request.getParameter("companyId");
			int companyId = (companyIdStr != null && !companyIdStr.isEmpty()) ? Integer.parseInt(companyIdStr) : 0;

			String companyName = request.getParameter("companyName");
			String representativeTitle = request.getParameter("representativeTitle");
			String representativeName = request.getParameter("representativeName");
			String businessNumber = request.getParameter("businessNumber");
			String corporationNumber = request.getParameter("corporationNumber");

			// 날짜 파싱 (String -> java.util.Date)
			String estDateStr = request.getParameter("establishmentDate");
			Date establishmentDate = null;
			if (estDateStr != null && !estDateStr.trim().isEmpty()) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					establishmentDate = sdf.parse(estDateStr);
				} catch (Exception e) {
					System.out.println("날짜 파싱 오류: " + e.getMessage());
				}
			}

			String website = request.getParameter("website");
			String officeAddress = request.getParameter("officeAddress");
			String phoneNumber = request.getParameter("phoneNumber");
			String faxNumber = request.getParameter("faxNumber");
			String businessType = request.getParameter("businessType");
			String businessItem = request.getParameter("businessItem");

			// CompanyInfo 객체 생성
			CompanyInfo companyInfo = new CompanyInfo(
				companyId, companyName, representativeTitle, representativeName,
				businessNumber, corporationNumber, establishmentDate, website,
				officeAddress, phoneNumber, faxNumber, businessType, businessItem);

			// 2. ContactPersonInfo 데이터 받기 및 형변환
			String personIdStr = request.getParameter("personId");
			int personId = (personIdStr != null && !personIdStr.isEmpty()) ? Integer.parseInt(personIdStr) : 0;

			String contactName = request.getParameter("contactName");

			// 부서ID와 직위ID 파싱
			String deptIdStr = request.getParameter("departmentId");
			int departmentId = (deptIdStr != null && !deptIdStr.isEmpty()) ? Integer.parseInt(deptIdStr) : 0;

			String posIdStr = request.getParameter("positionId");
			int positionId = (posIdStr != null && !posIdStr.isEmpty()) ? Integer.parseInt(posIdStr) : 0;

			String conPhoneNumber = request.getParameter("conPhoneNumber");
			String mobileNumber = request.getParameter("mobileNumber");
			String email = request.getParameter("email");

			// ContactPersonInfo 객체 생성
			ContactPersonInfo contactInfo = new ContactPersonInfo(
				personId, companyId, contactName, departmentId,
				positionId, conPhoneNumber, mobileNumber, email);

			// 3. Service 호출하여 데이터 저장 (INSERT)
			try {
				// SaveCompanyInfoService 클래스에 save(CompanyInfo, ContactPersonInfo) 메서드가 있어야 함!
				saveService.save(companyInfo, contactInfo);

				// 성공적으로 저장되면 결과 화면으로 이동
				return "/WEB-INF/view/saveResult.jsp";

			} catch (Exception e) {
				e.printStackTrace();
				// 저장 중 에러 발생 시 에러 화면으로 이동
				return "/WEB-INF/view/error.jsp";
			}
		}

		// GET 요청 시 (잘못된 접근) 처리 방식
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}
}