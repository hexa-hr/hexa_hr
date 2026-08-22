package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import employee.dao.DegreeDao;
import employee.dao.DependentsDao;
import employee.dao.EmployeeDao;
import employee.dao.EmployeeSalaryAccountDao;
import employee.dao.InsuranceDao;
import employee.model.Degree;
import employee.model.Dependents;
import employee.model.Employee;
import employee.model.EmployeeSalaryAccount;
import employee.model.Insurance;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeRegisterService {

	private EmployeeDao employeeDao = new EmployeeDao();
	private EmployeeSalaryAccountDao accountDao = new EmployeeSalaryAccountDao();
	private DependentsDao dependentsDao = new DependentsDao();
	private DegreeDao degreeDao = new DegreeDao();
	private InsuranceDao insuranceDao = new InsuranceDao();

	// 🌟 List<Insurance> 파라미터 적용
	public Integer register(Employee employee, EmployeeSalaryAccount account, List<Dependents> dependentsList,
		List<Degree> degreeList, List<Insurance> insuranceList) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			Integer newEmpId = employeeDao.getNextId(conn);
			employee.setEmployeeId(newEmpId);

			employeeDao.insert(conn, employee);

			account.setEmployeeId(newEmpId);
			accountDao.insert(conn, account);

			if (dependentsList != null && !dependentsList.isEmpty()) {
				for (Dependents dep : dependentsList) {
					dep.setEmployeeId(newEmpId);
					dependentsDao.insert(conn, dep);
				}
			}

			if (degreeList != null && !degreeList.isEmpty()) {
				for (Degree deg : degreeList) {
					deg.setEmployeeId(newEmpId);
					degreeDao.insert(conn, deg);
				}
			}

			// 🌟 체크된 개수만큼 반복하며 각각 독립된 행으로 INSERT
			if (insuranceList != null && !insuranceList.isEmpty()) {
				for (Insurance ins : insuranceList) {
					ins.setEmployeeId(newEmpId);
					insuranceDao.insert(conn, ins);
				}
			}

			conn.commit();
			return newEmpId;

		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("사원정보 1 등록 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public Employee getEmployee(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeDao.selectById(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException("사원 상세정보 조회 오류", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Map<String, Object>> getDepartments() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeDao.selectDepartments(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Map<String, Object>> getPositions() {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeDao.selectPositions(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}