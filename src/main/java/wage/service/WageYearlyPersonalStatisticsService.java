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
import wage.model.WageMonthlyPersonalStatisticsRow;
import wage.model.WageYearlyPersonalStatisticsDetail;
import wage.model.WageYearlyPersonalStatisticsResult;

// 연도별 개인연봉 통계 조회 서비스
public class WageYearlyPersonalStatisticsService {

	private WageDao wageDao = new WageDao();

	public WageYearlyPersonalStatisticsResult getYearlyPersonalStatistics(
		Integer employeeId,
		String selectedYear) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"사원을 선택해야 합니다.");
		}

		if (selectedYear == null) {
			throw new IllegalArgumentException(
				"귀속년도를 선택해야 합니다.");
		}

		selectedYear = selectedYear.trim();

		if (selectedYear.isEmpty()) {
			throw new IllegalArgumentException(
				"귀속년도를 선택해야 합니다.");
		}

		int endYear;

		try {
			endYear = Year.parse(selectedYear).getValue();
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속년도는 YYYY 형식이어야 합니다.");
		}

		int startYear = endYear - 9;

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageMonthlyPersonalStatisticsRow> rawRows = wageDao.selectMonthlyPersonalStatisticsRows(
				conn,
				employeeId,
				String.valueOf(startYear),
				String.valueOf(endYear));

			Map<String, List<WageMonthlyPersonalStatisticsRow>> yearlyMap = new HashMap<>();

			for (WageMonthlyPersonalStatisticsRow rawRow : rawRows) {

				String year = rawRow.getWageMonth().substring(0, 4);

				List<WageMonthlyPersonalStatisticsRow> yearRows = yearlyMap.get(year);

				if (yearRows == null) {
					yearRows = new ArrayList<>();
					yearlyMap.put(year, yearRows);
				}

				yearRows.add(rawRow);
			}

			List<WageYearlyPersonalStatisticsDetail> rows = new ArrayList<>();

			Long previousAnnualSalary = null;
			boolean previousHasData = false;

			for (int year = startYear; year <= endYear; year++) {

				String yearText = String.valueOf(year);

				List<WageMonthlyPersonalStatisticsRow> yearRows = yearlyMap.get(yearText);

				boolean hasData = yearRows != null && !yearRows.isEmpty();

				long annualSalary = 0L;
				long totalDeduction = 0L;

				if (hasData) {

					for (WageMonthlyPersonalStatisticsRow row : yearRows) {

						annualSalary += safe(row.getTotalPayment());

						totalDeduction += safe(row.getTotalDeduction());
					}
				}

				Double salaryGrowthRate = null;

				// 현재 연도와 직전 연도 모두 실제 급여 데이터가 있을 때만 증감률 계산
				if (hasData && previousHasData) {

					salaryGrowthRate = calculateGrowthRate(
						previousAnnualSalary,
						annualSalary);
				}

				rows.add(
					new WageYearlyPersonalStatisticsDetail(
						yearText,
						annualSalary,
						salaryGrowthRate,
						totalDeduction,
						hasData));

				previousAnnualSalary = annualSalary;
				previousHasData = hasData;
			}

			return new WageYearlyPersonalStatisticsResult(rows);

		} catch (SQLException e) {
			throw new RuntimeException(
				"연도별 개인연봉 통계 조회 중 데이터베이스 오류가 발생했습니다.",
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
}