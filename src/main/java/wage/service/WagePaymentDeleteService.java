package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;

// 給与入力画面 - 選択社員の給与削除Service
public class WagePaymentDeleteService {

	private WageDao wageDao = new WageDao();

	public void delete(
		Integer employeeId,
		String wageMonth,
		String wagePeriod) {

		deleteEmployees(
			Collections.singletonList(
				employeeId),
			wageMonth,
			wagePeriod);
	}

	public void deleteEmployees(
		List<Integer> employeeIds,
		String wageMonth,
		String wagePeriod) {

		Set<Integer> normalizedEmployeeIds = normalizeEmployeeIds(
			employeeIds);

		String normalizedWageMonth = normalizeWageMonth(
			wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(
			wagePeriod);

		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				for (Integer employeeId : normalizedEmployeeIds) {

					int deletedCount = wageDao.deleteEmployeeWages(
						conn,
						employeeId,
						normalizedWageMonth,
						normalizedWagePeriod);

					if (deletedCount <= 0) {

						throw new IllegalStateException(
							"削除する給与情報がありません。");
					}
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
				"給与削除中にデータベースエラーが発生しました。",
				e);
		}
	}

	private Set<Integer> normalizeEmployeeIds(
		List<Integer> employeeIds) {

		if (employeeIds == null
			|| employeeIds.isEmpty()) {

			throw new IllegalArgumentException(
				"削除する社員がいません。");
		}

		Set<Integer> result = new LinkedHashSet<>();

		for (Integer employeeId : employeeIds) {

			validateEmployeeId(
				employeeId);

			result.add(
				employeeId);
		}

		return result;
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
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