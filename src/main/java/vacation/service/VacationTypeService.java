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

	// 목록 가져오기
	public List<VacationType> getVacationList() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return vacationDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException("DB 조회 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 휴가 항목 추가하기
	public void addVacationType(VacationType vacation) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			vacationDao.insert(conn, vacation);

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("DB 저장 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 휴가 항목 수정하기 (추가된 메서드)
	public void updateVacationType(VacationType vacation) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			int updatedRows = vacationDao.update(conn, vacation);
			if (updatedRows == 0) {
				throw new RuntimeException("수정할 휴가 항목이 존재하지 않습니다.");
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("DB 수정 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}