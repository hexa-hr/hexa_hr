<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여항목 구성 통계</title>

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

.summary-table-container {
	margin-bottom: 30px;
}

.summary-table {
	width: 100%;
	border-collapse: collapse;
}

.summary-table th, .summary-table td {
	border: 1px solid #aaa;
	padding: 12px;
	text-align: center;
}

.summary-table th {
	background-color: #f2f2f2;
}

.summary-table td {
	font-weight: bold;
}

.composition-table {
	border-collapse: collapse;
	white-space: nowrap;
}

.composition-table th, .composition-table td {
	border: 1px solid #aaa;
	padding: 9px 12px;
	text-align: right;
}

/* 왼쪽 구분 열 - 지급항목 / 공제항목 */
.composition-table .row-head {
	background-color: #eef2f8;
	text-align: left;
	font-weight: bold;
}

/* 왼쪽 구분 열 - 금액 / 구성비율 */
.composition-table .sub-head {
	background-color: #fff;
	text-align: left;
	font-weight: normal;
}

/* 급여항목명 헤더 */
.composition-table .item-head, .composition-table .total-head {
	background-color: #eef2f8;
	text-align: center;
	font-weight: bold;
}

.composition-table .total {
	font-weight: bold;
}

/*
 * 값이 없는 칸도 폭이 줄어들지 않도록 최소 폭 지정.
 * 좌우 padding 12px씩은 별도로 붙으므로 실제 칸 폭은 여기 값 + 24px.
 */
.composition-table .row-head, .composition-table .sub-head {
	min-width: 100px;
}

.composition-table .item-head, .composition-table .item-cell {
	min-width: 80px;
}

.composition-table .total-head, .composition-table .total {
	min-width: 90px;
}

/* 실지급액 */
.composition-table .net-head {
	background-color: #4a80c0;
	color: #fff;
	text-align: center;
}

.composition-table .net-head, .composition-table .net-amount,
	.composition-table .net-blank {
	min-width: 90px;
}

.composition-table .net-amount {
	font-weight: bold;
}

/* 지급항목 행에는 실지급액 칸이 없으므로 테두리 없이 비움 */
.composition-table .outside {
	border: none;
	background-color: transparent;
}

.no-data {
	padding: 30px;
	border: 1px solid #ccc;
	text-align: center;
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

	<h1>급여항목 구성 통계</h1>

	<p class="description">귀속년월과 사원을 선택하면 해당 사원의 급여항목 구성표를 확인할 수 있습니다.
	</p>

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">

		<div class="search-row">

			<label for="wageMonth">귀속년월</label> <input type="month"
				id="wageMonth" name="wageMonth"
				value="<c:out value='${selectedWageMonth}' />" required> <label
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


	<!-- 급여항목 구성 통계 -->
	<c:if test="${not empty itemCompositionStatistics}">

		<div class="result-container">


			<c:choose>

				<c:when test="${itemCompositionStatistics.hasData}">

					<div class="summary-table-container">

						<table class="summary-table">

							<thead>
								<tr>
									<th>지급합계</th>
									<th>공제합계</th>
									<th>실지급액</th>
								</tr>
							</thead>

							<tbody>
								<tr>
									<td><fmt:formatNumber
											value="${itemCompositionStatistics.totalPayment}"
											pattern="#,##0" />원</td>

									<td><fmt:formatNumber
											value="${itemCompositionStatistics.totalDeduction}"
											pattern="#,##0" />원</td>

									<td><fmt:formatNumber
											value="${itemCompositionStatistics.netPayment}"
											pattern="#,##0" />원</td>
								</tr>
							</tbody>

						</table>

					</div>

					<c:set var="paymentCount"
						value="${fn:length(itemCompositionStatistics.paymentItems)}" />

					<c:set var="deductionCount"
						value="${fn:length(itemCompositionStatistics.deductionItems)}" />

					<%-- 급여항목 칸은 기본 10개를 유지하고, 항목이 더 많으면 그만큼 늘어남 --%>
					<c:set var="minItemColumnCount" value="${10}" />

					<c:set var="itemColumnCount"
						value="${paymentCount > deductionCount ? paymentCount : deductionCount}" />

					<c:set var="itemColumnCount"
						value="${itemColumnCount < minItemColumnCount ? minItemColumnCount : itemColumnCount}" />


					<table class="composition-table">

						<tbody>

							<!-- 지급항목 : 항목명 -->
							<tr>
								<th class="row-head">지급항목</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.paymentItems}">

									<th class="item-head"><c:out value="${row.wageTypeName}" /></th>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
									<th class="item-head"></th>
								</c:forEach>

								<th class="total-head">합계</th>

								<td class="outside"></td>
							</tr>

							<!-- 지급항목 : 금액 -->
							<tr>
								<th class="sub-head">┗ 금액 (원)</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.paymentItems}">

									<td class="item-cell"><fmt:formatNumber
											value="${row.amount}" pattern="#,##0" /></td>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
									<td class="item-cell"></td>
								</c:forEach>

								<td class="total"><fmt:formatNumber
										value="${itemCompositionStatistics.totalPayment}"
										pattern="#,##0" /></td>

								<td class="outside"></td>
							</tr>

							<!-- 지급항목 : 구성비율 -->
							<tr>
								<th class="sub-head">┗ 구성비율</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.paymentItems}">

									<td class="item-cell"><fmt:formatNumber
											value="${row.compositionRate}" pattern="0.0" />%</td>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
									<td class="item-cell"></td>
								</c:forEach>

								<td class="total"><c:choose>
										<c:when test="${itemCompositionStatistics.totalPayment == 0}">
											0.0%
										</c:when>

										<c:otherwise>
											100.0%
										</c:otherwise>
									</c:choose></td>

								<td class="outside"></td>
							</tr>


							<!-- 공제항목 : 항목명 -->
							<tr>
								<th class="row-head">공제항목</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.deductionItems}">

									<th class="item-head"><c:out value="${row.wageTypeName}" /></th>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
									<th class="item-head"></th>
								</c:forEach>

								<th class="total-head">합계</th>

								<th class="net-head">실지급액</th>
							</tr>

							<!-- 공제항목 : 금액 -->
							<tr>
								<th class="sub-head">┗ 금액 (원)</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.deductionItems}">

									<td class="item-cell"><fmt:formatNumber
											value="${row.amount}" pattern="#,##0" /></td>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
									<td class="item-cell"></td>
								</c:forEach>

								<td class="total"><fmt:formatNumber
										value="${itemCompositionStatistics.totalDeduction}"
										pattern="#,##0" /></td>

								<td class="net-amount"><fmt:formatNumber
										value="${itemCompositionStatistics.netPayment}"
										pattern="#,##0" /></td>
							</tr>

							<!-- 공제항목 : 구성비율 -->
							<tr>
								<th class="sub-head">┗ 구성비율</th>

								<c:forEach var="row"
									items="${itemCompositionStatistics.deductionItems}">

									<td class="item-cell"><fmt:formatNumber
											value="${row.compositionRate}" pattern="0.0" />%</td>

								</c:forEach>

								<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
									<td class="item-cell"></td>
								</c:forEach>

								<td class="total"><c:choose>
										<c:when
											test="${itemCompositionStatistics.totalDeduction == 0}">
											0.0%
										</c:when>

										<c:otherwise>
											100.0%
										</c:otherwise>
									</c:choose></td>

								<td class="net-blank"></td>
							</tr>

						</tbody>

					</table>

				</c:when>


				<c:otherwise>

					<div class="no-data">조회된 급여 데이터가 없습니다.</div>

				</c:otherwise>

			</c:choose>

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