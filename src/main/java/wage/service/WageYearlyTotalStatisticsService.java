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
import wage.model.WageMonthlyTotalStatisticsRow;
import wage.model.WageYearlyTotalStatisticsDetail;
import wage.model.WageYearlyTotalStatisticsResult;

// 연도별 전체급여 통계 조회 서비스
public class WageYearlyTotalStatisticsService {

	private WageDao wageDao = new WageDao();

	public WageYearlyTotalStatisticsResult getYearlyTotalStatistics(
		String selectedYear) {

		if (selectedYear == null) {
			throw new IllegalArgumentException(
				"帰属年度を選択する必要があります。");
		}

		selectedYear = selectedYear.trim();

		if (selectedYear.isEmpty()) {
			throw new IllegalArgumentException(
				"帰属年度を選択する必要があります。");
		}

		int endYear;

		try {
			endYear = Year.parse(selectedYear).getValue();
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"帰属年度はYYYY形式である必要があります。");
		}

		int startYear = endYear - 9;

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageMonthlyTotalStatisticsRow> rawRows = wageDao.selectMonthlyTotalStatisticsRows(
				conn,
				String.valueOf(startYear),
				String.valueOf(endYear));

			Map<String, List<WageMonthlyTotalStatisticsRow>> yearlyMap = new HashMap<>();

			for (WageMonthlyTotalStatisticsRow rawRow : rawRows) {

				String year = rawRow.getWageMonth().substring(0, 4);

				List<WageMonthlyTotalStatisticsRow> yearRows = yearlyMap.get(year);

				if (yearRows == null) {
					yearRows = new ArrayList<>();
					yearlyMap.put(year, yearRows);
				}

				yearRows.add(rawRow);
			}

			List<WageYearlyTotalStatisticsDetail> rows = new ArrayList<>();

			Long previousPayment = null;
			Double previousAverageEmployeeCount = null;
			boolean previousHasData = false;

			for (int year = startYear; year <= endYear; year++) {

				String yearText = String.valueOf(year);

				List<WageMonthlyTotalStatisticsRow> yearRows = yearlyMap.get(yearText);

				boolean hasData = yearRows != null && !yearRows.isEmpty();

				long totalPayment = 0L;
				int totalEmployeeCount = 0;
				int dataMonthCount = 0;

				if (hasData) {

					dataMonthCount = yearRows.size();

					for (WageMonthlyTotalStatisticsRow row : yearRows) {

						totalPayment += safe(row.getTotalPayment());

						totalEmployeeCount += safe(row.getEmployeeCount());
					}
				}

				double averageEmployeeCount = dataMonthCount == 0
					? 0.0
					: (double)totalEmployeeCount
						/ dataMonthCount;

				Double paymentGrowthRate = null;
				Double employeeGrowthRate = null;

				// 현재 연도와 직전 연도 모두 실제 급여 데이터가 있을 때만 증감률 계산
				if (hasData && previousHasData) {

					paymentGrowthRate = calculateGrowthRate(
						previousPayment,
						totalPayment);

					employeeGrowthRate = calculateGrowthRate(
						previousAverageEmployeeCount,
						averageEmployeeCount);
				}

				rows.add(
					new WageYearlyTotalStatisticsDetail(
						yearText,
						totalPayment,
						paymentGrowthRate,
						averageEmployeeCount,
						employeeGrowthRate));

				previousPayment = totalPayment;
				previousAverageEmployeeCount = averageEmployeeCount;
				previousHasData = hasData;
			}

			return new WageYearlyTotalStatisticsResult(rows);

		} catch (SQLException e) {
			throw new RuntimeException(
				"年度別全体給与統計の照会中にデータベースエラーが発生しました。",
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

	private Double calculateGrowthRate(
		double previousValue,
		double currentValue) {

		if (previousValue == 0.0) {
			return null;
		}

		return (currentValue - previousValue)
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