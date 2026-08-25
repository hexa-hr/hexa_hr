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
				"給与入力初期値の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public List<WagePaymentInputViewItem> getFrameViewItems(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		String normalizedWageMonth;

		try {

			normalizedWageMonth = YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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
				"給与回次は1以上10以下の数値である必要があります。");
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
				 * 保存済みワークスペースがない場合のみ
				 * 現在の新規給与入力項目基準を適用する。
				 *
				 * 保存済みワークスペースがある場合は当時実際に保存された
				 * 給与項目の枠をそのまま表示する。
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
				"給与入力基本項目の照会中にデータベースエラーが発生しました。",
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
				"帰属年月を入力する必要があります。");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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
				"給与回次は1以上10以下の数値である必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WagePaymentCalculationItem> savedItems = wageDao.selectEmployeeWageItems(
				conn,
				employeeId,
				wageMonth.trim(),
				wagePeriod.trim());

			// 保存済み給与がない場合 → 新規給与
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
			 * 既存給与の場合
			 *
			 * 保存時に実際に存在した給与項目のみを返す。
			 * 現在有効な給与項目は追加しない。
			 *
			 * wage行の集合自体が
			 * 該当社員の当時の給与項目スナップショットである。
			 */
			return savedItems;

		} catch (SQLException e) {

			throw new RuntimeException(
				"給与入力項目の照会中にデータベースエラーが発生しました。",
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
		 * 新規/既存の区分に応じて、実際に画面に表示する
		 * 給与項目と金額を先に照会する。
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
					"存在しない社員です。");
			}

			String wageCategory = determineWageCategory(employmentType);

			/*
			 * 現在使用中の給与項目をID基準で構成する。
			 * このMapにない場合は、現在usage='N'の項目である。
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
				"給与入力画面項目の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public WageLedgerSummary getPeriodSummary(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWageLedgerSummary(
				conn,
				wageMonth.trim(),
				wagePeriod.trim());

		} catch (SQLException e) {

			throw new RuntimeException(
				"給与回次基本情報の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public List<WagePaymentEmployeeRow> getSavedEmployees(
		String wageMonth,
		String wagePeriod) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
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
				"給与回次は1以上10以下の数値である必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageDao.selectWagePaymentEmployeeRows(
				conn,
				wageMonth.trim(),
				String.valueOf(wagePeriodNumber));

		} catch (SQLException e) {

			throw new RuntimeException(
				"給与入力社員一覧の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public WagePaymentPeriodDefault getDefaultPeriod(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		YearMonth baseMonth;

		try {

			baseMonth = YearMonth.parse(
				wageMonth.trim());

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			EmployeeSalaryAccount account = employeeSalaryAccountDao.selectByCompanyId(
				conn,
				COMPANY_ID);

			if (account == null) {
				throw new IllegalArgumentException(
					"給与算定期間と支給日が設定されていません。ユーザー情報で給与支給情報を先に設定してください。");
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
					"給与支給情報の精算期間設定が正しくありません。");
			}

			return new WagePaymentPeriodDefault(
				Date.valueOf(settlementStartDate),
				Date.valueOf(settlementEndDate),
				Date.valueOf(wagePaymentDate));

		} catch (SQLException e) {

			throw new RuntimeException(
				"給与基本日付の照会中にデータベースエラーが発生しました。",
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
				"存在しない社員です。");
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
			 * 新規給与の場合のみ
			 * 一括支給 / 勤怠連動の初期値を適用
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
							"一括支給金額が正しくありません: "
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
		 * 社員が選択されていないため、基本給と勤怠連動は0ウォンである。
		 * 一括支給項目のみ給与項目設定の金額を表示する。
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
				"一括支給金額が正しくありません: "
					+ wageType.getWageTypeName(),
				e);
		}
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"社員情報が正しくありません。");
		}
	}

	private void validateSettlementPeriod(
		Date settlementStartDate,
		Date settlementEndDate) {

		if (settlementStartDate == null
			|| settlementEndDate == null) {

			throw new IllegalArgumentException(
				"精算期間が正しくありません。");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalArgumentException(
				"精算開始日は終了日より後にすることはできません。");
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
				"精算月区分が設定されていません。");
		}

		YearMonth targetMonth;

		if ("C".equals(monthType)) {

			targetMonth = baseMonth;

		} else if ("P".equals(monthType)) {

			targetMonth = baseMonth.minusMonths(1);

		} else {

			throw new IllegalStateException(
				"精算月区分が正しくありません: "
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
				"支給月区分が設定されていません。");
		}

		YearMonth targetMonth;

		if ("C".equals(monthType)) {

			targetMonth = baseMonth;

		} else if ("N".equals(monthType)) {

			targetMonth = baseMonth.plusMonths(1);

		} else {

			throw new IllegalStateException(
				"支給月区分が正しくありません: "
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
				"給与日付が設定されていません。");
		}

		/*
		 * DB設定値0は該当月の末日を意味する。
		 */
		if (day == 0) {

			return targetMonth.atEndOfMonth();
		}

		if (day < 1
			|| day > targetMonth.lengthOfMonth()) {

			throw new IllegalStateException(
				"給与日付設定が正しくありません: "
					+ day);
		}

		return targetMonth.atDay(day);
	}
}