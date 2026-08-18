package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;

// 급여입력 화면 - 선택 사원 급여 삭제 Service
public class WagePaymentDeleteService {

	private WageDao wageDao = new WageDao();

	public void delete(
		Integer employeeId,
		String wageMonth,
		String wagePeriod) {

		validateEmployeeId(
			employeeId);

		String normalizedWageMonth = normalizeWageMonth(
			wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(
			wagePeriod);

		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				int deletedCount = wageDao.deleteEmployeeWages(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod);

				if (deletedCount <= 0) {

					throw new IllegalStateException(
						"삭제할 급여정보가 없습니다.");
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
				"급여 삭제 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"올바른 사원을 선택해야 합니다.");
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
}