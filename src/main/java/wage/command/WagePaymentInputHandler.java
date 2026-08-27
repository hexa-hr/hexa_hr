package wage.command;

import java.sql.Date;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employee.model.EmployeeSelectRow;
import employee.service.EmployeeSelectService;
import mvc.command.CommandHandler;
import wage.model.WageLedgerSummary;
import wage.model.WagePaymentEmployeeRow;
import wage.model.WagePaymentInputViewItem;
import wage.model.WagePaymentPeriodDefault;
import wage.service.WagePaymentInputService;
import wage.service.WagePaymentPreviousCopyService;

// 給与入力画面照会Handler
public class WagePaymentInputHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/wage/wagePaymentInput.jsp";

	private EmployeeSelectService employeeSelectService = new EmployeeSelectService();

	private WagePaymentInputService wagePaymentInputService = new WagePaymentInputService();

	private WagePaymentPreviousCopyService wagePaymentPreviousCopyService = new WagePaymentPreviousCopyService();

	@Override
	public String process(
		HttpServletRequest req,
		HttpServletResponse res)
		throws Exception {

		if (!"GET".equalsIgnoreCase(req.getMethod())) {

			res.setStatus(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return null;
		}

		/*
		 * 新規追加機能で使用する社員選択一覧。
		 * 現在の段階ではJSPで直接使用しないが
		 * 次の段階との連携のために維持する。
		 */
		List<EmployeeSelectRow> employeeRows = employeeSelectService.getEmployeeRows(
			null,
			null,
			null);

		req.setAttribute(
			"employeeRows",
			employeeRows);

		String employeeIdParam = trim(req.getParameter("employeeId"));

		String wageMonth = trim(req.getParameter("wageMonth"));

		String wagePeriod = trim(req.getParameter("wagePeriod"));

		try {

			List<Integer> pendingEmployeeIds = parsePendingEmployeeIds(
				req);

			List<Integer> addEmployeeIds = parseAddEmployeeIds(
				req);

			String incomeType = normalizeIncomeType(
				req.getParameter(
					"incomeType"));

			req.setAttribute(
				"modalEmployees",
				filterEmployeesByIncomeType(
					employeeRows,
					incomeType));

			req.setAttribute(
				"incomeType",
				incomeType);

			boolean previousCopied = "true".equals(
				req.getParameter(
					"previousCopied"));

			req.setAttribute(
				"previousCopied",
				previousCopied);

			if ("true".equals(
				req.getParameter("saved"))) {

				req.setAttribute(
					"successMessage",
					"給与が保存されました。");
			}

			/*
			 * 初回アクセス
			 * → 現在の帰属年月 / 1回を基本ワークスペースとして使用
			 */
			if (wageMonth == null) {

				wageMonth = YearMonth.now().toString();
			}

			if (wagePeriod == null) {

				wagePeriod = "1";
			}

			req.setAttribute(
				"selectedEmployeeId",
				employeeIdParam);

			req.setAttribute(
				"wageMonth",
				wageMonth);

			req.setAttribute(
				"wagePeriod",
				wagePeriod);

			req.setAttribute(
				"previousWageSourceOptions",
				wagePaymentPreviousCopyService.getSourceOptions(
					wageMonth,
					wagePeriod));

			/*
			 * 現在の帰属年月 + 給与回次に
			 * 実際に保存された社員一覧
			 */
			List<WagePaymentEmployeeRow> allSavedEmployees = wagePaymentInputService
				.getSavedEmployees(
					wageMonth,
					wagePeriod);

			if (previousCopied) {

				int workerEmployeeCount = 0;
				int businessEmployeeCount = 0;

				for (WagePaymentEmployeeRow employee : allSavedEmployees) {

					if (isWorkerEmploymentType(
						employee.getEmploymentType())) {

						workerEmployeeCount++;

					} else if ("임시직".equals(
						employee.getEmploymentType())) {

						businessEmployeeCount++;
					}
				}

				req.setAttribute(
					"successMessage",
					"[読み込み] 一般所得: "
						+ workerEmployeeCount
						+ "件、事業所得: "
						+ businessEmployeeCount
						+ "件");
			}

			/*
			 * モーダルで選択した複数の社員を検証した後、pendingとして追加する。
			 */
			List<Integer> validatedAddEmployeeIds = new ArrayList<>();

			for (Integer addEmployeeId : addEmployeeIds) {

				EmployeeSelectRow addEmployee = findEmployee(
					employeeRows,
					addEmployeeId);

				if (addEmployee == null) {

					throw new IllegalArgumentException(
						"正しい社員を選択する必要があります。");
				}

				if (!isAvailableEmployeeForIncomeType(
					addEmployee,
					incomeType)) {

					throw new IllegalArgumentException(
						"現在の所得区分では追加できない社員です。");
				}

				validatedAddEmployeeIds.add(
					addEmployeeId);
			}

			Integer firstAddedEmployeeId = null;

			for (Integer addEmployeeId : validatedAddEmployeeIds) {

				/*
				 * すでに保存済み、またはpendingの社員は何も処理しない。
				 */
				if (containsSavedEmployee(
					allSavedEmployees,
					addEmployeeId)
					|| pendingEmployeeIds.contains(
						addEmployeeId)) {

					continue;
				}

				pendingEmployeeIds.add(
					addEmployeeId);

				if (firstAddedEmployeeId == null) {

					firstAddedEmployeeId = addEmployeeId;
				}
			}

			/*
			 * 実際に新しく追加された最初の社員を選択する。
			 * すべて既存の社員の場合は、現在の選択状態を維持する。
			 */
			if (firstAddedEmployeeId != null) {

				employeeIdParam = String.valueOf(
					firstAddedEmployeeId);

				req.setAttribute(
					"selectedEmployeeId",
					employeeIdParam);
			}

			/*
			 * リクエスト全体で維持する未保存社員。
			 *
			 * 所得区分タブに関係なく維持するが
			 * 保存済み社員と日雇いは除外する。
			 */
			List<EmployeeSelectRow> allPendingEmployees = buildAllPendingEmployees(
				employeeRows,
				allSavedEmployees,
				pendingEmployeeIds);

			req.setAttribute(
				"allPendingEmployees",
				allPendingEmployees);

			/*
			 * 現在の所得区分タブに表示する保存済み社員
			 */
			List<WagePaymentEmployeeRow> savedEmployees = filterSavedEmployeesByIncomeType(
				allSavedEmployees,
				incomeType);

			req.setAttribute(
				"savedEmployees",
				savedEmployees);

			/*
			 * 現在の所得区分タブに表示する未保存社員
			 */
			List<EmployeeSelectRow> pendingEmployees = filterEmployeesByIncomeType(
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"pendingEmployees",
				pendingEmployees);

			/*
			 * 新規追加候補には、現在のタブの社員のうち
			 * 全体のsaved / pendingのいずれにも存在しない社員のみを使用する。
			 */
			List<EmployeeSelectRow> availableEmployees = buildAvailableEmployees(
				employeeRows,
				allSavedEmployees,
				allPendingEmployees,
				incomeType);

			req.setAttribute(
				"availableEmployees",
				availableEmployees);

			/*
			 * 現在の帰属年月 + 給与回次の
			 * 保存済み基本情報を確認
			 */
			WageLedgerSummary periodSummary = wagePaymentInputService.getPeriodSummary(
				wageMonth,
				wagePeriod);

			Date settlementStartDate;
			Date settlementEndDate;

			if (periodSummary != null) {

				/*
				 * 既存の給与回次
				 * → wageに保存された日付を使用
				 */
				settlementStartDate = toSqlDate(
					periodSummary
						.getSettlementPeriodStartDate());

				settlementEndDate = toSqlDate(
					periodSummary
						.getSettlementPeriodEndDate());

				Date savedPaymentDate = toSqlDate(
					periodSummary
						.getWagePaymentDate());

				req.setAttribute(
					"settlementStartDate",
					toDateString(
						settlementStartDate));

				req.setAttribute(
					"settlementEndDate",
					toDateString(
						settlementEndDate));

				req.setAttribute(
					"wagePaymentDate",
					toDateString(
						savedPaymentDate));

				req.setAttribute(
					"existingPeriod",
					true);

			} else {

				/*
				 * 新規給与回次
				 * → 会社の給与支給情報の基本日付を使用
				 */
				WagePaymentPeriodDefault defaultPeriod = wagePaymentInputService.getDefaultPeriod(
					wageMonth);

				settlementStartDate = defaultPeriod.getSettlementStartDate();

				settlementEndDate = defaultPeriod.getSettlementEndDate();

				Date defaultPaymentDate = defaultPeriod.getWagePaymentDate();

				req.setAttribute(
					"settlementStartDate",
					toDateString(
						settlementStartDate));

				req.setAttribute(
					"settlementEndDate",
					toDateString(
						settlementEndDate));

				req.setAttribute(
					"wagePaymentDate",
					toDateString(
						defaultPaymentDate));

				req.setAttribute(
					"existingPeriod",
					false);
			}

			/*
			 * 社員が選択されていない初期画面でも
			 * 現在使用中の給与項目の枠を表示する。
			 */
			req.setAttribute(
				"wageItems",
				wagePaymentInputService.getFrameViewItems(
					wageMonth,
					wagePeriod));

			/*
			 * 社員が選択された場合のみ
			 * 右側の給与入力項目を照会する。
			 *
			 * employeeIdはワークスペースの必須キーではない。
			 */
			if (employeeIdParam != null) {

				Integer employeeId;

				try {

					employeeId = Integer.valueOf(
						employeeIdParam);

				} catch (NumberFormatException e) {

					throw new IllegalArgumentException(
						"正しい社員を選択する必要があります。");
				}

				EmployeeSelectRow selectedEmployee = findEmployee(
					employeeRows,
					employeeId);

				if (selectedEmployee == null) {

					throw new IllegalArgumentException(
						"正しい社員を選択する必要があります。");
				}

				boolean selectedEmployeeSaved = containsSavedEmployee(
					savedEmployees,
					employeeId);

				boolean selectedEmployeePending = containsEmployee(
					pendingEmployees,
					employeeId);

				if (!selectedEmployeeSaved
					&& !selectedEmployeePending) {

					throw new IllegalArgumentException(
						"現在の給与回次に登録されていない社員です。");
				}

				req.setAttribute(
					"selectedEmployeeSaved",
					selectedEmployeeSaved);

				req.setAttribute(
					"selectedEmployeePending",
					selectedEmployeePending);

				req.setAttribute(
					"selectedEmployeeName",
					selectedEmployee.getKoreanName());

				List<WagePaymentInputViewItem> wageItems = wagePaymentInputService.getViewItems(
					employeeId,
					wageMonth,
					wagePeriod,
					settlementStartDate,
					settlementEndDate);

				req.setAttribute(
					"wageItems",
					wageItems);
			}

		} catch (IllegalArgumentException e) {

			req.setAttribute(
				"errorMessage",
				e.getMessage());
		}

		return FORM_VIEW;
	}

	// --- Helperメソッド ---

	private EmployeeSelectRow findEmployee(
		List<EmployeeSelectRow> employeeRows,
		Integer employeeId) {

		for (EmployeeSelectRow employeeRow : employeeRows) {

			if (employeeId.equals(
				employeeRow.getEmployeeId())) {

				return employeeRow;
			}
		}

		return null;
	}

	private List<Integer> parsePendingEmployeeIds(
		HttpServletRequest req) {

		String[] values = req.getParameterValues(
			"pendingEmployeeId");

		Set<Integer> result = new LinkedHashSet<>();

		if (values == null) {
			return new ArrayList<>();
		}

		for (String value : values) {

			value = trim(value);

			if (value == null) {
				continue;
			}

			try {

				Integer employeeId = Integer.valueOf(value);

				if (employeeId <= 0) {

					throw new NumberFormatException();
				}

				result.add(employeeId);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"pending社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(result);
	}

	private List<Integer> parseAddEmployeeIds(
		HttpServletRequest req) {

		String[] values = req.getParameterValues(
			"addEmployeeId");

		Set<Integer> result = new LinkedHashSet<>();

		if (values == null) {
			return new ArrayList<>();
		}

		for (String value : values) {

			value = trim(value);

			if (value == null) {
				continue;
			}

			try {

				Integer employeeId = Integer.valueOf(
					value);

				if (employeeId <= 0) {
					throw new NumberFormatException();
				}

				result.add(
					employeeId);

			} catch (NumberFormatException e) {

				throw new IllegalArgumentException(
					"追加する社員情報が正しくありません。");
			}
		}

		return new ArrayList<>(
			result);
	}

	private List<EmployeeSelectRow> buildAllPendingEmployees(
		List<EmployeeSelectRow> employeeRows,
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<Integer> pendingEmployeeIds) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (Integer employeeId : pendingEmployeeIds) {

			if (containsSavedEmployee(
				allSavedEmployees,
				employeeId)) {

				continue;
			}

			EmployeeSelectRow employee = findEmployee(
				employeeRows,
				employeeId);

			if (employee == null) {
				continue;
			}

			/*
			 * 日雇給与は別画面で処理するため
			 * 全体のpending状態にも含めない。
			 */
			if ("일용직".equals(
				employee.getEmploymentType())) {

				continue;
			}

			if (!containsEmployee(
				result,
				employeeId)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<WagePaymentEmployeeRow> filterSavedEmployeesByIncomeType(
		List<WagePaymentEmployeeRow> allSavedEmployees,
		String incomeType) {

		List<WagePaymentEmployeeRow> result = new ArrayList<>();

		for (WagePaymentEmployeeRow employee : allSavedEmployees) {

			if (isAvailableEmploymentTypeForIncomeType(
				employee.getEmploymentType(),
				incomeType)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> filterEmployeesByIncomeType(
		List<EmployeeSelectRow> employees,
		String incomeType) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employees) {

			if (isAvailableEmployeeForIncomeType(
				employee,
				incomeType)) {

				result.add(
					employee);
			}
		}

		return result;
	}

	private List<EmployeeSelectRow> buildAvailableEmployees(
		List<EmployeeSelectRow> employeeRows,
		List<WagePaymentEmployeeRow> allSavedEmployees,
		List<EmployeeSelectRow> allPendingEmployees,
		String incomeType) {

		List<EmployeeSelectRow> result = new ArrayList<>();

		for (EmployeeSelectRow employee : employeeRows) {

			if (!isAvailableEmployeeForIncomeType(
				employee,
				incomeType)) {

				continue;
			}

			Integer employeeId = employee.getEmployeeId();

			if (containsSavedEmployee(
				allSavedEmployees,
				employeeId)
				|| containsEmployee(
					allPendingEmployees,
					employeeId)) {

				continue;
			}

			result.add(
				employee);
		}

		return result;
	}

	private boolean containsSavedEmployee(
		List<WagePaymentEmployeeRow> employees,
		Integer employeeId) {

		for (WagePaymentEmployeeRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return true;
			}
		}

		return false;
	}

	private boolean containsEmployee(
		List<EmployeeSelectRow> employees,
		Integer employeeId) {

		for (EmployeeSelectRow employee : employees) {

			if (employeeId.equals(
				employee.getEmployeeId())) {

				return true;
			}
		}

		return false;
	}

	private String normalizeIncomeType(
		String incomeType) {

		incomeType = trim(
			incomeType);

		if (incomeType == null) {
			return "worker";
		}

		if ("worker".equals(
			incomeType)
			|| "business".equals(
				incomeType)) {

			return incomeType;
		}

		throw new IllegalArgumentException(
			"正しい所得区分を選択する必要があります。");
	}

	private boolean isAvailableEmployeeForIncomeType(
		EmployeeSelectRow employee,
		String incomeType) {

		return isAvailableEmploymentTypeForIncomeType(
			employee.getEmploymentType(),
			incomeType);
	}

	private boolean isAvailableEmploymentTypeForIncomeType(
		String employmentType,
		String incomeType) {

		if ("worker".equals(
			incomeType)) {

			return !"임시직".equals(
				employmentType)
				&& !"일용직".equals(
					employmentType);
		}

		if ("business".equals(
			incomeType)) {

			return "임시직".equals(
				employmentType);
		}

		return false;
	}

	private boolean isWorkerEmploymentType(
		String employmentType) {

		return "정규직".equals(employmentType)
			|| "계약직".equals(employmentType)
			|| "파견직".equals(employmentType)
			|| "위촉직".equals(employmentType);
	}

	private Date parseRequiredDate(
		String value,
		String fieldName) {

		if (value == null) {

			throw new IllegalArgumentException(
				fieldName + "を入力する必要があります。");
		}

		try {

			return Date.valueOf(value);

		} catch (IllegalArgumentException e) {

			throw new IllegalArgumentException(
				fieldName
					+ "はYYYY-MM-DD形式である必要があります。");
		}
	}

	private Date toSqlDate(
		java.util.Date value) {

		if (value == null) {
			return null;
		}

		return new Date(
			value.getTime());
	}

	private String toDateString(
		Date value) {

		return value == null
			? null
			: value.toString();
	}

	private String trim(
		String value) {

		if (value == null) {
			return null;
		}

		value = value.trim();

		return value.isEmpty()
			? null
			: value;
	}
}