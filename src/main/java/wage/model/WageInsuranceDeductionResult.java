package wage.model;

import java.util.List;

// 4대보험 공제내역 화면에 전달할 전체 조회 결과
public class WageInsuranceDeductionResult {
	private WageLedgerSummary summary;

	private List<WageInsuranceDeductionDetail> rows;

	private Long totalNationalPensionEmployer;
	private Long totalNationalPensionEmployee;

	private Long totalHealthInsuranceEmployer;
	private Long totalHealthInsuranceEmployee;

	private Long totalLongTermCareInsuranceEmployer;
	private Long totalLongTermCareInsuranceEmployee;

	private Long totalEmploymentInsuranceEmployer;
	private Long totalEmploymentInsuranceEmployee;

	public WageInsuranceDeductionResult(
		WageLedgerSummary summary,
		List<WageInsuranceDeductionDetail> rows,
		Long totalNationalPensionEmployer,
		Long totalNationalPensionEmployee,
		Long totalHealthInsuranceEmployer,
		Long totalHealthInsuranceEmployee,
		Long totalLongTermCareInsuranceEmployer,
		Long totalLongTermCareInsuranceEmployee,
		Long totalEmploymentInsuranceEmployer,
		Long totalEmploymentInsuranceEmployee) {

		this.summary = summary;
		this.rows = rows;
		this.totalNationalPensionEmployer = totalNationalPensionEmployer;
		this.totalNationalPensionEmployee = totalNationalPensionEmployee;
		this.totalHealthInsuranceEmployer = totalHealthInsuranceEmployer;
		this.totalHealthInsuranceEmployee = totalHealthInsuranceEmployee;
		this.totalLongTermCareInsuranceEmployer = totalLongTermCareInsuranceEmployer;
		this.totalLongTermCareInsuranceEmployee = totalLongTermCareInsuranceEmployee;
		this.totalEmploymentInsuranceEmployer = totalEmploymentInsuranceEmployer;
		this.totalEmploymentInsuranceEmployee = totalEmploymentInsuranceEmployee;
	}

	public WageLedgerSummary getSummary() {
		return summary;
	}

	public List<WageInsuranceDeductionDetail> getRows() {
		return rows;
	}

	public Long getTotalNationalPensionEmployer() {
		return totalNationalPensionEmployer;
	}

	public Long getTotalNationalPensionEmployee() {
		return totalNationalPensionEmployee;
	}

	public Long getTotalNationalPension() {
		return safe(totalNationalPensionEmployer)
			+ safe(totalNationalPensionEmployee);
	}

	public Long getTotalHealthInsuranceEmployer() {
		return totalHealthInsuranceEmployer;
	}

	public Long getTotalHealthInsuranceEmployee() {
		return totalHealthInsuranceEmployee;
	}

	public Long getTotalHealthInsurance() {
		return safe(totalHealthInsuranceEmployer)
			+ safe(totalHealthInsuranceEmployee);
	}

	public Long getTotalLongTermCareInsuranceEmployer() {
		return totalLongTermCareInsuranceEmployer;
	}

	public Long getTotalLongTermCareInsuranceEmployee() {
		return totalLongTermCareInsuranceEmployee;
	}

	public Long getTotalLongTermCareInsurance() {
		return safe(totalLongTermCareInsuranceEmployer)
			+ safe(totalLongTermCareInsuranceEmployee);
	}

	public Long getTotalEmploymentInsuranceEmployer() {
		return totalEmploymentInsuranceEmployer;
	}

	public Long getTotalEmploymentInsuranceEmployee() {
		return totalEmploymentInsuranceEmployee;
	}

	public Long getTotalEmploymentInsurance() {
		return safe(totalEmploymentInsuranceEmployer)
			+ safe(totalEmploymentInsuranceEmployee);
	}

	public Long getTotalEmployer() {
		return safe(totalNationalPensionEmployer)
			+ safe(totalHealthInsuranceEmployer)
			+ safe(totalLongTermCareInsuranceEmployer)
			+ safe(totalEmploymentInsuranceEmployer);
	}

	public Long getTotalEmployee() {
		return safe(totalNationalPensionEmployee)
			+ safe(totalHealthInsuranceEmployee)
			+ safe(totalLongTermCareInsuranceEmployee)
			+ safe(totalEmploymentInsuranceEmployee);
	}

	public Long getGrandTotal() {
		return getTotalEmployer() + getTotalEmployee();
	}

	private long safe(Long value) {
		return value == null ? 0L : value;
	}
}