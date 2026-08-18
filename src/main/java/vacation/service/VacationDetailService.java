package vacation.service;

import java.sql.Connection;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import vacation.dao.VacationDao;
import vacation.model.VacationDetail;

public class VacationDetailService {
	private VacationDao vacationDao = new VacationDao();

	public List<VacationDetail> getVacationDetail(int employeeId) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return vacationDao.selectVacationDetail(conn, employeeId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}