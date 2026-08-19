package dailywork.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dailywork.model.FieldOrProjectVO;

public class FieldOrProjectDao {

	// 1. 전체 목록 조회 (현재 테이블 구조인 id, name 기준)
	public List<FieldOrProjectVO> selectVisibleProjects(Connection conn) throws SQLException {
		String sql = "SELECT field_or_project_id, name FROM FIELD_OR_PROJECT ORDER BY field_or_project_id";
		List<FieldOrProjectVO> list = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				list.add(new FieldOrProjectVO(rs.getInt("field_or_project_id"), rs.getString("name"), "Y" // displayYn

				));
			}
			return list;
		}
	}

	// 2. 프로젝트 추가
	public int insertProject(Connection conn, String projectName) throws SQLException {
		String sql = "INSERT INTO FIELD_OR_PROJECT (field_or_project_id, name) VALUES (field_or_project_seq.NEXTVAL, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, projectName);
			return pstmt.executeUpdate();
		}
	}

	// 3. 삭제 (실제 DELETE 처리)
	public int softDeleteProject(Connection conn, int projectId) throws SQLException {
		String sql = "DELETE FROM FIELD_OR_PROJECT WHERE field_or_project_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, projectId);
			return pstmt.executeUpdate();
		}
	}

	// 4. 초기화 (전체 삭제)
	public int resetProjects(Connection conn) throws SQLException {
		String sql = "DELETE FROM FIELD_OR_PROJECT";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			return pstmt.executeUpdate();
		}
	}
}