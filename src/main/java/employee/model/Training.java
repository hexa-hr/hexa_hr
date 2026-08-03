package employee.model;

import java.util.Date;

// 교육 훈련
public class Training {
	private Integer trainingId;
	private Integer employeeId;
	private String trainingType;
	private String trainingName;
	private Date trainingStartDate;
	private Date trainingEndDate;
	private String trainingOrganization;
	private Long trainingCost;
	private Long refundableTrainingCost;

	public Training(Integer trainingId, Integer employeeId, String trainingType, String trainingName,
		Date trainingStartDate, Date trainingEndDate, String trainingOrganization, Long trainingCost,
		Long refundableTrainingCost) {
		this.trainingId = trainingId;
		this.employeeId = employeeId;
		this.trainingType = trainingType;
		this.trainingName = trainingName;
		this.trainingStartDate = trainingStartDate;
		this.trainingEndDate = trainingEndDate;
		this.trainingOrganization = trainingOrganization;
		this.trainingCost = trainingCost;
		this.refundableTrainingCost = refundableTrainingCost;
	}

	public Integer getTrainingId() {
		return trainingId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getTrainingType() {
		return trainingType;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public Date getTrainingStartDate() {
		return trainingStartDate;
	}

	public Date getTrainingEndDate() {
		return trainingEndDate;
	}

	public String getTrainingOrganization() {
		return trainingOrganization;
	}

	public Long getTrainingCost() {
		return trainingCost;
	}

	public Long getRefundableTrainingCost() {
		return refundableTrainingCost;
	}

}
