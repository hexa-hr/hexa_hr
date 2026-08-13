package dailywork.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dailywork.dao.FieldOrProjectDao;
import dailywork.model.FieldOrProjectVO;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class FieldOrProjectService {
	private FieldOrProjectDao projectDao = new FieldOrProjectDao();

	// 1. 프로젝트 리스트 가져오기
	public List<FieldOrProjectVO> getVisibleProjectList() {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return projectDao.selectVisibleProjects(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// 2. 현장 추가
	public boolean addProject(String projectName) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			int result = projectDao.insertProject(conn, projectName);
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

	// 3. 현장 삭제 처리 (Soft Delete)
	public boolean removeProject(int projectId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			int result = projectDao.softDeleteProject(conn, projectId);
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

	// 4. 현장 초기화 (모두 숨김 처리)
	public boolean resetProjects() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			projectDao.resetProjects(conn); // 영향받은 행이 0개일 수도 있으므로 무조건 commit
			conn.commit();
			return true;
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}