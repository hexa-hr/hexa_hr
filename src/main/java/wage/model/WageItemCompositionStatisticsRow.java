package wage.model;

// 급여항목 구성 통계 조회 결과 한 행
public class WageItemCompositionStatisticsRow {

	private String wageTypeName;
	private String itemType;
	private Long amount;

	public WageItemCompositionStatisticsRow(
		String wageTypeName,
		String itemType,
		Long amount) {

		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
		this.amount = amount;
	}

	public String getWageTypeName() {
		return wageTypeName;
	}

	public String getItemType() {
		return itemType;
	}

	public Long getAmount() {
		return amount;
	}
}