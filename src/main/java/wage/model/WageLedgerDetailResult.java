package wage.model;

import java.util.List;
import java.util.Map;

import master.model.WageTypeOption;

// 급여대장 상세 조회 결과 DTO
public class WageLedgerDetailResult {

	private WageLedgerSummary summary;
	private List<WageTypeOption> paymentTypes;
	private List<WageTypeOption> deductionTypes;
	private List<WageLedgerEmployeeRow> employeeRows;
	private Map<Integer, Long> itemTotals;

	public WageLedgerDetailResult(
		WageLedgerSummary summary,
		List<WageTypeOption> paymentTypes,
		List<WageTypeOption> deductionTypes,
		List<WageLedgerEmployeeRow> employeeRows,
		Map<Integer, Long> itemTotals) {

		this.summary = summary;
		this.paymentTypes = paymentTypes;
		this.deductionTypes = deductionTypes;
		this.employeeRows = employeeRows;
		this.itemTotals = itemTotals;
	}

	public WageLedgerSummary getSummary() {
		return summary;
	}

	public List<WageTypeOption> getPaymentTypes() {
		return paymentTypes;
	}

	public List<WageTypeOption> getDeductionTypes() {
		return deductionTypes;
	}

	public List<WageLedgerEmployeeRow> getEmployeeRows() {
		return employeeRows;
	}

	public Map<Integer, Long> getItemTotals() {
		return itemTotals;
	}
}