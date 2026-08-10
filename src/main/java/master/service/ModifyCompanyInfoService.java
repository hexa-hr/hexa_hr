package master.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import master.dao.CompanyInfoDao;
import master.model.CompanyInfo;

public class ModifyCompanyInfoService {

	private CompanyInfoDao companyDao = new CompanyInfoDao();

	public void modify(CompanyInfo info) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			// DAO 호출하여 DB 수정
			companyDao.update(conn, info);

			conn.commit(); // 성공 시 커밋
		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 에러 발생 시 롤백
			throw new RuntimeException("회사 정보 수정 실패", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}