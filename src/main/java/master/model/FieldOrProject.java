package master.model;

// 일용직 현장/프로젝트
public class FieldOrProject {
	private Integer fieldOrProjectId;
	private String name;

	public FieldOrProject(Integer fieldOrProjectId, String name) {
		this.fieldOrProjectId = fieldOrProjectId;
		this.name = name;
	}

	public Integer getFieldOrProjectId() {
		return fieldOrProjectId;
	}

	public String getName() {
		return name;
	}

}
