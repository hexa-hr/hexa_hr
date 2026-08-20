package wage.model;

// 급여입력 화면 - 저장된 사원 목록 행
public class WagePaymentEmployeeRow {

	private Integer employeeId;
	private String employmentType;
	private String koreanName;
	private String departmentName;
	private Long totalPayment;
	private Long totalDeduction;
	private Long netPayment;

	public WagePaymentEmployeeRow(
		Integer employeeId,
		String employmentType,
		String koreanName,
		String departmentName,
		Long totalPayment,
		Long totalDeduction,
		Long netPayment) {

		this.employeeId = employeeId;
		this.employmentType = employmentType;
		this.koreanName = koreanName;
		this.departmentName = departmentName;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
		this.netPayment = netPayment;
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

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return netPayment;
	}
}