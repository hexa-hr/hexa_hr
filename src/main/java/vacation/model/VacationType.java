package vacation.model;

import java.util.Date;

public class VacationType {

	private int vacationTypeId; // NUMBER (PK)
	private String vacationTypeName; // VARCHAR2(50)
	private Date applyPeriod1; // DATE
	private Date applyPeriod2; // DATE
	private String usage; // CHAR(1)
	private int vacationDays; // 화면 출력용 (테이블 외)

	public VacationType() {}

	public int getVacationTypeId() {
		return vacationTypeId;
	}

	public void setVacationTypeId(int vacationTypeId) {
		this.vacationTypeId = vacationTypeId;
	}

	public String getVacationTypeName() {
		return vacationTypeName;
	}

	public void setVacationTypeName(String vacationTypeName) {
		this.vacationTypeName = vacationTypeName;
	}

	public Date getApplyPeriod1() {
		return applyPeriod1;
	}

	public void setApplyPeriod1(Date applyPeriod1) {
		this.applyPeriod1 = applyPeriod1;
	}

	public Date getApplyPeriod2() {
		return applyPeriod2;
	}

	public void setApplyPeriod2(Date applyPeriod2) {
		this.applyPeriod2 = applyPeriod2;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public int getVacationDays() {
		return vacationDays;
	}

	public void setVacationDays(int vacationDays) {
		this.vacationDays = vacationDays;
	}
}