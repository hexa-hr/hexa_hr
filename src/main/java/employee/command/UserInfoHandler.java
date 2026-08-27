package employee.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.dao.EmployeeSalaryAccountDao;
import employee.dao.UserInfoDao;
import employee.model.EmployeeSalaryAccount;
import employee.service.EmployeeRegisterService;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class UserInfoHandler implements CommandHandler {

	private UserInfoDao dao = new UserInfoDao();
	private EmployeeSalaryAccountDao accDao = new EmployeeSalaryAccountDao();
	private EmployeeRegisterService empService = new EmployeeRegisterService();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (request.getMethod().equalsIgnoreCase("GET")) {
			try (java.sql.Connection conn = ConnectionProvider.getConnection()) {
				// 1. 회사 및 담당자 기본 정보 조회
				Map<String, String> info = dao.selectCompanyInfo(conn);

				// 2. 회사 급여 지급정보 조회 (company_id = 1)
				EmployeeSalaryAccount account = accDao.selectByCompanyId(conn, 1);

				request.setAttribute("info", info);
				request.setAttribute("account", account); // JSP에 account 객체 전달
				request.setAttribute("deptList", empService.getDepartments());
				request.setAttribute("posList", empService.getPositions());

				return "/WEB-INF/view/employee/userInfo.jsp";
			}
		}

		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

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

			// 3. 급여 설정 객체 매핑
			EmployeeSalaryAccount accToSave = new EmployeeSalaryAccount();
			accToSave.setCompanyId(1); // 회사 ID 고정
			accToSave.setBankName(request.getParameter("bankName"));
			accToSave.setAccountNumber(request.getParameter("accountNumber"));
			accToSave.setDepositStocks(request.getParameter("depositStocks"));
			accToSave.setSalaryCalculation1(parseInt(request.getParameter("salaryCalc1")));
			accToSave.setSalaryCalculation2(parseInt(request.getParameter("salaryCalc2")));
			accToSave.setSalaryPaymentDate(parseInt(request.getParameter("salaryPaymentDate")));
			accToSave.setCalc1MonthType(request.getParameter("calc1MonthType"));
			accToSave.setCalc2MonthType(request.getParameter("calc2MonthType"));
			accToSave.setPaymentMonthType(request.getParameter("paymentMonthType"));

			java.sql.Connection conn = null;
			try {
				conn = ConnectionProvider.getConnection();
				conn.setAutoCommit(false); // 트랜잭션 시작

				// 기존 정보 업데이트 (회사, 담당자 등)
				dao.updateUserInfo(conn, dbData);

				// 급여 정보 등록 또는 업데이트 로직
				EmployeeSalaryAccount existingAcc = accDao.selectByCompanyId(conn, 1);
				if (existingAcc != null) {
					accToSave.setAccountId(existingAcc.getAccountId());
					accDao.update(conn, accToSave); // 기존 행이 있으면 UPDATE
				} else {
					accDao.insert(conn, accToSave); // 없으면 시퀀스 발급 후 INSERT
				}

				conn.commit(); // 트랜잭션 종료
			} catch (Exception e) {
				JdbcUtil.rollback(conn);
				throw e;
			} finally {
				JdbcUtil.close(conn);
			}

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>alert('ユーザー情報と給与日が正常に保存されました！'); location.href='"
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