package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public List<WageType> getWageList() {
		List<WageType> allList = getWageTypeList();
		List<WageType> wageList = new ArrayList<>();
		for (WageType w : allList) {
			if (w.getItemType() == null || w.getItemType().equals("P")) {
				wageList.add(w);
			}
		}
		return wageList;
	}

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
		WageType newWage = processAttendanceType(wageType);

		try (Connection conn = ConnectionProvider.getConnection()) {
			wageTypeDao.insert(conn, newWage);
		} catch (SQLException e) {
			throw new RuntimeException("급여 항목 등록 오류", e);
		}
	}

	public void modifyWageType(WageType wageType) {
		WageType newWage = processAttendanceType(wageType);

		try (Connection conn = ConnectionProvider.getConnection()) {
			wageTypeDao.update(conn, newWage);
		} catch (SQLException e) {
			throw new RuntimeException("급여 항목 수정 오류", e);
		}
	}

	public int deleteWageType(Integer wageTypeId) {
		List<WageType> allList = getWageTypeList();
		for (WageType w : allList) {
			if (wageTypeId.equals(w.getWageTypeId())) {
				String name = w.getWageTypeName();
				String itemType = w.getItemType();

				if (("P".equals(itemType) || itemType == null) && "기본급".equals(name)) {
					System.out.println("서버 차단: 기본급은 삭제할 수 없습니다.");
					return 0;
				}

				if ("D".equals(itemType)) {
					List<String> fixedDeductions = Arrays.asList(
						"국민연금", "건강보험", "장기요양보험", "고용보험",
						"소득세", "지방소득세", "사업소득", "일용급여");
					if (fixedDeductions.contains(name)) {
						System.out.println("서버 차단: 필수 공제항목(" + name + ")은 삭제할 수 없습니다.");
						return 0;
					}
				}
				break;
			}
		}

		try (Connection conn = ConnectionProvider.getConnection()) {
			WageTypeDao wageDao = new WageTypeDao();
			return wageDao.delete(conn, wageTypeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private WageType processAttendanceType(WageType w) {
		String type = w.getAttendanceOrLumpsum();
		String content = w.getAttendanceOrLumpsumContent();

		if (type != null && !type.isEmpty() && !type.equals("일괄지급")) {
			content = type;
			type = "근태연결";
		}

		return new WageType(
			w.getWageTypeId(), w.getWageTypeName(), w.getNumberCut(),
			type, content, w.getUsage(), w.getItemType(),
			w.getTaxableYn(), w.getTaxFreeLimit(), w.getTaxFreeName());
	}
}