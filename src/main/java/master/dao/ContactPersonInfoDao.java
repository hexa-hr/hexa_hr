package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactPersonInfoDao {

	// 지정된 회사의 담당자 중 가장 작은 person_id를 반환 (없으면 null 반환)
	public Integer selectMinPersonIdByCompanyId(Connection conn, int companyId) throws SQLException {
		String sql = "SELECT MIN(person_id) FROM contact_person_info WHERE company_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, companyId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int minId = rs.getInt(1);
					if (!rs.wasNull()) {
						return minId;
					}
				}
			}
		}
		return null;
	}
}