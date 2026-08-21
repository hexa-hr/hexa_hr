package wage.model;

// 지난급여 불러오기 - 원본 귀속연월/급여차수 선택 항목
public class WagePaymentPreviousSourceOption {

	private String wageMonth;
	private String wagePeriod;
	private Integer workerEmployeeCount;
	private Integer businessEmployeeCount;

	public WagePaymentPreviousSourceOption(
		String wageMonth,
		String wagePeriod,
		Integer workerEmployeeCount,
		Integer businessEmployeeCount) {

		this.wageMonth = wageMonth;
		this.wagePeriod = wagePeriod;
		this.workerEmployeeCount = workerEmployeeCount;
		this.businessEmployeeCount = businessEmployeeCount;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public String getWagePeriod() {
		return wagePeriod;
	}

	public Integer getWorkerEmployeeCount() {
		return workerEmployeeCount;
	}

	public Integer getBusinessEmployeeCount() {
		return businessEmployeeCount;
	}
}