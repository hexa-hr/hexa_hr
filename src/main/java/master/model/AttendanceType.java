package master.model;

// 근태 항목
public class AttendanceType {
	private Integer attendanceTypeId;
	private String attendanceTypeName;
	private String unit;
	private Integer attendanceGroupId;
	private Integer vacationTypeId;
	private String usage; // CHAR(1)

	// [추가된 부분] JOIN으로 가져올 휴가 기간 데이터를 담을 변수
	private String vacationTypeName;
	private java.util.Date applyPeriod1;
	private java.util.Date applyPeriod2;

	// 기존 생성자는 그대로 유지합니다. (팀원 코드 보호)
	public AttendanceType(Integer attendanceTypeId, String attendanceTypeName, String unit, Integer attendanceGroupId,
			Integer vacationTypeId, String usage) {
		this.attendanceTypeId = attendanceTypeId;
		this.attendanceTypeName = attendanceTypeName;
		this.unit = unit;
		this.attendanceGroupId = attendanceGroupId;
		this.vacationTypeId = vacationTypeId;
		this.usage = usage;
	}

	public Integer getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public String getAttendanceTypeName() {
		return attendanceTypeName;
	}

	public String getUnit() {
		return unit;
	}

	public Integer getAttendanceGroupId() {
		return attendanceGroupId;
	}

	public Integer getVacationTypeId() {
		return vacationTypeId;
	}

	public String getUsage() {
		return usage;
	}

	// [추가된 부분] 새로 추가한 변수들의 Getter / Setter
	public String getVacationTypeName() {
		return vacationTypeName;
	}

	public void setVacationTypeName(String vacationTypeName) {
		this.vacationTypeName = vacationTypeName;
	}

	public java.util.Date getApplyPeriod1() {
		return applyPeriod1;
	}

	public void setApplyPeriod1(java.util.Date applyPeriod1) {
		this.applyPeriod1 = applyPeriod1;
	}

	public java.util.Date getApplyPeriod2() {
		return applyPeriod2;
	}

	public void setApplyPeriod2(java.util.Date applyPeriod2) {
		this.applyPeriod2 = applyPeriod2;
	}
}