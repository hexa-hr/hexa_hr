package attendance.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import attendance.dao.AttendanceDao;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import master.model.AttendanceType;

public class AttendanceService {

	private AttendanceDao attendanceDao = new AttendanceDao();

	// 1. 勤怠リスト全体照会メソッドの追加
	public List<AttendanceType> getAttendanceList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return attendanceDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 2. 勤怠追加メソッド
	public void addAttendance(AttendanceType att) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			attendanceDao.insert(conn, att);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 勤怠項目の編集
	public void modifyAttendance(AttendanceType att) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceDao.update(conn, att);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 勤怠項目の削除
	public void removeAttendance(int attendanceTypeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceDao.delete(conn, attendanceTypeId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("勤怠項目の削除エラー", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 勤怠記録の保存および編集 (エスターのコード)
	public boolean saveAttendance(attendance.model.AttendanceVO vo) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			int result = 0;

			// attendanceIdが存在する場合は編集、そうでない場合は新規登録
			if (vo.getAttendanceId() > 0) {
				result = attendanceDao.updateAttendance(conn, vo);
			} else {
				result = attendanceDao.insertAttendance(conn, vo);
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
			throw new RuntimeException("勤怠記録の保存中にエラーが発生", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

}