package employee.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import employee.dao.AppointmentDao;
import employee.dao.CareerDao;
import employee.dao.CertificationDao;
import employee.dao.GuarantorDao; // 🌟 보증인 DAO 임포트
import employee.dao.LanguageAbilityDao;
import employee.dao.MilitaryServiceDao;
import employee.dao.ReferrerDao;
import employee.dao.RetirementDao;
import employee.dao.RewardPenaltyDao;
import employee.dao.TrainingDao;
import employee.model.Appointment;
import employee.model.Career;
import employee.model.Certification;
import employee.model.Guarantor; // 🌟 보증인 모델 임포트
import employee.model.LanguageAbility;
import employee.model.MilitaryService;
import employee.model.Referrer;
import employee.model.Retirement;
import employee.model.RewardPenalty;
import employee.model.Training;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class EmployeeRegister2Service {

	private CareerDao careerDao = new CareerDao();
	private MilitaryServiceDao militaryDao = new MilitaryServiceDao();
	private CertificationDao certificationDao = new CertificationDao();
	private LanguageAbilityDao languageDao = new LanguageAbilityDao();
	private TrainingDao trainingDao = new TrainingDao();
	private RewardPenaltyDao rewardDao = new RewardPenaltyDao();
	private AppointmentDao apptDao = new AppointmentDao();
	private ReferrerDao referrerDao = new ReferrerDao();
	private GuarantorDao guarantorDao = new GuarantorDao(); // 🌟 보증인 DAO 추가
	private RetirementDao retirementDao = new RetirementDao();

	// 🌟 파라미터에 List<Guarantor> guarantorList 쏙 추가!
	public void register2(Integer employeeId, List<Career> careerList,
		List<MilitaryService> militaryList, List<Certification> certList,
		List<LanguageAbility> langList,
		List<Training> trainingList, List<RewardPenalty> rewardList,
		List<Appointment> apptList, List<Referrer> referrerList,
		List<Guarantor> guarantorList, Retirement retirement) {

		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);

			if (careerList != null && !careerList.isEmpty()) {
				for (Career career : careerList)
					careerDao.insert(conn, career);
			}
			if (militaryList != null && !militaryList.isEmpty()) {
				for (MilitaryService mil : militaryList)
					militaryDao.insert(conn, mil);
			}
			if (certList != null && !certList.isEmpty()) {
				for (Certification cert : certList)
					certificationDao.insert(conn, cert);
			}
			if (langList != null && !langList.isEmpty()) {
				for (LanguageAbility lang : langList)
					languageDao.insert(conn, lang);
			}
			if (trainingList != null) {
				for (Training t : trainingList)
					trainingDao.insert(conn, t);
			}
			if (rewardList != null) {
				for (RewardPenalty r : rewardList)
					rewardDao.insert(conn, r);
			}
			if (apptList != null) {
				for (Appointment a : apptList)
					apptDao.insert(conn, a);
			}
			if (referrerList != null) {
				for (Referrer r : referrerList)
					referrerDao.insert(conn, r);
			}

			// 🌟 보증인 등록 로직 추가
			if (guarantorList != null && !guarantorList.isEmpty()) {
				for (Guarantor g : guarantorList) {
					guarantorDao.insert(conn, g);
				}
			}

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
}