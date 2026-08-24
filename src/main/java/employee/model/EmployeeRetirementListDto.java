package employee.model;

import java.util.Date;

public class EmployeeRetirementListDto {
	private Integer employeeId;
	private String status;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private Date hireDate;
	private Date resignationDate;
	private int yearsOfService;
	private String retirementSettlement;

	// 🌟 팝업창(모달)에 띄워줄 기존 정보 3개 추가!
	private String retirementType;
	private String retirementReason;
	private String contactAfterRetirement;

	public EmployeeRetirementListDto(Integer employeeId, String status, String koreanName,
		String departmentName, String positionName, Date hireDate,
		Date resignationDate, int yearsOfService, String retirementSettlement,
		String retirementType, String retirementReason, String contactAfterRetirement) {
		this.employeeId = employeeId;
		this.status = status;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.hireDate = hireDate;
		this.resignationDate = resignationDate;
		this.yearsOfService = yearsOfService;
		this.retirementSettlement = retirementSettlement;
		this.retirementType = retirementType;
		this.retirementReason = retirementReason;
		this.contactAfterRetirement = contactAfterRetirement;
	}

	// Getter
	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getStatus() {
		return status;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public Date getResignationDate() {
		return resignationDate;
	}

	public int getYearsOfService() {
		return yearsOfService;
	}

	public String getRetirementSettlement() {
		return retirementSettlement;
	}

	// 🌟 추가된 Getter
	public String getRetirementType() {
		return retirementType;
	}

	public String getRetirementReason() {
		return retirementReason;
	}

	public String getContactAfterRetirement() {
		return contactAfterRetirement;
	}
}