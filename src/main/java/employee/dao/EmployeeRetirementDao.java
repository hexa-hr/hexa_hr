package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeRetirementListDto;
import jdbc.JdbcUtil;

public class EmployeeRetirementDao {

	// 🌟 1. 총 데이터 개수 구하기 (페이징용)
	public int selectCount(Connection conn, String statusFilter) throws SQLException {
		String sql = "SELECT COUNT(*) FROM employee";
		if (statusFilter != null && !statusFilter.trim().isEmpty()) {
			sql += " WHERE status = ?";
		}
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			if (statusFilter != null && !statusFilter.trim().isEmpty()) {
				pstmt.setString(1, statusFilter);
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		}
		return 0;
	}

	// 🌟 2. 조건에 맞는 데이터만 20개씩 잘라서 가져오기
	public List<EmployeeRetirementListDto> selectRetirementList(Connection conn, String statusFilter, int startRow,
		int endRow) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ( ");
		sql.append("    SELECT ROWNUM AS rnum, a.* FROM ( ");
		sql.append("        SELECT e.employee_id, e.status, e.korean_name, d.department_name, p.position_name, ");
		sql.append("               e.hire_date, e.resignation_date, ");
		sql.append(
			"               TRUNC(MONTHS_BETWEEN(NVL(e.resignation_date, SYSDATE), e.hire_date) / 12) AS years_of_service, ");
		sql.append("               CASE WHEN r.retirement_id IS NOT NULL THEN 'O' ELSE 'X' END AS is_settled, ");
		sql.append("               r.retirement_type, r.retirement_reason, r.contact_after_retirement ");
		sql.append("        FROM employee e ");
		sql.append("        LEFT JOIN department d ON e.department_id = d.department_id ");
		sql.append("        LEFT JOIN position p ON e.position_id = p.position_id ");
		sql.append("        LEFT JOIN retirement r ON e.employee_id = r.employee_id ");

		// 상태 필터링이 있으면 WHERE 조건 추가
		if (statusFilter != null && !statusFilter.trim().isEmpty()) {
			sql.append("        WHERE e.status = ? ");
		}

		sql.append("        ORDER BY e.employee_id DESC ");
		sql.append("    ) a WHERE ROWNUM <= ? ");
		sql.append(") WHERE rnum >= ?");

		List<EmployeeRetirementListDto> result = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			if (statusFilter != null && !statusFilter.trim().isEmpty()) {
				pstmt.setString(idx++, statusFilter);
			}
			pstmt.setInt(idx++, endRow);
			pstmt.setInt(idx++, startRow);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					result.add(new EmployeeRetirementListDto(
						rs.getInt("employee_id"), rs.getString("status"), rs.getString("korean_name"),
						rs.getString("department_name"), rs.getString("position_name"),
						rs.getDate("hire_date"), rs.getDate("resignation_date"),
						rs.getInt("years_of_service"), rs.getString("is_settled"),
						rs.getString("retirement_type"), rs.getString("retirement_reason"),
						rs.getString("contact_after_retirement")));
				}
			}
		}
		return result;
	}

	// 3. 퇴직 처리 (변경 없음)
	public void processRetirement(Connection conn, int empId, String type, java.sql.Date date, String reason,
		String contact) throws SQLException {
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt1 = conn
				.prepareStatement("UPDATE employee SET status = '퇴직', resignation_date = ? WHERE employee_id = ?");
			pstmt1.setDate(1, date);
			pstmt1.setInt(2, empId);
			pstmt1.executeUpdate();

			pstmt2 = conn.prepareStatement(
				"INSERT INTO retirement (retirement_id, employee_id, retirement_type, retirement_date, retirement_reason, contact_after_retirement) VALUES (retirement_seq.NEXTVAL, ?, ?, ?, ?, ?)");
			pstmt2.setInt(1, empId);
			pstmt2.setString(2, type);
			pstmt2.setDate(3, date);
			pstmt2.setString(4, reason);
			pstmt2.setString(5, contact);
			pstmt2.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt1);
			JdbcUtil.close(pstmt2);
		}
	}

	// 4. 퇴직 취소 (변경 없음)
	public void cancelRetirement(Connection conn, int empId) throws SQLException {
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		try {
			pstmt1 = conn
				.prepareStatement("UPDATE employee SET status = '재직', resignation_date = NULL WHERE employee_id = ?");
			pstmt1.setInt(1, empId);
			pstmt1.executeUpdate();

			pstmt2 = conn.prepareStatement("DELETE FROM retirement WHERE employee_id = ?");
			pstmt2.setInt(1, empId);
			pstmt2.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt1);
			JdbcUtil.close(pstmt2);
		}
	}
}