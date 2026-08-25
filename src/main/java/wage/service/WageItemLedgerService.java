package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageTypeOption;
import wage.dao.WageDao;
import wage.model.WageItemLedgerEmployeeRow;
import wage.model.WageItemLedgerResult;
import wage.model.WageItemLedgerRow;

// 項目別台帳照会Service
public class WageItemLedgerService {

	private WageDao wageDao = new WageDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();

	public WageItemLedgerResult getItemLedger(Integer wageTypeId,
		String startMonth, String endMonth) {

		if (wageTypeId == null || wageTypeId <= 0) {
			throw new IllegalArgumentException("給与項目を選択する必要があります。");
		}

		if (startMonth == null || endMonth == null) {
			throw new IllegalArgumentException("照会期間を入力する必要があります。");
		}

		YearMonth start;
		YearMonth end;

		try {
			start = YearMonth.parse(startMonth);
			end = YearMonth.parse(endMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"照会期間はYYYY-MM形式である必要があります。");
		}

		if (start.isAfter(end)) {
			throw new IllegalArgumentException(
				"開始月は終了月より後にすることはできません。");
		}

		if (start.plusMonths(11).isBefore(end)) {
			throw new IllegalArgumentException(
				"照会期間は最大12か月まで選択できます。");
		}

		List<String> months = createMonths(start, end);

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageItemLedgerRow> rawRows = wageDao.selectItemLedger(
				conn, wageTypeId, startMonth, endMonth);

			// 社員の照会順序を維持するためLinkedHashMapを使用
			Map<Integer, WageItemLedgerEmployeeRow> employeeMap = new LinkedHashMap<>();

			// 画面の月順序を維持するためLinkedHashMapを使用
			Map<String, Long> monthlyTotals = new LinkedHashMap<>();

			for (String month : months) {
				monthlyTotals.put(month, 0L);
			}

			long grandTotal = 0L;

			for (WageItemLedgerRow row : rawRows) {

				// DAOの照会結果が指定した照会期間外でないことを確認
				if (!monthlyTotals.containsKey(row.getWageMonth())) {
					throw new IllegalStateException(
						"照会期間外の給与データが含まれています。");
				}

				WageItemLedgerEmployeeRow employeeRow = employeeMap.get(row.getEmployeeId());

				if (employeeRow == null) {
					employeeRow = new WageItemLedgerEmployeeRow(
						row.getEmployeeId(),
						row.getEmploymentType(),
						row.getKoreanName(),
						row.getDepartmentName(),
						row.getPositionName(),
						months);

					employeeMap.put(
						row.getEmployeeId(), employeeRow);
				}

				long wageValue = row.getWageValue() == null
					? 0L
					: row.getWageValue();

				employeeRow.addWageValue(
					row.getWageMonth(), wageValue);

				Long monthlyTotal = monthlyTotals.get(row.getWageMonth());

				if (monthlyTotal == null) {
					monthlyTotal = 0L;
				}

				monthlyTotals.put(
					row.getWageMonth(),
					monthlyTotal + wageValue);

				grandTotal += wageValue;
			}

			List<WageItemLedgerEmployeeRow> employeeRows = new ArrayList<>(employeeMap.values());

			return new WageItemLedgerResult(
				months,
				employeeRows,
				monthlyTotals,
				grandTotal);

		} catch (SQLException e) {
			throw new RuntimeException(
				"項目別台帳の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	public List<WageTypeOption> getWageTypeOptions() {

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageTypeDao.selectWageTypeOptions(conn);

		} catch (SQLException e) {
			throw new RuntimeException(
				"給与項目一覧の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	// 開始月から終了月までの月一覧を生成
	private List<String> createMonths(YearMonth start, YearMonth end) {

		List<String> months = new ArrayList<>();
		YearMonth current = start;

		while (!current.isAfter(end)) {
			months.add(current.toString());
			current = current.plusMonths(1);
		}

		return months;
	}

}
