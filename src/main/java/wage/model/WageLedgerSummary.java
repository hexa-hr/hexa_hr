package wage.model;

import java.util.Date;

// 급여대장 급여차수 목록 조회 DTO
public class WageLedgerSummary {

	private String wageMonth;
	private String wagePeriod;
	private Date settlementPeriodStartDate;
	private Date settlementPeriodEndDate;
	private Date wagePaymentDate;
	private Integer employeeCount;
	private Long totalPayment;
	private Long totalDeduction;

	public WageLedgerSummary(String wageMonth, String wagePeriod,
		Date settlementPeriodStartDate, Date settlementPeriodEndDate,
		Date wagePaymentDate, Integer employeeCount,
		Long totalPayment, Long totalDeduction) {

		this.wageMonth = wageMonth;
		this.wagePeriod = wagePeriod;
		this.settlementPeriodStartDate = settlementPeriodStartDate;
		this.settlementPeriodEndDate = settlementPeriodEndDate;
		this.wagePaymentDate = wagePaymentDate;
		this.employeeCount = employeeCount;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public String getWagePeriod() {
		return wagePeriod;
	}

	public Date getSettlementPeriodStartDate() {
		return settlementPeriodStartDate;
	}

	public Date getSettlementPeriodEndDate() {
		return settlementPeriodEndDate;
	}

	public Date getWagePaymentDate() {
		return wagePaymentDate;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
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