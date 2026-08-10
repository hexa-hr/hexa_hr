package master.service;

import master.model.CompanyInfo;

public class CompanyInfoService {

	// ★ static으로 선언! (서버 메모리에 하나만 존재하여 가짜 DB 역할을 함)
	private static CompanyInfo mockDb = new CompanyInfo();

	// 서버 시작될 때 맨 처음 한 번만 들어가는 기본값 세팅
	static {
		mockDb.setCompanyName("(주)예스폼");
		mockDb.setCeoTitle("대표이사");
		mockDb.setCeoName("이용열");
		mockDb.setBusinessNumber("120-86-50680");
		mockDb.setCorpNumber("110111-275101");
		mockDb.setFoundingDate("2000-01-03");
		mockDb.setHomepage("www.yesform.com");
		mockDb.setZipCode("00133");
		mockDb.setAddress("서울특별시 성동구 성수동1가 14-18 코오롱디지털3차 901호");
		mockDb.setPhone2("1588");
		mockDb.setPhone3("2390");
		mockDb.setFax2("2117");
		mockDb.setFax3("0691");
		mockDb.setBusinessCondition("사업서비스업");
		mockDb.setBusinessType("온라인정보제공");
		mockDb.setManagerName("김동현");
		mockDb.setMgrPhone2("1588");
		mockDb.setMgrPhone3("1588");
		mockDb.setMgrMobile2("1588");
		mockDb.setMgrMobile3("2390");
		mockDb.setMgrEmail("kim_2016@payzon.co.kr");
		mockDb.setAccountNumber("123-123456-12-123");
		mockDb.setAccountHolder("(주)예스폼");
	}

	// 데이터 조회
	public CompanyInfo getCompanyInfo() {
		return mockDb; // 현재 메모리에 있는 상태 그대로 반환
	}

	// 데이터 저장(수정)
	public void updateCompanyInfo(CompanyInfo info) {
		mockDb = info; // 새로 들어온 데이터로 메모리를 통째로 덮어쓰기!
	}
}