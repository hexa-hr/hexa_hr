package master.model;

// 근태 그룹
public class AttendanceGroup {
	private Integer attendanceGroupId;
	private String attendanceGroupName;

	public AttendanceGroup(Integer attendanceGroupId, String attendanceGroupName) {
		this.attendanceGroupId = attendanceGroupId;
		this.attendanceGroupName = attendanceGroupName;
	}

	public Integer getAttendanceGroupId() {
		return attendanceGroupId;
	}

	public String getAttendanceGroupName() {
		return attendanceGroupName;
	}

}
