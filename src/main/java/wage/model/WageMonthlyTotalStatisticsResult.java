package wage.model;

import java.util.List;

// 월별 전체급여 통계 화면에 전달할 전체 조회 결과
public class WageMonthlyTotalStatisticsResult {

	private List<WageMonthlyTotalStatisticsDetail> rows;
	private Long totalPayment;
	private Double averageEmployeeCount;

	public WageMonthlyTotalStatisticsResult(
		List<WageMonthlyTotalStatisticsDetail> rows,
		Long totalPayment,
		Double averageEmployeeCount) {

		this.rows = rows;
		this.totalPayment = totalPayment;
		this.averageEmployeeCount = averageEmployeeCount;
	}

	public List<WageMonthlyTotalStatisticsDetail> getRows() {
		return rows;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Double getAverageEmployeeCount() {
		return averageEmployeeCount;
	}
}