package dailywork.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dailywork.model.DailyWorkVO;

public class DailyWorkDao {

	// 일용직 근무기록 등록 (INSERT)
	public int insertDailyWork(Connection conn, DailyWorkVO vo) throws SQLException {
		String sql = "INSERT INTO DAILY_WORK (work_id, employee_id, work_date, field_or_project_id, daily_wage, payment_rate, income_tax, local_tax, actual_payment) "
				+ "VALUES ((SELECT NVL(MAX(work_id), 0) + 1 FROM DAILY_WORK), ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, vo.getEmployeeId());
			pstmt.setDate(2, vo.getWorkDate());
			pstmt.setInt(3, vo.getFieldOrProjectId());
			pstmt.setLong(4, vo.getDailyWage());
			pstmt.setDouble(5, vo.getPaymentRate());
			pstmt.setLong(6, vo.getIncomeTax());
			pstmt.setLong(7, vo.getLocalTax());
			pstmt.setLong(8, vo.getActualPayment());
			return pstmt.executeUpdate();
		}
	}
}