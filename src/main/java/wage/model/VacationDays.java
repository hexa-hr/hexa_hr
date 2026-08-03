package wage.model;

// 사원별 잔여 휴가 일수
public class VacationDays {
	private Integer vacationDaysId;
	private Integer vacationTypeId;
	private Integer employeeId;
	private Integer vacationValue;

	public VacationDays(Integer vacationDaysId, Integer vacationTypeId, Integer employeeId, Integer vacationValue) {
		this.vacationDaysId = vacationDaysId;
		this.vacationTypeId = vacationTypeId;
		this.employeeId = employeeId;
		this.vacationValue = vacationValue;
	}

	public Integer getVacationDaysId() {
		return vacationDaysId;
	}

	public Integer getVacationTypeId() {
		return vacationTypeId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public Integer getVacationValue() {
		return vacationValue;
	}

}
