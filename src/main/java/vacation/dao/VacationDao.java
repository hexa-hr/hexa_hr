package vacation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vacation.model.VacationDetail; // 상세 DTO 임포트
import vacation.model.VacationType;

public class VacationDao {

	// 1. 사용여부가 'Y'인 휴가 항목 리스트 조회 (셀렉트 박스용)
	public List<VacationType> selectActiveVacationTypes(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT vacation_type_id, vacation_type_name, apply_period1, apply_period2, usage FROM vacation_type WHERE usage = 'Y' ORDER BY vacation_type_id";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			List<VacationType> list = new ArrayList<>();
			while (rs.next()) {
				VacationType vt = new VacationType();
				vt.setVacationTypeId(rs.getInt("vacation_type_id"));
				vt.setVacationTypeName(rs.getString("vacation_type_name"));
				vt.setApplyPeriod1(rs.getDate("apply_period1"));
				vt.setApplyPeriod2(rs.getDate("apply_period2"));
				vt.setUsage(rs.getString("usage"));
				list.add(vt);
			}
			return list;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {}
		}
	}

	// 2. 휴가 현황 리스트 조회 (employeeId 추가 매핑)
	public List<VacationType> selectVacationList(Connection conn, String vacationTypeId, String keyword)
		throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("SELECT e.employee_id AS employeeId, "); // 👉 [수정] 사원 ID 추가
			sql.append("       e.employment_type AS employmentType, ");
			sql.append("       e.account_id AS employeeNumber, ");
			sql.append("       e.korean_name AS koreanName, ");
			sql.append("       d.department_name AS departmentName, ");
			sql.append("       p.position_name AS positionName, ");
			sql.append("       vt.vacation_type_name AS vacationTypeName, ");
			sql.append("       19 AS totalDays, ");
			sql.append("       vd.vacation_value AS usedDays, ");
			sql.append("       (19 - vd.vacation_value) AS remainingDays ");
			sql.append("FROM vacation_days vd ");
			sql.append("JOIN employee e ON vd.employee_id = e.employee_id ");
			sql.append("JOIN vacation_type vt ON vd.vacation_type_id = vt.vacation_type_id ");
			sql.append("LEFT JOIN department d ON e.department_id = d.department_id ");
			sql.append("LEFT JOIN position p ON e.position_id = p.position_id ");
			sql.append("WHERE 1=1 ");

			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				sql.append("AND vt.vacation_type_id = ? ");
			}
			if (keyword != null && !keyword.isEmpty()) {
				sql.append("AND (e.korean_name LIKE ? OR e.account_id LIKE ?) ");
			}

			pstmt = conn.prepareStatement(sql.toString());

			int idx = 1;
			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				pstmt.setInt(idx++, Integer.parseInt(vacationTypeId));
			}
			if (keyword != null && !keyword.isEmpty()) {
				pstmt.setString(idx++, "%" + keyword + "%");
				pstmt.setString(idx++, "%" + keyword + "%");
			}

			rs = pstmt.executeQuery();
			List<VacationType> list = new ArrayList<>();
			while (rs.next()) {
				VacationType v = new VacationType();
				v.setEmployeeId(rs.getInt("employeeId")); // 👉 [수정] 사원 ID 세팅
				v.setEmploymentType(rs.getString("employmentType"));
				v.setEmployeeNumber(rs.getString("employeeNumber"));
				v.setKoreanName(rs.getString("koreanName"));
				v.setDepartmentName(rs.getString("departmentName"));
				v.setPositionName(rs.getString("positionName"));
				v.setVacationTypeName(rs.getString("vacationTypeName"));
				v.setTotalDays(rs.getDouble("totalDays"));
				v.setUsedDays(rs.getDouble("usedDays"));
				v.setRemainingDays(rs.getDouble("remainingDays"));

				list.add(v);
			}
			return list;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {}
		}
	}

	// 사원별 상세 휴가 내역 조회
	public List<VacationDetail> selectVacationDetail(Connection conn, int employeeId) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.department_name AS departmentName, ");
		sql.append("       e.korean_name AS koreanName, ");
		sql.append("       vd.vacation_days_id AS seq, ");
		sql.append("       TO_CHAR(SYSDATE, 'YYYY-MM-DD') AS regDate, "); // 필요시 실제 날짜 컬럼으로 변경
		sql.append("       vt.vacation_type_name AS vacationType, ");
		sql.append("       '연차' AS attendance, "); // 근태항목 (필요시 DB 컬럼으로 대체)
		sql.append("       TO_CHAR(SYSDATE, 'YYYY-MM-DD') AS period, "); // 기간 (필요시 DB 컬럼으로 대체)
		sql.append("       vd.vacation_value AS days, ");
		sql.append("       '' AS remarks ");
		sql.append("FROM vacation_days vd ");
		sql.append("JOIN employee e ON vd.employee_id = e.employee_id ");
		sql.append("JOIN vacation_type vt ON vd.vacation_type_id = vt.vacation_type_id ");
		sql.append("LEFT JOIN department d ON e.department_id = d.department_id ");
		sql.append("WHERE vd.employee_id = ? ");
		sql.append("ORDER BY vd.vacation_days_id DESC");

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			pstmt.setInt(1, employeeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				List<VacationDetail> list = new ArrayList<>();
				while (rs.next()) {
					VacationDetail d = new VacationDetail();
					d.setDepartmentName(rs.getString("departmentName"));
					d.setKoreanName(rs.getString("koreanName"));
					d.setSeq(rs.getInt("seq"));
					d.setRegDate(rs.getString("regDate"));
					d.setVacationType(rs.getString("vacationType"));
					d.setAttendance(rs.getString("attendance"));
					d.setPeriod(rs.getString("period"));
					d.setDays(rs.getDouble("days"));
					d.setRemarks(rs.getString("remarks"));
					list.add(d);
				}
				return list;
			}
		}
	}
}