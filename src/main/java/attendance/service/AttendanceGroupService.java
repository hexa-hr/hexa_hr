package attendance.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import attendance.dao.AttendanceGroupDao;
import attendance.model.AttendanceGroup;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class AttendanceGroupService {

	private AttendanceGroupDao groupDao = new AttendanceGroupDao();

	// 그룹 목록 조회
	public List<AttendanceGroup> getGroupList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return groupDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException("근태그룹 목록 조회 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 그룹 신규 저장
	public void addGroup(String groupName) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			groupDao.insert(conn, groupName);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("근태그룹 저장 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void updateGroup(int id, String groupName) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			groupDao.update(conn, id, groupName);
		} catch (SQLException e) {
			throw new RuntimeException("그룹 수정 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void deleteGroup(int id) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			groupDao.delete(conn, id);
		} catch (SQLException e) {
			throw new RuntimeException("그룹 삭제 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public void resetGroups() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			groupDao.deleteAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException("그룹 초기화 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}