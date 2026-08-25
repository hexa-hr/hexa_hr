package employee.command;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import employee.dao.UserInfoDao;
import employee.service.EmployeeRegisterService;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class UserInfoHandler implements CommandHandler {

	private UserInfoDao dao = new UserInfoDao();
	private EmployeeRegisterService empService = new EmployeeRegisterService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();

		if (request.getMethod().equalsIgnoreCase("GET")) {
			try (java.sql.Connection conn = ConnectionProvider.getConnection()) {
				Map<String, String> info = dao.selectCompanyInfo(conn);

				@SuppressWarnings("unchecked") Map<String, String> fakeData = (Map<String, String>)session
					.getAttribute("fakeData");
				if (fakeData != null) {
					info.putAll(fakeData);
				}

				request.setAttribute("info", info);
				request.setAttribute("deptList", empService.getDepartments());
				request.setAttribute("posList", empService.getPositions());

				return "/WEB-INF/view/employee/userInfo.jsp";
			}
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

			Map<String, String> fakeData = new HashMap<>();
			Enumeration<String> params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = params.nextElement();
				fakeData.put(name, request.getParameter(name));
			}
			session.setAttribute("fakeData", fakeData);

			Map<String, String> dbData = new HashMap<>();

			// 1. 회사 정보
			dbData.put("companyName", request.getParameter("companyName"));
			dbData.put("repTitle", request.getParameter("repTitle"));
			dbData.put("repName", request.getParameter("repName"));
			dbData.put("businessNumber", request.getParameter("businessNumber"));
			dbData.put("corpNumber", request.getParameter("corpNumber"));
			dbData.put("officeAddress", request.getParameter("officeAddress"));
			dbData.put("website", request.getParameter("website"));
			dbData.put("bizType", request.getParameter("bizType"));
			dbData.put("bizItem", request.getParameter("bizItem"));

			String p1 = request.getParameter("phone1");
			dbData.put("phoneNumber", (p1 != null && !p1.isEmpty()
				? p1 + "-" + request.getParameter("phone2") + "-" + request.getParameter("phone3") : ""));

			String f1 = request.getParameter("fax1");
			dbData.put("faxNumber", (f1 != null && !f1.isEmpty()
				? f1 + "-" + request.getParameter("fax2") + "-" + request.getParameter("fax3") : ""));

			// 🌟 설립일 자동 변환 마법 (20260825, 2026.08.25 -> 2026-08-25)
			String estDate = request.getParameter("establishmentDate");
			if (estDate != null) {
				estDate = estDate.trim();
				String onlyNums = estDate.replaceAll("[^0-9]", ""); // 숫자만 쏙 뽑아냄
				if (onlyNums.length() >= 8) {
					estDate = onlyNums.substring(0, 4) + "-" + onlyNums.substring(4, 6) + "-"
						+ onlyNums.substring(6, 8);
				} else if (estDate.length() >= 10) {
					estDate = estDate.substring(0, 10);
				}
			}
			dbData.put("establishmentDate",
				(estDate != null && estDate.matches("\\d{4}-\\d{2}-\\d{2}")) ? estDate : "");

			// 2. 담당자 정보
			dbData.put("contactName", request.getParameter("contactName"));
			dbData.put("departmentId", request.getParameter("departmentId"));
			dbData.put("positionId", request.getParameter("positionId"));
			dbData.put("email", request.getParameter("email"));

			String cp1 = request.getParameter("cPhone1");
			dbData.put("conPhoneNumber", (cp1 != null && !cp1.isEmpty()
				? cp1 + "-" + request.getParameter("cPhone2") + "-" + request.getParameter("cPhone3") : ""));

			String mob1 = request.getParameter("mobile1");
			dbData.put("mobileNumber", (mob1 != null && !mob1.isEmpty()
				? mob1 + "-" + request.getParameter("mobile2") + "-" + request.getParameter("mobile3") : ""));

			// 3. 급여 설정
			Integer salaryCalc1 = parseInt(request.getParameter("salaryCalc1"));
			Integer salaryCalc2 = parseInt(request.getParameter("salaryCalc2"));
			Integer salaryPaymentDate = parseInt(request.getParameter("salaryPaymentDate"));
			String calc1MonthType = request.getParameter("calc1MonthType");
			String calc2MonthType = request.getParameter("calc2MonthType");
			String paymentMonthType = request.getParameter("paymentMonthType");
			String bankName = request.getParameter("bankName");
			String accountNumber = request.getParameter("accountNumber");
			String depositStocks = request.getParameter("depositStocks");

			java.sql.Connection conn = null;
			try {
				conn = ConnectionProvider.getConnection();
				conn.setAutoCommit(false);

				dao.updateUserInfo(conn, dbData);
				dao.updateAllEmployeeSalaryDates(conn, salaryCalc1, salaryCalc2, salaryPaymentDate, calc1MonthType,
					calc2MonthType, paymentMonthType, bankName, accountNumber, depositStocks);

				conn.commit();
			} catch (Exception e) {
				JdbcUtil.rollback(conn);
				throw e;
			} finally {
				JdbcUtil.close(conn);
			}

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>alert('사용자 정보와 급여일 설정이 성공적으로 저장되었습니다!'); location.href='"
				+ request.getContextPath() + "/employee/userInfo.do';</script>");
			return null;
		}
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
}