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

	// 전체 목록 조회
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

	// 신규 항목 저장 (Oracle 시퀀스 vacation_type_seq 사용 예시)
	public void insert(Connection conn, VacationType vacation) throws SQLException {
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
		String sql = "UPDATE vacation_type SET "
			+ " vacation_type_name = ?, "
			+ " apply_period1 = ?, "
			+ " apply_period2 = ?, "
			+ " usage = ? "
			+ "WHERE vacation_type_id = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, vacation.getVacationTypeName());

			// java.util.Date -> java.sql.Date 내부 변환
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
}