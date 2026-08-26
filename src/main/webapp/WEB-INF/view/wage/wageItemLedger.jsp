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

/* 3. 검색 폼 영역 */
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

.filter-bar select, .filter-bar input[type="month"] {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

.filter-bar select {
	width: 220px;
}

/* 버튼 스타일 */
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

/* 4. 메시지 스타일 */
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

.result-summary {
	margin-bottom: 15px;
	font-weight: bold;
	color: #4e73df;
	font-size: 14px;
}

/* 5. 데이터 테이블 */
.result-container {
	margin-top: 10px;
	overflow-x: auto;
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

table.data-table tfoot th {
	background-color: #f4f4f4;
	border-top: 2px solid #4e73df;
	color: #333;
	text-align: right;
}

table.data-table tfoot th:first-child {
	text-align: center;
}

/* 유틸리티 */
.text-right { text-align: right; }
.text-blue { color: #4e73df; font-weight: bold; }
.bottom-actions {
	margin-top: 20px;
	text-align: center;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			
			<div class="page-header">
				<h1>項目別台帳</h1>
			</div>

			<form method="get" action="<%=request.getContextPath()%>/wage/itemLedger.do">
				<div class="filter-bar">
					<label for="wageTypeId">給与項目</label> 
					<select id="wageTypeId" name="wageTypeId" required>
						<option value="">給与項目を選択</option>
						<%
						if (wageTypeOptions != null) {
							for (WageTypeOption option : wageTypeOptions) {
								String optionId = String.valueOf(option.getWageTypeId());
								boolean selected = optionId.equals(selectedWageTypeId);
								String itemTypeName = "P".equals(option.getItemType()) ? "支給" : "控除";
						%>
						<option value="<%=optionId%>" <%=selected ? "selected" : ""%>>
							[<%=itemTypeName%>] <%=option.getWageTypeName()%>
						</option>
						<%
							}
						}
						%>
					</select> 
					
					<label for="startMonth">開始月</label> 
					<input type="month" id="startMonth" name="startMonth" value="<%=startMonth == null ? "" : startMonth%>" required>

					<label for="endMonth">終了月</label> 
					<input type="month" id="endMonth" name="endMonth" value="<%=endMonth == null ? "" : endMonth%>" required>

					<button type="submit" class="btn-search">照会</button>
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
					<i class="fas fa-info-circle"></i> 照会結果: <%=ledgerResult.getEmployeeRows().size()%>名, <%=ledgerResult.getMonths().size()%>か月
				</div>

				<%
				if (ledgerResult.getEmployeeRows().isEmpty()) {
				%>
				<div class="empty-message">照会された給与履歴がありません。</div>
				<%
				} else {
				%>

				<table class="data-table">
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

							<th class="text-blue">合計</th>
						</tr>
					</thead>

					<tbody>
						<%
						for (WageItemLedgerEmployeeRow employeeRow : ledgerResult.getEmployeeRows()) {
						%>
						<tr>
							<td><%=employeeRow.getEmploymentType() == null ? "" : employeeRow.getEmploymentType()%></td>
							<td style="font-weight: bold;"><%=employeeRow.getKoreanName()%></td>
							<td><%=employeeRow.getDepartmentName() == null ? "" : employeeRow.getDepartmentName()%></td>
							<td><%=employeeRow.getPositionName() == null ? "" : employeeRow.getPositionName()%></td>

							<%
							for (String month : ledgerResult.getMonths()) {
								Long wageValue = employeeRow.getMonthlyValues().get(month);
							%>
							<td class="text-right"><%=String.format("%,d", wageValue)%></td>
							<%
							}
							%>

							<td class="text-right text-blue"><%=String.format("%,d", employeeRow.getTotalValue())%></td>
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
								Long monthlyTotal = ledgerResult.getMonthlyTotals().get(month);
							%>
							<th class="text-right"><%=String.format("%,d", monthlyTotal)%></th>
							<%
							}
							%>

							<th class="text-right text-blue"><%=String.format("%,d", ledgerResult.getGrandTotal())%></th>
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

			<div class="bottom-actions">
				<a class="btn-list" href="<%=request.getContextPath()%>/wage/ledger.do">給与台帳 一覧へ戻る</a>
			</div>
			
		</div>
	</div>

</body>
</html>