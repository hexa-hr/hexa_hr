package wage.model;

// 월별 전체급여 통계 화면에 표시할 월별 상세 데이터
public class WageMonthlyTotalStatisticsDetail {

	private String wageMonth;
	private Long totalPayment;
	private Double paymentGrowthRate;
	private Integer employeeCount;
	private Double employeeGrowthRate;

	public WageMonthlyTotalStatisticsDetail(
		String wageMonth,
		Long totalPayment,
		Double paymentGrowthRate,
		Integer employeeCount,
		Double employeeGrowthRate) {

		this.wageMonth = wageMonth;
		this.totalPayment = totalPayment;
		this.paymentGrowthRate = paymentGrowthRate;
		this.employeeCount = employeeCount;
		this.employeeGrowthRate = employeeGrowthRate;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Double getPaymentGrowthRate() {
		return paymentGrowthRate;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
	}

	public Double getEmployeeGrowthRate() {
		return employeeGrowthRate;
	}
}