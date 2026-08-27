package wage.model;

// 월별 개인급여 통계 화면에 표시할 월별 상세 데이터
public class WageMonthlyPersonalStatisticsDetail {

	private String wageMonth;
	private Long totalPayment;
	private Long totalDeduction;

	public WageMonthlyPersonalStatisticsDetail(
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

	public Long getNetPayment() {
		return safe(totalPayment) - safe(totalDeduction);
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}