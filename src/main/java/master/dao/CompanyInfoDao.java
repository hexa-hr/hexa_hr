package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import master.model.CompanyInfo;

public class CompanyInfoDao {

	// 1. 단일 회사 정보 조회 (SELECT)
	public CompanyInfo selectById(Connection conn, int companyId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT * FROM company_info WHERE company_id = ?");
			pstmt.setInt(1, companyId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return new CompanyInfo(
					rs.getInt("company_id"),
					rs.getString("company_name"),
					rs.getString("representative_title"),
					rs.getString("representative_name"),
					rs.getString("business_number"),
					rs.getString("corporation_number"),
					rs.getDate("establishment_date"),
					rs.getString("website"),
					rs.getString("office_address"),
					rs.getString("phone_number"),
					rs.getString("fax_number"),
					rs.getString("business_type"),
					rs.getString("business_item"));
			}
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 회사 정보 신규 저장 (INSERT)
	public void insert(Connection conn, CompanyInfo company) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"INSERT INTO company_info " +
					"(company_name, representative_title, representative_name, business_number, " +
					"corporation_number, establishment_date, website, office_address, " +
					"phone_number, fax_number, business_type, business_item) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			pstmt.setString(1, company.getCompanyName());
			pstmt.setString(2, company.getRepresentativeTitle());
			pstmt.setString(3, company.getRepresentativeName());
			pstmt.setString(4, company.getBusinessNumber());
			pstmt.setString(5, company.getCorporationNumber());

			if (company.getEstablishmentDate() != null) {
				pstmt.setDate(6, new java.sql.Date(company.getEstablishmentDate().getTime()));
			} else {
				pstmt.setNull(6, java.sql.Types.DATE);
			}

			pstmt.setString(7, company.getWebsite());
			pstmt.setString(8, company.getOfficeAddress());
			pstmt.setString(9, company.getPhoneNumber());
			pstmt.setString(10, company.getFaxNumber());
			pstmt.setString(11, company.getBusinessType());
			pstmt.setString(12, company.getBusinessItem());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 3. 회사 정보 수정 (UPDATE)
	public int update(Connection conn, CompanyInfo company) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
				"UPDATE company_info SET " +
					"company_name=?, representative_title=?, representative_name=?, business_number=?, " +
					"corporation_number=?, establishment_date=?, website=?, office_address=?, " +
					"phone_number=?, fax_number=?, business_type=?, business_item=? " +
					"WHERE company_id=?");

			pstmt.setString(1, company.getCompanyName());
			pstmt.setString(2, company.getRepresentativeTitle());
			pstmt.setString(3, company.getRepresentativeName());
			pstmt.setString(4, company.getBusinessNumber());
			pstmt.setString(5, company.getCorporationNumber());

			if (company.getEstablishmentDate() != null) {
				pstmt.setDate(6, new java.sql.Date(company.getEstablishmentDate().getTime()));
			} else {
				pstmt.setNull(6, java.sql.Types.DATE);
			}

			pstmt.setString(7, company.getWebsite());
			pstmt.setString(8, company.getOfficeAddress());
			pstmt.setString(9, company.getPhoneNumber());
			pstmt.setString(10, company.getFaxNumber());
			pstmt.setString(11, company.getBusinessType());
			pstmt.setString(12, company.getBusinessItem());
			pstmt.setInt(13, company.getCompanyId());

			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}