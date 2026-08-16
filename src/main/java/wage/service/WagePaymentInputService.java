package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import attendance.dao.AttendanceDao;
import employee.dao.EmployeeDao;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.dao.WageDao;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentInputViewItem;

public class WagePaymentInputService {

	private WageTypeDao wageTypeDao = new WageTypeDao();
	private AttendanceDao attendanceDao = new AttendanceDao();
	private EmployeeDao employeeDao = new EmployeeDao();
	private WageDao wageDao = new WageDao();

	public List<WagePaymentCalculationItem> getInitialItems(
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate) {

		validateEmployeeId(employeeId);
		validateSettlementPeriod(
			settlementStartDate,
			settlementEndDate);

		try (Connection conn = ConnectionProvider.getConnection()) {

			return buildItems(
				conn,
				employeeId,
				settlementStartDate,
				settlementEndDate,
				true);

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 초기값 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WagePaymentCalculationItem> getItems(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate) {

		validateEmployeeId(employeeId);

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WagePaymentCalculationItem> savedItems = wageDao.selectEmployeeWageItems(
				conn,
				employeeId,
				wageMonth.trim(),
				wagePeriod.trim());

			// 저장된 급여가 없는 경우 → 신규 급여
			if (savedItems.isEmpty()) {

				validateSettlementPeriod(
					settlementStartDate,
					settlementEndDate);

				return buildItems(
					conn,
					employeeId,
					settlementStartDate,
					settlementEndDate,
					true);
			}

			/*
			 * 기존 급여인 경우
			 * 현재 사용 가능한 급여항목은 모두 0원으로 구성한다.
			 * 근태연결·일괄지급은 다시 계산하지 않는다.
			 */
			List<WagePaymentCalculationItem> currentItems = buildItems(
				conn,
				employeeId,
				null,
				null,
				false);

			Map<Integer, WagePaymentCalculationItem> savedItemMap = new LinkedHashMap<>();

			for (WagePaymentCalculationItem savedItem : savedItems) {

				savedItemMap.put(
					savedItem.getWageTypeId(),
					savedItem);
			}

			List<WagePaymentCalculationItem> result = new ArrayList<>();

			// 현재 활성 급여항목에 저장값 덮어쓰기
			for (WagePaymentCalculationItem currentItem : currentItems) {

				WagePaymentCalculationItem savedItem = savedItemMap.remove(
					currentItem.getWageTypeId());

				if (savedItem != null) {
					result.add(savedItem);
				} else {
					result.add(currentItem);
				}
			}

			/*
			 * 현재 사용안함이 되었더라도
			 * 과거 급여에 실제 저장된 항목은 유지한다.
			 */
			result.addAll(savedItemMap.values());

			return result;

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 항목 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WagePaymentInputViewItem> getViewItems(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate) {

		/*
		 * 신규/기존 여부에 따라 실제 화면에 표시할
		 * 급여항목과 금액을 먼저 조회한다.
		 */
		List<WagePaymentCalculationItem> items = getItems(
			employeeId,
			wageMonth,
			wagePeriod,
			settlementStartDate,
			settlementEndDate);

		try (Connection conn = ConnectionProvider.getConnection()) {

			String employmentType = employeeDao.selectEmploymentType(
				conn,
				employeeId);

			if (employmentType == null) {
				throw new IllegalArgumentException(
					"존재하지 않는 사원입니다.");
			}

			String wageCategory = determineWageCategory(employmentType);

			/*
			 * 현재 사용 중인 급여항목을 ID 기준으로 구성한다.
			 * 이 Map에 없으면 현재 usage='N'인 항목이다.
			 */
			List<WageType> activeWageTypes = wageTypeDao.selectActiveWageTypes(conn);

			Map<Integer, WageType> activeWageTypeMap = new LinkedHashMap<>();

			for (WageType wageType : activeWageTypes) {

				activeWageTypeMap.put(
					wageType.getWageTypeId(),
					wageType);
			}

			List<WagePaymentInputViewItem> result = new ArrayList<>();

			for (WagePaymentCalculationItem item : items) {

				WageType activeWageType = activeWageTypeMap.get(
					item.getWageTypeId());

				boolean active = activeWageType != null;

				boolean calculable = active
					&& isAvailableWageType(
						wageCategory,
						activeWageType);

				result.add(
					new WagePaymentInputViewItem(
						item.getWageTypeId(),
						item.getWageTypeName(),
						item.getItemType(),
						item.getTaxableYn(),
						item.getWageValue(),
						active,
						calculable));
			}

			return result;

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 화면 항목 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public WageLedgerSummary getPeriodSummary(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWageLedgerSummary(
				conn,
				wageMonth.trim(),
				wagePeriod.trim());

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여차수 기본정보 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private List<WagePaymentCalculationItem> buildItems(
		Connection conn,
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate,
		boolean applyInitialValues)
		throws SQLException {

		String employmentType = employeeDao.selectEmploymentType(
			conn,
			employeeId);

		if (employmentType == null) {
			throw new IllegalArgumentException(
				"존재하지 않는 사원입니다.");
		}

		String wageCategory = determineWageCategory(employmentType);

		List<WageType> wageTypes = wageTypeDao.selectActiveWageTypes(conn);

		List<WagePaymentCalculationItem> result = new ArrayList<>();

		for (WageType wageType : wageTypes) {

			if (!isAvailableWageType(
				wageCategory,
				wageType)) {

				continue;
			}

			long wageValue = 0L;

			/*
			 * 신규 급여일 때만
			 * 일괄지급 / 근태연결 초기값 적용
			 */
			if (applyInitialValues
				&& "P".equals(
					wageType.getItemType())) {

				String linkType = wageType.getAttendanceOrLumpsum();

				String linkContent = wageType
					.getAttendanceOrLumpsumContent();

				if ("근태연결".equals(linkType)
					&& linkContent != null
					&& !linkContent.trim().isEmpty()) {

					wageValue = attendanceDao
						.selectLinkedAllowanceAmount(
							conn,
							employeeId,
							linkContent,
							settlementStartDate,
							settlementEndDate);

				} else if ("일괄지급".equals(linkType)
					&& linkContent != null
					&& !linkContent.trim().isEmpty()) {

					try {

						wageValue = Long.parseLong(
							linkContent.trim());

					} catch (NumberFormatException e) {

						throw new IllegalStateException(
							"일괄지급 금액이 올바르지 않습니다: "
								+ wageType.getWageTypeName(),
							e);
					}
				}
			}

			result.add(
				new WagePaymentCalculationItem(
					wageType.getWageTypeId(),
					wageType.getWageTypeName(),
					wageType.getItemType(),
					wageType.getTaxableYn(),
					wageValue));
		}

		return result;
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"사원 정보가 올바르지 않습니다.");
		}
	}

	private void validateSettlementPeriod(
		Date settlementStartDate,
		Date settlementEndDate) {

		if (settlementStartDate == null
			|| settlementEndDate == null) {

			throw new IllegalArgumentException(
				"정산기간이 올바르지 않습니다.");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalArgumentException(
				"정산 시작일은 종료일보다 늦을 수 없습니다.");
		}
	}

	private String determineWageCategory(
		String employmentType) {

		if ("임시직".equals(employmentType)) {
			return "BUSINESS";
		}

		if ("일용직".equals(employmentType)) {
			return "DAILY";
		}

		return "WORKER";
	}

	private boolean isAvailableWageType(
		String wageCategory,
		WageType wageType) {

		String itemType = wageType.getItemType();
		String wageTypeName = wageType.getWageTypeName();

		if ("WORKER".equals(wageCategory)) {

			if ("P".equals(itemType)
				&& ("사업소득".equals(wageTypeName)
					|| "일용급여".equals(wageTypeName))) {

				return false;
			}

			return true;
		}

		if ("BUSINESS".equals(wageCategory)) {

			if ("P".equals(itemType)) {
				return "사업소득".equals(wageTypeName);
			}

			if ("D".equals(itemType)) {
				return "소득세".equals(wageTypeName)
					|| "지방소득세".equals(wageTypeName);
			}

			return false;
		}

		/*
		 * 일용직은 DAILY_WORK 연동 규칙이 확정된 후
		 * 별도 처리한다.
		 */
		return false;
	}
}