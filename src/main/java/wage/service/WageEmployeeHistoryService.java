package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageEmployeeHistoryResult;
import wage.model.WageEmployeeHistoryRow;

// 사원별 급여내역 조회 서비스
public class WageEmployeeHistoryService {

	private WageDao wageDao = new WageDao();

	public WageEmployeeHistoryResult getWageEmployeeHistory(
		Integer employeeId,
		String startMonth,
		String endMonth) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"社員を選択する必要があります。");
		}

		if (startMonth == null || endMonth == null) {
			throw new IllegalArgumentException(
				"照会期間を入力する必要があります。");
		}

		startMonth = startMonth.trim();
		endMonth = endMonth.trim();

		if (startMonth.isEmpty() || endMonth.isEmpty()) {
			throw new IllegalArgumentException(
				"照会期間を入力する必要があります。");
		}

		YearMonth start;
		YearMonth end;

		try {
			start = YearMonth.parse(startMonth);
			end = YearMonth.parse(endMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"照会期間はYYYY-MM形式である必要があります。");
		}

		if (start.isAfter(end)) {
			throw new IllegalArgumentException(
				"開始月は終了月より後にすることはできません。");
		}

		if (start.plusMonths(11).isBefore(end)) {
			throw new IllegalArgumentException(
				"照会期間は最大12か月まで選択できます。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageEmployeeHistoryRow> rows = wageDao.selectWageEmployeeHistoryRows(
				conn,
				employeeId,
				startMonth,
				endMonth);

			long totalMonthlyRemuneration = 0L;
			long totalPayment = 0L;
			long totalDeduction = 0L;
			long totalNationalPension = 0L;
			long totalHealthInsurance = 0L;
			long totalLongTermCareInsurance = 0L;
			long totalEmploymentInsurance = 0L;
			long totalIncomeTax = 0L;
			long totalLocalIncomeTax = 0L;

			for (WageEmployeeHistoryRow row : rows) {

				if (row.getMonthlyRemuneration() != null) {
					totalMonthlyRemuneration += row.getMonthlyRemuneration();
				}

				if (row.getTotalPayment() != null) {
					totalPayment += row.getTotalPayment();
				}

				if (row.getTotalDeduction() != null) {
					totalDeduction += row.getTotalDeduction();
				}

				if (row.getNationalPension() != null) {
					totalNationalPension += row.getNationalPension();
				}

				if (row.getHealthInsurance() != null) {
					totalHealthInsurance += row.getHealthInsurance();
				}

				if (row.getLongTermCareInsurance() != null) {
					totalLongTermCareInsurance += row.getLongTermCareInsurance();
				}

				if (row.getEmploymentInsurance() != null) {
					totalEmploymentInsurance += row.getEmploymentInsurance();
				}

				if (row.getIncomeTax() != null) {
					totalIncomeTax += row.getIncomeTax();
				}

				if (row.getLocalIncomeTax() != null) {
					totalLocalIncomeTax += row.getLocalIncomeTax();
				}
			}

			return new WageEmployeeHistoryResult(
				rows,
				totalMonthlyRemuneration,
				totalPayment,
				totalDeduction,
				totalNationalPension,
				totalHealthInsurance,
				totalLongTermCareInsurance,
				totalEmploymentInsurance,
				totalIncomeTax,
				totalLocalIncomeTax);

		} catch (SQLException e) {
			throw new RuntimeException(
				"社員別給与履歴の照会中にデータベースエラーが発生しました。",
				e);
		}
	}
}