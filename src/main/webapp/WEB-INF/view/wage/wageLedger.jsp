<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여대장</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

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
						</tr>
					</tfoot>

				</table>

			</c:otherwise>

		</c:choose>

	</c:if>

</body>
</html>