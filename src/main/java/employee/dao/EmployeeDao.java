package employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import employee.model.Employee;

public class EmployeeDao {

	public void insert(Connection connection, Employee employee) throws SQLException {
		// ★ 주의: EMPLOYEE 테이블명과 컬럼명은 실제 데이터베이스에 맞게 수정해야 합니다.
		String sqlQuery = "INSERT INTO EMPLOYEE (EMPLOYMENT_TYPE, KOREAN_NAME, ENGLISH_NAME, HIRE_DATE, STATUS) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
			preparedStatement.setString(1, employee.getEmploymentType());
			preparedStatement.setString(2, employee.getKoreanName());
			preparedStatement.setString(3, employee.getEnglishName());

			// java.util.Date를 java.sql.Date로 변환하여 데이터베이스에 저장합니다.
			if (employee.getHireDate() != null) {
				preparedStatement.setDate(4, new java.sql.Date(employee.getHireDate().getTime()));
			} else {
				preparedStatement.setNull(4, java.sql.Types.DATE);
			}

			preparedStatement.setString(5, employee.getStatus());

			preparedStatement.executeUpdate();
		}
	}
}