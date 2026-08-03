package employee.model;

import java.util.Date;

// 자격증
public class Certification {
	private Integer certificationId;
	private Integer employeeId;
	private String certificationName;
	private Date acquisitionDate;
	private String issuingOrganization;
	private String certificationNumber;
	private String remarks1;

	public Certification(Integer certificationId, Integer employeeId, String certificationName, Date acquisitionDate,
		String issuingOrganization, String certificationNumber, String remarks1) {
		this.certificationId = certificationId;
		this.employeeId = employeeId;
		this.certificationName = certificationName;
		this.acquisitionDate = acquisitionDate;
		this.issuingOrganization = issuingOrganization;
		this.certificationNumber = certificationNumber;
		this.remarks1 = remarks1;
	}

	public Integer getCertificationId() {
		return certificationId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getCertificationName() {
		return certificationName;
	}

	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	public String getIssuingOrganization() {
		return issuingOrganization;
	}

	public String getCertificationNumber() {
		return certificationNumber;
	}

	public String getRemarks1() {
		return remarks1;
	}

}
