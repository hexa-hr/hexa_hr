package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import attendance.model.AttendanceGroup;
import jdbc.JdbcUtil;

public class AttendanceGroupDao {

	// 근태그룹 목록 조회
	public List<AttendanceGroup> selectAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<AttendanceGroup> list = new ArrayList<>();

		// 컬럼명: attendance_group_name 으로 수정
		String sql = "SELECT attendance_group_id, attendance_group_name FROM attendance_group ORDER BY attendance_group_id ASC";

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				AttendanceGroup group = new AttendanceGroup(
					rs.getInt("attendance_group_id"),
					rs.getString("attendance_group_name") // 컬럼명 수정
				);
				list.add(group);
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 신규 근태그룹 추가
	public void insert(Connection conn, String groupName) throws SQLException {
		PreparedStatement pstmt = null;
		// 컬럼명: attendance_group_name 으로 수정
		String sql = "INSERT INTO attendance_group (attendance_group_id, attendance_group_name) "
			+ "VALUES (attendance_group_seq.NEXTVAL, ?)";

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, groupName);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 그룹 수정
	public void update(Connection conn, int id, String groupName) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "UPDATE attendance_group SET attendance_group_name = ? WHERE attendance_group_id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, groupName);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 단일 삭제
	public void delete(Connection conn, int id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM attendance_group WHERE attendance_group_id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 전체 초기화 (전체 삭제)
	public void deleteAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM attendance_group";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}