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

	// 画面ローディング (GET)
	private String processForm(HttpServletRequest req, HttpServletResponse res) throws Exception {
		try (Connection conn = ConnectionProvider.getConnection()) {
			List<EmployeeVO> empList = attendanceDao.selectAllEmployees(conn);
			req.setAttribute("empList", empList);

			List<master.model.AttendanceType> attendanceList = attendanceDao.selectAll(conn);
			req.setAttribute("attendanceList", attendanceList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/WEB-INF/view/attendance/attendanceManagement.jsp";
	}

	// 勤怠記録の保存 (POST)
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req.setCharacterEncoding("UTF-8");

		try {
			// 1. 社員番号の収集 (empNoまたはemployeeIdの両方に対応し、「No-」接頭辞を削除)
			String empNoStr = req.getParameter("empNo");
			if (empNoStr == null || empNoStr.trim().isEmpty()) {
				empNoStr = req.getParameter("employeeId");
			}

			int employeeId = 0;
			if (empNoStr != null && !empNoStr.trim().isEmpty()) {
				empNoStr = empNoStr.replace("No-", "").trim();
				employeeId = Integer.parseInt(empNoStr);
			}

			// 2. 勤怠タイプの収集 (attendanceType 文字列/数字互換変換)
			String typeStr = req.getParameter("attendanceType");
			if (typeStr == null || typeStr.trim().isEmpty()) {
				typeStr = req.getParameter("attendanceTypeId");
			}
			int attendanceTypeId = parseAttendanceTypeId(typeStr);

			// 3. 勤怠開始日 / 終了日の収集
			String startDateStr = req.getParameter("startDate");
			String endDateStr = req.getParameter("endDate");
			Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr)
					: null;
			Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;

			// 4. 勤怠日数の収集
			String daysStr = req.getParameter("attendanceDays");
			double attendanceDays = (daysStr != null && !daysStr.trim().isEmpty()) ? Double.parseDouble(daysStr) : 0.0;

			// 5. 金額(手当)の収集 (wageAmountまたはamount互換)
			String amountStr = req.getParameter("wageAmount");
			if (amountStr == null || amountStr.trim().isEmpty()) {
				amountStr = req.getParameter("amount");
			}
			int amount = (amountStr != null && !amountStr.trim().isEmpty()) ? Integer.parseInt(amountStr) : 0;

			// 6. 摘要の収集 (remarkまたはsummary互換)
			String summary = req.getParameter("remark");
			if (summary == null) {
				summary = req.getParameter("summary");
			}

			// VOオブジェクトの生成
			AttendanceVO vo = new AttendanceVO();
			vo.setEmployeeId(employeeId);
			vo.setAttendanceTypeId(attendanceTypeId);
			vo.setStartDate(startDate);
			vo.setEndDate(endDate);
			vo.setAttendanceDays(attendanceDays);
			vo.setAmount(amount);
			vo.setSummary(summary);

			// DB保存の実行
			try (Connection conn = ConnectionProvider.getConnection()) {
				attendanceDao.insertAttendance(conn, vo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 保存処理後にページ更新(リダイレクト)
		res.sendRedirect(req.getContextPath() + "/attendance/manage.do");
		return null;
	}

	// 勤怠項目名("有給休暇", "早退"など)をDB ID(数字)に安全に変換するメソッド
	private int parseAttendanceTypeId(String typeStr) {
		if (typeStr == null || typeStr.trim().isEmpty())
			return 1;

		try {
			return Integer.parseInt(typeStr);
		} catch (NumberFormatException e) {
			switch (typeStr.trim()) {
			case "有給休暇":
				return 1;
			case "早退":
				return 2;
			case "リフレッシュ休暇":
				return 3;
			default:
				return 1;
			}
		}
	}
}