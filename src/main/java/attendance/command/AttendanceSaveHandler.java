package attendance.command;

import java.io.PrintWriter;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.model.AttendanceVO;
import attendance.service.AttendanceService;
import mvc.command.CommandHandler;

public class AttendanceSaveHandler implements CommandHandler {
	private AttendanceService attendanceService = new AttendanceService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		}
		res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		PrintWriter out = res.getWriter();

		try {
			// 1. 파라미터 파싱
			String empIdStr = req.getParameter("employee_id");
			if (empIdStr == null || empIdStr.isEmpty())
				empIdStr = req.getParameter("empNo");
			int employeeId = (empIdStr != null && !empIdStr.isEmpty()) ? Integer.parseInt(empIdStr) : 0;

			String typeIdStr = req.getParameter("attendance_type_id");
			if (typeIdStr == null || typeIdStr.isEmpty())
				typeIdStr = req.getParameter("attendanceType");
			int attendanceTypeId = (typeIdStr != null && !typeIdStr.isEmpty()) ? Integer.parseInt(typeIdStr) : 0;

			// 문자열 날짜를 java.sql.Date로 변환
			String startDateStr = req.getParameter("start_date") != null ? req.getParameter("start_date")
					: req.getParameter("startDate");
			String endDateStr = req.getParameter("end_date") != null ? req.getParameter("end_date")
					: req.getParameter("endDate");
			Date startDate = (startDateStr != null && !startDateStr.isEmpty()) ? Date.valueOf(startDateStr) : null;
			Date endDate = (endDateStr != null && !endDateStr.isEmpty()) ? Date.valueOf(endDateStr) : null;

			String daysStr = req.getParameter("attendance_days");
			if (daysStr == null || daysStr.isEmpty())
				daysStr = req.getParameter("attendanceDays");
			double attendanceDays = (daysStr != null && !daysStr.isEmpty()) ? Double.parseDouble(daysStr) : 0.0;

			String amountStr = req.getParameter("amount");
			if (amountStr == null || amountStr.isEmpty())
				amountStr = req.getParameter("wageAmount");
			int amount = 0; // DAO에서 amount를 int로 받고 있으므로 int 사용
			if (amountStr != null && !amountStr.trim().isEmpty()) {
				amount = Integer.parseInt(amountStr.replaceAll(",", ""));
			}

			String summary = req.getParameter("summary") != null ? req.getParameter("summary")
					: req.getParameter("remark");

			String attendanceIdStr = req.getParameter("attendance_id");
			int attendanceId = 0;
			if (attendanceIdStr != null && !attendanceIdStr.trim().isEmpty() && !attendanceIdStr.equals("undefined")) {
				attendanceId = Integer.parseInt(attendanceIdStr);
			}

			// 2. VO 객체에 데이터 담기
			AttendanceVO vo = new AttendanceVO();
			vo.setAttendanceId(attendanceId);
			vo.setEmployeeId(employeeId);
			vo.setAttendanceTypeId(attendanceTypeId);
			vo.setStartDate(startDate);
			vo.setEndDate(endDate);
			vo.setAttendanceDays(attendanceDays);
			vo.setAmount(amount);
			vo.setSummary(summary);

			// 3. 비즈니스 로직(Service) 실행
			boolean isSuccess = attendanceService.saveAttendance(vo);
			String redirectUrl = req.getContextPath() + "/attendance/manage.do";

			// 4. 결과 출력 (alert)
			if (isSuccess) {
				String msg = (attendanceId > 0) ? "근태 기록이 성공적으로 수정되었습니다." : "근태 기록이 성공적으로 등록되었습니다.";
				out.println("<script>alert('" + msg + "'); location.href='" + redirectUrl + "';</script>");
			} else {
				String msg = (attendanceId > 0) ? "수정 실패: 일치하는 근태 기록을 찾을 수 없습니다." : "등록 실패: 데이터가 저장되지 않았습니다.";
				out.println("<script>alert('" + msg + "'); history.back();</script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = e.getMessage() != null ? e.getMessage().replace("'", "\\'").replace("\n", " ")
					: "알 수 없는 에러";
			out.println("<script>alert('처리 중 서버 오류 발생:\\n" + errorMsg + "'); history.back();</script>");
		} finally {
			out.flush();
		}

		return null;
	}
}