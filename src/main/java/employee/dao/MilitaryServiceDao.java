package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.MilitaryService;
import jdbc.JdbcUtil;

public class MilitaryServiceDao {

	public void insert(Connection conn, MilitaryService mil) throws SQLException {
		// ⭐ 실제 DB 테이블명/시퀀스명에 맞게 수정해 줘!
		String sql = "INSERT INTO military_service (military_service_id, employee_id, service_type, branch, "
			+ "service_period1, service_period2, final_rank, department1, exemption_reason) "
			+ "VALUES (military_service_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, mil.getEmployeeId());
			pstmt.setString(2, mil.getServiceType());
			pstmt.setString(3, mil.getBranch());

			pstmt.setDate(4,
				mil.getServicePeriod1() != null ? new java.sql.Date(mil.getServicePeriod1().getTime()) : null);
			pstmt.setDate(5,
				mil.getServicePeriod2() != null ? new java.sql.Date(mil.getServicePeriod2().getTime()) : null);

			pstmt.setString(6, mil.getFinalRank());
			pstmt.setString(7, mil.getDepartment1());
			pstmt.setString(8, mil.getExemptionReason());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}