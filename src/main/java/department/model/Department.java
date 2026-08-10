package department.model;

public class Department {
	private int departmentId;
	private String departmentName;

	// 생성자 (DAO에서 데이터를 바구니에 담을 때 사용)
	public Department(int departmentId, String departmentName) {
		this.departmentId = departmentId;
		this.departmentName = departmentName;
	}

	// JSP가 ${dept.departmentId}를 부를 때 실행되는 메서드! (이름이 완벽히 맞아야 함)
	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	// JSP가 ${dept.departmentName}을 부를 때 실행되는 메서드!
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}