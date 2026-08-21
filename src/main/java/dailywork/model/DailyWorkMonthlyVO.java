package dailywork.model;

import java.util.ArrayList;
import java.util.List;

public class DailyWorkMonthlyVO {
	private String empNo; // 사원번호
	private String empName; // 성명
	private String department; // 부서
	private List<Integer> workedDays; // 일한 날짜 리스트 (예: [2, 18, 23])
	private int totalDays; // 합계(총 근무일수)
	private long incomeTax; // 소득세
	private long localTax; // 지방소득세
	private long totalActualPayment; // 실지급합계

	// 생성자
	public DailyWorkMonthlyVO(String empNo, String empName, String department, String workDaysStr, int totalDays,
			long incomeTax, long localTax, long totalActualPayment) {
		this.empNo = empNo;
		this.empName = empName;
		this.department = department;
		this.totalDays = totalDays;
		this.incomeTax = incomeTax;
		this.localTax = localTax;
		this.totalActualPayment = totalActualPayment;

		// "2,18,25" 와 같은 문자열을 분리하여 정수 리스트로 변환
		this.workedDays = new ArrayList<>();
		if (workDaysStr != null && !workDaysStr.isEmpty()) {
			String[] days = workDaysStr.split(",");
			for (String d : days) {
				this.workedDays.add(Integer.parseInt(d.trim()));
			}
		}
	}

	// Getter 및 Setter 생략 (JSP에서 사용하기 위해 반드시 Getter를 만들어주세요)
	public String getEmpNo() {
		return empNo;
	}

	public String getEmpName() {
		return empName;
	}

	public String getDepartment() {
		return department;
	}

	public List<Integer> getWorkedDays() {
		return workedDays;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public long getIncomeTax() {
		return incomeTax;
	}

	public long getLocalTax() {
		return localTax;
	}

	public long getTotalActualPayment() {
		return totalActualPayment;
	}
}