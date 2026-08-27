package vacation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jdbc.JdbcUtil;
import vacation.model.VacationType;

public class VacationTypeDao {

	// 全体目録照会
	public List<VacationType> selectAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<VacationType> list = new ArrayList<>();
		String sql = "SELECT vacation_type_id, vacation_type_name, apply_period1, apply_period2, usage FROM vacation_type ORDER BY vacation_type_id DESC";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				VacationType v = new VacationType();
				v.setVacationTypeId(rs.getInt("vacation_type_id"));
				v.setVacationTypeName(rs.getString("vacation_type_name"));
				v.setApplyPeriod1(rs.getDate("apply_period1"));
				v.setApplyPeriod2(rs.getDate("apply_period2"));
				v.setUsage(rs.getString("usage"));
				list.add(v);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 👉 [追加] 同じ休暇項目名が既に存在するか確認するメソッド
	public boolean isDuplicateName(Connection conn, String vacationTypeName) throws SQLException {
		String sql = "SELECT COUNT(*) FROM vacation_type WHERE vacation_type_name = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, vacationTypeName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}

	// 休暇項目を追加（重複チェックロジックを含む）
	public void insert(Connection conn, VacationType vacation) throws SQLException {
		// 1. 登録しようとする名前がすでに存在するか検査
		if (isDuplicateName(conn, vacation.getVacationTypeName())) {
			throw new SQLException("既に存在する休暇項目の名前です。"); // 重複時の例外発生
		}

		PreparedStatement pstmt = null;
		String sql = "INSERT INTO vacation_type (vacation_type_id, vacation_type_name, apply_period1, apply_period2, usage) "
			+ "VALUES (vacation_type_seq.NEXTVAL, ?, ?, ?, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vacation.getVacationTypeName());
			pstmt.setDate(2, new java.sql.Date(vacation.getApplyPeriod1().getTime()));
			pstmt.setDate(3, new java.sql.Date(vacation.getApplyPeriod2().getTime()));
			pstmt.setString(4, vacation.getUsage());
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	public int update(Connection conn, VacationType vacation) throws SQLException {
		// 👉 [追加] 修正しようとする名前が自分を除いてすでに存在するかを検査
		if (isDuplicateNameForUpdate(conn, vacation.getVacationTypeId(), vacation.getVacationTypeName())) {
			throw new SQLException("既に存在する休暇項目の名前です。"); // 重複時の例外発生
		}

		String sql = "UPDATE vacation_type SET "
			+ " vacation_type_name = ?, "
			+ " apply_period1 = ?, "
			+ " apply_period2 = ?, "
			+ " usage = ? "
			+ "WHERE vacation_type_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, vacation.getVacationTypeName());

			if (vacation.getApplyPeriod1() != null) {
				pstmt.setDate(2, new java.sql.Date(vacation.getApplyPeriod1().getTime()));
			} else {
				pstmt.setNull(2, java.sql.Types.DATE);
			}

			if (vacation.getApplyPeriod2() != null) {
				pstmt.setDate(3, new java.sql.Date(vacation.getApplyPeriod2().getTime()));
			} else {
				pstmt.setNull(3, java.sql.Types.DATE);
			}

			pstmt.setString(4, vacation.getUsage());
			pstmt.setInt(5, vacation.getVacationTypeId());

			return pstmt.executeUpdate();
		}
	}

	public int delete(Connection conn, int vacationTypeId) throws SQLException {
		String sql = "DELETE FROM vacation_type WHERE vacation_type_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, vacationTypeId);
			return pstmt.executeUpdate();
		}
	}

	public boolean isDuplicateNameForUpdate(Connection conn, int vacationTypeId, String vacationTypeName)
		throws SQLException {
		String sql = "SELECT COUNT(*) FROM vacation_type WHERE vacation_type_name = ? AND vacation_type_id != ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, vacationTypeName);
			pstmt.setInt(2, vacationTypeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}

	// 該当する休暇項目が別のテーブル（vacation_days, attendance_type)で使用中か確認
	public boolean isUsedInVacationDays(Connection conn, int vacationTypeId) throws SQLException {
		// 1. vacation_daysテーブルの参照確認
		String sql1 = "SELECT COUNT(*) FROM vacation_days WHERE vacation_type_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
			pstmt.setInt(1, vacationTypeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					return true;
				}
			}
		}

		// 2. attendance_typeテーブルの参照確認
		String sql2 = "SELECT COUNT(*) FROM attendance_type WHERE vacation_type_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql2)) {
			pstmt.setInt(1, vacationTypeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					return true;
				}
			}
		}

		return false;
	}
}