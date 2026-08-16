package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import employee.model.EmployeeSalaryAccount;
import jdbc.JdbcUtil;

public class EmployeeSalaryAccountDao {

    // 1. 시퀀스에서 계좌 번호표를 미리 뽑아오는 메서드
    public Integer getNextId(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT salary_account_seq.nextval FROM dual");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } finally {
            JdbcUtil.close(rs);
            JdbcUtil.close(pstmt);
        }
    }

    // 2. DB에 실제 INSERT 하는 메서드
    public void insert(Connection conn, EmployeeSalaryAccount account) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(
                "INSERT INTO employee_salary_account " +
                "(account_id, company_id, bank_name, account_number, deposit_stocks, " +
                "salary_calculation1, salary_calculation2, salary_payment_date, " +
                "calc1_month_type, calc2_month_type, payment_month_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            pstmt.setInt(1, account.getAccountId()); // 위에서 미리 뽑은 번호 넣기
            
            if (account.getCompanyId() != null) pstmt.setInt(2, account.getCompanyId());
            else pstmt.setNull(2, java.sql.Types.INTEGER);
            
            pstmt.setString(3, account.getBankName());
            pstmt.setString(4, account.getAccountNumber());
            pstmt.setString(5, account.getDepositStocks());

            if (account.getSalaryCalculation1() != null) pstmt.setInt(6, account.getSalaryCalculation1());
            else pstmt.setNull(6, java.sql.Types.INTEGER);

            if (account.getSalaryCalculation2() != null) pstmt.setInt(7, account.getSalaryCalculation2());
            else pstmt.setNull(7, java.sql.Types.INTEGER);

            if (account.getSalaryPaymentDate() != null) pstmt.setInt(8, account.getSalaryPaymentDate());
            else pstmt.setNull(8, java.sql.Types.INTEGER);

            pstmt.setString(9, account.getCalc1MonthType());
            pstmt.setString(10, account.getCalc2MonthType());
            pstmt.setString(11, account.getPaymentMonthType());

            pstmt.executeUpdate();
        } finally {
            JdbcUtil.close(pstmt);
        }
    }
}