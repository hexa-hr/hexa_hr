package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import employee.dao.EmployeeRetirementDao;
import employee.model.EmployeeRetirementListDto;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeRetirementService {
	private EmployeeRetirementDao dao = new EmployeeRetirementDao();

	// 🌟 1. 총 데이터 개수 가져오기
	public int getRetirementCount(String statusFilter) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return dao.selectCount(conn, statusFilter);
		} catch (SQLException e) {
			throw new RuntimeException("개수 조회 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 🌟 2. 20개씩 잘라서 리스트 가져오기
	public List<EmployeeRetirementListDto> getRetirementList(String statusFilter, int page, int size) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			int startRow = (page - 1) * size + 1; // 시작 번호 (예: 1, 21, 41...)
			int endRow = page * size; // 끝 번호 (예: 20, 40, 60...)
			return dao.selectRetirementList(conn, statusFilter, startRow, endRow);
		} catch (SQLException e) {
			throw new RuntimeException("퇴직처리 목록 조회 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 3. 퇴직 처리 (변경 없음)
	public void executeRetirement(int empId, String type, java.util.Date date, String reason, String contact) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			dao.processRetirement(conn, empId, type, new java.sql.Date(date.getTime()), reason, contact);
			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("퇴직 처리 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 4. 퇴직 취소 (변경 없음)
	public void cancelRetirement(int empId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			dao.cancelRetirement(conn, empId);
			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("퇴직 취소 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}