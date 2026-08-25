package vacation.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vacation.model.VacationDetail; // 詳細DTOインポート
import vacation.model.VacationType;

public class VacationDao {

	// 1. 使用有無が 'Y' の休暇項目リストを取得 (セレクトボックス用)
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

	// 2. 休暇現況リストを取得 (employeeId 追加マッピング)
	public List<VacationType> selectVacationList(Connection conn, String vacationTypeId, String keyword)
		throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder();

			// 1. 各社員の「使用済み日数」を計算するためにサブクエリまたはJOINを使用
			sql.append("SELECT e.employee_id AS employeeId, ");
			sql.append("       e.employment_type AS employmentType, ");
			sql.append("       e.employee_id AS employeeNumber, ");
			sql.append("       e.korean_name AS koreanName, ");
			sql.append("       d.department_name AS departmentName, ");
			sql.append("       p.position_name AS positionName, ");
			sql.append("       vt.vacation_type_name AS \"vacationTypeName\", ");
			sql.append("       vd.vacation_value AS totalDays, ");

			// 使用日数の計算（attendanceテーブルで該当社員の該当休暇タイプの総使用量合計）
			sql.append("       (SELECT NVL(SUM(a.attendance_days), 0) ");
			sql.append("        FROM attendance a ");
			sql.append("        JOIN attendance_type atp ON a.attendance_type_id = atp.attendance_type_id ");
			sql.append("        WHERE a.employee_id = e.employee_id ");
			sql.append("        AND atp.vacation_type_id = vt.vacation_type_id) AS usedDays, ");

			// 残りの年次計算（総日数 - 使用日数）
			sql.append("       (vd.vacation_value - (SELECT NVL(SUM(a.attendance_days), 0) ");
			sql.append("                             FROM attendance a ");
			sql.append(
				"                             JOIN attendance_type atp ON a.attendance_type_id = atp.attendance_type_id ");
			sql.append("                             WHERE a.employee_id = e.employee_id ");
			sql.append(
				"                             AND atp.vacation_type_id = vt.vacation_type_id)) AS remainingDays ");

			sql.append("FROM vacation_days vd ");
			sql.append("JOIN employee e ON vd.employee_id = e.employee_id ");
			sql.append("JOIN vacation_type vt ON vd.vacation_type_id = vt.vacation_type_id ");
			sql.append("LEFT JOIN department d ON e.department_id = d.department_id ");
			sql.append("LEFT JOIN position p ON e.position_id = p.position_id ");
			sql.append("WHERE 1=1 ");

			// 💡 2. 재직(在職) 상태인 사원만 조회되도록 조건 추가
			sql.append("AND e.status = '재직' ");
			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				sql.append("AND vt.vacation_type_id = ? ");
			}
			if (keyword != null && !keyword.isEmpty()) {
				sql.append("AND (e.employment_type LIKE ? ");
				sql.append("     OR e.account_id LIKE ? ");
				sql.append("     OR e.korean_name LIKE ? ");
				sql.append("     OR d.department_name LIKE ? ");
				sql.append("     OR p.position_name LIKE ?) ");
			}

			pstmt = conn.prepareStatement(sql.toString());

			int idx = 1;
			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				pstmt.setInt(idx++, Integer.parseInt(vacationTypeId));
			}

			if (keyword != null && !keyword.isEmpty()) {
				String likeKeyword = "%" + keyword + "%";
				pstmt.setString(idx++, likeKeyword);
				pstmt.setString(idx++, likeKeyword);
				pstmt.setString(idx++, likeKeyword);
				pstmt.setString(idx++, likeKeyword);
				pstmt.setString(idx++, likeKeyword);
			}

			rs = pstmt.executeQuery();
			List<VacationType> list = new ArrayList<>();
			while (rs.next()) {
				VacationType v = new VacationType();
				v.setEmployeeId(rs.getInt("employeeId"));
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

	// 社員別詳細休暇履歴の取得
	public List<VacationDetail> selectVacationDetail(Connection conn, int employeeId, String vacationTypeId)
		throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.department_name AS departmentName, ");
		sql.append("       e.korean_name AS koreanName, ");
		sql.append("       a.attendance_id AS seq, ");
		sql.append("       TO_CHAR(a.start_date, 'YYYY-MM-DD') AS regDate, ");
		sql.append("       vt.vacation_type_name AS vacationType, ");
		sql.append("       at.attendance_type_name AS attendance, ");
		sql.append(
			"       TO_CHAR(a.start_date, 'YYYY-MM-DD') || ' ~ ' || TO_CHAR(a.end_date, 'YYYY-MM-DD') AS period, ");
		sql.append("       a.attendance_days AS days, ");
		sql.append("       a.summary AS remarks, ");
		sql.append("       NVL(vd.vacation_value, 0) AS totalDays ");
		sql.append("FROM attendance a ");
		sql.append("JOIN employee e ON a.employee_id = e.employee_id ");
		sql.append("JOIN attendance_type at ON a.attendance_type_id = at.attendance_type_id ");
		sql.append("JOIN vacation_type vt ON at.vacation_type_id = vt.vacation_type_id ");
		sql.append("LEFT JOIN department d ON e.department_id = d.department_id ");
		sql.append(
			"LEFT JOIN vacation_days vd ON a.employee_id = vd.employee_id AND vd.vacation_type_id = vt.vacation_type_id ");
		sql.append("WHERE a.employee_id = ? ");

		// 💡 선택된 휴가 항목이 있는 경우 조건 추가
		if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
			sql.append("AND vt.vacation_type_id = ? ");
		}

		sql.append("ORDER BY a.start_date DESC");

		try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			pstmt.setInt(1, employeeId);

			// 💡 vacationTypeId 바인딩
			if (vacationTypeId != null && !vacationTypeId.isEmpty()) {
				pstmt.setInt(2, Integer.parseInt(vacationTypeId));
			}

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
					d.setTotalDays(rs.getDouble("totalDays"));
					list.add(d);
				}
				return list;
			}
		}
	}
}