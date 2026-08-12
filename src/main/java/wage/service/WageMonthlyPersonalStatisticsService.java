package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageMonthlyPersonalStatisticsDetail;
import wage.model.WageMonthlyPersonalStatisticsResult;
import wage.model.WageMonthlyPersonalStatisticsRow;

// 월별 개인급여 통계 조회 서비스
public class WageMonthlyPersonalStatisticsService {

	private WageDao wageDao = new WageDao();

	public WageMonthlyPersonalStatisticsResult getMonthlyPersonalStatistics(
		Integer employeeId,
		String year) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"사원을 선택해야 합니다.");
		}

		if (year == null) {
			throw new IllegalArgumentException(
				"귀속년도를 선택해야 합니다.");
		}

		year = year.trim();

		if (year.isEmpty()) {
			throw new IllegalArgumentException(
				"귀속년도를 선택해야 합니다.");
		}

		try {
			Year.parse(year);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속년도는 YYYY 형식이어야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageMonthlyPersonalStatisticsRow> rawRows = wageDao.selectMonthlyPersonalStatisticsRows(
				conn,
				employeeId,
				year);

			Map<String, WageMonthlyPersonalStatisticsRow> rowMap = new HashMap<>();

			long totalPayment = 0L;
			long totalDeduction = 0L;

			for (WageMonthlyPersonalStatisticsRow rawRow : rawRows) {

				rowMap.put(
					rawRow.getWageMonth(),
					rawRow);

				totalPayment += safe(rawRow.getTotalPayment());
				totalDeduction += safe(rawRow.getTotalDeduction());
			}

			List<WageMonthlyPersonalStatisticsDetail> rows = new ArrayList<>();

			for (int month = 1; month <= 12; month++) {

				String wageMonth = String.format(
					"%s-%02d",
					year,
					month);

				WageMonthlyPersonalStatisticsRow rawRow = rowMap.get(wageMonth);

				long monthlyPayment = rawRow == null
					? 0L
					: safe(rawRow.getTotalPayment());

				long monthlyDeduction = rawRow == null
					? 0L
					: safe(rawRow.getTotalDeduction());

				rows.add(
					new WageMonthlyPersonalStatisticsDetail(
						wageMonth,
						monthlyPayment,
						monthlyDeduction));
			}

			return new WageMonthlyPersonalStatisticsResult(
				rows,
				totalPayment,
				totalDeduction);

		} catch (SQLException e) {
			throw new RuntimeException(
				"월별 개인급여 통계 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}