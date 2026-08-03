package employee.model;

import java.util.Date;

// 인사 발령
public class Appointment {
	private Integer appointmentId;
	private Integer employeeId;
	private String appointmentType;
	private Date appointmentDate;
	private Integer departmentId;
	private Integer positionId;
	private String positionType;
	private String remarks3;

	public Appointment(Integer appointmentId, Integer employeeId, String appointmentType, Date appointmentDate,
		Integer departmentId, Integer positionId, String positionType, String remarks3) {
		this.appointmentId = appointmentId;
		this.employeeId = employeeId;
		this.appointmentType = appointmentType;
		this.appointmentDate = appointmentDate;
		this.departmentId = departmentId;
		this.positionId = positionId;
		this.positionType = positionType;
		this.remarks3 = remarks3;
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public String getPositionType() {
		return positionType;
	}

	public String getRemarks3() {
		return remarks3;
	}

}
