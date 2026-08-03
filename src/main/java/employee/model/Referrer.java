package employee.model;

// 추천인
public class Referrer {
	private Integer referrerId;
	private Integer employeeId;
	private String referrerName;
	private String referrerRelationship;
	private String referrerCompanyName;
	private String referrerPosition;
	private String referrerPhoneNumber;

	public Referrer(Integer referrerId, Integer employeeId, String referrerName, String referrerRelationship,
		String referrerCompanyName, String referrerPosition, String referrerPhoneNumber) {
		this.referrerId = referrerId;
		this.employeeId = employeeId;
		this.referrerName = referrerName;
		this.referrerRelationship = referrerRelationship;
		this.referrerCompanyName = referrerCompanyName;
		this.referrerPosition = referrerPosition;
		this.referrerPhoneNumber = referrerPhoneNumber;
	}

	public Integer getReferrerId() {
		return referrerId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getReferrerName() {
		return referrerName;
	}

	public String getReferrerRelationship() {
		return referrerRelationship;
	}

	public String getReferrerCompanyName() {
		return referrerCompanyName;
	}

	public String getReferrerPosition() {
		return referrerPosition;
	}

	public String getReferrerPhoneNumber() {
		return referrerPhoneNumber;
	}

}
