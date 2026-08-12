package wage.model;

// 연도별 개인연봉 통계 화면에 표시할 연도별 상세 데이터
public class WageYearlyPersonalStatisticsDetail {

	private String year;
	private Long annualSalary;
	private Double salaryGrowthRate;
	private Long totalDeduction;
	private boolean hasData;

	public WageYearlyPersonalStatisticsDetail(
		String year,
		Long annualSalary,
		Double salaryGrowthRate,
		Long totalDeduction,
		boolean hasData) {

		this.year = year;
		this.annualSalary = annualSalary;
		this.salaryGrowthRate = salaryGrowthRate;
		this.totalDeduction = totalDeduction;
		this.hasData = hasData;
	}

	public String getYear() {
		return year;
	}

	public Long getAnnualSalary() {
		return annualSalary;
	}

	public Double getSalaryGrowthRate() {
		return salaryGrowthRate;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		return safe(annualSalary) - safe(totalDeduction);
	}

	public boolean isHasData() {
		return hasData;
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}