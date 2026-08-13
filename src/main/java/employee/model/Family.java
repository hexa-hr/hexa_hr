package employee.model;

public class Family {
	private Integer employeeId; // 누구의 가족인지 식별할 사원번호
	private String relation;
	private String name;
	private String type;

	public Family(Integer employeeId, String relation, String name, String type) {
		this.employeeId = employeeId;
		this.relation = relation;
		this.name = name;
		this.type = type;
	}

	// Getter 메서드들... (생략)
	public String getRelation() {
		return relation;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}