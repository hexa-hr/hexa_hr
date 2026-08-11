package wage.model;

import java.util.List;
import java.util.Map;

//항목별 대장 화면에 전달할 전체 조회 결과
public class WageItemLedgerResult {

	private List<String> months;
	private List<WageItemLedgerEmployeeRow> employeeRows;
	private Map<String, Long> monthlyTotals;
	private Long grandTotal;

	public WageItemLedgerResult(List<String> months,
		List<WageItemLedgerEmployeeRow> employeeRows,
		Map<String, Long> monthlyTotals, Long grandTotal) {

		this.months = months;
		this.employeeRows = employeeRows;
		this.monthlyTotals = monthlyTotals;
		this.grandTotal = grandTotal;
	}

	public List<String> getMonths() {
		return months;
	}

	public List<WageItemLedgerEmployeeRow> getEmployeeRows() {
		return employeeRows;
	}

	public Map<String, Long> getMonthlyTotals() {
		return monthlyTotals;
	}

	public Long getGrandTotal() {
		return grandTotal;
	}
}
