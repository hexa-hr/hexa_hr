package wage.model;

import java.util.List;

// 월별 개인급여 통계 화면에 전달할 전체 조회 결과
public class WageMonthlyPersonalStatisticsResult {

	private List<WageMonthlyPersonalStatisticsDetail> rows;
	private Long totalPayment;
	private Long totalDeduction;

	public WageMonthlyPersonalStatisticsResult(
		List<WageMonthlyPersonalStatisticsDetail> rows,
		Long totalPayment,
		Long totalDeduction) {

		this.rows = rows;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
	}

	public List<WageMonthlyPersonalStatisticsDetail> getRows() {
		return rows;
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