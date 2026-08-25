<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与入力</title>

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
	width: 100%;
	table-layout: fixed;
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
	min-height: 58px;
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

@media ( max-width : 900px) {
	.payroll-summary-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media ( max-width : 560px) {
	.payroll-summary-grid {
		grid-template-columns: 1fr;
	}
}

.payroll-workspace {
	display: grid;
	grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
	gap: 24px;
	align-items: start;
}

.payroll-pane {
	min-width: 0;
}

.table-scroll {
	width: 100%;
	overflow-x: auto;
}

.table-scroll table {
	margin-bottom: 0;
}

@media ( max-width : 1200px) {
	.payroll-workspace {
		grid-template-columns: 1fr;
	}
}

.employee-toolbar {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 16px;
}

.employee-add-form {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 18px;
}

.income-tabs {
	display: grid;
	grid-template-columns: 1fr 1fr;
	margin-bottom: 16px;
	border-bottom: 2px solid #333333;
}

.income-tab {
	display: block;
	padding: 11px 12px;
	background-color: #aaaaaa;
	color: #ffffff;
	font-weight: bold;
	text-align: center;
	text-decoration: none;
}

.income-tab.active {
	background-color: #009b95;
}

.wage-input-form {
	min-width: 0;
}

.wage-item-grid {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
	border-top: 2px solid #333333;
	border-bottom: 1px solid #dddddd;
}

.wage-item-column {
	min-width: 0;
}

.wage-item-column:first-child {
	border-right: 1px solid #dddddd;
}

.wage-item-column-header {
	padding: 10px;
	font-weight: bold;
	text-align: center;
}

.payment-header {
	background-color: #f3f8fb;
	color: #0759d1;
}

.deduction-header {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 42px;
	background-color: #fff4f1;
	color: #e44343;
	box-sizing: border-box;
}

.wage-auto-calculate-button {
	position: absolute;
	top: 50%;
	right: 8px;
	transform: translateY(-50%);
	padding: 3px 7px;
	border: 0;
	border-radius: 2px;
	background-color: #111111;
	color: #ffffff;
	font-size: 12px;
}

.wage-auto-calculate-button:disabled {
	opacity: 0.45;
	cursor: default;
}

.wage-item-row {
	display: grid;
	grid-template-columns: minmax(0, 1fr) minmax(110px, 46%);
	align-items: center;
	gap: 8px;
	min-height: 42px;
	padding: 4px 10px;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.wage-item-name {
	min-width: 0;
}

.wage-item-amount {
	min-width: 0;
}

.wage-item-amount input {
	width: 100%;
	box-sizing: border-box;
	text-align: right;
}

.tax-free-mark {
	color: red;
	font-size: 11px;
}

.wage-subtotals {
	display: grid;
	grid-template-columns: 1fr 1fr;
}

.wage-subtotal {
	display: flex;
	justify-content: space-between;
	padding: 12px 16px;
	background-color: #f7fafc;
	border-bottom: 1px solid #dddddd;
	box-sizing: border-box;
}

.wage-subtotal:first-child {
	color: #0759d1;
	border-right: 1px solid #dddddd;
}

.wage-subtotal:last-child {
	color: #e44343;
}

.wage-net-total {
	padding: 14px;
	background-color: #315d7d;
	color: #ffffff;
	font-size: 18px;
	font-weight: bold;
	text-align: center;
}

.wage-form-actions {
	display: flex;
	justify-content: center;
	gap: 8px;
	margin-top: 15px;
}

@media ( max-width : 700px) {
	.wage-item-grid {
		grid-template-columns: 1fr;
	}
	.wage-item-column:first-child {
		border-right: 0;
	}
}

.employee-select-dialog {
	border: 0;
	border-radius: 14px;
	width: min(900px, calc(100vw - 40px));
	max-width: 900px;
	max-height: calc(100vh - 40px);
	padding: 24px;
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
	box-sizing: border-box;
}

.employee-modal-table-wrap {
	min-height: 360px;
	max-height: 430px;
	overflow: auto;
	border: 1px solid #dddddd;
}

.employee-modal-table {
	width: 100%;
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

@media ( max-width : 760px) {
	.employee-modal-filters {
		grid-template-columns: 1fr 1fr;
	}
	.employee-modal-search {
		grid-column: 1/-1;
	}
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<c:set var="visibleEmployeeCount"
		value="${fn:length(savedEmployees) + fn:length(pendingEmployees)}" />

	<c:set var="monthlyEmployeeCount" value="${fn:length(savedEmployees)}" />

	<c:set var="monthlyTotalPayment" value="${0}" />
	<c:set var="monthlyTotalDeduction" value="${0}" />
	<c:set var="monthlyNetPayment" value="${0}" />

	<c:forEach var="employee" items="${savedEmployees}">

		<c:set var="monthlyTotalPayment"
			value="${monthlyTotalPayment + employee.totalPayment}" />

		<c:set var="monthlyTotalDeduction"
			value="${monthlyTotalDeduction + employee.totalDeduction}" />

		<c:set var="monthlyNetPayment"
			value="${monthlyNetPayment + employee.netPayment}" />

	</c:forEach>

	<c:set var="wageInputEnabled"
		value="${selectedEmployeeSaved == true or selectedEmployeePending == true}" />

	<c:set var="currentWageTotalPayment" value="${0}" />
	<c:set var="currentWageTotalDeduction" value="${0}" />

	<c:if test="${wageInputEnabled}">

		<c:forEach var="item" items="${wageItems}">

			<c:choose>

				<c:when test="${item.itemType eq 'P'}">
					<c:set var="currentWageTotalPayment"
						value="${currentWageTotalPayment + item.wageValue}" />
				</c:when>

				<c:when test="${item.itemType eq 'D'}">
					<c:set var="currentWageTotalDeduction"
						value="${currentWageTotalDeduction + item.wageValue}" />
				</c:when>

			</c:choose>

		</c:forEach>

	</c:if>

	<c:set var="currentWageNetPayment"
		value="${currentWageTotalPayment - currentWageTotalDeduction}" />

	<h1>給与入力</h1>

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
				for="wageYear">帰属年月</label> <select id="wageYear" required>
			</select> <select id="wageMonthPart" required>

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
						<c:if test="${wagePeriod eq period.toString()}">
							selected
						</c:if>>

						<c:out value="${period}" />回

					</option>

				</c:forEach>

			</select>

		</div>


		<div class="form-row">

			<label for="settlementStartDate"> 精算開始日 </label> <input type="date"
				id="settlementStartDate" name="settlementStartDate"
				value="<c:out value='${settlementStartDate}' />" readonly> <label
				for="settlementEndDate"> 精算終了日 </label> <input type="date"
				id="settlementEndDate" name="settlementEndDate"
				value="<c:out value='${settlementEndDate}' />" readonly> <label
				for="wagePaymentDate"> 給与支給日 </label> <input type="date"
				id="wagePaymentDate" name="wagePaymentDate"
				value="<c:out value='${wagePaymentDate}' />" readonly>

		</div>


		<c:if test="${not empty errorMessage}">

			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>

		</c:if>

	</form>

	<dialog id="previousWageDialog" class="previous-wage-dialog">

	<h2>給与年月の選択</h2>

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

			<option value="">帰属年月・給与回次を選択</option>

			<c:forEach var="source" items="${previousWageSourceOptions}">

				<option value="<c:out value='${source.wageMonth}' />"
					data-wage-month="<c:out value='${source.wageMonth}' />"
					data-wage-period="<c:out value='${source.wagePeriod}' />">
					<c:out value="${fn:substring(source.wageMonth, 0, 4)}" />年
					<c:out value="${fn:substring(source.wageMonth, 5, 7)}" />月
					<fmt:formatNumber value="${source.wagePeriod}" pattern="00" />回
				</option>

			</c:forEach>

		</select>

		<div class="previous-wage-dialog-actions">

			<button type="button" id="previousWageSubmitButton">給与情報を
				読み込む</button>

			<button type="button" id="previousWageCloseButton">キャンセル</button>

		</div>

	</form>

	</dialog>

	<dialog id="employeeSelectDialog" class="employee-select-dialog">

	<h2>給与支給対象社員の選択</h2>

	<form id="employeeModalAddForm" method="get"
		action="${pageContext.request.contextPath}/wage/paymentInput.do">

		<input type="hidden" name="wageMonth"
			value="<c:out value='${wageMonth}' />"> <input type="hidden"
			name="wagePeriod" value="<c:out value='${wagePeriod}' />"> <input
			type="hidden" name="incomeType"
			value="<c:out value='${incomeType}' />">

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

			<select id="employeeModalDepartmentFilter" aria-label="部署別">
				<option value="">部署別</option>
			</select> <select id="employeeModalPositionFilter" aria-label="役職別">
				<option value="">役職別</option>
			</select> <select id="employeeModalStatusFilter" aria-label="在職状態">
				<option value="">在職状態</option>
			</select>

		</div>

		<div class="employee-modal-table-wrap">

			<table class="employee-modal-table">

				<thead>
					<tr>
						<th><input type="checkbox" id="employeeModalSelectAll"
							aria-label="現在のページをすべて選択"></th>
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
								value="<c:out value='${employee.employeeId}' />"
								aria-label="<c:out value='${employee.koreanName}' />を選択">
							</td>

							<td><c:out value="${employee.employmentType}" /></td>

							<td><c:out value="${employee.employeeId}" /></td>

							<td><c:out value="${employee.koreanName}" /></td>

							<td><c:out
									value="${empty employee.departmentName ? '-' : employee.departmentName}" />
							</td>

							<td><c:out
									value="${empty employee.positionName ? '-' : employee.positionName}" />
							</td>

							<td><c:out
									value="${empty employee.status ? '-' : employee.status}" /></td>

						</tr>

					</c:forEach>

					<tr id="employeeModalNoResultRow" style="display: none;">
						<td colspan="7">条件に一致する社員がいません。</td>
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

		<section class="payroll-pane employee-pane">

			<div class="employee-toolbar">

				<button type="button" id="previousWageOpenButton"
					<c:if test="${empty previousWageSourceOptions}">
						disabled
					</c:if>>
					過去給与の読み込み</button>

				<form id="employeeDeleteForm" method="post"
					action="${pageContext.request.contextPath}/wage/paymentInputDelete.do"
					style="display: inline; margin-left: 8px;">

					<input type="hidden" name="deleteMode" value="selected"> <input
						type="hidden" name="employeeId"
						value="<c:out value='${selectedEmployeeId}' />"> <input
						type="hidden" name="wageMonth"
						value="<c:out value='${wageMonth}' />"> <input
						type="hidden" name="wagePeriod"
						value="<c:out value='${wagePeriod}' />"> <input
						type="hidden" name="incomeType"
						value="<c:out value='${incomeType}' />"> <input
						type="hidden" id="employeeDeleteConfirmed" name="deleteConfirmed"
						value="false">

					<c:forEach var="pending" items="${allPendingEmployees}">

						<input type="hidden" name="pendingEmployeeId"
							value="<c:out value='${pending.employeeId}' />">

					</c:forEach>

					<button type="submit" id="employeeDeleteButton">選択削除</button>

				</form>

				<form id="employeeDeleteAllForm" method="post"
					action="${pageContext.request.contextPath}/wage/paymentInputDelete.do"
					style="display: inline; margin-left: 8px;">

					<input type="hidden" name="deleteMode" value="all"> <input
						type="hidden" name="wageMonth"
						value="<c:out value='${wageMonth}' />"> <input
						type="hidden" name="wagePeriod"
						value="<c:out value='${wagePeriod}' />"> <input
						type="hidden" name="incomeType"
						value="<c:out value='${incomeType}' />"> <input
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

			</div>

			<div class="employee-add-form">

				<button type="button" id="employeeSelectOpenButton">新規追加</button>

			</div>

			<h2>社員一覧</h2>

			<div style="margin-bottom: 10px;">
				合計
				<c:out value="${visibleEmployeeCount}" />
				名
			</div>

			<div class="table-scroll">
				<table style="margin-bottom: 25px;">

					<thead>
						<tr>
							<th>社員ID</th>
							<th>区分</th>
							<th>氏名</th>
							<th>部署</th>
							<th>支給総額</th>
							<th>控除総額</th>
							<th>差引支給額</th>
						</tr>
					</thead>

					<tbody>

						<c:forEach var="employee" items="${savedEmployees}">

							<tr
								class="employee-select-row ${selectedEmployeeSaved == true
								and selectedEmployeeId == employee.employeeId
									? 'selected-employee-row' : ''}">

								<td class="center"><c:out value="${employee.employeeId}" />
								</td>

								<td class="center"><c:out
										value="${employee.employmentType}" /></td>

								<td><c:url var="employeeSelectUrl"
										value="/wage/paymentInput.do">

										<c:param name="wageMonth" value="${wageMonth}" />

										<c:param name="wagePeriod" value="${wagePeriod}" />

										<c:param name="incomeType" value="${incomeType}" />

										<c:param name="employeeId" value="${employee.employeeId}" />

										<c:forEach var="pending" items="${allPendingEmployees}">

											<c:param name="pendingEmployeeId"
												value="${pending.employeeId}" />

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

							<tr
								class="employee-select-row ${selectedEmployeePending == true
								and selectedEmployeeId == employee.employeeId
									? 'selected-employee-row' : ''}">

								<td class="center"><c:out value="${employee.employeeId}" />
								</td>

								<td class="center"><c:out
										value="${employee.employmentType}" /></td>

								<td><c:url var="pendingEmployeeSelectUrl"
										value="/wage/paymentInput.do">

										<c:param name="wageMonth" value="${wageMonth}" />

										<c:param name="wagePeriod" value="${wagePeriod}" />

										<c:param name="incomeType" value="${incomeType}" />

										<c:param name="employeeId" value="${employee.employeeId}" />

										<c:forEach var="pending" items="${allPendingEmployees}">

											<c:param name="pendingEmployeeId"
												value="${pending.employeeId}" />

										</c:forEach>

									</c:url> <a href="${pendingEmployeeSelectUrl}"> <c:out
											value="${employee.koreanName}" />
								</a>（未保存）</td>

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
			</div>

		</section>

		<section class="payroll-pane wage-pane">

			<div class="income-tabs">

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
					class="income-tab ${incomeType eq 'worker' ? 'active' : ''}">
					一般所得 </a> <a href="${businessIncomeUrl}"
					class="income-tab ${incomeType eq 'business' ? 'active' : ''}">
					事業所得・雑所得 </a>

			</div>

			<c:if test="${not empty wageItems}">

				<form id="wagePaymentInputForm" class="wage-input-form"
					method="post"
					action="${pageContext.request.contextPath}/wage/paymentInputCalculate.do">

					<input type="hidden" name="employeeId"
						value="<c:out value='${selectedEmployeeId}' />"> <input
						type="hidden" name="wageMonth"
						value="<c:out value='${wageMonth}' />"> <input
						type="hidden" name="wagePeriod"
						value="<c:out value='${wagePeriod}' />"> <input
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

					<div class="wage-item-grid">

						<div class="wage-item-column">

							<div class="wage-item-column-header payment-header">支給項目</div>

							<c:forEach var="item" items="${wageItems}">

								<c:if test="${item.itemType eq 'P'}">

									<div class="wage-item-row">

										<div class="wage-item-name">

											<c:out value="${item.wageTypeName}" />

											<c:if test="${item.taxableYn eq 'N'}">
												<span class="tax-free-mark">[非課税]</span>
											</c:if>

										</div>

										<div class="wage-item-amount">

											<input type="hidden" name="wageTypeId"
												value="<c:out value='${item.wageTypeId}' />"> <input
												type="number" name="wageValue" min="0" step="1"
												value="<c:out value='${item.wageValue}' />" required
												<c:if test="${not wageInputEnabled}">disabled</c:if>>

										</div>

									</div>

								</c:if>

							</c:forEach>

						</div>

						<div class="wage-item-column">

							<div class="wage-item-column-header deduction-header">

								<span>控除項目</span>

								<button type="submit" class="wage-auto-calculate-button"
									<c:if test="${not wageInputEnabled}">disabled</c:if>>
									自動計算</button>

							</div>

							<c:forEach var="item" items="${wageItems}">

								<c:if test="${item.itemType eq 'D'}">

									<div class="wage-item-row">

										<div class="wage-item-name">
											<c:out value="${item.wageTypeName}" />
										</div>

										<div class="wage-item-amount">

											<input type="hidden" name="wageTypeId"
												value="<c:out value='${item.wageTypeId}' />"> <input
												type="number" name="wageValue" min="0" step="1"
												value="<c:out value='${item.wageValue}' />" required
												<c:if test="${not wageInputEnabled}">disabled</c:if>>

										</div>

									</div>

								</c:if>

							</c:forEach>

						</div>

					</div>

					<div class="wage-subtotals">

						<div class="wage-subtotal">
							<span>支給総額</span> <strong> <fmt:formatNumber
									value="${currentWageTotalPayment}" pattern="#,##0" />ウォン
							</strong>
						</div>

						<div class="wage-subtotal">
							<span>控除総額</span> <strong> <fmt:formatNumber
									value="${currentWageTotalDeduction}" pattern="#,##0" />ウォン
							</strong>
						</div>

					</div>

					<div class="wage-net-total">
						差引支給額：
						<fmt:formatNumber value="${currentWageNetPayment}" pattern="#,##0" />
						ウォン
					</div>

					<c:url var="wageContentClearUrl" value="/wage/paymentInput.do">

						<c:param name="wageMonth" value="${wageMonth}" />
						<c:param name="wagePeriod" value="${wagePeriod}" />
						<c:param name="incomeType" value="${incomeType}" />

						<c:forEach var="pending" items="${allPendingEmployees}">
							<c:param name="pendingEmployeeId" value="${pending.employeeId}" />
						</c:forEach>

					</c:url>

					<div class="wage-form-actions">

						<button type="submit"
							formaction="${pageContext.request.contextPath}/wage/paymentInputSave.do"
							<c:if test="${not wageInputEnabled}">disabled</c:if>>保存
						</button>

						<button type="button" id="wageContentClearButton"
							data-clear-url="<c:out value='${wageContentClearUrl}' />"
							<c:if test="${not wageInputEnabled}">disabled</c:if>>入力内容を
							クリア</button>

					</div>

				</form>

			</c:if>

		</section>

	</div>

	<section class="payroll-summary">

		<h2>給与総合情報</h2>

		<div class="payroll-summary-grid">

			<div class="payroll-summary-card summary-count">
				<span>月間合計</span> <strong> <c:out
						value="${monthlyEmployeeCount}" />件
				</strong>
			</div>

			<div class="payroll-summary-card summary-payment">
				<span>支給総額</span> <strong> <fmt:formatNumber
						value="${monthlyTotalPayment}" pattern="#,##0" />ウォン
				</strong>
			</div>

			<div class="payroll-summary-card summary-deduction">
				<span>控除総額</span> <strong> <fmt:formatNumber
						value="${monthlyTotalDeduction}" pattern="#,##0" />ウォン
				</strong>
			</div>

			<div class="payroll-summary-card summary-net">
				<span>差引支給額</span> <strong> <fmt:formatNumber
						value="${monthlyNetPayment}" pattern="#,##0" />ウォン
				</strong>
			</div>

		</div>

	</section>

	<script>
	(function() {

		const PAGE_SIZE = 8;

		const openButton =
			document.getElementById(
				"employeeSelectOpenButton");

		const dialog =
			document.getElementById(
				"employeeSelectDialog");

		const addForm =
			document.getElementById(
				"employeeModalAddForm");

		const closeButton =
			document.getElementById(
				"employeeModalCloseButton");

		const submitButton =
			document.getElementById(
				"employeeModalSubmitButton");

		const searchInput =
			document.getElementById(
				"employeeModalSearchInput");

		const searchButton =
			document.getElementById(
				"employeeModalSearchButton");

		const departmentFilter =
			document.getElementById(
				"employeeModalDepartmentFilter");

		const positionFilter =
			document.getElementById(
				"employeeModalPositionFilter");

		const statusFilter =
			document.getElementById(
				"employeeModalStatusFilter");

		const selectAllCheckbox =
			document.getElementById(
				"employeeModalSelectAll");

		const previousPageButton =
			document.getElementById(
				"employeeModalPreviousPage");

		const nextPageButton =
			document.getElementById(
				"employeeModalNextPage");

		const pageInfo =
			document.getElementById(
				"employeeModalPageInfo");

		const noResultRow =
			document.getElementById(
				"employeeModalNoResultRow");

		if (!openButton
			|| !dialog
			|| !addForm
			|| !closeButton
			|| !submitButton
			|| !searchInput
			|| !searchButton
			|| !departmentFilter
			|| !positionFilter
			|| !statusFilter
			|| !selectAllCheckbox
			|| !previousPageButton
			|| !nextPageButton
			|| !pageInfo
			|| !noResultRow) {

			return;
		}

		const rows =
			Array.from(
				dialog.querySelectorAll(
					".employee-modal-row"));

		const employeeCheckboxes =
			Array.from(
				dialog.querySelectorAll(
					".employee-modal-checkbox"));

		let filteredRows =
			rows.slice();

		let currentPage = 1;

		function normalize(value) {

			return (value || "")
				.trim()
				.toLowerCase();
		}

		function populateFilter(
			filter,
			dataName) {

			const values =
				new Set();

			rows.forEach(
				function(row) {

					const value =
						(row.dataset[dataName] || "")
							.trim();

					if (value) {
						values.add(value);
					}
				});

			Array.from(values)
				.sort(
					function(first, second) {

						return first.localeCompare(
							second,
							"ko");
					})
				.forEach(
					function(value) {

						const option =
							document.createElement(
								"option");

						option.value = value;
						option.textContent = value;

						filter.appendChild(
							option);
					});
		}

		function getCurrentPageRows() {

			const startIndex =
				(currentPage - 1)
					* PAGE_SIZE;

			return filteredRows.slice(
				startIndex,
				startIndex + PAGE_SIZE);
		}

		function updateRowSelection(
			row,
			checked) {

			row.classList.toggle(
				"selected-modal-row",
				checked);
		}

		function updateSelectAllState() {

			const pageCheckboxes =
				getCurrentPageRows()
					.map(
						function(row) {

							return row.querySelector(
								".employee-modal-checkbox");
						})
					.filter(Boolean);

			const selectedCount =
				pageCheckboxes.filter(
					function(checkbox) {

						return checkbox.checked;
					}).length;

			selectAllCheckbox.disabled =
				pageCheckboxes.length === 0;

			selectAllCheckbox.checked =
				pageCheckboxes.length > 0
					&& selectedCount
						=== pageCheckboxes.length;

			selectAllCheckbox.indeterminate =
				selectedCount > 0
					&& selectedCount
						< pageCheckboxes.length;
		}

		function renderPage() {

			const pageCount =
				Math.max(
					1,
					Math.ceil(
						filteredRows.length
							/ PAGE_SIZE));

			if (currentPage > pageCount) {
				currentPage = pageCount;
			}

			rows.forEach(
				function(row) {

					row.style.display =
						"none";
				});

			getCurrentPageRows()
				.forEach(
					function(row) {

						row.style.display =
							"table-row";
					});

			noResultRow.style.display =
				filteredRows.length === 0
					? "table-row"
					: "none";

			pageInfo.textContent =
				currentPage
					+ " / "
					+ pageCount;

			previousPageButton.disabled =
				currentPage <= 1;

			nextPageButton.disabled =
				currentPage >= pageCount;

			updateSelectAllState();
		}

		function applyFilters() {

			const searchText =
				normalize(
					searchInput.value);

			const department =
				departmentFilter.value;

			const position =
				positionFilter.value;

			const status =
				statusFilter.value;

			filteredRows =
				rows.filter(
					function(row) {

						const employeeText =
							normalize(
								row.dataset.employeeId
									+ " "
									+ row.dataset.employeeName);

						return (!searchText
								|| employeeText.includes(
									searchText))
							&& (!department
								|| row.dataset.department
									=== department)
							&& (!position
								|| row.dataset.position
									=== position)
							&& (!status
								|| row.dataset.status
									=== status);
					});

			currentPage = 1;

			renderPage();
		}

		populateFilter(
			departmentFilter,
			"department");

		populateFilter(
			positionFilter,
			"position");

		populateFilter(
			statusFilter,
			"status");

		rows.forEach(
			function(row) {

				const checkbox =
					row.querySelector(
						".employee-modal-checkbox");

				if (!checkbox) {
					return;
				}

				row.addEventListener(
					"click",
					function(event) {

						if (event.target.closest(
								"input")) {

							return;
						}

						checkbox.checked =
							!checkbox.checked;

						updateRowSelection(
							row,
							checkbox.checked);

						updateSelectAllState();
					});

				checkbox.addEventListener(
					"change",
					function() {

						updateRowSelection(
							row,
							checkbox.checked);

						updateSelectAllState();
					});
			});

		openButton.addEventListener(
			"click",
			function() {

				searchInput.value = "";
				departmentFilter.value = "";
				positionFilter.value = "";

				/*
				 * Payzonと同様に、在職中の社員をデフォルトで表示する。
				 * 在職の状態値がない場合は、すべての状態を表示する。
				 */
				const hasActiveStatus =
					Array.from(
						statusFilter.options)
						.some(
							function(option) {

								return option.value
									=== "재직";
							});

				statusFilter.value =
					hasActiveStatus
						? "재직"
						: "";

				employeeCheckboxes.forEach(
					function(checkbox) {

						checkbox.checked =
							false;

						const row =
							checkbox.closest(
								".employee-modal-row");

						if (row) {

							updateRowSelection(
								row,
								false);
						}
					});

				currentPage = 1;
				applyFilters();

				submitButton.disabled =
					false;

				dialog.showModal();
			});

		closeButton.addEventListener(
			"click",
			function() {

				dialog.close();
			});

		searchInput.addEventListener(
			"input",
			applyFilters);

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

				getCurrentPageRows()
					.forEach(
						function(row) {

							const checkbox =
								row.querySelector(
									".employee-modal-checkbox");

							if (!checkbox) {
								return;
							}

							checkbox.checked =
								checked;

							updateRowSelection(
								row,
								checked);
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

				const pageCount =
					Math.max(
						1,
						Math.ceil(
							filteredRows.length
								/ PAGE_SIZE));

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

				submitButton.disabled =
					true;
			});

		dialog.addEventListener(
			"close",
			function() {

				submitButton.disabled =
					false;
			});

		renderPage();

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

		const employeeRows =
			document.querySelectorAll(
				".employee-select-row");

		employeeRows.forEach(
			function(employeeRow) {

				employeeRow.addEventListener(
					"click",
					function(event) {

						/*
						 * 社員名のリンクをクリックした場合は、
						 * 既存のリンク遷移をそのまま使用する。
						 */
						if (event.target.closest("a")) {
							return;
						}

						const selectLink =
							employeeRow.querySelector(
								"a");

						if (selectLink) {

							window.location.assign(
								selectLink.href);
						}
					});
			});

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
			${not empty savedEmployees or not empty pendingEmployees};

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
						"読み込む帰属年月と給与回次を選択してください。");

					return;
				}

				sourceWageMonthInput.value =
					selectedOption.dataset.wageMonth;

				sourceWagePeriodInput.value =
					selectedOption.dataset.wagePeriod;

				const confirmed =
					window.confirm(
						"登録済みの給与テーブルは削除され、\n"
						+ "読み込んだ給与テーブルに置き換えられます。\n\n"
						+ "読み込みますか？");

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
			option.textContent = year + "年";

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

		const clearButton =
			document.getElementById(
				"wageContentClearButton");

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
		 * pendingEmployeeIdはURLに残さない。
		 *
		 * そのため、現在の画面ではpendingリストが維持されるが、
		 * F5／再アクセス時にはDBに保存された社員のみを再取得する。
		 */
		window.history.replaceState(
			null,
			"",
			url.pathname + url.search);

	})();
	</script>

</body>
</html>