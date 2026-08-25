package employee.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import employee.dao.CareerDao;
import employee.dao.DegreeDao;
import employee.dao.DependentsDao;
import employee.dao.EmployeeDao;
import employee.dao.InsuranceDao;
import employee.dao.MilitaryServiceDao;
import employee.model.Career;
import employee.model.Degree;
import employee.model.Dependents;
import employee.model.Employee;
import employee.model.Insurance;
import employee.model.MilitaryService;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import master.dao.ContactPersonInfoDao;

public class EmployeeRegisterService {

	private EmployeeDao employeeDao = new EmployeeDao();
	private DependentsDao dependentsDao = new DependentsDao();
	private DegreeDao degreeDao = new DegreeDao();
	private InsuranceDao insuranceDao = new InsuranceDao();
	private CareerDao careerDao = new CareerDao();
	private MilitaryServiceDao militaryDao = new MilitaryServiceDao();
	private ContactPersonInfoDao contactDao = new ContactPersonInfoDao();

	public Integer register(Employee employee, List<Dependents> dependentsList,
		List<Degree> degreeList, List<Insurance> insuranceList, List<Career> careerList,
		List<MilitaryService> militaryList) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			// 🌟 [핵심] 클라이언트 요청 파라미터 무시하고 서버에서 값 강제 지정
			employee.setCompanyId(1);
			employee.setAccountId(null);

			// company_id=1인 담당자 중 가장 작은 person_id 가져오기
			Integer minPersonId = contactDao.selectMinPersonIdByCompanyId(conn, 1);
			employee.setPersonId(minPersonId); // 담당자가 없으면 자연스럽게 null 세팅됨

			Integer empId = employee.getEmployeeId();

			if (empId == null || empId == 0) {
				// 신규 등록
				employee.setStatus("재직"); // 신규 사원은 상태를 '재직'으로 강제 고정

				empId = employeeDao.getNextId(conn);
				employee.setEmployeeId(empId);
				employeeDao.insert(conn, employee);
			} else {
				// 기존 사원 수정 (UPDATE)
				// DB에서 기존 사원 정보를 조회하여 상태(status) 값을 그대로 유지시킴
				Employee existingEmp = employeeDao.selectById(conn, empId);
				if (existingEmp != null) {
					employee.setStatus(existingEmp.getStatus());
				}

				updateEmployee(conn, employee);

				// 기존 다중 행 표 데이터 싹 다 지우기 (Delete & Insert 방식)
				deleteOldData(conn, "dependents", empId);
				deleteOldData(conn, "degree", empId);
				deleteOldData(conn, "insurance", empId);
				deleteOldData(conn, "career", empId);
				deleteOldData(conn, "military_service", empId);
			}

			// 3. 부양가족 (공통 INSERT)
			if (dependentsList != null) {
				for (Dependents dep : dependentsList) {
					dep.setEmployeeId(empId);
					dependentsDao.insert(conn, dep);
				}
			}

			// 4. 학력
			if (degreeList != null) {
				for (Degree deg : degreeList) {
					deg.setEmployeeId(empId);
					degreeDao.insert(conn, deg);
				}
			}

			// 5. 보험
			if (insuranceList != null) {
				for (Insurance ins : insuranceList) {
					ins.setEmployeeId(empId);
					insuranceDao.insert(conn, ins);
				}
			}

			// 6. 경력
			if (careerList != null) {
				for (Career c : careerList) {
					Career newCareer = new Career(
						null, empId, c.getCompanyName(), c.getStartDate(), c.getEndDate(),
						c.getEmploymentPeriod(), c.getFinalPosition(), c.getResponsibilities(),
						c.getReasonForResignation());
					careerDao.insert(conn, newCareer);
				}
			}

			// 7. 병역
			if (militaryList != null) {
				for (MilitaryService mil : militaryList) {
					MilitaryService newMil = new MilitaryService(
						null, empId, mil.getServiceType(), mil.getBranch(),
						mil.getServicePeriod1(), mil.getServicePeriod2(), mil.getFinalRank(),
						mil.getDepartment1(), mil.getExemptionReason());
					militaryDao.insert(conn, newMil);
				}
			}

			conn.commit();
			return empId;
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("사원정보 1 등록/수정 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 🌟 다중 행 데이터 삭제 공통 메서드
	private void deleteOldData(Connection conn, String tableName, int employeeId) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			pstmt.executeUpdate();
		}
	}

	// 🌟 사원 인적사항 단일 행 UPDATE 메서드
	private void updateEmployee(Connection conn, Employee emp) throws SQLException {
		String sql = "UPDATE employee SET account_id=NULL, employment_type=?, korean_name=?, english_name=?, hire_date=?, resignation_date=?, "
			+ "department_id=?, position_id=?, foreign_or_domestic=?, resident_number1=?, resident_number2=?, "
			+ "address=?, tel_phone=?, mobile=?, email=?, sns=?, other_details=?, status=?, basic_pay=? "
			+ "WHERE employee_id=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, emp.getEmploymentType());
			pstmt.setString(2, emp.getKoreanName());
			pstmt.setString(3, emp.getEnglishName());
			pstmt.setDate(4, emp.getHireDate() != null ? new java.sql.Date(emp.getHireDate().getTime()) : null);
			pstmt.setDate(5,
				emp.getResignationDate() != null ? new java.sql.Date(emp.getResignationDate().getTime()) : null);
			if (emp.getDepartmentId() != null)
				pstmt.setInt(6, emp.getDepartmentId());
			else
				pstmt.setNull(6, java.sql.Types.INTEGER);
			if (emp.getPositionId() != null)
				pstmt.setInt(7, emp.getPositionId());
			else
				pstmt.setNull(7, java.sql.Types.INTEGER);
			pstmt.setString(8, emp.getForeignOrDomestic());
			pstmt.setString(9, emp.getResidentNumber1());
			pstmt.setString(10, emp.getResidentNumber2());
			pstmt.setString(11, emp.getAddress());
			pstmt.setString(12, emp.getTelPhone());
			pstmt.setString(13, emp.getMobile());
			pstmt.setString(14, emp.getEmail());
			pstmt.setString(15, emp.getSns());
			pstmt.setString(16, emp.getOtherDetails());
			pstmt.setString(17, emp.getStatus());
			if (emp.getBasicPay() != null)
				pstmt.setLong(18, emp.getBasicPay());
			else
				pstmt.setNull(18, java.sql.Types.NUMERIC);
			pstmt.setInt(19, emp.getEmployeeId());
			pstmt.executeUpdate();
		}
	}

	public Employee getEmployee(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return employeeDao.selectById(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Dependents> getDependents(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return dependentsDao.selectByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Degree> getDegrees(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return degreeDao.selectAllByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Insurance> getInsurances(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return insuranceDao.selectAllByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<Career> getCareers(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return careerDao.selectAllByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	public List<MilitaryService> getMilitaryServices(int employeeId) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			return militaryDao.selectAllByEmployeeId(conn, employeeId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
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