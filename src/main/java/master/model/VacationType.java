package master.model;

import java.util.Date;

// 휴가 항목/종류
public class VacationType {
	private Integer vacationTypeId;
	private String vacationTypeName;
	private Date applyPeriod1;
	private Date applyPeriod2;
	private String usage; // CHAR(1)

	public VacationType(Integer vacationTypeId, String vacationTypeName, Date applyPeriod1, Date applyPeriod2,
		String usage) {
		this.vacationTypeId = vacationTypeId;
		this.vacationTypeName = vacationTypeName;
		this.applyPeriod1 = applyPeriod1;
		this.applyPeriod2 = applyPeriod2;
		this.usage = usage;
	}

	public Integer getVacationTypeId() {
		return vacationTypeId;
	}

	public String getVacationTypeName() {
		return vacationTypeName;
	}

	public Date getApplyPeriod1() {
		return applyPeriod1;
	}

	public Date getApplyPeriod2() {
		return applyPeriod2;
	}

	public String getUsage() {
		return usage;
	}

}
