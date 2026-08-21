package employee.model;

// 사원별 4대보험 가입정보
public class EmployeeInsurance {

	private String insuranceAgency;
	private Long insuranceAmount;

	public EmployeeInsurance(
		String insuranceAgency,
		Long insuranceAmount) {

		this.insuranceAgency = insuranceAgency;
		this.insuranceAmount = insuranceAmount;
	}

	public String getInsuranceAgency() {
		return insuranceAgency;
	}

	public Long getInsuranceAmount() {
		return insuranceAmount;
	}
}