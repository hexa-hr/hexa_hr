package wage.model;

import java.util.Date;

// 급여 지급/공제 내역 대장
public class Wage {
	private Integer wageId;
	private Integer employeeId;
	private String wagePeriod;
	private Integer wageTypeId;
	private Long wageValue;
	private Date settlementPeriodStartDate;
	private Date settlementPeriodEndDate;
	private Date wagePaymentDate;

	public Wage(Integer wageId, Integer employeeId, String wagePeriod, Integer wageTypeId, Long wageValue,
		Date settlementPeriodStartDate, Date settlementPeriodEndDate, Date wagePaymentDate) {
		this.wageId = wageId;
		this.employeeId = employeeId;
		this.wagePeriod = wagePeriod;
		this.wageTypeId = wageTypeId;
		this.wageValue = wageValue;
		this.settlementPeriodStartDate = settlementPeriodStartDate;
		this.settlementPeriodEndDate = settlementPeriodEndDate;
		this.wagePaymentDate = wagePaymentDate;
	}

	public Integer getWageId() {
		return wageId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getWagePeriod() {
		return wagePeriod;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public Long getWageValue() {
		return wageValue;
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

}
