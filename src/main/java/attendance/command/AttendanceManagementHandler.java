package attendance.command;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDao;
import attendance.model.AttendanceVO;
import attendance.model.EmployeeVO;
import jdbc.connection.ConnectionProvider;
import mvc.command.CommandHandler;

public class AttendanceManagementHandler implements CommandHandler {

	private AttendanceDao attendanceDao = new AttendanceDao();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req, res);
		} else if (req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	// 화면 로딩 (GET)
	private String processForm(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try (Connection conn = ConnectionProvider.getConnection()) {
			List<EmployeeVO> empList = attendanceDao.selectAllEmployees(conn);
			req.setAttribute("empList", empList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/WEB-INF/view/attendance/attendanceManagement.jsp";
	}

	// 근태 기록 저장 (POST)
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		try {
			// 1. 사원번호 수집 (empNo 또는 employeeId 모두 대응 및 "No-" 접두사 제거)
			String empNoStr = req.getParameter("empNo");
			if (empNoStr == null || empNoStr.trim().isEmpty()) {
				empNoStr = req.getParameter("employeeId");
			}

			int employeeId = 0;
			if (empNoStr != null && !empNoStr.trim().isEmpty()) {
				empNoStr = empNoStr.replace("No-", "").trim();
				employeeId = Integer.parseInt(empNoStr);
			}

			// 2. 근태 유형 수집 (attendanceType 문자열/숫자 호환 변환)
			String typeStr = req.getParameter("attendanceType");
			if (typeStr == null || typeStr.trim().isEmpty()) {
				typeStr = req.getParameter("attendanceTypeId");
			}
			int attendanceTypeId = parseAttendanceTypeId(typeStr);

			// 3. 근태 시작일 / 종료일 수집
			String startDateStr = req.getParameter("startDate");
			String endDateStr = req.getParameter("endDate");
			Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr)
					: null;
			Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;

			// 4. 근태일수 수집
			String daysStr = req.getParameter("attendanceDays");
			double attendanceDays = (daysStr != null && !daysStr.trim().isEmpty()) ? Double.parseDouble(daysStr) : 0.0;

			// 5. 금액(수당) 수집 (wageAmount 또는 amount 호환)
			String amountStr = req.getParameter("wageAmount");
			if (amountStr == null || amountStr.trim().isEmpty()) {
				amountStr = req.getParameter("amount");
			}
			int amount = (amountStr != null && !amountStr.trim().isEmpty()) ? Integer.parseInt(amountStr) : 0;

			// 6. 적요 수집 (remark 또는 summary 호환)
			String summary = req.getParameter("remark");
			if (summary == null) {
				summary = req.getParameter("summary");
			}

			// VO 객체 생성
			AttendanceVO vo = new AttendanceVO();
			vo.setEmployeeId(employeeId);
			vo.setAttendanceTypeId(attendanceTypeId);
			vo.setStartDate(startDate);
			vo.setEndDate(endDate);
			vo.setAttendanceDays(attendanceDays);
			vo.setAmount(amount);
			vo.setSummary(summary);

			// DB 저장 수행
			try (Connection conn = ConnectionProvider.getConnection()) {
				attendanceDao.insertAttendance(conn, vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 저장 처리 후 페이지 새로고침(리다이렉트)
		res.sendRedirect(req.getContextPath() + "/attendance/manage.do");
		return null;
	}

	// 근태 항목 이름("연차", "조퇴" 등)을 DB ID(숫자)로 안전하게 변환해주는 메서드
	private int parseAttendanceTypeId(String typeStr) {
		if (typeStr == null || typeStr.trim().isEmpty())
			return 1;

		try {
			return Integer.parseInt(typeStr);
		} catch (NumberFormatException e) {
			switch (typeStr.trim()) {
			case "연차":
				return 1;
			case "조퇴":
				return 2;
			case "포상휴가":
				return 3;
			default:
				return 1;
			}
		}
	}
}