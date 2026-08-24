package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import employee.model.Employee;
import employee.model.EmployeeSelectRow;
import jdbc.JdbcUtil;

public class EmployeeDao {

	public List<EmployeeSelectRow> selectEmployeeRows(Connection conn, String keyword, Integer departmentId,
		String status) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(
			"SELECT e.employee_id, e.employment_type, e.korean_name, d.department_name, p.position_name, e.status ");
		sql.append("FROM employee e ");
		sql.append("LEFT JOIN department d ON d.department_id = e.department_id ");
		sql.append("LEFT JOIN position p ON p.position_id = e.position_id ");
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
						rs.getInt("employee_id"), rs.getString("employment_type"),
						rs.getString("korean_name"), rs.getString("department_name"),
						rs.getString("position_name"), rs.getString("status"));
					result.add(row);
				}
			}
		}
		return result;
	}

	public Employee selectById(Connection conn, int employeeId) throws SQLException {
		String sql = "SELECT * FROM employee WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Employee(
						rs.getInt("employee_id"),
						rs.getObject("account_id") != null ? rs.getInt("account_id") : null,
						rs.getObject("company_id") != null ? rs.getInt("company_id") : null,
						rs.getObject("person_id") != null ? rs.getInt("person_id") : null,
						rs.getString("employment_type"), rs.getString("korean_name"), rs.getString("english_name"),
						rs.getDate("hire_date"), rs.getDate("resignation_date"),
						rs.getObject("department_id") != null ? rs.getInt("department_id") : null,
						rs.getObject("position_id") != null ? rs.getInt("position_id") : null,
						rs.getString("foreign_or_domestic"), rs.getString("resident_number1"),
						rs.getString("resident_number2"),
						rs.getString("address"), rs.getString("tel_phone"), rs.getString("mobile"),
						rs.getString("email"), rs.getString("sns"), rs.getString("other_details"),
						rs.getString("status"),
						rs.getObject("basic_pay") != null ? rs.getLong("basic_pay") : null);
				}
			}
		}
		return null;
	}

	public String selectEmploymentType(Connection conn, Integer employeeId) throws SQLException {
		String sql = "SELECT employment_type FROM employee WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getString("employment_type");
			}
		}
		return null;
	}

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

	public void insert(Connection conn, Employee employee) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO employee " +
					"(employee_id, account_id, company_id, person_id, employment_type, korean_name, english_name, " +
					"hire_date, resignation_date, department_id, position_id, foreign_or_domestic, " +
					"resident_number1, resident_number2, address, tel_phone, mobile, email, sns, other_details, status, basic_pay) "
					+
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setInt(1, employee.getEmployeeId());
			if (employee.getAccountId() != null)
				pstmt.setInt(2, employee.getAccountId());
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);
			if (employee.getCompanyId() != null)
				pstmt.setInt(3, employee.getCompanyId());
			else
				pstmt.setNull(3, java.sql.Types.INTEGER);
			if (employee.getPersonId() != null)
				pstmt.setInt(4, employee.getPersonId());
			else
				pstmt.setNull(4, java.sql.Types.INTEGER);
			pstmt.setString(5, employee.getEmploymentType());
			pstmt.setString(6, employee.getKoreanName());
			pstmt.setString(7, employee.getEnglishName());
			if (employee.getHireDate() != null)
				pstmt.setDate(8, new java.sql.Date(employee.getHireDate().getTime()));
			else
				pstmt.setNull(8, java.sql.Types.DATE);
			if (employee.getResignationDate() != null)
				pstmt.setDate(9, new java.sql.Date(employee.getResignationDate().getTime()));
			else
				pstmt.setNull(9, java.sql.Types.DATE);
			if (employee.getDepartmentId() != null && employee.getDepartmentId() > 0)
				pstmt.setInt(10, employee.getDepartmentId());
			else
				pstmt.setNull(10, java.sql.Types.INTEGER);
			if (employee.getPositionId() != null && employee.getPositionId() > 0)
				pstmt.setInt(11, employee.getPositionId());
			else
				pstmt.setNull(11, java.sql.Types.INTEGER);
			pstmt.setString(12, employee.getForeignOrDomestic());
			pstmt.setString(13, employee.getResidentNumber1());
			pstmt.setString(14, employee.getResidentNumber2());
			pstmt.setString(15, employee.getAddress());
			pstmt.setString(16, employee.getTelPhone());
			pstmt.setString(17, employee.getMobile());
			pstmt.setString(18, employee.getEmail());
			pstmt.setString(19, employee.getSns());
			pstmt.setString(20, employee.getOtherDetails());
			pstmt.setString(21, employee.getStatus());
			if (employee.getBasicPay() != null)
				pstmt.setLong(22, employee.getBasicPay());
			else
				pstmt.setNull(22, java.sql.Types.INTEGER);

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// =========================================================================
	// 🌟 추가된 부분: 부서 및 직위 목록 불러오기
	// =========================================================================
	public List<Map<String, Object>> selectDepartments(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM department ORDER BY department_id");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", rs.getInt("department_id"));
				map.put("name", rs.getString("department_name"));
				list.add(map);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	public List<Map<String, Object>> selectPositions(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			pstmt = conn.prepareStatement("SELECT * FROM position ORDER BY position_id");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", rs.getInt("position_id"));
				map.put("name", rs.getString("position_name"));
				list.add(map);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
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