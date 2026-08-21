package dailywork.command;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.service.DailyWorkService;
import mvc.command.CommandHandler;

public class DailyWorkListHandler implements CommandHandler {
	private DailyWorkService workService = new DailyWorkService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int empNo = Integer.parseInt(req.getParameter("empNo"));
		String yearMonth = req.getParameter("yearMonth");

		List<Map<String, Object>> list = workService.getDailyWorkList(empNo, yearMonth);

		res.setContentType("application/json; charset=UTF-8");
		StringBuilder json = new StringBuilder();
		json.append("[");
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			json.append("{").append("\"workId\":").append(map.get("workId")).append(",").append("\"workDate\":\"")
					.append(map.get("workDate")).append("\",").append("\"projectName\":\"")
					.append(map.get("projectName")).append("\",").append("\"fieldProjectId\":")
					.append(map.get("fieldProjectId")).append(",").append("\"dailyWage\":").append(map.get("dailyWage"))
					.append(",").append("\"paymentRate\":").append(map.get("paymentRate")).append(",")
					.append("\"incomeTax\":").append(map.get("incomeTax")).append(",").append("\"localTax\":")
					.append(map.get("localTax")).append(",").append("\"actualPayment\":")
					.append(map.get("actualPayment")).append("}");
			if (i < list.size() - 1)
				json.append(",");
		}
		json.append("]");
		res.getWriter().write(json.toString());
		return null;
	}
}