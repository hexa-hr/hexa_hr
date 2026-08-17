<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여입력</title>

<style>
body {
	font-family: Arial, sans-serif;
	margin: 30px;
}

.search-form {
	border: 1px solid #ccc;
	padding: 20px;
	margin-bottom: 25px;
}

.form-row {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-bottom: 10px;
}

label {
	font-weight: bold;
}

input, select, button {
	padding: 6px;
}

.error-message {
	color: red;
	font-weight: bold;
	margin-top: 10px;
}

.status-message {
	margin: 15px 0;
	font-weight: bold;
}

table {
	border-collapse: collapse;
	min-width: 850px;
}

th, td {
	border: 1px solid #aaa;
	padding: 8px 10px;
}

th {
	background-color: #f2f2f2;
}

.amount {
	text-align: right;
}

.center {
	text-align: center;
}

.inactive {
	background-color: #eeeeee;
}

.previous-wage-dialog {
	border: 1px solid #aaa;
	border-radius: 8px;
	padding: 20px;
}

.previous-wage-dialog::backdrop {
	background-color: rgba(0, 0, 0, 0.45);
}

.previous-wage-dialog h2 {
	margin-top: 0;
}

.previous-wage-dialog select {
	min-width: 230px;
}

.previous-wage-dialog-actions {
	margin-top: 15px;
	display: flex;
	gap: 8px;
}
</style>
</head>

<body>

	<h1>급여입력</h1>

	<c:if test="${not empty successMessage}">

		<div style="margin-bottom: 15px; font-weight: bold;">

			<c:out value="${successMessage}" />

		</div>

	</c:if>

	<form id="workspaceSearchForm" class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/paymentInput.do">

		<input type="hidden" name="incomeType"
			value="<c:out value='${incomeType}' />">

		<div class="form-row">

			<input type="hidden" id="wageMonth" name="wageMonth"
				value="<c:out value='${wageMonth}' />"> <label
				for="wageYear">귀속연월</label> <select id="wageYear" required>
			</select> <select id="wageMonthPart" required>

				<c:forEach var="month" begin="1" end="12">

					<fmt:formatNumber var="monthValue" value="${month}" pattern="00" />

					<option value="${monthValue}">
						<c:out value="${monthValue}" />월
					</option>

				</c:forEach>

			</select> <label for="wagePeriod">급여차수</label> <select id="wagePeriod"
				name="wagePeriod" required>

				<c:forEach var="period" begin="1" end="10">

					<option value="${period}"
						<c:if test="${wagePeriod eq period.toString()}">
							selected
						</c:if>>

						<c:out value="${period}" />차

					</option>

				</c:forEach>

			</select>

		</div>


		<div class="form-row">

			<label for="settlementStartDate"> 정산 시작일 </label> <input type="date"
				id="settlementStartDate" name="settlementStartDate"
				value="<c:out value='${settlementStartDate}' />" readonly> <label
				for="settlementEndDate"> 정산 종료일 </label> <input type="date"
				id="settlementEndDate" name="settlementEndDate"
				value="<c:out value='${settlementEndDate}' />" readonly> <label
				for="wagePaymentDate"> 급여 지급일 </label> <input type="date"
				id="wagePaymentDate" name="wagePaymentDate"
				value="<c:out value='${wagePaymentDate}' />" readonly>

		</div>

		<c:if test="${not empty errorMessage}">

			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>

		</c:if>

	</form>

	<div style="margin-bottom: 15px;">

		<button type="button" id="previousWageOpenButton"
			<c:if test="${empty previousWageSourceOptions}">
				disabled
			</c:if>>
			지난급여 불러오기</button>

	</div>

	<dialog id="previousWageDialog" class="previous-wage-dialog">

	<h2>급여연월 선택</h2>

	<form id="previousWageCopyForm" method="post"
		action="${pageContext.request.contextPath}/wage/paymentPreviousCopy.do">

		<input type="hidden" id="previousCopySourceWageMonth"
			name="sourceWageMonth"> <input type="hidden"
			id="previousCopySourceWagePeriod" name="sourceWagePeriod"> <input
			type="hidden" name="wageMonth" value="<c:out value='${wageMonth}' />">

		<input type="hidden" name="wagePeriod"
			value="<c:out value='${wagePeriod}' />"> <input type="hidden"
			name="incomeType" value="<c:out value='${incomeType}' />"> <input
			type="hidden" id="previousCopyReplaceConfirmed"
			name="replaceConfirmed" value="false"> <select
			id="previousWageSourceSelect" required>

			<option value="">귀속연월 차수 선택</option>

			<c:forEach var="source" items="${previousWageSourceOptions}">

				<option value="<c:out value='${source.wageMonth}' />"
					data-wage-month="<c:out value='${source.wageMonth}' />"
					data-wage-period="<c:out value='${source.wagePeriod}' />">
					<c:out value="${fn:substring(source.wageMonth, 0, 4)}" />년
					<c:out value="${fn:substring(source.wageMonth, 5, 7)}" />월
					<fmt:formatNumber value="${source.wagePeriod}" pattern="00" />차
				</option>

			</c:forEach>

		</select>

		<div class="previous-wage-dialog-actions">

			<button type="button" id="previousWageSubmitButton">급여정보
				불러오기</button>

			<button type="button" id="previousWageCloseButton">취소</button>

		</div>

	</form>

	</dialog>

	<div style="margin-bottom: 15px;">

		<c:url var="workerIncomeUrl" value="/wage/paymentInput.do">

			<c:param name="wageMonth" value="${wageMonth}" />

			<c:param name="wagePeriod" value="${wagePeriod}" />

			<c:param name="incomeType" value="worker" />

		</c:url>

		<c:url var="businessIncomeUrl" value="/wage/paymentInput.do">

			<c:param name="wageMonth" value="${wageMonth}" />

			<c:param name="wagePeriod" value="${wagePeriod}" />

			<c:param name="incomeType" value="business" />

		</c:url>

		<a href="${workerIncomeUrl}"
			style="font-weight:
				${incomeType eq 'worker' ? 'bold' : 'normal'};">
			일반소득 </a> &nbsp; | &nbsp; <a href="${businessIncomeUrl}"
			style="font-weight:
				${incomeType eq 'business' ? 'bold' : 'normal'};">
			사업소득/기타소득 </a>

	</div>

	<form method="get"
		action="${pageContext.request.contextPath}/wage/paymentInput.do"
		style="margin-bottom: 20px;">

		<input type="hidden" name="wageMonth"
			value="<c:out value='${wageMonth}' />"> <input type="hidden"
			name="wagePeriod" value="<c:out value='${wagePeriod}' />"> <input
			type="hidden" name="incomeType"
			value="<c:out value='${incomeType}' />">

		<c:forEach var="pending" items="${allPendingEmployees}">

			<input type="hidden" name="pendingEmployeeId"
				value="<c:out value='${pending.employeeId}' />">

		</c:forEach>

		<label for="addEmployeeId"> 신규추가 </label> <select id="addEmployeeId"
			name="addEmployeeId" required>

			<option value="">사원 선택</option>

			<c:forEach var="employee" items="${availableEmployees}">

				<option value="${employee.employeeId}">
					<c:out value="${employee.koreanName}" /> -
					<c:out value="${employee.employmentType}" />
				</option>

			</c:forEach>

		</select>

		<button type="submit">추가</button>

	</form>

	<c:if test="${not empty savedEmployees or not empty pendingEmployees}">

		<h2>사원 목록</h2>

		<div style="margin-bottom: 10px;">
			총
			<c:out value="${savedEmployees.size()}" />
			명
		</div>

		<table style="margin-bottom: 25px;">

			<thead>
				<tr>
					<th>사원ID</th>
					<th>구분</th>
					<th>성명</th>
					<th>부서</th>
					<th>지급총액</th>
					<th>공제총액</th>
					<th>실지급액</th>
				</tr>
			</thead>

			<tbody>

				<c:forEach var="employee" items="${savedEmployees}">

					<tr>

						<td class="center"><c:out value="${employee.employeeId}" />
						</td>

						<td class="center"><c:out value="${employee.employmentType}" />
						</td>

						<td><c:url var="employeeSelectUrl"
								value="/wage/paymentInput.do">

								<c:param name="wageMonth" value="${wageMonth}" />

								<c:param name="wagePeriod" value="${wagePeriod}" />

								<c:param name="incomeType" value="${incomeType}" />

								<c:param name="employeeId" value="${employee.employeeId}" />

								<c:forEach var="pending" items="${allPendingEmployees}">

									<c:param name="pendingEmployeeId" value="${pending.employeeId}" />

								</c:forEach>

							</c:url> <a href="${employeeSelectUrl}"> <c:out
									value="${employee.koreanName}" />
						</a></td>

						<td><c:choose>
								<c:when test="${empty employee.departmentName}">
								-
							</c:when>
								<c:otherwise>
									<c:out value="${employee.departmentName}" />
								</c:otherwise>
							</c:choose></td>

						<td class="amount"><fmt:formatNumber
								value="${employee.totalPayment}" pattern="#,##0" /></td>

						<td class="amount"><fmt:formatNumber
								value="${employee.totalDeduction}" pattern="#,##0" /></td>

						<td class="amount"><fmt:formatNumber
								value="${employee.netPayment}" pattern="#,##0" /></td>

					</tr>

				</c:forEach>

				<c:forEach var="employee" items="${pendingEmployees}">

					<tr>

						<td class="center"><c:out value="${employee.employeeId}" />
						</td>

						<td class="center"><c:out value="${employee.employmentType}" />
						</td>

						<td><c:url var="pendingEmployeeSelectUrl"
								value="/wage/paymentInput.do">

								<c:param name="wageMonth" value="${wageMonth}" />

								<c:param name="wagePeriod" value="${wagePeriod}" />

								<c:param name="incomeType" value="${incomeType}" />

								<c:param name="employeeId" value="${employee.employeeId}" />

								<c:forEach var="pending" items="${allPendingEmployees}">

									<c:param name="pendingEmployeeId" value="${pending.employeeId}" />

								</c:forEach>

							</c:url> <a href="${pendingEmployeeSelectUrl}"> <c:out
									value="${employee.koreanName}" />
						</a> (미저장)</td>

						<td><c:choose>
								<c:when test="${empty employee.departmentName}">
									-
								</c:when>
								<c:otherwise>
									<c:out value="${employee.departmentName}" />
								</c:otherwise>
							</c:choose></td>

						<td class="amount">0</td>
						<td class="amount">0</td>
						<td class="amount">0</td>

					</tr>

				</c:forEach>

			</tbody>

		</table>

	</c:if>

	<c:if test="${not empty wageItems}">

		<div class="status-message">

			선택 사원:
			<c:out value="${selectedEmployeeName}" />

			/

			<c:choose>

				<c:when test="${selectedEmployeeSaved}">
					기존 저장 급여
				</c:when>

				<c:otherwise>
					미저장 신규 급여
				</c:otherwise>

			</c:choose>

		</div>


		<form method="post"
			action="${pageContext.request.contextPath}/wage/paymentInputCalculate.do">

			<input type="hidden" name="employeeId"
				value="<c:out value='${selectedEmployeeId}' />"> <input
				type="hidden" name="wageMonth"
				value="<c:out value='${wageMonth}' />"> <input type="hidden"
				name="wagePeriod" value="<c:out value='${wagePeriod}' />"> <input
				type="hidden" name="incomeType"
				value="<c:out value='${incomeType}' />"> <input
				type="hidden" name="settlementStartDate"
				value="<c:out value='${settlementStartDate}' />"> <input
				type="hidden" name="settlementEndDate"
				value="<c:out value='${settlementEndDate}' />"> <input
				type="hidden" name="wagePaymentDate"
				value="<c:out value='${wagePaymentDate}' />">

			<c:forEach var="pending" items="${allPendingEmployees}">
				<input type="hidden" name="pendingEmployeeId"
					value="<c:out value='${pending.employeeId}' />">
			</c:forEach>


			<table>

				<thead>

					<tr>
						<th>ID</th>
						<th>구분</th>
						<th>급여항목</th>
						<th>과세구분</th>
						<th>금액</th>
						<th>active</th>
						<th>calculable</th>
					</tr>

				</thead>


				<tbody>

					<c:forEach var="item" items="${wageItems}">

						<tr class="${item.active ? '' : 'inactive'}">

							<td class="center"><c:out value="${item.wageTypeId}" /> <input
								type="hidden" name="wageTypeId"
								value="<c:out value='${item.wageTypeId}' />"></td>

							<td class="center"><c:choose>

									<c:when test="${item.itemType eq 'P'}">
										지급
									</c:when>

									<c:when test="${item.itemType eq 'D'}">
										공제
									</c:when>

									<c:otherwise>
										<c:out value="${item.itemType}" />
									</c:otherwise>

								</c:choose></td>

							<td><c:out value="${item.wageTypeName}" /></td>

							<td class="center"><c:out value="${item.taxableYn}" /></td>

							<td class="amount"><input type="number" name="wageValue"
								min="0" step="1" value="<c:out value='${item.wageValue}' />"
								required></td>

							<td class="center"><c:out value="${item.active}" /></td>

							<td class="center"><c:out value="${item.calculable}" /></td>

						</tr>

					</c:forEach>

				</tbody>

			</table>


			<div style="margin-top: 15px;">

				<button type="submit">자동계산</button>

				<button type="submit"
					formaction="${pageContext.request.contextPath}/wage/paymentInputSave.do">
					저장</button>

			</div>


			<c:if test="${autoCalculated}">

				<div style="margin-top: 20px;">

					<strong>지급합계:</strong>

					<fmt:formatNumber value="${totalPayment}" pattern="#,##0" />

					원 &nbsp;&nbsp; <strong>공제합계:</strong>

					<fmt:formatNumber value="${totalDeduction}" pattern="#,##0" />

					원 &nbsp;&nbsp; <strong>실지급액:</strong>

					<fmt:formatNumber value="${netPayment}" pattern="#,##0" />

					원

				</div>

			</c:if>

		</form>

	</c:if>

	<script>
	(function() {

		const openButton =
			document.getElementById(
				"previousWageOpenButton");

		const dialog =
			document.getElementById(
				"previousWageDialog");

		const copyForm =
			document.getElementById(
				"previousWageCopyForm");

		const sourceSelect =
			document.getElementById(
				"previousWageSourceSelect");

		const sourceWageMonthInput =
			document.getElementById(
				"previousCopySourceWageMonth");

		const sourceWagePeriodInput =
			document.getElementById(
				"previousCopySourceWagePeriod");

		const replaceConfirmedInput =
			document.getElementById(
				"previousCopyReplaceConfirmed");

		const submitButton =
			document.getElementById(
				"previousWageSubmitButton");

		const closeButton =
			document.getElementById(
				"previousWageCloseButton");

		if (!openButton
			|| !dialog
			|| !copyForm
			|| !sourceSelect
			|| !sourceWageMonthInput
			|| !sourceWagePeriodInput
			|| !replaceConfirmedInput
			|| !submitButton
			|| !closeButton) {

			return;
		}

		openButton.addEventListener(
			"click",
			function() {

				sourceSelect.value = "";
				sourceWageMonthInput.value = "";
				sourceWagePeriodInput.value = "";
				replaceConfirmedInput.value = "false";
				submitButton.disabled = false;

				dialog.showModal();
			});

		closeButton.addEventListener(
			"click",
			function() {

				dialog.close();
			});

		submitButton.addEventListener(
			"click",
			function() {

				const selectedOption =
					sourceSelect.options[
						sourceSelect.selectedIndex];

				if (!sourceSelect.value
					|| !selectedOption) {

					window.alert(
						"불러올 귀속연월과 급여차수를 선택해 주세요.");

					return;
				}

				sourceWageMonthInput.value =
					selectedOption.dataset.wageMonth;

				sourceWagePeriodInput.value =
					selectedOption.dataset.wagePeriod;

				const confirmed =
					window.confirm(
						"기등록된 급여테이블은 삭제되며,\n"
						+ "불러오기 한 급여테이블로 교체됩니다.\n\n"
						+ "불러오기 하시겠습니까?");

				if (!confirmed) {
					return;
				}

				replaceConfirmedInput.value =
					"true";

				submitButton.disabled =
					true;

				copyForm.submit();
			});

	})();
	</script>

	<script>
	(function() {

		const searchForm =
			document.getElementById("workspaceSearchForm");

		if (!searchForm) {
			return;
		}

		const wageMonthInput =
			document.getElementById("wageMonth");

		const wageYearSelect =
			document.getElementById("wageYear");

		const wageMonthSelect =
			document.getElementById("wageMonthPart");

		const wagePeriodSelect =
			document.getElementById("wagePeriod");

		const incomeTypeInput =
			searchForm.elements["incomeType"];

		const wageMonthMatch =
			/^(\d{4})-(0[1-9]|1[0-2])$/.exec(
				wageMonthInput.value);

		const today = new Date();

		const selectedYear = wageMonthMatch
			? Number(wageMonthMatch[1])
			: today.getFullYear();

		const currentMonthNumber =
			today.getMonth() + 1;

		const currentMonth =
			currentMonthNumber < 10
				? "0" + currentMonthNumber
				: String(currentMonthNumber);

		const selectedMonth = wageMonthMatch
			? wageMonthMatch[2]
			: currentMonth;

		const firstYear = 2005;
		const lastYear = today.getFullYear() + 1;

		for (let year = firstYear;
			year <= lastYear;
			year++) {

			const option =
				document.createElement("option");

			option.value = String(year);
			option.textContent = year + "년";

			wageYearSelect.appendChild(option);
		}

		wageYearSelect.value =
			String(selectedYear);

		wageMonthSelect.value =
			selectedMonth;

		function buildWageMonth() {

			return wageYearSelect.value
				+ "-"
				+ wageMonthSelect.value;
		}

		function moveWorkspace() {

			const nextWageMonth =
				buildWageMonth();

			wageMonthInput.value =
				nextWageMonth;

			const url = new URL(
				searchForm.action,
				window.location.origin);

			url.search = "";

			url.searchParams.set(
				"wageMonth",
				nextWageMonth);

			url.searchParams.set(
				"wagePeriod",
				wagePeriodSelect.value);

			url.searchParams.set(
				"incomeType",
				incomeTypeInput.value);

			window.location.assign(
				url.pathname + url.search);
		}

		wageYearSelect.addEventListener(
			"change",
			moveWorkspace);

		wageMonthSelect.addEventListener(
			"change",
			moveWorkspace);

		wagePeriodSelect.addEventListener(
			"change",
			moveWorkspace);

		searchForm.addEventListener(
			"submit",
			function(event) {

				event.preventDefault();
				moveWorkspace();
			});

	})();
	</script>

	<script>
	(function() {

		const hasPending =
			${not empty allPendingEmployees};

		const autoCalculated =
			${autoCalculated == true};

		const previousCopied =
			${previousCopied == true};

		if (!hasPending
			&& !autoCalculated
			&& !previousCopied) {

			return;
		}

		const url =
			new URL(
				"${pageContext.request.contextPath}/wage/paymentInput.do",
				window.location.origin);

		url.searchParams.set(
			"wageMonth",
			"<c:out value='${wageMonth}' />");

		url.searchParams.set(
			"wagePeriod",
			"<c:out value='${wagePeriod}' />");
			
		url.searchParams.set(
			"incomeType",
			"<c:out value='${incomeType}' />");

		const selectedEmployeeSaved =
			${selectedEmployeeSaved == true};

		if (selectedEmployeeSaved) {

			url.searchParams.set(
				"employeeId",
				"<c:out value='${selectedEmployeeId}' />");
		}

		/*
		 * pendingEmployeeId는 주소에 남기지 않는다.
		 *
		 * 따라서 현재 화면에서는 pending 목록이 유지되지만
		 * F5 / 재진입 시에는 DB 저장 사원만 다시 조회된다.
		 */
		window.history.replaceState(
			null,
			"",
			url.pathname + url.search);

	})();
	</script>

</body>
</html>