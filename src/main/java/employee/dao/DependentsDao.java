package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Dependents;
import jdbc.JdbcUtil;

public class DependentsDao {

	// 가족 사항 INSERT 메서드
	public void insert(Connection conn, Dependents dependent) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			// dependent_id는 시퀀스로 자동 생성
			pstmt = conn.prepareStatement(
				"INSERT INTO dependents " +
					"(dependent_id, employee_id, relationship, parents_name, foreign_or_domestic1, parents_number1, parents_number2) "
					+
					"VALUES (dependents_seq.nextval, ?, ?, ?, ?, ?, ?)");

			// 1. employee_id (Service에서 넘겨받은 사원번호)
			pstmt.setInt(1, dependent.getEmployeeId());

			// 2. relationship ~ 6. parentsNumber2
			pstmt.setString(2, dependent.getRelationship());
			pstmt.setString(3, dependent.getParentsName());
			pstmt.setString(4, dependent.getForeignOrDomestic1());
			pstmt.setString(5, dependent.getParentsNumber1());
			pstmt.setString(6, dependent.getParentsNumber2());

			pstmt.executeUpdate();

		} finally {
			JdbcUtil.close(pstmt);
		}
	}
}