package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.MilitaryService;
import jdbc.JdbcUtil;

public class MilitaryServiceDao {

	public void insert(Connection conn, MilitaryService mil) throws SQLException {
		String sql = "INSERT INTO military_service (military_service_id, employee_id, service_type, branch, service_period1, service_period2, final_rank, department1, exemption_reason) VALUES (military_service_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
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

	// 🌟 새로 추가
	public List<MilitaryService> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MilitaryService> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement(
				"SELECT * FROM military_service WHERE employee_id = ? ORDER BY military_service_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new MilitaryService(rs.getInt("military_service_id"), rs.getInt("employee_id"),
					rs.getString("service_type"),
					rs.getString("branch"), rs.getDate("service_period1"), rs.getDate("service_period2"),
					rs.getString("final_rank"), rs.getString("department1"), rs.getString("exemption_reason")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}