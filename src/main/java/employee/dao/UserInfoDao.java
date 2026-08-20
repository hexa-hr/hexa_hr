package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.JdbcUtil;

public class UserInfoDao {

	public Map<String, String> selectCompanyInfo(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String, String> info = new HashMap<>();
		String sql = "SELECT c.*, p.contact_name, p.con_phone_number, p.mobile_number, p.email, p.department_id, p.position_id "
			+ "FROM company_info c "
			+ "LEFT JOIN contact_person_info p ON c.company_id = p.company_id WHERE c.company_id = 1";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				info.put("companyName", rs.getString("company_name"));
				info.put("businessNumber", rs.getString("business_number"));
				info.put("corporationNumber", rs.getString("corporation_number"));
				info.put("establishmentDate", rs.getString("establishment_date"));
				info.put("website", rs.getString("website"));
				info.put("officeAddress", rs.getString("office_address"));
				info.put("phoneNumber", rs.getString("phone_number"));
				info.put("faxNumber", rs.getString("fax_number"));
				info.put("businessType", rs.getString("business_type"));
				info.put("businessItem", rs.getString("business_item"));
				info.put("contactName", rs.getString("contact_name"));
				info.put("email", rs.getString("email"));
				info.put("departmentId", rs.getString("department_id"));
				info.put("positionId", rs.getString("position_id"));
			}
			return info;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

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

	public void insertDepartment(Connection conn, String name) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO department (department_id, department_name) VALUES ((SELECT NVL(MAX(department_id), 0) + 1 FROM department), ?)")) {
			pstmt.setString(1, name);
			pstmt.executeUpdate();
		}
	}

	public void updateDepartment(Connection conn, int id, String name) throws SQLException {
		try (PreparedStatement pstmt = conn
			.prepareStatement("UPDATE department SET department_name = ? WHERE department_id = ?")) {
			pstmt.setString(1, name);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}
	}

	public void deleteDepartment(Connection conn, int id) throws SQLException {
		try (
			PreparedStatement p1 = conn
				.prepareStatement("UPDATE employee SET department_id = NULL WHERE department_id = ?");
			PreparedStatement p2 = conn
				.prepareStatement("UPDATE contact_person_info SET department_id = NULL WHERE department_id = ?")) {
			p1.setInt(1, id);
			p1.executeUpdate();
			p2.setInt(1, id);
			p2.executeUpdate();
		}
		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM department WHERE department_id = ?")) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}

	public void insertPosition(Connection conn, String name) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement(
			"INSERT INTO position (position_id, position_name) VALUES ((SELECT NVL(MAX(position_id), 0) + 1 FROM position), ?)")) {
			pstmt.setString(1, name);
			pstmt.executeUpdate();
		}
	}

	public void updatePosition(Connection conn, int id, String name) throws SQLException {
		try (PreparedStatement pstmt = conn
			.prepareStatement("UPDATE position SET position_name = ? WHERE position_id = ?")) {
			pstmt.setString(1, name);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}
	}

	public void deletePosition(Connection conn, int id) throws SQLException {
		try (
			PreparedStatement p1 = conn
				.prepareStatement("UPDATE employee SET position_id = NULL WHERE position_id = ?");
			PreparedStatement p2 = conn
				.prepareStatement("UPDATE contact_person_info SET position_id = NULL WHERE position_id = ?")) {
			p1.setInt(1, id);
			p1.executeUpdate();
			p2.setInt(1, id);
			p2.executeUpdate();
		}
		try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM position WHERE position_id = ?")) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}

	// 🌟 DB 업데이트 쿼리 (날짜는 완벽한 형태일 때만 들어가도록 설계)
	public void updateUserInfo(Connection conn, Map<String, String> data) throws SQLException {
		String sql1 = "UPDATE company_info SET company_name=?, business_number=?, establishment_date=TO_DATE(?, 'YYYY-MM-DD'), office_address=?, phone_number=? WHERE company_id = 1";
		try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
			pstmt.setString(1, data.get("companyName"));
			pstmt.setString(2, data.get("businessNumber"));
			pstmt.setString(3, data.get("establishmentDate")); // 핸들러에서 안전한 값만 넘어옴!
			pstmt.setString(4, data.get("officeAddress"));
			pstmt.setString(5, data.get("phoneNumber"));
			pstmt.executeUpdate();
		}
		String sql2 = "UPDATE contact_person_info SET contact_name=?, department_id=?, position_id=?, email=? WHERE company_id = 1";
		try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
			pstmt.setString(1, data.get("contactName"));
			if (data.get("departmentId") != null && !data.get("departmentId").isEmpty())
				pstmt.setInt(2, Integer.parseInt(data.get("departmentId")));
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);
			if (data.get("positionId") != null && !data.get("positionId").isEmpty())
				pstmt.setInt(3, Integer.parseInt(data.get("positionId")));
			else
				pstmt.setNull(3, java.sql.Types.INTEGER);
			pstmt.setString(4, data.get("email"));
			pstmt.executeUpdate();
		}
	}

	// 🌟 새로 추가된 부분: 모든 사원의 급여일 및 계좌 정보 테이블 일괄 업데이트 실행!
	public void updateAllEmployeeSalaryDates(java.sql.Connection conn, Integer calc1, Integer calc2,
		Integer paymentDate,
		String type1, String type2, String pType, String bank, String accNum, String deposit)
		throws java.sql.SQLException {

		String sql = "UPDATE employee_salary_account SET "
			+ "salary_calculation1 = ?, salary_calculation2 = ?, salary_payment_date = ?, "
			+ "calc1_month_type = ?, calc2_month_type = ?, payment_month_type = ?, "
			+ "bank_name = ?, account_number = ?, deposit_stocks = ?";

		try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

			if (calc1 != null)
				pstmt.setInt(1, calc1);
			else
				pstmt.setNull(1, java.sql.Types.INTEGER);
			if (calc2 != null)
				pstmt.setInt(2, calc2);
			else
				pstmt.setNull(2, java.sql.Types.INTEGER);
			if (paymentDate != null)
				pstmt.setInt(3, paymentDate);
			else
				pstmt.setNull(3, java.sql.Types.INTEGER);

			pstmt.setString(4, type1);
			pstmt.setString(5, type2);
			pstmt.setString(6, pType);
			pstmt.setString(7, bank);
			pstmt.setString(8, accNum);
			pstmt.setString(9, deposit);

			pstmt.executeUpdate();
		}
	}
}