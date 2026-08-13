package wage.model;

import java.util.Date;

// 4대보험 공제내역 화면의 사원별 계산 결과
public class WageInsuranceDeductionDetail {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private Date hireDate;
	private String departmentName;
	private String positionName;

	private Long nationalPensionEmployer;
	private Long nationalPensionEmployee;
	private Long healthInsuranceEmployer;
	private Long healthInsuranceEmployee;
	private Long longTermCareInsuranceEmployer;
	private Long longTermCareInsuranceEmployee;
	private Long employmentInsuranceEmployer;
	private Long employmentInsuranceEmployee;

	public WageInsuranceDeductionDetail(
		Integer employeeId,
		String employmentType,
		String koreanName,
		Date hireDate,
		String departmentName,
		String positionName,
		Long nationalPensionEmployer,
		Long nationalPensionEmployee,
		Long healthInsuranceEmployer,
		Long healthInsuranceEmployee,
		Long longTermCareInsuranceEmployer,
		Long longTermCareInsuranceEmployee,
		Long employmentInsuranceEmployer,
		Long employmentInsuranceEmployee) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.hireDate = hireDate;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.nationalPensionEmployer = nationalPensionEmployer;
		this.nationalPensionEmployee = nationalPensionEmployee;
		this.healthInsuranceEmployer = healthInsuranceEmployer;
		this.healthInsuranceEmployee = healthInsuranceEmployee;
		this.longTermCareInsuranceEmployer = longTermCareInsuranceEmployer;
		this.longTermCareInsuranceEmployee = longTermCareInsuranceEmployee;
		this.employmentInsuranceEmployer = employmentInsuranceEmployer;
		this.employmentInsuranceEmployee = employmentInsuranceEmployee;
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

	public Long getNationalPensionEmployer() {
		return nationalPensionEmployer;
	}

	public Long getNationalPensionEmployee() {
		return nationalPensionEmployee;
	}

	public Long getNationalPensionTotal() {
		return safe(nationalPensionEmployer)
			+ safe(nationalPensionEmployee);
	}

	public Long getHealthInsuranceEmployer() {
		return healthInsuranceEmployer;
	}

	public Long getHealthInsuranceEmployee() {
		return healthInsuranceEmployee;
	}

	public Long getHealthInsuranceTotal() {
		return safe(healthInsuranceEmployer)
			+ safe(healthInsuranceEmployee);
	}

	public Long getLongTermCareInsuranceEmployer() {
		return longTermCareInsuranceEmployer;
	}

	public Long getLongTermCareInsuranceEmployee() {
		return longTermCareInsuranceEmployee;
	}

	public Long getLongTermCareInsuranceTotal() {
		return safe(longTermCareInsuranceEmployer)
			+ safe(longTermCareInsuranceEmployee);
	}

	public Long getEmploymentInsuranceEmployer() {
		return employmentInsuranceEmployer;
	}

	public Long getEmploymentInsuranceEmployee() {
		return employmentInsuranceEmployee;
	}

	public Long getEmploymentInsuranceTotal() {
		return safe(employmentInsuranceEmployer)
			+ safe(employmentInsuranceEmployee);
	}

	public Long getEmployerTotal() {
		return safe(nationalPensionEmployer)
			+ safe(healthInsuranceEmployer)
			+ safe(longTermCareInsuranceEmployer)
			+ safe(employmentInsuranceEmployer);
	}

	public Long getEmployeeTotal() {
		return safe(nationalPensionEmployee)
			+ safe(healthInsuranceEmployee)
			+ safe(longTermCareInsuranceEmployee)
			+ safe(employmentInsuranceEmployee);
	}

	public Long getGrandTotal() {
		return getEmployerTotal() + getEmployeeTotal();
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}