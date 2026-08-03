package employee.model;

import java.util.Date;

// 사원 급여 계좌
public class EmployeeSalaryAccount {
	private Integer accountId;
	private Integer companyId;
	private String bankName;
	private String accountNumber;
	private String depositStocks;
	private Date salaryCalculation1;
	private Date salaryCalculation2;
	private Date salaryPaymentDate;

	public EmployeeSalaryAccount(Integer accountId, Integer companyId, String bankName, String accountNumber,
		String depositStocks, Date salaryCalculation1, Date salaryCalculation2, Date salaryPaymentDate) {
		this.accountId = accountId;
		this.companyId = companyId;
		this.bankName = bankName;
		this.accountNumber = accountNumber;
		this.depositStocks = depositStocks;
		this.salaryCalculation1 = salaryCalculation1;
		this.salaryCalculation2 = salaryCalculation2;
		this.salaryPaymentDate = salaryPaymentDate;
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

	public Date getSalaryCalculation1() {
		return salaryCalculation1;
	}

	public Date getSalaryCalculation2() {
		return salaryCalculation2;
	}

	public Date getSalaryPaymentDate() {
		return salaryPaymentDate;
	}

}
