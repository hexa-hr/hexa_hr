package wage.model;

import java.util.Date;

// 4대보험 공제내역 조회 결과 한 행 (사원 단위)
public class WageInsuranceDeductionRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private Date hireDate;
	private String departmentName;
	private String positionName;

	private Long nationalPension;
	private Long healthInsurance;
	private Long longTermCareInsurance;
	private Long employmentInsurance;

	public WageInsuranceDeductionRow(
		Integer employeeId,
		String employmentType,
		String koreanName,
		Date hireDate,
		String departmentName,
		String positionName,
		Long nationalPension,
		Long healthInsurance,
		Long longTermCareInsurance,
		Long employmentInsurance) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.hireDate = hireDate;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.nationalPension = nationalPension;
		this.healthInsurance = healthInsurance;
		this.longTermCareInsurance = longTermCareInsurance;
		this.employmentInsurance = employmentInsurance;
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

	public Long getNationalPension() {
		return nationalPension;
	}

	public Long getHealthInsurance() {
		return healthInsurance;
	}

	public Long getLongTermCareInsurance() {
		return longTermCareInsurance;
	}

	public Long getEmploymentInsurance() {
		return employmentInsurance;
	}
}