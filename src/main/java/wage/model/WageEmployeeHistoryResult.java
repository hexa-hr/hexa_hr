package wage.model;

import java.util.List;

// 사원별 급여내역 화면에 전달할 전체 조회 결과
public class WageEmployeeHistoryResult {

	private List<WageEmployeeHistoryRow> rows;
	private Long totalMonthlyRemuneration;
	private Long totalPayment;
	private Long totalDeduction;
	private Long totalNationalPension;
	private Long totalHealthInsurance;
	private Long totalLongTermCareInsurance;
	private Long totalEmploymentInsurance;
	private Long totalIncomeTax;
	private Long totalLocalIncomeTax;

	public WageEmployeeHistoryResult(
		List<WageEmployeeHistoryRow> rows,
		Long totalMonthlyRemuneration,
		Long totalPayment,
		Long totalDeduction,
		Long totalNationalPension,
		Long totalHealthInsurance,
		Long totalLongTermCareInsurance,
		Long totalEmploymentInsurance,
		Long totalIncomeTax,
		Long totalLocalIncomeTax) {

		this.rows = rows;
		this.totalMonthlyRemuneration = totalMonthlyRemuneration;
		this.totalPayment = totalPayment;
		this.totalDeduction = totalDeduction;
		this.totalNationalPension = totalNationalPension;
		this.totalHealthInsurance = totalHealthInsurance;
		this.totalLongTermCareInsurance = totalLongTermCareInsurance;
		this.totalEmploymentInsurance = totalEmploymentInsurance;
		this.totalIncomeTax = totalIncomeTax;
		this.totalLocalIncomeTax = totalLocalIncomeTax;
	}

	public List<WageEmployeeHistoryRow> getRows() {
		return rows;
	}

	public Long getTotalMonthlyRemuneration() {
		return totalMonthlyRemuneration;
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

	public Long getTotalNationalPension() {
		return totalNationalPension;
	}

	public Long getTotalHealthInsurance() {
		return totalHealthInsurance;
	}

	public Long getTotalLongTermCareInsurance() {
		return totalLongTermCareInsurance;
	}

	public Long getTotalEmploymentInsurance() {
		return totalEmploymentInsurance;
	}

	public Long getTotalIncomeTax() {
		return totalIncomeTax;
	}

	public Long getTotalLocalIncomeTax() {
		return totalLocalIncomeTax;
	}
}