package wage.model;

// 급여 자동계산 결과의 급여항목 한 건
public class WagePaymentCalculationItem {

	private Integer wageTypeId;
	private String wageTypeName;
	private String itemType;
	private Long wageValue;

	public WagePaymentCalculationItem(
		Integer wageTypeId,
		String wageTypeName,
		String itemType,
		Long wageValue) {

		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
		this.wageValue = wageValue;
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

	public Long getWageValue() {
		return wageValue;
	}
}