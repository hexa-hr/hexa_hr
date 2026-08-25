<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与入力・管理（日雇い）</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	font-family: Arial, sans-serif;
	margin: 0;
	color: #222222;
}

button, input, select {
	padding: 6px;
	box-sizing: border-box;
}

button {
	cursor: pointer;
}

button:disabled {
	cursor: default;
}

.page-title {
	margin: 0 0 22px;
}

.error-message {
	margin: 12px 0;
	color: #d60000;
	font-weight: bold;
}

.success-message {
	margin: 12px 0;
	font-weight: bold;
}

.search-form {
	border: 1px solid #cccccc;
	padding: 18px 20px;
	margin-bottom: 22px;
}

.form-row {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 9px;
	margin-bottom: 10px;
}

.form-row:last-child {
	margin-bottom: 0;
}

.form-row label {
	font-weight: bold;
}

.form-row input[type="date"] {
	width: 135px;
}

.payroll-workspace {
	display: grid;
	grid-template-columns: minmax(460px, 0.9fr) minmax(680px, 1.4fr);
	gap: 28px;
	align-items: start;
}

.payroll-pane {
	min-width: 0;
}

.employee-toolbar {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 14px;
}

.employee-count {
	margin: 12px 0 8px;
}

.table-scroll {
	width: 100%;
	overflow-x: auto;
}

table {
	width: 100%;
	border-collapse: collapse;
	table-layout: fixed;
}

th, td {
	border: 1px solid #bbbbbb;
	padding: 8px 9px;
}

th {
	background-color: #f2f2f2;
}

.center {
	text-align: center;
}

.amount {
	text-align: right;
}

.employee-table {
	min-width: 500px;
}

.employee-select-row {
	cursor: pointer;
}

.employee-select-row:hover {
	background-color: #f5f5f5;
}

.employee-select-row.selected-employee-row, .employee-select-row.selected-employee-row:hover
	{
	background-color: #d9edf7;
}

.employee-name-link {
	color: #0759d1;
}

.pending-mark {
	margin-left: 3px;
	font-size: 12px;
}

.daily-input-panel {
	border-top: 2px solid #222222;
}

.daily-input-grid {
	display: grid;
	grid-template-columns: minmax(420px, 1.35fr) minmax(260px, 0.65fr);
}

.daily-record-column {
	min-width: 0;
	border-right: 1px solid #dddddd;
}

.deduction-column {
	min-width: 0;
}

.daily-record-table {
	table-layout: fixed;
}

.daily-record-table th, .daily-record-table td {
	border-color: #dddddd;
}

.daily-record-table th {
	background-color: #f3f8fb;
	font-weight: normal;
	text-align: center;
}

.daily-record-table td {
	text-align: right;
}

.daily-record-table td:first-child {
	text-align: center;
}

.daily-empty-row td {
	height: 120px;
	text-align: center;
	color: #777777;
}

.source-tax {
	background-color: #fff7ad;
}

.deduction-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 8px;
	min-height: 42px;
	padding: 5px 10px;
	background-color: #fff4f1;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.deduction-header strong {
	color: #e44343;
}

.deduction-header-actions {
	display: flex;
	gap: 5px;
}

.deduction-header-actions button {
	padding: 4px 7px;
	font-size: 12px;
}

.deduction-item-row {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(100px, 45%);
	align-items: center;
	gap: 8px;
	min-height: 42px;
	padding: 4px 10px;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.deduction-item-row input {
	width: 100%;
	text-align: right;
}

.deduction-empty {
	min-height: 120px;
	padding: 45px 10px;
	color: #777777;
	text-align: center;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.daily-subtotals {
	display: grid;
	grid-template-columns: 1fr 1fr;
}

.daily-subtotal {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 16px;
	background-color: #f7fafc;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.daily-subtotal:first-child {
	color: #0759d1;
	border-right: 1px solid #dddddd;
}

.daily-subtotal:last-child {
	color: #e44343;
}

.daily-net-total {
	padding: 14px;
	background-color: #315d7d;
	color: #ffffff;
	font-size: 18px;
	font-weight: bold;
	text-align: center;
}

.daily-form-actions {
	display: flex;
	justify-content: center;
	gap: 8px;
	margin-top: 15px;
}

.payroll-summary {
	margin-top: 45px;
}

.payroll-summary h2 {
	margin-bottom: 14px;
	font-size: 20px;
}

.payroll-summary-grid {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 12px;
}

.payroll-summary-card {
	display: flex;
	align-items: center;
	justify-content: space-between;
	min-height: 60px;
	padding: 0 20px;
	border-radius: 5px;
	color: #ffffff;
	box-sizing: border-box;
}

.payroll-summary-card span {
	font-weight: bold;
}

.payroll-summary-card strong {
	font-size: 20px;
}

.summary-count {
	background-color: #999999;
}

.summary-payment {
	background-color: #45b9dc;
}

.summary-deduction {
	background-color: #ef7777;
}

.summary-net {
	background-color: #4c4d49;
}

.employee-select-dialog {
	width: min(900px, calc(100vw - 40px));
	max-width: 900px;
	max-height: calc(100vh - 40px);
	padding: 24px;
	border: 0;
	border-radius: 14px;
	box-sizing: border-box;
}

.employee-select-dialog::backdrop {
	background-color: rgba(0, 0, 0, 0.45);
}

.employee-select-dialog h2 {
	margin: 0 0 18px;
}

.employee-modal-filters {
	display: grid;
	grid-template-columns: minmax(220px, 1fr) repeat(3, minmax(110px, 150px));
	gap: 8px;
	margin-bottom: 12px;
}

.employee-modal-search {
	display: grid;
	grid-template-columns: minmax(0, 1fr) auto;
	gap: 6px;
}

.employee-modal-filters input, .employee-modal-filters select {
	width: 100%;
	min-width: 0;
}

.employee-modal-table-wrap {
	min-height: 360px;
	max-height: 430px;
	overflow: auto;
	border: 1px solid #dddddd;
}

.employee-modal-table {
	min-width: 760px;
}

.employee-modal-table th, .employee-modal-table td {
	text-align: center;
}

.employee-modal-table th:first-child, .employee-modal-table td:first-child
	{
	width: 42px;
}

.employee-modal-row {
	cursor: pointer;
}

.employee-modal-row:hover {
	background-color: #f5f5f5;
}

.employee-modal-row.selected-modal-row, .employee-modal-row.selected-modal-row:hover
	{
	background-color: #d9edf7;
}

.employee-modal-pagination {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12px;
	margin-top: 14px;
}

.employee-modal-actions {
	display: flex;
	justify-content: center;
	gap: 10px;
	margin-top: 18px;
}

@media ( max-width : 1200px) {
	.payroll-workspace {
		grid-template-columns: 1fr;
	}
}

@media ( max-width : 800px) {
	body {
		margin: 0;
	}
	.daily-input-grid {
		grid-template-columns: 1fr;
	}
	.daily-record-column {
		border-right: 0;
	}
	.payroll-summary-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
	.employee-modal-filters {
		grid-template-columns: 1fr 1fr;
	}
	.employee-modal-search {
		grid-column: 1/-1;
	}
}

@media ( max-width : 560px) {
	.payroll-summary-grid {
		grid-template-columns: 1fr;
	}
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h1 class="page-title">給与入力・管理（日雇い）</h1>

	<c:if test="${not empty errorMessage}">
		<div class="error-message">
			<c:out value="${errorMessage}" />
		</div>
	</c:if>

	<c:if test="${not empty successMessage}">
		<div class="success-message">
			<c:out value="${successMessage}" />
		</div>
	</c:if>

	<form id="workspaceSearchForm" class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">

		<div class="form-row">

			<input type="hidden" id="wageMonth" name="wageMonth"
				value="<c:out value='${wageMonth}' />"> <label
				for="wageYear">帰属年月</label> <select id="wageYear" required></select>

			<select id="wageMonthPart" required>

				<c:forEach var="month" begin="1" end="12">

					<fmt:formatNumber var="monthValue" value="${month}" pattern="00" />

					<option value="${monthValue}">
						<c:out value="${monthValue}" />月
					</option>

				</c:forEach>

			</select> <label for="wagePeriod">給与回次</label> <select id="wagePeriod"
				name="wagePeriod" required>

				<c:forEach var="period" begin="1" end="10">

					<option value="${period}"
						<c:if test="${wagePeriod == period}">
							selected
						</c:if>>

						<c:out value="${period}" />回

					</option>

				</c:forEach>

			</select>

		</div>

		<div class="form-row">

			<label>精算開始日</label> <input type="date"
				value="<c:out value='${settlementStartDate}' />" readonly> <label>精算
				終了日</label> <input type="date"
				value="<c:out value='${settlementEndDate}' />" readonly> <label>給与
				支給日</label> <input type="date" value="<c:out value='${wagePaymentDate}' />"
				readonly>

		</div>

	</form>

	<dialog id="employeeSelectDialog" class="employee-select-dialog">

	<h2>給与支給対象社員の選択</h2>

	<form id="employeeModalAddForm" method="get"
		action="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">

		<input type="hidden" name="wageMonth"
			value="<c:out value='${wageMonth}' />"> <input type="hidden"
			name="wagePeriod" value="<c:out value='${wagePeriod}' />">

		<c:if test="${not empty selectedEmployeeId}">

			<input type="hidden" name="employeeId"
				value="<c:out value='${selectedEmployeeId}' />">

		</c:if>

		<c:forEach var="pending" items="${allPendingEmployees}">

			<input type="hidden" name="pendingEmployeeId"
				value="<c:out value='${pending.employeeId}' />">

		</c:forEach>

		<div class="employee-modal-filters">

			<div class="employee-modal-search">

				<input type="search" id="employeeModalSearchInput"
					placeholder="社員番号または氏名" autocomplete="off">

				<button type="button" id="employeeModalSearchButton">検索</button>

			</div>

			<select id="employeeModalDepartmentFilter">
				<option value="">部署別</option>
			</select> <select id="employeeModalPositionFilter">
				<option value="">役職別</option>
			</select> <select id="employeeModalStatusFilter">
				<option value="">在職状態</option>
			</select>

		</div>

		<div class="employee-modal-table-wrap">

			<table class="employee-modal-table">

				<thead>
					<tr>
						<th><input type="checkbox" id="employeeModalSelectAll">
						</th>
						<th>区分</th>
						<th>社員番号</th>
						<th>氏名</th>
						<th>部署</th>
						<th>役職</th>
						<th>状態</th>
					</tr>
				</thead>

				<tbody>

					<c:forEach var="employee" items="${modalEmployees}">

						<tr class="employee-modal-row"
							data-employee-id="<c:out value='${employee.employeeId}' />"
							data-employee-name="<c:out value='${employee.koreanName}' />"
							data-department="<c:out value='${employee.departmentName}' />"
							data-position="<c:out value='${employee.positionName}' />"
							data-status="<c:out value='${employee.status}' />">

							<td><input type="checkbox" class="employee-modal-checkbox"
								name="addEmployeeId"
								value="<c:out value='${employee.employeeId}' />"></td>

							<td><c:out value="${employee.employmentType}" /></td>

							<td><c:out value="${employee.employeeId}" /></td>

							<td><c:out value="${employee.koreanName}" /></td>

							<td><c:out
									value="${empty employee.departmentName
										? '-' : employee.departmentName}" />
							</td>

							<td><c:out
									value="${empty employee.positionName
										? '-' : employee.positionName}" />
							</td>

							<td><c:out
									value="${empty employee.status
										? '-' : employee.status}" />
							</td>

						</tr>

					</c:forEach>

					<tr id="employeeModalNoResultRow" style="display: none;">
						<td colspan="7">条件に一致する日雇い社員がいません。</td>
					</tr>

				</tbody>

			</table>

		</div>

		<div class="employee-modal-pagination">

			<button type="button" id="employeeModalPreviousPage">‹ 前へ</button>

			<strong id="employeeModalPageInfo">1 / 1</strong>

			<button type="button" id="employeeModalNextPage">次へ ›</button>

		</div>

		<div class="employee-modal-actions">

			<button type="submit" id="employeeModalSubmitButton">社員選択</button>

			<button type="button" id="employeeModalCloseButton">キャンセル</button>

		</div>

	</form>

	</dialog>

	<div class="payroll-workspace">

		<section class="payroll-pane">

			<div class="employee-toolbar">

				<button type="button" id="employeeSelectOpenButton">新規追加</button>

				<form id="employeeDeleteAllForm" method="post"
					action="${pageContext.request.contextPath}/wage/dailyPaymentInputDelete.do"
					style="margin: 0;">

					<input type="hidden" name="deleteMode" value="all"> <input
						type="hidden" name="wageMonth"
						value="<c:out value='${wageMonth}' />"> <input
						type="hidden" name="wagePeriod"
						value="<c:out value='${wagePeriod}' />"> <input
						type="hidden" id="employeeDeleteAllConfirmed"
						name="deleteConfirmed" value="false"> <input type="hidden"
						id="employeeDeleteAllFinalConfirmed" name="deleteFinalConfirmed"
						value="false">

					<c:forEach var="pending" items="${allPendingEmployees}">

						<input type="hidden" name="pendingEmployeeId"
							value="<c:out value='${pending.employeeId}' />">

					</c:forEach>

					<button type="submit" id="employeeDeleteAllButton">一括削除</button>

				</form>

				<form id="employeeDeleteForm" method="post"
					action="${pageContext.request.contextPath}/wage/dailyPaymentInputDelete.do"
					style="margin: 0;">

					<input type="hidden" name="deleteMode" value="selected"> <input
						type="hidden" name="employeeId"
						value="<c:out value='${selectedEmployeeId}' />"> <input
						type="hidden" name="wageMonth"
						value="<c:out value='${wageMonth}' />"> <input
						type="hidden" name="wagePeriod"
						value="<c:out value='${wagePeriod}' />"> <input
						type="hidden" id="employeeDeleteConfirmed" name="deleteConfirmed"
						value="false">

					<c:forEach var="pending" items="${allPendingEmployees}">

						<input type="hidden" name="pendingEmployeeId"
							value="<c:out value='${pending.employeeId}' />">

					</c:forEach>

					<button type="submit" id="employeeDeleteButton">選択削除</button>

				</form>

			</div>

			<h2>社員一覧</h2>

			<div class="employee-count">
				合計
				<c:out
					value="${empty visibleEmployeeCount
					? 0 : visibleEmployeeCount}" />
				名
			</div>

			<div class="table-scroll">

				<table class="employee-table">

					<thead>
						<tr>
							<th>区分</th>
							<th>氏名</th>
							<th>部署</th>
							<th>差引支給額</th>
						</tr>
					</thead>

					<tbody>

						<c:forEach var="employee" items="${savedEmployees}">

							<c:url var="savedEmployeeSelectUrl"
								value="/wage/dailyPaymentInput.do">

								<c:param name="wageMonth" value="${wageMonth}" />

								<c:param name="wagePeriod" value="${wagePeriod}" />

								<c:param name="employeeId" value="${employee.employeeId}" />

								<c:forEach var="pending" items="${allPendingEmployees}">

									<c:param name="pendingEmployeeId" value="${pending.employeeId}" />

								</c:forEach>

							</c:url>

							<tr
								class="employee-select-row
								${selectedEmployeeSaved == true
								and selectedEmployeeId == employee.employeeId
								? 'selected-employee-row' : ''}">

								<td class="center"><c:out
										value="${employee.employmentType}" /></td>

								<td><a class="employee-name-link"
									href="${savedEmployeeSelectUrl}"> <c:out
											value="${employee.koreanName}" />
								</a></td>

								<td><c:out
										value="${empty employee.departmentName
										? '-' : employee.departmentName}" />
								</td>

								<td class="amount"><fmt:formatNumber
										value="${employee.netPayment}" pattern="#,##0" /></td>

							</tr>

						</c:forEach>

						<c:forEach var="employee" items="${pendingEmployees}">

							<c:url var="pendingEmployeeSelectUrl"
								value="/wage/dailyPaymentInput.do">

								<c:param name="wageMonth" value="${wageMonth}" />

								<c:param name="wagePeriod" value="${wagePeriod}" />

								<c:param name="employeeId" value="${employee.employeeId}" />

								<c:forEach var="pending" items="${allPendingEmployees}">

									<c:param name="pendingEmployeeId" value="${pending.employeeId}" />

								</c:forEach>

							</c:url>

							<tr
								class="employee-select-row
								${selectedEmployeePending == true
								and selectedEmployeeId == employee.employeeId
								? 'selected-employee-row' : ''}">

								<td class="center"><c:out
										value="${employee.employmentType}" /></td>

								<td><a class="employee-name-link"
									href="${pendingEmployeeSelectUrl}"> <c:out
											value="${employee.koreanName}" />
								</a> <span class="pending-mark">（未保存）</span></td>

								<td><c:out
										value="${empty employee.departmentName
										? '-' : employee.departmentName}" />
								</td>

								<td class="amount">0</td>

							</tr>

						</c:forEach>

						<c:if
							test="${empty savedEmployees
							and empty pendingEmployees}">

							<tr>
								<td colspan="4" class="center">給与対象として登録された日雇い社員がいません。</td>
							</tr>

						</c:if>

					</tbody>

				</table>

			</div>

		</section>

		<section class="payroll-pane">

			<form id="dailyWagePaymentInputForm" class="daily-input-panel"
				method="post"
				action="${pageContext.request.contextPath}/wage/dailyPaymentInputCalculate.do">

				<input type="hidden" name="employeeId"
					value="<c:out value='${selectedEmployeeId}' />"> <input
					type="hidden" name="wageMonth"
					value="<c:out value='${wageMonth}' />"> <input
					type="hidden" name="wagePeriod"
					value="<c:out value='${wagePeriod}' />">

				<c:forEach var="pending" items="${allPendingEmployees}">
					<input type="hidden" name="pendingEmployeeId"
						value="<c:out value='${pending.employeeId}' />">
				</c:forEach>

				<div class="daily-input-grid">

					<div class="daily-record-column">

						<table class="daily-record-table">

							<thead>
								<tr>
									<th>日付</th>
									<th>支給率</th>
									<th>支給額</th>
									<th>所得税</th>
									<th>住民税</th>
								</tr>
							</thead>

							<tbody>

								<c:choose>

									<c:when
										test="${not empty workResult
										and not empty workResult.workRows}">

										<c:forEach var="workRow" items="${workResult.workRows}">

											<tr>
												<td><c:out value="${workRow.workDate}" /></td>
												<td><fmt:formatNumber value="${workRow.paymentRate}"
														pattern="0.0##" /></td>
												<td><fmt:formatNumber value="${workRow.paymentAmount}"
														pattern="#,##0" /></td>
												<td class="source-tax"><fmt:formatNumber
														value="${workRow.incomeTax}" pattern="#,##0" /></td>
												<td class="source-tax"><fmt:formatNumber
														value="${workRow.localTax}" pattern="#,##0" /></td>
											</tr>

										</c:forEach>

									</c:when>

									<c:when test="${wageInputEnabled == true}">
										<tr class="daily-empty-row">
											<td colspan="5">精算期間内に日雇い勤務記録がありません。</td>
										</tr>
									</c:when>

									<c:otherwise>
										<tr class="daily-empty-row">
											<td colspan="5">社員を選択してください。</td>
										</tr>
									</c:otherwise>

								</c:choose>

							</tbody>

						</table>

					</div>

					<div class="deduction-column">

						<div class="deduction-header">

							<strong>控除項目</strong>

							<div class="deduction-header-actions">

								<button type="submit"
									<c:if test="${not wageInputEnabled}">disabled</c:if>>
									4大保険</button>

							</div>

						</div>

						<c:choose>

							<c:when test="${not empty deductionItems}">

								<c:forEach var="item" items="${deductionItems}">

									<div class="deduction-item-row">

										<div>
											<c:out value="${item.wageTypeName}" />
										</div>

										<div>
											<input type="hidden" name="wageTypeId"
												value="<c:out value='${item.wageTypeId}' />"> <input
												type="number" name="wageValue"
												data-wage-type-id="<c:out value='${item.wageTypeId}' />"
												value="<c:out value='${item.wageValue}' />" min="0" step="1"
												required
												<c:if test="${not wageInputEnabled}">disabled</c:if>>
										</div>

									</div>

								</c:forEach>

							</c:when>

							<c:otherwise>
								<div class="deduction-empty">現在の給与回次に表示する控除項目はありません。</div>
							</c:otherwise>

						</c:choose>

					</div>

				</div>

				<div class="daily-subtotals">

					<div class="daily-subtotal">
						<span>支給総額</span> <strong> <fmt:formatNumber
								value="${empty currentTotalPayment
									? 0 : currentTotalPayment}"
								pattern="#,##0" />ウォン
						</strong>
					</div>

					<div class="daily-subtotal">
						<span>控除総額</span> <strong> <fmt:formatNumber
								value="${empty currentTotalDeduction
									? 0 : currentTotalDeduction}"
								pattern="#,##0" />ウォン
						</strong>
					</div>

				</div>

				<div class="daily-net-total">
					差引支給額：
					<fmt:formatNumber
						value="${empty currentNetPayment
							? 0 : currentNetPayment}"
						pattern="#,##0" />
					ウォン
				</div>

				<c:url var="dailyContentClearUrl" value="/wage/dailyPaymentInput.do">

					<c:param name="wageMonth" value="${wageMonth}" />
					<c:param name="wagePeriod" value="${wagePeriod}" />

					<c:forEach var="pending" items="${allPendingEmployees}">
						<c:param name="pendingEmployeeId" value="${pending.employeeId}" />
					</c:forEach>

				</c:url>
				<div class="daily-form-actions">

					<button type="submit"
						formaction="${pageContext.request.contextPath}/wage/dailyPaymentInputSave.do"
						<c:if test="${not wageInputEnabled}">disabled</c:if>>保存</button>

					<button type="button" id="dailyContentClearButton"
						data-clear-url="<c:out value='${dailyContentClearUrl}' />"
						<c:if test="${not wageInputEnabled}">disabled</c:if>>入力内容を
						クリア</button>

				</div>

			</form>

		</section>

	</div>

	<section class="payroll-summary">

		<h2>給与総合情報</h2>

		<div class="payroll-summary-grid">

			<div class="payroll-summary-card summary-count">
				<span>月間合計</span> <strong> <c:out
						value="${empty monthlyEmployeeCount
						? 0 : monthlyEmployeeCount}" />件
				</strong>
			</div>

			<div class="payroll-summary-card summary-payment">
				<span>支給総額</span> <strong> <fmt:formatNumber
						value="${empty monthlyTotalPayment
							? 0 : monthlyTotalPayment}"
						pattern="#,##0" />ウォン
				</strong>
			</div>

			<div class="payroll-summary-card summary-deduction">
				<span>控除総額</span> <strong> <fmt:formatNumber
						value="${empty monthlyTotalDeduction
							? 0 : monthlyTotalDeduction}"
						pattern="#,##0" />ウォン
				</strong>
			</div>

			<div class="payroll-summary-card summary-net">
				<span>差引支給額</span> <strong> <fmt:formatNumber
						value="${empty monthlyNetPayment
							? 0 : monthlyNetPayment}"
						pattern="#,##0" />ウォン
				</strong>
			</div>

		</div>

	</section>

	<script>
	(function() {

		const PAGE_SIZE = 8;

		const openButton = document.getElementById(
			"employeeSelectOpenButton");

		const dialog = document.getElementById(
			"employeeSelectDialog");

		const addForm = document.getElementById(
			"employeeModalAddForm");

		const closeButton = document.getElementById(
			"employeeModalCloseButton");

		const submitButton = document.getElementById(
			"employeeModalSubmitButton");

		const searchInput = document.getElementById(
			"employeeModalSearchInput");

		const searchButton = document.getElementById(
			"employeeModalSearchButton");

		const departmentFilter = document.getElementById(
			"employeeModalDepartmentFilter");

		const positionFilter = document.getElementById(
			"employeeModalPositionFilter");

		const statusFilter = document.getElementById(
			"employeeModalStatusFilter");

		const selectAllCheckbox = document.getElementById(
			"employeeModalSelectAll");

		const previousPageButton = document.getElementById(
			"employeeModalPreviousPage");

		const nextPageButton = document.getElementById(
			"employeeModalNextPage");

		const pageInfo = document.getElementById(
			"employeeModalPageInfo");

		const noResultRow = document.getElementById(
			"employeeModalNoResultRow");

		if (!openButton || !dialog || !addForm
			|| !closeButton || !submitButton
			|| !searchInput || !searchButton
			|| !departmentFilter || !positionFilter
			|| !statusFilter || !selectAllCheckbox
			|| !previousPageButton || !nextPageButton
			|| !pageInfo || !noResultRow) {

			return;
		}

		const rows = Array.from(
			dialog.querySelectorAll(".employee-modal-row"));

		const employeeCheckboxes = Array.from(
			dialog.querySelectorAll(
				".employee-modal-checkbox"));

		let filteredRows = rows.slice();
		let currentPage = 1;

		function normalize(value) {
			return (value || "").trim().toLowerCase();
		}

		function populateFilter(filter, dataName) {

			const values = new Set();

			rows.forEach(function(row) {

				const value =
					(row.dataset[dataName] || "").trim();

				if (value) {
					values.add(value);
				}
			});

			Array.from(values).sort(function(first, second) {
				return first.localeCompare(second, "ko");
			}).forEach(function(value) {

				const option =
					document.createElement("option");

				option.value = value;
				option.textContent = value;

				filter.appendChild(option);
			});
		}

		function getCurrentPageRows() {

			const startIndex =
				(currentPage - 1) * PAGE_SIZE;

			return filteredRows.slice(
				startIndex,
				startIndex + PAGE_SIZE);
		}

		function updateRowSelection(row, checked) {

			row.classList.toggle(
				"selected-modal-row",
				checked);
		}

		function updateSelectAllState() {

			const pageCheckboxes =
				getCurrentPageRows().map(function(row) {

					return row.querySelector(
						".employee-modal-checkbox");

				}).filter(Boolean);

			const selectedCount =
				pageCheckboxes.filter(function(checkbox) {
					return checkbox.checked;
				}).length;

			selectAllCheckbox.disabled =
				pageCheckboxes.length === 0;

			selectAllCheckbox.checked =
				pageCheckboxes.length > 0
				&& selectedCount === pageCheckboxes.length;

			selectAllCheckbox.indeterminate =
				selectedCount > 0
				&& selectedCount < pageCheckboxes.length;
		}

		function renderPage() {

			const pageCount = Math.max(
				1,
				Math.ceil(filteredRows.length / PAGE_SIZE));

			if (currentPage > pageCount) {
				currentPage = pageCount;
			}

			rows.forEach(function(row) {
				row.style.display = "none";
			});

			getCurrentPageRows().forEach(function(row) {
				row.style.display = "table-row";
			});

			noResultRow.style.display =
				filteredRows.length === 0
					? "table-row" : "none";

			pageInfo.textContent =
				currentPage + " / " + pageCount;

			previousPageButton.disabled =
				currentPage <= 1;

			nextPageButton.disabled =
				currentPage >= pageCount;

			updateSelectAllState();
		}

		function applyFilters() {

			const searchText =
				normalize(searchInput.value);

			const department =
				departmentFilter.value;

			const position =
				positionFilter.value;

			const status =
				statusFilter.value;

			filteredRows = rows.filter(function(row) {

				const employeeText = normalize(
					row.dataset.employeeId
					+ " "
					+ row.dataset.employeeName);

				return (!searchText
						|| employeeText.includes(searchText))
					&& (!department
						|| row.dataset.department === department)
					&& (!position
						|| row.dataset.position === position)
					&& (!status
						|| row.dataset.status === status);
			});

			currentPage = 1;
			renderPage();
		}

		populateFilter(departmentFilter, "department");
		populateFilter(positionFilter, "position");
		populateFilter(statusFilter, "status");

		rows.forEach(function(row) {

			const checkbox = row.querySelector(
				".employee-modal-checkbox");

			if (!checkbox) {
				return;
			}

			row.addEventListener("click", function(event) {

				if (event.target.closest("input")) {
					return;
				}

				checkbox.checked = !checkbox.checked;

				updateRowSelection(
					row,
					checkbox.checked);

				updateSelectAllState();
			});

			checkbox.addEventListener("change", function() {

				updateRowSelection(
					row,
					checkbox.checked);

				updateSelectAllState();
			});
		});

		openButton.addEventListener("click", function() {

			searchInput.value = "";
			departmentFilter.value = "";
			positionFilter.value = "";

			const hasActiveStatus =
				Array.from(statusFilter.options)
					.some(function(option) {
						return option.value === "재직";
					});

			statusFilter.value =
				hasActiveStatus ? "재직" : "";

			employeeCheckboxes.forEach(function(checkbox) {

				checkbox.checked = false;

				const row = checkbox.closest(
					".employee-modal-row");

				if (row) {
					updateRowSelection(row, false);
				}
			});

			currentPage = 1;
			applyFilters();

			submitButton.disabled = false;
			dialog.showModal();
		});

		closeButton.addEventListener("click", function() {
			dialog.close();
		});

		searchInput.addEventListener("input", applyFilters);

		searchInput.addEventListener(
			"keydown",
			function(event) {

				if (event.key !== "Enter") {
					return;
				}

				event.preventDefault();
				applyFilters();
			});

		searchButton.addEventListener(
			"click",
			applyFilters);

		departmentFilter.addEventListener(
			"change",
			applyFilters);

		positionFilter.addEventListener(
			"change",
			applyFilters);

		statusFilter.addEventListener(
			"change",
			applyFilters);

		selectAllCheckbox.addEventListener(
			"change",
			function() {

				const checked =
					selectAllCheckbox.checked;

				getCurrentPageRows().forEach(function(row) {

					const checkbox = row.querySelector(
						".employee-modal-checkbox");

					if (!checkbox) {
						return;
					}

					checkbox.checked = checked;
					updateRowSelection(row, checked);
				});

				updateSelectAllState();
			});

		previousPageButton.addEventListener(
			"click",
			function() {

				if (currentPage <= 1) {
					return;
				}

				currentPage--;
				renderPage();
			});

		nextPageButton.addEventListener(
			"click",
			function() {

				const pageCount = Math.max(
					1,
					Math.ceil(
						filteredRows.length / PAGE_SIZE));

				if (currentPage >= pageCount) {
					return;
				}

				currentPage++;
				renderPage();
			});

		addForm.addEventListener(
			"submit",
			function(event) {

				const hasSelection =
					employeeCheckboxes.some(
						function(checkbox) {
							return checkbox.checked;
						});

				if (!hasSelection) {

					event.preventDefault();

					window.alert(
						"追加する社員を選択してください。");

					return;
				}

				submitButton.disabled = true;
			});

		dialog.addEventListener("close", function() {
			submitButton.disabled = false;
		});

		renderPage();

	})();
	</script>

	<script>
	(function() {

		document.querySelectorAll(
			".employee-select-row"
		).forEach(function(employeeRow) {

			employeeRow.addEventListener(
				"click",
				function(event) {

					if (event.target.closest("a")) {
						return;
					}

					const selectLink =
						employeeRow.querySelector("a");

					if (selectLink) {
						window.location.assign(
							selectLink.href);
					}
				});
		});

	})();
	</script>

	<script>
	(function() {

		const deleteForm =
			document.getElementById(
				"employeeDeleteForm");

		const deleteConfirmedInput =
			document.getElementById(
				"employeeDeleteConfirmed");

		const deleteButton =
			document.getElementById(
				"employeeDeleteButton");

		const employeeIdInput =
			deleteForm
				? deleteForm.elements["employeeId"]
				: null;

		if (!deleteForm
			|| !deleteConfirmedInput
			|| !deleteButton
			|| !employeeIdInput) {

			return;
		}

		deleteForm.addEventListener(
			"submit",
			function(event) {

				event.preventDefault();

				deleteConfirmedInput.value =
					"false";

				if (!employeeIdInput.value.trim()) {

					window.alert(
						"選択された社員がいません。");

					return;
				}

				const confirmed =
					window.confirm(
						"選択した社員を削除しますか？");

				if (!confirmed) {
					return;
				}

				deleteConfirmedInput.value =
					"true";

				deleteButton.disabled =
					true;

				deleteForm.submit();
			});

	})();
	</script>

	<script>
	(function() {

		const deleteAllForm =
			document.getElementById(
				"employeeDeleteAllForm");

		const deleteAllConfirmedInput =
			document.getElementById(
				"employeeDeleteAllConfirmed");

		const deleteAllFinalConfirmedInput =
			document.getElementById(
				"employeeDeleteAllFinalConfirmed");

		const deleteAllButton =
			document.getElementById(
				"employeeDeleteAllButton");

		const hasCurrentEmployees =
			${not empty savedEmployees
				or not empty allPendingEmployees};

		if (!deleteAllForm
			|| !deleteAllConfirmedInput
			|| !deleteAllFinalConfirmedInput
			|| !deleteAllButton) {

			return;
		}

		deleteAllForm.addEventListener(
			"submit",
			function(event) {

				event.preventDefault();

				deleteAllConfirmedInput.value =
					"false";

				deleteAllFinalConfirmedInput.value =
					"false";

				if (!hasCurrentEmployees) {

					window.alert(
						"追加された社員がいません。");

					return;
				}

				const confirmed =
					window.confirm(
						"■ 注意！\n"
						+ "- [すべて] 給与入力情報を削除しますか？");

				if (!confirmed) {
					return;
				}

				const finalConfirmed =
					window.confirm(
						"注意！\n"
						+ "削除した給与入力情報は復元できません。\n"
						+ "削除しますか？");

				if (!finalConfirmed) {
					return;
				}

				deleteAllConfirmedInput.value =
					"true";

				deleteAllFinalConfirmedInput.value =
					"true";

				deleteAllButton.disabled =
					true;

				deleteAllForm.submit();
			});

	})();
	</script>

	<script>
	(function() {

		const searchForm = document.getElementById(
			"workspaceSearchForm");

		const wageMonthInput = document.getElementById(
			"wageMonth");

		const wageYearSelect = document.getElementById(
			"wageYear");

		const wageMonthSelect = document.getElementById(
			"wageMonthPart");

		const wagePeriodSelect = document.getElementById(
			"wagePeriod");

		if (!searchForm || !wageMonthInput
			|| !wageYearSelect || !wageMonthSelect
			|| !wagePeriodSelect) {

			return;
		}

		const wageMonthMatch =
			/^(\d{4})-(0[1-9]|1[0-2])$/.exec(
				wageMonthInput.value);

		const today = new Date();

		const selectedYear = wageMonthMatch
			? Number(wageMonthMatch[1])
			: today.getFullYear();

		const selectedMonth = wageMonthMatch
			? wageMonthMatch[2]
			: String(today.getMonth() + 1).padStart(2, "0");

		const firstYear = Math.min(2005, selectedYear);

		const lastYear = Math.max(
			today.getFullYear() + 1,
			selectedYear);

		for (let year = firstYear;
			year <= lastYear;
			year++) {

			const option =
				document.createElement("option");

			option.value = String(year);
			option.textContent = year + "年";

			wageYearSelect.appendChild(option);
		}

		wageYearSelect.value = String(selectedYear);
		wageMonthSelect.value = selectedMonth;

		function moveWorkspace() {

			const nextWageMonth =
				wageYearSelect.value
					+ "-"
					+ wageMonthSelect.value;

			const url = new URL(
				searchForm.action,
				window.location.origin);

			url.searchParams.set(
				"wageMonth",
				nextWageMonth);

			url.searchParams.set(
				"wagePeriod",
				wagePeriodSelect.value);

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

		const clearButton = document.getElementById(
			"dailyContentClearButton");

		if (!clearButton) {
			return;
		}

		clearButton.addEventListener(
			"click",
			function() {

				const clearUrl =
					clearButton.dataset.clearUrl;

				if (!clearUrl) {
					return;
				}

				window.location.assign(clearUrl);
			});

	})();
	</script>

	<script>
	(function() {

		const hasPending =
			${not empty allPendingEmployees};

		const currentUrl =
			new URL(window.location.href);

		const hasAddCommand =
			currentUrl.searchParams.has("addEmployeeId");

		const hasSavedMessage =
			currentUrl.searchParams.get("saved") === "true";

		const calculationAttempted =
			${calculationAttempted == true};

		if (!hasPending
			&& !hasAddCommand
			&& !hasSavedMessage
			&& !calculationAttempted) {

			return;
		}

		const cleanUrl = new URL(
			"${pageContext.request.contextPath}/wage/dailyPaymentInput.do",
			window.location.origin);

		cleanUrl.searchParams.set(
			"wageMonth",
			"<c:out value='${wageMonth}' />");

		cleanUrl.searchParams.set(
			"wagePeriod",
			"<c:out value='${wagePeriod}' />");

		const selectedEmployeeSaved =
			${selectedEmployeeSaved == true};

		if (selectedEmployeeSaved) {

			cleanUrl.searchParams.set(
				"employeeId",
				"<c:out value='${selectedEmployeeId}' />");
		}

		/*
		 * pendingは現在レンダリングされているリンクとフォームでは維持するが
		 * URLからは削除する。
		 *
		 * したがって、F5時には未保存の社員は再表示されない。
		 */
		window.history.replaceState(
			null,
			"",
			cleanUrl.pathname + cleanUrl.search);

	})();
	</script>

</body>
</html>