package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.Career;
import jdbc.JdbcUtil;

public class CareerDao {

	public void insert(Connection conn, Career career) throws SQLException {
		String sql = "INSERT INTO career (career_id, employee_id, company_name, start_date, end_date, employment_period, final_position, responsibilities, reason_for_resignation) VALUES (career_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, career.getEmployeeId());
			pstmt.setString(2, career.getCompanyName());
			pstmt.setDate(3, career.getStartDate() != null ? new java.sql.Date(career.getStartDate().getTime()) : null);
			pstmt.setDate(4, career.getEndDate() != null ? new java.sql.Date(career.getEndDate().getTime()) : null);
			pstmt.setString(5, career.getEmploymentPeriod());
			pstmt.setString(6, career.getFinalPosition());
			pstmt.setString(7, career.getResponsibilities());
			pstmt.setString(8, career.getReasonForResignation());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 새로 추가
	public List<Career> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Career> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM career WHERE employee_id = ? ORDER BY career_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Career(rs.getInt("career_id"), rs.getInt("employee_id"), rs.getString("company_name"),
					rs.getDate("start_date"), rs.getDate("end_date"), rs.getString("employment_period"),
					rs.getString("final_position"), rs.getString("responsibilities"),
					rs.getString("reason_for_resignation")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}