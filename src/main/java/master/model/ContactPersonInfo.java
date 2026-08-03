package master.model;

// 담당자 정보
public class ContactPersonInfo {
	private Integer personId;
	private Integer companyId;
	private String contactName;
	private Integer departmentId;
	private Integer positionId;
	private String conPhoneNumber;
	private String mobileNumber;
	private String email;

	public ContactPersonInfo(Integer personId, Integer companyId, String contactName, Integer departmentId,
		Integer positionId, String conPhoneNumber, String mobileNumber, String email) {
		this.personId = personId;
		this.companyId = companyId;
		this.contactName = contactName;
		this.departmentId = departmentId;
		this.positionId = positionId;
		this.conPhoneNumber = conPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
	}

	public Integer getPersonId() {
		return personId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public String getContactName() {
		return contactName;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public String getConPhoneNumber() {
		return conPhoneNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public String getEmail() {
		return email;
	}

}
