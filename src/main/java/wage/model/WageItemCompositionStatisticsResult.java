package wage.model;

import java.util.List;

// 급여항목 구성 통계 조회 결과
public class WageItemCompositionStatisticsResult {

	private List<WageItemCompositionStatisticsDetail> paymentItems;
	private List<WageItemCompositionStatisticsDetail> deductionItems;

	private Long totalPayment;
	private Long totalDeduction;

	public WageItemCompositionStatisticsResult(
		List<WageItemCompositionStatisticsDetail> paymentItems,
		List<WageItemCompositionStatisticsDetail> deductionItems,
		Long totalPayment,
		Long totalDeduction) {

		this.paymentItems = paymentItems;
		this.deductionItems = deductionItems;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public List<WageItemCompositionStatisticsDetail> getPaymentItems() {
		return paymentItems;
	}

	public List<WageItemCompositionStatisticsDetail> getDeductionItems() {
		return deductionItems;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return totalPayment - totalDeduction;
	}

	public boolean isHasData() {
		return !paymentItems.isEmpty()
			|| !deductionItems.isEmpty();
	}
}