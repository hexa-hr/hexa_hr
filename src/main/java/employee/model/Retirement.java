package employee.model;

import java.util.Date;

public class Retirement {
	private Integer employeeId; // FK (누구의 퇴직정보인지)
	private String retirementType; // 퇴직구분 (정년퇴직, 자진퇴사 등)
	private Date retirementDate; // 퇴직일자
	private String retirementReason; // 퇴직사유
	private String retirementContact; // 퇴직 후 연락처
	private Long severancePay; // 퇴직금

	public Retirement(Integer employeeId, String retirementType, Date retirementDate,
		String retirementReason, String retirementContact, Long severancePay) {
		this.employeeId = employeeId;
		this.retirementType = retirementType;
		this.retirementDate = retirementDate;
		this.retirementReason = retirementReason;
		this.retirementContact = retirementContact;
		this.severancePay = severancePay;
	}

	// ⭐ Getter/Setter 필수 생성!
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

	public String getRetirementContact() {
		return retirementContact;
	}

	public Long getSeverancePay() {
		return severancePay;
	}
}