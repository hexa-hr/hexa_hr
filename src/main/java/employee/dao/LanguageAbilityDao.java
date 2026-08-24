package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.LanguageAbility;
import jdbc.JdbcUtil;

public class LanguageAbilityDao {

	public void insert(Connection conn, LanguageAbility lang) throws SQLException {
		String sql = "INSERT INTO language_ability (language_ability_id, employee_id, language, test_name, "
			+ "official_score, acquisition_date1, reading_ability, writing_ability, speaking_ability) "
			+ "VALUES (language_ability_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, lang.getEmployeeId());
			pstmt.setString(2, lang.getLanguage());
			pstmt.setString(3, lang.getTestName());

			if (lang.getOfficialScore() != null)
				pstmt.setInt(4, lang.getOfficialScore());
			else
				pstmt.setNull(4, java.sql.Types.INTEGER);

			if (lang.getAcquisitionDate1() != null)
				pstmt.setDate(5, new java.sql.Date(lang.getAcquisitionDate1().getTime()));
			else
				pstmt.setNull(5, java.sql.Types.DATE);

			pstmt.setString(6, lang.getReadingAbility());
			pstmt.setString(7, lang.getWritingAbility());
			pstmt.setString(8, lang.getSpeakingAbility());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 서비스가 애타게 찾던 조회 메서드 추가!
	public List<LanguageAbility> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<LanguageAbility> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement(
				"SELECT * FROM language_ability WHERE employee_id = ? ORDER BY language_ability_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Integer officialScore = rs.getInt("official_score");
				if (rs.wasNull())
					officialScore = null; // null 처리 안전장치

				result.add(new LanguageAbility(
					rs.getInt("language_ability_id"), rs.getInt("employee_id"), rs.getString("language"),
					rs.getString("test_name"), officialScore, rs.getDate("acquisition_date1"),
					rs.getString("reading_ability"), rs.getString("writing_ability"),
					rs.getString("speaking_ability")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}