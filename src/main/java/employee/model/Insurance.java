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

	// 🌟 1. 빈 상자를 만들기 위한 기본 생성자 추가!
	public Insurance() {}

	// 2. 기존 전체 필드 생성자
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

	// 3. Getter 메서드들
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

	// 🌟 4. 값을 집어넣기 위한 Setter 메서드들 추가! (이게 없으면 또 에러 나!)
	public void setInsuranceId(Integer insuranceId) {
		this.insuranceId = insuranceId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public void setInsuranceAgency(String insuranceAgency) {
		this.insuranceAgency = insuranceAgency;
	}

	public void setInsuranceNumber(String insuranceNumber) {
		this.insuranceNumber = insuranceNumber;
	}

	public void setInsuranceAmount(Long insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public void setInsuranceStartDate(Date insuranceStartDate) {
		this.insuranceStartDate = insuranceStartDate;
	}

	public void setInsuranceEndDate(Date insuranceEndDate) {
		this.insuranceEndDate = insuranceEndDate;
	}

	public void setRemarks4(String remarks4) {
		this.remarks4 = remarks4;
	}
}