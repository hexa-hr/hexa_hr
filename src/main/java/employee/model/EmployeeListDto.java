package employee.model;

import java.util.Date;

// 사원 목록 조회용 DTO (부서명, 직위명 포함)
public class EmployeeListDto {
	private Integer employeeId; // 사원번호
	private String koreanName; // 이름
	private String departmentName; // 부서명 (JOIN)
	private String positionName; // 직위명 (JOIN)
	private String employmentType; // 고용형태 (정사원, 계약직 등)
	private String status; // 재직상태 (재직, 퇴사 등)
	private Date hireDate; // 입사일

	public EmployeeListDto(Integer employeeId, String koreanName, String departmentName,
		String positionName, String employmentType, String status, Date hireDate) {
		this.employeeId = employeeId;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.employmentType = employmentType;
		this.status = status;
		this.hireDate = hireDate;
	}

	// ⭐ Getter/Setter (단축키로 생성해도 돼!)
	public Integer getEmployeeId() {
		return employeeId;
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

	public String getEmploymentType() {
		return employmentType;
	}

	public String getStatus() {
		return status;
	}

	public Date getHireDate() {
		return hireDate;
	}
}