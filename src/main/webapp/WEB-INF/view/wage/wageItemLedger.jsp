<%@ page import="wage.model.WageItemLedgerEmployeeRow"%>
<%@ page import="java.util.List"%>
<%@ page import="master.model.WageTypeOption"%>
<%@ page import="wage.model.WageItemLedgerResult"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
List<WageTypeOption> wageTypeOptions = (List<WageTypeOption>)request.getAttribute("wageTypeOptions");

String selectedWageTypeId = (String)request.getAttribute("selectedWageTypeId");

String startMonth = (String)request.getAttribute("startMonth");

String endMonth = (String)request.getAttribute("endMonth");

String errorMessage = (String)request.getAttribute("errorMessage");

WageItemLedgerResult ledgerResult = (WageItemLedgerResult)request.getAttribute("ledgerResult");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>항목별 대장</title>

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
	width: 800px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 15px;
}

label {
	font-weight: bold;
}

select, input, button {
	padding: 7px;
}

select {
	width: 180px;
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
	margin-top: 25px;
	overflow-x: auto;
}

.result-summary {
	margin-bottom: 10px;
	font-weight: bold;
}

.result-table {
	border-collapse: collapse;
	min-width: 900px;
	white-space: nowrap;
}

.result-table th, .result-table td {
	border: 1px solid #aaa;
	padding: 8px 12px;
}

.result-table th {
	background-color: #f2f2f2;
	text-align: center;
}

.result-table td {
	text-align: right;
}

.result-table td.employee-info {
	text-align: left;
}

.result-table tfoot th {
	background-color: #e8e8e8;
	font-weight: bold;
	text-align: right;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h1>항목별 대장</h1>

	<form class="search-form" method="get"
		action="<%=request.getContextPath()%>/wage/itemLedger.do">

		<div class="search-row">

			<label for="wageTypeId">급여항목</label> <select id="wageTypeId"
				name="wageTypeId" required>

				<option value="">급여항목 선택</option>

				<%
				if (wageTypeOptions != null) {
					for (WageTypeOption option : wageTypeOptions) {

						String optionId = String.valueOf(option.getWageTypeId());

						boolean selected = optionId.equals(selectedWageTypeId);

						String itemTypeName = "P".equals(option.getItemType())
					? "지급"
					: "공제";
				%>
				<option value="<%=optionId%>" <%=selected ? "selected" : ""%>>

					[<%=itemTypeName%>]
					<%=option.getWageTypeName()%>
				</option>
				<%
				}
				}
				%>

			</select> <label for="startMonth">시작월</label> <input type="month"
				id="startMonth" name="startMonth"
				value="<%=startMonth == null ? "" : startMonth%>" required>

			<label for="endMonth">종료월</label> <input type="month" id="endMonth"
				name="endMonth" value="<%=endMonth == null ? "" : endMonth%>"
				required>

			<button type="submit">조회</button>

		</div>

		<%
		if (errorMessage != null) {
		%>
		<div class="error-message">
			<%=errorMessage%>
		</div>
		<%
		}
		%>

	</form>

	<%
	if (ledgerResult != null) {
	%>
	<div class="result-container">

		<div class="result-summary">
			조회 결과:
			<%=ledgerResult.getEmployeeRows().size()%>명,
			<%=ledgerResult.getMonths().size()%>개월
		</div>

		<%
		if (ledgerResult.getEmployeeRows().isEmpty()) {
		%>
		<p>조회된 급여내역이 없습니다.</p>
		<%
		} else {
		%>

		<table class="result-table">
			<thead>
				<tr>
					<th>구분</th>
					<th>사원명</th>
					<th>부서</th>
					<th>직위</th>

					<%
					for (String month : ledgerResult.getMonths()) {
					%>
					<th><%=month%></th>
					<%
					}
					%>

					<th>합계</th>
				</tr>
			</thead>

			<tbody>
				<%
				for (WageItemLedgerEmployeeRow employeeRow : ledgerResult.getEmployeeRows()) {
				%>
				<tr>
					<td class="employee-info"><%=employeeRow.getEmploymentType() == null
	? ""
	: employeeRow.getEmploymentType()%></td>

					<td class="employee-info"><%=employeeRow.getKoreanName()%></td>

					<td class="employee-info"><%=employeeRow.getDepartmentName() == null
	? ""
	: employeeRow.getDepartmentName()%></td>

					<td class="employee-info"><%=employeeRow.getPositionName() == null
	? ""
	: employeeRow.getPositionName()%></td>

					<%
					for (String month : ledgerResult.getMonths()) {
						Long wageValue = employeeRow
							.getMonthlyValues()
							.get(month);
					%>
					<td><%=String.format("%,d", wageValue)%></td>
					<%
					}
					%>

					<td><strong> <%=String.format(
	"%,d",
	employeeRow.getTotalValue())%>
					</strong></td>
				</tr>
				<%
				}
				%>
			</tbody>

			<tfoot>
				<tr>
					<th colspan="4">월별 합계</th>

					<%
					for (String month : ledgerResult.getMonths()) {
						Long monthlyTotal = ledgerResult
							.getMonthlyTotals()
							.get(month);
					%>
					<th><%=String.format("%,d", monthlyTotal)%></th>
					<%
					}
					%>

					<th><%=String.format(
	"%,d",
	ledgerResult.getGrandTotal())%></th>
				</tr>
			</tfoot>
		</table>

		<%
		}
		%>

	</div>
	<%
	}
	%>

	<p>
		<a href="<%=request.getContextPath()%>/wage/ledger.do"> 급여대장 목록 </a>
	</p>

</body>
</html>