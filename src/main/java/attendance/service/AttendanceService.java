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

	// 1. 근태 목록 전체 조회 메서드 추가
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

	// 2. 근태 추가 메서드
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

	// 근태항목 수정
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

	// 근태항목 삭제
	public void removeAttendance(int attendanceTypeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			attendanceDao.delete(conn, attendanceTypeId);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("근태항목 삭제 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

}