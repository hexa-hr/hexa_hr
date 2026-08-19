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
	background-color: #fff4f1;
	color: #e44343;
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
	margin-top: 15px;
	text-align: center;
}

@media ( max-width : 700px) {
	.wage-item-grid {
		grid-template-columns: 1fr;
	}
	.wage-item-column:first-child {
		border-right: 0;
	}
}
</style>
</head>

<body>

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

	<div class="payroll-workspace">

		<section class="payroll-pane employee-pane">

			<div class="employee-toolbar">

				<button type="button" id="previousWageOpenButton"
					<c:if test="${empty previousWageSourceOptions}">
						disabled
					</c:if>>
					지난급여 불러오기</button>

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

					<button type="submit" id="employeeDeleteButton">선택삭제</button>

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

					<button type="submit" id="employeeDeleteAllButton">전체삭제</button>

				</form>

			</div>

			<form class="employee-add-form" method="get"
				action="${pageContext.request.contextPath}/wage/paymentInput.do">

				<input type="hidden" name="wageMonth"
					value="<c:out value='${wageMonth}' />"> <input
					type="hidden" name="wagePeriod"
					value="<c:out value='${wagePeriod}' />"> <input
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

			<h2>사원 목록</h2>

			<div style="margin-bottom: 10px;">
				총
				<c:out value="${visibleEmployeeCount}" />
				명
			</div>

			<div class="table-scroll">
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
					일반소득 </a> <a href="${businessIncomeUrl}"
					class="income-tab ${incomeType eq 'business' ? 'active' : ''}">
					사업소득/기타소득 </a>

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

							<div class="wage-item-column-header payment-header">지급항목</div>

							<c:forEach var="item" items="${wageItems}">

								<c:if test="${item.itemType eq 'P'}">

									<div class="wage-item-row">

										<div class="wage-item-name">

											<c:out value="${item.wageTypeName}" />

											<c:if test="${item.taxableYn eq 'N'}">
												<span class="tax-free-mark">[비]</span>
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

							<div class="wage-item-column-header deduction-header">공제항목
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
							<span>지급총액</span> <strong> <fmt:formatNumber
									value="${currentWageTotalPayment}" pattern="#,##0" />원
							</strong>
						</div>

						<div class="wage-subtotal">
							<span>공제총액</span> <strong> <fmt:formatNumber
									value="${currentWageTotalDeduction}" pattern="#,##0" />원
							</strong>
						</div>

					</div>

					<div class="wage-net-total">
						실지급액 :
						<fmt:formatNumber value="${currentWageNetPayment}" pattern="#,##0" />
						원
					</div>

					<div class="wage-form-actions">

						<button type="submit"
							<c:if test="${not wageInputEnabled}">disabled</c:if>>
							자동계산</button>

						<button type="submit"
							formaction="${pageContext.request.contextPath}/wage/paymentInputSave.do"
							<c:if test="${not wageInputEnabled}">disabled</c:if>>저장
						</button>

					</div>

				</form>

			</c:if>

		</section>

	</div>

	<section class="payroll-summary">

		<h2>급여 종합정보</h2>

		<div class="payroll-summary-grid">

			<div class="payroll-summary-card summary-count">
				<span>월 합계</span> <strong> <c:out
						value="${monthlyEmployeeCount}" />건
				</strong>
			</div>

			<div class="payroll-summary-card summary-payment">
				<span>지급 총액</span> <strong> <fmt:formatNumber
						value="${monthlyTotalPayment}" pattern="#,##0" />원
				</strong>
			</div>

			<div class="payroll-summary-card summary-deduction">
				<span>공제 총액</span> <strong> <fmt:formatNumber
						value="${monthlyTotalDeduction}" pattern="#,##0" />원
				</strong>
			</div>

			<div class="payroll-summary-card summary-net">
				<span>실지급액</span> <strong> <fmt:formatNumber
						value="${monthlyNetPayment}" pattern="#,##0" />원
				</strong>
			</div>

		</div>

	</section>

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
						 * 사원 이름 링크를 클릭한 경우에는
						 * 기존 링크 이동을 그대로 사용한다.
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
						"선택된 사원이 없습니다.");

					return;
				}

				const confirmed =
					window.confirm(
						"선택된 사원을 삭제 하시겠습니까?");

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
						"추가된 사원이 없습니다.");

					return;
				}

				const confirmed =
					window.confirm(
						"■ 주의!!\n"
						+ "- [전체] 급여입력 정보를 삭제 하시겠습니까?");

				if (!confirmed) {
					return;
				}

				const finalConfirmed =
					window.confirm(
						"주의!!\n"
						+ "삭제된 급여입력 정보는 복구할 수 없습니다.\n"
						+ "삭제 하시겠습니까?");

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