package wage.model;

import java.sql.Date;

// 신규 급여차수 기본 정산기간 / 지급일
public class WagePaymentPeriodDefault {

	private Date settlementStartDate;
	private Date settlementEndDate;
	private Date wagePaymentDate;

	public WagePaymentPeriodDefault(
		Date settlementStartDate,
		Date settlementEndDate,
		Date wagePaymentDate) {

		this.settlementStartDate = settlementStartDate;
		this.settlementEndDate = settlementEndDate;
		this.wagePaymentDate = wagePaymentDate;
	}

	public Date getSettlementStartDate() {
		return settlementStartDate;
	}

	public Date getSettlementEndDate() {
		return settlementEndDate;
	}

	public Date getWagePaymentDate() {
		return wagePaymentDate;
	}
}