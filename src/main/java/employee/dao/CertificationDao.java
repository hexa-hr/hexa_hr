package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.Certification;
import jdbc.JdbcUtil;

public class CertificationDao {

	public void insert(Connection conn, Certification cert) throws SQLException {
		String sql = "INSERT INTO certification (certification_id, employee_id, certification_name, acquisition_date, "
			+ "issuing_organization, certification_number, remarks1) "
			+ "VALUES (certification_seq.NEXTVAL, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, cert.getEmployeeId());
			pstmt.setString(2, cert.getCertificationName());
			if (cert.getAcquisitionDate() != null)
				pstmt.setDate(3, new java.sql.Date(cert.getAcquisitionDate().getTime()));
			else
				pstmt.setNull(3, java.sql.Types.DATE);

			pstmt.setString(4, cert.getIssuingOrganization());
			pstmt.setString(5, cert.getCertificationNumber());
			pstmt.setString(6, cert.getRemarks1());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 🌟 서비스가 애타게 찾던 조회 메서드 추가!
	public List<Certification> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Certification> result = new ArrayList<>();
		try {
			pstmt = conn
				.prepareStatement("SELECT * FROM certification WHERE employee_id = ? ORDER BY certification_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Certification(
					rs.getInt("certification_id"), rs.getInt("employee_id"), rs.getString("certification_name"),
					rs.getDate("acquisition_date"), rs.getString("issuing_organization"),
					rs.getString("certification_number"), rs.getString("remarks1")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}