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
			throw new RuntimeException("給与項目一覧の照会エラー", e);
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
				throw new RuntimeException("既に存在する支払/控除項目の名前です。");
			}
			// 2. 신규 등록
			wageTypeDao.insert(conn, wageType);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void modifyWageType(WageType wageType) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			// 1. 기존 데이터 조회 (원래 이름 확인용)
			WageType origin = wageTypeDao.selectById(conn, wageType.getWageTypeId());
			if (origin != null) {
				// 원래 이름이 보호 대상 시스템 항목이고, 입력된 이름이 기존과 다를 경우 (이름을 변경하려고 할 때)
				if (PROTECTED_WAGE_NAMES.contains(origin.getWageTypeName())) {
					if (!origin.getWageTypeName().equals(wageType.getWageTypeName())) {
						throw new RuntimeException("システムの基本項目(" + origin.getWageTypeName() + ")の名前은 변경할 수 없습니다.");
					}
				}
			}

			// 2. 수정 시 자기 자신을 제외하고 이름 중복 체크
			boolean isDuplicate = wageTypeDao.isDuplicateNameForUpdate(conn, wageType.getWageTypeId(),
				wageType.getWageTypeName());
			if (isDuplicate) {
				throw new RuntimeException("既に存在する支払/控除項目の名前です。");
			}

			// 3. 수정 실행
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
					throw new RuntimeException("基本給は削除できません。");
				}

				if ("D".equals(itemType)) {
					List<String> fixedDeductions = Arrays.asList(
						"국민연금", "건강보험", "장기요양보험", "고용보험",
						"소득세", "지방소득세", "사업소득", "일용급여");
					if (fixedDeductions.contains(name)) {
						throw new RuntimeException("必須の控除項目（" + name + "）は削除できません。");
					}
				}
				break;
			}
		}

		try (Connection conn = ConnectionProvider.getConnection()) {
			WageTypeDao wageDao = new WageTypeDao();
			return wageDao.delete(conn, wageTypeId);
		} catch (SQLException e) {
			// 💡 ORA-02292 (자식 레코드 존재 에러) 발생 시 500 에러 대신 사용자 친화적 메시지 전달
			if (e.getErrorCode() == 2292) {
				throw new RuntimeException("既に使用中の項目であるため削除できません。(関連データが存在します)");
			}
			throw new RuntimeException("給与項目削除失敗: " + e.getMessage(), e);
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