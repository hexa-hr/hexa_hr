package master.model;

// 근태 항목
public class AttendanceType {
	private Integer attendanceTypeId;
	private String attendanceTypeName;
	private String unit;
	private Integer attendanceGroupId;
	private Integer vacationTypeId;
	private String usage; // CHAR(1)

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

}
