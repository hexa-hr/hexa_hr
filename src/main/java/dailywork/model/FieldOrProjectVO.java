package dailywork.model;

public class FieldOrProjectVO {
	private Integer fieldOrProjectId;
	private String projectName;
	private String displayYn;

	public FieldOrProjectVO(Integer fieldOrProjectId, String projectName, String displayYn) {
		this.fieldOrProjectId = fieldOrProjectId;
		this.projectName = projectName;
		this.displayYn = displayYn;
	}

	public Integer getFieldOrProjectId() {
		return fieldOrProjectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getDisplayYn() {
		return displayYn;
	}
}