package employee.command;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Career;
import employee.model.Degree;
import employee.model.Dependents;
import employee.model.Employee;
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

			// 🌟 1. 서버사이드 필수값 검증 (4대 보험 포함)
			String koreanName = request.getParameter("koreanName");
			String employmentType = request.getParameter("employmentType");
			String hireDateStr = request.getParameter("hireDate");
			String basicPayStr = request.getParameter("basicPay");
			String[] insuranceAgencies = request.getParameterValues("insuranceAgency");

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();

			if (koreanName == null || koreanName.trim().isEmpty() ||
				employmentType == null || employmentType.trim().isEmpty() ||
				hireDateStr == null || hireDateStr.trim().isEmpty() ||
				basicPayStr == null || basicPayStr.trim().isEmpty()) {

				out.println("<script>parent.alert('エラー：必須項目（氏名、雇用形態、入社日、基本給）が入力されていません。');</script>");
				out.flush();
				return null;
			}

			// 🌟 4대 보험 필수 검증 추가
			if (insuranceAgencies == null || insuranceAgencies.length == 0) {
				out.println("<script>parent.alert('エラー：必須項目（4大保険）が1つ以上選択されていません。');</script>");
				out.flush();
				return null;
			}

			// 허용된 고용형태인지 검증
			List<String> validEmpTypes = Arrays.asList("정규직", "계약직", "파견직", "위촉직", "임시직", "일용직");
			if (!validEmpTypes.contains(employmentType)) {
				out.println("<script>parent.alert('エラー：無効な雇用形態です。正しい雇用形態を選択してください。');</script>");
				out.flush();
				return null;
			}

			Integer employeeId = null;
			String empIdStr = request.getParameter("employeeId");
			if (empIdStr != null && !empIdStr.trim().isEmpty()) {
				employeeId = Integer.parseInt(empIdStr);
			}

			Integer companyId = parseInt(request.getParameter("companyId"));
			Integer personId = parseInt(request.getParameter("personId"));
			Integer departmentId = parseInt(request.getParameter("departmentId"));
			Integer positionId = parseInt(request.getParameter("positionId"));

			Employee employee = new Employee(
				employeeId, null, companyId, personId, employmentType,
				koreanName, request.getParameter("englishName"),
				parseDate(hireDateStr), parseDate(request.getParameter("resignationDate")),
				departmentId, positionId, request.getParameter("foreignOrDomestic"),
				request.getParameter("residentNumber1"), request.getParameter("residentNumber2"),
				request.getParameter("address"), request.getParameter("telPhone"), request.getParameter("mobile"),
				request.getParameter("email"), request.getParameter("sns"), request.getParameter("otherDetails"),
				null, parseLong(basicPayStr));

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

			String insuranceNumber = request.getParameter("insuranceNumber");
			String insuranceAmountStr = request.getParameter("insuranceAmount");
			Long insuranceAmount = (insuranceAmountStr != null && !insuranceAmountStr.trim().isEmpty())
				? Long.parseLong(insuranceAmountStr.trim()) : null;
			Date insuranceStartDate = null;
			Date insuranceEndDate = null;
			String remarks4 = request.getParameter("remarks4");

			List<String> validInsurances = Arrays.asList("국민연금", "건강보험", "장기요양보험", "고용보험");

			List<Insurance> insuranceList = new ArrayList<>();
			if (insuranceAgencies != null) {
				for (String agency : insuranceAgencies) {
					if (agency != null && validInsurances.contains(agency.trim())) {
						insuranceList.add(new Insurance(null, null, agency.trim(), insuranceNumber, insuranceAmount,
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

			try {
				Integer newEmpId = registerService.register(employee, dependentsList, degreeList,
					insuranceList, careerList, militaryList);

				// 🌟 변경 포인트: 자동 페이지 이동 제거, 알림창만 띄움
				out.println(
					"<script>parent.alert('社員情報が登録されました。'); parent.document.getElementById('hiddenEmpId').value = '"
						+ newEmpId + "';</script>");
				out.flush();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				String errMsg = (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
				if (errMsg != null)
					errMsg = errMsg.replace("'", "\\'").replace("\n", " ");
				out.println("<script>parent.alert('登録失敗: " + errMsg + "');</script>");
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