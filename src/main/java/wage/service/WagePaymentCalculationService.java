package wage.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentCalculationRequest;
import wage.model.WagePaymentCalculationResult;
import wage.model.WagePaymentItemInput;

// 급여 자동계산 서비스
public class WagePaymentCalculationService {

	private WageTypeDao wageTypeDao = new WageTypeDao();

	public WagePaymentCalculationResult calculate(
		WagePaymentCalculationRequest request) {

		validateRequest(request);

		try (Connection conn = ConnectionProvider.getConnection()) {

			List<WageType> wageTypes = wageTypeDao.selectActiveWageTypes(conn);

			Map<Integer, WageType> wageTypeMap = new LinkedHashMap<>();

			for (WageType wageType : wageTypes) {
				wageTypeMap.put(
					wageType.getWageTypeId(),
					wageType);
			}

			List<WagePaymentCalculationItem> paymentItems = new ArrayList<>();

			List<WagePaymentCalculationItem> deductionItems = new ArrayList<>();

			Set<Integer> inputWageTypeIds = new HashSet<>();

			long totalPayment = 0L;
			long taxFreeAmount = 0L;
			long totalDeduction = 0L;

			for (WagePaymentItemInput input : request.getItemInputs()) {

				if (input == null || input.getWageTypeId() == null) {
					throw new IllegalArgumentException(
						"급여항목 정보가 올바르지 않습니다.");
				}

				Integer wageTypeId = input.getWageTypeId();

				if (!inputWageTypeIds.add(wageTypeId)) {
					throw new IllegalArgumentException(
						"중복된 급여항목이 포함되어 있습니다.");
				}

				WageType wageType = wageTypeMap.get(wageTypeId);

				if (wageType == null) {
					throw new IllegalArgumentException(
						"현재 사용 중이지 않거나 존재하지 않는 급여항목입니다.");
				}

				long wageValue = 0L;

				if (input.getWageValue() != null) {
					wageValue = input.getWageValue();
				}

				if (wageValue < 0) {
					throw new IllegalArgumentException(
						"급여금액은 0원 이상이어야 합니다.");
				}

				WagePaymentCalculationItem item = new WagePaymentCalculationItem(
					wageType.getWageTypeId(),
					wageType.getWageTypeName(),
					wageType.getItemType(),
					wageType.getTaxableYn(),
					wageValue);

				if ("P".equals(wageType.getItemType())) {

					paymentItems.add(item);
					totalPayment += wageValue;

					if ("N".equals(wageType.getTaxableYn())) {

						long taxFreeLimit = 0L;

						if (wageType.getTaxFreeLimit() != null) {
							taxFreeLimit = wageType.getTaxFreeLimit();
						}

						taxFreeAmount += Math.min(wageValue, taxFreeLimit);
					}

				} else if ("D".equals(wageType.getItemType())) {

					deductionItems.add(item);
					totalDeduction += wageValue;

				} else {

					throw new IllegalStateException(
						"급여항목의 지급·공제 구분이 올바르지 않습니다.");
				}
			}

			long monthlyRemuneration = totalPayment - taxFreeAmount;

			long netPayment = totalPayment - totalDeduction;

			return new WagePaymentCalculationResult(
				paymentItems,
				deductionItems,
				totalPayment,
				taxFreeAmount,
				monthlyRemuneration,
				totalDeduction,
				netPayment);

		} catch (SQLException e) {
			throw new RuntimeException(
				"급여 자동계산 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private void validateRequest(
		WagePaymentCalculationRequest request) {

		if (request == null) {
			throw new IllegalArgumentException(
				"급여 계산 요청 정보가 없습니다.");
		}

		if (request.getEmployeeId() == null
			|| request.getEmployeeId() <= 0) {

			throw new IllegalArgumentException(
				"사원 정보가 올바르지 않습니다.");
		}

		String wageMonth = request.getWageMonth();

		if (wageMonth == null) {
			throw new IllegalArgumentException(
				"귀속연월을 입력해야 합니다.");
		}

		wageMonth = wageMonth.trim();

		try {
			YearMonth.parse(wageMonth);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
				"귀속연월은 YYYY-MM 형식이어야 합니다.");
		}

		if (request.getItemInputs() == null) {
			throw new IllegalArgumentException(
				"급여항목 정보가 없습니다.");
		}
	}
}