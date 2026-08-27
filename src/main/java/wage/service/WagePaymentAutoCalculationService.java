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

// 給与入力画面自動計算処理Service
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
				"給与項目情報が正しくありません。");
		}

		/*
		 * DBを基準に画面に存在すべき給与項目と
		 * active / calculable情報を再照会する。
		 *
		 * ブラウザから送信されたactive / calculable値を
		 * 信頼しない。
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
		 * ユーザーが画面で入力した現在の金額を
		 * 給与項目IDを基準に構成する。
		 */
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
		 * 画面に表示されたすべての給与項目が
		 * POSTデータにも存在する必要がある。
		 *
		 * 過去の非アクティブ給与項目の欠落防止が目的。
		 */
		if (currentValueMap.size() != baseItems.size()) {

			throw new IllegalArgumentException(
				"給与項目の一部が欠落しています。");
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
			 * 自動計算可能な項目のみ
			 * 既存の計算Serviceに渡す。
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
				settlementStartDate,
				settlementEndDate,
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
				 * 計算可能な項目は計算結果で更新する。
				 *
				 * 計算結果にない保険項目などは
				 * 0ウォンとして処理する。
				 */
				Long calculatedValue = calculatedValueMap.remove(
					currentItem.getWageTypeId());

				finalValue = calculatedValue == null
					? 0L
					: calculatedValue;

			} else {

				/*
				 * 年末調整などの計算対象外項目は
				 * ユーザーが入力した現在の値をそのまま維持する。
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
		 * 計算Serviceが現在の画面にない給与項目を返す場合がある。
		 *
		 * 既存の保存済み給与は保存当時の給与項目構成を維持する必要があるため、
		 * 画面スナップショットに存在しない計算結果は反映しない。
		 */

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
					"自動計算結果の給与項目情報が正しくありません。");
			}

			if (calculatedValueMap.containsKey(
				item.getWageTypeId())) {

				throw new IllegalStateException(
					"自動計算結果に重複した給与項目が含まれています。");
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