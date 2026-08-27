package employee.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeInsurance;
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

	public List<EmployeeInsurance> selectByEmployeeId(
		Connection conn,
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate) throws SQLException {

		String sql = "SELECT insurance_agency, NVL(insurance_amount, 0) AS insurance_amount "
			+ "FROM insurance "
			+ "WHERE employee_id = ? "
			+ "  AND (insurance_start_date IS NULL OR insurance_start_date <= ?) "
			+ "  AND (insurance_end_date IS NULL OR insurance_end_date >= ?) "
			+ "ORDER BY insurance_id";

		List<EmployeeInsurance> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			// 보험 시작일이 정산 종료일 이하여야 함
			pstmt.setDate(2, settlementEndDate);
			// 보험 종료일이 정산 시작일 이상이어야 함
			pstmt.setDate(3, settlementStartDate);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeInsurance insurance = new EmployeeInsurance(
						rs.getString("insurance_agency"),
						rs.getLong("insurance_amount"));

					result.add(insurance);
				}
			}
		}
		return result;
	}

	// 🌟 이번에 빼먹어서 에러를 냈던 바로 그 메서드! (새로 추가)
	public List<Insurance> selectAllByEmployeeId(Connection conn, int employeeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Insurance> result = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM insurance WHERE employee_id = ? ORDER BY insurance_id ASC");
			pstmt.setInt(1, employeeId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Insurance ins = new Insurance(
					rs.getInt("insurance_id"),
					rs.getInt("employee_id"),
					rs.getString("insurance_agency"),
					rs.getString("insurance_number"),
					rs.getLong("insurance_amount"),
					rs.getDate("insurance_start_date"),
					rs.getDate("insurance_end_date"),
					rs.getString("remarks4"));
				result.add(ins);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
}