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

// 급여입력 화면 - 선택 사원 급여 저장 Service
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
				"급여항목 정보가 올바르지 않습니다.");
		}

		/*
		 * 해당 귀속연월/차수의 날짜를
		 * 브라우저가 아니라 서버 기준으로 다시 결정한다.
		 *
		 * 기존 차수 → DB 저장 날짜
		 * 신규 차수 → 회사 급여지급정보 기본 날짜
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
		 * 서버 기준으로
		 * 이 사원의 화면에 존재해야 하는
		 * 급여항목 스냅샷을 다시 조회한다.
		 *
		 * 기존 저장 사원 → 기존 wage 스냅샷
		 * 신규 사원 → 현재 활성/적용 가능 급여항목
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
				"저장할 급여항목이 없습니다.");
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
					"급여항목 정보가 올바르지 않습니다.");
			}

			Integer wageTypeId = input.getWageTypeId();

			if (!baseItemMap.containsKey(
				wageTypeId)) {

				throw new IllegalArgumentException(
					"화면에 존재하지 않는 급여항목이 포함되어 있습니다.");
			}

			if (currentValueMap.containsKey(
				wageTypeId)) {

				throw new IllegalArgumentException(
					"중복된 급여항목이 포함되어 있습니다.");
			}

			long wageValue = input.getWageValue() == null
				? 0L
				: input.getWageValue();

			if (wageValue < 0L) {

				throw new IllegalArgumentException(
					"급여금액은 0원 이상이어야 합니다.");
			}

			currentValueMap.put(
				wageTypeId,
				wageValue);
		}

		/*
		 * 서버에서 확인한 스냅샷과
		 * 사용자가 전송한 급여항목 개수가
		 * 정확히 같아야 한다.
		 */
		if (currentValueMap.size() != baseItems.size()) {

			throw new IllegalArgumentException(
				"급여항목 일부가 누락되었습니다.");
		}

		/*
		 * 검증이 전부 끝난 후에만
		 * DB 변경을 시작한다.
		 */
		try (Connection conn = ConnectionProvider.getConnection()) {

			conn.setAutoCommit(false);

			try {

				/*
				 * 선택 사원의 해당 월/차수만 삭제
				 */
				wageDao.deleteEmployeeWages(
					conn,
					employeeId,
					normalizedWageMonth,
					normalizedWagePeriod);

				/*
				 * 서버 기준 스냅샷 순서대로
				 * 모든 항목을 다시 저장한다.
				 *
				 * 0원 항목도 제외하지 않는다.
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
				"급여 저장 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
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

		String normalized = wageMonth.trim();

		try {

			YearMonth.parse(
				normalized);

		} catch (DateTimeException e) {

			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		return normalized;
	}

	private String normalizeWagePeriod(
		String wagePeriod) {

		if (wagePeriod == null
			|| wagePeriod.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"급여차수를 입력해야 합니다.");
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
				"급여차수는 1 이상 10 이하의 숫자여야 합니다.");
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