package employee.model;

import java.util.Date;

// 군 복무
public class MilitaryService {
	private Integer militaryServiceId;
	private Integer employeeId;
	private String serviceType;
	private String branch;
	private Date servicePeriod1;
	private Date servicePeriod2;
	private String finalRank;
	private String department1;
	private String exemptionReason;

	public MilitaryService(Integer militaryServiceId, Integer employeeId, String serviceType, String branch,
		Date servicePeriod1, Date servicePeriod2, String finalRank, String department1, String exemptionReason) {
		this.militaryServiceId = militaryServiceId;
		this.employeeId = employeeId;
		this.serviceType = serviceType;
		this.branch = branch;
		this.servicePeriod1 = servicePeriod1;
		this.servicePeriod2 = servicePeriod2;
		this.finalRank = finalRank;
		this.department1 = department1;
		this.exemptionReason = exemptionReason;
	}

	public Integer getMilitaryServiceId() {
		return militaryServiceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public String getBranch() {
		return branch;
	}

	public Date getServicePeriod1() {
		return servicePeriod1;
	}

	public Date getServicePeriod2() {
		return servicePeriod2;
	}

	public String getFinalRank() {
		return finalRank;
	}

	public String getDepartment1() {
		return department1;
	}

	public String getExemptionReason() {
		return exemptionReason;
	}

}
