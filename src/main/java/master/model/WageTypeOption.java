package master.model;

//항목별 대장의 급여항목 선택 목록
public class WageTypeOption {

	private Integer wageTypeId;
	private String wageTypeName;
	private String itemType;

	public WageTypeOption(Integer wageTypeId,
		String wageTypeName, String itemType) {
		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public String getWageTypeName() {
		return wageTypeName;
	}

	public String getItemType() {
		return itemType;
	}
}
