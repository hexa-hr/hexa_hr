package dailywork.service;

import java.sql.Connection;
import java.sql.SQLException;

import dailywork.dao.DailyWorkDao;
import dailywork.model.DailyWorkVO;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class DailyWorkService {
	private DailyWorkDao dailyWorkDao = new DailyWorkDao();

	public boolean saveDailyWork(DailyWorkVO vo) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			int result = dailyWorkDao.insertDailyWork(conn, vo);

			if (result > 0) {
				conn.commit();
				return true;
			} else {
				JdbcUtil.rollback(conn);
				return false;
			}
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}