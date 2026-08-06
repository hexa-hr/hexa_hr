package wage.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//항목별 대장 화면에 표시할 사원별 월 급여 정보
public class WageItemLedgerEmployeeRow {

	private Integer employeeId;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private Map<String, Long> monthlyValues;
	private Long totalValue;

	public WageItemLedgerEmployeeRow(Integer employeeId, String koreanName,
		String departmentName, String positionName, List<String> months) {

		this.employeeId = employeeId;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.monthlyValues = new LinkedHashMap<>();
		this.totalValue = 0L;

		// 급여 데이터가 없는 월도 화면에 0으로 표시하기 위해 초기화
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
