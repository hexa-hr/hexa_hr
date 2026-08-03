package employee.model;

import java.util.Date;

// 언어 능력
public class LanguageAbility {
	private Integer languageAbilityId;
	private Integer employeeId;
	private String language;
	private String testName;
	private Integer officialScore;
	private Date acquisitionDate1;
	private String readingAbility;
	private String writingAbility;
	private String speakingAbility;

	public LanguageAbility(Integer languageAbilityId, Integer employeeId, String language, String testName,
		Integer officialScore, Date acquisitionDate1, String readingAbility, String writingAbility,
		String speakingAbility) {
		this.languageAbilityId = languageAbilityId;
		this.employeeId = employeeId;
		this.language = language;
		this.testName = testName;
		this.officialScore = officialScore;
		this.acquisitionDate1 = acquisitionDate1;
		this.readingAbility = readingAbility;
		this.writingAbility = writingAbility;
		this.speakingAbility = speakingAbility;
	}

	public Integer getLanguageAbilityId() {
		return languageAbilityId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public String getLanguage() {
		return language;
	}

	public String getTestName() {
		return testName;
	}

	public Integer getOfficialScore() {
		return officialScore;
	}

	public Date getAcquisitionDate1() {
		return acquisitionDate1;
	}

	public String getReadingAbility() {
		return readingAbility;
	}

	public String getWritingAbility() {
		return writingAbility;
	}

	public String getSpeakingAbility() {
		return speakingAbility;
	}

}
