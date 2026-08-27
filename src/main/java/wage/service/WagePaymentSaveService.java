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
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentItemInput;
import wage.model.WagePaymentPeriodDefault;

// 給与入力画面 - 選択社員の給与保存Service
public class WagePaymentSaveService {

	private WageDao wageDao = new WageDao();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	public void save(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		List<WagePaymentItemInput> currentItemInputs) {

		validateEmployeeId(
			employeeId);

		String normalizedWageMonth = normalizeWageMonth(
			wageMonth);

		String normalizedWagePeriod = normalizeWagePeriod(
			wagePeriod);

		if (currentItemInputs == null) {

			throw new IllegalArgumentException(
				"給与項目情報が正しくありません。");
		}

		/*
		 * 該当する帰属年月 / 給与回次の日付を
		 * ブラウザではなくサーバーを基準に再決定する。
		 *
		 * 既存の給与回次 → DB保存日付
		 * 新規給与回次 → 会社の給与支給情報の基本日付
		 */
		WageLedgerSummary periodSummary = wagePaymentInputService
			.getPeriodSummary(
				normalizedWageMonth,
				normalizedWagePeriod);

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

			WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService
				.getDefaultPeriod(
					normalizedWageMonth);

			settlementStartDate = defaultPeriod
				.getSettlementStartDate();

			settlementEndDate = defaultPeriod
				.getSettlementEndDate();

			wagePaymentDate = defaultPeriod
				.getWagePaymentDate();
		}

		/*
		 * サーバーを基準に
		 * この社員の画面に存在すべき
		 * 給与項目スナップショットを再照会する。
		 *
		 * 既存の保存済み社員 → 既存のwageスナップショット
		 * 新規社員 → 現在有効 / 適用可能な給与項目
		 */
		List<WagePaymentInputViewItem> baseItems = wagePaymentInputService
			.getViewItems(
				employeeId,
				normalizedWageMonth,
				normalizedWagePeriod,
				settlementStartDate,
				settlementEndDate);

		if (baseItems.isEmpty()) {

			throw new IllegalStateException(
				"保存する給与項目がありません。");
		}

		Map<Integer, WagePaymentInputViewItem> baseItemMap = new LinkedHashMap<>();

		for (WagePaymentInputViewItem baseItem : baseItems) {

			baseItemMap.put(
				baseItem.getWageTypeId(),
				baseItem);
		}

		Map<Integer, Long> currentValueMap = new LinkedHashMap<>();

		for (WagePaymentItemInput input : currentItemInputs) {

			if (input == null
				|| input.getWageTypeId() == null) {

				throw new IllegalArgumentException(
					"給与項目情報が正しくありません。");
			}

			Integer wageTypeId = input.getWageTypeId();

			if (!baseItemMap.containsKey(
				wageTypeId)) {

				throw new IllegalArgumentException(
					"画面に存在しない給与項目が含まれています。");
			}

			if (currentValueMap.containsKey(
				wageTypeId)) {

				throw new IllegalArgumentException(
					"重複した給与項目が含まれています。");
			}

			long wageValue = input.getWageValue() == null
				? 0L
				: input.getWageValue();

			if (wageValue < 0L) {

				throw new IllegalArgumentException(
					"給与金額は0ウォン以上である必要があります。");
			}

			currentValueMap.put(
				wageTypeId,
				wageValue);
		}

		/*
		 * サーバーで確認したスナップショットと
		 * ユーザーが送信した給与項目数が
		 * 正確に一致する必要がある。
		 */
		if (currentValueMap.size() != baseItems.size()) {

			throw new IllegalArgumentException(
				"給与項目の一部が欠落しています。");
		}

		/*
		 * すべての検証が完了した後にのみ
		 * DB変更を開始する。
		 */
		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				/*
				 * 選択社員の該当月 / 給与回次のみ削除
				 */
				wageDao.deleteEmployeeWages(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod);

				/*
				 * サーバー基準のスナップショット順に
				 * すべての項目を再保存する。
				 *
				 * 0ウォンの項目も除外しない。
				 */
				for (WagePaymentInputViewItem baseItem : baseItems) {

					Long wageValue = currentValueMap.get(
						baseItem
							.getWageTypeId());

					wageDao.insertEmployeeWage(
						conn,
						employeeId,
						normalizedWageMonth,
						normalizedWagePeriod,
						baseItem.getWageTypeId(),
						wageValue,
						settlementStartDate,
						settlementEndDate,
						wagePaymentDate);
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
				"給与保存中にデータベースエラーが発生しました。",
				e);
		}
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

		String normalized = wageMonth.trim();

		try {

			YearMonth.parse(
				normalized);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		return normalized;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"給与回次を入力する必要があります。");
		}

		int period;

		try {

			period = Integer.parseInt(
				wagePeriod.trim());

			if (period < 1
				|| period > 10) {

				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {

			throw new IllegalArgumentException(
				"給与回次は1以上10以下の数値である必要があります。");
		}

		return String.valueOf(
			period);
	}

	private Date toSqlDate(
		java.util.Date value) {

		if (value == null) {
			return null;
		}

		return new Date(
			value.getTime());
	}
}