package wage.model;

import java.util.List;

// 연도별 개인연봉 통계 화면에 전달할 전체 조회 결과
public class WageYearlyPersonalStatisticsResult {

	private List<WageYearlyPersonalStatisticsDetail> rows;

	public WageYearlyPersonalStatisticsResult(
		List<WageYearlyPersonalStatisticsDetail> rows) {

		this.rows = rows;
	}

	public List<WageYearlyPersonalStatisticsDetail> getRows() {
		return rows;
	}
}