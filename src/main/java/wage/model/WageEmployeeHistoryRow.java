package wage.model;

// 사원별 급여내역 조회 결과 한 행 (귀속연월 x 급여차수 단위)
public class WageEmployeeHistoryRow {

	private String wageMonth;
	private String wagePeriod;
	private Long monthlyRemuneration;
	private Long totalPayment;
	private Long totalDeduction;
	private Long nationalPension;
	private Long healthInsurance;
	private Long longTermCareInsurance;
	private Long employmentInsurance;
	private Long incomeTax;
	private Long localIncomeTax;

	public WageEmployeeHistoryRow(
		String wageMonth,
		String wagePeriod,
		Long monthlyRemuneration,
		Long totalPayment,
		Long totalDeduction,
		Long nationalPension,
		Long healthInsurance,
		Long longTermCareInsurance,
		Long employmentInsurance,
		Long incomeTax,
		Long localIncomeTax) {

		this.wageMonth = wageMonth;
		this.wagePeriod = wagePeriod;
		this.monthlyRemuneration = monthlyRemuneration;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
		this.nationalPension = nationalPension;
		this.healthInsurance = healthInsurance;
		this.longTermCareInsurance = longTermCareInsurance;
		this.employmentInsurance = employmentInsurance;
		this.incomeTax = incomeTax;
		this.localIncomeTax = localIncomeTax;
	}

	public String getWageMonth() {
		return wageMonth;
	}

	public String getWagePeriod() {
		return wagePeriod;
	}

	public Long getMonthlyRemuneration() {
		return monthlyRemuneration;
	}

	public Long getTotalPayment() {
		return totalPayment;
	}

	public Long getTotalDeduction() {
		return totalDeduction;
	}

	public Long getNetPayment() {
		long payment = totalPayment == null ? 0L : totalPayment;
		long deduction = totalDeduction == null ? 0L : totalDeduction;

		return payment - deduction;
	}

	public Long getNationalPension() {
		return nationalPension;
	}

	public Long getHealthInsurance() {
		return healthInsurance;
	}

	public Long getLongTermCareInsurance() {
		return longTermCareInsurance;
	}

	public Long getEmploymentInsurance() {
		return employmentInsurance;
	}

	public Long getIncomeTax() {
		return incomeTax;
	}

	public Long getLocalIncomeTax() {
		return localIncomeTax;
	}
}