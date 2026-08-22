package wage.model;

import java.sql.Date;
import java.util.List;

// 급여 자동계산 요청 DTO
public class WagePaymentCalculationRequest {

	private Integer employeeId;
	private String wageMonth;
	private Date settlementStartDate;
	private Date settlementEndDate;
	private List<WagePaymentItemInput> itemInputs;

	public WagePaymentCalculationRequest(
		Integer employeeId,
		String wageMonth,
		Date settlementStartDate,
		Date settlementEndDate,
		List<WagePaymentItemInput> itemInputs) {

		this.employeeId = employeeId;
		this.wageMonth = wageMonth;
		this.settlementStartDate = settlementStartDate;
		this.settlementEndDate = settlementEndDate;
		this.itemInputs = itemInputs;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public Date getSettlementStartDate() {
		return settlementStartDate;
	}

	public Date getSettlementEndDate() {
		return settlementEndDate;
	}

	public List<WagePaymentItemInput> getItemInputs() {
		return itemInputs;
	}
}