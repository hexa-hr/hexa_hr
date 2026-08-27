package vacation.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationTypeDao;
import vacation.model.VacationType;

public class VacationTypeService {

	private VacationTypeDao vacationDao = new VacationTypeDao();

	// 一覧取得
	public List<VacationType> getVacationList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return vacationDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException("DB照会エラー発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 休暇項目の追加
	public void addVacationType(VacationType vacation) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // トランザクション開始

			vacationDao.insert(conn, vacation);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("DB保存エラー発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 休暇項目の修正 (追加されたメソッド)
	public void updateVacationType(VacationType vacation) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // トランザクション開始

			int updatedRows = vacationDao.update(conn, vacation);
			if (updatedRows == 0) {
				throw new RuntimeException("修正すべき休暇項目は存在しません。");
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("DB修正エラー発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}