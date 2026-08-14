package master.service;

import java.text.SimpleDateFormat;

import master.model.CompanyInfo;
import master.model.ContactPersonInfo;

public class CompanyInfoService {

	// ★ static으로 선언! (서버 메모리에 하나만 존재하여 가짜 DB 역할을 함)
	// 회사 정보와 담당자 정보를 분리된 DTO에 맞게 각각 생성
	private static CompanyInfo mockComp = new CompanyInfo();
	private static ContactPersonInfo mockCont = new ContactPersonInfo();

	// 서버 시작될 때 맨 처음 한 번만 들어가는 기본값 세팅
	static {
		try {
			// 1. 회사 정보 세팅 (새로 만든 CompanyInfo DTO 메서드 이름에 맞춤)
			mockComp.setCompanyId(1);
			mockComp.setCompanyName("(주)예스폼");
			mockComp.setRepresentativeTitle("대표이사");
			mockComp.setRepresentativeName("이용열");
			mockComp.setBusinessNumber("120-86-50680");
			mockComp.setCorporationNumber("110111-275101");

			// 설립일: String이 아닌 java.util.Date 타입이므로 변환해서 넣기
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			mockComp.setEstablishmentDate(sdf.parse("2000-01-03"));

			mockComp.setWebsite("www.yesform.com");
			mockComp.setOfficeAddress("서울특별시 성동구 성수동1가 14"); // 우편번호와 주소 통합
			mockComp.setPhoneNumber("1588-2390");
			mockComp.setFaxNumber("02-2117-0691");
			mockComp.setBusinessType("사업서비스업");
			mockComp.setBusinessItem("온라인정보제공");

			// 2. 담당자 정보 세팅 (새로 만든 ContactPersonInfo DTO 메서드 이름에 맞춤)
			mockCont.setCompId(1);
			mockCont.setContName("김동현");
			// 부서, 직위는 현재 ID(Integer) 타입이므로 가짜 데이터로 1을 넣음
			mockCont.setDeptId(1);
			mockCont.setPosId(1);
			mockCont.setConPhone("1588-1588");
			mockCont.setMobile("010-1588-2390");
			mockCont.setEmail("kim_2016@payzon.co.kr");

			// (급여이체계좌 정보는 현재 DTO 설계에서 제외했으므로 생략)

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 데이터 조회 메서드 1: 회사 정보 가져오기
	public CompanyInfo getCompanyInfo() {
		// 나중에는 이 부분을 DAO를 호출하는 코드로 바꿀 거야! (예: return companyDao.selectById(1);)
		return mockComp;
	}

	// 데이터 조회 메서드 2: 담당자 정보 가져오기
	public ContactPersonInfo getContactPersonInfo() {
		return mockCont;
	}
}