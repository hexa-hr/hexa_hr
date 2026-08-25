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
				// 회사 정보 불러오기
				info.put("companyName", rs.getString("company_name"));
				info.put("repTitle", rs.getString("representative_title"));
				info.put("repName", rs.getString("representative_name"));
				info.put("businessNumber", rs.getString("business_number"));
				info.put("corpNumber", rs.getString("corporation_number"));
				info.put("establishmentDate", rs.getString("establishment_date"));
				info.put("website", rs.getString("website"));
				info.put("officeAddress", rs.getString("office_address"));
				info.put("bizType", rs.getString("business_type"));
				info.put("bizItem", rs.getString("business_item"));

				// 회사 전화번호 쪼개기
				String phone = rs.getString("phone_number");
				if (phone != null && phone.split("-").length == 3) {
					String[] p = phone.split("-");
					info.put("phone1", p[0]);
					info.put("phone2", p[1]);
					info.put("phone3", p[2]);
				}
				// 팩스번호 쪼개기
				String fax = rs.getString("fax_number");
				if (fax != null && fax.split("-").length == 3) {
					String[] f = fax.split("-");
					info.put("fax1", f[0]);
					info.put("fax2", f[1]);
					info.put("fax3", f[2]);
				}

				// 담당자 정보 불러오기
				info.put("contactName", rs.getString("contact_name"));
				info.put("departmentId", rs.getString("department_id"));
				info.put("positionId", rs.getString("position_id"));
				info.put("email", rs.getString("email"));

				// 담당자 전화번호 쪼개기
				String cPhone = rs.getString("con_phone_number");
				if (cPhone != null && cPhone.split("-").length == 3) {
					String[] cp = cPhone.split("-");
					info.put("cPhone1", cp[0]);
					info.put("cPhone2", cp[1]);
					info.put("cPhone3", cp[2]);
				}
				// 담당자 휴대폰번호 쪼개기
				String mobile = rs.getString("mobile_number");
				if (mobile != null && mobile.split("-").length == 3) {
					String[] m = mobile.split("-");
					info.put("mobile1", m[0]);
					info.put("mobile2", m[1]);
					info.put("mobile3", m[2]);
				}
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

	// 🌟 회사 정보 및 담당자 정보 모든 컬럼 저장 로직 반영!
	public void updateUserInfo(Connection conn, Map<String, String> data) throws SQLException {

		// 1. company_info 테이블 저장/수정
		boolean companyExists = false;
		try (PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM company_info WHERE company_id = 1");
			ResultSet rs = checkStmt.executeQuery()) {
			if (rs.next())
				companyExists = true;
		}

		String estDate = data.get("establishmentDate");

		if (companyExists) {
			String sql1 = "UPDATE company_info SET company_name=?, business_number=?, establishment_date=TO_DATE(?, 'YYYY-MM-DD'), "
				+ "office_address=?, phone_number=?, representative_title=?, representative_name=?, corporation_number=?, "
				+ "website=?, fax_number=?, business_type=?, business_item=? WHERE company_id = 1";
			try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
				pstmt.setString(1, data.get("companyName"));
				pstmt.setString(2, data.get("businessNumber"));
				pstmt.setString(3, (estDate != null && !estDate.isEmpty()) ? estDate : null);
				pstmt.setString(4, data.get("officeAddress"));
				pstmt.setString(5, data.get("phoneNumber"));
				pstmt.setString(6, data.get("repTitle"));
				pstmt.setString(7, data.get("repName"));
				pstmt.setString(8, data.get("corpNumber"));
				pstmt.setString(9, data.get("website"));
				pstmt.setString(10, data.get("faxNumber"));
				pstmt.setString(11, data.get("bizType"));
				pstmt.setString(12, data.get("bizItem"));
				pstmt.executeUpdate();
			}
		} else {
			String sql1 = "INSERT INTO company_info (company_id, company_name, business_number, establishment_date, "
				+ "office_address, phone_number, representative_title, representative_name, corporation_number, "
				+ "website, fax_number, business_type, business_item) "
				+ "VALUES (1, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
				pstmt.setString(1, data.get("companyName"));
				pstmt.setString(2, data.get("businessNumber"));
				pstmt.setString(3, (estDate != null && !estDate.isEmpty()) ? estDate : null);
				pstmt.setString(4, data.get("officeAddress"));
				pstmt.setString(5, data.get("phoneNumber"));
				pstmt.setString(6, data.get("repTitle"));
				pstmt.setString(7, data.get("repName"));
				pstmt.setString(8, data.get("corpNumber"));
				pstmt.setString(9, data.get("website"));
				pstmt.setString(10, data.get("faxNumber"));
				pstmt.setString(11, data.get("bizType"));
				pstmt.setString(12, data.get("bizItem"));
				pstmt.executeUpdate();
			}
		}

		// 2. contact_person_info 테이블 저장/수정
		boolean contactExists = false;
		try (
			PreparedStatement checkStmt = conn
				.prepareStatement("SELECT 1 FROM contact_person_info WHERE company_id = 1");
			ResultSet rs = checkStmt.executeQuery()) {
			if (rs.next())
				contactExists = true;
		}

		Integer deptId = (data.get("departmentId") != null && !data.get("departmentId").isEmpty())
			? Integer.parseInt(data.get("departmentId")) : null;
		Integer posId = (data.get("positionId") != null && !data.get("positionId").isEmpty())
			? Integer.parseInt(data.get("positionId")) : null;

		if (contactExists) {
			String sql2 = "UPDATE contact_person_info SET contact_name=?, department_id=?, position_id=?, email=?, con_phone_number=?, mobile_number=? WHERE company_id = 1";
			try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
				pstmt.setString(1, data.get("contactName"));
				if (deptId != null)
					pstmt.setInt(2, deptId);
				else
					pstmt.setNull(2, java.sql.Types.INTEGER);
				if (posId != null)
					pstmt.setInt(3, posId);
				else
					pstmt.setNull(3, java.sql.Types.INTEGER);
				pstmt.setString(4, data.get("email"));
				pstmt.setString(5, data.get("conPhoneNumber"));
				pstmt.setString(6, data.get("mobileNumber"));
				pstmt.executeUpdate();
			}
		} else {
			String sql2 = "INSERT INTO contact_person_info (person_id, company_id, contact_name, department_id, position_id, email, con_phone_number, mobile_number) "
				+ "VALUES ((SELECT NVL(MAX(person_id), 0) + 1 FROM contact_person_info), 1, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
				pstmt.setString(1, data.get("contactName"));
				if (deptId != null)
					pstmt.setInt(2, deptId);
				else
					pstmt.setNull(2, java.sql.Types.INTEGER);
				if (posId != null)
					pstmt.setInt(3, posId);
				else
					pstmt.setNull(3, java.sql.Types.INTEGER);
				pstmt.setString(4, data.get("email"));
				pstmt.setString(5, data.get("conPhoneNumber"));
				pstmt.setString(6, data.get("mobileNumber"));
				pstmt.executeUpdate();
			}
		}
	}

	public void updateAllEmployeeSalaryDates(java.sql.Connection conn, Integer calc1, Integer calc2,
		Integer paymentDate,
		String type1, String type2, String pType, String bank, String accNum, String deposit)
		throws java.sql.SQLException {

		String sql = "UPDATE employee_salary_account SET salary_calculation1=?, salary_calculation2=?, salary_payment_date=?, calc1_month_type=?, calc2_month_type=?, payment_month_type=?, bank_name=?, account_number=?, deposit_stocks=?";
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