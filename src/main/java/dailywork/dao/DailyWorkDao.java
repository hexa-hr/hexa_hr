package dailywork.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

	// 1. 목록 조회 (모달창 용) - 조인해서 현장 이름(project_name)도 가져옵니다.
	public List<java.util.Map<String, Object>> selectDailyWorkList(Connection conn, int empId, String yearMonth)
			throws SQLException {
		String sql = "SELECT d.work_id, d.work_date, d.field_or_project_id, p.project_name, d.daily_wage, d.payment_rate, d.income_tax, d.local_tax, d.actual_payment "
				+ "FROM DAILY_WORK d "
				+ "LEFT JOIN FIELD_OR_PROJECT p ON d.field_or_project_id = p.field_or_project_id "
				+ "WHERE d.employee_id = ? AND TO_CHAR(d.work_date, 'YYYY-MM') = ? " + "ORDER BY d.work_date DESC";

		List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, empId);
			pstmt.setString(2, yearMonth);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					java.util.Map<String, Object> map = new java.util.HashMap<>();
					map.put("workId", rs.getInt("work_id"));
					map.put("workDate", rs.getDate("work_date"));
					map.put("fieldProjectId", rs.getInt("field_or_project_id"));
					map.put("projectName", rs.getString("project_name") != null ? rs.getString("project_name") : "");
					map.put("dailyWage", rs.getLong("daily_wage"));
					map.put("paymentRate", rs.getDouble("payment_rate"));
					map.put("incomeTax", rs.getLong("income_tax"));
					map.put("localTax", rs.getLong("local_tax"));
					map.put("actualPayment", rs.getLong("actual_payment"));
					list.add(map);
				}
			}
		}
		return list;
	}

	// 2. 수정 (UPDATE) - SaveHandler에서 호출됨
	public int updateDailyWork(Connection conn, dailywork.model.DailyWorkVO vo) throws SQLException {
		String sql = "UPDATE DAILY_WORK SET work_date=?, field_or_project_id=?, daily_wage=?, payment_rate=?, income_tax=?, local_tax=?, actual_payment=? WHERE work_id=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setDate(1, vo.getWorkDate());
			pstmt.setInt(2, vo.getFieldOrProjectId());
			pstmt.setLong(3, vo.getDailyWage());
			pstmt.setDouble(4, vo.getPaymentRate());
			pstmt.setLong(5, vo.getIncomeTax());
			pstmt.setLong(6, vo.getLocalTax());
			pstmt.setLong(7, vo.getActualPayment());
			pstmt.setInt(8, vo.getWorkId());
			return pstmt.executeUpdate();
		}
	}

	// 3. 삭제 (DELETE)
	public int deleteDailyWork(Connection conn, int workId) throws SQLException {
		String sql = "DELETE FROM DAILY_WORK WHERE work_id=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, workId);
			return pstmt.executeUpdate();
		}
	}
}