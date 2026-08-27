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

// 過去給与の読み込みService
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
				"過去給与一覧の照会中にデータベースエラーが発生しました。",
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
				"コピー元の給与と対象の給与を同一にすることはできません。");
		}

		if (!replaceConfirmed) {

			throw new IllegalArgumentException(
				"過去給与の読み込みを確認する必要があります。");
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
						"読み込む過去給与がありません。");
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
						"読み込む過去給与項目がありません。");
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
				"過去給与の読み込み中にデータベースエラーが発生しました。",
				e);
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		String normalized = wageMonth.trim();

		try {

			YearMonth.parse(
				normalized);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		return normalized;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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
				"給与回次は1以上10以下の数値である必要があります。");
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