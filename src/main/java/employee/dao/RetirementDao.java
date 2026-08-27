package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import employee.model.Retirement;
import jdbc.JdbcUtil;

public class RetirementDao {
	public void insert(Connection conn, Retirement r) throws SQLException {
		String sql = "INSERT INTO retirement (retirement_id, employee_id, retirement_type, retirement_date, retirement_reason, contact_after_retirement, retirement_pay) VALUES (retirement_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getEmployeeId());
			pstmt.setString(2, r.getRetirementType());
			pstmt.setDate(3, r.getRetirementDate() != null ? new java.sql.Date(r.getRetirementDate().getTime()) : null);
			pstmt.setString(4, r.getRetirementReason());
			pstmt.setString(5, r.getRetirementContact());
			if (r.getSeverancePay() != null)
				pstmt.setLong(6, r.getSeverancePay());
			else
				pstmt.setNull(6, Types.NUMERIC);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 새로 추가 (퇴직은 단일 객체)
	public Retirement selectByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT * FROM retirement WHERE employee_id = ?");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return new Retirement(rs.getInt("employee_id"), rs.getString("retirement_type"),
					rs.getDate("retirement_date"), rs.getString("retirement_reason"),
					rs.getString("contact_after_retirement"), rs.getLong("retirement_pay"));
			}
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}