package employee.model;

import java.util.Date;

public class Employee {
	private Integer employeeId;
	private Integer accountId;
	private Integer companyId;
	private Integer personId;
	private String employmentType;
	private String koreanName;
	private String englishName;
	private Date hireDate;
	private Date resignationDate;
	private Integer departmentId;
	private Integer positionId;
	private String foreignOrDomestic;
	private String residentNumber1;
	private String residentNumber2;
	private String address;
	private String telPhone;
	private String mobile;
	private String email;
	private String sns;
	private String otherDetails;
	private String status;
	// 🌟 추가된 급여(기본급/일급) 컬럼
	private Integer basicPay;

	public Employee(Integer employeeId, Integer accountId, Integer companyId, Integer personId, String employmentType,
		String koreanName, String englishName, Date hireDate, Date resignationDate, Integer departmentId,
		Integer positionId, String foreignOrDomestic, String residentNumber1, String residentNumber2,
		String address, String telPhone, String mobile, String email, String sns, String otherDetails,
		String status, Integer basicPay) { // 🌟 맨 끝에 basicPay 추가!

		this.employeeId = employeeId;
		this.accountId = accountId;
		this.companyId = companyId;
		this.personId = personId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.englishName = englishName;
		this.hireDate = hireDate;
		this.resignationDate = resignationDate;
		this.departmentId = departmentId;
		this.positionId = positionId;
		this.foreignOrDomestic = foreignOrDomestic;
		this.residentNumber1 = residentNumber1;
		this.residentNumber2 = residentNumber2;
		this.address = address;
		this.telPhone = telPhone;
		this.mobile = mobile;
		this.email = email;
		this.sns = sns;
		this.otherDetails = otherDetails;
		this.status = status;
		this.basicPay = basicPay; // 🌟 추가!
	}

	// Getter & Setter
	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public void setKoreanName(String koreanName) {
		this.koreanName = koreanName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(Date resignationDate) {
		this.resignationDate = resignationDate;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public String getForeignOrDomestic() {
		return foreignOrDomestic;
	}

	public void setForeignOrDomestic(String foreignOrDomestic) {
		this.foreignOrDomestic = foreignOrDomestic;
	}

	public String getResidentNumber1() {
		return residentNumber1;
	}

	public void setResidentNumber1(String residentNumber1) {
		this.residentNumber1 = residentNumber1;
	}

	public String getResidentNumber2() {
		return residentNumber2;
	}

	public void setResidentNumber2(String residentNumber2) {
		this.residentNumber2 = residentNumber2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSns() {
		return sns;
	}

	public void setSns(String sns) {
		this.sns = sns;
	}

	public String getOtherDetails() {
		return otherDetails;
	}

	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(Integer basicPay) {
		this.basicPay = basicPay;
	}
}