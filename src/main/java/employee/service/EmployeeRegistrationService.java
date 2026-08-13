package employee.service;

import java.sql.Connection;
import java.sql.SQLException;

import employee.dao.EmployeeDao;
import employee.model.Employee;
import jdbc.connection.ConnectionProvider;

public class EmployeeRegistrationService {

	private EmployeeDao employeeDao = new EmployeeDao();

	public void register(Employee employee) {
		Connection connection = null;
		try {
			connection = ConnectionProvider.getConnection();
			connection.setAutoCommit(false);

			employeeDao.insert(connection, employee);

			connection.commit();
		} catch (SQLException sqlException) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException rollbackException) {}
			}
			throw new RuntimeException(sqlException);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException closeException) {}
			}
		}
	}
}