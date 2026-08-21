package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import employee.model.Employee;
import employee.model.EmployeeSelectRow;
import jdbc.JdbcUtil;

public class EmployeeDao {

	public List<EmployeeSelectRow> selectEmployeeRows(
		Connection conn,
		String keyword,
		Integer departmentId,
		String status)
		throws SQLException {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT e.employee_id, ");
		sql.append("       e.employment_type, ");
		sql.append("       e.korean_name, ");
		sql.append("       d.department_name, ");
		sql.append("       p.position_name, ");
		sql.append("       e.status ");
		sql.append("FROM employee e ");
		sql.append("LEFT JOIN department d ");
		sql.append("  ON d.department_id = e.department_id ");
		sql.append("LEFT JOIN position p ");
		sql.append("  ON p.position_id = e.position_id ");
		sql.append("WHERE 1 = 1 ");

		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.trim().isEmpty()) {
			sql.append("AND e.korean_name LIKE ? ");
			params.add("%" + keyword.trim() + "%");
		}

		if (departmentId != null && departmentId > 0) {
			sql.append("AND e.department_id = ? ");
			params.add(departmentId);
		}

		if (status != null && !status.trim().isEmpty()) {
			sql.append("AND e.status = ? ");
			params.add(status.trim());
		}

		sql.append("ORDER BY e.employee_id");

		List<EmployeeSelectRow> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					EmployeeSelectRow row = new EmployeeSelectRow(
						rs.getInt("employee_id"),
						rs.getString("employment_type"),
						rs.getString("korean_name"),
						rs.getString("department_name"),
						rs.getString("position_name"),
						rs.getString("status"));

					result.add(row);
				}
			}
		}

		return result;
	}

	public String selectEmploymentType(Connection conn, Integer employeeId) throws SQLException {
		String sql = "SELECT employment_type FROM employee WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("employment_type");
				}
			}
		}
		return null;
	}

	// =========================================================================
	// 🌟 다중 테이블 저장을 위해 사원 번호표를 미리 뽑는 메서드 추가!
	// =========================================================================
	public Integer getNextId(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT employee_seq.nextval FROM dual");
			rs = pstmt.executeQuery();
			if (rs.next())
				return rs.getInt(1);
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// =========================================================================
	// 🌟 사원 등록(INSERT) 메서드 (미리 뽑은 사원번호 ? 적용)
	// =========================================================================
	public void insert(Connection conn, Employee employee) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// employee_seq.nextval 대신 1번 파라미터로 ? 를 받도록 수정!
			pstmt = conn.prepareStatement(
				"INSERT INTO employee " +
					"(employee_id, account_id, company_id, person_id, employment_type, korean_name, english_name, " +
					"hire_date, resignation_date, department_id, position_id, foreign_or_domestic, " +
					"resident_number1, resident_number2, address, tel_phone, mobile, email, sns, other_details, status) "
					+
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			// 1. employeeId (Service에서 미리 뽑아온 번호 세팅)
			pstmt.setInt(1, employee.getEmployeeId());

			// 2. accountId (NULL 허용 처리)
			if (employee.getAccountId() != null)
				pstmt.setInt(2, employee.getAccountId());
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);

			// 3. companyId (NULL 허용 처리)
			if (employee.getCompanyId() != null)
				pstmt.setInt(3, employee.getCompanyId());
			else
				pstmt.setNull(3, java.sql.Types.INTEGER);

			// 4. personId (NULL 허용 처리)
			if (employee.getPersonId() != null)
				pstmt.setInt(4, employee.getPersonId());
			else
				pstmt.setNull(4, java.sql.Types.INTEGER);

			// 5. employmentType ~ 7. englishName
			pstmt.setString(5, employee.getEmploymentType());
			pstmt.setString(6, employee.getKoreanName());
			pstmt.setString(7, employee.getEnglishName());

			// 8. hireDate (날짜 처리)
			if (employee.getHireDate() != null)
				pstmt.setDate(8, new java.sql.Date(employee.getHireDate().getTime()));
			else
				pstmt.setNull(8, java.sql.Types.DATE);

			// 9. resignationDate (날짜 처리)
			if (employee.getResignationDate() != null)
				pstmt.setDate(9, new java.sql.Date(employee.getResignationDate().getTime()));
			else
				pstmt.setNull(9, java.sql.Types.DATE);

			// 10. departmentId (NULL 허용 처리)
			if (employee.getDepartmentId() != null && employee.getDepartmentId() > 0)
				pstmt.setInt(10, employee.getDepartmentId());
			else
				pstmt.setNull(10, java.sql.Types.INTEGER);

			// 11. positionId (NULL 허용 처리)
			if (employee.getPositionId() != null && employee.getPositionId() > 0)
				pstmt.setInt(11, employee.getPositionId());
			else
				pstmt.setNull(11, java.sql.Types.INTEGER);

			// 12. foreignOrDomestic
			pstmt.setString(12, employee.getForeignOrDomestic());

			// 13 ~ 14. 주민등록번호 앞/뒷자리
			pstmt.setString(13, employee.getResidentNumber1());
			pstmt.setString(14, employee.getResidentNumber2());

			// 15 ~ 21. 나머지 문자열 데이터
			pstmt.setString(15, employee.getAddress());
			pstmt.setString(16, employee.getTelPhone());
			pstmt.setString(17, employee.getMobile());
			pstmt.setString(18, employee.getEmail());
			pstmt.setString(19, employee.getSns());
			pstmt.setString(20, employee.getOtherDetails());
			pstmt.setString(21, employee.getStatus());

			// 쿼리 실행
			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}
	// 사원등록 시 설정한 기본급 또는 일급 조회
	public Long selectBasicPay(
		Connection conn,
		Integer employeeId)
		throws SQLException {

		String sql = "SELECT basic_pay "
			+ "FROM employee "
			+ "WHERE employee_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(
				1,
				employeeId);

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					long basicPay = rs.getLong(
						"basic_pay");

					return rs.wasNull()
						? null
						: basicPay;
				}
			}
		}

		return null;
	}
}
