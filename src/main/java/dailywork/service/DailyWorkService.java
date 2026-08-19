package dailywork.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

			int result = 0;
			// workId가 있으면 수정(Update), 없으면 등록(Insert)
			if (vo.getWorkId() != null && vo.getWorkId() > 0) {
				result = dailyWorkDao.updateDailyWork(conn, vo);
			} else {
				result = dailyWorkDao.insertDailyWork(conn, vo);
			}

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

	public List<Map<String, Object>> getDailyWorkList(int empId, String yearMonth) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return dailyWorkDao.selectDailyWorkList(conn, empId, yearMonth);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean deleteDailyWork(int workId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			int result = dailyWorkDao.deleteDailyWork(conn, workId);
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