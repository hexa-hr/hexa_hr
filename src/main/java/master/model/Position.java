package master.model;

// 직위/직급
public class Position {
	private Integer positionId;
	private String positionName;

	public Position(Integer positionId, String positionName) {
		this.positionId = positionId;
		this.positionName = positionName;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public String getPositionName() {
		return positionName;
	}

}
