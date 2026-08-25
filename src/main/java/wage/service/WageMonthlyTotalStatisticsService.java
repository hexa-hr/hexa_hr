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
import wage.model.WageMonthlyTotalStatisticsDetail;
import wage.model.WageMonthlyTotalStatisticsResult;
import wage.model.WageMonthlyTotalStatisticsRow;

// 월별 전체급여 통계 조회 서비스
public class WageMonthlyTotalStatisticsService {

	private WageDao wageDao = new WageDao();

	public WageMonthlyTotalStatisticsResult getMonthlyTotalStatistics(
		String year) {

		if (year == null) {
			throw new IllegalArgumentException(
				"帰属年度を選択する必要があります。");
		}

		year = year.trim();

		if (year.isEmpty()) {
			throw new IllegalArgumentException(
				"帰属年度を選択する必要があります。");
		}

		try {
			Year.parse(year);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"帰属年度はYYYY形式である必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageMonthlyTotalStatisticsRow> rawRows = wageDao.selectMonthlyTotalStatisticsRows(
				conn,
				year);

			Map<String, WageMonthlyTotalStatisticsRow> rowMap = new HashMap<>();

			long totalPayment = 0L;
			int totalEmployeeCount = 0;

			for (WageMonthlyTotalStatisticsRow rawRow : rawRows) {

				rowMap.put(
					rawRow.getWageMonth(),
					rawRow);

				totalPayment += safe(rawRow.getTotalPayment());
				totalEmployeeCount += safe(rawRow.getEmployeeCount());
			}

			int dataMonthCount = rawRows.size();

			double averageEmployeeCount = dataMonthCount == 0
				? 0.0
				: (double)totalEmployeeCount / dataMonthCount;

			List<WageMonthlyTotalStatisticsDetail> rows = new ArrayList<>();

			for (int month = 1; month <= 12; month++) {

				String wageMonth = String.format(
					"%s-%02d",
					year,
					month);

				WageMonthlyTotalStatisticsRow current = rowMap.get(wageMonth);

				WageMonthlyTotalStatisticsRow previous = null;

				if (month > 1) {

					String previousMonth = String.format(
						"%s-%02d",
						year,
						month - 1);

					previous = rowMap.get(previousMonth);
				}

				long currentPayment = current == null
					? 0L
					: safe(current.getTotalPayment());

				int currentEmployeeCount = current == null
					? 0
					: safe(current.getEmployeeCount());

				Double paymentGrowthRate = null;
				Double employeeGrowthRate = null;

				// 현재 월과 직전 월 모두 실제 급여 데이터가 있을 때만 증감률 계산
				if (current != null && previous != null) {

					long previousPayment = safe(previous.getTotalPayment());

					int previousEmployeeCount = safe(previous.getEmployeeCount());

					paymentGrowthRate = calculateGrowthRate(
						previousPayment,
						currentPayment);

					employeeGrowthRate = calculateGrowthRate(
						previousEmployeeCount,
						currentEmployeeCount);
				}

				rows.add(
					new WageMonthlyTotalStatisticsDetail(
						wageMonth,
						currentPayment,
						paymentGrowthRate,
						currentEmployeeCount,
						employeeGrowthRate));
			}

			return new WageMonthlyTotalStatisticsResult(
				rows,
				totalPayment,
				averageEmployeeCount);

		} catch (SQLException e) {
			throw new RuntimeException(
				"月別全体給与統計の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	private Double calculateGrowthRate(
		long previousValue,
		long currentValue) {

		if (previousValue == 0L) {
			return null;
		}

		return ((double)currentValue - previousValue)
			/ previousValue
			* 100.0;
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}

	private int safe(Integer value) {
		return value == null ? 0 : value;
	}
}