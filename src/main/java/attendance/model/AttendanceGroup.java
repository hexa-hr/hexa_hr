package attendance.model;

public class AttendanceGroup {

	private int attendanceGroupId;
	private String groupName;

	public AttendanceGroup() {}

	public AttendanceGroup(int attendanceGroupId, String groupName) {
		this.attendanceGroupId = attendanceGroupId;
		this.groupName = groupName;
	}

	public int getAttendanceGroupId() {
		return attendanceGroupId;
	}

	public void setAttendanceGroupId(int attendanceGroupId) {
		this.attendanceGroupId = attendanceGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	// JSP ${group.attendanceGroupName} 호환용 Getter
	public String getAttendanceGroupName() {
		return groupName;
	}

	// JSP 호환용 Setter
	public void setAttendanceGroupName(String attendanceGroupName) {
		this.groupName = attendanceGroupName;
	}
}