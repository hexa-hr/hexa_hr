package wage.model;

// 월별 개인급여 통계 조회 결과 한 행
public class WageMonthlyPersonalStatisticsRow {

	private String wageMonth;
	private Long totalPayment;
	private Long totalDeduction;

	public WageMonthlyPersonalStatisticsRow(
		String wageMonth,
		Long totalPayment,
		Long totalDeduction) {

		this.wageMonth = wageMonth;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}
}