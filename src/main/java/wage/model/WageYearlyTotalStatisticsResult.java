package wage.model;

import java.util.List;

// 연도별 전체급여 통계 화면에 전달할 전체 조회 결과
public class WageYearlyTotalStatisticsResult {

	private List<WageYearlyTotalStatisticsDetail> rows;

	public WageYearlyTotalStatisticsResult(
		List<WageYearlyTotalStatisticsDetail> rows) {

		this.rows = rows;
	}

	public List<WageYearlyTotalStatisticsDetail> getRows() {
		return rows;
	}
}