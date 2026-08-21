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

	// 2. 근태 추가 메서드 [유진 코드]
	public void addAttendance(AttendanceType att) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			// DAO 중복 체크 호출
			if (attendanceDao.isDuplicateName(conn, att.getAttendanceTypeName())) {
				throw new RuntimeException("이미 존재하는 근태 항목 이름입니다.");
			}
			attendanceDao.insert(conn, att);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	//근태항목수정 [유진 코드]
	public void modifyAttendance(AttendanceType att) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			// DAO 중복 체크 호출
			if (attendanceDao.isDuplicateNameForUpdate(conn, att.getAttendanceTypeId(), att.getAttendanceTypeName())) {
				throw new RuntimeException("이미 존재하는 근태 항목 이름입니다.");
			}
			attendanceDao.update(conn, att);
		} catch (SQLException e) {
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

	// 근태 기록 저장 및 수정 (에스더 코드)
	public boolean saveAttendance(attendance.model.AttendanceVO vo) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			int result = 0;

			// attendanceId가 존재하면 수정, 아니면 신규 등록
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
			throw new RuntimeException("근태 기록 저장 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

}