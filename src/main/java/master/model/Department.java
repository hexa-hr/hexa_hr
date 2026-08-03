package master.model;

// 부서
public class Department {
	private Integer departmentId;
	private String departmentName;

	public Department(Integer departmentId, String departmentName) {
		this.departmentId = departmentId;
		this.departmentName = departmentName;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

}
