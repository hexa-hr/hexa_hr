package master.model;

// 급여 종류
public class WageType {
	private Integer wageTypeId;
	private String wageTypeName;
	private String numberCut;
	private String attendanceOrLumpsum;
	private String attendanceOrLumpsumContent;
	private String usage; // CHAR(1)

	public WageType(Integer wageTypeId, String wageTypeName, String numberCut, String attendanceOrLumpsum,
		String attendanceOrLumpsumContent, String usage) {
		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.numberCut = numberCut;
		this.attendanceOrLumpsum = attendanceOrLumpsum;
		this.attendanceOrLumpsumContent = attendanceOrLumpsumContent;
		this.usage = usage;
	}

	public Integer getWageTypeId() {
		return wageTypeId;
	}

	public String getWageTypeName() {
		return wageTypeName;
	}

	public String getNumberCut() {
		return numberCut;
	}

	public String getAttendanceOrLumpsum() {
		return attendanceOrLumpsum;
	}

	public String getAttendanceOrLumpsumContent() {
		return attendanceOrLumpsumContent;
	}

	public String getUsage() {
		return usage;
	}

}
