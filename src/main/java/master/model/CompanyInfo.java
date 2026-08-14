package master.model;

import java.util.Date;

public class CompanyInfo {
	private Integer companyId;
	private String companyName;
	private String representativeTitle;
	private String representativeName;
	private String businessNumber;
	private String corporationNumber;
	private Date establishmentDate; // java.util.Date 사용
	private String website;
	private String officeAddress;
	private String phoneNumber;
	private String faxNumber;
	private String businessType;
	private String businessItem;

	// 생성자 (기본 생성자 & 모든 필드를 포함하는 생성자)
	public CompanyInfo() {}

	public CompanyInfo(Integer companyId, String companyName, String representativeTitle, String representativeName,
		String businessNumber, String corporationNumber, Date establishmentDate, String website,
		String officeAddress, String phoneNumber, String faxNumber, String businessType, String businessItem) {
		this.companyId = companyId;
		this.companyName = companyName;
		this.representativeTitle = representativeTitle;
		this.representativeName = representativeName;
		this.businessNumber = businessNumber;
		this.corporationNumber = corporationNumber;
		this.establishmentDate = establishmentDate;
		this.website = website;
		this.officeAddress = officeAddress;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.businessType = businessType;
		this.businessItem = businessItem;
	}

	// Getter & Setter (모든 필드에 대해 생성)
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRepresentativeTitle() {
		return representativeTitle;
	}

	public void setRepresentativeTitle(String representativeTitle) {
		this.representativeTitle = representativeTitle;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public String getCorporationNumber() {
		return corporationNumber;
	}

	public void setCorporationNumber(String corporationNumber) {
		this.corporationNumber = corporationNumber;
	}

	public Date getEstablishmentDate() {
		return establishmentDate;
	}

	public void setEstablishmentDate(Date establishmentDate) {
		this.establishmentDate = establishmentDate;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress) {
		this.officeAddress = officeAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessItem() {
		return businessItem;
	}

	public void setBusinessItem(String businessItem) {
		this.businessItem = businessItem;
	}
}