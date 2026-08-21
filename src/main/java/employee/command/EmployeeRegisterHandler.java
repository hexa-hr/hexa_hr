package employee.command;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Degree;
import employee.model.Dependents;
import employee.model.Employee;
import employee.model.EmployeeSalaryAccount;
import employee.model.Insurance;
import employee.service.EmployeeRegisterService;
import mvc.command.CommandHandler;

public class EmployeeRegisterHandler implements CommandHandler {

	private EmployeeRegisterService registerService = new EmployeeRegisterService();

	// 👇 여기서부터가 process 메서드 시작이야!
	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("GET")) {
			return "/WEB-INF/view/employee/employeeRegister.jsp";
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

			// --- 1. 기본 정보 ---
			Integer accountId = null;
			Integer companyId = parseInt(request.getParameter("companyId"));
			Integer personId = parseInt(request.getParameter("personId"));
			Integer departmentId = parseInt(request.getParameter("departmentId"));
			Integer positionId = parseInt(request.getParameter("positionId"));

			String employmentType = request.getParameter("employmentType");
			String koreanName = request.getParameter("koreanName");
			String englishName = request.getParameter("englishName");
			Date hireDate = parseDate(request.getParameter("hireDate"));
			Date resignationDate = parseDate(request.getParameter("resignationDate"));
			String foreignOrDomestic = request.getParameter("foreignOrDomestic");
			String residentNumber1 = request.getParameter("residentNumber1");
			String residentNumber2 = request.getParameter("residentNumber2");
			String address = request.getParameter("address");
			String telPhone = request.getParameter("telPhone");
			String mobile = request.getParameter("mobile");
			String email = request.getParameter("email");
			String sns = request.getParameter("sns");
			String otherDetails = request.getParameter("otherDetails");
			String status = request.getParameter("status");

			Employee employee = new Employee(
				null, accountId, companyId, personId, employmentType,
				koreanName, englishName, hireDate, resignationDate, departmentId,
				positionId, foreignOrDomestic, residentNumber1, residentNumber2, address,
				telPhone, mobile, email, sns, otherDetails, status);

			// --- 2. 급여 계좌 ---
			String bankName = request.getParameter("bankName");
			String accountNumber = request.getParameter("accountNumber");
			String depositStocks = request.getParameter("depositStocks");
			Integer salaryCalculation1 = parseInt(request.getParameter("salaryCalculation1"));
			Integer salaryCalculation2 = parseInt(request.getParameter("salaryCalculation2"));
			Integer salaryPaymentDate = parseInt(request.getParameter("salaryPaymentDate"));
			String calc1MonthType = request.getParameter("calc1MonthType");
			String calc2MonthType = request.getParameter("calc2MonthType");
			String paymentMonthType = request.getParameter("paymentMonthType");

			EmployeeSalaryAccount account = new EmployeeSalaryAccount(
				null, companyId, bankName, accountNumber, depositStocks,
				salaryCalculation1, salaryCalculation2, salaryPaymentDate,
				calc1MonthType, calc2MonthType, paymentMonthType);

			// --- 3. 가족 사항 리스트 ---
			String[] relationships = request.getParameterValues("relationship");
			String[] parentsNames = request.getParameterValues("parentsName");
			String[] foreignOrDomestic1s = request.getParameterValues("foreignOrDomestic1");
			String[] parentsNumber1s = request.getParameterValues("parentsNumber1");
			String[] parentsNumber2s = request.getParameterValues("parentsNumber2");

			List<Dependents> dependentsList = new ArrayList<>();
			if (relationships != null) {
				for (int i = 0; i < relationships.length; i++) {
					if (relationships[i] != null && !relationships[i].trim().isEmpty() &&
						parentsNames[i] != null && !parentsNames[i].trim().isEmpty()) {

						Dependents dep = new Dependents(
							null, null, relationships[i], parentsNames[i],
							foreignOrDomestic1s[i], parentsNumber1s[i], parentsNumber2s[i]);
						dependentsList.add(dep);
					}
				}
			}

			// --- 4. 학력 사항 리스트 ---
			String[] graduates = request.getParameterValues("graduate");
			String[] admissionDates = request.getParameterValues("admissionDate");
			String[] graduationDates = request.getParameterValues("graduationDate");
			String[] schoolNames = request.getParameterValues("schoolName");
			String[] majors = request.getParameterValues("major");
			String[] completions = request.getParameterValues("completion");

			List<Degree> degreeList = new ArrayList<>();
			if (graduates != null) {
				for (int i = 0; i < graduates.length; i++) {
					// 학교명이 입력된 경우에만 유효한 데이터로 판단
					if (schoolNames[i] != null && !schoolNames[i].trim().isEmpty()) {
						Degree deg = new Degree(
							null, null, graduates[i],
							parseDate(admissionDates[i]), parseDate(graduationDates[i]),
							schoolNames[i], majors[i], completions[i]);
						degreeList.add(deg);
					}
				}
			}

			// --- 5. 보험 정보 ---
			String insuranceAgency = request.getParameter("insuranceAgency");
			String insuranceNumber = request.getParameter("insuranceNumber");
			String insuranceAmountStr = request.getParameter("insuranceAmount");
			Long insuranceAmount = (insuranceAmountStr != null && !insuranceAmountStr.trim().isEmpty())
				? Long.parseLong(insuranceAmountStr.trim()) : null;
			Date insuranceStartDate = parseDate(request.getParameter("insuranceStartDate"));
			Date insuranceEndDate = parseDate(request.getParameter("insuranceEndDate"));
			String remarks4 = request.getParameter("remarks4");

			Insurance insurance = new Insurance(
				null, null, insuranceAgency, insuranceNumber, insuranceAmount,
				insuranceStartDate, insuranceEndDate, remarks4);

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();

			// 🌟 여기서부터 기존 try ~ catch 전체를 덮어씌워 주세요
			try {
				// 1. 서비스 호출 후 사원번호(newEmpId) 받아오기
				Integer newEmpId = registerService.register(employee, account, dependentsList, degreeList, insurance);

				// 2. 🌟 성공 팝업 및 이동 코드는 반드시 try 안쪽(newEmpId 밑)에 있어야 해!
				out.println("<script>");
				out.println("alert('사원정보 1이 성공적으로 저장되었습니다.');");
				out.println("parent.document.getElementById('hiddenEmpId').value = '" + newEmpId + "';");
				out.println("</script>");
				out.flush();
				return null;

			} catch (Exception e) {
				// 3. 🌟 여기는 등록이 실패(에러)했을 때만 실행되는 곳이야!
				e.printStackTrace();
				out.println("<script>");
				out.println("alert('사원 등록 실패: " + e.getMessage() + "');");
				out.println("history.back();");
				out.println("</script>");
				out.flush();
				return null;
			}
		}

		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	} // 👆 여기까지가 process 메서드의 끝이야!

	// (아래는 글자를 숫자로, 날짜로 바꿔주는 보조 도구들)
	private Integer parseInt(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private Date parseDate(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(val.trim());
		} catch (Exception e) {
			return null;
		}
	}
}