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
	font-family: Arial, sans-serif;
	margin: 0;
}
</style>

</head>
<body>
	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h2>給与台帳</h2>

	<form method="get"
		action="${pageContext.request.contextPath}/wage/ledger.do">

		<label for="year">帰属年度</label> <input type="number" id="year"
			name="year" min="1000" max="9999" value="${selectedYear}">

		<button type="submit">照会</button>
	</form>

	<hr>

	<c:if test="${not empty errorMessage}">
		<p>
			<c:out value="${errorMessage}" />
		</p>
	</c:if>

	<c:if test="${not empty deleteMessage}">
		<span id="ledgerDeleteMessage" hidden> <c:out
				value="${deleteMessage}" />
		</span>
	</c:if>

	<c:if test="${empty errorMessage}">

		<c:choose>

			<c:when test="${empty ledgerSummary.summaries}">
				<p>照会された給与履歴がありません。</p>
			</c:when>

			<c:otherwise>

				<table border="1">
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


							<tr
								onclick="location.href='${pageContext.request.contextPath}/wage/ledgerDetail.do?wageMonth=${summary.wageMonth}&wagePeriod=${summary.wagePeriod}'"
								style="cursor: pointer;">
								<td>${summary.wageMonth}</td>

								<td>給与-<fmt:formatNumber value="${summary.wagePeriod}"
										pattern="00" />回
								</td>

								<td><fmt:formatDate
										value="${summary.settlementPeriodStartDate}"
										pattern="yyyy-MM-dd" /> ~ <fmt:formatDate
										value="${summary.settlementPeriodEndDate}"
										pattern="yyyy-MM-dd" /></td>

								<td><fmt:formatDate value="${summary.wagePaymentDate}"
										pattern="yyyy-MM-dd" /></td>

								<td>${summary.employeeCount}</td>

								<td><fmt:formatNumber value="${summary.totalPayment}"
										pattern="#,##0" /></td>

								<td><fmt:formatNumber value="${summary.totalDeduction}"
										pattern="#,##0" /></td>

								<td><fmt:formatNumber value="${summary.netPayment}"
										pattern="#,##0" /></td>

								<td onclick="event.stopPropagation();">
									<form class="ledger-delete-form" method="post"
										action="${pageContext.request.contextPath}/wage/ledgerDelete.do">

										<input type="hidden" name="wageMonth"
											value="${summary.wageMonth}"> <input type="hidden"
											name="wagePeriod" value="${summary.wagePeriod}"> <input
											type="hidden" name="deleteConfirmed" value="false"> <input
											type="hidden" name="deleteFinalConfirmed" value="false">

										<button type="submit">削除</button>
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

							<th><fmt:formatNumber value="${ledgerSummary.totalPayment}"
									pattern="#,##0" /></th>
							<th><fmt:formatNumber
									value="${ledgerSummary.totalDeduction}" pattern="#,##0" /></th>
							<th><fmt:formatNumber value="${ledgerSummary.netPayment}"
									pattern="#,##0" /></th>
							<th></th>
						</tr>
					</tfoot>

				</table>

			</c:otherwise>

		</c:choose>

	</c:if>

	<script>
	document.addEventListener("DOMContentLoaded", function() {

		var deleteMessage = document.getElementById(
			"ledgerDeleteMessage");

		if (deleteMessage) {

			window.alert(
				deleteMessage.textContent.trim());

			if (window.history.replaceState) {

				var currentUrl = new URL(
					window.location.href);

				currentUrl.searchParams.delete(
					"deleteResult");

				window.history.replaceState(
					null,
					document.title,
					currentUrl.pathname
						+ currentUrl.search
						+ currentUrl.hash);
			}
		}

		var deleteForms = document.querySelectorAll(
			".ledger-delete-form");

		deleteForms.forEach(function(form) {

			form.addEventListener(
				"click",
				function(event) {

					event.stopPropagation();
				});

			form.addEventListener(
				"submit",
				function(event) {

					event.preventDefault();
					event.stopPropagation();

					form.elements["deleteConfirmed"].value =
						"false";

					form.elements["deleteFinalConfirmed"].value =
						"false";

					var firstConfirmed = window.confirm(
						"[必読] - [削除機能]\n\n"
						+ "選択した給与回次に該当する\n"
						+ "給与データがすべて削除されます。\n\n"
						+ "削除された給与台帳および給与データは\n"
						+ "復元できません。\n"
						+ "もう一度確認してから削除してください。");

					if (!firstConfirmed) {
						return;
					}

					var finalConfirmed = window.confirm(
						"[警告] 本当に削除しますか？");

					if (!finalConfirmed) {
						return;
					}

					form.elements["deleteConfirmed"].value =
						"true";

					form.elements["deleteFinalConfirmed"].value =
						"true";

					var submitButton = form.querySelector(
						"button[type='submit']");

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