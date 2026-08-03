package employee.model;

import java.util.Date;

// 경력
public class Career {
	private Integer careerId;
	private Integer employeeId;
	private String companyName;
	private Date startDate;
	private Date endDate;
	private String employmentPeriod;
	private String finalPosition;
	private String responsibilities;
	private String reasonForResignation;

	public Career(Integer careerId, Integer employeeId, String companyName, Date startDate, Date endDate,
		String employmentPeriod, String finalPosition, String responsibilities, String reasonForResignation) {
		this.careerId = careerId;
		this.employeeId = employeeId;
		this.companyName = companyName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.employmentPeriod = employmentPeriod;
		this.finalPosition = finalPosition;
		this.responsibilities = responsibilities;
		this.reasonForResignation = reasonForResignation;
	}

	public Integer getCareerId() {
		return careerId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getEmploymentPeriod() {
		return employmentPeriod;
	}

	public String getFinalPosition() {
		return finalPosition;
	}

	public String getResponsibilities() {
		return responsibilities;
	}

	public String getReasonForResignation() {
		return reasonForResignation;
	}

}
