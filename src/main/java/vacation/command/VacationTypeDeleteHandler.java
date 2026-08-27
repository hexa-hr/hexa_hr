package vacation.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import vacation.service.DeleteVacationTypeService;

public class VacationTypeDeleteHandler implements CommandHandler {

	private DeleteVacationTypeService deleteService = new DeleteVacationTypeService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (!req.getMethod().equalsIgnoreCase("POST")) {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// 1. パラメータ 受信（PK値）
		String idStr = req.getParameter("vacationTypeId");

		// 2. パラメータの検証と削除処理
		if (idStr != null && !idStr.trim().isEmpty()) {
			int vacationTypeId = Integer.parseInt(idStr);
			try {
				deleteService.delete(vacationTypeId);
			} catch (IllegalStateException e) {
				// [追加] 使用中のため削除できない場合、エラーパラメータと共にリダイレクト
				res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do?error=inUse");
				return null;
			}
		}

		// 3. 処理後、リストページにリダイレクト
		res.sendRedirect(req.getContextPath() + "/vacationTypeSetting.do");
		return null;
	}

	// 該当の休暇項目が使用中かどうかを確認するメソッド
	public boolean isUsedInVacationDays(Connection conn, int vacationTypeId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM vacation_days WHERE vacation_type_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, vacationTypeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0; // 1件以上存在する場合は true (使用中)
				}
			}
		}
		return false;
	}
}