package master.model;

public class ContactPersonInfo {
	private Integer personId;
	private Integer compId; // Company -> Comp
	private String contName; // Contact -> Cont
	private Integer deptId; // Department -> Dept
	private Integer posId; // Position -> Pos
	private String conPhone; // PhoneNumber -> Phone
	private String mobile;
	private String email;

	public ContactPersonInfo() {}

	public ContactPersonInfo(Integer personId, Integer compId, String contName, Integer deptId,
		Integer posId, String conPhone, String mobile, String email) {
		this.personId = personId;
		this.compId = compId;
		this.contName = contName;
		this.deptId = deptId;
		this.posId = posId;
		this.conPhone = conPhone;
		this.mobile = mobile;
		this.email = email;
	}

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Integer getCompId() {
		return compId;
	}

	public void setCompId(Integer compId) {
		this.compId = compId;
	}

	public String getContName() {
		return contName;
	}

	public void setContName(String contName) {
		this.contName = contName;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public Integer getPosId() {
		return posId;
	}

	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	public String getConPhone() {
		return conPhone;
	}

	public void setConPhone(String conPhone) {
		this.conPhone = conPhone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}