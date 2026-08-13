package employee.command;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Employee;
import employee.service.EmployeeRegistrationService;
import mvc.command.CommandHandler;

public class EmployeeRegistrationFormHandler implements CommandHandler {

	private EmployeeRegistrationService registrationService = new EmployeeRegistrationService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("GET")) {
			return "/WEB-INF/view/employeeRegistration.jsp";
		}

		else if (request.getMethod().equalsIgnoreCase("POST")) {

			request.setCharacterEncoding("utf-8");

			// 1. 화면(JSP)에서 넘어온 데이터 꺼내기
			String employmentType = request.getParameter("empType");
			String koreanName = request.getParameter("empName");
			String englishName = request.getParameter("empEngName");
			String joinDateString = request.getParameter("joinDate");

			// 2. 문자열 날짜를 java.util.Date 형식으로 변환하기
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date hireDate = null;
			if (joinDateString != null && !joinDateString.trim().isEmpty()) {
				hireDate = simpleDateFormat.parse(joinDateString);
			}

			// 3. Employee 생성자 순서에 맞춰 새로운 Employee 객체 생성하기
			Employee newEmployee = new Employee(
				null, // employeeId
				null, // accountId
				null, // companyId
				null, // personId
				employmentType, // employmentType
				koreanName, // koreanName
				englishName, // englishName
				hireDate, // hireDate
				null, // resignationDate
				null, // departmentId
				null, // positionId
				null, // foreignOrDomestic
				null, // residentNumber1
				null, // residentNumber2
				null, // address
				null, // telPhone
				null, // mobile
				null, // email
				null, // sns
				null, // otherDetails
				"재직" // status
			);

			// 4. 서비스 호출하여 데이터베이스에 저장하기
			try {
				registrationService.register(newEmployee);
				request.setAttribute("message", "사원 정보가 성공적으로 등록되었습니다!");
				return "/WEB-INF/view/employeeRegistration.jsp";

			} catch (Exception exception) {
				request.setAttribute("error", "저장 중 오류가 발생했습니다.");
				return "/WEB-INF/view/employeeRegistration.jsp";
			}
		}
		return null;
	}

}