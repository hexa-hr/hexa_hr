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
<title>項目別台帳</title>

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

	<h1>項目別台帳</h1>

	<form class="search-form" method="get"
		action="<%=request.getContextPath()%>/wage/itemLedger.do">

		<div class="search-row">

			<label for="wageTypeId">給与項目</label> <select id="wageTypeId"
				name="wageTypeId" required>

				<option value="">給与項目を選択</option>

				<%
				if (wageTypeOptions != null) {
					for (WageTypeOption option : wageTypeOptions) {

						String optionId = String.valueOf(option.getWageTypeId());

						boolean selected = optionId.equals(selectedWageTypeId);

						String itemTypeName = "P".equals(option.getItemType())
					? "支給"
					: "控除";
				%>
				<option value="<%=optionId%>" <%=selected ? "selected" : ""%>>

					[<%=itemTypeName%>]
					<%=option.getWageTypeName()%>
				</option>
				<%
				}
				}
				%>

			</select> <label for="startMonth">開始月</label> <input type="month"
				id="startMonth" name="startMonth"
				value="<%=startMonth == null ? "" : startMonth%>" required>

			<label for="endMonth">終了月</label> <input type="month" id="endMonth"
				name="endMonth" value="<%=endMonth == null ? "" : endMonth%>"
				required>

			<button type="submit">照会</button>

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
			照会結果:
			<%=ledgerResult.getEmployeeRows().size()%>名,
			<%=ledgerResult.getMonths().size()%>か月
		</div>

		<%
		if (ledgerResult.getEmployeeRows().isEmpty()) {
		%>
		<p>照会された給与履歴がありません。</p>
		<%
		} else {
		%>

		<table class="result-table">
			<thead>
				<tr>
					<th>区分</th>
					<th>社員名</th>
					<th>部署</th>
					<th>役職</th>

					<%
					for (String month : ledgerResult.getMonths()) {
					%>
					<th><%=month%></th>
					<%
					}
					%>

					<th>合計</th>
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
					<th colspan="4">月別合計</th>

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
		<a href="<%=request.getContextPath()%>/wage/ledger.do"> 給与台帳一覧 </a>
	</p>

</body>
</html>