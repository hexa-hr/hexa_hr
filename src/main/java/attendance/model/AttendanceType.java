package attendance.model;

public class AttendanceType {

	private java.util.Date applyPeriod1;
	private java.util.Date applyPeriod2;

	private int attendanceTypeId; // 근태항목 ID (PK)
	private String name; // 근태항목명
	private String unit; // 단위 (일, 시간 등)
	private int attendanceGroupId; // 근태그룹 ID (FK)
	private int vacationTypeId; // 휴가공제 ID (FK)
	private String usage; // 사용여부 (Y/N)

	// 화면 출력(조인 결과)을 위한 추가 필드
	private String attendanceGroupName; // 근태그룹명
	private String vacationTypeName; // 휴가공제명

	// 기본 생성자
	public AttendanceType() {
	}

	// Getter & Setter
	public int getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public void setAttendanceTypeId(int attendanceTypeId) {
		this.attendanceTypeId = attendanceTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getAttendanceGroupId() {
		return attendanceGroupId;
	}

	public void setAttendanceGroupId(int attendanceGroupId) {
		this.attendanceGroupId = attendanceGroupId;
	}

	public int getVacationTypeId() {
		return vacationTypeId;
	}

	public void setVacationTypeId(int vacationTypeId) {
		this.vacationTypeId = vacationTypeId;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getAttendanceGroupName() {
		return attendanceGroupName;
	}

	public void setAttendanceGroupName(String attendanceGroupName) {
		this.attendanceGroupName = attendanceGroupName;
	}

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