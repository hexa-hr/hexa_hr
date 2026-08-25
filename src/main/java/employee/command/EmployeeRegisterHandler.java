package employee.command;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Career;
import employee.model.Degree;
import employee.model.Dependents;
import employee.model.Employee;
import employee.model.EmployeeSalaryAccount;
import employee.model.Insurance;
import employee.model.MilitaryService;
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
				request.setAttribute("depList", registerService.getDependents(employeeId));
				request.setAttribute("account", registerService.getAccount(employeeId));
				request.setAttribute("degList", registerService.getDegrees(employeeId));
				request.setAttribute("insList", registerService.getInsurances(employeeId));
				request.setAttribute("careerList", registerService.getCareers(employeeId));
				request.setAttribute("milList", registerService.getMilitaryServices(employeeId));
			}

			request.setAttribute("deptList", registerService.getDepartments());
			request.setAttribute("posList", registerService.getPositions());

			return "/WEB-INF/view/employee/employeeRegister.jsp";
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

			// 🌟 1. JSP에서 name="employeeId" 로 보낸 사원번호 받기!
			Integer employeeId = null;
			String empIdStr = request.getParameter("employeeId");
			if (empIdStr != null && !empIdStr.trim().isEmpty()) {
				employeeId = Integer.parseInt(empIdStr);
			}

			Integer companyId = parseInt(request.getParameter("companyId"));
			Integer personId = parseInt(request.getParameter("personId"));
			Integer departmentId = parseInt(request.getParameter("departmentId"));
			Integer positionId = parseInt(request.getParameter("positionId"));

			// 🌟 2. Employee 객체 생성 시 첫 번째 자리에 employeeId 삽입
			Employee employee = new Employee(
				employeeId, null, companyId, personId, request.getParameter("employmentType"),
				request.getParameter("koreanName"), request.getParameter("englishName"),
				parseDate(request.getParameter("hireDate")), parseDate(request.getParameter("resignationDate")),
				departmentId, positionId, request.getParameter("foreignOrDomestic"),
				request.getParameter("residentNumber1"), request.getParameter("residentNumber2"),
				request.getParameter("address"), request.getParameter("telPhone"), request.getParameter("mobile"),
				request.getParameter("email"), request.getParameter("sns"), request.getParameter("otherDetails"),
				request.getParameter("status"), parseLong(request.getParameter("basicPay")));

			EmployeeSalaryAccount account = new EmployeeSalaryAccount(
				null, companyId, request.getParameter("bankName"), request.getParameter("accountNumber"),
				request.getParameter("depositStocks"), 0, 0, 0, "", "", "");

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
						dependentsList.add(new Dependents(null, null, relationships[i], parentsNames[i],
							foreignOrDomestic1s[i], parentsNumber1s[i], parentsNumber2s[i]));
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
						degreeList.add(new Degree(null, null, graduates[i], parseDate(admissionDates[i]),
							parseDate(graduationDates[i]), schoolNames[i], majors[i], completions[i]));
					}
				}
			}

			String[] insuranceAgencies = request.getParameterValues("insuranceAgency");
			String insuranceNumber = request.getParameter("insuranceNumber");
			String insuranceAmountStr = request.getParameter("insuranceAmount");
			Long insuranceAmount = (insuranceAmountStr != null && !insuranceAmountStr.trim().isEmpty())
				? Long.parseLong(insuranceAmountStr.trim()) : null;
			Date insuranceStartDate = parseDate(request.getParameter("insuranceStartDate"));
			Date insuranceEndDate = parseDate(request.getParameter("insuranceEndDate"));
			String remarks4 = request.getParameter("remarks4");

			List<Insurance> insuranceList = new ArrayList<>();
			if (insuranceAgencies != null) {
				for (String agency : insuranceAgencies) {
					if (agency != null && !agency.trim().isEmpty()) {
						insuranceList.add(new Insurance(null, null, agency, insuranceNumber, insuranceAmount,
							insuranceStartDate, insuranceEndDate, remarks4));
					}
				}
			}

			String[] companyNames = request.getParameterValues("companyName");
			String[] startDates = request.getParameterValues("startDate");
			String[] endDates = request.getParameterValues("endDate");
			String[] finalPositions = request.getParameterValues("finalPosition");
			String[] responsibilities = request.getParameterValues("responsibilities");

			List<Career> careerList = new ArrayList<>();
			if (companyNames != null) {
				for (int i = 0; i < companyNames.length; i++) {
					if (companyNames[i] != null && !companyNames[i].trim().isEmpty()) {
						careerList.add(new Career(
							null, null, companyNames[i], parseDate(safeGet(startDates, i)),
							parseDate(safeGet(endDates, i)), null, safeGet(finalPositions, i),
							safeGet(responsibilities, i), null));
					}
				}
			}

			String[] serviceTypes = request.getParameterValues("serviceType");
			String[] branches = request.getParameterValues("branch");
			String[] servicePeriod1s = request.getParameterValues("servicePeriod1");
			String[] servicePeriod2s = request.getParameterValues("servicePeriod2");
			String[] finalRanks = request.getParameterValues("finalRank");
			String[] department1s = request.getParameterValues("department1");
			String[] exemptionReasons = request.getParameterValues("exemptionReason");

			List<MilitaryService> militaryList = new ArrayList<>();
			if (serviceTypes != null) {
				for (int i = 0; i < serviceTypes.length; i++) {
					if (serviceTypes[i] != null && !serviceTypes[i].trim().isEmpty()) {
						militaryList.add(new MilitaryService(
							null, null, serviceTypes[i], safeGet(branches, i),
							parseDate(safeGet(servicePeriod1s, i)), parseDate(safeGet(servicePeriod2s, i)),
							safeGet(finalRanks, i), safeGet(department1s, i), safeGet(exemptionReasons, i)));
					}
				}
			}

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();

			try {
				Integer newEmpId = registerService.register(employee, account, dependentsList, degreeList,
					insuranceList, careerList, militaryList);
				out.println(
					"<script>parent.alert('사원정보 1이 성공적으로 저장되었습니다.'); parent.document.getElementById('hiddenEmpId').value = '"
						+ newEmpId + "';</script>");
				out.flush();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				String errMsg = (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
				if (errMsg != null)
					errMsg = errMsg.replace("'", "\\'").replace("\n", " ");
				out.println("<script>parent.alert('사원 등록 실패: " + errMsg + "');</script>");
				out.flush();
				return null;
			}
		}
		return null;
	}

	private String safeGet(String[] arr, int index) {
		if (arr != null && arr.length > index)
			return arr[index];
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
			return new SimpleDateFormat("yyyy-MM-dd").parse(val.trim());
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