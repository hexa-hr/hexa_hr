package master.model;

import java.util.Date;

// 회사 정보
public class CompanyInfo {
	private Integer companyId;
	private String companyName;
	private String representativeTitle;
	private String representativeName;
	private String businessNumber;
	private String corporationNumber;
	private Date establishmentDate;
	private String website;
	private String officeAddress;
	private String phoneNumber;
	private String faxNumber;
	private String businessType;
	private String businessItem;

	public CompanyInfo(Integer companyId, String companyName, String representativeTitle, String representativeName,
		String businessNumber, String corporationNumber, Date establishmentDate, String website, String officeAddress,
		String phoneNumber, String faxNumber, String businessType, String businessItem) {
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

	public Integer getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getRepresentativeTitle() {
		return representativeTitle;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public String getBusinessNumber() {
		return businessNumber;
	}

	public String getCorporationNumber() {
		return corporationNumber;
	}

	public Date getEstablishmentDate() {
		return establishmentDate;
	}

	public String getWebsite() {
		return website;
	}

	public String getOfficeAddress() {
		return officeAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public String getBusinessType() {
		return businessType;
	}

	public String getBusinessItem() {
		return businessItem;
	}

}
