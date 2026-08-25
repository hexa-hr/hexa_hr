package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import employee.model.EmployeeSalaryAccount;
import jdbc.JdbcUtil;

public class EmployeeSalaryAccountDao {

	public Integer getNextId(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT salary_account_seq.nextval FROM dual");
			rs = pstmt.executeQuery();
			if (rs.next())
				return rs.getInt(1);
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public void insert(Connection conn, EmployeeSalaryAccount account) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO employee_salary_account (account_id, company_id, bank_name, account_number, deposit_stocks, "
					+
					"salary_calculation1, salary_calculation2, salary_payment_date, calc1_month_type, calc2_month_type, payment_month_type) "
					+
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			if (account.getAccountId() == null)
				account.setAccountId(getNextId(conn));
			pstmt.setInt(1, account.getAccountId());
			if (account.getCompanyId() != null)
				pstmt.setInt(2, account.getCompanyId());
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);
			pstmt.setString(3, account.getBankName());
			pstmt.setString(4, account.getAccountNumber());
			pstmt.setString(5, account.getDepositStocks());
			if (account.getSalaryCalculation1() != null)
				pstmt.setInt(6, account.getSalaryCalculation1());
			else
				pstmt.setNull(6, java.sql.Types.INTEGER);
			if (account.getSalaryCalculation2() != null)
				pstmt.setInt(7, account.getSalaryCalculation2());
			else
				pstmt.setNull(7, java.sql.Types.INTEGER);
			if (account.getSalaryPaymentDate() != null)
				pstmt.setInt(8, account.getSalaryPaymentDate());
			else
				pstmt.setNull(8, java.sql.Types.INTEGER);
			pstmt.setString(9, account.getCalc1MonthType());
			pstmt.setString(10, account.getCalc2MonthType());
			pstmt.setString(11, account.getPaymentMonthType());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public EmployeeSalaryAccount selectByCompanyId(Connection conn, Integer companyId) throws SQLException {
		String sql = "SELECT * FROM employee_salary_account WHERE company_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, companyId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new EmployeeSalaryAccount(rs.getInt("account_id"), rs.getInt("company_id"),
						rs.getString("bank_name"),
						rs.getString("account_number"), rs.getString("deposit_stocks"),
						getNullableInteger(rs, "salary_calculation1"),
						getNullableInteger(rs, "salary_calculation2"), getNullableInteger(rs, "salary_payment_date"),
						rs.getString("calc1_month_type"), rs.getString("calc2_month_type"),
						rs.getString("payment_month_type"));
				}
			}
		}
		return null;
	}

	public EmployeeSalaryAccount selectByEmployeeId(Connection conn, Integer employeeId) throws SQLException {
		String sql = "SELECT a.* FROM employee_salary_account a JOIN employee e ON a.account_id = e.account_id WHERE e.employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new EmployeeSalaryAccount(rs.getInt("account_id"), rs.getInt("company_id"),
						rs.getString("bank_name"),
						rs.getString("account_number"), rs.getString("deposit_stocks"),
						getNullableInteger(rs, "salary_calculation1"),
						getNullableInteger(rs, "salary_calculation2"), getNullableInteger(rs, "salary_payment_date"),
						rs.getString("calc1_month_type"), rs.getString("calc2_month_type"),
						rs.getString("payment_month_type"));
				}
			}
		}
		return null;
	}

	private Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {
		int value = rs.getInt(columnName);
		return rs.wasNull() ? null : value;
	}
}