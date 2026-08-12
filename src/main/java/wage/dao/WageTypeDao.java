package wage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import master.model.WageType;

public class WageTypeDao {

	// 전체 목록 조회
	public List<WageType> selectAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<WageType> list = new ArrayList<>();
		String sql = "SELECT wage_type_id, wage_type_name, number_cut, attendance_or_lumpsum, "
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit "
			+ "FROM wage_type ORDER BY wage_type_id DESC";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				WageType wage = new WageType(
					rs.getInt("wage_type_id"),
					rs.getString("wage_type_name"),
					rs.getString("number_cut"),
					rs.getString("attendance_or_lumpsum"),
					rs.getString("attendance_or_lumpsum_content"),
					rs.getString("usage"),
					rs.getString("item_type"),
					rs.getString("taxable_yn"),
					rs.getLong("tax_free_limit"));
				list.add(wage);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 신규 등록 (DB 시퀀스 미사용 / MAX + 1 처리)
	public void insert(Connection conn, WageType wageType) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// 1. 현재 가장 큰 wage_type_id + 1 계산 (데이터 없으면 1)
		int nextId = 1;
		String selectMaxSql = "SELECT NVL(MAX(wage_type_id), 0) + 1 FROM wage_type";

		try {
			pstmt = conn.prepareStatement(selectMaxSql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				nextId = rs.getInt(1);
			}
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}

		// 2. 계산한 nextId로 INSERT 실행
		String sql = "INSERT INTO wage_type ("
			+ "wage_type_id, wage_type_name, number_cut, attendance_or_lumpsum, "
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit"
			+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, nextId);
			pstmt.setString(2, wageType.getWageTypeName());
			pstmt.setString(3, wageType.getNumberCut());
			pstmt.setString(4, wageType.getAttendanceOrLumpsum());
			pstmt.setString(5, wageType.getAttendanceOrLumpsumContent());
			pstmt.setString(6, wageType.getUsage());
			pstmt.setString(7, wageType.getItemType());
			pstmt.setString(8, wageType.getTaxableYn());

			if (wageType.getTaxFreeLimit() != null) {
				pstmt.setLong(9, wageType.getTaxFreeLimit());
			} else {
				pstmt.setLong(9, 0L);
			}

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 기존 insert 메서드 아래에 추가

	public void update(Connection conn, WageType wageType) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "UPDATE wage_type SET "
			+ "wage_type_name = ?, number_cut = ?, attendance_or_lumpsum = ?, "
			+ "attendance_or_lumpsum_content = ?, usage = ?, item_type = ?, "
			+ "taxable_yn = ?, tax_free_limit = ? "
			+ "WHERE wage_type_id = ?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, wageType.getWageTypeName());
			pstmt.setString(2, wageType.getNumberCut());
			pstmt.setString(3, wageType.getAttendanceOrLumpsum());
			pstmt.setString(4, wageType.getAttendanceOrLumpsumContent());
			pstmt.setString(5, wageType.getUsage());
			pstmt.setString(6, wageType.getItemType());
			pstmt.setString(7, wageType.getTaxableYn());

			if (wageType.getTaxFreeLimit() != null) {
				pstmt.setLong(8, wageType.getTaxFreeLimit());
			} else {
				pstmt.setLong(8, 0L);
			}

			pstmt.setInt(9, wageType.getWageTypeId());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}