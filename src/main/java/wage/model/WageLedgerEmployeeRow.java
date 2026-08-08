package wage.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// 급여대장 상세 화면의 사원별 1행 DTO
public class WageLedgerEmployeeRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private Date hireDate;
	private String departmentName;
	private String positionName;

	// key: wageTypeId, value: 해당 급여항목 금액
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

		// 데이터가 없는 급여항목도 화면에 0으로 표시하기 위해 초기화
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