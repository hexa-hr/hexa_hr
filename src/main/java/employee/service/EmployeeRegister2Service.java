package employee.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import employee.dao.AppointmentDao;
import employee.dao.CertificationDao;
import employee.dao.EmployeeDao;
import employee.dao.GuarantorDao;
import employee.dao.LanguageAbilityDao;
import employee.dao.ReferrerDao;
import employee.dao.RetirementDao;
import employee.dao.RewardPenaltyDao;
import employee.dao.TrainingDao;
import employee.model.Appointment;
import employee.model.Certification;
import employee.model.Guarantor;
import employee.model.LanguageAbility;
import employee.model.Referrer;
import employee.model.Retirement;
import employee.model.RewardPenalty;
import employee.model.Training;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeRegister2Service {

	private CertificationDao certificationDao = new CertificationDao();
	private LanguageAbilityDao languageDao = new LanguageAbilityDao();
	private TrainingDao trainingDao = new TrainingDao();
	private RewardPenaltyDao rewardDao = new RewardPenaltyDao();
	private AppointmentDao apptDao = new AppointmentDao();
	private ReferrerDao referrerDao = new ReferrerDao();
	private GuarantorDao guarantorDao = new GuarantorDao();
	private RetirementDao retirementDao = new RetirementDao();
	private EmployeeDao employeeDao = new EmployeeDao();

	public void register2(Integer employeeId,
		List<Certification> certList, List<LanguageAbility> langList,
		List<Training> trainingList, List<RewardPenalty> rewardList,
		List<Appointment> apptList, List<Referrer> referrerList,
		List<Guarantor> guarantorList, Retirement retirement) {

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			// 🌟 [핵심 해결책!] 기존에 저장되어 있던 이 사원의 부가정보를 싹 다 지웁니다.
			deleteOldData(conn, "certification", employeeId);
			deleteOldData(conn, "language_ability", employeeId);
			deleteOldData(conn, "training", employeeId);
			deleteOldData(conn, "reward_penalty", employeeId);
			deleteOldData(conn, "appointment", employeeId);
			deleteOldData(conn, "referrer", employeeId);
			deleteOldData(conn, "guarantor", employeeId);
			deleteOldData(conn, "retirement", employeeId);

			// 🌟 화면에서 넘어온 최신 데이터로 다시 깔끔하게 INSERT 합니다.
			if (certList != null)
				for (Certification c : certList)
					certificationDao.insert(conn, c);
			if (langList != null)
				for (LanguageAbility l : langList)
					languageDao.insert(conn, l);
			if (trainingList != null)
				for (Training t : trainingList)
					trainingDao.insert(conn, t);
			if (rewardList != null)
				for (RewardPenalty r : rewardList)
					rewardDao.insert(conn, r);
			if (apptList != null)
				for (Appointment a : apptList)
					apptDao.insert(conn, a);
			if (referrerList != null)
				for (Referrer r : referrerList)
					referrerDao.insert(conn, r);
			if (guarantorList != null)
				for (Guarantor g : guarantorList)
					guarantorDao.insert(conn, g);

			if (retirement != null && retirement.getRetirementType() != null
				&& !retirement.getRetirementType().isEmpty()) {
				retirementDao.insert(conn, retirement);
			}

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("부가정보 등록 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	// 🌟 여러 DAO를 열어서 수정하는 번거로움을 없애주는 마법의 삭제 공통 메서드
	private void deleteOldData(Connection conn, String tableName, int employeeId) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			pstmt.executeUpdate();
		}
	}

	public List<Certification> getCertifications(int employeeId) throws SQLException {
		return certificationDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<LanguageAbility> getLanguageAbilities(int employeeId) throws SQLException {
		return languageDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<Training> getTrainings(int employeeId) throws SQLException {
		return trainingDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<RewardPenalty> getRewardPenalties(int employeeId) throws SQLException {
		return rewardDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<Appointment> getAppointments(int employeeId) throws SQLException {
		return apptDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<Referrer> getReferrers(int employeeId) throws SQLException {
		return referrerDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<Guarantor> getGuarantors(int employeeId) throws SQLException {
		return guarantorDao.selectAllByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public Retirement getRetirement(int employeeId) throws SQLException {
		return retirementDao.selectByEmployeeId(ConnectionProvider.getConnection(), employeeId);
	}

	public List<Map<String, Object>> getDepartments() throws SQLException {
		return employeeDao.selectDepartments(ConnectionProvider.getConnection());
	}

	public List<Map<String, Object>> getPositions() throws SQLException {
		return employeeDao.selectPositions(ConnectionProvider.getConnection());
	}
}