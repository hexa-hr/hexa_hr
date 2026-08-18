package attendance.command;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class AttendanceDeleteHandler implements CommandHandler {

	private AttendanceDao attendanceDao = new AttendanceDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 1. 삭제할 근태 번호 파라미터 수신 (attendanceId 또는 id)
		String idParam = req.getParameter("attendanceId");
		if (idParam == null || idParam.trim().isEmpty()) {
			idParam = req.getParameter("id");
		}

		if (idParam == null || idParam.trim().isEmpty()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}

		// 숫자만 추출
		String cleanId = idParam.replaceAll("[^0-9]", "");
		int attendanceId = Integer.parseInt(cleanId);

		// 2. DB 삭제 실행
		int result = 0;
		try (Connection conn = ConnectionProvider.getConnection()) {
			result = attendanceDao.deleteAttendance(conn, attendanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 3. 처리 결과 응답
		res.setContentType("text/plain; charset=UTF-8");
		if (result > 0) {
			res.getWriter().write("success");
		} else {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().write("fail");
		}

		return null;
	}
}