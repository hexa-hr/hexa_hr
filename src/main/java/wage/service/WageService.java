package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jdbc.connection.ConnectionProvider;
import master.model.WageType;
import wage.dao.WageTypeDao;

public class WageService {

	private WageTypeDao wageTypeDao = new WageTypeDao();

	public List<WageType> getWageTypeList() {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return wageTypeDao.selectAll(conn);
		} catch (SQLException e) {
			throw new RuntimeException("급여항목 목록 조회 오류", e);
		}
	}

	public void addWageType(WageType wageType) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			wageTypeDao.insert(conn, wageType);
		} catch (SQLException e) {
			throw new RuntimeException("급여 항목 등록 오류", e);
		}
	}

	public void modifyWageType(WageType wageType) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			wageTypeDao.update(conn, wageType);
		} catch (SQLException e) {
			throw new RuntimeException("급여 항목 수정 오류", e);
		}
	}

	public int deleteWageType(Integer wageTypeId) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			WageTypeDao wageDao = new WageTypeDao();
			return wageDao.delete(conn, wageTypeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}