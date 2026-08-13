package dailywork.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dailywork.model.FieldOrProjectVO;
import jdbc.JdbcUtil;

public class FieldOrProjectDao {

	// 1. 목록 조회 (display_yn이 'Y'인 것만)
	public List<FieldOrProjectVO> selectVisibleProjects(Connection conn) throws SQLException {
		String sql = "SELECT field_or_project_id, project_name, display_yn FROM FIELD_OR_PROJECT WHERE display_yn = 'Y' ORDER BY field_or_project_id";
		List<FieldOrProjectVO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new FieldOrProjectVO(rs.getInt("field_or_project_id"), rs.getString("project_name"),
						rs.getString("display_yn")));
			}
			return list;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 현장/프로젝트 추가
	public int insertProject(Connection conn, String projectName) throws SQLException {
		// 시퀀스 이름은 실제 사용하는 시퀀스명으로 변경하세요 (예: field_seq.NEXTVAL)
		String sql = "INSERT INTO FIELD_OR_PROJECT (field_or_project_id, project_name, display_yn) VALUES ((SELECT NVL(MAX(field_or_project_id), 0) + 1 FROM FIELD_OR_PROJECT), ?, 'Y')";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, projectName);
			return pstmt.executeUpdate();
		}
	}

	// 3. 소프트 삭제 (아예 삭제하지 않고 display_yn만 'N'으로 변경)
	public int softDeleteProject(Connection conn, int projectId) throws SQLException {
		String sql = "UPDATE FIELD_OR_PROJECT SET display_yn = 'N' WHERE field_or_project_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, projectId);
			return pstmt.executeUpdate();
		}
	}

	// 4. 초기화 (모든 프로젝트를 목록에서 숨김 처리)
	public int resetProjects(Connection conn) throws SQLException {
		String sql = "UPDATE FIELD_OR_PROJECT SET display_yn = 'N'";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			return pstmt.executeUpdate();
		}
	}
}