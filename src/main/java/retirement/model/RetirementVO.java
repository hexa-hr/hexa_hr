package retirement.model;

import java.sql.Date;

public class RetirementVO {
	private int employeeId;
	private String empName;
	private String deptName;
	private String positionName;
	private Date hireDate;
	private Date resignationDate;

	// 銀行口座情報 (employee_salary_account テーブル)
	private String bankName;
	private String accountNumber;

	// 最近3ヶ月の給与合計
	private long recent3MonthsTotalWage;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public Date getResignationDate() {
		return resignationDate;
	}

	public void setResignationDate(Date resignationDate) {
		this.resignationDate = resignationDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public long getRecent3MonthsTotalWage() {
		return recent3MonthsTotalWage;
	}

	public void setRecent3MonthsTotalWage(long recent3MonthsTotalWage) {
		this.recent3MonthsTotalWage = recent3MonthsTotalWage;
	}
}