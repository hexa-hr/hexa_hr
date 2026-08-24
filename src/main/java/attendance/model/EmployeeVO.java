package attendance.model;

public class EmployeeVO {
	private int employeeId;
	private String employmentType;
	private String koreanName;
	private String departmentName;
	private String positionName;

	// [추가된 부분] 일당(기본급) 데이터를 담을 변수
	private Long basicPay;

	public EmployeeVO() {
	}

	public EmployeeVO(int employeeId, String employmentType, String koreanName, String departmentName,
			String positionName) {
		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	// [추가된 부분] Getter & Setter
	public Long getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(Long basicPay) {
		this.basicPay = basicPay;
	}
}