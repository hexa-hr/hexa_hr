package wage.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 給与台帳詳細画面の社員別1行DTO
public class WageLedgerEmployeeRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private Date hireDate;
	private String departmentName;
	private String positionName;

	// key: wageTypeId, value: 該当する給与項目の金額
	private Map<Integer, Long> wageValues = new LinkedHashMap<>();

	private Long totalPayment = 0L;
	private Long totalDeduction = 0L;

	public WageLedgerEmployeeRow(Integer employeeId,
		String employmentType, String koreanName,
		Date hireDate, String departmentName,
		String positionName, List<Integer> wageTypeIds) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.hireDate = hireDate;
		this.departmentName = departmentName;
		this.positionName = positionName;

		// データがない給与項目も画面に0として表示するために初期化
		for (Integer wageTypeId : wageTypeIds) {
			wageValues.put(wageTypeId, 0L);
		}
	}

	public void addWageValue(Integer wageTypeId,
		String itemType, Long wageValue) {

		long value = wageValue == null ? 0L : wageValue;

		Long previousValue = wageValues.get(wageTypeId);

		if (previousValue == null) {
			previousValue = 0L;
		}

		wageValues.put(wageTypeId, previousValue + value);

		if ("P".equals(itemType)) {
			totalPayment += value;
		} else if ("D".equals(itemType)) {
			totalDeduction += value;
		}
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

	public Date getHireDate() {
		return hireDate;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public Map<Integer, Long> getWageValues() {
		return wageValues;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return totalPayment - totalDeduction;
	}
}