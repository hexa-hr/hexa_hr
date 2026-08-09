<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>연도별 전체급여 통계</title>

<style>
body {
	font-family: Arial, sans-serif;
	margin: 30px;
}

.description {
	margin-bottom: 20px;
}

.search-form {
	border: 1px solid #ccc;
	padding: 20px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 10px;
}

label {
	font-weight: bold;
}

input, button {
	padding: 7px;
}

button {
	cursor: pointer;
}

.error-message {
	margin-top: 15px;
	color: red;
	font-weight: bold;
}

.result-container {
	margin-top: 30px;
	overflow-x: auto;
}

.result-table {
	border-collapse: collapse;
	min-width: 1200px;
	width: 100%;
	white-space: nowrap;
}

.result-table th, .result-table td {
	border: 1px solid #aaa;
	padding: 9px 12px;
	text-align: right;
}

.result-table th {
	background-color: #f2f2f2;
	text-align: center;
}

.result-table .row-title {
	text-align: left;
	font-weight: bold;
	background-color: #f8f8f8;
}

.result-table .growth-title {
	text-align: left;
	padding-left: 25px;
	background-color: #fafafa;
}

.positive {
	color: red;
}

.negative {
	color: blue;
}
</style>
</head>

<body>

	<h1>연도별 전체급여 통계</h1>

	<p class="description">귀속년도를 선택하면 선택연도를 포함한 최근 10개년의 전체 급여액과 인원현황을
		확인할 수 있습니다.</p>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/yearlyTotalStatistics.do">

		<div class="search-row">

			<label for="year">귀속년도</label> <input type="number" id="year"
				name="year" min="1000" max="9999"
				value="<c:out value='${selectedYear}' />" required>

			<button type="submit">조회</button>

		</div>

		<c:if test="${not empty errorMessage}">
			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

	</form>


	<c:if test="${not empty yearlyTotalStatistics}">

		<div class="result-container">

			<table class="result-table">

				<thead>
					<tr>
						<th>구분</th>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<th><c:out value="${row.year}" />년</th>

						</c:forEach>
					</tr>
				</thead>


				<tbody>

					<tr>
						<td class="row-title">전체 급여액 (원)</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><fmt:formatNumber value="${row.totalPayment}"
									pattern="#,##0" /></td>

						</c:forEach>
					</tr>


					<tr>
						<td class="growth-title">└ 증감률</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><c:choose>

									<c:when test="${row.paymentGrowthRate == null}">
										-
									</c:when>

									<c:when test="${row.paymentGrowthRate > 0}">
										<span class="positive"> <fmt:formatNumber
												value="${row.paymentGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:when test="${row.paymentGrowthRate < 0}">
										<span class="negative"> <fmt:formatNumber
												value="${row.paymentGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:otherwise>
										<fmt:formatNumber value="${row.paymentGrowthRate}"
											pattern="0.0" />%
									</c:otherwise>

								</c:choose></td>

						</c:forEach>
					</tr>


					<tr>
						<td class="row-title">인원 (명)</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><fmt:formatNumber value="${row.averageEmployeeCount}"
									pattern="0.0" /></td>

						</c:forEach>
					</tr>


					<tr>
						<td class="growth-title">└ 증감률</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><c:choose>

									<c:when test="${row.employeeGrowthRate == null}">
										-
									</c:when>

									<c:when test="${row.employeeGrowthRate > 0}">
										<span class="positive"> <fmt:formatNumber
												value="${row.employeeGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:when test="${row.employeeGrowthRate < 0}">
										<span class="negative"> <fmt:formatNumber
												value="${row.employeeGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:otherwise>
										<fmt:formatNumber value="${row.employeeGrowthRate}"
											pattern="0.0" />%
									</c:otherwise>

								</c:choose></td>

						</c:forEach>
					</tr>

				</tbody>

			</table>

		</div>

	</c:if>

</body>
</html>