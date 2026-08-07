package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageLedgerSummary;

// 급여대장 조회 서비스
public class WageLedgerService {

	private WageDao wageDao = new WageDao();

	public List<WageLedgerSummary> getWageLedgerSummaries(String year) {

		if (year == null || !year.matches("\\d{4}")) {
			throw new IllegalArgumentException(
				"귀속연도는 YYYY 형식이어야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWageLedgerSummaries(conn, year);

		} catch (SQLException e) {
			throw new RuntimeException(
				"급여대장 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}
}