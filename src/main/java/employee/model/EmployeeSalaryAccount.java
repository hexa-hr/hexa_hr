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

}
