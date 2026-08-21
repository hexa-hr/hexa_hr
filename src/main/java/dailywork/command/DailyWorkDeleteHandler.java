package dailywork.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dailywork.service.DailyWorkService;
import mvc.command.CommandHandler;

public class DailyWorkDeleteHandler implements CommandHandler {
	private DailyWorkService workService = new DailyWorkService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		int workId = Integer.parseInt(req.getParameter("workId"));
		boolean success = workService.deleteDailyWork(workId);

		res.setContentType("text/plain; charset=UTF-8");
		res.getWriter().write(success ? "success" : "fail");
		return null;
	}
}