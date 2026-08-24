package employee.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.service.EmployeeRegister2Service;
import mvc.command.CommandHandler;

public class EmployeeRegister2Handler implements CommandHandler {

	private EmployeeRegister2Service register2Service = new EmployeeRegister2Service();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("GET")) {
			String empIdStr = request.getParameter("employeeId");
			String tab = request.getParameter("tab");
			if (tab == null || tab.trim().isEmpty()) {
				tab = "cert";
			}

			if (empIdStr != null && !empIdStr.trim().isEmpty()) {
				int employeeId = Integer.parseInt(empIdStr);
				request.setAttribute("employeeId", employeeId);
				request.setAttribute("tab", tab);

				// 🌟 2페이지 필수 조회 데이터 전달
				request.setAttribute("certList", register2Service.getCertifications(employeeId));
				request.setAttribute("langList", register2Service.getLanguageAbilities(employeeId));
				request.setAttribute("trainingList", register2Service.getTrainings(employeeId));
				request.setAttribute("rewardList", register2Service.getRewardPenalties(employeeId));
				request.setAttribute("apptList", register2Service.getAppointments(employeeId));
				request.setAttribute("refList", register2Service.getReferrers(employeeId));
				request.setAttribute("guaList", register2Service.getGuarantors(employeeId));
				request.setAttribute("retirement", register2Service.getRetirement(employeeId));
			}

			// 🌟 발령 테이블 부서/직위 드롭다운용 리스트 전달
			request.setAttribute("deptList", register2Service.getDepartments());
			request.setAttribute("posList", register2Service.getPositions());

			return "/WEB-INF/view/employee/employeeRegister2.jsp";
		}
		return null;
	}
}