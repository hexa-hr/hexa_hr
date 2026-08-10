package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import master.model.CompanyInfo;

public class CompanyInfoDao {

	/**
	 * 1. 회사 정보 조회 (select)
	 */
	public CompanyInfo select(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// ※ 실제 DB에 만들어진 테이블명(COMPANY_INFO 등)에 맞게 수정해 주세요.
			String sql = "SELECT * FROM COMPANY_INFO WHERE COMPANY_ID = 1"; // 예시 조건
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				CompanyInfo info = new CompanyInfo();

				// DB 테이블의 컬럼명에 맞게 데이터를 꺼내서 DTO 바구니에 담습니다.
				// rs.getString("DB컬럼명")
				info.setCompanyName(rs.getString("COMPANY_NAME"));
				info.setBusinessNumber(rs.getString("BUSINESS_NUMBER"));
				info.setAddress(rs.getString("ADDRESS"));
				// ... CompanyInfo.java에 선언한 나머지 변수들도 똑같이 세팅해 주세요 ...

				return info;
			}
			return null; // DB에 데이터가 없으면 null 반환
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * 2. 회사 정보 수정 (update)
	 */
	public int update(Connection conn, CompanyInfo info) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// ※ 실제 DB 테이블명과 수정할 컬럼명들에 맞게 쿼리문을 수정해 주세요.
			String sql = "UPDATE COMPANY_INFO SET COMPANY_NAME = ?, BUSINESS_NUMBER = ?, ADDRESS = ? WHERE COMPANY_ID = 1";
			pstmt = conn.prepareStatement(sql);

			// 물음표(?) 순서에 맞게 DTO 바구니에서 값을 꺼내 세팅합니다.
			pstmt.setString(1, info.getCompanyName());
			pstmt.setString(2, info.getBusinessNumber());
			pstmt.setString(3, info.getAddress());
			// ... 나머지 수정할 항목들도 전부 세팅해 주세요 ...

			return pstmt.executeUpdate(); // DB 업데이트 실행
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}