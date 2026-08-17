<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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
</style>
</head>

<body>

	<h1>급여입력</h1>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/paymentInput.do">

		<div class="form-row">

			<label for="wageMonth">귀속연월</label> <input type="month"
				id="wageMonth" name="wageMonth"
				value="<c:out value='${wageMonth}' />" required> <label
				for="wagePeriod"> 급여차수 </label> <select id="wagePeriod"
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


		<button type="submit" name="search" value="true">조회</button>


		<c:if test="${not empty errorMessage}">

			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>

		</c:if>

	</form>

	<c:if test="${not empty savedEmployees}">

		<h2>저장된 사원 목록</h2>

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

								<c:param name="employeeId" value="${employee.employeeId}" />

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

			</tbody>

		</table>

	</c:if>

	<c:if test="${not empty wageItems}">

		<div class="status-message">

			선택 사원:
			<c:out value="${selectedEmployeeName}" />

			/

			<c:choose>

				<c:when test="${existingPeriod}">
					기존 저장 급여
				</c:when>

				<c:otherwise>
					신규 급여
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
				type="hidden" name="settlementStartDate"
				value="<c:out value='${settlementStartDate}' />"> <input
				type="hidden" name="settlementEndDate"
				value="<c:out value='${settlementEndDate}' />"> <input
				type="hidden" name="wagePaymentDate"
				value="<c:out value='${wagePaymentDate}' />">


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

</body>
</html>