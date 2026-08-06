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

//항목별 대장 조회 서비스
public class WageItemLedgerService {

	private WageDao wageDao = new WageDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();

	public WageItemLedgerResult getItemLedger(Integer wageTypeId,
		String startMonth, String endMonth) {

		if (wageTypeId == null || wageTypeId <= 0) {
			throw new IllegalArgumentException("급여항목을 선택해야 합니다.");
		}

		if (startMonth == null || endMonth == null) {
			throw new IllegalArgumentException("조회 기간을 입력해야 합니다.");
		}

		YearMonth start;
		YearMonth end;

		try {
			start = YearMonth.parse(startMonth);
			end = YearMonth.parse(endMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"조회 기간은 YYYY-MM 형식이어야 합니다.");
		}

		if (start.isAfter(end)) {
			throw new IllegalArgumentException(
				"시작 월은 종료 월보다 늦을 수 없습니다.");
		}

		if (start.plusMonths(11).isBefore(end)) {
			throw new IllegalArgumentException(
				"조회 기간은 최대 12개월까지 선택할 수 있습니다.");
		}

		List<String> months = createMonths(start, end);

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageItemLedgerRow> rawRows = wageDao.selectItemLedger(
				conn, wageTypeId, startMonth, endMonth);

			// 사원의 조회 순서를 유지하기 위해 LinkedHashMap 사용
			Map<Integer, WageItemLedgerEmployeeRow> employeeMap = new LinkedHashMap<>();

			// 화면의 월 순서를 유지하기 위해 LinkedHashMap 사용
			Map<String, Long> monthlyTotals = new LinkedHashMap<>();

			for (String month : months) {
				monthlyTotals.put(month, 0L);
			}

			long grandTotal = 0L;

			for (WageItemLedgerRow row : rawRows) {

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
				"항목별 대장 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	public List<WageTypeOption> getWageTypeOptions() {

		try (Connection conn = ConnectionProvider.getConnection()) {

			return wageTypeDao.selectWageTypeOptions(conn);

		} catch (SQLException e) {
			throw new RuntimeException(
				"급여항목 목록 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	// 시작 월부터 종료 월까지의 월 목록 생성
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
