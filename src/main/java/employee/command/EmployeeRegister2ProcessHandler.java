package employee.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Appointment;
import employee.model.Certification;
import employee.model.Guarantor;
import employee.model.LanguageAbility;
import employee.model.Referrer;
import employee.model.Retirement;
import employee.model.RewardPenalty;
import employee.model.Training;
import employee.service.EmployeeRegister2Service;
import mvc.command.CommandHandler;

public class EmployeeRegister2ProcessHandler implements CommandHandler {

	private EmployeeRegister2Service service = new EmployeeRegister2Service();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");

		String empIdStr = request.getParameter("employeeId");
		if (empIdStr == null || empIdStr.trim().isEmpty())
			return null;
		Integer employeeId = Integer.valueOf(empIdStr);

		// 자격증
		String[] certNames = request.getParameterValues("certName");
		String[] acqDates = request.getParameterValues("certAcqDate");
		String[] orgs = request.getParameterValues("certIssuer");
		String[] certNums = request.getParameterValues("certNumber");
		String[] remarks = request.getParameterValues("certRemarks");
		List<Certification> certList = new ArrayList<>();
		if (certNames != null) {
			for (int i = 0; i < certNames.length; i++) {
				if (certNames[i] != null && !certNames[i].trim().isEmpty()) {
					certList.add(new Certification(null, employeeId, certNames[i], parseDate(safeGet(acqDates, i)),
						safeGet(orgs, i), safeGet(certNums, i), safeGet(remarks, i)));
				}
			}
		}

		// 어학
		String[] langNames = request.getParameterValues("langName");
		String[] langTests = request.getParameterValues("langTest");
		String[] langScores = request.getParameterValues("langScore");
		String[] langAcqDates = request.getParameterValues("langAcqDate");
		String[] langReadings = request.getParameterValues("langReading");
		String[] langWritings = request.getParameterValues("langWriting");
		String[] langSpeakings = request.getParameterValues("langSpeaking");
		List<LanguageAbility> langList = new ArrayList<>();
		if (langNames != null) {
			for (int i = 0; i < langNames.length; i++) {
				if (langNames[i] != null && !langNames[i].trim().isEmpty()) {
					langList.add(new LanguageAbility(null, employeeId, langNames[i], safeGet(langTests, i),
						parseInteger(safeGet(langScores, i)), parseDate(safeGet(langAcqDates, i)),
						safeGet(langReadings, i), safeGet(langWritings, i), safeGet(langSpeakings, i)));
				}
			}
		}

		// 교육훈련
		String[] trTypes = request.getParameterValues("trainingType");
		List<Training> trainingList = new ArrayList<>();
		if (trTypes != null) {
			String[] trNames = request.getParameterValues("trainingName");
			String[] trStarts = request.getParameterValues("trainingStartDate");
			String[] trEnds = request.getParameterValues("trainingEndDate");
			String[] trOrgs = request.getParameterValues("trainingOrganization");
			String[] trCosts = request.getParameterValues("trainingCost");
			String[] trRefs = request.getParameterValues("refundableTrainingCost");
			for (int i = 0; i < trTypes.length; i++) {
				if (trTypes[i] != null && !trTypes[i].trim().isEmpty()) {
					trainingList.add(new Training(null, employeeId, trTypes[i], safeGet(trNames, i),
						parseDate(safeGet(trStarts, i)), parseDate(safeGet(trEnds, i)), safeGet(trOrgs, i),
						parseLong(safeGet(trCosts, i)), parseLong(safeGet(trRefs, i))));
				}
			}
		}

		// 상벌
		String[] rwTypes = request.getParameterValues("rewardPenaltyType");
		List<RewardPenalty> rewardList = new ArrayList<>();
		if (rwTypes != null) {
			String[] rwNames = request.getParameterValues("rewardPenaltyName");
			String[] rwGivers = request.getParameterValues("rewardPenaltyGiver");
			String[] rwDates = request.getParameterValues("rewardPenaltyDate");
			String[] rwDescs = request.getParameterValues("rewardPenaltyDescription");
			String[] rwRems = request.getParameterValues("remarks2");
			for (int i = 0; i < rwTypes.length; i++) {
				if (rwTypes[i] != null && !rwTypes[i].trim().isEmpty()) {
					rewardList.add(new RewardPenalty(null, employeeId, rwTypes[i], safeGet(rwNames, i),
						safeGet(rwGivers, i), parseDate(safeGet(rwDates, i)), safeGet(rwDescs, i), safeGet(rwRems, i)));
				}
			}
		}

		// 발령
		String[] apTypes = request.getParameterValues("appointmentType");
		List<Appointment> apptList = new ArrayList<>();
		if (apTypes != null) {
			String[] apDates = request.getParameterValues("appointmentDate");
			String[] apDepts = request.getParameterValues("departmentId");
			String[] apPos = request.getParameterValues("positionId");
			String[] apPosTypes = request.getParameterValues("positionType");
			String[] apRems = request.getParameterValues("remarks3");
			for (int i = 0; i < apTypes.length; i++) {
				if (apTypes[i] != null && !apTypes[i].trim().isEmpty()) {
					apptList.add(new Appointment(null, employeeId, apTypes[i], parseDate(safeGet(apDates, i)),
						parseInteger(safeGet(apDepts, i)), parseInteger(safeGet(apPos, i)), safeGet(apPosTypes, i),
						safeGet(apRems, i)));
				}
			}
		}

		// 추천인
		String[] refNames = request.getParameterValues("referrerName");
		List<Referrer> referrerList = new ArrayList<>();
		if (refNames != null) {
			String[] refRels = request.getParameterValues("referrerRelationship");
			String[] refComps = request.getParameterValues("referrerCompanyName");
			String[] refPos = request.getParameterValues("referrerPosition");
			String[] refPhones = request.getParameterValues("referrerPhoneNumber");
			for (int i = 0; i < refNames.length; i++) {
				if (refNames[i] != null && !refNames[i].trim().isEmpty()) {
					referrerList.add(new Referrer(null, employeeId, refNames[i], safeGet(refRels, i),
						safeGet(refComps, i), safeGet(refPos, i), safeGet(refPhones, i)));
				}
			}
		}

		// 신원보증
		String[] guaNames = request.getParameterValues("guaName");
		String[] guaRels = request.getParameterValues("guaRelation");
		String[] guaRrns = request.getParameterValues("guaRrn");
		String[] guaAmounts = request.getParameterValues("guaAmount");
		List<Guarantor> guarantorList = new ArrayList<>();
		if (guaNames != null) {
			for (int i = 0; i < guaNames.length; i++) {
				if (guaNames[i] != null && !guaNames[i].trim().isEmpty()) {
					guarantorList.add(new Guarantor(null, employeeId, guaNames[i], safeGet(guaRels, i),
						safeGet(guaRrns, i), parseLong(safeGet(guaAmounts, i)), null, null, null));
				}
			}
		}

		// 퇴직
		Retirement retirement = null;
		String retType = request.getParameter("retirementType");
		if (retType != null && !retType.trim().isEmpty()) {
			retirement = new Retirement(
				employeeId, retType, parseDate(request.getParameter("retirementDate")),
				request.getParameter("retirementReason"), request.getParameter("retirementContact"),
				parseLong(request.getParameter("severancePay")));
		}

		try {
			service.register2(employeeId, certList, langList, trainingList, rewardList,
				apptList, referrerList, guarantorList, retirement);
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>parent.alert('사원 부가정보가 성공적으로 저장되었습니다.');</script>");
			return null;
		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>parent.alert('등록 실패: " + e.getMessage() + "');</script>");
			return null;
		}
	}

	private Date parseDate(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private String safeGet(String[] arr, int index) {
		if (arr != null && arr.length > index)
			return arr[index];
		return null;
	}

	private Integer parseInteger(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Integer.valueOf(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private Long parseLong(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Long.valueOf(val.trim());
		} catch (Exception e) {
			return null;
		}
	}
}