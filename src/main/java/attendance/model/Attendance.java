package attendance.model;

import java.util.Date;

// 근태 및 일용직 기록
public class Attendance {
	private Integer attendanceId;
	private Integer employeeId;
	private Date inputDate;
	private Integer attendanceTypeId;
	private Integer fieldOrProjectId;
	private Date startDate;
	private Date endDate;
	private Integer attendanceDays;
	private Long amount;
	private String summary;

	public Attendance(Integer attendanceId, Integer employeeId, Date inputDate, Integer attendanceTypeId,
		Integer fieldOrProjectId, Date startDate, Date endDate, Integer attendanceDays, Long amount, String summary) {
		this.attendanceId = attendanceId;
		this.employeeId = employeeId;
		this.inputDate = inputDate;
		this.attendanceTypeId = attendanceTypeId;
		this.fieldOrProjectId = fieldOrProjectId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.attendanceDays = attendanceDays;
		this.amount = amount;
		this.summary = summary;
	}

	public Integer getAttendanceId() {
		return attendanceId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public Integer getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public Integer getFieldOrProjectId() {
		return fieldOrProjectId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Integer getAttendanceDays() {
		return attendanceDays;
	}

	public Long getAmount() {
		return amount;
	}

	public String getSummary() {
		return summary;
	}

}
