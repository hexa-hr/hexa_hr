<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与台帳詳細</title>
</head>
<body>

	<h2>給与台帳詳細</h2>

	<c:if test="${not empty errorMessage}">
		<p>
			<c:out value="${errorMessage}" />
		</p>
	</c:if>

	<c:if test="${empty errorMessage and not empty ledgerDetail}">

		<c:set var="summary" value="${ledgerDetail.summary}" />

		<p>
			帰属年月:
			<c:out value="${summary.wageMonth}" />
			&nbsp;&nbsp; 給与回次: 給与-
			<fmt:formatNumber value="${summary.wagePeriod}" pattern="00" />
			回 &nbsp;&nbsp; 精算期間:
			<fmt:formatDate value="${summary.settlementPeriodStartDate}"
				pattern="yyyy-MM-dd" />
			~
			<fmt:formatDate value="${summary.settlementPeriodEndDate}"
				pattern="yyyy-MM-dd" />
			&nbsp;&nbsp; 支給日:
			<fmt:formatDate value="${summary.wagePaymentDate}"
				pattern="yyyy-MM-dd" />
		</p>

		<div style="overflow-x: auto;">

			<form method="get"
				action="${pageContext.request.contextPath}/wage/ledgerDetail.do">

				<input type="hidden" name="wageMonth"
					value="<c:out value='${summary.wageMonth}' />"> <input
					type="hidden" name="wagePeriod"
					value="<c:out value='${summary.wagePeriod}' />">

				<!-- 雇用形態 -->
				<select name="employmentType">
					<option value="">すべて</option>

					<option value="정규직"
						<c:if test="${employmentType eq '정규직'}">selected</c:if>>
						正社員</option>

					<option value="계약직"
						<c:if test="${employmentType eq '계약직'}">selected</c:if>>
						契約社員</option>

					<option value="임시직"
						<c:if test="${employmentType eq '임시직'}">selected</c:if>>
						臨時社員</option>

					<option value="파견직"
						<c:if test="${employmentType eq '파견직'}">selected</c:if>>
						派遣社員</option>

					<option value="위촉직"
						<c:if test="${employmentType eq '위촉직'}">selected</c:if>>
						業務委託</option>

					<option value="일용직"
						<c:if test="${employmentType eq '일용직'}">selected</c:if>>
						日雇い</option>
				</select>


				<!-- 部署 -->
				<select name="departmentId">
					<option value="">全部署</option>

					<c:forEach var="department" items="${ledgerDetail.departments}">

						<option value="${department.departmentId}"
							<c:if test="${departmentId eq department.departmentId.toString()}">
					selected
				</c:if>>

							<c:out value="${department.departmentName}" />
						</option>

					</c:forEach>
				</select>


				<!-- 所得者区分 -->
				<select name="incomeType">
					<option value="">すべて</option>

					<option value="worker"
						<c:if test="${incomeType eq 'worker'}">selected</c:if>>
						給与所得者</option>

					<option value="business"
						<c:if test="${incomeType eq 'business'}">selected</c:if>>
						事業所得者</option>

					<option value="daily"
						<c:if test="${incomeType eq 'daily'}">selected</c:if>>
						日雇労働者</option>
				</select>

				<button type="submit">照会</button>

			</form>

			<table border="1">
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

						<th>支給総額</th>

						<c:forEach var="type" items="${ledgerDetail.deductionTypes}">
							<th><c:out value="${type.wageTypeName}" /></th>
						</c:forEach>

						<th>控除総額</th>
						<th>差引支給額</th>
					</tr>
				</thead>

				<tbody>

					<c:forEach var="row" items="${ledgerDetail.employeeRows}">

						<tr>
							<td><c:out value="${row.employmentType}" /></td>

							<td><c:out value="${row.koreanName}" /></td>

							<td><fmt:formatDate value="${row.hireDate}"
									pattern="yyyy-MM-dd" /></td>

							<td><c:out value="${row.departmentName}" /></td>

							<td><c:out value="${row.positionName}" /></td>

							<c:forEach var="type" items="${ledgerDetail.paymentTypes}">

								<td><fmt:formatNumber
										value="${row.wageValues[type.wageTypeId]}" pattern="#,##0" />
								</td>

							</c:forEach>

							<td><fmt:formatNumber value="${row.totalPayment}"
									pattern="#,##0" /></td>

							<c:forEach var="type" items="${ledgerDetail.deductionTypes}">

								<td><fmt:formatNumber
										value="${row.wageValues[type.wageTypeId]}" pattern="#,##0" />
								</td>

							</c:forEach>

							<td><fmt:formatNumber value="${row.totalDeduction}"
									pattern="#,##0" /></td>

							<td><fmt:formatNumber value="${row.netPayment}"
									pattern="#,##0" /></td>
						</tr>

					</c:forEach>

				</tbody>

				<tfoot>
					<tr>
						<th colspan="5">合計</th>

						<c:forEach var="type" items="${ledgerDetail.paymentTypes}">
							<th><fmt:formatNumber
									value="${ledgerDetail.itemTotals[type.wageTypeId]}"
									pattern="#,##0" /></th>
						</c:forEach>

						<th><fmt:formatNumber value="${ledgerDetail.totalPayment}"
								pattern="#,##0" /></th>

						<c:forEach var="type" items="${ledgerDetail.deductionTypes}">
							<th><fmt:formatNumber
									value="${ledgerDetail.itemTotals[type.wageTypeId]}"
									pattern="#,##0" /></th>
						</c:forEach>

						<th><fmt:formatNumber value="${ledgerDetail.totalDeduction}"
								pattern="#,##0" /></th>

						<th><fmt:formatNumber value="${ledgerDetail.netPayment}"
								pattern="#,##0" /></th>
					</tr>
				</tfoot>

			</table>

		</div>

		<p>
			<a
				href="${pageContext.request.contextPath}/wage/ledger.do?year=${fn:substring(summary.wageMonth, 0, 4)}">
				給与台帳一覧 </a>
		</p>

	</c:if>

</body>
</html>