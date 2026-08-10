package department.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import department.model.Department;
import jdbc.JdbcUtil;

public class DepartmentDao {

	// 1. 전체 부서 목록 조회
	public List<Department> selectAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// 테이블명(DEPARTMENT_TB -> DEPARTMENT), 컬럼명(DEPT_ -> DEPARTMENT_) 수정 완!
			pstmt = conn
				.prepareStatement("SELECT DEPARTMENT_ID, DEPARTMENT_NAME FROM DEPARTMENT ORDER BY DEPARTMENT_ID ASC");
			rs = pstmt.executeQuery();
			List<Department> result = new ArrayList<>();
			while (rs.next()) {
				// 바구니에 담을 때도 실제 컬럼명과 똑같이!
				result.add(new Department(rs.getInt("DEPARTMENT_ID"), rs.getString("DEPARTMENT_NAME")));
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	// 2. 새 부서 추가
	public void insert(Connection conn, String deptName) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// 테이블명 및 컬럼명 수정. (※ 시퀀스 이름도 DEPARTMENT_SEQ로 가정하고 수정했습니다)
			pstmt = conn
				.prepareStatement(
					"INSERT INTO DEPARTMENT (DEPARTMENT_ID, DEPARTMENT_NAME) VALUES (DEPARTMENT_SEQ.NEXTVAL, ?)");
			pstmt.setString(1, deptName);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 3. 개별 부서 삭제
	public void delete(Connection conn, int deptId) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// 테이블명, 조건 컬럼명 수정 완!
			pstmt = conn.prepareStatement("DELETE FROM DEPARTMENT WHERE DEPARTMENT_ID = ?");
			pstmt.setInt(1, deptId);
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	// 4. 전체 초기화 (싹 지우기)
	public void deleteAll(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// 테이블명 수정 완!
			pstmt = conn.prepareStatement("DELETE FROM DEPARTMENT");
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}