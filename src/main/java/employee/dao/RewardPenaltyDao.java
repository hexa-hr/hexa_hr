package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	// 🌟 새로 추가
	public List<RewardPenalty> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<RewardPenalty> result = new ArrayList<>();
		try {
			pstmt = conn
				.prepareStatement("SELECT * FROM reward_penalty WHERE employee_id = ? ORDER BY reward_penalty_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new RewardPenalty(rs.getInt("reward_penalty_id"), rs.getInt("employee_id"),
					rs.getString("reward_penalty_type"),
					rs.getString("reward_penalty_name"), rs.getString("reward_penalty_giver"),
					rs.getDate("reward_penalty_date"),
					rs.getString("reward_penalty_description"), rs.getString("remarks2")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}