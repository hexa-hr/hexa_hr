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

	// 사용 여부와 관계없이 전체 급여항목 상세정보 조회
	public List<WageType> selectAllWageTypes(Connection conn)
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

				result.add(
					new WageType(
						rs.getInt("wage_type_id"),
						rs.getString("wage_type_name"),
						rs.getString("number_cut"),
						rs.getString("attendance_or_lumpsum"),
						rs.getString(
							"attendance_or_lumpsum_content"),
						rs.getString("usage"),
						rs.getString("item_type"),
						rs.getString("taxable_yn"),
						taxFreeLimit,
						rs.getString("tax_free_name")));
			}
		}

		return result;
	}

	// 급여입력용 - 귀속연월/급여차수에 저장된 급여항목 틀 조회
	public List<WageType> selectWorkspaceWageTypes(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		String sql = "SELECT wt.wage_type_id, "
			+ "       wt.wage_type_name, "
			+ "       wt.number_cut, "
			+ "       wt.attendance_or_lumpsum, "
			+ "       wt.attendance_or_lumpsum_content, "
			+ "       wt.usage, "
			+ "       wt.item_type, "
			+ "       wt.taxable_yn, "
			+ "       wt.tax_free_limit, "
			+ "       wt.tax_free_name "
			+ "FROM wage_type wt "
			+ "WHERE wt.item_type IN ('P', 'D') "
			+ "  AND EXISTS ( "
			+ "      SELECT 1 "
			+ "      FROM wage w "
			+ "      JOIN employee e "
			+ "        ON e.employee_id = w.employee_id "
			+ "      WHERE w.wage_type_id = wt.wage_type_id "
			+ "        AND w.wage_month = ? "
			+ "        AND w.wage_period = ? "
			+ "        AND e.employment_type IN ( "
			+ "            '정규직', '계약직', '파견직', "
			+ "            '위촉직', '임시직' "
			+ "        ) "
			+ "  ) "
			+ "ORDER BY CASE wt.item_type "
			+ "           WHEN 'P' THEN 1 "
			+ "           WHEN 'D' THEN 2 "
			+ "           ELSE 3 "
			+ "         END, "
			+ "         wt.wage_type_id";

		List<WageType> result = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, wageMonth);
			pstmt.setString(2, wagePeriod);

			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					Long taxFreeLimit = null;

					long taxFreeLimitValue = rs.getLong("tax_free_limit");

					if (!rs.wasNull()) {
						taxFreeLimit = taxFreeLimitValue;
					}

					result.add(
						new WageType(
							rs.getInt("wage_type_id"),
							rs.getString("wage_type_name"),
							rs.getString("number_cut"),
							rs.getString("attendance_or_lumpsum"),
							rs.getString(
								"attendance_or_lumpsum_content"),
							rs.getString("usage"),
							rs.getString("item_type"),
							rs.getString("taxable_yn"),
							taxFreeLimit,
							rs.getString("tax_free_name")));
				}
			}
		}

		return result;
	}

}