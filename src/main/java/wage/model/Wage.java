package wage.model;

import java.util.Date;

// 급여 지급/공제 내역 대장
public class Wage {
	private Integer wageId;
	private Integer employeeId;
	private String wagePeriod;
	private String wageMonth; // [추가] 귀속연월 'YYYY-MM'
	private Integer wageTypeId;
	private Long wageValue;
	private Date settlementPeriodStartDate;
	private Date settlementPeriodEndDate;
	private Date wagePaymentDate;

	public Wage() {
	}

	public Wage(Integer wageId, Integer employeeId, String wagePeriod, String wageMonth, Integer wageTypeId,
			Long wageValue, Date settlementPeriodStartDate, Date settlementPeriodEndDate, Date wagePaymentDate) {
		this.wageId = wageId;
		this.employeeId = employeeId;
		this.wagePeriod = wagePeriod;
		this.wageMonth = wageMonth;
		this.wageTypeId = wageTypeId;
		this.wageValue = wageValue;
		this.settlementPeriodStartDate = settlementPeriodStartDate;
		this.settlementPeriodEndDate = settlementPeriodEndDate;
		this.wagePaymentDate = wagePaymentDate;
	}

	public Integer getWageId() {
		return wageId;
	}

	public void setWageId(Integer wageId) {
		this.wageId = wageId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getWagePeriod() {
		return wagePeriod;
	}

	public void setWagePeriod(String wagePeriod) {
		this.wagePeriod = wagePeriod;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public void setWageMonth(String wageMonth) {
		this.wageMonth = wageMonth;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public void setWageTypeId(Integer wageTypeId) {
		this.wageTypeId = wageTypeId;
	}

	public Long getWageValue() {
		return wageValue;
	}

	public void setWageValue(Long wageValue) {
		this.wageValue = wageValue;
	}

	public Date getSettlementPeriodStartDate() {
		return settlementPeriodStartDate;
	}

	public void setSettlementPeriodStartDate(Date settlementPeriodStartDate) {
		this.settlementPeriodStartDate = settlementPeriodStartDate;
	}

	public Date getSettlementPeriodEndDate() {
		return settlementPeriodEndDate;
	}

	public void setSettlementPeriodEndDate(Date settlementPeriodEndDate) {
		this.settlementPeriodEndDate = settlementPeriodEndDate;
	}

	public Date getWagePaymentDate() {
		return wagePaymentDate;
	}

	public void setWagePaymentDate(Date wagePaymentDate) {
		this.wagePaymentDate = wagePaymentDate;
	}
}