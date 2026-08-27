package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import employee.dao.EmployeeDao;
import jdbc.connection.ConnectionProvider;
import wage.dao.WageDao;
import wage.model.WageItemCompositionStatisticsDetail;
import wage.model.WageItemCompositionStatisticsResult;
import wage.model.WageItemCompositionStatisticsRow;

// 급여항목 구성 통계 집계 서비스
public class WageItemCompositionStatisticsService {

	private static final int TABLE_VISIBLE_ITEM_COUNT = 9;

	private EmployeeDao employeeDao = new EmployeeDao();
	private WageDao wageDao = new WageDao();

	public WageItemCompositionStatisticsResult getItemCompositionStatistics(
		Integer employeeId,
		String wageMonth) {

		validateEmployeeId(employeeId);
		String normalizedWageMonth = validateAndNormalizeWageMonth(wageMonth);

		try (Connection conn = ConnectionProvider.getConnection()) {

			String employmentType = employeeDao.selectEmploymentType(
				conn,
				employeeId);

			if (employmentType == null) {
				throw new IllegalArgumentException(
					"正しい社員を選択する必要があります。");
			}

			// 실제 저장된 급여항목은 usage와 관계없이 조회
			List<WageItemCompositionStatisticsRow> actualRows = wageDao.selectItemCompositionStatisticsRows(
				conn,
				employeeId,
				normalizedWageMonth);

			// 해당 귀속월에 급여 데이터가 없으면
			// 0원 기본항목도 생성하지 않음
			if (actualRows.isEmpty()) {
				return emptyResult();
			}

			long totalPayment = 0L;
			long totalDeduction = 0L;

			// 선택 사원의 해당 귀속월 모든 차수 금액 합계
			for (WageItemCompositionStatisticsRow actualRow : actualRows) {

				if ("P".equals(actualRow.getItemType())) {
					totalPayment += actualRow.getAmount();

				} else if ("D".equals(actualRow.getItemType())) {
					totalDeduction += actualRow.getAmount();
				}
			}

			List<WageItemCompositionStatisticsDetail> paymentItems = new ArrayList<>();

			List<WageItemCompositionStatisticsDetail> deductionItems = new ArrayList<>();

			// WAGE에 실제 존재하는 항목만 화면 목록으로 구성
			for (WageItemCompositionStatisticsRow actualRow : actualRows) {

				if ("P".equals(actualRow.getItemType())) {

					paymentItems.add(
						new WageItemCompositionStatisticsDetail(
							actualRow.getWageTypeName(),
							actualRow.getItemType(),
							actualRow.getAmount(),
							calculateCompositionRate(
								actualRow.getAmount(),
								totalPayment)));

				} else if ("D".equals(actualRow.getItemType())) {

					deductionItems.add(
						new WageItemCompositionStatisticsDetail(
							actualRow.getWageTypeName(),
							actualRow.getItemType(),
							actualRow.getAmount(),
							calculateCompositionRate(
								actualRow.getAmount(),
								totalDeduction)));
				}
			}

			List<WageItemCompositionStatisticsDetail> tablePaymentItems = createTableItems(
				paymentItems,
				totalPayment,
				"P");

			List<WageItemCompositionStatisticsDetail> tableDeductionItems = createTableItems(
				deductionItems,
				totalDeduction,
				"D");

			return new WageItemCompositionStatisticsResult(
				paymentItems,
				deductionItems,
				tablePaymentItems,
				tableDeductionItems,
				totalPayment,
				totalDeduction);

		} catch (SQLException e) {

			throw new RuntimeException(
				"給与項目構成統計の照会中にデータベースエラーが発生しました。",
				e);
		}
	}

	private void validateEmployeeId(Integer employeeId) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"正しい社員を選択する必要があります。");
		}
	}

	private String validateAndNormalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"帰属年月を入力する必要があります。");
		}

		String normalizedWageMonth = wageMonth.trim();

		try {

			YearMonth.parse(normalizedWageMonth);

		} catch (DateTimeParseException e) {

			throw new IllegalArgumentException(
				"帰属年月はYYYY-MM形式である必要があります。");
		}

		return normalizedWageMonth;
	}

	private List<WageItemCompositionStatisticsDetail> createTableItems(
		List<WageItemCompositionStatisticsDetail> items,
		long totalAmount,
		String itemType) {

		if (items.size() <= TABLE_VISIBLE_ITEM_COUNT) {
			return new ArrayList<>(items);
		}

		List<WageItemCompositionStatisticsDetail> tableItems = new ArrayList<>();

		for (int index = 0; index < TABLE_VISIBLE_ITEM_COUNT; index++) {

			tableItems.add(items.get(index));
		}

		long otherAmount = 0L;

		for (int index = TABLE_VISIBLE_ITEM_COUNT; index < items.size(); index++) {

			otherAmount += items.get(index).getAmount();
		}

		int otherCount = items.size() - TABLE_VISIBLE_ITEM_COUNT;

		tableItems.add(
			new WageItemCompositionStatisticsDetail(
				"その他(" + otherCount + ")",
				itemType,
				otherAmount,
				calculateCompositionRate(
					otherAmount,
					totalAmount)));

		return tableItems;
	}

	private double calculateCompositionRate(
		long amount,
		long totalAmount) {

		if (totalAmount == 0L) {
			return 0.0;
		}

		return (double)amount
			/ totalAmount
			* 100.0;
	}

	private WageItemCompositionStatisticsResult emptyResult() {

		return new WageItemCompositionStatisticsResult(
			Collections
				.<WageItemCompositionStatisticsDetail>emptyList(),
			Collections
				.<WageItemCompositionStatisticsDetail>emptyList(),
			0L,
			0L);
	}
}