package vacation.model;

public class VacationDetail {
	// 상단 타이틀용 정보
	private String departmentName;
	private String koreanName;

	// 테이블 행 데이터
	private int seq;
	private String regDate; // 입력일자
	private String vacationType; // 휴가항목
	private String attendance; // 근태항목
	private String period; // 기간
	private double days; // 일수
	private String remarks; // 적요

	// 하단 요약용 데이터
	private double totalDays = 19; // 전체 휴가일수 (기본 19일)
	private double usedDays; // 총 사용일수
	private double remainingDays; // 잔여일수

	// --- Getter & Setter ---
	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getKoreanName() {
		return koreanName;
	}

	public void setKoreanName(String koreanName) {
		this.koreanName = koreanName;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getVacationType() {
		return vacationType;
	}

	public void setVacationType(String vacationType) {
		this.vacationType = vacationType;
	}

	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public double getDays() {
		return days;
	}

	public void setDays(double days) {
		this.days = days;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
}