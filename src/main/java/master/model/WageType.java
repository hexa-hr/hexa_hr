package master.model;

// 급여 종류
public class WageType {
	private Integer wageTypeId;
	private String wageTypeName;
	private String numberCut;
	private String attendanceOrLumpsum;
	private String attendanceOrLumpsumContent;
	private String usage; // CHAR(1)
	private String itemType;
	private String taxableYn;
	private Long taxFreeLimit;

	public WageType(Integer wageTypeId, String wageTypeName, String numberCut, String attendanceOrLumpsum,
			String attendanceOrLumpsumContent, String usage, String itemType, String taxableYn, Long taxFreeLimit) {
		this.wageTypeId = wageTypeId;
		this.wageTypeName = wageTypeName;
		this.numberCut = numberCut;
		this.attendanceOrLumpsum = attendanceOrLumpsum;
		this.attendanceOrLumpsumContent = attendanceOrLumpsumContent;
		this.usage = usage;
		this.itemType = itemType;
		this.taxableYn = taxableYn;
		this.taxFreeLimit = taxFreeLimit;
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

	public String getItemType() {
		return itemType;
	}

	public String getTaxableYn() {
		return taxableYn;
	}

	public Long getTaxFreeLimit() {
		return taxFreeLimit;
	}

}