package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

	public Employee getEmployee(int employeeId) {
		Connection conn = null;
		try {
			conn = jdbc.connection.ConnectionProvider.getConnection();
			return employeeDao.selectById(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException("사원 상세정보 조회 오류", e);
		} finally {
			jdbc.JdbcUtil.close(conn);
		}
	}

	// 🌟 반환 타입을 Integer로 변경
	public Integer register(Employee employee, EmployeeSalaryAccount account,
		List<Dependents> dependentsList, List<Degree> degreeList,
		Insurance insurance) {

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); // 트랜잭션 시작

			// 1. 사원 번호 발급 (empId 하나만 깔끔하게 사용!)
			Integer empId = employeeDao.getNextId(conn);
			employee.setEmployeeId(empId);

			// 2. 계좌 저장 (계좌가 먼저 들어가야 사원 테이블에 계좌번호를 연결할 수 있음)
			if (account != null && account.getBankName() != null && !account.getBankName().trim().isEmpty()) {
				Integer accId = accountDao.getNextId(conn);
				account.setAccountId(accId);
				accountDao.insert(conn, account);
				employee.setAccountId(accId); // 사원 상자에도 계좌 ID 담아주기
			}

			// 3. 사원 기본 정보 진짜로 저장!
			employeeDao.insert(conn, employee);

			// 4. 가족 저장
			if (dependentsList != null && !dependentsList.isEmpty()) {
				for (Dependents dep : dependentsList) {
					dep.setEmployeeId(empId); // 발급받은 사원번호 꼬리표 달기
					dependentsDao.insert(conn, dep);
				}
			}

			// 5. 학력 사항 저장 
			if (degreeList != null && !degreeList.isEmpty()) {
				for (Degree deg : degreeList) {
					deg.setEmployeeId(empId); // 사원번호 꼬리표 달기
					degreeDao.insert(conn, deg);
				}
			}

			// 6. 보험 정보 저장
			if (insurance != null && insurance.getInsuranceAgency() != null
				&& !insurance.getInsuranceAgency().trim().isEmpty()) {
				insurance.setEmployeeId(empId); // 사원번호 꼬리표 달기
				insuranceDao.insert(conn, insurance);
			}

			// 7. 모든 작업이 안전하게 끝났으면 딱 한 번만 커밋!
			conn.commit();

			// 🌟 8. 성공했으니 방금 만든 사원 번호를 핸들러에게 전달하며 끝내기!
			return empId;

		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("사원 및 부가정보 등록 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}
}