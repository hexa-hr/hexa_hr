package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

	// 지급항목 목록 (item_type이 'WAGE'이거나 지정되지 않은 기본 데이터)[cite: 6]
	public List<WageType> getWageList() {
		List<WageType> allList = getWageTypeList();
		List<WageType> wageList = new ArrayList<>();
		for (WageType w : allList) {
			if (w.getItemType() == null || w.getItemType().equals("W")) {
				wageList.add(w);
			}
		}
		return wageList;
	}

	// 공제항목 목록 (item_type이 'DEDUCTION'인 데이터)[cite: 6]
	public List<WageType> getDeductionList() {
		List<WageType> allList = getWageTypeList();
		List<WageType> deductionList = new ArrayList<>();
		for (WageType w : allList) {
			if ("D".equals(w.getItemType())) {
				deductionList.add(w);
			}
		}
		return deductionList;
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