package employee.model;

// 부양 가족
public class Dependents {
	private Integer dependentId;
	private Integer employeeId;
	private String relationship;
	private String parentsName;
	private String foreignOrDomestic1;
	private String parentsNumber1;
	private String parentsNumber2;

	public Dependents(Integer dependentId, Integer employeeId, String relationship, String parentsName,
		String foreignOrDomestic1, String parentsNumber1, String parentsNumber2) {
		this.dependentId = dependentId;
		this.employeeId = employeeId;
		this.relationship = relationship;
		this.parentsName = parentsName;
		this.foreignOrDomestic1 = foreignOrDomestic1;
		this.parentsNumber1 = parentsNumber1;
		this.parentsNumber2 = parentsNumber2;
	}

	public Integer getDependentId() {
		return dependentId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getRelationship() {
		return relationship;
	}

	public String getParentsName() {
		return parentsName;
	}

	public String getForeignOrDomestic1() {
		return foreignOrDomestic1;
	}

	public String getParentsNumber1() {
		return parentsNumber1;
	}

	public String getParentsNumber2() {
		return parentsNumber2;
	}

}
