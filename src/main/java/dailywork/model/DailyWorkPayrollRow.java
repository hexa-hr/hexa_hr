package dailywork.model;

import java.sql.Date;

// 일용직 급여입력 화면에 표시할 근무기록 한 건
public class DailyWorkPayrollRow {

	private Integer workId;
	private Date workDate;
	private Long dailyWage;
	private Double paymentRate;
	private Long paymentAmount;
	private Long incomeTax;
	private Long localTax;

	public DailyWorkPayrollRow(
		Integer workId,
		Date workDate,
		Long dailyWage,
		Double paymentRate,
		Long paymentAmount,
		Long incomeTax,
		Long localTax) {

		this.workId = workId;
		this.workDate = workDate;
		this.dailyWage = dailyWage;
		this.paymentRate = paymentRate;
		this.paymentAmount = paymentAmount;
		this.incomeTax = incomeTax;
		this.localTax = localTax;
	}

	public Integer getWorkId() {
		return workId;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public Long getDailyWage() {
		return dailyWage;
	}

	public Double getPaymentRate() {
		return paymentRate;
	}

	public Long getPaymentAmount() {
		return paymentAmount;
	}

	public Long getIncomeTax() {
		return incomeTax;
	}

	public Long getLocalTax() {
		return localTax;
	}
}