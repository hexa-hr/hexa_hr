package wage.model;

// 급여항목 구성 통계 화면 표시용 한 행
public class WageItemCompositionStatisticsDetail {

	private String wageTypeName;
	private String itemType;
	private Long amount;
	private double compositionRate;

	public WageItemCompositionStatisticsDetail(
		String wageTypeName,
		String itemType,
		Long amount,
		double compositionRate) {

		this.wageTypeName = wageTypeName;
		this.itemType = itemType;
		this.amount = amount;
		this.compositionRate = compositionRate;
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

	public double getCompositionRate() {
		return compositionRate;
	}
}