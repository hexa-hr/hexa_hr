package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import employee.model.EmployeeSalaryAccount;

// 회사별 급여지급정보 조회
public class EmployeeSalaryAccountDao {

	public EmployeeSalaryAccount selectByCompanyId(
		Connection conn,
		Integer companyId)
		throws SQLException {

		String sql = "SELECT account_id, "
			+ "company_id, "
			+ "bank_name, "
			+ "account_number, "
			+ "deposit_stocks, "
			+ "salary_calculation1, "
			+ "salary_calculation2, "
			+ "salary_payment_date, "
			+ "calc1_month_type, "
			+ "calc2_month_type, "
			+ "payment_month_type "
			+ "FROM employee_salary_account "
			+ "WHERE company_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, companyId);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					return new EmployeeSalaryAccount(
						rs.getInt("account_id"),
						rs.getInt("company_id"),
						rs.getString("bank_name"),
						rs.getString("account_number"),
						rs.getString("deposit_stocks"),
						getNullableInteger(
							rs,
							"salary_calculation1"),
						getNullableInteger(
							rs,
							"salary_calculation2"),
						getNullableInteger(
							rs,
							"salary_payment_date"),
						rs.getString("calc1_month_type"),
						rs.getString("calc2_month_type"),
						rs.getString("payment_month_type"));
				}
			}
		}

		return null;
	}

	private Integer getNullableInteger(
		ResultSet rs,
		String columnName)
		throws SQLException {

		int value = rs.getInt(columnName);

		return rs.wasNull()
			? null
			: value;
	}
}