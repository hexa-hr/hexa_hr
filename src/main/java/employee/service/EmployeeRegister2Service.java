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
// 🌟 RetirementDao import 제거됨
import employee.dao.RewardPenaltyDao;
import employee.dao.TrainingDao;
import employee.model.Appointment;
import employee.model.Certification;
import employee.model.Guarantor;
import employee.model.LanguageAbility;
import employee.model.Referrer;
// 🌟 Retirement import 제거됨
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
	// 🌟 RetirementDao 필드 제거됨
	private EmployeeDao employeeDao = new EmployeeDao();

	// 🌟 매개변수 마지막의 Retirement retirement 제거됨
	public void register2(Integer employeeId,
		List<Certification> certList, List<LanguageAbility> langList,
		List<Training> trainingList, List<RewardPenalty> rewardList,
		List<Appointment> apptList, List<Referrer> referrerList,
		List<Guarantor> guarantorList) {

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			deleteOldData(conn, "certification", employeeId);
			deleteOldData(conn, "language_ability", employeeId);
			deleteOldData(conn, "training", employeeId);
			deleteOldData(conn, "reward_penalty", employeeId);
			deleteOldData(conn, "appointment", employeeId);
			deleteOldData(conn, "referrer", employeeId);
			deleteOldData(conn, "guarantor", employeeId);
			// 🌟 retirement delete 처리 제거됨

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

			// 🌟 retirement insert 조건문 제거됨

			conn.commit();
		} catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException("부가정보 등록 중 오류 발생", e);
		} finally {
			JdbcUtil.close(conn);
		}
	}

	private void deleteOldData(Connection conn, String tableName, int employeeId) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE employee_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, employeeId);
			pstmt.executeUpdate();
		}
	}

	public List<Certification> getCertifications(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return certificationDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<LanguageAbility> getLanguageAbilities(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return languageDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<Training> getTrainings(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return trainingDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<RewardPenalty> getRewardPenalties(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return rewardDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<Appointment> getAppointments(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return apptDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<Referrer> getReferrers(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return referrerDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	public List<Guarantor> getGuarantors(int employeeId) throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return guarantorDao.selectAllByEmployeeId(conn, employeeId);
		}
	}

	// 🌟 getRetirement 메서드 전체 제거됨

	public List<Map<String, Object>> getDepartments() throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return employeeDao.selectDepartments(conn);
		}
	}

	public List<Map<String, Object>> getPositions() throws SQLException {
		try (Connection conn = ConnectionProvider.getConnection()) {
			return employeeDao.selectPositions(conn);
		}
	}
}