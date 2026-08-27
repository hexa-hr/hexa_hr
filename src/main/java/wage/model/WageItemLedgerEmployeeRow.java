package wage.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 項目別台帳画面に表示する社員別月次給与情報
public class WageItemLedgerEmployeeRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private Map<String, Long> monthlyValues;
	private Long totalValue;

	public WageItemLedgerEmployeeRow(Integer employeeId, String employmentType, String koreanName,
		String departmentName, String positionName, List<String> months) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.monthlyValues = new LinkedHashMap<>();
		this.totalValue = 0L;

		// 給与データがない月も画面に0として表示するために初期化
		for (String month : months) {
			monthlyValues.put(month, 0L);
		}
	}

	public void addWageValue(String wageMonth, Long wageValue) {
		long value = wageValue == null ? 0L : wageValue;

		Long previousValue = monthlyValues.get(wageMonth);
		if (previousValue == null) {
			previousValue = 0L;
		}

		monthlyValues.put(wageMonth, previousValue + value);
		totalValue += value;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public Map<String, Long> getMonthlyValues() {
		return monthlyValues;
	}

	public Long getTotalValue() {
		return totalValue;
	}

}
