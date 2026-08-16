package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Insurance;
import jdbc.JdbcUtil;

public class InsuranceDao {

	public void insert(Connection conn, Insurance insurance) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO insurance (insurance_id, employee_id, insurance_agency, insurance_number, " +
					"insurance_amount, insurance_start_date, insurance_end_date, remarks4) " +
					"VALUES (insurance_seq.nextval, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, insurance.getEmployeeId());
			pstmt.setString(2, insurance.getInsuranceAgency());
			pstmt.setString(3, insurance.getInsuranceNumber());

			if (insurance.getInsuranceAmount() != null)
				pstmt.setLong(4, insurance.getInsuranceAmount());
			else
				pstmt.setNull(4, java.sql.Types.NUMERIC);

			if (insurance.getInsuranceStartDate() != null)
				pstmt.setDate(5, new java.sql.Date(insurance.getInsuranceStartDate().getTime()));
			else
				pstmt.setNull(5, java.sql.Types.DATE);

			if (insurance.getInsuranceEndDate() != null)
				pstmt.setDate(6, new java.sql.Date(insurance.getInsuranceEndDate().getTime()));
			else
				pstmt.setNull(6, java.sql.Types.DATE);

			pstmt.setString(7, insurance.getRemarks4());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}