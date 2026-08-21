package wage.model;

import java.util.List;

// 급여입력 화면 자동계산 결과
public class WagePaymentAutoCalculationResult {

	private List<WagePaymentInputViewItem> wageItems;

	private Long totalPayment;
	private Long totalDeduction;
	private Long netPayment;

	public WagePaymentAutoCalculationResult(
		List<WagePaymentInputViewItem> wageItems,
		Long totalPayment,
		Long totalDeduction,
		Long netPayment) {

		this.wageItems = wageItems;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
		this.netPayment = netPayment;
	}

	public List<WagePaymentInputViewItem> getWageItems() {
		return wageItems;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return netPayment;
	}
}