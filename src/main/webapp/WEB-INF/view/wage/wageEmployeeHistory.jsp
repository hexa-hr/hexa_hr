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
body {
	font-family: Arial, sans-serif;
	margin: 0;
}

.search-form {
	border: 1px solid #ccc;
	padding: 20px;
	width: 900px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 10px;
}

label {
	font-weight: bold;
}

input, button {
	padding: 7px;
}

.employee-name {
	width: 140px;
	background-color: #fff;
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

.result-table {
	border-collapse: collapse;
	min-width: 1100px;
	white-space: nowrap;
}

.result-table th, .result-table td {
	border: 1px solid #aaa;
	padding: 8px 10px;
}

.result-table th {
	text-align: center;
	background-color: #f2f2f2;
}

.result-table td {
	text-align: right;
}

.result-table td.month {
	text-align: center;
}

.result-table .group-payroll {
	background-color: #eef8fb;
}

.result-table .group-insurance {
	background-color: #fff3ef;
}

.result-table tfoot th {
	background-color: #fffde0;
	font-weight: bold;
	text-align: right;
}

.modal {
	display: none;
	position: fixed;
	z-index: 1000;
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
	background-color: #fff;
	width: 650px;
	margin: 80px auto;
	padding: 20px;
	border: 1px solid #999;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.modal-header h2 {
	margin: 0 0 15px 0;
}

.employee-filter {
	display: flex;
	gap: 10px;
	margin-bottom: 10px;
}

.employee-table-container {
	max-height: 350px;
	overflow-y: auto;
}

.employee-table {
	width: 100%;
	border-collapse: collapse;
}

.employee-table th, .employee-table td {
	border: 1px solid #ccc;
	padding: 8px;
	text-align: center;
}

.employee-table th {
	background-color: #f2f2f2;
}

.employee-row {
	cursor: pointer;
}

.employee-row:hover {
	background-color: #eef6ff;
}

.employee-row.selected {
	background-color: #d9ebff;
}

.modal-buttons {
	margin-top: 15px;
	text-align: center;
}

.modal-buttons button {
	margin: 0 5px;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h1>社員別給与履歴</h1>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/employeeHistory.do">

		<div class="search-row">

			<label for="startMonth">期間選択</label> <input type="month"
				id="startMonth" name="startMonth"
				value="<c:out value='${startMonth}' />" required> <span>~</span>

			<input type="month" id="endMonth" name="endMonth"
				value="<c:out value='${endMonth}' />" required> <label
				for="selectedEmployeeName">社員選択</label> <input type="hidden"
				id="employeeId" name="employeeId"
				value="<c:out value='${selectedEmployeeId}' />"> <input
				type="text" id="selectedEmployeeName" class="employee-name"
				value="<c:out value='${selectedEmployeeName}' />" readonly>

			<button type="button" id="openEmployeeModal">社員選択</button>

			<button type="submit" name="search" value="true">給与履歴照会</button>

		</div>

		<c:if test="${not empty errorMessage}">
			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

	</form>

	<div id="employeeModal" class="modal">

		<div class="modal-content">

			<div class="modal-header">
				<h2>給与履歴照会対象社員の選択</h2>
				<button type="button" id="closeEmployeeModal">×</button>
			</div>

			<div class="employee-filter">

				<input type="text" id="employeeKeyword" placeholder="社員検索">

				<select id="departmentFilter">
					<option value="">全部署</option>
				</select> <select id="statusFilter">
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

								<td><c:out value="${employee.koreanName}" /></td>

								<td><c:out value="${employee.departmentName}" /></td>

								<td><c:out value="${employee.positionName}" /></td>

								<td><c:out value="${employee.status}" /></td>

							</tr>

						</c:forEach>

					</tbody>

				</table>

			</div>

			<div class="modal-buttons">
				<button type="button" id="selectEmployeeButton">社員選択</button>

				<button type="button" id="cancelEmployeeButton">キャンセル</button>
			</div>

		</div>

	</div>


	<c:if test="${not empty employeeHistory}">

		<div class="result-container">

			<c:choose>

				<c:when test="${empty employeeHistory.rows}">
					<p>照会された給与履歴がありません。</p>
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

									<td class="month"><c:out
											value="${fn:replace(row.wageMonth, '-', '.')}" />(<fmt:formatNumber
											value="${row.wagePeriod}" pattern="00" />)</td>

									<td><fmt:formatNumber value="${row.monthlyRemuneration}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.totalPayment}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.totalDeduction}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.netPayment}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.nationalPension}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.healthInsurance}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.longTermCareInsurance}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.employmentInsurance}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.incomeTax}"
											pattern="#,##0" /></td>

									<td><fmt:formatNumber value="${row.localIncomeTax}"
											pattern="#,##0" /></td>

								</tr>

							</c:forEach>

						</tbody>

						<tfoot>

							<tr>

								<th>合計</th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalMonthlyRemuneration}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalPayment}" pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalDeduction}" pattern="#,##0" />
								</th>

								<th><fmt:formatNumber value="${employeeHistory.netPayment}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalNationalPension}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalHealthInsurance}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalLongTermCareInsurance}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalEmploymentInsurance}"
										pattern="#,##0" /></th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalIncomeTax}" pattern="#,##0" />
								</th>

								<th><fmt:formatNumber
										value="${employeeHistory.totalLocalIncomeTax}" pattern="#,##0" />
								</th>

							</tr>

						</tfoot>

					</table>

				</c:otherwise>

			</c:choose>

		</div>

	</c:if>

	<p>
		<a href="${pageContext.request.contextPath}/wage/ledger.do"> 給与台帳
			一覧 </a>
	</p>

	<script>
		document.addEventListener("DOMContentLoaded", function() {

			const modal = document.getElementById("employeeModal");

			const openButton = document.getElementById("openEmployeeModal");

			const closeButton = document.getElementById("closeEmployeeModal");

			const cancelButton = document
					.getElementById("cancelEmployeeButton");

			const selectButton = document
					.getElementById("selectEmployeeButton");

			const keywordInput = document.getElementById("employeeKeyword");

			const departmentFilter = document
					.getElementById("departmentFilter");

			const statusFilter = document.getElementById("statusFilter");

			const employeeRows = document.querySelectorAll(".employee-row");

			const employeeIdInput = document.getElementById("employeeId");

			const employeeNameInput = document
					.getElementById("selectedEmployeeName");

			let selectedRow = null;

			// 社員一覧を基に部署選択リストを構成
			const departments = new Set();

			employeeRows.forEach(function(row) {

				const department = row.dataset.department;

				if (department) {
					departments.add(department);
				}
			});

			Array.from(departments).sort().forEach(function(department) {

				const option = document.createElement("option");

				option.value = department;
				option.textContent = department;

				departmentFilter.appendChild(option);
			});

			function openModal() {
				modal.style.display = "block";
			}

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

				employeeRows
						.forEach(function(row) {

							const name = row.dataset.name || "";

							const rowDepartment = row.dataset.department || "";

							const rowStatus = row.dataset.status || "";

							const keywordMatched = keyword === ""
									|| name.includes(keyword);

							const departmentMatched = department === ""
									|| rowDepartment === department;

							const statusMatched = status === ""
									|| rowStatus === status;

							row.style.display = keywordMatched
									&& departmentMatched && statusMatched ? ""
									: "none";
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