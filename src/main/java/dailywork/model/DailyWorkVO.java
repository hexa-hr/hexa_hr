package dailywork.model;

import java.sql.Date;

public class DailyWorkVO {
	private Integer workId;
	private Integer employeeId;
	private Date workDate;
	private Integer fieldOrProjectId;
	private Long dailyWage;
	private Double paymentRate;
	private Long incomeTax;
	private Long localTax;
	private Long actualPayment;

	public DailyWorkVO(Integer workId, Integer employeeId, Date workDate, Integer fieldOrProjectId, Long dailyWage,
			Double paymentRate, Long incomeTax, Long localTax, Long actualPayment) {
		this.workId = workId;
		this.employeeId = employeeId;
		this.workDate = workDate;
		this.fieldOrProjectId = fieldOrProjectId;
		this.dailyWage = dailyWage;
		this.paymentRate = paymentRate;
		this.incomeTax = incomeTax;
		this.localTax = localTax;
		this.actualPayment = actualPayment;
	}

	public Integer getWorkId() {
		return workId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public Integer getFieldOrProjectId() {
		return fieldOrProjectId;
	}

	public Long getDailyWage() {
		return dailyWage;
	}

	public Double getPaymentRate() {
		return paymentRate;
	}

	public Long getIncomeTax() {
		return incomeTax;
	}

	public Long getLocalTax() {
		return localTax;
	}

	public Long getActualPayment() {
		return actualPayment;
	}
}