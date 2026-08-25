package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;

// 給与台帳 - 帰属年月・給与回次の全給与削除Service
public class WageLedgerDeleteService {

	private WageDao wageDao = new WageDao();

	public void delete(
		String wageMonth,
		String wagePeriod) {

		String normalizedWageMonth = normalizeWageMonth(
			wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(
			wagePeriod);

		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				int deletedCount = wageDao.deleteWageLedgerRows(
					conn,
					normalizedWageMonth,
					normalizedWagePeriod);

				if (deletedCount <= 0) {

					throw new IllegalStateException(
						"削除する給与台帳がありません。");
				}

				conn.commit();

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
				"給与台帳の削除中にデータベースエラーが発生しました。",
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
}