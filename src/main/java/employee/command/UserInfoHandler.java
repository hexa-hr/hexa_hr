package employee.command;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import employee.dao.UserInfoDao;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class UserInfoHandler implements CommandHandler {

	private UserInfoDao dao = new UserInfoDao();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();

		// 🌟 GET: 화면 보여주기 (DB 데이터 + 세션에 저장해둔 가짜 데이터 합치기!)
		if (request.getMethod().equalsIgnoreCase("GET")) {
			try (java.sql.Connection conn = ConnectionProvider.getConnection()) {
				// 1. 기본 DB 데이터 불러오기
				Map<String, String> info = dao.selectCompanyInfo(conn);

				// 2. 세션에 저장해둔 화면용 입력 데이터(fakeData)가 있으면 덮어씌우기
				@SuppressWarnings("unchecked") Map<String, String> fakeData = (Map<String, String>)session
					.getAttribute("fakeData");
				if (fakeData != null) {
					info.putAll(fakeData);
				}

				request.setAttribute("info", info);
				request.setAttribute("deptList", dao.selectDepartments(conn));
				request.setAttribute("posList", dao.selectPositions(conn));
				return "/WEB-INF/view/employee/userInfo.jsp";
			}
		}

		// 🌟 POST: 저장하기 (세션에 화면 값 몽땅 저장 + DB에는 안전한 값만 저장)
		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

			// 1. 화면의 모든 입력값을 세션(fakeData)에 저장해서 유지되도록 만듦!
			Map<String, String> fakeData = new HashMap<>();
			Enumeration<String> params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = params.nextElement();
				fakeData.put(name, request.getParameter(name));
			}
			session.setAttribute("fakeData", fakeData);

			// 2. DB 업데이트용 안전한 데이터 뽑기
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

			// 🌟 설립일 에러 차단 방어막! (정확히 2000-01-01 형태일 때만 DB에 넣음)
			String estDate = request.getParameter("establishmentDate");
			if (estDate != null && estDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
				dbData.put("establishmentDate", estDate);
			} else {
				dbData.put("establishmentDate", ""); // 조건 안맞으면 NULL 처리되어 에러 안남
			}

			java.sql.Connection conn = null;
			try {
				conn = ConnectionProvider.getConnection();
				conn.setAutoCommit(false);
				dao.updateUserInfo(conn, dbData);
				conn.commit();
			} catch (Exception e) {
				JdbcUtil.rollback(conn);
				throw e;
			} finally {
				JdbcUtil.close(conn);
			}

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>alert('사용자 정보가 성공적으로 저장(유지)되었습니다!'); location.href='"
				+ request.getContextPath() + "/employee/userInfo.do';</script>");
			return null;
		}
		return null;
	}
}