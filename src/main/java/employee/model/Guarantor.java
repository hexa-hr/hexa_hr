package employee.model;

import java.util.Date;

// 보증인
public class Guarantor {
	private Integer guarantorId;
	private Integer employeeId;
	private String guarantorName;
	private String guarantorRelationship;
	private String guarantorResidentNumber;
	private Long guaranteeAmount;
	private Date guaranteeDate;
	private Date guaranteeExpirationDate;
	private String guarantorPhoneNumber;

	public Guarantor(Integer guarantorId, Integer employeeId, String guarantorName, String guarantorRelationship,
		String guarantorResidentNumber, Long guaranteeAmount, Date guaranteeDate, Date guaranteeExpirationDate,
		String guarantorPhoneNumber) {
		this.guarantorId = guarantorId;
		this.employeeId = employeeId;
		this.guarantorName = guarantorName;
		this.guarantorRelationship = guarantorRelationship;
		this.guarantorResidentNumber = guarantorResidentNumber;
		this.guaranteeAmount = guaranteeAmount;
		this.guaranteeDate = guaranteeDate;
		this.guaranteeExpirationDate = guaranteeExpirationDate;
		this.guarantorPhoneNumber = guarantorPhoneNumber;
	}

	public Integer getGuarantorId() {
		return guarantorId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getGuarantorName() {
		return guarantorName;
	}

	public String getGuarantorRelationship() {
		return guarantorRelationship;
	}

	public String getGuarantorResidentNumber() {
		return guarantorResidentNumber;
	}

	public Long getGuaranteeAmount() {
		return guaranteeAmount;
	}

	public Date getGuaranteeDate() {
		return guaranteeDate;
	}

	public Date getGuaranteeExpirationDate() {
		return guaranteeExpirationDate;
	}

	public String getGuarantorPhoneNumber() {
		return guarantorPhoneNumber;
	}

}
