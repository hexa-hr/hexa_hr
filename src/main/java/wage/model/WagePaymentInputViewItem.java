package wage.model;

// 급여입력 화면에 표시되는 급여항목 한 건
public class WagePaymentInputViewItem {

	private Integer wageTypeId;
	private String wageTypeName;
	private String itemType;
	private String taxableYn;
	private Long wageValue;

	// 현재 사용 중인 급여항목인지
	private boolean active;

	// 현재 자동계산에 포함 가능한 급여항목인지
	private boolean calculable;

	public WagePaymentInputViewItem(
		Integer wageTypeId,
		String wageTypeName,
		String itemType,
		String taxableYn,
		Long wageValue,
		boolean active,
		boolean calculable) {

		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
		this.taxableYn = taxableYn;
		this.wageValue = wageValue;
		this.active = active;
		this.calculable = calculable;
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

	public String getTaxableYn() {
		return taxableYn;
	}

	public Long getWageValue() {
		return wageValue;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCalculable() {
		return calculable;
	}
}