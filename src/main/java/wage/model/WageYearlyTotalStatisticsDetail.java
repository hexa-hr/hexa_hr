package wage.model;

// 연도별 전체급여 통계 화면에 표시할 연도별 상세 데이터
public class WageYearlyTotalStatisticsDetail {

	private String year;
	private Long totalPayment;
	private Double paymentGrowthRate;
	private Double averageEmployeeCount;
	private Double employeeGrowthRate;

	public WageYearlyTotalStatisticsDetail(
		String year,
		Long totalPayment,
		Double paymentGrowthRate,
		Double averageEmployeeCount,
		Double employeeGrowthRate) {

		this.year = year;
		this.totalPayment = totalPayment;
		this.paymentGrowthRate = paymentGrowthRate;
		this.averageEmployeeCount = averageEmployeeCount;
		this.employeeGrowthRate = employeeGrowthRate;
	}

	public String getYear() {
		return year;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Double getPaymentGrowthRate() {
		return paymentGrowthRate;
	}

	public Double getAverageEmployeeCount() {
		return averageEmployeeCount;
	}

	public Double getEmployeeGrowthRate() {
		return employeeGrowthRate;
	}
}