package wage.model;

// 項目別台帳照会結果の1行（社員 × 帰属年月単位）
public class WageItemLedgerRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private String wageMonth;
	private Long wageValue;

	public WageItemLedgerRow(Integer employeeId, String employmentType, String koreanName,
		String departmentName, String positionName,
		String wageMonth, Long wageValue) {
		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.positionName = positionName;
		this.wageMonth = wageMonth;
		this.wageValue = wageValue;
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

	public String getWageMonth() {
		return wageMonth;
	}

	public Long getWageValue() {
		return wageValue;
	}
}