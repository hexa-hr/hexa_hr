package vacation.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import attendance.dao.AttendanceDao;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class VacationService {

	private AttendanceDao attendanceDao = new AttendanceDao();

	// 사원별 휴가일수 목록 조회
	public List<Map<String, Object>> getEmployeeVacationList(int attendanceTypeId) {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return attendanceDao.selectEmployeeVacationList(conn, attendanceTypeId);
		} catch (SQLException e) {
			throw new RuntimeException("사원 휴가목록 조회 오류", e);
		}
	}

	// 사원별 휴가일수 저장
	public void saveVacationDays(int attendanceTypeId, String[] employeeIds, String[] vacationDays) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (employeeIds != null && vacationDays != null) {
				for (int i = 0; i < employeeIds.length; i++) {
					int empId = Integer.parseInt(employeeIds[i]);
					int days = Integer.parseInt(vacationDays[i]);
					attendanceDao.saveEmployeeVacationDays(conn, attendanceTypeId, empId, days);
				}
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("휴가일수 저장 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 선택된 사원의 휴가일수 삭제 (초기화)
	public void resetVacationDays(int attendanceTypeId, String[] employeeIds) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (employeeIds != null) {
				for (String empIdStr : employeeIds) {
					int empId = Integer.parseInt(empIdStr);
					// MERGE 대신 DELETE 호출로 외래키 문제 회피
					attendanceDao.deleteEmployeeVacationDays(conn, attendanceTypeId, empId);
				}
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("휴가일수 삭제 처리 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}