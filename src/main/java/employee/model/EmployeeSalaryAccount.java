package employee.model;

// 사원 급여 계좌
public class EmployeeSalaryAccount {
	private Integer accountId;
	private Integer companyId;
	private String bankName;
	private String accountNumber;
	private String depositStocks;
	private Integer salaryCalculation1;
	private Integer salaryCalculation2;
	private Integer salaryPaymentDate;
	private String calc1MonthType;
	private String calc2MonthType;
	private String paymentMonthType;

	// 1. 기본 생성자 추가 (빈 상자 만들기)
	public EmployeeSalaryAccount() {}

	// 기존에 네가 만들어둔 전체 필드 생성자 (유지)
	public EmployeeSalaryAccount(Integer accountId, Integer companyId, String bankName, String accountNumber,
		String depositStocks, Integer salaryCalculation1, Integer salaryCalculation2, Integer salaryPaymentDate,
		String calc1MonthType, String calc2MonthType, String paymentMonthType) {
		this.accountId = accountId;
		this.companyId = companyId;
		this.bankName = bankName;
		this.accountNumber = accountNumber;
		this.depositStocks = depositStocks;
		this.salaryCalculation1 = salaryCalculation1;
		this.salaryCalculation2 = salaryCalculation2;
		this.salaryPaymentDate = salaryPaymentDate;
		this.calc1MonthType = calc1MonthType;
		this.calc2MonthType = calc2MonthType;
		this.paymentMonthType = paymentMonthType;
	}

	// 기존 Getter들 (유지)
	public Integer getAccountId() {
		return accountId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public String getBankName() {
		return bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getDepositStocks() {
		return depositStocks;
	}

	public Integer getSalaryCalculation1() {
		return salaryCalculation1;
	}

	public Integer getSalaryCalculation2() {
		return salaryCalculation2;
	}

	public Integer getSalaryPaymentDate() {
		return salaryPaymentDate;
	}

	public String getCalc1MonthType() {
		return calc1MonthType;
	}

	public String getCalc2MonthType() {
		return calc2MonthType;
	}

	public String getPaymentMonthType() {
		return paymentMonthType;
	}

	// 2. Setter 메서드들 추가 (데이터를 집어넣기 위해 필수!)
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setDepositStocks(String depositStocks) {
		this.depositStocks = depositStocks;
	}

	public void setSalaryCalculation1(Integer salaryCalculation1) {
		this.salaryCalculation1 = salaryCalculation1;
	}

	public void setSalaryCalculation2(Integer salaryCalculation2) {
		this.salaryCalculation2 = salaryCalculation2;
	}

	public void setSalaryPaymentDate(Integer salaryPaymentDate) {
		this.salaryPaymentDate = salaryPaymentDate;
	}

	public void setCalc1MonthType(String calc1MonthType) {
		this.calc1MonthType = calc1MonthType;
	}

	public void setCalc2MonthType(String calc2MonthType) {
		this.calc2MonthType = calc2MonthType;
	}

	public void setPaymentMonthType(String paymentMonthType) {
		this.paymentMonthType = paymentMonthType;
	}
}