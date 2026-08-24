package employee.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Appointment;
import employee.model.Career;
import employee.model.Certification;
import employee.model.MilitaryService;
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

		// 1. 사원번호 확인
		String empIdStr = request.getParameter("employeeId");
		if (empIdStr == null || empIdStr.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/employee/register.do");
			return null;
		}
		Integer employeeId = Integer.valueOf(empIdStr);

		// ==========================================
		// 2. 경력(Career) 수집
		// ==========================================
		String[] companyNames = request.getParameterValues("companyName");
		String[] startDates = request.getParameterValues("startDate");
		String[] endDates = request.getParameterValues("endDate");
		String[] employmentPeriods = request.getParameterValues("employmentPeriod");
		String[] finalPositions = request.getParameterValues("finalPosition");
		String[] responsibilities = request.getParameterValues("responsibilities");
		String[] reasonForResignations = request.getParameterValues("reasonForResignation");

		List<Career> careerList = new ArrayList<>();
		if (companyNames != null) {
			for (int i = 0; i < companyNames.length; i++) {
				if (companyNames[i] != null && !companyNames[i].trim().isEmpty()) {
					// 🌟 NPE 방지를 위해 날짜 파싱에도 safeGet을 씌웠어!
					Career career = new Career(null, employeeId, companyNames[i],
						parseDate(safeGet(startDates, i)),
						parseDate(safeGet(endDates, i)),
						safeGet(employmentPeriods, i), safeGet(finalPositions, i), safeGet(responsibilities, i),
						safeGet(reasonForResignations, i));
					careerList.add(career);
				}
			}
		}

		// ==========================================
		// 3. 병역(MilitaryService) 수집
		// ==========================================
		String[] serviceTypes = request.getParameterValues("serviceType");
		String[] branches = request.getParameterValues("branch");
		String[] servicePeriod1s = request.getParameterValues("servicePeriod1");
		String[] servicePeriod2s = request.getParameterValues("servicePeriod2");
		String[] finalRanks = request.getParameterValues("finalRank");
		String[] department1s = request.getParameterValues("department1");
		String[] exemptionReasons = request.getParameterValues("exemptionReason");

		List<MilitaryService> militaryList = new ArrayList<>();
		if (serviceTypes != null) {
			for (int i = 0; i < serviceTypes.length; i++) {
				if (serviceTypes[i] != null && !serviceTypes[i].trim().isEmpty()) {
					MilitaryService mil = new MilitaryService(null, employeeId, serviceTypes[i], safeGet(branches, i),
						parseDate(safeGet(servicePeriod1s, i)), parseDate(safeGet(servicePeriod2s, i)),
						safeGet(finalRanks, i),
						safeGet(department1s, i), safeGet(exemptionReasons, i));
					militaryList.add(mil);
				}
			}
		}

		// ==========================================
		// 4. 자격증(Certification) 수집
		// ==========================================
		String[] certNames = request.getParameterValues("certificationName");
		String[] acqDates = request.getParameterValues("acquisitionDate");
		String[] orgs = request.getParameterValues("issuingOrganization");
		String[] certNums = request.getParameterValues("certificationNumber");
		String[] remarks = request.getParameterValues("remarks1");

		List<Certification> certList = new ArrayList<>();
		if (certNames != null) {
			for (int i = 0; i < certNames.length; i++) {
				if (certNames[i] != null && !certNames[i].trim().isEmpty()) {
					Certification cert = new Certification(null, employeeId, certNames[i],
						parseDate(safeGet(acqDates, i)),
						safeGet(orgs, i), safeGet(certNums, i), safeGet(remarks, i));
					certList.add(cert);
				}
			}
		}

		// ==========================================
		// 5. 교육훈련(Training) 수집
		// ==========================================
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

		// ==========================================
		// 6. 상벌(RewardPenalty) 수집
		// ==========================================
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

		// ==========================================
		// 7. 발령(Appointment) 수집
		// ==========================================
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

		// ==========================================
		// 8. 추천인(Referrer) 수집
		// ==========================================
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

		// ==========================================
		// 9. 퇴직(Retirement) 수집
		// ==========================================
		Retirement retirement = null;
		String retType = request.getParameter("retirementType");
		if (retType != null && !retType.trim().isEmpty()) {
			retirement = new Retirement(
				employeeId,
				retType,
				parseDate(request.getParameter("retirementDate")),
				request.getParameter("retirementReason"),
				request.getParameter("retirementContact"),
				parseLong(request.getParameter("severancePay")));
		}

		// ==========================================
		// 10. DB 저장 실행
		// ==========================================
		try {
			service.register2(employeeId, careerList, militaryList, certList, trainingList, rewardList, apptList,
				referrerList, retirement);

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>parent.alert('사원 부가정보가 성공적으로 저장되었습니다.');</script>");
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().println("<script>parent.alert('등록 실패: " + e.getMessage() + "');</script>");
			return null;
		}
	}

	// 날짜 파싱 유틸리티
	private Date parseDate(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	// 배열 Null 방지 유틸리티
	private String safeGet(String[] arr, int index) {
		if (arr != null && arr.length > index)
			return arr[index];
		return null;
	}

	// Integer 파싱 유틸리티 (에러 방지용)
	private Integer parseInteger(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Integer.valueOf(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	// Long 파싱 유틸리티 (에러 방지용)
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