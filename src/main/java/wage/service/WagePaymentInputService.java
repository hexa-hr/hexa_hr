package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import attendance.dao.AttendanceDao;
import employee.dao.EmployeeDao;
import employee.dao.EmployeeSalaryAccountDao;
import employee.model.EmployeeSalaryAccount;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.dao.WageDao;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentPeriodDefault;
import wage.model.WageTypeSystemIds;

public class WagePaymentInputService {

	private WageTypeDao wageTypeDao = new WageTypeDao();
	private AttendanceDao attendanceDao = new AttendanceDao();
	private EmployeeDao employeeDao = new EmployeeDao();
	private WageDao wageDao = new WageDao();
	private EmployeeSalaryAccountDao employeeSalaryAccountDao = new EmployeeSalaryAccountDao();
	private static final int COMPANY_ID = 1;

	public List<WagePaymentCalculationItem> getInitialItems(
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate) {

		validateEmployeeId(employeeId);
		validateSettlementPeriod(
			settlementStartDate,
			settlementEndDate);

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageType> activeWageTypes = wageTypeDao.selectActiveWageTypes(conn);

			return buildItems(
				conn,
				employeeId,
				activeWageTypes,
				settlementStartDate,
				settlementEndDate,
				true);

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 초기값 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WagePaymentInputViewItem> getFrameViewItems(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		String normalizedWageMonth;

		try {

			normalizedWageMonth = YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
		}

		int wagePeriodNumber;

		try {

			wagePeriodNumber = Integer.parseInt(
				wagePeriod.trim());

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
		}

		String normalizedWagePeriod = String.valueOf(wagePeriodNumber);

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageType> wageTypes = resolveFrameWageTypes(
				conn,
				normalizedWageMonth,
				normalizedWagePeriod);

			List<WagePaymentInputViewItem> result = new ArrayList<>();

			for (WageType wageType : wageTypes) {

				/*
				 * 저장된 작업공간이 없을 때만
				 * 현재 신규 급여입력 항목 기준을 적용한다.
				 *
				 * 저장된 작업공간이 있으면 당시 실제 저장된
				 * 급여항목 틀을 그대로 표시한다.
				 */
				if (!isDisplayableWageType(
					"WORKER",
					wageType)) {

					continue;
				}

				boolean active = "Y".equals(wageType.getUsage());

				result.add(
					new WagePaymentInputViewItem(
						wageType.getWageTypeId(),
						wageType.getWageTypeName(),
						wageType.getItemType(),
						wageType.getTaxableYn(),
						resolveFrameInitialValue(wageType),
						active,
						false));
			}

			return result;

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 기본 항목 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private List<WageType> resolveFrameWageTypes(
		Connection conn,
		String wageMonth,
		String wagePeriod)
		throws SQLException {

		List<WageType> workspaceWageTypes = wageTypeDao.selectWorkspaceWageTypes(
			conn,
			wageMonth,
			wagePeriod);

		if (!workspaceWageTypes.isEmpty()) {
			return workspaceWageTypes;
		}

		return wageTypeDao.selectActiveWageTypes(conn);
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

		int wagePeriodNumber;

		try {

			wagePeriodNumber = Integer.parseInt(
				wagePeriod.trim());

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
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

				String normalizedWagePeriod = String.valueOf(wagePeriodNumber);

				List<WageType> frameWageTypes = resolveFrameWageTypes(
					conn,
					wageMonth.trim(),
					normalizedWagePeriod);

				List<WagePaymentCalculationItem> initialItems = buildItems(
					conn,
					employeeId,
					frameWageTypes,
					settlementStartDate,
					settlementEndDate,
					true);

				Long employeeBasicPay = employeeDao.selectBasicPay(
					conn,
					employeeId);

				return applyEmployeeBasicPay(
					initialItems,
					employeeBasicPay);
			}

			/*
			 * 기존 급여인 경우
			 *
			 * 저장 당시 실제 존재했던 급여항목만 반환한다.
			 * 현재 활성 급여항목을 추가하지 않는다.
			 *
			 * wage 행의 집합 자체가
			 * 해당 사원의 당시 급여항목 스냅샷이다.
			 */
			return savedItems;

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

				boolean calculable = isCalculableWageType(
					wageCategory,
					item.getItemType());

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

	public List<WagePaymentEmployeeRow> getSavedEmployees(
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

		int wagePeriodNumber;

		try {

			wagePeriodNumber = Integer.parseInt(
				wagePeriod.trim());

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWagePaymentEmployeeRows(
				conn,
				wageMonth.trim(),
				String.valueOf(wagePeriodNumber));

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 사원 목록 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public WagePaymentPeriodDefault getDefaultPeriod(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		YearMonth baseMonth;

		try {

			baseMonth = YearMonth.parse(
				wageMonth.trim());

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			EmployeeSalaryAccount account = employeeSalaryAccountDao.selectByCompanyId(
				conn,
				COMPANY_ID);

			if (account == null) {

				throw new IllegalStateException(
					"급여지급정보가 등록되어 있지 않습니다.");
			}

			LocalDate settlementStartDate = resolveCalculationDate(
				baseMonth,
				account.getCalc1MonthType(),
				account.getSalaryCalculation1());

			LocalDate settlementEndDate = resolveCalculationDate(
				baseMonth,
				account.getCalc2MonthType(),
				account.getSalaryCalculation2());

			LocalDate wagePaymentDate = resolvePaymentDate(
				baseMonth,
				account.getPaymentMonthType(),
				account.getSalaryPaymentDate());

			if (settlementStartDate.isAfter(
				settlementEndDate)) {

				throw new IllegalStateException(
					"급여지급정보의 정산기간 설정이 올바르지 않습니다.");
			}

			return new WagePaymentPeriodDefault(
				Date.valueOf(settlementStartDate),
				Date.valueOf(settlementEndDate),
				Date.valueOf(wagePaymentDate));

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여 기본 날짜 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private List<WagePaymentCalculationItem> buildItems(
		Connection conn,
		Integer employeeId,
		List<WageType> wageTypes,
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

		List<WagePaymentCalculationItem> result = new ArrayList<>();

		for (WageType wageType : wageTypes) {

			if (!isDisplayableWageType(
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

	private List<WagePaymentCalculationItem> applyEmployeeBasicPay(
		List<WagePaymentCalculationItem> items,
		Long employeeBasicPay) {

		long basicPay = employeeBasicPay == null
			? 0L
			: employeeBasicPay;

		for (int i = 0; i < items.size(); i++) {

			WagePaymentCalculationItem item = items.get(i);

			if (Integer.valueOf(
				WageTypeSystemIds.BASIC_PAY_ID).equals(
					item.getWageTypeId())) {

				items.set(
					i,
					new WagePaymentCalculationItem(
						item.getWageTypeId(),
						item.getWageTypeName(),
						item.getItemType(),
						item.getTaxableYn(),
						basicPay));

				break;
			}
		}

		return items;
	}

	private long resolveFrameInitialValue(
		WageType wageType) {

		/*
		 * 사원이 선택되지 않았으므로 기본급과 근태연결은 0원이다.
		 * 일괄지급 항목만 급여항목 설정의 금액을 표시한다.
		 */
		if (!"P".equals(wageType.getItemType())
			|| !"일괄지급".equals(
				wageType.getAttendanceOrLumpsum())) {

			return 0L;
		}

		String linkContent = wageType.getAttendanceOrLumpsumContent();

		if (linkContent == null
			|| linkContent.trim().isEmpty()) {

			return 0L;
		}

		try {

			return Long.parseLong(
				linkContent.trim());

		} catch (NumberFormatException e) {

			throw new IllegalStateException(
				"일괄지급 금액이 올바르지 않습니다: "
					+ wageType.getWageTypeName(),
				e);
		}
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

	private boolean isDisplayableWageType(
		String wageCategory,
		WageType wageType) {

		if (!"WORKER".equals(wageCategory)
			&& !"BUSINESS".equals(wageCategory)) {

			return false;
		}

		String itemType = wageType.getItemType();

		return "P".equals(itemType)
			|| "D".equals(itemType);
	}

	private boolean isCalculableWageType(
		String wageCategory,
		String itemType) {

		if (!"WORKER".equals(wageCategory)
			&& !"BUSINESS".equals(wageCategory)) {

			return false;
		}

		return "P".equals(itemType)
			|| "D".equals(itemType);
	}

	private LocalDate resolveCalculationDate(
		YearMonth baseMonth,
		String monthType,
		Integer day) {

		if (monthType == null) {

			throw new IllegalStateException(
				"정산월 구분이 설정되어 있지 않습니다.");
		}

		YearMonth targetMonth;

		if ("C".equals(monthType)) {

			targetMonth = baseMonth;

		} else if ("P".equals(monthType)) {

			targetMonth = baseMonth.minusMonths(1);

		} else {

			throw new IllegalStateException(
				"올바르지 않은 정산월 구분입니다: "
					+ monthType);
		}

		return resolveDay(
			targetMonth,
			day);
	}

	private LocalDate resolvePaymentDate(
		YearMonth baseMonth,
		String monthType,
		Integer day) {

		if (monthType == null) {

			throw new IllegalStateException(
				"지급월 구분이 설정되어 있지 않습니다.");
		}

		YearMonth targetMonth;

		if ("C".equals(monthType)) {

			targetMonth = baseMonth;

		} else if ("N".equals(monthType)) {

			targetMonth = baseMonth.plusMonths(1);

		} else {

			throw new IllegalStateException(
				"올바르지 않은 지급월 구분입니다: "
					+ monthType);
		}

		return resolveDay(
			targetMonth,
			day);
	}

	private LocalDate resolveDay(
		YearMonth targetMonth,
		Integer day) {

		if (day == null) {

			throw new IllegalStateException(
				"급여 날짜가 설정되어 있지 않습니다.");
		}

		/*
		 * DB 설정값 0은 해당 월의 말일을 의미한다.
		 */
		if (day == 0) {

			return targetMonth.atEndOfMonth();
		}

		if (day < 1
			|| day > targetMonth.lengthOfMonth()) {

			throw new IllegalStateException(
				"급여 날짜 설정이 올바르지 않습니다: "
					+ day);
		}

		return targetMonth.atDay(day);
	}
}