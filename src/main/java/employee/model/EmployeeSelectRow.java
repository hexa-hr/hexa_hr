package employee.model;

// 사원 선택 화면의 조회 결과 한 행
public class EmployeeSelectRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private String status;

	public EmployeeSelectRow(
		Integer employeeId,
		String employmentType,
		String koreanName,
		String departmentName,
		String positionName,
		String status) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.status = status;
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

	public String getDepartmentName() {
		return departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public String getStatus() {
		return status;
	}
}