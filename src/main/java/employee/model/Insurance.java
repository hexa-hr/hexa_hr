package employee.model;

import java.util.Date;

// 보증보험
public class Insurance {
	private Integer insuranceId;
	private Integer employeeId;
	private String insuranceAgency;
	private String insuranceNumber;
	private Long insuranceAmount;
	private Date insuranceStartDate;
	private Date insuranceEndDate;
	private String remarks4;

	public Insurance(Integer insuranceId, Integer employeeId, String insuranceAgency, String insuranceNumber,
		Long insuranceAmount, Date insuranceStartDate, Date insuranceEndDate, String remarks4) {
		this.insuranceId = insuranceId;
		this.employeeId = employeeId;
		this.insuranceAgency = insuranceAgency;
		this.insuranceNumber = insuranceNumber;
		this.insuranceAmount = insuranceAmount;
		this.insuranceStartDate = insuranceStartDate;
		this.insuranceEndDate = insuranceEndDate;
		this.remarks4 = remarks4;
	}

	public Integer getInsuranceId() {
		return insuranceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getInsuranceAgency() {
		return insuranceAgency;
	}

	public String getInsuranceNumber() {
		return insuranceNumber;
	}

	public Long getInsuranceAmount() {
		return insuranceAmount;
	}

	public Date getInsuranceStartDate() {
		return insuranceStartDate;
	}

	public Date getInsuranceEndDate() {
		return insuranceEndDate;
	}

	public String getRemarks4() {
		return remarks4;
	}

}
