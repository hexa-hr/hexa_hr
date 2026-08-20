package dailywork.model;

import java.util.List;

// 일용직 급여입력용 근무기록 및 합계
public class DailyWorkPayrollResult {

	private List<DailyWorkPayrollRow> workRows;
	private Long totalPayment;
	private Long totalIncomeTax;
	private Long totalLocalTax;

	public DailyWorkPayrollResult(
		List<DailyWorkPayrollRow> workRows,
		Long totalPayment,
		Long totalIncomeTax,
		Long totalLocalTax) {

		this.workRows = workRows;
		this.totalPayment = totalPayment;
		this.totalIncomeTax = totalIncomeTax;
		this.totalLocalTax = totalLocalTax;
	}

	public List<DailyWorkPayrollRow> getWorkRows() {
		return workRows;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalIncomeTax() {
		return totalIncomeTax;
	}

	public Long getTotalLocalTax() {
		return totalLocalTax;
	}
}