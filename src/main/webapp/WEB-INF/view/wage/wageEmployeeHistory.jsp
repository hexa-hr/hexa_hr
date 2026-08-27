<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員別給与履歴</title>

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

/* 3. 상단 검색 폼 영역 */
.search-form {
	background: #f4f4f4;
	padding: 15px 20px;
	border: 1px solid #ddd;
	border-radius: 3px;
	margin-bottom: 25px;
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

input[type="month"], input[type="text"], button {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

input[type="month"] { width: 140px; }

.employee-name {
	width: 150px;
	background-color: #fff;
	text-align: center;
	font-weight: bold;
	color: #4e73df;
}

/* 버튼 스타일 통일 */
button {
	cursor: pointer;
	font-weight: bold;
	border: none;
	color: white;
}
.btn-select { background-color: #a5a5a5; }
.btn-select:hover { background-color: #858796; }

.btn-search { background-color: #4e73df; }
.btn-search:hover { background-color: #2e59d9; }

/* 4. 데이터 테이블 스타일 */
.result-container {
	margin-top: 25px;
	overflow-x: auto;
}

table.result-table {
	border-collapse: collapse;
	width: 100%;
	min-width: 1100px;
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
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
}

table.result-table td {
	text-align: right; /* 금액 우측 정렬 */
}

table.result-table td.month {
	text-align: center;
}

table.result-table tbody tr:hover td {
	background-color: #f1f5f9;
}

/* 그룹 헤더 색상 */
table.result-table .group-payroll {
	background-color: #eef6ff;
	color: #4e73df;
}
table.result-table .group-insurance {
	background-color: #fff4f1;
	color: #e74a3b;
}

/* 푸터(총계) 행 강조 */
table.result-table tfoot th {
	background-color: #f4f4f4;
	border-top: 2px solid #4e73df;
	color: #333;
	text-align: right;
}

/* 5. 모달 팝업 스타일 */
.modal {
	display: none;
	position: fixed;
	z-index: 1000;
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.45);
}

.modal-content {
	background-color: #fff;
	width: 700px;
	margin: 80px auto;
	padding: 30px;
	border: 0;
	border-radius: 5px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
	box-sizing: border-box;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 8px;
}

.modal-header h2 {
	margin: 0;
	font-size: 18px;
	color: #333;
}
.modal-header button {
	background: none;
	border: none;
	font-size: 24px;
	color: #999;
	padding: 0;
}
.modal-header button:hover { color: #333; }

.employee-filter {
	display: flex;
	gap: 10px;
	margin-bottom: 15px;
}

.employee-filter select {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	outline: none;
}

.employee-table-container {
	max-height: 350px;
	overflow-y: auto;
	border: 1px solid #ccc;
}

.employee-table {
	width: 100%;
	border-collapse: collapse;
}

.employee-table th, .employee-table td {
	border: 1px solid #ccc;
	padding: 10px;
	text-align: center;
	font-size: 14px;
}

.employee-table th {
	background-color: #f8f9fa;
	position: sticky;
	top: 0;
}

.employee-row { cursor: pointer; }
.employee-row:hover { background-color: #f1f5f9; }
.employee-row.selected { background-color: #e2e8f0; font-weight: bold; }

.modal-buttons {
	margin-top: 20px;
	text-align: center;
}

.modal-buttons button {
	margin: 0 5px;
	padding: 8px 25px;
}

/* 기타 유틸리티 */
.error-message {
	margin-top: 15px;
	color: #e74a3b;
	font-weight: bold;
	font-size: 14px;
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

.bottom-actions {
	text-align: center;
	margin-top: 20px;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			
			<div class="page-header">
				<h1>社員別給与履歴</h1>
			</div>

			<form class="search-form" method="get" action="${pageContext.request.contextPath}/wage/employeeHistory.do">
				<div class="search-row">
					<label for="startMonth">期間選択</label> 
					<input type="month" id="startMonth" name="startMonth" value="<c:out value='${startMonth}' />" required> 
					<span style="font-weight: bold; color: #666;">~</span>
					<input type="month" id="endMonth" name="endMonth" value="<c:out value='${endMonth}' />" required> 
					
					<label for="selectedEmployeeName" style="margin-left: 20px;">社員選択</label> 
					<input type="hidden" id="employeeId" name="employeeId" value="<c:out value='${selectedEmployeeId}' />"> 
					<input type="text" id="selectedEmployeeName" class="employee-name" value="<c:out value='${selectedEmployeeName}' />" readonly placeholder="社員を選択">

					<button type="button" id="openEmployeeModal" class="btn-select">社員検索</button>
					<button type="submit" name="search" value="true" class="btn-search" style="margin-left: 10px;">給与履歴照会</button>
				</div>

				<c:if test="${not empty errorMessage}">
					<div class="error-message">
						<c:out value="${errorMessage}" />
					</div>
				</c:if>
			</form>

			<c:if test="${not empty employeeHistory}">
				<div class="result-container">
					<c:choose>
						<c:when test="${empty employeeHistory.rows}">
							<div style="padding: 30px; text-align: center; color: #777; border: 1px solid #ccc; background-color: #f8f9fa; border-radius: 3px;">
								照会された給与履歴がありません。
							</div>
						</c:when>

						<c:otherwise>
							<table class="result-table">
								<thead>
									<tr>
										<th colspan="5" class="group-payroll">月別給与履歴</th>
										<th colspan="6" class="group-insurance">4大保険および所得税内訳</th>
									</tr>
									<tr>
										<th>給与月（回次）</th>
										<th>報酬月額</th>
										<th>支給合計</th>
										<th>控除合計</th>
										<th>差引支給額</th>

										<th>国民年金</th>
										<th>健康保険</th>
										<th>介護保険</th>
										<th>雇用保険</th>
										<th>所得税</th>
										<th>住民税</th>
									</tr>
								</thead>

								<tbody>
									<c:forEach var="row" items="${employeeHistory.rows}">
										<tr>
											<td class="month"><c:out value="${fn:replace(row.wageMonth, '-', '.')}" />(<fmt:formatNumber value="${row.wagePeriod}" pattern="00" />)</td>
											<td><fmt:formatNumber value="${row.monthlyRemuneration}" pattern="#,##0" /></td>
											<td style="color: #4e73df; font-weight: bold;"><fmt:formatNumber value="${row.totalPayment}" pattern="#,##0" /></td>
											<td style="color: #e74a3b; font-weight: bold;"><fmt:formatNumber value="${row.totalDeduction}" pattern="#,##0" /></td>
											<td style="color: #333; font-weight: bold; background-color: #f8f9fa;"><fmt:formatNumber value="${row.netPayment}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.nationalPension}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.healthInsurance}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.longTermCareInsurance}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.employmentInsurance}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.incomeTax}" pattern="#,##0" /></td>
											<td><fmt:formatNumber value="${row.localIncomeTax}" pattern="#,##0" /></td>
										</tr>
									</c:forEach>
								</tbody>

								<tfoot>
									<tr>
										<th style="text-align: center;">合計</th>
										<th><fmt:formatNumber value="${employeeHistory.totalMonthlyRemuneration}" pattern="#,##0" /></th>
										<th style="color: #4e73df;"><fmt:formatNumber value="${employeeHistory.totalPayment}" pattern="#,##0" /></th>
										<th style="color: #e74a3b;"><fmt:formatNumber value="${employeeHistory.totalDeduction}" pattern="#,##0" /></th>
										<th style="color: #333; background-color: #eaeaea;"><fmt:formatNumber value="${employeeHistory.netPayment}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalNationalPension}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalHealthInsurance}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalLongTermCareInsurance}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalEmploymentInsurance}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalIncomeTax}" pattern="#,##0" /></th>
										<th><fmt:formatNumber value="${employeeHistory.totalLocalIncomeTax}" pattern="#,##0" /></th>
									</tr>
								</tfoot>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

			<div class="bottom-actions">
				<a class="btn-list" href="${pageContext.request.contextPath}/wage/ledger.do">給与台帳 一覧へ戻る</a>
			</div>
		</div>
	</div>

	<!-- 사원 검색 모달 -->
	<div id="employeeModal" class="modal">
		<div class="modal-content">
			<div class="modal-header">
				<h2>給与履歴照会対象社員の選択</h2>
				<button type="button" id="closeEmployeeModal">×</button>
			</div>

			<div class="employee-filter">
				<input type="text" id="employeeKeyword" placeholder="社員検索" style="flex: 1;">
				<select id="departmentFilter">
					<option value="">全部署</option>
				</select> 
				<select id="statusFilter">
					<option value="">すべての状態</option>
					<option value="재직">在職</option>
					<option value="퇴직">退職</option>
				</select>
			</div>

			<div class="employee-table-container">
				<table class="employee-table">
					<thead>
						<tr>
							<th>区分</th>
							<th>氏名</th>
							<th>部署</th>
							<th>役職</th>
							<th>状態</th>
						</tr>
					</thead>
					<tbody id="employeeTableBody">
						<c:forEach var="employee" items="${employeeRows}">
							<tr class="employee-row"
								data-employee-id="<c:out value='${employee.employeeId}' />"
								data-name="<c:out value='${employee.koreanName}' />"
								data-department="<c:out value='${employee.departmentName}' />"
								data-status="<c:out value='${employee.status}' />">
								<td><c:out value="${employee.employmentType}" /></td>
								<td style="font-weight: bold;"><c:out value="${employee.koreanName}" /></td>
								<td><c:out value="${empty employee.departmentName ? '-' : employee.departmentName}" /></td>
								<td><c:out value="${empty employee.positionName ? '-' : employee.positionName}" /></td>
								<td><c:out value="${employee.status}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>

			<div class="modal-buttons">
				<button type="button" id="selectEmployeeButton" class="btn-search">社員選択</button>
				<button type="button" id="cancelEmployeeButton" class="btn-select">キャンセル</button>
			</div>
		</div>
	</div>

	<script>
		document.addEventListener("DOMContentLoaded", function() {
			const modal = document.getElementById("employeeModal");
			const openButton = document.getElementById("openEmployeeModal");
			const closeButton = document.getElementById("closeEmployeeModal");
			const cancelButton = document.getElementById("cancelEmployeeButton");
			const selectButton = document.getElementById("selectEmployeeButton");
			const keywordInput = document.getElementById("employeeKeyword");
			const departmentFilter = document.getElementById("departmentFilter");
			const statusFilter = document.getElementById("statusFilter");
			const employeeRows = document.querySelectorAll(".employee-row");
			const employeeIdInput = document.getElementById("employeeId");
			const employeeNameInput = document.getElementById("selectedEmployeeName");

			let selectedRow = null;

			const departments = new Set();
			employeeRows.forEach(function(row) {
				const department = row.dataset.department;
				if (department && department !== '-') {
					departments.add(department);
				}
			});

			Array.from(departments).sort().forEach(function(department) {
				const option = document.createElement("option");
				option.value = department;
				option.textContent = department;
				departmentFilter.appendChild(option);
			});

			function openModal() { modal.style.display = "block"; }

			function closeModal() {
				modal.style.display = "none";
				selectedRow = null;
				employeeRows.forEach(function(row) {
					row.classList.remove("selected");
				});
			}

			function filterEmployees() {
				const keyword = keywordInput.value.trim();
				const department = departmentFilter.value;
				const status = statusFilter.value;

				employeeRows.forEach(function(row) {
					const name = row.dataset.name || "";
					const rowDepartment = row.dataset.department || "";
					const rowStatus = row.dataset.status || "";

					const keywordMatched = keyword === "" || name.includes(keyword);
					const departmentMatched = department === "" || rowDepartment === department;
					const statusMatched = status === "" || rowStatus === status;

					row.style.display = keywordMatched && departmentMatched && statusMatched ? "" : "none";
				});
			}

			openButton.addEventListener("click", openModal);
			closeButton.addEventListener("click", closeModal);
			cancelButton.addEventListener("click", closeModal);
			keywordInput.addEventListener("input", filterEmployees);
			departmentFilter.addEventListener("change", filterEmployees);
			statusFilter.addEventListener("change", filterEmployees);

			employeeRows.forEach(function(row) {
				row.addEventListener("click", function() {
					employeeRows.forEach(function(otherRow) {
						otherRow.classList.remove("selected");
					});
					row.classList.add("selected");
					selectedRow = row;
				});
			});

			selectButton.addEventListener("click", function() {
				if (selectedRow == null) {
					alert("社員を選択してください。");
					return;
				}
				employeeIdInput.value = selectedRow.dataset.employeeId;
				employeeNameInput.value = selectedRow.dataset.name;
				closeModal();
			});

			window.addEventListener("click", function(event) {
				if (event.target === modal) {
					closeModal();
				}
			});
		});
	</script>

</body>
</html>