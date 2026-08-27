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
		"基本給", "国民年金", "健康保険", "介護保険", "雇用保険", "所得税", "住民税");
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
			// 1. 기존 데이터 조회 (원래 이름 확인용)[cite: 10]
			WageType origin = wageTypeDao.selectById(conn, wageType.getWageTypeId());
			if (origin != null) {
				// 원래 이름이 보호 대상 시스템 항목이고, 입력된 이름이 기존과 다를 경우 (이름을 변경하려고 할 때)
				if (PROTECTED_WAGE_NAMES.contains(origin.getWageTypeName())) {
					if (!origin.getWageTypeName().equals(wageType.getWageTypeName())) {
						throw new RuntimeException("システムの基本項目(" + origin.getWageTypeName() + ")의 이름은 수정할 수 없습니다.");
					}
				}
			}

			// 2. 수정 시 자기 자신을 제외하고 이름 중복 체크[cite: 10]
			boolean isDuplicate = wageTypeDao.isDuplicateNameForUpdate(conn, wageType.getWageTypeId(),
				wageType.getWageTypeName());
			if (isDuplicate) {
				throw new RuntimeException("既に存在する支払/控除項目の名前です。");
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

				if (("P".equals(itemType) || itemType == null) && "基本給".equals(name)) {
					System.out.println("サーバーのブロック：基本給は削除できません。");
					return 0;
				}

				if ("D".equals(itemType)) {
					List<String> fixedDeductions = Arrays.asList(
						"国民年金", "健康保険", "介護保険", "雇用保険",
						"所得税", "住民税", "事業所得", "日雇給与");
					if (fixedDeductions.contains(name)) {
						System.out.println("サーバーのブロック：必須の控除項目（" + name + "）は削除できません。");
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

		if (type != null && !type.isEmpty() && !type.equals("一括支給")) {
			content = type;
			type = "勤怠連動";
		}

		return new WageType(
			w.getWageTypeId(), w.getWageTypeName(), w.getNumberCut(),
			type, content, w.getUsage(), w.getItemType(),
			w.getTaxableYn(), w.getTaxFreeLimit(), w.getTaxFreeName());
	}

	public void updateWageType(WageType wageType) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			WageTypeDao dao = new WageTypeDao();

			// 수정 시 이름 중복 체크가 필요하다면 아래와 같이 검증 로직 추가 가능
			if (dao.isDuplicateNameForUpdate(conn, wageType.getWageTypeId(), wageType.getWageTypeName())) {
				throw new RuntimeException("이미 존재하는 지급/공제 항목 이름입니다.");
			}

			dao.update(conn, wageType); // WageTypeDao의 update 메서드 호출[cite: 6]
		} catch (SQLException e) {
			throw new RuntimeException("급여 항목 수정 실패: " + e.getMessage(), e);
		}
	}

}