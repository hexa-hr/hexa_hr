package wage.model;

import java.util.List;

// 급여항목 구성 통계 조회 결과
public class WageItemCompositionStatisticsResult {

	// 도넛 차트용 전체 항목
	private List<WageItemCompositionStatisticsDetail> paymentItems;
	private List<WageItemCompositionStatisticsDetail> deductionItems;

	// 표 출력용 항목
	// 10개 이상이면 9개 + 그 외(N)
	private List<WageItemCompositionStatisticsDetail> tablePaymentItems;
	private List<WageItemCompositionStatisticsDetail> tableDeductionItems;

	private Long totalPayment;
	private Long totalDeduction;

	public WageItemCompositionStatisticsResult(
		List<WageItemCompositionStatisticsDetail> paymentItems,
		List<WageItemCompositionStatisticsDetail> deductionItems,
		Long totalPayment,
		Long totalDeduction) {

		this(
			paymentItems,
			deductionItems,
			paymentItems,
			deductionItems,
			totalPayment,
			totalDeduction);
	}

	public WageItemCompositionStatisticsResult(
		List<WageItemCompositionStatisticsDetail> paymentItems,
		List<WageItemCompositionStatisticsDetail> deductionItems,
		List<WageItemCompositionStatisticsDetail> tablePaymentItems,
		List<WageItemCompositionStatisticsDetail> tableDeductionItems,
		Long totalPayment,
		Long totalDeduction) {

		this.paymentItems = paymentItems;
		this.deductionItems = deductionItems;
		this.tablePaymentItems = tablePaymentItems;
		this.tableDeductionItems = tableDeductionItems;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public List<WageItemCompositionStatisticsDetail> getPaymentItems() {
		return paymentItems;
	}

	public List<WageItemCompositionStatisticsDetail> getDeductionItems() {
		return deductionItems;
	}

	public List<WageItemCompositionStatisticsDetail> getTablePaymentItems() {
		return tablePaymentItems;
	}

	public List<WageItemCompositionStatisticsDetail> getTableDeductionItems() {
		return tableDeductionItems;
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