package master.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import master.model.WageType;
import master.model.WageTypeOption;

//급여항목 기준정보 조회. 여러 급여 기능에서 공통으로 참조
public class WageTypeDao {

	public List<WageTypeOption> selectWageTypeOptions(Connection conn)
		throws SQLException {

		String sql = "SELECT wage_type_id, "
			+ "       wage_type_name, "
			+ "       item_type "
			+ "FROM wage_type "
			+ "ORDER BY CASE item_type "
			+ "           WHEN 'P' THEN 1 "
			+ "           WHEN 'D' THEN 2 "
			+ "           ELSE 3 "
			+ "         END, "
			+ "         wage_type_id";

		List<WageTypeOption> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				WageTypeOption option = new WageTypeOption(
					rs.getInt("wage_type_id"),
					rs.getString("wage_type_name"),
					rs.getString("item_type"));

				result.add(option);
			}
		}

		return result;
	}

	// 현재 사용 중인 급여항목 기준정보 조회
	public List<WageTypeOption> selectActiveWageTypeOptions(
		Connection conn)
		throws SQLException {

		String sql = "SELECT wage_type_id, "
			+ "       wage_type_name, "
			+ "       item_type "
			+ "FROM wage_type "
			+ "WHERE usage = 'Y' "
			+ "ORDER BY CASE item_type "
			+ "           WHEN 'P' THEN 1 "
			+ "           WHEN 'D' THEN 2 "
			+ "           ELSE 3 "
			+ "         END, "
			+ "         wage_type_id";

		List<WageTypeOption> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				WageTypeOption option = new WageTypeOption(
					rs.getInt("wage_type_id"),
					rs.getString("wage_type_name"),
					rs.getString("item_type"));

				result.add(option);
			}
		}

		return result;
	}

	// 현재 사용 중인 급여항목 상세정보 조회
	public List<WageType> selectActiveWageTypes(Connection conn)
		throws SQLException {

		String sql = "SELECT wage_type_id, "
			+ "       wage_type_name, "
			+ "       number_cut, "
			+ "       attendance_or_lumpsum, "
			+ "       attendance_or_lumpsum_content, "
			+ "       usage, "
			+ "       item_type, "
			+ "       taxable_yn, "
			+ "       tax_free_limit, "
			+ "       tax_free_name "
			+ "FROM wage_type "
			+ "WHERE usage = 'Y' "
			+ "ORDER BY CASE item_type "
			+ "           WHEN 'P' THEN 1 "
			+ "           WHEN 'D' THEN 2 "
			+ "           ELSE 3 "
			+ "         END, "
			+ "         wage_type_id";

		List<WageType> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				Long taxFreeLimit = null;

				long taxFreeLimitValue = rs.getLong("tax_free_limit");

				if (!rs.wasNull()) {
					taxFreeLimit = taxFreeLimitValue;
				}

				WageType wageType = new WageType(
					rs.getInt("wage_type_id"),
					rs.getString("wage_type_name"),
					rs.getString("number_cut"),
					rs.getString("attendance_or_lumpsum"),
					rs.getString("attendance_or_lumpsum_content"),
					rs.getString("usage"),
					rs.getString("item_type"),
					rs.getString("taxable_yn"),
					taxFreeLimit,
					rs.getString("tax_free_name"));

				result.add(wageType);
			}
		}

		return result;
	}

}
