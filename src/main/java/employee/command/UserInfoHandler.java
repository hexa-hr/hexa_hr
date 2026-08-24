package employee.command;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import employee.dao.UserInfoDao;
import employee.service.EmployeeRegisterService; // 🌟 사원등록 서비스를 불러옵니다!
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class UserInfoHandler implements CommandHandler {

	private UserInfoDao dao = new UserInfoDao();
	// 🌟 사원등록 페이지와 똑같은 DB 테이블을 바라보기 위해 서비스 객체 생성
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

				// 🌟 핵심 변경: UserInfoDao가 아닌, 사원등록(EmployeeRegisterService)에서 쓰는 부서/직위 목록을 그대로 가져옵니다!
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
			dbData.put("companyName", request.getParameter("companyName"));
			dbData.put("businessNumber", request.getParameter("businessNumber"));
			dbData.put("officeAddress", request.getParameter("officeAddress"));
			dbData.put("phoneNumber", request.getParameter("phone1") + "-" + request.getParameter("phone2") + "-"
				+ request.getParameter("phone3"));
			dbData.put("contactName", request.getParameter("contactName"));
			dbData.put("departmentId", request.getParameter("departmentId"));
			dbData.put("positionId", request.getParameter("positionId"));
			dbData.put("email", request.getParameter("email"));

			String estDate = request.getParameter("establishmentDate");
			if (estDate != null && estDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
				dbData.put("establishmentDate", estDate);
			} else {
				dbData.put("establishmentDate", "");
			}

			// 화면에서 급여 설정값(날짜 등) 뽑아오기
			Integer salaryCalc1 = parseInt(request.getParameter("salaryCalc1"));
			Integer salaryCalc2 = parseInt(request.getParameter("salaryCalc2"));
			Integer salaryPaymentDate = parseInt(request.getParameter("salaryPaymentDate"));
			String calc1MonthType = request.getParameter("calc1MonthType");
			String calc2MonthType = request.getParameter("calc2MonthType");
			String paymentMonthType = request.getParameter("paymentMonthType");

			// 추가된 부분: 은행, 계좌번호, 예금주 파라미터 뽑아오기
			String bankName = request.getParameter("bankName");
			String accountNumber = request.getParameter("accountNumber");
			String depositStocks = request.getParameter("depositStocks");

			java.sql.Connection conn = null;
			try {
				conn = ConnectionProvider.getConnection();
				conn.setAutoCommit(false);

				// 1. 기존 회사 정보 업데이트
				dao.updateUserInfo(conn, dbData);

				// 2. 모든 사원의 급여일 테이블 일괄 업데이트 실행! (파라미터 3개 추가 연결 완료)
				dao.updateAllEmployeeSalaryDates(conn, salaryCalc1, salaryCalc2, salaryPaymentDate,
					calc1MonthType, calc2MonthType, paymentMonthType, bankName, accountNumber, depositStocks);

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

	// 문자열을 안전하게 숫자로 바꿔주는 헬퍼 메서드
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