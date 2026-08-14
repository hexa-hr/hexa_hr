package master.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import master.dao.CompanyInfoDao;
import master.dao.ContactPersonInfoDao;
import master.model.CompanyInfo;
import master.model.ContactPersonInfo;

public class SaveCompanyInfoService {

	// DB에 쿼리를 날릴 DAO 객체 생성
	private CompanyInfoDao companyDao = new CompanyInfoDao();
	private ContactPersonInfoDao contactDao = new ContactPersonInfoDao();

	// Handler에서 호출할 save 메서드 만들기
	public void save(CompanyInfo companyInfo, ContactPersonInfo contactInfo) {
		Connection conn = null;
		try {
			// 1. DB 연결 가져오기
			conn = ConnectionProvider.getConnection();

			// 2. 자동 커밋 끄기 (트랜잭션 시작)
			// 회사 정보와 담당자 정보 둘 다 에러 없이 저장될 때만 최종 승인하기 위함
			conn.setAutoCommit(false);

			// 3. DAO를 통해 데이터 INSERT 실행
			companyDao.insert(conn, companyInfo);
			contactDao.insert(conn, contactInfo);

			// 4. 모든 저장이 에러 없이 끝났다면 완전 저장(Commit)
			conn.commit();

		} catch (SQLException e) {
			// 저장 도중 하나라도 에러가 나면 전부 취소(Rollback)
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		} finally {
			// 5. DB 연결 닫기
			JdbcUtil.close(conn);
		}
	}
}