package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Career;
import jdbc.JdbcUtil;

public class CareerDao {

	public void insert(Connection conn, Career career) throws SQLException {
		// ⭐ 실제 Oracle DB의 테이블명과 컬럼명, 시퀀스명(career_seq)에 맞게 수정해 줘!
		String sql = "INSERT INTO career (career_id, employee_id, company_name, start_date, end_date, "
			+ "employment_period, final_position, responsibilities, reason_for_resignation) "
			+ "VALUES (career_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			// DTO에 있는 데이터를 하나씩 꺼내서 쿼리문의 '?' 자리에 세팅
			pstmt.setInt(1, career.getEmployeeId());
			pstmt.setString(2, career.getCompanyName());

			// java.util.Date를 Oracle이 좋아하는 java.sql.Date로 변환
			pstmt.setDate(3, career.getStartDate() != null ? new java.sql.Date(career.getStartDate().getTime()) : null);
			pstmt.setDate(4, career.getEndDate() != null ? new java.sql.Date(career.getEndDate().getTime()) : null);

			pstmt.setString(5, career.getEmploymentPeriod());
			pstmt.setString(6, career.getFinalPosition());
			pstmt.setString(7, career.getResponsibilities());
			pstmt.setString(8, career.getReasonForResignation());

			pstmt.executeUpdate(); // DB에 쏘기!
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}