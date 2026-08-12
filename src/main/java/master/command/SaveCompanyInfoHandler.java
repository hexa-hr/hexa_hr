package master.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import master.model.CompanyInfo;
import master.service.CompanyInfoService;
import mvc.command.CommandHandler;

public class SaveCompanyInfoHandler implements CommandHandler {

	private CompanyInfoService companyService = new CompanyInfoService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("POST")) {
			req.setCharacterEncoding("utf-8");

			CompanyInfo newInfo = new CompanyInfo();

			// ★ 생략했던 파라미터들을 전부 가져와서 바구니에 담아주는 필수 코드입니다.
			newInfo.setCompanyName(req.getParameter("companyName"));
			newInfo.setCeoTitle(req.getParameter("ceoTitle"));
			newInfo.setCeoName(req.getParameter("ceoName"));
			newInfo.setBusinessNumber(req.getParameter("businessNumber"));
			newInfo.setCorpNumber(req.getParameter("corpNumber"));
			newInfo.setFoundingDate(req.getParameter("foundingDate"));
			newInfo.setHomepage(req.getParameter("homepage"));
			newInfo.setZipCode(req.getParameter("zipCode"));
			newInfo.setAddress(req.getParameter("address"));
			newInfo.setPhone2(req.getParameter("phone2"));
			newInfo.setPhone3(req.getParameter("phone3"));
			newInfo.setFax2(req.getParameter("fax2"));
			newInfo.setFax3(req.getParameter("fax3"));
			newInfo.setBusinessCondition(req.getParameter("businessCondition"));
			newInfo.setBusinessType(req.getParameter("businessType"));
			newInfo.setManagerName(req.getParameter("managerName"));
			newInfo.setMgrPhone2(req.getParameter("mgrPhone2"));
			newInfo.setMgrPhone3(req.getParameter("mgrPhone3"));
			newInfo.setMgrMobile2(req.getParameter("mgrMobile2"));
			newInfo.setMgrMobile3(req.getParameter("mgrMobile3"));
			newInfo.setMgrEmail(req.getParameter("mgrEmail"));
			newInfo.setAccountNumber(req.getParameter("accountNumber"));
			newInfo.setAccountHolder(req.getParameter("accountHolder"));

			CompanyInfo oldInfo = companyService.getCompanyInfo();

			// 기존 파일명 유지 세팅
			newInfo.setLogoFileName(oldInfo.getLogoFileName());
			newInfo.setStampFileName(oldInfo.getStampFileName());

			// 1. 로고 이미지 처리
			Part logoPart = req.getPart("logoFile");
			String deleteLogoFlag = req.getParameter("deleteLogo");

			if (logoPart != null && logoPart.getSize() > 0) {
				// 새 파일이 등록된 경우
				String fileName = getFileName(logoPart);
				logoPart.write("C:\\upload\\" + fileName); // C 드라이브 upload 폴더에 저장
				newInfo.setLogoFileName(fileName);
			} else if ("true".equals(deleteLogoFlag)) {
				// [삭제] 버튼을 눌러서 지운 경우
				newInfo.setLogoFileName(null);
			}

			// 2. 도장 이미지 처리
			Part stampPart = req.getPart("stampFile");
			String deleteStampFlag = req.getParameter("deleteStamp");

			if (stampPart != null && stampPart.getSize() > 0) {
				String fileName = getFileName(stampPart);
				stampPart.write("C:\\upload\\" + fileName);
				newInfo.setStampFileName(fileName);
			} else if ("true".equals(deleteStampFlag)) {
				newInfo.setStampFileName(null);
			}

			// 가짜 DB 업데이트
			companyService.updateCompanyInfo(newInfo);

			req.setAttribute("alertMsg", "저장되었습니다.");
			return "/WEB-INF/view/saveResult.jsp";
		}
		return null;
	}

	// Part에서 파일명을 추출하는 메서드
	private String getFileName(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}