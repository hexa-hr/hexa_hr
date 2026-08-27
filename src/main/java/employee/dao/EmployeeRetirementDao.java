package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.EmployeeRetirementListDto;

public class EmployeeRetirementDao {

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

	public List<EmployeeRetirementListDto> selectRetirementList(Connection conn, String statusFilter, int startRow,
		int endRow) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ( ");
		sql.append("    SELECT ROWNUM AS rnum, a.* FROM ( ");
		sql.append("        SELECT e.employee_id, e.status, e.korean_name, d.department_name, p.position_name, ");
		sql.append("               e.hire_date, e.resignation_date, ");
		sql.append(
			"               TRUNC(MONTHS_BETWEEN(NVL(e.resignation_date, SYSDATE), e.hire_date) / 12) AS years_of_service, ");
		sql.append("               CASE WHEN r.retirement_type IS NOT NULL THEN 'O' ELSE 'X' END AS is_settled, ");
		sql.append("               r.retirement_type, r.retirement_reason, r.contact_after_retirement ");
		sql.append("        FROM employee e ");
		sql.append("        LEFT JOIN department d ON e.department_id = d.department_id ");
		sql.append("        LEFT JOIN position p ON e.position_id = p.position_id ");
		sql.append("        LEFT JOIN retirement r ON e.employee_id = r.employee_id ");

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

	// 🌟 3. 퇴직 처리 (시퀀스 에러 100% 방지 버전)
	public void processRetirement(Connection conn, int empId, String type, java.sql.Date date, String reason,
		String contact) throws SQLException {

		// 1) 사원 상태를 '퇴직'으로, 퇴사일자를 업데이트
		String updateSql = "UPDATE employee SET status = '퇴직', resignation_date = ? WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
			pstmt.setDate(1, date);
			pstmt.setInt(2, empId);
			pstmt.executeUpdate();
		}

		// 2) 혹시 꼬여있는 기존 데이터 날리기
		String deleteSql = "DELETE FROM retirement WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
			pstmt.setInt(1, empId);
			pstmt.executeUpdate();
		}

		// 3) retirement_seq 시퀀스 대신 MAX값+1 로 고유번호를 자동 생성 (에러 완벽 차단)
		String insertSql = "INSERT INTO retirement (retirement_id, employee_id, retirement_type, retirement_date, retirement_reason, contact_after_retirement) "
			+ "VALUES ((SELECT NVL(MAX(retirement_id), 0) + 1 FROM retirement), ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
			pstmt.setInt(1, empId);
			pstmt.setString(2, type);
			pstmt.setDate(3, date);
			pstmt.setString(4, reason);
			pstmt.setString(5, contact);
			pstmt.executeUpdate();
		}
	}

	// 🌟 4. 퇴직 취소
	public void cancelRetirement(Connection conn, int empId) throws SQLException {
		String updateSql = "UPDATE employee SET status = '재직', resignation_date = NULL WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
			pstmt.setInt(1, empId);
			pstmt.executeUpdate();
		}

		String deleteSql = "DELETE FROM retirement WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
			pstmt.setInt(1, empId);
			pstmt.executeUpdate();
		}
	}
}