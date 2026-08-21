package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Referrer;
import jdbc.JdbcUtil;

public class ReferrerDao {
	public void insert(Connection conn, Referrer r) throws SQLException {
		String sql = "INSERT INTO referrer (referrer_id, employee_id, referrer_name, referrer_relationship, referrer_company_name, referrer_position, referrer_phone_number) VALUES (referrer_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getEmployeeId());
			pstmt.setString(2, r.getReferrerName());
			pstmt.setString(3, r.getReferrerRelationship());
			pstmt.setString(4, r.getReferrerCompanyName());
			pstmt.setString(5, r.getReferrerPosition());
			pstmt.setString(6, r.getReferrerPhoneNumber());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}