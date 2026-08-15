package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeInsurance;

// 사원별 보험 가입정보 조회
public class InsuranceDao {

	public List<EmployeeInsurance> selectByEmployeeId(
		Connection conn,
		Integer employeeId)
		throws SQLException {

		String sql = "SELECT insurance_agency, "
			+ "NVL(insurance_amount, 0) AS insurance_amount "
			+ "FROM insurance "
			+ "WHERE employee_id = ? "
			+ "ORDER BY insurance_id";

		List<EmployeeInsurance> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);

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
}