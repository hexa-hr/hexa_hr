package vacation.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationTypeDao;
import vacation.model.VacationType;

public class UpdateVacationTypeService {

	private VacationTypeDao vacationTypeDao = new VacationTypeDao();

	public void update(VacationType vacation) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			int updatedRows = vacationTypeDao.update(conn, vacation);
			if (updatedRows == 0) {
				throw new RuntimeException("수정할 휴가항목이 존재하지 않습니다.");
			}

			conn.commit(); // 트랜잭션 커밋
		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 예외 발생 시 롤백
			throw new RuntimeException("DB 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn); // 커넥션 닫기
		}
	}
}