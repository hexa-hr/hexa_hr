package wage.model;

// 월별 전체급여 통계 조회 결과 한 행
public class WageMonthlyTotalStatisticsRow {

	private String wageMonth;
	private Long totalPayment;
	private Integer employeeCount;

	public WageMonthlyTotalStatisticsRow(
		String wageMonth,
		Long totalPayment,
		Integer employeeCount) {

		this.wageMonth = wageMonth;
		this.totalPayment = totalPayment;
		this.employeeCount = employeeCount;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Integer getEmployeeCount() {
		return employeeCount;
	}
}