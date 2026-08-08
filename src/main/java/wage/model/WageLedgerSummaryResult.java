package wage.model;

import java.util.List;

// 급여대장 급여차수 목록 조회 결과 DTO
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