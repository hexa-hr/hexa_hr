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

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("GET")) {
			String empIdStr = request.getParameter("employeeId");
			if (empIdStr != null && !empIdStr.trim().isEmpty()) {
				int employeeId = parseInt(empIdStr);
				Employee emp = registerService.getEmployee(employeeId);
				request.setAttribute("emp", emp);
			}

			request.setAttribute("deptList", registerService.getDepartments());
			request.setAttribute("posList", registerService.getPositions());

			return "/WEB-INF/view/employee/employeeRegister.jsp";
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

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
			Long basicPay = parseLong(request.getParameter("basicPay"));

			Employee employee = new Employee(
				null, accountId, companyId, personId, employmentType,
				koreanName, englishName, hireDate, resignationDate, departmentId,
				positionId, foreignOrDomestic, residentNumber1, residentNumber2, address,
				telPhone, mobile, email, sns, otherDetails, status, basicPay);

			String bankName = request.getParameter("bankName");
			String accountNumber = request.getParameter("accountNumber");
			String depositStocks = request.getParameter("depositStocks");

			// 🌟 핵심 해결 포인트!
			// DAO에서 NullPointerException이 나지 않도록, 삭제된 급여날짜 항목들에 더미값(0, "")을 줍니다.
			EmployeeSalaryAccount account = new EmployeeSalaryAccount(
				null, companyId, bankName, accountNumber, depositStocks,
				0, 0, 0, "", "", "");

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

			String[] graduates = request.getParameterValues("graduate");
			String[] admissionDates = request.getParameterValues("admissionDate");
			String[] graduationDates = request.getParameterValues("graduationDate");
			String[] schoolNames = request.getParameterValues("schoolName");
			String[] majors = request.getParameterValues("major");
			String[] completions = request.getParameterValues("completion");

			List<Degree> degreeList = new ArrayList<>();
			if (graduates != null) {
				for (int i = 0; i < graduates.length; i++) {
					if (schoolNames[i] != null && !schoolNames[i].trim().isEmpty()) {
						Degree deg = new Degree(
							null, null, graduates[i],
							parseDate(admissionDates[i]), parseDate(graduationDates[i]),
							schoolNames[i], majors[i], completions[i]);
						degreeList.add(deg);
					}
				}
			}

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

			try {
				Integer newEmpId = registerService.register(employee, account, dependentsList, degreeList, insurance);

				out.println("<script>");
				out.println("parent.alert('사원정보 1이 성공적으로 저장되었습니다.');");
				out.println("parent.document.getElementById('hiddenEmpId').value = '" + newEmpId + "';");
				out.println("</script>");
				out.flush();
				return null;

			} catch (Exception e) {
				e.printStackTrace();
				out.println("<script>");
				String errMsg = e.getMessage() != null ? e.getMessage().replace("'", "\\'") : "알 수 없는 오류";
				out.println("parent.alert('사원 등록 실패: 입력하신 정보를 다시 확인해주세요.\\n(상세 원인: " + errMsg + ")');");
				out.println("</script>");
				out.flush();
				return null;
			}
		}

		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}

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

	private Long parseLong(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Long.parseLong(val.trim());
		} catch (Exception e) {
			return null;
		}
	}
}