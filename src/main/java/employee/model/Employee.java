package employee.model;

import java.util.Date;

// 사원 기본 정보
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

	public Employee(Integer employeeId, Integer accountId, Integer companyId, Integer personId, String employmentType,
		String koreanName, String englishName, Date hireDate, Date resignationDate, Integer departmentId,
		Integer positionId, String foreignOrDomestic, String residentNumber1, String residentNumber2, String address,
		String telPhone, String mobile, String email, String sns, String otherDetails, String status) {
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
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public Integer getPersonId() {
		return personId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public Date getResignationDate() {
		return resignationDate;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public String getForeignOrDomestic() {
		return foreignOrDomestic;
	}

	public String getResidentNumber1() {
		return residentNumber1;
	}

	public String getResidentNumber2() {
		return residentNumber2;
	}

	public String getAddress() {
		return address;
	}

	public String getTelPhone() {
		return telPhone;
	}

	public String getMobile() {
		return mobile;
	}

	public String getEmail() {
		return email;
	}

	public String getSns() {
		return sns;
	}

	public String getOtherDetails() {
		return otherDetails;
	}

	public String getStatus() {
		return status;
	}

}
