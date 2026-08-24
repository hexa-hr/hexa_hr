package vacation.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationDao;
import vacation.model.VacationType; // vacation.model 패키지 임포트

public class VacationListService {

	private VacationDao vacationDao = new VacationDao();

	// 사용 중인 휴가 항목 조회
	public List<VacationType> getActiveVacationTypes() {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return vacationDao.selectActiveVacationTypes(conn);
		} catch (SQLException e) {
			throw new RuntimeException("활성화된 휴가 항목 조회 오류", e);
		}
	}

	// 휴가 현황 리스트 조회
	public List<VacationType> getVacationList(String vacationTypeId, String keyword) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return vacationDao.selectVacationList(conn, vacationTypeId, keyword);
		} catch (SQLException e) {
			throw new RuntimeException("휴가 현황 조회 오류", e);
		}
	}

}