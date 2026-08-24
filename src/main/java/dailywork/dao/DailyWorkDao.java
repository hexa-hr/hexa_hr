package dailywork.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dailywork.model.DailyWorkMonthlyVO;
import dailywork.model.DailyWorkPayrollRow;
import dailywork.model.DailyWorkVO;

public class DailyWorkDao {

	// 日雇い勤務記録登録 (INSERT)[cite: 24]
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

	// 1. リスト照会 (モーダル用) - ジョインして現場名(name)を取得します[cite: 24].
	public List<java.util.Map<String, Object>> selectDailyWorkList(Connection conn, int empId, String yearMonth)
			throws SQLException {

		// p.nameを明示的に取得し、取り出しやすいようにAS proj_nameエイリアスを付けます[cite: 24].
		String sql = "SELECT d.work_id, d.work_date, d.field_or_project_id, p.name AS proj_name, d.daily_wage, d.payment_rate, d.income_tax, d.local_tax, d.actual_payment "
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

					// DBから取得したproj_name(実際にはp.name)を安全に取り出します[cite: 24].
					String pName = rs.getString("proj_name");
					// 現場データが存在すればその名前を入れ、現場が削除されて見つからない場合は「削除された現場」と表示します[cite: 24].
					map.put("projectName", (pName != null && !pName.trim().isEmpty()) ? pName : "削除された現場");

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

	// 2. 修正 (UPDATE) - SaveHandlerから呼び出されます[cite: 24]
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

	// 3. 削除 (DELETE)[cite: 24]
	public int deleteDailyWork(Connection conn, int workId) throws SQLException {
		String sql = "DELETE FROM DAILY_WORK WHERE work_id=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, workId);
			return pstmt.executeUpdate();
		}
	}

	// 4. 月別勤務要約照会 (月別照会カレンダー用)[cite: 24]
	public List<DailyWorkMonthlyVO> selectMonthlySummary(Connection conn, String yearMonth) throws SQLException {
		String sql = "SELECT " + "    'No-' || e.employee_id AS emp_no, " + "    e.korean_name, "
				+ "    NVL(d.department_name, '未配属') AS dept_name, "
				+ "    LISTAGG(TO_CHAR(dw.work_date, 'FMDD'), ',') WITHIN GROUP (ORDER BY dw.work_date) AS work_days, "
				+ "    COUNT(dw.work_id) AS total_work_days, " + "    NVL(SUM(dw.income_tax), 0) AS total_income_tax, "
				+ "    NVL(SUM(dw.local_tax), 0) AS total_local_tax, "
				+ "    NVL(SUM(dw.actual_payment), 0) AS total_actual_payment " + "FROM employee e "
				+ "LEFT JOIN daily_work dw ON e.employee_id = dw.employee_id "
				+ "                       AND TO_CHAR(dw.work_date, 'YYYY-MM') = ? "
				+ "LEFT JOIN department d ON e.department_id = d.department_id " + "WHERE e.employment_type = '日雇い' "
				+ "GROUP BY e.employee_id, e.korean_name, d.department_name " + "ORDER BY e.employee_id";

		List<DailyWorkMonthlyVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, yearMonth);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					list.add(new DailyWorkMonthlyVO(rs.getString("emp_no"), rs.getString("korean_name"),
							rs.getString("dept_name"), rs.getString("work_days"), rs.getInt("total_work_days"),
							rs.getLong("total_income_tax"), rs.getLong("total_local_tax"),
							rs.getLong("total_actual_payment")));
				}
			}
		}
		return list;
	}

	// 5. 詳細照会マルチ条件検索 (動的検索ロジック)[cite: 24]
	public List<Map<String, Object>> selectDailyWorkDetailList(Connection conn, String startDate, String endDate,
			String empName, String deptId, String projectId) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.work_date, 'No-' || e.employee_id AS emp_no, e.korean_name, ");
		sql.append("       NVL(dp.department_name, '未配属') AS dept_name, ");
		sql.append("       NVL(p.name, '削除された現場') AS proj_name, ");
		sql.append("       d.daily_wage, d.payment_rate, d.income_tax, d.local_tax, d.actual_payment ");
		sql.append("FROM DAILY_WORK d ");
		sql.append("JOIN EMPLOYEE e ON d.employee_id = e.employee_id ");
		sql.append("LEFT JOIN DEPARTMENT dp ON e.department_id = dp.department_id ");
		sql.append("LEFT JOIN FIELD_OR_PROJECT p ON d.field_or_project_id = p.field_or_project_id ");
		sql.append("WHERE e.employment_type = '日雇い' ");

		// チェックされた条件のみクエリに動的追加[cite: 24]
		if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
			sql.append("AND d.work_date BETWEEN TO_DATE('" + startDate + "', 'YYYY-MM-DD') AND TO_DATE('" + endDate
					+ "', 'YYYY-MM-DD') ");
		}
		if (empName != null && !empName.isEmpty()) {
			sql.append("AND e.korean_name LIKE '%" + empName + "%' ");
		}

		if (deptId != null && !deptId.isEmpty()) {
			// [修正された部分] 名前の誤った検索コードを削除し、部署IDで正確に検索するように原状復帰！[cite: 24]
			sql.append("AND e.department_id = " + deptId + " ");
		}

		if (projectId != null && !projectId.isEmpty()) {
			sql.append("AND d.field_or_project_id = " + projectId + " ");
		}

		sql.append("ORDER BY d.work_date DESC, e.employee_id");

		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString()); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("workDate", rs.getDate("work_date"));
				map.put("empNo", rs.getString("emp_no"));
				map.put("empName", rs.getString("korean_name"));
				map.put("deptName", rs.getString("dept_name"));
				map.put("projName", rs.getString("proj_name"));
				map.put("dailyWage", rs.getLong("daily_wage"));
				map.put("paymentRate", rs.getDouble("payment_rate"));
				map.put("incomeTax", rs.getLong("income_tax"));
				map.put("localTax", rs.getLong("local_tax"));
				map.put("actualPayment", rs.getLong("actual_payment"));
				list.add(map);
			}
		}
		return list;
	}

	// 日雇い給与入力用 - 精算期間内の社員別勤務記録照会[cite: 24]
	public List<DailyWorkPayrollRow> selectPayrollRows(Connection conn, int employeeId, Date settlementStartDate,
			Date settlementEndDate) throws SQLException {

		String sql = "SELECT work_id, " + "       work_date, " + "       NVL(daily_wage, 0) AS daily_wage, "
				+ "       NVL(payment_rate, 1) AS payment_rate, " + "       ROUND(NVL(daily_wage, 0) "
				+ "           * NVL(payment_rate, 1)) AS payment_amount, " + "       NVL(income_tax, 0) AS income_tax, "
				+ "       NVL(local_tax, 0) AS local_tax " + "FROM daily_work " + "WHERE employee_id = ? "
				+ "  AND work_date BETWEEN ? AND ? " + "ORDER BY work_date, work_id";

		List<DailyWorkPayrollRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, employeeId);
			pstmt.setDate(2, settlementStartDate);
			pstmt.setDate(3, settlementEndDate);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					result.add(new DailyWorkPayrollRow(rs.getInt("work_id"), rs.getDate("work_date"),
							rs.getLong("daily_wage"), rs.getDouble("payment_rate"), rs.getLong("payment_amount"),
							rs.getLong("income_tax"), rs.getLong("local_tax")));
				}
			}
		}

		return result;
	}
}