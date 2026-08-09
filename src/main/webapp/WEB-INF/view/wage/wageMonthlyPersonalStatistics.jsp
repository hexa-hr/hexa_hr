<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>월별 개인급여 통계</title>

<style>
body {
	font-family: Arial, sans-serif;
	margin: 30px;
}

.description {
	margin-bottom: 20px;
}

.search-form {
	border: 1px solid #ccc;
	padding: 20px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 10px;
}

label {
	font-weight: bold;
}

input, button, select {
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
	margin-top: 30px;
	overflow-x: auto;
}

.result-table {
	border-collapse: collapse;
	min-width: 1400px;
	width: 100%;
	white-space: nowrap;
}

.result-table th, .result-table td {
	border: 1px solid #aaa;
	padding: 9px 12px;
	text-align: right;
}

.result-table th {
	background-color: #f2f2f2;
	text-align: center;
}

.result-table .row-title {
	text-align: left;
	font-weight: bold;
	background-color: #f8f8f8;
}

.result-table .sub-title {
	text-align: left;
	padding-left: 25px;
	background-color: #fafafa;
}

.result-table .total-column {
	background-color: #fffde0;
	font-weight: bold;
}

/* 사원 선택 모달 */
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
	width: 700px;
	margin: 60px auto;
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

	<h1>월별 개인급여 통계</h1>

	<p class="description">귀속년도와 사원을 선택하면 해당 사원의 월별 급여현황을 확인할 수 있습니다.</p>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/monthlyPersonalStatistics.do">

		<div class="search-row">

			<label for="year">귀속년도</label> <input type="number" id="year"
				name="year" min="1000" max="9999"
				value="<c:out value='${selectedYear}' />" required> <label
				for="selectedEmployeeName">대상자</label> <input type="hidden"
				id="employeeId" name="employeeId"
				value="<c:out value='${selectedEmployeeId}' />"> <input
				type="text" id="selectedEmployeeName" class="employee-name"
				value="<c:out value='${selectedEmployeeName}' />"
				placeholder="대상자를 선택해 주세요." readonly>

			<button type="button" id="openEmployeeModal">사원선택</button>

			<button type="submit" name="search" value="true">조회</button>

		</div>

		<c:if test="${not empty errorMessage}">
			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

	</form>


	<!-- 사원 선택 모달 -->
	<div id="employeeModal" class="modal">

		<div class="modal-content">

			<div class="modal-header">
				<h2>급여통계 사원선택</h2>

				<button type="button" id="closeEmployeeModal">×</button>
			</div>

			<div class="employee-filter">

				<input type="text" id="employeeKeyword" placeholder="사원검색">

				<select id="departmentFilter">
					<option value="">전체 부서</option>
				</select> <select id="statusFilter">
					<option value="">전체 상태</option>
					<option value="재직">재직</option>
					<option value="퇴직">퇴직</option>
				</select>

			</div>


			<div class="employee-table-container">

				<table class="employee-table">

					<thead>
						<tr>
							<th>구분</th>
							<th>성명</th>
							<th>부서</th>
							<th>직위</th>
							<th>상태</th>
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

				<button type="button" id="selectEmployeeButton">사원선택</button>

				<button type="button" id="cancelEmployeeButton">선택취소</button>

			</div>

		</div>

	</div>


	<!-- 월별 개인급여 통계 -->
	<c:if test="${not empty monthlyPersonalStatistics}">

		<div class="result-container">

			<table class="result-table">

				<thead>
					<tr>

						<th>구분</th>

						<c:forEach var="row" items="${monthlyPersonalStatistics.rows}"
							varStatus="status">

							<th>${status.count}월</th>

						</c:forEach>

						<th class="total-column">합계</th>

					</tr>
				</thead>


				<tbody>

					<tr>

						<td class="row-title">월급여액 (원)</td>

						<c:forEach var="row" items="${monthlyPersonalStatistics.rows}">

							<td><fmt:formatNumber value="${row.totalPayment}"
									pattern="#,##0" /></td>

						</c:forEach>

						<td class="total-column"><fmt:formatNumber
								value="${monthlyPersonalStatistics.totalPayment}"
								pattern="#,##0" /></td>

					</tr>


					<tr>

						<td class="sub-title">└ 공제액 (원)</td>

						<c:forEach var="row" items="${monthlyPersonalStatistics.rows}">

							<td><fmt:formatNumber value="${row.totalDeduction}"
									pattern="#,##0" /></td>

						</c:forEach>

						<td class="total-column"><fmt:formatNumber
								value="${monthlyPersonalStatistics.totalDeduction}"
								pattern="#,##0" /></td>

					</tr>


					<tr>

						<td class="sub-title">└ 실지급액 (원)</td>

						<c:forEach var="row" items="${monthlyPersonalStatistics.rows}">

							<td><fmt:formatNumber value="${row.netPayment}"
									pattern="#,##0" /></td>

						</c:forEach>

						<td class="total-column"><fmt:formatNumber
								value="${monthlyPersonalStatistics.netPayment}" pattern="#,##0" />
						</td>

					</tr>

				</tbody>

			</table>

		</div>

	</c:if>


	<script>
		document.addEventListener("DOMContentLoaded", function() {

			const modal = document.getElementById("employeeModal");

			const openButton = document.getElementById("openEmployeeModal");

			const closeButton = document.getElementById("closeEmployeeModal");

			const cancelButton = document
					.getElementById("cancelEmployeeButton");

			const selectButton = document
					.getElementById("selectEmployeeButton");

			const employeeIdInput = document.getElementById("employeeId");

			const employeeNameInput = document
					.getElementById("selectedEmployeeName");

			const keywordInput = document.getElementById("employeeKeyword");

			const departmentFilter = document
					.getElementById("departmentFilter");

			const statusFilter = document.getElementById("statusFilter");

			const employeeRows = Array.from(document
					.querySelectorAll(".employee-row"));

			let selectedRow = null;

			// 부서 목록 생성
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

			// 기존 선택 사원이 있으면 선택 상태 복원
			employeeRows.forEach(function(row) {

				if (row.dataset.employeeId === employeeIdInput.value) {

					row.classList.add("selected");
					selectedRow = row;
				}
			});

			function filterEmployees() {

				const keyword = keywordInput.value.trim().toLowerCase();

				const department = departmentFilter.value;

				const status = statusFilter.value;

				employeeRows
						.forEach(function(row) {

							const name = row.dataset.name.toLowerCase();

							const rowDepartment = row.dataset.department;

							const rowStatus = row.dataset.status;

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

			openButton.addEventListener("click", function() {
				modal.style.display = "block";
			});

			closeButton.addEventListener("click", function() {
				modal.style.display = "none";
			});

			cancelButton.addEventListener("click", function() {
				modal.style.display = "none";
			});

			employeeRows.forEach(function(row) {

				row.addEventListener("click", function() {

					employeeRows.forEach(function(otherRow) {
						otherRow.classList.remove("selected");
					});

					row.classList.add("selected");

					selectedRow = row;
				});

				row.addEventListener("dblclick", function() {

					employeeIdInput.value = row.dataset.employeeId;

					employeeNameInput.value = row.dataset.name;

					modal.style.display = "none";
				});
			});

			selectButton.addEventListener("click", function() {

				if (selectedRow == null) {
					alert("사원을 선택해 주세요.");
					return;
				}

				employeeIdInput.value = selectedRow.dataset.employeeId;

				employeeNameInput.value = selectedRow.dataset.name;

				modal.style.display = "none";
			});

			keywordInput.addEventListener("input", filterEmployees);

			departmentFilter.addEventListener("change", filterEmployees);

			statusFilter.addEventListener("change", filterEmployees);

			window.addEventListener("click", function(event) {

				if (event.target === modal) {
					modal.style.display = "none";
				}
			});

		});
	</script>

</body>
</html>