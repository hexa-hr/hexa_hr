package attendance.model;

import java.util.Date;

// 퇴직 정보 및 정산
public class Retirement {
	private Integer retirementId;
	private Integer employeeId;
	private String retirementType;
	private Date retirementDate;
	private String retirementReason;
	private String contactAfterRetirement;
	private Long retirementPay;

	public Retirement(Integer retirementId, Integer employeeId, String retirementType, Date retirementDate,
		String retirementReason, String contactAfterRetirement, Long retirementPay) {
		this.retirementId = retirementId;
		this.employeeId = employeeId;
		this.retirementType = retirementType;
		this.retirementDate = retirementDate;
		this.retirementReason = retirementReason;
		this.contactAfterRetirement = contactAfterRetirement;
		this.retirementPay = retirementPay;
	}

	public Integer getRetirementId() {
		return retirementId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getRetirementType() {
		return retirementType;
	}

	public Date getRetirementDate() {
		return retirementDate;
	}

	public String getRetirementReason() {
		return retirementReason;
	}

	public String getContactAfterRetirement() {
		return contactAfterRetirement;
	}

	public Long getRetirementPay() {
		return retirementPay;
	}

}
