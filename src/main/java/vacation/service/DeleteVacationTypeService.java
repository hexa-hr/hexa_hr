package vacation.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationTypeDao;

public class DeleteVacationTypeService {

	private VacationTypeDao vacationTypeDao = new VacationTypeDao();

	public void delete(int vacationTypeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // トランザクション開始

			VacationTypeDao dao = new VacationTypeDao();

			// [追加] 使用中かどうかの検査
			if (dao.isUsedInVacationDays(conn, vacationTypeId)) {
				throw new IllegalStateException("現在使用中の休暇項目のため削除できません。");
			}

			int deletedRows = dao.delete(conn, vacationTypeId);
			if (deletedRows == 0) {
				throw new RuntimeException("削除する休暇項目が存在しません。");
			}

			conn.commit(); // トランザクションコミット
		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 例外発生時のロールバック
			throw new RuntimeException("DB削除処理中にエラーが発生しました: " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			JdbcUtil.rollback(conn);
			throw e; // 使用中の例外はそのままスローしてハンドラーでキャッチできるようにする
		} finally {
			JdbcUtil.close(conn); // コネクションを閉じる
		}
	}
}