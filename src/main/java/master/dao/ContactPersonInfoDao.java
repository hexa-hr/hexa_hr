package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import master.model.ContactPersonInfo;

public class ContactPersonInfoDao {

	// 1. 담당자 정보 신규 저장 (INSERT)
	public void insert(Connection conn, ContactPersonInfo contact) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO contact_person_info " +
					"(company_id, contact_name, department_id, position_id, con_phone_number, mobile_number, email) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, contact.getCompId());
			pstmt.setString(2, contact.getContName());

			// 부서ID NULL 처리 (약어 deptId 적용)
			if (contact.getDeptId() != null && contact.getDeptId() > 0) {
				pstmt.setInt(3, contact.getDeptId());
			} else {
				pstmt.setNull(3, java.sql.Types.INTEGER);
			}

			// 직위ID NULL 처리 (약어 posId 적용)
			if (contact.getPosId() != null && contact.getPosId() > 0) {
				pstmt.setInt(4, contact.getPosId());
			} else {
				pstmt.setNull(4, java.sql.Types.INTEGER);
			}

			pstmt.setString(5, contact.getConPhone());
			pstmt.setString(6, contact.getMobile());
			pstmt.setString(7, contact.getEmail());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 담당자 정보 수정 (UPDATE)
	public int update(Connection conn, ContactPersonInfo contact) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"UPDATE contact_person_info SET " +
					"contact_name=?, department_id=?, position_id=?, con_phone_number=?, mobile_number=?, email=? " +
					"WHERE company_id=?");

			pstmt.setString(1, contact.getContName());

			if (contact.getDeptId() != null && contact.getDeptId() > 0) {
				pstmt.setInt(2, contact.getDeptId());
			} else {
				pstmt.setNull(2, java.sql.Types.INTEGER);
			}

			if (contact.getPosId() != null && contact.getPosId() > 0) {
				pstmt.setInt(3, contact.getPosId());
			} else {
				pstmt.setNull(3, java.sql.Types.INTEGER);
			}

			pstmt.setString(4, contact.getConPhone());
			pstmt.setString(5, contact.getMobile());
			pstmt.setString(6, contact.getEmail());
			pstmt.setInt(7, contact.getCompId());

			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}