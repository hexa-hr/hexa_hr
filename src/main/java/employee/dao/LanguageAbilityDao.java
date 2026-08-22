package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.LanguageAbility;
import jdbc.JdbcUtil;

public class LanguageAbilityDao {

	public void insert(Connection conn, LanguageAbility lang) throws SQLException {
		// ⭐ 실제 DB 테이블명/시퀀스명에 맞게 수정해 줘!
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
}