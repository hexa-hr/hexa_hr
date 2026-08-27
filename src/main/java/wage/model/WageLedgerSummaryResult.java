package wage.model;

import java.util.List;

// 給与台帳の給与回次一覧照会結果DTO
public class WageLedgerSummaryResult {

	private List<WageLedgerSummary> summaries;
	private Long totalPayment;
	private Long totalDeduction;

	public WageLedgerSummaryResult(
		List<WageLedgerSummary> summaries,
		Long totalPayment,
		Long totalDeduction) {

		this.summaries = summaries;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public List<WageLedgerSummary> getSummaries() {
		return summaries;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		long payment = totalPayment == null ? 0L : totalPayment;
		long deduction = totalDeduction == null ? 0L : totalDeduction;

		return payment - deduction;
	}
}