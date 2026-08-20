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

// 급여입력/관리(일용직) - 선택 사원 급여 저장 Service
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
				"공제항목 정보가 올바르지 않습니다.");
		}

		/*
		 * 기존 월·차수이면 저장된 날짜,
		 * 신규 월·차수이면 회사 급여설정의 기본 날짜를 사용한다.
		 */
		PeriodDates periodDates = resolvePeriodDates(
			normalizedWageMonth,
			normalizedWagePeriod);

		/*
		 * 저장 직전에 서버에서 다시 계산한다.
		 *
		 * - 일용직 사원 여부
		 * - DAILY_WORK 지급액과 세금
		 * - 공제항목 스냅샷
		 * - 보험 가입정보와 보험료
		 * - 전송된 공제항목 ID
		 */
		WagePaymentAutoCalculationResult canonicalResult = dailyWagePaymentInputService.calculate(
			employeeId,
			normalizedWageMonth,
			normalizedWagePeriod,
			periodDates.settlementStartDate,
			periodDates.settlementEndDate,
			currentDeductionInputs);

		if (canonicalResult == null) {

			throw new IllegalStateException(
				"일용직 급여 계산 결과가 없습니다.");
		}

		long totalPayment = requireNonNegative(
			canonicalResult.getTotalPayment(),
			"지급총액");

		List<WagePaymentInputViewItem> deductionItems = canonicalResult.getWageItems();

		if (deductionItems == null) {

			throw new IllegalStateException(
				"일용직 급여 공제항목 계산 결과가 없습니다.");
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
					"일용직 급여 공제항목 계산 결과가 올바르지 않습니다.");
			}

			Integer wageTypeId = deductionItem.getWageTypeId();

			if (Integer.valueOf(
				WageTypeSystemIds.BASIC_PAY_ID)
				.equals(wageTypeId)) {

				throw new IllegalStateException(
					"지급항목이 공제항목에 포함되어 있습니다.");
			}

			long wageValue = requireNonNegative(
				deductionItem.getWageValue(),
				"공제금액");

			if (deductionValueMap.put(
				wageTypeId,
				wageValue) != null) {

				throw new IllegalStateException(
					"중복된 공제항목 계산 결과가 존재합니다.");
			}

			totalDeduction += wageValue;
		}

		long netPayment = totalPayment - totalDeduction;

		if (canonicalResult.getTotalDeduction() == null
			|| canonicalResult.getTotalDeduction().longValue() != totalDeduction
			|| canonicalResult.getNetPayment() == null
			|| canonicalResult.getNetPayment().longValue() != netPayment) {

			throw new IllegalStateException(
				"일용직 급여 계산 결과의 합계가 일치하지 않습니다.");
		}

		/*
		 * WAGE 변경은 하나의 트랜잭션으로 처리한다.
		 * DAILY_WORK는 수정하지 않는다.
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
				 * 일용직 지급총액은 기본급 시스템 항목
				 * wage_type_id=1에 저장한다.
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
				 * 모든 공제항목을 0원 항목까지 저장한다.
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
				"일용직 급여 저장 중 데이터베이스 오류가 발생했습니다.",
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
				"급여차수 날짜 정보가 올바르지 않습니다.");
		}

		if (settlementStartDate.after(
			settlementEndDate)) {

			throw new IllegalStateException(
				"정산 시작일은 종료일보다 늦을 수 없습니다.");
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
				"올바른 사원을 선택해야 합니다.");
		}
	}

	private String normalizeWageMonth(
		String wageMonth) {

		if (wageMonth == null
			|| wageMonth.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		try {

			return YearMonth.parse(
				wageMonth.trim()).toString();

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
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
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
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
					+ "은 0원 이상이어야 합니다.");
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