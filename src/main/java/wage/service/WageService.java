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
	private static final List<String> PROTECTED_WAGE_NAMES = Arrays.asList(
		"기본급", "국민연금", "건강보험", "장기요양보험", "고용보험", "소득세", "지방소득세");
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
		try (Connection conn = ConnectionProvider.getConnection()) {
			// 1. 이름 중복 체크
			boolean isDuplicate = wageTypeDao.isDuplicateName(conn, wageType.getWageTypeName());
			if (isDuplicate) {
				throw new RuntimeException("이미 존재하는 지급/공제 항목 이름입니다.");
			}
			// 2. 신규 등록
			wageTypeDao.insert(conn, wageType);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void modifyWageType(WageType wageType) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			// 1. 기존 데이터 조회 (원래 이름 확인용)[cite: 10]
			WageType origin = wageTypeDao.selectById(conn, wageType.getWageTypeId());
			if (origin != null) {
				// 원래 이름이 보호 대상 시스템 항목이고, 입력된 이름이 기존과 다를 경우 (이름을 변경하려고 할 때)
				if (PROTECTED_WAGE_NAMES.contains(origin.getWageTypeName())) {
					if (!origin.getWageTypeName().equals(wageType.getWageTypeName())) {
						throw new RuntimeException("시스템 기본 항목(" + origin.getWageTypeName() + ")의 이름은 수정할 수 없습니다.");
					}
				}
			}

			// 2. 수정 시 자기 자신을 제외하고 이름 중복 체크[cite: 10]
			boolean isDuplicate = wageTypeDao.isDuplicateNameForUpdate(conn, wageType.getWageTypeId(),
				wageType.getWageTypeName());
			if (isDuplicate) {
				throw new RuntimeException("이미 존재하는 지급/공제 항목 이름입니다.");
			}

			// 3. 수정 실행[cite: 10]
			wageTypeDao.update(conn, wageType);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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