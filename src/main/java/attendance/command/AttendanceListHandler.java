package attendance.command;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import attendance.model.AttendanceVO;
import jdbc.connection.ConnectionProvider; // ★ 프로젝트 내 ConnectionProvider 패키지 경로를 확인 후 수정해주세요.
import mvc.command.CommandHandler;

public class AttendanceListHandler implements CommandHandler {

	private AttendanceDao attendanceDao = new AttendanceDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// 1. 파라미터 수신 (empNo)
		String empNoParam = req.getParameter("empNo");

		if (empNoParam == null || empNoParam.trim().isEmpty()) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}

		int empNo = Integer.parseInt(empNoParam);

		// 2. DB 연결 생성 후 DAO 메서드 호출
		List<AttendanceVO> attendanceList = null;
		try (Connection conn = ConnectionProvider.getConnection()) {
			attendanceList = attendanceDao.selectAttendanceByEmpId(conn, empNo);
		}

		// 3. 응답 헤더 설정 (JSON)
		res.setContentType("application/json; charset=UTF-8");

		// 4. StringBuilder로 JSON 배열 조립
		StringBuilder json = new StringBuilder();
		json.append("[");

		if (attendanceList != null) {
			for (int i = 0; i < attendanceList.size(); i++) {
				AttendanceVO item = attendanceList.get(i);
				json.append("{");
				json.append("\"attendanceId\":").append(item.getAttendanceId()).append(",");
				json.append("\"inputDate\":\"").append(item.getInputDate()).append("\",");
				json.append("\"attendanceTypeName\":\"")
						.append(item.getAttendanceTypeName() != null ? item.getAttendanceTypeName() : "").append("\",");
				json.append("\"startDate\":\"").append(item.getStartDate()).append("\",");
				json.append("\"endDate\":\"").append(item.getEndDate()).append("\",");
				json.append("\"attendanceDays\":").append(item.getAttendanceDays()).append(",");
				json.append("\"amount\":").append(item.getAmount()).append(",");
				json.append("\"summary\":\"").append(item.getSummary() != null ? item.getSummary() : "").append("\"");
				json.append("}");

				if (i < attendanceList.size() - 1) {
					json.append(",");
				}
			}
		}
		json.append("]");

		// 5. 브라우저로 데이터 직접 출력
		res.getWriter().write(json.toString());

		// 6. JSP 포워딩 없이 직접 응답했으므로 null 반환
		return null;
	}
}