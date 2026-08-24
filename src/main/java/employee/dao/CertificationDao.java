package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Certification;
import jdbc.JdbcUtil;

public class CertificationDao {

	public void insert(Connection conn, Certification cert) throws SQLException {
		// ⭐ 실제 DB 테이블명/시퀀스명에 맞게 수정해 줘!
		String sql = "INSERT INTO certification (certification_id, employee_id, certification_name, acquisition_date, "
			+ "issuing_organization, certification_number, remarks1) "
			+ "VALUES (certification_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, cert.getEmployeeId());
			pstmt.setString(2, cert.getCertificationName());

			pstmt.setDate(3,
				cert.getAcquisitionDate() != null ? new java.sql.Date(cert.getAcquisitionDate().getTime()) : null);

			pstmt.setString(4, cert.getIssuingOrganization());
			pstmt.setString(5, cert.getCertificationNumber());
			pstmt.setString(6, cert.getRemarks1());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}