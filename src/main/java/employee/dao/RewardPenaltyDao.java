package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.RewardPenalty;
import jdbc.JdbcUtil;

public class RewardPenaltyDao {
	public void insert(Connection conn, RewardPenalty r) throws SQLException {
		String sql = "INSERT INTO reward_penalty (reward_penalty_id, employee_id, reward_penalty_type, reward_penalty_name, reward_penalty_giver, reward_penalty_date, reward_penalty_description, remarks2) VALUES (reward_penalty_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getEmployeeId());
			pstmt.setString(2, r.getRewardPenaltyType());
			pstmt.setString(3, r.getRewardPenaltyName());
			pstmt.setString(4, r.getRewardPenaltyGiver());
			pstmt.setDate(5,
				r.getRewardPenaltyDate() != null ? new java.sql.Date(r.getRewardPenaltyDate().getTime()) : null);
			pstmt.setString(6, r.getRewardPenaltyDescription());
			pstmt.setString(7, r.getRemarks2());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}