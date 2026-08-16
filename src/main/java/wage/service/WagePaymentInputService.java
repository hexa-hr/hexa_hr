package wage.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import attendance.dao.AttendanceDao;
import employee.dao.EmployeeDao;
import jdbc.connection.ConnectionProvider;
import master.dao.WageTypeDao;
import master.model.WageType;
import wage.model.WagePaymentCalculationItem;

public class WagePaymentInputService {

	private WageTypeDao wageTypeDao = new WageTypeDao();
	private AttendanceDao attendanceDao = new AttendanceDao();
	private EmployeeDao employeeDao = new EmployeeDao();

	public List<WagePaymentCalculationItem> getInitialItems(
		Integer employeeId,
		Date settlementStartDate,
		Date settlementEndDate) {

		if (employeeId == null || employeeId <= 0) {
			throw new IllegalArgumentException(
				"사원 정보가 올바르지 않습니다.");
		}

		if (settlementStartDate == null
			|| settlementEndDate == null) {

			throw new IllegalArgumentException(
				"정산기간이 올바르지 않습니다.");
		}

		if (settlementStartDate.after(settlementEndDate)) {
			throw new IllegalArgumentException(
				"정산 시작일은 종료일보다 늦을 수 없습니다.");
		}

		try (Connection conn = ConnectionProvider.getConnection()) {

			String employmentType = employeeDao.selectEmploymentType(
				conn,
				employeeId);

			if (employmentType == null) {
				throw new IllegalArgumentException(
					"존재하지 않는 사원입니다.");
			}

			String wageCategory = determineWageCategory(employmentType);

			List<WageType> wageTypes = wageTypeDao.selectActiveWageTypes(conn);

			List<WagePaymentCalculationItem> result = new ArrayList<>();

			for (WageType wageType : wageTypes) {

				if (!isAvailableWageType(
					wageCategory,
					wageType)) {
					continue;
				}

				long wageValue = 0L;

				if ("P".equals(wageType.getItemType())) {

					String linkType = wageType.getAttendanceOrLumpsum();

					String linkContent = wageType.getAttendanceOrLumpsumContent();

					// 근태연결
					if ("근태연결".equals(linkType)
						&& linkContent != null
						&& !linkContent.trim().isEmpty()) {

						wageValue = attendanceDao.selectLinkedAllowanceAmount(
							conn,
							employeeId,
							linkContent,
							settlementStartDate,
							settlementEndDate);

						// 일괄지급
					} else if ("일괄지급".equals(linkType)
						&& linkContent != null
						&& !linkContent.trim().isEmpty()) {

						try {
							wageValue = Long.parseLong(linkContent.trim());

						} catch (NumberFormatException e) {

							throw new IllegalStateException(
								"일괄지급 금액이 올바르지 않습니다: "
									+ wageType.getWageTypeName(),
								e);
						}
					}
				}

				result.add(
					new WagePaymentCalculationItem(
						wageType.getWageTypeId(),
						wageType.getWageTypeName(),
						wageType.getItemType(),
						wageType.getTaxableYn(),
						wageValue));
			}

			return result;

		} catch (SQLException e) {

			throw new RuntimeException(
				"급여입력 초기값 조회 중 데이터베이스 오류가 발생했습니다.",
				e);
		}
	}

	private String determineWageCategory(
		String employmentType) {

		if ("임시직".equals(employmentType)) {
			return "BUSINESS";
		}

		if ("일용직".equals(employmentType)) {
			return "DAILY";
		}

		return "WORKER";
	}

	private boolean isAvailableWageType(
		String wageCategory,
		WageType wageType) {

		String itemType = wageType.getItemType();
		String wageTypeName = wageType.getWageTypeName();

		if ("WORKER".equals(wageCategory)) {

			if ("P".equals(itemType)
				&& ("사업소득".equals(wageTypeName)
					|| "일용급여".equals(wageTypeName))) {

				return false;
			}

			return true;
		}

		if ("BUSINESS".equals(wageCategory)) {

			if ("P".equals(itemType)) {
				return "사업소득".equals(wageTypeName);
			}

			if ("D".equals(itemType)) {
				return "소득세".equals(wageTypeName)
					|| "지방소득세".equals(wageTypeName);
			}

			return false;
		}

		/*
		 * 일용직은 DAILY_WORK 연동 규칙이 확정된 후
		 * 별도 처리한다.
		 */
		return false;
	}
}