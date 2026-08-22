package employee.command;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.Certification;
import employee.model.LanguageAbility;
import employee.service.EmployeeRegister2Service;
import mvc.command.CommandHandler;

public class EmployeeRegister2Handler implements CommandHandler {

	private EmployeeRegister2Service register2Service = new EmployeeRegister2Service();

	@Override
	public String process(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// GET 요청: 사원정보 1에서 넘어왔을 때 또는 메뉴를 클릭했을 때 화면 보여주기
		if (request.getMethod().equalsIgnoreCase("GET")) {
			String employeeId = request.getParameter("employeeId");
			String tab = request.getParameter("tab");

			if (tab == null || tab.trim().isEmpty()) {
				tab = "cert";
			}

			request.setAttribute("employeeId", employeeId);
			request.setAttribute("tab", tab);

			return "/WEB-INF/view/employee/employeeRegister2.jsp";
		}

		// POST 요청: 저장 버튼을 눌렀을 때 DB에 Insert 처리
		if (request.getMethod().equalsIgnoreCase("POST")) {
			request.setCharacterEncoding("UTF-8");

			Integer employeeId = parseInt(request.getParameter("employeeId"));

			// ==========================================
			// 1. 자격/면허 데이터 배열로 받기
			// ==========================================
			String[] certNames = request.getParameterValues("certName");
			String[] certAcqDates = request.getParameterValues("certAcqDate");
			String[] certIssuers = request.getParameterValues("certIssuer");
			String[] certNumbers = request.getParameterValues("certNumber");
			String[] certRemarks = request.getParameterValues("certRemarks");

			List<Certification> certList = new ArrayList<>();
			if (certNames != null) {
				for (int i = 0; i < certNames.length; i++) {
					// 빈칸이 아닌 진짜 데이터가 입력된 줄만 저장
					if (certNames[i] != null && !certNames[i].trim().isEmpty()) {
						Certification cert = new Certification(
							null, employeeId, certNames[i], parseDate(certAcqDates[i]),
							certIssuers[i], certNumbers[i], certRemarks[i]);
						certList.add(cert);
					}
				}
			}

			// ==========================================
			// 2. 어학능력 데이터 배열로 받기
			// ==========================================
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
						LanguageAbility lang = new LanguageAbility(
							null, employeeId, langNames[i], langTests[i],
							parseInt(langScores[i]), parseDate(langAcqDates[i]),
							langReadings[i], langWritings[i], langSpeakings[i]);
						langList.add(lang);
					}
				}
			}

			// ==========================================
			// 3. 서비스 호출하여 DB에 일괄 저장
			// ==========================================
			try {
				// (경력, 병역 등 나머지 리스트는 일단 null로 처리! 나중에 같은 방식으로 추가하면 됩니다)
				register2Service.register2(employeeId, null, null, certList, langList, null, null, null, null, null);

				// 화면(숨겨진 iframe)에 성공 메시지 띄우고 부모 창 새로고침
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("parent.alert('자격증 및 어학능력이 성공적으로 저장되었습니다!');");
				// out.println("parent.location.reload();"); // 필요시 주석 해제하여 새로고침
				out.println("</script>");
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>parent.alert('저장 중 오류가 발생했습니다. 확인 후 다시 시도해주세요.');</script>");
				out.flush();
			}
			return null;
		}
		return null;
	}

	// 문자열 -> Integer 안전 변환 헬퍼 메서드
	private Integer parseInt(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			return Integer.parseInt(val.trim());
		} catch (Exception e) {
			return null;
		}
	}

	// 문자열 -> Date 안전 변환 헬퍼 메서드
	private Date parseDate(String val) {
		if (val == null || val.trim().isEmpty())
			return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(val.trim());
		} catch (Exception e) {
			return null;
		}
	}
}