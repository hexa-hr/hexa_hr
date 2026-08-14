package wage.model;

// 급여 자동계산 시 입력되는 급여항목 한 건
public class WagePaymentItemInput {

	private Integer wageTypeId;
	private Long wageValue;

	public WagePaymentItemInput(
		Integer wageTypeId,
		Long wageValue) {

		this.wageTypeId = wageTypeId;
		this.wageValue = wageValue;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public Long getWageValue() {
		return wageValue;
	}
}