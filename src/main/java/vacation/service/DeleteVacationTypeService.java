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
			conn.setAutoCommit(false); // 트랜잭션 시작

			int deletedRows = vacationTypeDao.delete(conn, vacationTypeId);
			if (deletedRows == 0) {
				throw new RuntimeException("삭제할 휴가항목이 존재하지 않습니다.");
			}

			conn.commit(); // 트랜잭션 커밋
		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 예외 발생 시 롤백
			throw new RuntimeException("DB 삭제 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
		} finally {
			JdbcUtil.close(conn); // 커넥션 닫기
		}
	}
}