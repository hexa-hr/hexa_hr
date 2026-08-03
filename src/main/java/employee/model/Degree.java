package employee.model;

import java.util.Date;

// 학력
public class Degree {
	private Integer degreeId;
	private Integer employeeId;
	private String graduate;
	private Date admissionDate;
	private Date graduationDate;
	private String schoolName;
	private String major;
	private String completion;

	public Degree(Integer degreeId, Integer employeeId, String graduate, Date admissionDate, Date graduationDate,
		String schoolName, String major, String completion) {
		this.degreeId = degreeId;
		this.employeeId = employeeId;
		this.graduate = graduate;
		this.admissionDate = admissionDate;
		this.graduationDate = graduationDate;
		this.schoolName = schoolName;
		this.major = major;
		this.completion = completion;
	}

	public Integer getDegreeId() {
		return degreeId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getGraduate() {
		return graduate;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public Date getGraduationDate() {
		return graduationDate;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public String getMajor() {
		return major;
	}

	public String getCompletion() {
		return completion;
	}

}
