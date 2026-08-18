package vacation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	public List<VacationType> selectVacationList(Connection conn, String vacationTypeId, String keyword)
		throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();

			// 쿼리 안에서 연산을 수행하여 '전체', '사용', '잔여' 컬럼을 가상으로 만듭니다.
			sql.append("SELECT e.employment_type AS employmentType, ");
			sql.append("       e.account_id AS employeeNumber, ");
			sql.append("       e.korean_name AS koreanName, ");
			sql.append("       d.department_name AS departmentName, ");
			sql.append("       p.position_name AS positionName, ");
			sql.append("       vt.vacation_type_name AS vacationTypeName, ");
			sql.append("       19 AS totalDays, "); // DB에 없지만 화면상 '전체' 일수를 19로 고정 출력
			sql.append("       vd.vacation_value AS usedDays, "); // '사용' 일수
			sql.append("       (19 - vd.vacation_value) AS remainingDays "); // '잔여' = 전체(19) - 사용
			sql.append("FROM vacation_days vd ");
			sql.append("JOIN employee e ON vd.employee_id = e.employee_id ");
			sql.append("JOIN vacation_type vt ON vd.vacation_type_id = vt.vacation_type_id ");
			sql.append("LEFT JOIN department d ON e.department_id = d.department_id ");
			sql.append("LEFT JOIN position p ON e.position_id = p.position_id ");
			sql.append("WHERE 1=1 ");

			// 검색 조건이 있을 경우 파라미터 처리 (여기서부터 완성해 주세요)
			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				sql.append("AND vt.vacation_type_id = ? ");
			}
			if (keyword != null && !keyword.isEmpty()) {
				sql.append("AND (e.korean_name LIKE ? OR e.account_id LIKE ?) ");
			}

			pstmt = conn.prepareStatement(sql.toString());

			// 파라미터 세팅 로직
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
				v.setEmploymentType(rs.getString("employmentType"));
				v.setEmployeeNumber(rs.getString("employeeNumber"));
				v.setKoreanName(rs.getString("koreanName"));
				v.setDepartmentName(rs.getString("departmentName"));
				v.setPositionName(rs.getString("positionName"));
				v.setVacationTypeName(rs.getString("vacationTypeName"));

				// 새로 만든 가상 컬럼들을 DTO에 매핑
				v.setTotalDays(rs.getDouble("totalDays"));
				v.setUsedDays(rs.getDouble("usedDays"));
				v.setRemainingDays(rs.getDouble("remainingDays"));

				list.add(v);
			}
			return list;
		} finally {
			// finally 안에서 안전하게 close 처리
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
}