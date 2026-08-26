<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与台帳</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
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

/* 3. 상단 검색 폼 영역 */
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

.filter-bar label {
	font-size: 14px;
	font-weight: bold;
	color: #333;
}

.filter-bar input[type="number"] {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
	width: 100px;
}

/* 4. 버튼 스타일 통일 */
.btn {
	padding: 6px 16px;
	border: none;
	border-radius: 3px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	color: white;
	outline: none;
}

.btn-blue {
	background-color: #4e73df;
}
.btn-blue:hover { background-color: #2e59d9; }

.btn-gray {
	background-color: #a5a5a5;
}
.btn-gray:hover { background-color: #858796; }

.btn-gray:disabled {
	background-color: #ccc;
	cursor: not-allowed;
}

/* 5. 데이터 테이블 스타일 */
table.data-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	background: white;
	margin-bottom: 30px;
}

table.data-table th, table.data-table td {
	border: 1px solid #ccc;
	padding: 12px 10px;
	font-size: 14px;
	white-space: nowrap;
}

table.data-table th {
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
}

table.data-table tbody tr {
	cursor: pointer;
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

/* 6. 알림 메시지 영역 */
.error-message {
	color: #e74a3b;
	font-weight: bold;
	margin-bottom: 20px;
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
}

/* 삭제 폼 마진 제거 */
.ledger-delete-form {
	margin: 0;
}
</style>

</head>
<body>
	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			<!-- 타이틀 영역 -->
			<div class="page-header">
				<h1>給与台帳</h1>
			</div>

			<!-- 검색 필터 폼 -->
			<form method="get" action="${pageContext.request.contextPath}/wage/ledger.do">
				<div class="filter-bar">
					<label for="year">帰属年度</label> 
					<input type="number" id="year" name="year" min="1000" max="9999" value="${selectedYear}">
					<button type="submit" class="btn btn-blue">照会</button>
				</div>
			</form>

			<c:if test="${not empty errorMessage}">
				<div class="error-message">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<c:if test="${not empty deleteMessage}">
				<span id="ledgerDeleteMessage" hidden>
					<c:out value="${deleteMessage}" />
				</span>
			</c:if>

			<c:if test="${empty errorMessage}">
				<c:choose>
					<c:when test="${empty ledgerSummary.summaries}">
						<div class="empty-message">照会された給与履歴がありません。</div>
					</c:when>
					<c:otherwise>
						<!-- 데이터 테이블 -->
						<table class="data-table">
							<thead>
								<tr>
									<th>帰属年月</th>
									<th>給与回次</th>
									<th>精算期間</th>
									<th>支給日</th>
									<th>人数</th>
									<th>支給総額</th>
									<th>控除総額</th>
									<th>差引支給額</th>
									<th>削除</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="summary" items="${ledgerSummary.summaries}">
									<tr onclick="location.href='${pageContext.request.contextPath}/wage/ledgerDetail.do?wageMonth=${summary.wageMonth}&wagePeriod=${summary.wagePeriod}'">
										<td>${summary.wageMonth}</td>
										<td>給与-<fmt:formatNumber value="${summary.wagePeriod}" pattern="00" />回</td>
										<td>
											<fmt:formatDate value="${summary.settlementPeriodStartDate}" pattern="yyyy-MM-dd" /> ~ 
											<fmt:formatDate value="${summary.settlementPeriodEndDate}" pattern="yyyy-MM-dd" />
										</td>
										<td>
											<fmt:formatDate value="${summary.wagePaymentDate}" pattern="yyyy-MM-dd" />
										</td>
										<td>${summary.employeeCount}</td>
										<td style="text-align: right;"><fmt:formatNumber value="${summary.totalPayment}" pattern="#,##0" /></td>
										<td style="text-align: right;"><fmt:formatNumber value="${summary.totalDeduction}" pattern="#,##0" /></td>
										<td style="text-align: right; color: #4e73df; font-weight: bold;"><fmt:formatNumber value="${summary.netPayment}" pattern="#,##0" /></td>
										<td onclick="event.stopPropagation();">
											<form class="ledger-delete-form" method="post" action="${pageContext.request.contextPath}/wage/ledgerDelete.do">
												<input type="hidden" name="wageMonth" value="${summary.wageMonth}"> 
												<input type="hidden" name="wagePeriod" value="${summary.wagePeriod}"> 
												<input type="hidden" name="deleteConfirmed" value="false"> 
												<input type="hidden" name="deleteFinalConfirmed" value="false">

												<button type="submit" class="btn btn-gray">削除</button>
											</form>
										</td>
									</tr>
								</c:forEach>
							</tbody>

							<tfoot>
								<tr>
									<th>合計</th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th style="text-align: right;"><fmt:formatNumber value="${ledgerSummary.totalPayment}" pattern="#,##0" /></th>
									<th style="text-align: right;"><fmt:formatNumber value="${ledgerSummary.totalDeduction}" pattern="#,##0" /></th>
									<th style="text-align: right; color: #4e73df;"><fmt:formatNumber value="${ledgerSummary.netPayment}" pattern="#,##0" /></th>
									<th></th>
								</tr>
							</tfoot>
						</table>
					</c:otherwise>
				</c:choose>
			</c:if>

		</div>
	</div>

	<!-- 자바스크립트 블록 유지 -->
	<script>
	document.addEventListener("DOMContentLoaded", function() {

		var deleteMessage = document.getElementById("ledgerDeleteMessage");

		if (deleteMessage) {
			window.alert(deleteMessage.textContent.trim());

			if (window.history.replaceState) {
				var currentUrl = new URL(window.location.href);
				currentUrl.searchParams.delete("deleteResult");
				window.history.replaceState(null, document.title, currentUrl.pathname + currentUrl.search + currentUrl.hash);
			}
		}

		var deleteForms = document.querySelectorAll(".ledger-delete-form");

		deleteForms.forEach(function(form) {
			form.addEventListener("click", function(event) {
				event.stopPropagation();
			});

			form.addEventListener("submit", function(event) {
				event.preventDefault();
				event.stopPropagation();

				form.elements["deleteConfirmed"].value = "false";
				form.elements["deleteFinalConfirmed"].value = "false";

				var firstConfirmed = window.confirm(
					"[必読] - [削除機能]\n\n"
					+ "選択した給与回次に該当する\n"
					+ "給与データがすべて削除されます。\n\n"
					+ "削除された給与台帳および給与データは\n"
					+ "復元できません。\n"
					+ "もう一度確認してから削除してください。");

				if (!firstConfirmed) { return; }

				var finalConfirmed = window.confirm("[警告] 本当に削除しますか？");

				if (!finalConfirmed) { return; }

				form.elements["deleteConfirmed"].value = "true";
				form.elements["deleteFinalConfirmed"].value = "true";

				var submitButton = form.querySelector("button[type='submit']");
				if (submitButton) {
					submitButton.disabled = true;
				}

				form.submit();
			});
		});
	});
	</script>
</body>
</html>