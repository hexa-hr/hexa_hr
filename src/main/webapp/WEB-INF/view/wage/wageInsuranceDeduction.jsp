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
/* 1. 전체 레이아웃 (공통) */
body {
	margin: 0;
	min-width: 1400px;
	background-color: #f8f9fa;
	font-family: 'Malgun Gothic', sans-serif;
	color: #333;
}

.wrap {
	display: flex;
	align-items: flex-start;
	width: 100%;
}

.container {
	padding: 30px 40px;
	background-color: white;
	box-sizing: border-box;
	flex: 1;
	min-height: 600px;
    min-width: 0;
}

/* 2. 타이틀 영역 */
.page-header {
	margin-bottom: 10px;
}

.page-header h1 {
	font-size: 22px;
	font-weight: bold;
	margin: 0;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.page-desc {
	font-size: 14px;
	color: #666;
	margin: 0 0 20px 0;
}

/* 3. 상단 검색 폼 영역 */
.search-form {
	background: #f4f4f4;
	padding: 15px 20px;
	border: 1px solid #ddd;
	border-radius: 3px;
	margin-bottom: 20px;
	box-sizing: border-box;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 15px;
}

.search-row label {
	font-size: 14px;
	font-weight: bold;
	color: #333;
}

input[type="month"], select {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

input[type="month"] { width: 140px; }
select { width: 150px; }

.btn-search {
	padding: 6px 16px;
	border: none;
	border-radius: 3px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	background-color: #4e73df;
	color: white;
	outline: none;
}
.btn-search:hover { background-color: #2e59d9; }

/* 4. 급여 요약 정보 바 */
.summary-info {
	background-color: #f1f5f9;
	padding: 15px 20px;
	border: 1px solid #ccc;
	border-radius: 3px;
	margin-bottom: 20px;
	display: flex;
	gap: 30px;
	align-items: center;
	font-size: 14px;
	font-weight: bold;
	color: #333;
}
.summary-info span.highlight {
	color: #4e73df;
}

/* 5. 데이터 테이블 스타일 */
.result-container {
	margin-top: 10px;
	overflow-x: auto;
}

table.result-table {
	border-collapse: collapse;
	width: 100%;
	min-width: 1500px;
	text-align: center;
	margin-bottom: 30px;
}

table.result-table th, table.result-table td {
	border: 1px solid #ccc;
	padding: 10px;
	font-size: 14px;
	white-space: nowrap;
}

table.result-table th {
	color: #333;
	font-weight: bold;
}

table.result-table td {
	text-align: right;
}

table.result-table td.employee-info {
	text-align: center;
}

table.result-table tbody tr:hover td {
	background-color: #f1f5f9;
}

/* 테이블 그룹 헤더 배경색 통일 */
table.result-table th { background-color: #f8f9fa; }
table.result-table .group-pension { background-color: #eef6ff; color: #4e73df; } /* 파랑 (국민연금) */
table.result-table .group-health { background-color: #e6f9ec; color: #2e7d32; } /* 초록 (건강보험) */
table.result-table .group-care { background-color: #fff8e1; color: #f57f17; } /* 노랑 (장기요양) */
table.result-table .group-employment { background-color: #fff4f1; color: #e74a3b; } /* 빨강 (고용보험) */
table.result-table .group-total { background-color: #f4f4f4; color: #333; } /* 회색 (총계) */

/* 푸터(총계) 행 강조 */
table.result-table tfoot th {
	background-color: #f4f4f4;
	border-top: 2px solid #4e73df;
	text-align: right;
}
table.result-table tfoot th:first-child {
	text-align: center;
}

/* 6. 유틸 및 버튼 */
.error-message {
	margin-top: 15px;
	color: #e74a3b;
	font-weight: bold;
	font-size: 14px;
}

.empty-message {
	padding: 30px;
	text-align: center;
	color: #777;
	font-size: 14px;
	border: 1px solid #ccc;
	background-color: #f8f9fa;
	border-radius: 3px;
	margin-top: 10px;
}

.bottom-actions {
	text-align: center;
	margin-top: 20px;
}

.btn-list {
	background-color: #a5a5a5;
	color: white;
	padding: 8px 25px;
	text-decoration: none;
	border-radius: 3px;
	font-weight: bold;
	display: inline-block;
}
.btn-list:hover { background-color: #858796; }

.bold-black { font-weight: bold; color: #333; }
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			
			<div class="page-header">
				<h1>4大保険控除内訳</h1>
			</div>
			<p class="page-desc">帰属年月および給与回次を基準に、労働者と事業主の4大保険負担内訳を照会します。</p>

			<form class="search-form" method="get" action="${pageContext.request.contextPath}/wage/insuranceDeduction.do">
				<div class="search-row">
					<label for="wageMonth">帰属年月</label> 
					<input type="month" id="wageMonth" name="wageMonth" value="<c:out value='${selectedWageMonth}' />" required> 
					
					<label for="wagePeriod" style="margin-left: 10px;">給与回次</label> 
					<select id="wagePeriod" name="wagePeriod" required>
						<c:forEach var="period" begin="1" end="10">
							<option value="${period}" <c:if test="${selectedWagePeriod == period}">selected</c:if>>
								給与-<fmt:formatNumber value="${period}" pattern="00" />回
							</option>
						</c:forEach>
					</select>

					<button type="submit" class="btn-search" style="margin-left: 10px;">控除内訳照会</button>
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
						<div class="empty-message">4大保険控除内訳のデータがありません。</div>
					</c:when>

					<c:otherwise>
						<!-- 요약 정보 바 -->
						<div class="summary-info">
							<div>
								精算期間: 
								<span class="highlight">
									<fmt:formatDate value="${insuranceDeduction.summary.settlementPeriodStartDate}" pattern="yyyy-MM-dd" /> ~ 
									<fmt:formatDate value="${insuranceDeduction.summary.settlementPeriodEndDate}" pattern="yyyy-MM-dd" />
								</span>
							</div>
							<div>
								給与支給日: 
								<span class="highlight">
									<fmt:formatDate value="${insuranceDeduction.summary.wagePaymentDate}" pattern="yyyy-MM-dd" />
								</span>
							</div>
						</div>

						<!-- 데이터 테이블 -->
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

										<th class="group-pension">事業主</th>
										<th class="group-pension">労働者</th>
										<th class="group-pension">合計</th>

										<th class="group-health">事業主</th>
										<th class="group-health">労働者</th>
										<th class="group-health">合計</th>

										<th class="group-care">事業主</th>
										<th class="group-care">労働者</th>
										<th class="group-care">合計</th>

										<th class="group-employment">事業主</th>
										<th class="group-employment">労働者</th>
										<th class="group-employment">合計</th>

										<th class="group-total">事業主</th>
										<th class="group-total">労働者</th>
										<th class="group-total">合計</th>
									</tr>
								</thead>

								<tbody>
									<c:forEach var="row" items="${insuranceDeduction.rows}">
										<tr>
											<td class="employee-info"><c:out value="${row.employmentType}" /></td>
											<td class="employee-info bold-black"><c:out value="${row.koreanName}" /></td>
											<td class="employee-info"><fmt:formatDate value="${row.hireDate}" pattern="yyyy-MM-dd" /></td>
											<td class="employee-info"><c:out value="${row.departmentName}" /></td>
											<td class="employee-info"><c:out value="${row.positionName}" /></td>

											<td><fmt:formatNumber value="${row.nationalPensionEmployer}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.nationalPensionEmployee}" pattern="#,##0" /></td>
											<td style="color: #4e73df; font-weight: bold;"><fmt:formatNumber value="${row.nationalPensionTotal}" pattern="#,##0" /></td>

											<td><fmt:formatNumber value="${row.healthInsuranceEmployer}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.healthInsuranceEmployee}" pattern="#,##0" /></td>
											<td style="color: #2e7d32; font-weight: bold;"><fmt:formatNumber value="${row.healthInsuranceTotal}" pattern="#,##0" /></td>

											<td><fmt:formatNumber value="${row.longTermCareInsuranceEmployer}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.longTermCareInsuranceEmployee}" pattern="#,##0" /></td>
											<td style="color: #f57f17; font-weight: bold;"><fmt:formatNumber value="${row.longTermCareInsuranceTotal}" pattern="#,##0" /></td>

											<td><fmt:formatNumber value="${row.employmentInsuranceEmployer}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.employmentInsuranceEmployee}" pattern="#,##0" /></td>
											<td style="color: #e74a3b; font-weight: bold;"><fmt:formatNumber value="${row.employmentInsuranceTotal}" pattern="#,##0" /></td>

											<td class="bold-black"><fmt:formatNumber value="${row.employerTotal}" pattern="#,##0" /></td>
											<td class="bold-black"><fmt:formatNumber value="${row.employeeTotal}" pattern="#,##0" /></td>
											<td class="bold-black" style="background-color: #f8f9fa;"><fmt:formatNumber value="${row.grandTotal}" pattern="#,##0" /></td>
										</tr>
									</c:forEach>
								</tbody>

								<tfoot>
									<tr>
										<th colspan="5">合計</th>

										<th><fmt:formatNumber value="${insuranceDeduction.totalNationalPensionEmployer}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${insuranceDeduction.totalNationalPensionEmployee}" pattern="#,##0" /></th>
										<th style="color: #4e73df;"><fmt:formatNumber value="${insuranceDeduction.totalNationalPension}" pattern="#,##0" /></th>

										<th><fmt:formatNumber value="${insuranceDeduction.totalHealthInsuranceEmployer}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${insuranceDeduction.totalHealthInsuranceEmployee}" pattern="#,##0" /></th>
										<th style="color: #2e7d32;"><fmt:formatNumber value="${insuranceDeduction.totalHealthInsurance}" pattern="#,##0" /></th>

										<th><fmt:formatNumber value="${insuranceDeduction.totalLongTermCareInsuranceEmployer}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${insuranceDeduction.totalLongTermCareInsuranceEmployee}" pattern="#,##0" /></th>
										<th style="color: #f57f17;"><fmt:formatNumber value="${insuranceDeduction.totalLongTermCareInsurance}" pattern="#,##0" /></th>

										<th><fmt:formatNumber value="${insuranceDeduction.totalEmploymentInsuranceEmployer}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${insuranceDeduction.totalEmploymentInsuranceEmployee}" pattern="#,##0" /></th>
										<th style="color: #e74a3b;"><fmt:formatNumber value="${insuranceDeduction.totalEmploymentInsurance}" pattern="#,##0" /></th>

										<th class="bold-black"><fmt:formatNumber value="${insuranceDeduction.totalEmployer}" pattern="#,##0" /></th>
										<th class="bold-black"><fmt:formatNumber value="${insuranceDeduction.totalEmployee}" pattern="#,##0" /></th>
										<th class="bold-black"><fmt:formatNumber value="${insuranceDeduction.grandTotal}" pattern="#,##0" /></th>
									</tr>
								</tfoot>
							</table>
						</div>
					</c:otherwise>
				</c:choose>
			</c:if>

			<div class="bottom-actions">
				<a class="btn-list" href="${pageContext.request.contextPath}/wage/ledger.do">給与台帳 一覧へ戻る</a>
			</div>
			
		</div>
	</div>

</body>
</html>