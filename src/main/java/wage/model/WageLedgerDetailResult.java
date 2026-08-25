package wage.model;

import java.util.List;
import java.util.Map;

import master.model.Department;
import master.model.WageTypeOption;

// 給与台帳詳細照会結果DTO
public class WageLedgerDetailResult {

	private WageLedgerSummary summary;
	private List<WageTypeOption> paymentTypes;
	private List<WageTypeOption> deductionTypes;
	private List<WageLedgerEmployeeRow> employeeRows;
	private Map<Integer, Long> itemTotals;
	private Long totalPayment;
	private Long totalDeduction;
	private List<Department> departments;

	public WageLedgerDetailResult(
		WageLedgerSummary summary,
		List<WageTypeOption> paymentTypes,
		List<WageTypeOption> deductionTypes,
		List<WageLedgerEmployeeRow> employeeRows,
		Map<Integer, Long> itemTotals,
		Long totalPayment,
		Long totalDeduction,
		List<Department> departments) {

		this.summary = summary;
		this.paymentTypes = paymentTypes;
		this.deductionTypes = deductionTypes;
		this.employeeRows = employeeRows;
		this.itemTotals = itemTotals;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
		this.departments = departments;
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

	public List<Department> getDepartments() {
		return departments;
	}
}