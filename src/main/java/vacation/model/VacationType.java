package vacation.model;

import java.util.Date;

// 휴가 항목/종류
public class VacationType {
	private Integer vacationTypeId;
	private String vacationTypeName;
	private Date applyPeriod1;
	private Date applyPeriod2;
	private String usage; // CHAR(1)

	// --- 가상 컬럼 및 사원별 휴가 현황 조회를 위한 필드들 ---
	private double totalDays; // 전체 일수 (기본값 필요시 생성자에서 지정 가능)
	private double usedDays; // 사용 일수
	private double remainingDays; // 잔여 일수

	private int employeeId;
	private String employeeNumber;
	private String koreanName;
	private String departmentName;
	private String positionName;
	private String employmentType;
	private double vacationValue;

	// 👉 [필수 추가] 기본 생성자
	public VacationType() {
		this.totalDays = 15; // 기본 전체 일수 초기화 필요시 사용
	}

	// 기존 파라미터 생성자
	public VacationType(Integer vacationTypeId, String vacationTypeName, Date applyPeriod1, Date applyPeriod2,
		String usage) {
		this.vacationTypeId = vacationTypeId;
		this.vacationTypeName = vacationTypeName;
		this.applyPeriod1 = applyPeriod1;
		this.applyPeriod2 = applyPeriod2;
		this.usage = usage;
		this.totalDays = 15;
	}

	// --- Getter & Setter ---
	public Integer getVacationTypeId() {
		return vacationTypeId;
	}

	public void setVacationTypeId(Integer vacationTypeId) {
		this.vacationTypeId = vacationTypeId;
	}

	public String getVacationTypeName() {
		return vacationTypeName;
	}

	public void setVacationTypeName(String vacationTypeName) {
		this.vacationTypeName = vacationTypeName;
	}

	public Date getApplyPeriod1() {
		return applyPeriod1;
	}

	public void setApplyPeriod1(Date applyPeriod1) {
		this.applyPeriod1 = applyPeriod1;
	}

	public Date getApplyPeriod2() {
		return applyPeriod2;
	}

	public void setApplyPeriod2(Date applyPeriod2) {
		this.applyPeriod2 = applyPeriod2;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public double getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(double totalDays) {
		this.totalDays = totalDays;
	}

	public double getUsedDays() {
		return usedDays;
	}

	public void setUsedDays(double usedDays) {
		this.usedDays = usedDays;
	}

	public double getRemainingDays() {
		return remainingDays;
	}

	public void setRemainingDays(double remainingDays) {
		this.remainingDays = remainingDays;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public void setKoreanName(String koreanName) {
		this.koreanName = koreanName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public double getVacationValue() {
		return vacationValue;
	}

	public void setVacationValue(double vacationValue) {
		this.vacationValue = vacationValue;
	}
}