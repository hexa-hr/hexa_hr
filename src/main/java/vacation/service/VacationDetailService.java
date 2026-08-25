package vacation.service;

import java.sql.Connection;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationDao;
import vacation.model.VacationDetail;

public class VacationDetailService {
	private VacationDao vacationDao = new VacationDao();

	// 💡 1. 파라미터에 vacationTypeId를 추가합니다.
	public List<VacationDetail> getVacationDetail(int employeeId, String vacationTypeId) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			// 💡 2. DAO로 vacationTypeId를 함께 넘겨줍니다.
			return vacationDao.selectVacationDetail(conn, employeeId, vacationTypeId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}