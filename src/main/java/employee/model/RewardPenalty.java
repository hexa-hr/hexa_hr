package employee.model;

import java.util.Date;

// 상벌 내역
public class RewardPenalty {
	private Integer rewardPenaltyId;
	private Integer employeeId;
	private String rewardPenaltyType;
	private String rewardPenaltyName;
	private String rewardPenaltyGiver;
	private Date rewardPenaltyDate;
	private String rewardPenaltyDescription;
	private String remarks2;

	public RewardPenalty(Integer rewardPenaltyId, Integer employeeId, String rewardPenaltyType,
		String rewardPenaltyName, String rewardPenaltyGiver, Date rewardPenaltyDate, String rewardPenaltyDescription,
		String remarks2) {
		this.rewardPenaltyId = rewardPenaltyId;
		this.employeeId = employeeId;
		this.rewardPenaltyType = rewardPenaltyType;
		this.rewardPenaltyName = rewardPenaltyName;
		this.rewardPenaltyGiver = rewardPenaltyGiver;
		this.rewardPenaltyDate = rewardPenaltyDate;
		this.rewardPenaltyDescription = rewardPenaltyDescription;
		this.remarks2 = remarks2;
	}

	public Integer getRewardPenaltyId() {
		return rewardPenaltyId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getRewardPenaltyType() {
		return rewardPenaltyType;
	}

	public String getRewardPenaltyName() {
		return rewardPenaltyName;
	}

	public String getRewardPenaltyGiver() {
		return rewardPenaltyGiver;
	}

	public Date getRewardPenaltyDate() {
		return rewardPenaltyDate;
	}

	public String getRewardPenaltyDescription() {
		return rewardPenaltyDescription;
	}

	public String getRemarks2() {
		return remarks2;
	}

}
