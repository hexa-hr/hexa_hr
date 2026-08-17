package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentPeriodDefault;
import wage.model.WagePaymentPreviousCopyResult;
import wage.model.WagePaymentPreviousSourceOption;

// 지난급여 불러오기 Service
public class WagePaymentPreviousCopyService {

	private WageDao wageDao = new WageDao();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	public List<WagePaymentPreviousSourceOption> getSourceOptions(
		String targetWageMonth,
		String targetWagePeriod) {

		String normalizedTargetWageMonth = normalizeWageMonth(
			targetWageMonth);

		String normalizedTargetWagePeriod = normalizeWagePeriod(
			targetWagePeriod);

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWagePaymentPreviousSourceOptions(
				conn,
				normalizedTargetWageMonth,
				normalizedTargetWagePeriod);

		} catch (SQLException e) {

			throw new RuntimeException(
				"지난 급여 목록 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public WagePaymentPreviousCopyResult copy(
		String sourceWageMonth,
		String sourceWagePeriod,
		String targetWageMonth,
		String targetWagePeriod,
		boolean replaceConfirmed) {

		String normalizedSourceWageMonth = normalizeWageMonth(
			sourceWageMonth);

		String normalizedSourceWagePeriod = normalizeWagePeriod(
			sourceWagePeriod);

		String normalizedTargetWageMonth = normalizeWageMonth(
			targetWageMonth);

		String normalizedTargetWagePeriod = normalizeWagePeriod(
			targetWagePeriod);

		if (normalizedSourceWageMonth.equals(
			normalizedTargetWageMonth)
			&& normalizedSourceWagePeriod.equals(
				normalizedTargetWagePeriod)) {

			throw new IllegalArgumentException(
				"원본 급여와 대상 급여가 같을 수 없습니다.");
		}

		if (!replaceConfirmed) {

			throw new IllegalArgumentException(
				"지난 급여 불러오기를 확인해야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				List<WagePaymentEmployeeRow> sourceEmployees = wageDao.selectWagePaymentEmployeeRows(
					conn,
					normalizedSourceWageMonth,
					normalizedSourceWagePeriod);

				int workerEmployeeCount = 0;
				int businessEmployeeCount = 0;

				for (WagePaymentEmployeeRow employee : sourceEmployees) {

					if (isWorkerEmploymentType(
						employee.getEmploymentType())) {

						workerEmployeeCount++;

					} else if ("임시직".equals(
						employee.getEmploymentType())) {

						businessEmployeeCount++;
					}
				}

				if (workerEmployeeCount == 0
					&& businessEmployeeCount == 0) {

					throw new IllegalStateException(
						"불러올 지난 급여가 없습니다.");
				}

				WageLedgerSummary targetSummary = wageDao.selectWageLedgerSummary(
					conn,
					normalizedTargetWageMonth,
					normalizedTargetWagePeriod);

				Date settlementStartDate;
				Date settlementEndDate;
				Date wagePaymentDate;

				if (targetSummary != null) {

					settlementStartDate = toSqlDate(
						targetSummary
							.getSettlementPeriodStartDate());

					settlementEndDate = toSqlDate(
						targetSummary
							.getSettlementPeriodEndDate());

					wagePaymentDate = toSqlDate(
						targetSummary
							.getWagePaymentDate());

				} else {

					WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
						normalizedTargetWageMonth);

					settlementStartDate = defaultPeriod.getSettlementStartDate();

					settlementEndDate = defaultPeriod.getSettlementEndDate();

					wagePaymentDate = defaultPeriod.getWagePaymentDate();
				}

				int deletedItemCount = wageDao.deleteWagePaymentWorkspaceRows(
					conn,
					normalizedTargetWageMonth,
					normalizedTargetWagePeriod);

				int copiedItemCount = wageDao.insertWagePaymentWorkspaceFromSource(
					conn,
					normalizedSourceWageMonth,
					normalizedSourceWagePeriod,
					normalizedTargetWageMonth,
					normalizedTargetWagePeriod,
					settlementStartDate,
					settlementEndDate,
					wagePaymentDate);

				if (copiedItemCount <= 0) {

					throw new IllegalStateException(
						"불러올 지난 급여항목이 없습니다.");
				}

				conn.commit();

				return new WagePaymentPreviousCopyResult(
					workerEmployeeCount,
					businessEmployeeCount,
					copiedItemCount,
					deletedItemCount);

			} catch (SQLException
				| RuntimeException e) {

				try {

					conn.rollback();

				} catch (SQLException rollbackException) {

					e.addSuppressed(
						rollbackException);
				}

				throw e;
			}

		} catch (SQLException e) {

			throw new RuntimeException(
				"지난 급여 불러오기 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		String normalized = wageMonth.trim();

		try {

			YearMonth.parse(
				normalized);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		return normalized;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
		}

		int period;

		try {

			period = Integer.parseInt(
				wagePeriod.trim());

			if (period < 1
				|| period > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
		}

		return String.valueOf(
			period);
	}

	private boolean isWorkerEmploymentType(
		String employmentType) {

		return "정규직".equals(employmentType)
			|| "계약직".equals(employmentType)
			|| "파견직".equals(employmentType)
			|| "위촉직".equals(employmentType);
	}

	private Date toSqlDate(
		java.util.Date value) {

		if (value == null) {
			return null;
		}

		return new Date(
			value.getTime());
	}
}