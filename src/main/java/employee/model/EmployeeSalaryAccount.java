package employee.model;

//사원 급여 계좌 및 정산/지급일 설정
public class EmployeeSalaryAccount {
	private Integer employeeId;
	private Integer salaryCalculation1; // 정산 시작일 (Integer)
	private Integer salaryCalculation2; // 정산 종료일 (Integer)
	private Integer salaryPaymentDate; // 급여 지급일 (Integer)
	private String calc1MonthType; // 신규
	private String calc2MonthType; // 신규
	private String paymentMonthType; // 신규

	public EmployeeSalaryAccount() {
	}

	public EmployeeSalaryAccount(Integer employeeId, Integer salaryCalculation1, Integer salaryCalculation2,
			Integer salaryPaymentDate, String calc1MonthType, String calc2MonthType, String paymentMonthType) {
		this.employeeId = employeeId;
		this.salaryCalculation1 = salaryCalculation1;
		this.salaryCalculation2 = salaryCalculation2;
		this.salaryPaymentDate = salaryPaymentDate;
		this.calc1MonthType = calc1MonthType;
		this.calc2MonthType = calc2MonthType;
		this.paymentMonthType = paymentMonthType;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getSalaryCalculation1() {
		return salaryCalculation1;
	}

	public void setSalaryCalculation1(Integer salaryCalculation1) {
		this.salaryCalculation1 = salaryCalculation1;
	}

	public Integer getSalaryCalculation2() {
		return salaryCalculation2;
	}

	public void setSalaryCalculation2(Integer salaryCalculation2) {
		this.salaryCalculation2 = salaryCalculation2;
	}

	public Integer getSalaryPaymentDate() {
		return salaryPaymentDate;
	}

	public void setSalaryPaymentDate(Integer salaryPaymentDate) {
		this.salaryPaymentDate = salaryPaymentDate;
	}

	public String getCalc1MonthType() {
		return calc1MonthType;
	}

	public void setCalc1MonthType(String calc1MonthType) {
		this.calc1MonthType = calc1MonthType;
	}

	public String getCalc2MonthType() {
		return calc2MonthType;
	}

	public void setCalc2MonthType(String calc2MonthType) {
		this.calc2MonthType = calc2MonthType;
	}

	public String getPaymentMonthType() {
		return paymentMonthType;
	}

	public void setPaymentMonthType(String paymentMonthType) {
		this.paymentMonthType = paymentMonthType;
	}
}