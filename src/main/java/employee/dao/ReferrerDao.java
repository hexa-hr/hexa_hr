package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	// 🌟 새로 추가
	public List<Referrer> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Referrer> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM referrer WHERE employee_id = ? ORDER BY referrer_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result
					.add(new Referrer(rs.getInt("referrer_id"), rs.getInt("employee_id"), rs.getString("referrer_name"),
						rs.getString("referrer_relationship"), rs.getString("referrer_company_name"),
						rs.getString("referrer_position"),
						rs.getString("referrer_phone_number")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}