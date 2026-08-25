package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentItemInput;
import wage.model.WagePaymentPeriodDefault;
import wage.model.WageTypeSystemIds;

// 給与入力・管理（日雇い）- 選択した社員の給与保存Service
public class DailyWagePaymentSaveService {

	private WageDao wageDao = new WageDao();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private DailyWagePaymentInputService dailyWagePaymentInputService = new DailyWagePaymentInputService();

	public void save(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		List<WagePaymentItemInput> currentDeductionInputs) {

		validateEmployeeId(employeeId);

		String normalizedWageMonth = normalizeWageMonth(wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(wagePeriod);

		if (currentDeductionInputs == null) {

			throw new IllegalArgumentException(
				"控除項目情報が正しくありません。");
		}

		/*
		 * 既存の月・回次の場合は保存済みの日付を、
		 * 新規の月・回次の場合は会社の給与設定の基本日付を使用する。
		 */
		PeriodDates periodDates = resolvePeriodDates(
			normalizedWageMonth,
			normalizedWagePeriod);

		/*
		 * 保存直前にサーバーで保存基準値を再構成する。
		 *
		 * - 日雇い社員であることと控除項目スナップショットを検証する。
		 * - 支給総額、所得税、住民税はDAILY_WORKを基準とする。
		 * - 4大保険とその他の控除項目は画面に表示された値を維持する。
		 */
		WagePaymentAutoCalculationResult canonicalResult = dailyWagePaymentInputService.prepareSaveResult(
			employeeId,
			normalizedWageMonth,
			normalizedWagePeriod,
			periodDates.settlementStartDate,
			periodDates.settlementEndDate,
			currentDeductionInputs);

		if (canonicalResult == null) {

			throw new IllegalStateException(
				"日雇い給与の計算結果がありません。");
		}

		long totalPayment = requireNonNegative(
			canonicalResult.getTotalPayment(),
			"支給総額");

		List<WagePaymentInputViewItem> deductionItems = canonicalResult.getWageItems();

		if (deductionItems == null) {

			throw new IllegalStateException(
				"日雇い給与の控除項目計算結果がありません。");
		}

		Map<Integer, Long> deductionValueMap = new LinkedHashMap<>();

		long totalDeduction = 0L;

		for (WagePaymentInputViewItem deductionItem : deductionItems) {

			if (deductionItem == null
				|| deductionItem.getWageTypeId() == null
				|| deductionItem.getWageTypeId() <= 0
				|| !"D".equals(
					deductionItem.getItemType())) {

				throw new IllegalStateException(
					"日雇い給与の控除項目計算結果が正しくありません。");
			}

			Integer wageTypeId = deductionItem.getWageTypeId();

			if (Integer.valueOf(
				WageTypeSystemIds.BASIC_PAY_ID)
				.equals(wageTypeId)) {

				throw new IllegalStateException(
					"支給項目が控除項目に含まれています。");
			}

			long wageValue = requireNonNegative(
				deductionItem.getWageValue(),
				"控除金額");

			if (deductionValueMap.put(
				wageTypeId,
				wageValue) != null) {

				throw new IllegalStateException(
					"重複した控除項目の計算結果が存在します。");
			}

			totalDeduction += wageValue;
		}

		long netPayment = totalPayment - totalDeduction;

		if (canonicalResult.getTotalDeduction() == null
			|| canonicalResult.getTotalDeduction().longValue() != totalDeduction
			|| canonicalResult.getNetPayment() == null
			|| canonicalResult.getNetPayment().longValue() != netPayment) {

			throw new IllegalStateException(
				"日雇い給与の計算結果の合計が一致しません。");
		}

		/*
		 * WAGEの変更は1つのトランザクションで処理する。
		 * DAILY_WORKは変更しない。
		 */
		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				wageDao.deleteEmployeeWages(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod);

				/*
				 * 日雇いの支給総額は基本給のシステム項目
				 * wage_type_id=1に保存する。
				 */
				wageDao.insertEmployeeWage(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod,
					WageTypeSystemIds.BASIC_PAY_ID,
					totalPayment,
					periodDates.settlementStartDate,
					periodDates.settlementEndDate,
					periodDates.wagePaymentDate);

				/*
				 * すべての控除項目を0ウォンの項目も含めて保存する。
				 */
				for (Map.Entry<Integer, Long> entry : deductionValueMap.entrySet()) {

					wageDao.insertEmployeeWage(
						conn,
						employeeId,
						normalizedWageMonth,
						normalizedWagePeriod,
						entry.getKey(),
						entry.getValue(),
						periodDates.settlementStartDate,
						periodDates.settlementEndDate,
						periodDates.wagePaymentDate);
				}

				conn.commit();

			} catch (SQLException
				| RuntimeException e) {

				try {

					conn.rollback();

				} catch (SQLException rollbackException) {

					e.addSuppressed(
						rollbackException);
				}

				throw e;
			}

		} catch (SQLException e) {

			throw new RuntimeException(
				"日雇い給与の保存中にデータベースエラーが発生しました。",
				e);
		}
	}

	private PeriodDates resolvePeriodDates(
		String wageMonth,
		String wagePeriod) {

		WageLedgerSummary periodSummary = wagePaymentInputService.getPeriodSummary(
			wageMonth,
			wagePeriod);

		Date settlementStartDate;
		Date settlementEndDate;
		Date wagePaymentDate;

		if (periodSummary != null) {

			settlementStartDate = toSqlDate(
				periodSummary
					.getSettlementPeriodStartDate());

			settlementEndDate = toSqlDate(
				periodSummary
					.getSettlementPeriodEndDate());

			wagePaymentDate = toSqlDate(
				periodSummary
					.getWagePaymentDate());

		} else {

			WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
				wageMonth);

			settlementStartDate = defaultPeriod.getSettlementStartDate();

			settlementEndDate = defaultPeriod.getSettlementEndDate();

			wagePaymentDate = defaultPeriod.getWagePaymentDate();
		}

		if (settlementStartDate == null
			|| settlementEndDate == null
			|| wagePaymentDate == null) {

			throw new IllegalStateException(
				"給与回次の日付情報が正しくありません。");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalStateException(
				"精算開始日は終了日より後にすることはできません。");
		}

		return new PeriodDates(
			settlementStartDate,
			settlementEndDate,
			wagePaymentDate);
	}

	private void validateEmployeeId(
		Integer employeeId) {

		if (employeeId == null
			|| employeeId <= 0) {

			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		try {

			return YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		try {

			int wagePeriodNumber = Integer.parseInt(
				wagePeriod.trim());

			if (wagePeriodNumber < 1
				|| wagePeriodNumber > 10) {

				throw new NumberFormatException();
			}

			return String.valueOf(
				wagePeriodNumber);

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"給与回次は1以上10以下の数値である必要があります。");
		}
	}

	private long requireNonNegative(
		Long value,
		String fieldName) {

		long normalizedValue = value == null
			? 0L
			: value;

		if (normalizedValue < 0L) {

			throw new IllegalArgumentException(
				fieldName
					+ "は0ウォン以上である必要があります。");
		}

		return normalizedValue;
	}

	private Date toSqlDate(
		java.util.Date value) {

		return value == null
			? null
			: new Date(value.getTime());
	}

	private static class PeriodDates {

		private Date settlementStartDate;
		private Date settlementEndDate;
		private Date wagePaymentDate;

		private PeriodDates(
			Date settlementStartDate,
			Date settlementEndDate,
			Date wagePaymentDate) {

			this.settlementStartDate = settlementStartDate;

			this.settlementEndDate = settlementEndDate;

			this.wagePaymentDate = wagePaymentDate;
		}
	}
}