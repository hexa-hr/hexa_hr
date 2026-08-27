package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import employee.model.Training;
import jdbc.JdbcUtil;

public class TrainingDao {
	public void insert(Connection conn, Training t) throws SQLException {
		String sql = "INSERT INTO training (training_id, employee_id, training_type, training_name, training_start_date, training_end_date, training_organization, training_cost, refundable_training_cost) VALUES (training_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, t.getEmployeeId());
			pstmt.setString(2, t.getTrainingType());
			pstmt.setString(3, t.getTrainingName());
			pstmt.setDate(4,
				t.getTrainingStartDate() != null ? new java.sql.Date(t.getTrainingStartDate().getTime()) : null);
			pstmt.setDate(5,
				t.getTrainingEndDate() != null ? new java.sql.Date(t.getTrainingEndDate().getTime()) : null);
			pstmt.setString(6, t.getTrainingOrganization());
			if (t.getTrainingCost() != null)
				pstmt.setLong(7, t.getTrainingCost());
			else
				pstmt.setNull(7, Types.NUMERIC);
			if (t.getRefundableTrainingCost() != null)
				pstmt.setLong(8, t.getRefundableTrainingCost());
			else
				pstmt.setNull(8, Types.NUMERIC);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 새로 추가
	public List<Training> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Training> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM training WHERE employee_id = ? ORDER BY training_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Training(rs.getInt("training_id"), rs.getInt("employee_id"),
					rs.getString("training_type"),
					rs.getString("training_name"), rs.getDate("training_start_date"), rs.getDate("training_end_date"),
					rs.getString("training_organization"), rs.getLong("training_cost"),
					rs.getLong("refundable_training_cost")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}