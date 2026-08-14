package master.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import master.dao.CompanyInfoDao;
import master.dao.ContactPersonInfoDao;
import master.model.CompanyInfo;
import master.model.ContactPersonInfo;

public class ModifyCompanyInfoService {

	private CompanyInfoDao compDao = new CompanyInfoDao();
	private ContactPersonInfoDao contDao = new ContactPersonInfoDao();

	public void modify(CompanyInfo compInfo, ContactPersonInfo contInfo) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			// UPDATE 실행
			int compResult = compDao.update(conn, compInfo);
			int contResult = contDao.update(conn, contInfo);

			// 만약 DB가 텅 비어있어서 UPDATE할 대상이 없다면 INSERT를 해주는 센스!
			if (compResult == 0)
				compDao.insert(conn, compInfo);
			if (contResult == 0)
				contDao.insert(conn, contInfo);

			conn.commit(); // 에러 없이 통과하면 최종 반영

		} catch (SQLException e) {
			JdbcUtil.rollback(conn); // 에러 나면 롤백
			throw new RuntimeException("회사 정보 업데이트 실패", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}