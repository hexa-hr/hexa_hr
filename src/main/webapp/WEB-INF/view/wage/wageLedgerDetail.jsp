<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与台帳詳細</title>

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
	margin-bottom: 20px;
}

.page-header h1 {
	font-size: 22px;
	font-weight: bold;
	margin: 0;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

/* 3. 급여 요약 정보 바 */
.summary-info {
	background-color: #f1f5f9; /* 은은한 파란빛 배경 */
	padding: 15px 20px;
	border: 1px solid #ccc;
	border-radius: 3px;
	margin-bottom: 20px;
	display: flex;
	gap: 25px;
	align-items: center;
	font-size: 14px;
	font-weight: bold;
	color: #333;
}

.summary-info span.highlight {
	color: #4e73df; /* 메인 파란색 포인트 */
}

/* 4. 상단 검색 폼 영역 */
.filter-bar {
	background: #f4f4f4;
	padding: 15px 20px;
	border: 1px solid #ddd;
	border-radius: 3px;
	display: flex;
	align-items: center;
	gap: 15px;
	margin-bottom: 25px;
}

.filter-bar select {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

/* 5. 버튼 스타일 통일 */
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

/* 6. 데이터 테이블 스타일 */
.table-container {
	width: 100%;
	overflow-x: auto; /* 내용이 길면 가로 스크롤 생성 */
}

table.data-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	background: white;
	margin-bottom: 30px;
}

table.data-table th, table.data-table td {
	border: 1px solid #ccc;
	padding: 10px;
	font-size: 14px;
	white-space: nowrap;
}

table.data-table th {
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
}

table.data-table tbody tr:hover td {
	background-color: #f1f5f9;
}

/* 푸터(총계) 행 강조 */
table.data-table tfoot th {
	background-color: #f4f4f4;
	border-top: 2px solid #4e73df;
	color: #333;
}

/* 유틸리티 (정렬 및 컬러) */
.text-right { text-align: right; }
.text-blue { color: #4e73df !important; font-weight: bold; }
.text-red { color: #e74a3b !important; font-weight: bold; }

.bottom-actions {
	margin-top: 20px;
	text-align: center;
}

.error-message {
	color: #e74a3b;
	font-weight: bold;
	margin-bottom: 20px;
	font-size: 14px;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			
			<div class="page-header">
				<h1>給与台帳詳細</h1>
			</div>

			<c:if test="${not empty errorMessage}">
				<div class="error-message">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<c:if test="${empty errorMessage and not empty ledgerDetail}">

				<c:set var="summary" value="${ledgerDetail.summary}" />

				<!-- 요약 정보 바 -->
				<div class="summary-info">
					<div>帰属年月: <span class="highlight"><c:out value="${summary.wageMonth}" /></span></div>
					<div>給与回次: <span class="highlight">給与-<fmt:formatNumber value="${summary.wagePeriod}" pattern="00" />回</span></div>
					<div>精算期間: <span class="highlight"><fmt:formatDate value="${summary.settlementPeriodStartDate}" pattern="yyyy-MM-dd" /> ~ <fmt:formatDate value="${summary.settlementPeriodEndDate}" pattern="yyyy-MM-dd" /></span></div>
					<div>支給日: <span class="highlight"><fmt:formatDate value="${summary.wagePaymentDate}" pattern="yyyy-MM-dd" /></span></div>
				</div>

				<!-- 검색 필터 폼 -->
				<form method="get" action="${pageContext.request.contextPath}/wage/ledgerDetail.do">
					<div class="filter-bar">
						<input type="hidden" name="wageMonth" value="<c:out value='${summary.wageMonth}' />"> 
						<input type="hidden" name="wagePeriod" value="<c:out value='${summary.wagePeriod}' />">

						<!-- 雇用形態 -->
						<select name="employmentType">
							<option value="">すべて (雇用形態)</option>
							<option value="정규직" <c:if test="${employmentType eq '정규직'}">selected</c:if>>正社員</option>
							<option value="계약직" <c:if test="${employmentType eq '계약직'}">selected</c:if>>契約社員</option>
							<option value="임시직" <c:if test="${employmentType eq '임시직'}">selected</c:if>>臨時社員</option>
							<option value="파견직" <c:if test="${employmentType eq '파견직'}">selected</c:if>>派遣社員</option>
							<option value="위촉직" <c:if test="${employmentType eq '위촉직'}">selected</c:if>>業務委託</option>
							<option value="일용직" <c:if test="${employmentType eq '일용직'}">selected</c:if>>日雇い</option>
						</select>

						<!-- 部署 -->
						<select name="departmentId">
							<option value="">全部署</option>
							<c:forEach var="department" items="${ledgerDetail.departments}">
								<option value="${department.departmentId}" <c:if test="${departmentId eq department.departmentId.toString()}">selected</c:if>>
									<c:out value="${department.departmentName}" />
								</option>
							</c:forEach>
						</select>

						<!-- 所得者区分 -->
						<select name="incomeType">
							<option value="">すべて (所得区分)</option>
							<option value="worker" <c:if test="${incomeType eq 'worker'}">selected</c:if>>給与所得者</option>
							<option value="business" <c:if test="${incomeType eq 'business'}">selected</c:if>>事業所得者</option>
							<option value="daily" <c:if test="${incomeType eq 'daily'}">selected</c:if>>日雇労働者</option>
						</select>

						<button type="submit" class="btn-search">照会</button>
					</div>
				</form>

				<!-- 데이터 테이블 -->
				<div class="table-container">
					<table class="data-table">
						<thead>
							<tr>
								<th>区分</th>
								<th>氏名</th>
								<th>入社日</th>
								<th>部署</th>
								<th>役職</th>

								<c:forEach var="type" items="${ledgerDetail.paymentTypes}">
									<th><c:out value="${type.wageTypeName}" /></th>
								</c:forEach>

								<th class="text-blue">支給総額</th>

								<c:forEach var="type" items="${ledgerDetail.deductionTypes}">
									<th><c:out value="${type.wageTypeName}" /></th>
								</c:forEach>

								<th class="text-red">控除総額</th>
								<th class="text-blue">差引支給額</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach var="row" items="${ledgerDetail.employeeRows}">
								<tr>
									<td><c:out value="${row.employmentType}" /></td>
									<td style="font-weight: bold;"><c:out value="${row.koreanName}" /></td>
									<td><fmt:formatDate value="${row.hireDate}" pattern="yyyy-MM-dd" /></td>
									<td><c:out value="${row.departmentName}" /></td>
									<td><c:out value="${row.positionName}" /></td>

									<c:forEach var="type" items="${ledgerDetail.paymentTypes}">
										<td class="text-right"><fmt:formatNumber value="${row.wageValues[type.wageTypeId]}" pattern="#,##0" /></td>
									</c:forEach>

									<td class="text-right text-blue"><fmt:formatNumber value="${row.totalPayment}" pattern="#,##0" /></td>

									<c:forEach var="type" items="${ledgerDetail.deductionTypes}">
										<td class="text-right"><fmt:formatNumber value="${row.wageValues[type.wageTypeId]}" pattern="#,##0" /></td>
									</c:forEach>

									<td class="text-right text-red"><fmt:formatNumber value="${row.totalDeduction}" pattern="#,##0" /></td>
									<td class="text-right text-blue"><fmt:formatNumber value="${row.netPayment}" pattern="#,##0" /></td>
								</tr>
							</c:forEach>
						</tbody>

						<tfoot>
							<tr>
								<th colspan="5">合計</th>

								<c:forEach var="type" items="${ledgerDetail.paymentTypes}">
									<th class="text-right"><fmt:formatNumber value="${ledgerDetail.itemTotals[type.wageTypeId]}" pattern="#,##0" /></th>
								</c:forEach>

								<th class="text-right text-blue"><fmt:formatNumber value="${ledgerDetail.totalPayment}" pattern="#,##0" /></th>

								<c:forEach var="type" items="${ledgerDetail.deductionTypes}">
									<th class="text-right"><fmt:formatNumber value="${ledgerDetail.itemTotals[type.wageTypeId]}" pattern="#,##0" /></th>
								</c:forEach>

								<th class="text-right text-red"><fmt:formatNumber value="${ledgerDetail.totalDeduction}" pattern="#,##0" /></th>
								<th class="text-right text-blue"><fmt:formatNumber value="${ledgerDetail.netPayment}" pattern="#,##0" /></th>
							</tr>
						</tfoot>
					</table>
				</div>

				<!-- 하단 버튼 영역 -->
				<div class="bottom-actions">
					<a class="btn-list" href="${pageContext.request.contextPath}/wage/ledger.do?year=${fn:substring(summary.wageMonth, 0, 4)}">
						給与台帳一覧へ戻る
					</a>
				</div>

			</c:if>
		</div>
	</div>

</body>
</html>