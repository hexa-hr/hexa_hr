package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageInsuranceDeductionDetail;
import wage.model.WageInsuranceDeductionResult;
import wage.model.WageInsuranceDeductionRow;
import wage.model.WageLedgerSummary;

// 4대보험 공제내역 조회 서비스
public class WageInsuranceDeductionService {

	private WageDao wageDao = new WageDao();

	public WageInsuranceDeductionResult getWageInsuranceDeduction(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null || wagePeriod == null) {
			throw new IllegalArgumentException(
				"귀속연월과 급여차수를 선택해야 합니다.");
		}

		wageMonth = wageMonth.trim();
		wagePeriod = wagePeriod.trim();

		if (wageMonth.isEmpty() || wagePeriod.isEmpty()) {
			throw new IllegalArgumentException(
				"귀속연월과 급여차수를 선택해야 합니다.");
		}

		try {
			YearMonth.parse(wageMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		int period;

		try {
			period = Integer.parseInt(wagePeriod);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				"급여차수가 올바르지 않습니다.");
		}

		if (period < 1 || period > 10) {
			throw new IllegalArgumentException(
				"급여차수는 1차부터 10차까지 선택할 수 있습니다.");
		}

		// "01"처럼 전달되어도 DB의 "1"과 일치하도록 정규화
		wagePeriod = String.valueOf(period);

		try (Connection conn = ConnectionProvider.getConnection()) {

			WageLedgerSummary summary = wageDao.selectWageLedgerSummary(
				conn,
				wageMonth,
				wagePeriod);

			List<WageInsuranceDeductionRow> rawRows = wageDao.selectWageInsuranceDeductionRows(
				conn,
				wageMonth,
				wagePeriod);

			List<WageInsuranceDeductionDetail> rows = new ArrayList<>();

			long totalNationalPensionEmployer = 0L;
			long totalNationalPensionEmployee = 0L;

			long totalHealthInsuranceEmployer = 0L;
			long totalHealthInsuranceEmployee = 0L;

			long totalLongTermCareInsuranceEmployer = 0L;
			long totalLongTermCareInsuranceEmployee = 0L;

			long totalEmploymentInsuranceEmployer = 0L;
			long totalEmploymentInsuranceEmployee = 0L;

			for (WageInsuranceDeductionRow rawRow : rawRows) {

				long nationalPensionEmployee = safe(rawRow.getNationalPension());

				long healthInsuranceEmployee = safe(rawRow.getHealthInsurance());

				long longTermCareInsuranceEmployee = safe(rawRow.getLongTermCareInsurance());

				long employmentInsuranceEmployee = safe(rawRow.getEmploymentInsurance());

				// 국민연금·건강보험·장기요양보험은
				// 프로젝트 기준으로 사업주 부담분을 근로자 부담분과 동일하게 계산
				long nationalPensionEmployer = nationalPensionEmployee;

				long healthInsuranceEmployer = healthInsuranceEmployee;

				long longTermCareInsuranceEmployer = longTermCareInsuranceEmployee;

				long employmentInsuranceEmployer = calculateEmploymentInsuranceEmployer(
					employmentInsuranceEmployee);

				WageInsuranceDeductionDetail detail = new WageInsuranceDeductionDetail(
					rawRow.getEmployeeId(),
					rawRow.getEmploymentType(),
					rawRow.getKoreanName(),
					rawRow.getHireDate(),
					rawRow.getDepartmentName(),
					rawRow.getPositionName(),
					nationalPensionEmployer,
					nationalPensionEmployee,
					healthInsuranceEmployer,
					healthInsuranceEmployee,
					longTermCareInsuranceEmployer,
					longTermCareInsuranceEmployee,
					employmentInsuranceEmployer,
					employmentInsuranceEmployee);

				rows.add(detail);

				totalNationalPensionEmployer += nationalPensionEmployer;
				totalNationalPensionEmployee += nationalPensionEmployee;

				totalHealthInsuranceEmployer += healthInsuranceEmployer;
				totalHealthInsuranceEmployee += healthInsuranceEmployee;

				totalLongTermCareInsuranceEmployer += longTermCareInsuranceEmployer;
				totalLongTermCareInsuranceEmployee += longTermCareInsuranceEmployee;

				totalEmploymentInsuranceEmployer += employmentInsuranceEmployer;
				totalEmploymentInsuranceEmployee += employmentInsuranceEmployee;
			}

			return new WageInsuranceDeductionResult(
				summary,
				rows,
				totalNationalPensionEmployer,
				totalNationalPensionEmployee,
				totalHealthInsuranceEmployer,
				totalHealthInsuranceEmployee,
				totalLongTermCareInsuranceEmployer,
				totalLongTermCareInsuranceEmployee,
				totalEmploymentInsuranceEmployer,
				totalEmploymentInsuranceEmployee);

		} catch (SQLException e) {
			throw new RuntimeException(
				"4대보험 공제내역 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private long calculateEmploymentInsuranceEmployer(
		long employeeEmploymentInsurance) {

		// 프로젝트 단순화 기준:
		// 고용보험 사업주 부담분은 근로자 부담분에 115 / 90 비율을 적용하고
		// 10원 미만을 절사한다.
		long employerEmploymentInsurance = employeeEmploymentInsurance * 115 / 90;

		return employerEmploymentInsurance / 10 * 10;
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}