package wage.model;

import java.util.Date;

// 급여대장 상세 조회용 원본 DTO
public class WageLedgerDetailRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private Date hireDate;
	private String departmentName;
	private String positionName;
	private Integer wageTypeId;
	private Long wageValue;

	public WageLedgerDetailRow(Integer employeeId,
		String employmentType, String koreanName,
		Date hireDate, String departmentName,
		String positionName, Integer wageTypeId,
		Long wageValue) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.hireDate = hireDate;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.wageTypeId = wageTypeId;
		this.wageValue = wageValue;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public Long getWageValue() {
		return wageValue;
	}
}