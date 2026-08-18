package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import employee.dao.EmployeeDao;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageTypeOption;
import wage.dao.WageDao;
import wage.model.WageItemCompositionStatisticsDetail;
import wage.model.WageItemCompositionStatisticsResult;
import wage.model.WageItemCompositionStatisticsRow;

// 급여항목 구성 통계 집계 서비스
public class WageItemCompositionStatisticsService {

	private static final String EMPLOYMENT_TYPE_TEMPORARY = "임시직";
	private static final String EMPLOYMENT_TYPE_DAILY = "일용직";

	private static final String WAGE_TYPE_DAILY_WAGE = "일용급여";
	private static final String WAGE_TYPE_BUSINESS_INCOME = "사업소득";
	private static final String WAGE_TYPE_INCOME_TAX = "소득세";
	private static final String WAGE_TYPE_LOCAL_INCOME_TAX = "지방소득세";

	private EmployeeDao employeeDao = new EmployeeDao();
	private WageDao wageDao = new WageDao();
	private WageTypeDao wageTypeDao = new WageTypeDao();

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
					"올바른 사원을 선택해야 합니다.");
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

			List<WageTypeOption> activeWageTypes = wageTypeDao.selectActiveWageTypeOptions(conn);

			Map<String, ItemAmount> itemMap = new LinkedHashMap<>();

			// 현재 사용 중인 급여항목을 기준으로
			// 사원 소득유형에 맞는 0원 기본항목 생성
			for (WageTypeOption wageType : activeWageTypes) {

				if (!shouldIncludeDefaultItem(
					employmentType,
					wageType)) {

					continue;
				}

				String key = createItemKey(
					wageType.getItemType(),
					wageType.getWageTypeName());

				itemMap.put(
					key,
					new ItemAmount(
						wageType.getWageTypeName(),
						wageType.getItemType(),
						0L));
			}

			// 실제 저장된 급여 데이터 반영
			// usage='N' 항목도 실제 데이터가 있으면 포함
			for (WageItemCompositionStatisticsRow actualRow : actualRows) {

				String key = createItemKey(
					actualRow.getItemType(),
					actualRow.getWageTypeName());

				itemMap.put(
					key,
					new ItemAmount(
						actualRow.getWageTypeName(),
						actualRow.getItemType(),
						actualRow.getAmount()));
			}

			long totalPayment = 0L;
			long totalDeduction = 0L;

			for (ItemAmount item : itemMap.values()) {

				if ("P".equals(item.itemType)) {
					totalPayment += item.amount;
				} else if ("D".equals(item.itemType)) {
					totalDeduction += item.amount;
				}
			}

			List<WageItemCompositionStatisticsDetail> paymentItems = new ArrayList<>();

			List<WageItemCompositionStatisticsDetail> deductionItems = new ArrayList<>();

			for (ItemAmount item : itemMap.values()) {

				if ("P".equals(item.itemType)) {

					paymentItems.add(
						new WageItemCompositionStatisticsDetail(
							item.wageTypeName,
							item.itemType,
							item.amount,
							calculateCompositionRate(
								item.amount,
								totalPayment)));

				} else if ("D".equals(item.itemType)) {

					deductionItems.add(
						new WageItemCompositionStatisticsDetail(
							item.wageTypeName,
							item.itemType,
							item.amount,
							calculateCompositionRate(
								item.amount,
								totalDeduction)));
				}
			}

			return new WageItemCompositionStatisticsResult(
				paymentItems,
				deductionItems,
				totalPayment,
				totalDeduction);

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여항목 구성 통계 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private void validateEmployeeId(Integer employeeId) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"올바른 사원을 선택해야 합니다.");
		}
	}

	private String validateAndNormalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속년월을 입력해야 합니다.");
		}

		String normalizedWageMonth = wageMonth.trim();

		try {

			YearMonth.parse(normalizedWageMonth);

		} catch (DateTimeParseException e) {

			throw new IllegalArgumentException(
				"귀속년월은 YYYY-MM 형식이어야 합니다.");
		}

		return normalizedWageMonth;
	}

	private boolean shouldIncludeDefaultItem(
		String employmentType,
		WageTypeOption wageType) {

		String wageTypeName = wageType.getWageTypeName();

		String itemType = wageType.getItemType();

		// 사업소득자
		if (EMPLOYMENT_TYPE_TEMPORARY.equals(
			employmentType)) {

			if ("P".equals(itemType)) {
				return WAGE_TYPE_BUSINESS_INCOME.equals(
					wageTypeName);
			}

			if ("D".equals(itemType)) {
				return isIncomeTax(wageTypeName);
			}

			return false;
		}

		// 일용근로자
		if (EMPLOYMENT_TYPE_DAILY.equals(
			employmentType)) {

			if ("P".equals(itemType)) {
				return WAGE_TYPE_DAILY_WAGE.equals(
					wageTypeName);
			}

			if ("D".equals(itemType)) {
				return isIncomeTax(wageTypeName);
			}

			return false;
		}

		// 일반 근로소득자
		if ("P".equals(itemType)) {

			return !WAGE_TYPE_DAILY_WAGE.equals(
				wageTypeName)
				&& !WAGE_TYPE_BUSINESS_INCOME.equals(
					wageTypeName);
		}

		return "D".equals(itemType);
	}

	private boolean isIncomeTax(String wageTypeName) {

		return WAGE_TYPE_INCOME_TAX.equals(wageTypeName)
			|| WAGE_TYPE_LOCAL_INCOME_TAX.equals(
				wageTypeName);
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

	private String createItemKey(
		String itemType,
		String wageTypeName) {

		return itemType
			+ ":"
			+ wageTypeName;
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

	// Service 내부에서 항목별 금액을 조립하기 위한 임시 객체
	private static class ItemAmount {

		private String wageTypeName;
		private String itemType;
		private long amount;

		private ItemAmount(
			String wageTypeName,
			String itemType,
			long amount) {

			this.wageTypeName = wageTypeName;
			this.itemType = itemType;
			this.amount = amount;
		}
	}
}