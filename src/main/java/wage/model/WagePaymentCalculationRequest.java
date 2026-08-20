package wage.model;

import java.util.List;

// 급여 자동계산 요청 DTO
public class WagePaymentCalculationRequest {

	private Integer employeeId;
	private String wageMonth;
	private List<WagePaymentItemInput> itemInputs;

	public WagePaymentCalculationRequest(
		Integer employeeId,
		String wageMonth,
		List<WagePaymentItemInput> itemInputs) {

		this.employeeId = employeeId;
		this.wageMonth = wageMonth;
		this.itemInputs = itemInputs;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public List<WagePaymentItemInput> getItemInputs() {
		return itemInputs;
	}
}