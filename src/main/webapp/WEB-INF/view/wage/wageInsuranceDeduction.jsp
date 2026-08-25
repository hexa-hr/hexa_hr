<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>4大保険控除内訳</title>

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
	width: 900px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 10px;
}

label {
	font-weight: bold;
}

input, select, button {
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

.summary-info {
	margin-top: 20px;
	display: flex;
	gap: 30px;
}

.result-container {
	margin-top: 25px;
	overflow-x: auto;
}

.result-table {
	border-collapse: collapse;
	min-width: 1900px;
	white-space: nowrap;
}

.result-table th, .result-table td {
	border: 1px solid #aaa;
	padding: 8px 10px;
}

.result-table th {
	background-color: #f2f2f2;
	text-align: center;
}

.result-table td {
	text-align: right;
}

.result-table td.employee-info {
	text-align: center;
}

.result-table .group-pension {
	background-color: #eef8fb;
}

.result-table .group-health {
	background-color: #f3f8ee;
}

.result-table .group-care {
	background-color: #fff8e8;
}

.result-table .group-employment {
	background-color: #fff3ef;
}

.result-table .group-total {
	background-color: #f5efff;
}

.result-table tfoot th {
	background-color: #fffde0;
	font-weight: bold;
}

.no-data {
	margin-top: 25px;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h1>4大保険控除内訳</h1>

	<p>帰属年月および給与回次を基準に、労働者と事業主の4大保険負担内訳を照会します。</p>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/insuranceDeduction.do">

		<div class="search-row">

			<label for="wageMonth">帰属年月</label> <input type="month"
				id="wageMonth" name="wageMonth"
				value="<c:out value='${selectedWageMonth}' />" required> <label
				for="wagePeriod">給与回次</label> <select id="wagePeriod"
				name="wagePeriod" required>

				<c:forEach var="period" begin="1" end="10">

					<option value="${period}"
						<c:if test="${selectedWagePeriod == period}">
							selected
						</c:if>>
						給与-
						<fmt:formatNumber value="${period}" pattern="00" />回
					</option>

				</c:forEach>

			</select>

			<button type="submit">控除内訳照会</button>

		</div>

		<c:if test="${not empty errorMessage}">
			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

	</form>


	<c:if test="${not empty insuranceDeduction}">

		<c:choose>

			<c:when test="${empty insuranceDeduction.rows}">

				<div class="no-data">4大保険控除内訳のデータがありません。</div>

			</c:when>

			<c:otherwise>

				<div class="summary-info">

					<div>
						<strong>精算期間:</strong>

						<fmt:formatDate
							value="${insuranceDeduction.summary.settlementPeriodStartDate}"
							pattern="yyyy-MM-dd" />

						~

						<fmt:formatDate
							value="${insuranceDeduction.summary.settlementPeriodEndDate}"
							pattern="yyyy-MM-dd" />
					</div>

					<div>
						<strong>給与支給日:</strong>

						<fmt:formatDate
							value="${insuranceDeduction.summary.wagePaymentDate}"
							pattern="yyyy-MM-dd" />
					</div>

				</div>


				<div class="result-container">

					<table class="result-table">

						<thead>

							<tr>

								<th colspan="5">社員情報</th>

								<th colspan="3" class="group-pension">国民年金</th>

								<th colspan="3" class="group-health">健康保険</th>

								<th colspan="3" class="group-care">介護保険</th>

								<th colspan="3" class="group-employment">雇用保険</th>

								<th colspan="3" class="group-total">総合計</th>

							</tr>

							<tr>

								<th>区分</th>
								<th>氏名</th>
								<th>入社日</th>
								<th>部署</th>
								<th>役職</th>

								<th>事業主</th>
								<th>労働者</th>
								<th>合計</th>

								<th>事業主</th>
								<th>労働者</th>
								<th>合計</th>

								<th>事業主</th>
								<th>労働者</th>
								<th>合計</th>

								<th>事業主</th>
								<th>労働者</th>
								<th>合計</th>

								<th>事業主</th>
								<th>労働者</th>
								<th>合計</th>

							</tr>

						</thead>


						<tbody>

							<c:forEach var="row" items="${insuranceDeduction.rows}">

								<tr>

									<td class="employee-info"><c:out
											value="${row.employmentType}" /></td>

									<td class="employee-info"><c:out value="${row.koreanName}" />
									</td>

									<td class="employee-info"><fmt:formatDate
											value="${row.hireDate}" pattern="yyyy-MM-dd" /></td>

									<td class="employee-info"><c:out
											value="${row.departmentName}" /></td>

									<td class="employee-info"><c:out
											value="${row.positionName}" /></td>


									<td><fmt:formatNumber
											value="${row.nationalPensionEmployer}" pattern="#,##0" /></td>

									<td><fmt:formatNumber
											value="${row.nationalPensionEmployee}" pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.nationalPensionTotal}"
											pattern="#,##0" /></td>


									<td><fmt:formatNumber
											value="${row.healthInsuranceEmployer}" pattern="#,##0" /></td>

									<td><fmt:formatNumber
											value="${row.healthInsuranceEmployee}" pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.healthInsuranceTotal}"
											pattern="#,##0" /></td>


									<td><fmt:formatNumber
											value="${row.longTermCareInsuranceEmployer}" pattern="#,##0" />
									</td>

									<td><fmt:formatNumber
											value="${row.longTermCareInsuranceEmployee}" pattern="#,##0" />
									</td>

									<td><fmt:formatNumber
											value="${row.longTermCareInsuranceTotal}" pattern="#,##0" />
									</td>


									<td><fmt:formatNumber
											value="${row.employmentInsuranceEmployer}" pattern="#,##0" />
									</td>

									<td><fmt:formatNumber
											value="${row.employmentInsuranceEmployee}" pattern="#,##0" />
									</td>

									<td><fmt:formatNumber
											value="${row.employmentInsuranceTotal}" pattern="#,##0" /></td>


									<td><fmt:formatNumber value="${row.employerTotal}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.employeeTotal}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.grandTotal}"
											pattern="#,##0" /></td>

								</tr>

							</c:forEach>

						</tbody>


						<tfoot>

							<tr>

								<th colspan="5">合計</th>


								<th><fmt:formatNumber
										value="${insuranceDeduction.totalNationalPensionEmployer}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalNationalPensionEmployee}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalNationalPension}"
										pattern="#,##0" /></th>


								<th><fmt:formatNumber
										value="${insuranceDeduction.totalHealthInsuranceEmployer}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalHealthInsuranceEmployee}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalHealthInsurance}"
										pattern="#,##0" /></th>


								<th><fmt:formatNumber
										value="${insuranceDeduction.totalLongTermCareInsuranceEmployer}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalLongTermCareInsuranceEmployee}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalLongTermCareInsurance}"
										pattern="#,##0" /></th>


								<th><fmt:formatNumber
										value="${insuranceDeduction.totalEmploymentInsuranceEmployer}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalEmploymentInsuranceEmployee}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalEmploymentInsurance}"
										pattern="#,##0" /></th>


								<th><fmt:formatNumber
										value="${insuranceDeduction.totalEmployer}" pattern="#,##0" />
								</th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.totalEmployee}" pattern="#,##0" />
								</th>

								<th><fmt:formatNumber
										value="${insuranceDeduction.grandTotal}" pattern="#,##0" /></th>

							</tr>

						</tfoot>

					</table>

				</div>

			</c:otherwise>

		</c:choose>

	</c:if>

	<p>
		<a href="${pageContext.request.contextPath}/wage/ledger.do"> 給与台帳
			一覧 </a>
	</p>

</body>
</html>