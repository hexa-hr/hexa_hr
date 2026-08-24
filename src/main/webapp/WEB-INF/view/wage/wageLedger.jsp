<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여대장</title>

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

	<h2>급여대장</h2>

	<form method="get"
		action="${pageContext.request.contextPath}/wage/ledger.do">

		<label for="year">귀속연도</label> <input type="number" id="year"
			name="year" min="1000" max="9999" value="${selectedYear}">

		<button type="submit">조회</button>
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
				<p>조회된 급여내역이 없습니다.</p>
			</c:when>

			<c:otherwise>

				<table border="1">
					<thead>
						<tr>
							<th>귀속연월</th>
							<th>급여차수</th>
							<th>정산기간</th>
							<th>지급일</th>
							<th>인원</th>
							<th>지급총액</th>
							<th>공제총액</th>
							<th>실지급액</th>
							<th>삭제</th>
						</tr>
					</thead>

					<tbody>

						<c:forEach var="summary" items="${ledgerSummary.summaries}">


							<tr
								onclick="location.href='${pageContext.request.contextPath}/wage/ledgerDetail.do?wageMonth=${summary.wageMonth}&wagePeriod=${summary.wagePeriod}'"
								style="cursor: pointer;">
								<td>${summary.wageMonth}</td>

								<td>급여-<fmt:formatNumber value="${summary.wagePeriod}"
										pattern="00" />차
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

										<button type="submit">삭제</button>
									</form>
								</td>
							</tr>

						</c:forEach>

					</tbody>

					<tfoot>
						<tr>
							<th>합계</th>
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
						"[필독] - [삭제기능]\n\n"
						+ "선택하신 급여차수에 해당하는\n"
						+ "급여데이터가 전부 삭제됩니다.\n\n"
						+ "삭제된 급여대장 및 급여데이터는\n"
						+ "복구할 수 없습니다.\n"
						+ "다시 한번 확인하시고 삭제해 주세요.");

					if (!firstConfirmed) {
						return;
					}

					var finalConfirmed = window.confirm(
						"[경고] 정말 삭제하시겠습니까?");

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