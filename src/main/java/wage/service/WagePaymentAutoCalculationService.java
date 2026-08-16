package wage.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import wage.model.WagePaymentAutoCalculationResult;
import wage.model.WagePaymentCalculationItem;
import wage.model.WagePaymentCalculationRequest;
import wage.model.WagePaymentCalculationResult;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentItemInput;

// 급여입력 화면 자동계산 처리 Service
public class WagePaymentAutoCalculationService {

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private WagePaymentCalculationService wagePaymentCalculationService = new WagePaymentCalculationService();

	public WagePaymentAutoCalculationResult calculate(
		Integer employeeId,
		String wageMonth,
		String wagePeriod,
		Date settlementStartDate,
		Date settlementEndDate,
		List<WagePaymentItemInput> currentItemInputs) {

		if (currentItemInputs == null) {

			throw new IllegalArgumentException(
				"급여항목 정보가 올바르지 않습니다.");
		}

		/*
		 * DB 기준으로 화면에 존재해야 하는 급여항목과
		 * active / calculable 정보를 다시 조회한다.
		 *
		 * 브라우저가 보내는 active/calculable 값을
		 * 신뢰하지 않는다.
		 */
		List<WagePaymentInputViewItem> baseItems = wagePaymentInputService.getViewItems(
			employeeId,
			wageMonth,
			wagePeriod,
			settlementStartDate,
			settlementEndDate);

		Map<Integer, WagePaymentInputViewItem> baseItemMap = new LinkedHashMap<>();

		for (WagePaymentInputViewItem baseItem : baseItems) {

			baseItemMap.put(
				baseItem.getWageTypeId(),
				baseItem);
		}

		/*
		 * 사용자가 화면에서 입력한 현재 금액을
		 * 급여항목 ID 기준으로 구성한다.
		 */
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
		 * 화면에 표시된 모든 급여항목이
		 * POST 데이터에도 존재해야 한다.
		 *
		 * 과거 비활성 급여항목의 유실 방지 목적.
		 */
		if (currentValueMap.size() != baseItems.size()) {

			throw new IllegalArgumentException(
				"급여항목 일부가 누락되었습니다.");
		}

		List<WagePaymentInputViewItem> currentItems = new ArrayList<>();

		List<WagePaymentItemInput> calculationInputs = new ArrayList<>();

		for (WagePaymentInputViewItem baseItem : baseItems) {

			Long wageValue = currentValueMap.get(
				baseItem.getWageTypeId());

			WagePaymentInputViewItem currentItem = new WagePaymentInputViewItem(
				baseItem.getWageTypeId(),
				baseItem.getWageTypeName(),
				baseItem.getItemType(),
				baseItem.getTaxableYn(),
				wageValue,
				baseItem.isActive(),
				baseItem.isCalculable());

			currentItems.add(currentItem);

			/*
			 * 자동계산 가능 항목만
			 * 기존 계산 Service에 전달한다.
			 */
			if (baseItem.isCalculable()) {

				calculationInputs.add(
					new WagePaymentItemInput(
						baseItem.getWageTypeId(),
						wageValue));
			}
		}

		Map<Integer, Long> calculatedValueMap = new LinkedHashMap<>();

		if (!calculationInputs.isEmpty()) {

			WagePaymentCalculationRequest request = new WagePaymentCalculationRequest(
				employeeId,
				wageMonth,
				calculationInputs);

			WagePaymentCalculationResult calculationResult = wagePaymentCalculationService.calculate(
				request);

			addCalculatedItems(
				calculatedValueMap,
				calculationResult.getPaymentItems());

			addCalculatedItems(
				calculatedValueMap,
				calculationResult.getDeductionItems());
		}

		List<WagePaymentInputViewItem> mergedItems = new ArrayList<>();

		long totalPayment = 0L;
		long totalDeduction = 0L;

		for (WagePaymentInputViewItem currentItem : currentItems) {

			long finalValue;

			if (currentItem.isCalculable()) {

				/*
				 * 계산 가능한 항목은 계산 결과로 갱신한다.
				 *
				 * 계산 결과에 없는 보험 항목 등은
				 * 0원으로 처리한다.
				 */
				Long calculatedValue = calculatedValueMap.remove(
					currentItem.getWageTypeId());

				finalValue = calculatedValue == null
					? 0L
					: calculatedValue;

			} else {

				/*
				 * 연말정산 등 계산 제외 항목은
				 * 사용자가 입력한 현재 값을 그대로 유지한다.
				 */
				finalValue = currentItem.getWageValue() == null
					? 0L
					: currentItem.getWageValue();
			}

			WagePaymentInputViewItem mergedItem = new WagePaymentInputViewItem(
				currentItem.getWageTypeId(),
				currentItem.getWageTypeName(),
				currentItem.getItemType(),
				currentItem.getTaxableYn(),
				finalValue,
				currentItem.isActive(),
				currentItem.isCalculable());

			mergedItems.add(mergedItem);

			if ("P".equals(
				mergedItem.getItemType())) {

				totalPayment += finalValue;

			} else if ("D".equals(
				mergedItem.getItemType())) {

				totalDeduction += finalValue;
			}
		}

		/*
		 * 계산 Service가 화면에 존재하지 않는
		 * 급여항목을 반환한 경우는 비정상 상태.
		 */
		if (!calculatedValueMap.isEmpty()) {

			throw new IllegalStateException(
				"자동계산 결과에 알 수 없는 급여항목이 포함되어 있습니다.");
		}

		long netPayment = totalPayment - totalDeduction;

		return new WagePaymentAutoCalculationResult(
			mergedItems,
			totalPayment,
			totalDeduction,
			netPayment);
	}

	private void addCalculatedItems(
		Map<Integer, Long> calculatedValueMap,
		List<WagePaymentCalculationItem> items) {

		if (items == null) {
			return;
		}

		for (WagePaymentCalculationItem item : items) {

			if (item == null
				|| item.getWageTypeId() == null) {

				throw new IllegalStateException(
					"자동계산 결과의 급여항목 정보가 올바르지 않습니다.");
			}

			if (calculatedValueMap.containsKey(
				item.getWageTypeId())) {

				throw new IllegalStateException(
					"자동계산 결과에 중복된 급여항목이 포함되어 있습니다.");
			}

			long wageValue = item.getWageValue() == null
				? 0L
				: item.getWageValue();

			calculatedValueMap.put(
				item.getWageTypeId(),
				wageValue);
		}
	}
}