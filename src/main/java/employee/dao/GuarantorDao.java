package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Guarantor;
import jdbc.JdbcUtil;

public class GuarantorDao {

	public void insert(Connection conn, Guarantor guarantor) throws SQLException {
		String sql = "INSERT INTO guarantor (guarantor_id, employee_id, guarantor_name, guarantor_relationship, "
			+ "guarantor_resident_number, guarantee_amount, guarantee_date, guarantee_expiration_date, guarantor_phone_number) "
			+ "VALUES (guarantor_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, guarantor.getEmployeeId());
			pstmt.setString(2, guarantor.getGuarantorName());
			pstmt.setString(3, guarantor.getGuarantorRelationship());
			pstmt.setString(4, guarantor.getGuarantorResidentNumber());

			if (guarantor.getGuaranteeAmount() != null)
				pstmt.setLong(5, guarantor.getGuaranteeAmount());
			else
				pstmt.setNull(5, java.sql.Types.NUMERIC);

			if (guarantor.getGuaranteeDate() != null)
				pstmt.setDate(6, new java.sql.Date(guarantor.getGuaranteeDate().getTime()));
			else
				pstmt.setNull(6, java.sql.Types.DATE);

			if (guarantor.getGuaranteeExpirationDate() != null)
				pstmt.setDate(7, new java.sql.Date(guarantor.getGuaranteeExpirationDate().getTime()));
			else
				pstmt.setNull(7, java.sql.Types.DATE);

			pstmt.setString(8, guarantor.getGuarantorPhoneNumber());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}