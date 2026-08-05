package master.model;

//급여 항목 구분 (수당/공제)
public class WageType {
	private Integer wageTypeId;
	private String wageTypeName;
	private String itemType; // 'P' (지급) / 'D' (공제)
	private String taxableYn; // 'Y' / 'N' (과세 여부)
	private Long taxFreeLimit; // 비과세 한도

	public WageType() {
	}

	public WageType(Integer wageTypeId, String wageTypeName, String itemType, String taxableYn, Long taxFreeLimit) {
		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
		this.taxableYn = taxableYn;
		this.taxFreeLimit = taxFreeLimit;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public void setWageTypeId(Integer wageTypeId) {
		this.wageTypeId = wageTypeId;
	}

	public String getWageTypeName() {
		return wageTypeName;
	}

	public void setWageTypeName(String wageTypeName) {
		this.wageTypeName = wageTypeName;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getTaxableYn() {
		return taxableYn;
	}

	public void setTaxableYn(String taxableYn) {
		this.taxableYn = taxableYn;
	}

	public Long getTaxFreeLimit() {
		return taxFreeLimit;
	}

	public void setTaxFreeLimit(Long taxFreeLimit) {
		this.taxFreeLimit = taxFreeLimit;
	}
}