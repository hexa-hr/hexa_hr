package wage.model;

import java.util.List;

// 급여 자동계산 결과 DTO
public class WagePaymentCalculationResult {

	private List<WagePaymentCalculationItem> paymentItems;
	private List<WagePaymentCalculationItem> deductionItems;

	private Long totalPayment;
	private Long taxFreeAmount;
	private Long monthlyRemuneration;
	private Long totalDeduction;
	private Long netPayment;

	public WagePaymentCalculationResult(
		List<WagePaymentCalculationItem> paymentItems,
		List<WagePaymentCalculationItem> deductionItems,
		Long totalPayment,
		Long taxFreeAmount,
		Long monthlyRemuneration,
		Long totalDeduction,
		Long netPayment) {

		this.paymentItems = paymentItems;
		this.deductionItems = deductionItems;
		this.totalPayment = totalPayment;
		this.taxFreeAmount = taxFreeAmount;
		this.monthlyRemuneration = monthlyRemuneration;
		this.totalDeduction = totalDeduction;
		this.netPayment = netPayment;
	}

	public List<WagePaymentCalculationItem> getPaymentItems() {
		return paymentItems;
	}

	public List<WagePaymentCalculationItem> getDeductionItems() {
		return deductionItems;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTaxFreeAmount() {
		return taxFreeAmount;
	}

	public Long getMonthlyRemuneration() {
		return monthlyRemuneration;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return netPayment;
	}
}