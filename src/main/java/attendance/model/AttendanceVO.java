package attendance.model;

import java.sql.Date;

public class AttendanceVO {
	private int attendanceId;
	private int employeeId;
	private Date inputDate;
	private int attendanceTypeId;
	private String attendanceTypeName;
	private Date startDate;
	private Date endDate;
	private double attendanceDays;
	private int amount;
	private String summary;

	public AttendanceVO() {
	}

	public int getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(int attendanceId) {
		this.attendanceId = attendanceId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public int getAttendanceTypeId() {
		return attendanceTypeId;
	}

	public void setAttendanceTypeId(int attendanceTypeId) {
		this.attendanceTypeId = attendanceTypeId;
	}

	public String getAttendanceTypeName() {
		return attendanceTypeName;
	}

	public void setAttendanceTypeName(String attendanceTypeName) {
		this.attendanceTypeName = attendanceTypeName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getAttendanceDays() {
		return attendanceDays;
	}

	public void setAttendanceDays(double attendanceDays) {
		this.attendanceDays = attendanceDays;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}