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
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit, tax_free_name "
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
					rs.getLong("tax_free_limit"),
					rs.getString("tax_free_name"));
				list.add(wage);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 신규 등록
	public void insert(Connection conn, WageType wageType) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

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

		String sql = "INSERT INTO wage_type ("
			+ "wage_type_id, wage_type_name, number_cut, attendance_or_lumpsum, "
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit, tax_free_name"
			+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
			pstmt.setString(10, wageType.getTaxFreeName());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 정보 수정
	public void update(Connection conn, WageType wageType) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "UPDATE wage_type SET "
			+ "wage_type_name = ?, number_cut = ?, attendance_or_lumpsum = ?, "
			+ "attendance_or_lumpsum_content = ?, usage = ?, item_type = ?, "
			+ "taxable_yn = ?, tax_free_limit = ?, tax_free_name = ? "
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
			pstmt.setString(9, wageType.getTaxFreeName());
			pstmt.setInt(10, wageType.getWageTypeId());

			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 삭제 기능 추가
	public int delete(Connection conn, Integer wageTypeId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM wage_type WHERE wage_type_id = ?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, wageTypeId);
			return pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 지급 또는 공제 구분별 목록 조회 (item_type: 'WAGE' 또는 'DEDUCTION')
	public List<WageType> selectByType(Connection conn, String itemType) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<WageType> list = new ArrayList<>();
		String sql = "SELECT wage_type_id, wage_type_name, number_cut, attendance_or_lumpsum, "
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit, tax_free_name "
			+ "FROM wage_type WHERE item_type = ? ORDER BY wage_type_id DESC";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, itemType);
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
					rs.getLong("tax_free_limit"),
					rs.getString("tax_free_name"));
				list.add(wage);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// [추가] 신규 등록 시 이름 중복 확인
	public boolean isDuplicateName(Connection conn, String wageTypeName) throws SQLException {
		String sql = "SELECT COUNT(*) FROM wage_type WHERE wage_type_name = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, wageTypeName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		}
		return false;
	}

	// [추가] 수정 시 자기 자신을 제외하고 이름 중복 확인
	public boolean isDuplicateNameForUpdate(Connection conn, int wageTypeId, String wageTypeName)
		throws SQLException {
		String sql = "SELECT COUNT(*) FROM wage_type WHERE wage_type_name = ? AND wage_type_id != ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, wageTypeName);
			pstmt.setInt(2, wageTypeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		}
		return false;
	}

	// [추가] ID로 특정 지급/공제 항목 조회
	public WageType selectById(Connection conn, int wageTypeId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT wage_type_id, wage_type_name, number_cut, attendance_or_lumpsum, "
			+ "attendance_or_lumpsum_content, usage, item_type, taxable_yn, tax_free_limit, tax_free_name "
			+ "FROM wage_type WHERE wage_type_id = ?";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, wageTypeId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return new WageType(
					rs.getInt("wage_type_id"),
					rs.getString("wage_type_name"),
					rs.getString("number_cut"),
					rs.getString("attendance_or_lumpsum"),
					rs.getString("attendance_or_lumpsum_content"),
					rs.getString("usage"),
					rs.getString("item_type"),
					rs.getString("taxable_yn"),
					rs.getLong("tax_free_limit"),
					rs.getString("tax_free_name"));
			}
			return null;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

}