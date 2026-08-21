<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>연도별 개인연봉 통계</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<script
	src="https://cdn.jsdelivr.net/npm/chart.js@4.5.1/dist/chart.umd.min.js"></script>

<script
	src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>

<style>
body {
	font-family: Arial, sans-serif;
    margin: 0;
}

.description {
	margin-bottom: 20px;
}

.search-form {
	background-color: #f7f8fa;
	border: 1px solid #e3e8ef;
	padding: 14px 20px;
}

.search-row {
	display: flex;
	align-items: center;
	gap: 10px;
}

label {
	color: #333;
}

.required-mark {
	color: #d9534f;
}

input, button, select {
	padding: 6px 10px;
}

select, .employee-name {
	border: 1px solid #b9c2cf;
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

.chart-container {
	position: relative;
	width: 100%;
	height: 430px;
	margin-top: 30px;
}

.result-container {
	margin-top: 30px;
	overflow-x: auto;
}

.result-table {
	border-collapse: collapse;
	table-layout: fixed;
	width: 100%;
	white-space: nowrap;
	font-size: 13px;
	border-top: 2px solid #4a80c0;
}

.result-table .col-title {
	width: 120px;
}

.result-table th, .result-table td {
	border: 1px solid #dde3ea;
	padding: 8px 12px;
	text-align: right;
	color: #33639c;
}

.result-table th {
	background-color: #eef2f8;
	text-align: center;
	color: #333;
}

.result-table .row-title {
	text-align: left;
	background-color: #f5f7fa;
	color: #333;
	white-space: normal;
	word-break: keep-all;
}

.result-table .growth-title {
	text-align: left;
	padding-left: 25px;
	background-color: #f5f7fa;
	color: #333;
	white-space: normal;
	word-break: keep-all;
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

.positive {
	color: red;
}

.negative {
	color: blue;
}

.neutral {
	color: #333;
}
</style>
</head>
<body>
    
    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<h1>연도별 개인연봉 통계</h1>

	<p class="description">귀속년도와 사원을 선택하면 해당 사원의 최근 10개년 연봉현황을 확인할 수
		있습니다.</p>

	<jsp:useBean id="today" class="java.util.Date" />

	<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/yearlyPersonalStatistics.do">

		<div class="search-row">

			<label for="year"><span class="required-mark">*</span> 귀속년도를
				선택해 주세요.</label> <select id="year" name="year">

				<c:forEach begin="0" end="9" var="offset">

					<c:set var="yearOption" value="${currentYear - 9 + offset}" />

					<option value="${yearOption}"
						<c:if test="${yearOption == selectedYear}">selected</c:if>>${yearOption}
						년</option>

				</c:forEach>

			</select> <label for="selectedEmployeeName">대상자를 선택해 주세요.</label> <input
				type="hidden" id="employeeId" name="employeeId"
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

	<c:if test="${not empty yearlyPersonalStatistics}">

		<div class="chart-container">
			<canvas id="yearlyPersonalChart"></canvas>
		</div>

		<div class="result-container">

			<table class="result-table">

				<colgroup>

					<col class="col-title" />

					<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">
						<col />
					</c:forEach>

				</colgroup>

				<thead>
					<tr>

						<th>구분</th>

						<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">

							<th><c:out value="${row.year}" />년</th>

						</c:forEach>

					</tr>
				</thead>


				<tbody>

					<tr>

						<td class="row-title">연봉액 (천원)</td>

						<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">

							<td><c:choose>

									<c:when test="${not row.hasData}">
									-
								</c:when>

									<c:otherwise>
										<fmt:formatNumber value="${row.annualSalary / 1000}"
											pattern="#,##0" />
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

					</tr>


					<tr>

						<td class="growth-title">└ 증감률</td>

						<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">

							<td><c:choose>

									<c:when test="${row.salaryGrowthRate == null}"></c:when>

									<c:when test="${row.salaryGrowthRate > 0}">
										<span class="positive"> <fmt:formatNumber
												value="${row.salaryGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:when test="${row.salaryGrowthRate < 0}">
										<span class="negative"> <fmt:formatNumber
												value="${row.salaryGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:otherwise>
										<span class="neutral"> <fmt:formatNumber
												value="${row.salaryGrowthRate}" pattern="0.0" />%
										</span>
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

					</tr>


					<tr>

						<td class="row-title">공제금액 (천원)</td>

						<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">

							<td><c:choose>

									<c:when test="${not row.hasData}">
									-
								</c:when>

									<c:otherwise>
										<fmt:formatNumber value="${row.totalDeduction / 1000}"
											pattern="#,##0" />
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

					</tr>


					<tr>

						<td class="row-title">실지급액 (천원)</td>

						<c:forEach var="row" items="${yearlyPersonalStatistics.rows}">

							<td><c:choose>

									<c:when test="${not row.hasData}">
									-
								</c:when>

									<c:otherwise>
										<fmt:formatNumber value="${row.netPayment / 1000}"
											pattern="#,##0" />
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

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

	<script>
		const yearLabels = [
			<c:forEach var="row" items="${yearlyPersonalStatistics.rows}"
				varStatus="status">

				"${row.year}년"

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		// 데이터가 없는 연도는 0이 아니라 null 이다. 막대를 그리지 않기 위함이다.
		const deductionData = [
			<c:forEach var="row" items="${yearlyPersonalStatistics.rows}"
				varStatus="status">

				<c:choose>
					<c:when test="${row.hasData}">${row.totalDeduction / 1000}</c:when>
					<c:otherwise>null</c:otherwise>
				</c:choose>

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const netPaymentData = [
			<c:forEach var="row" items="${yearlyPersonalStatistics.rows}"
				varStatus="status">

				<c:choose>
					<c:when test="${row.hasData}">${row.netPayment / 1000}</c:when>
					<c:otherwise>null</c:otherwise>
				</c:choose>

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		// 연봉액은 dataset 이 아니라 Tooltip 표시용으로만 쓴다.
		const annualSalaryData = [
			<c:forEach var="row" items="${yearlyPersonalStatistics.rows}"
				varStatus="status">

				<c:choose>
					<c:when test="${row.hasData}">${row.annualSalary / 1000}</c:when>
					<c:otherwise>null</c:otherwise>
				</c:choose>

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const axisColor = "#5b7096";

		// 차트 데이터는 원본 그대로 두고 화면에 찍을 때만 정수 천원으로 반올림한다.
		function formatThousandWon(value) {

			return Math.round(Number(value)).toLocaleString("ko-KR");
		}

		const personalChartCanvas = document
				.getElementById("yearlyPersonalChart");

		if (personalChartCanvas) {

			new Chart(personalChartCanvas, {

				type : "bar",

				data : {

					labels : yearLabels,

					datasets : [

							{
								label : "공제금액 (천원)",

								data : deductionData,

								stack : "salary",

								backgroundColor : "#F4A582",

								borderColor : "#F4A582",

								borderWidth : 1,

								barPercentage : 0.92,

								categoryPercentage : 0.9,

								datalabels : {
									color : "#333333",
									anchor : "center",
									align : "center",

									font : {
										size : 12
									},

									display : function(context) {

										const value = context.dataset.data[context.dataIndex];

										return value !== null && Number(value) > 0;
									},

									formatter : function(value) {
										return formatThousandWon(value);
									}
								}
							},

							{
								label : "실지급액 (천원)",

								data : netPaymentData,

								stack : "salary",

								backgroundColor : "#9FA8DA",

								borderColor : "#9FA8DA",

								borderWidth : 1,

								barPercentage : 0.92,

								categoryPercentage : 0.9,

								datalabels : {
									color : "#333333",
									anchor : "center",
									align : "center",

									font : {
										size : 12
									},

									display : function(context) {

										const value = context.dataset.data[context.dataIndex];

										return value !== null && Number(value) > 0;
									},

									formatter : function(value) {
										return formatThousandWon(value);
									}
								}
							} ]
				},

				plugins : [ ChartDataLabels ],

				options : {

					responsive : true,

					maintainAspectRatio : false,

					layout : {
						padding : {
							top : 24
						}
					},

					interaction : {
						mode : "index",
						intersect : false
					},

					plugins : {

						legend : {
							position : "bottom",

							labels : {
								boxWidth : 12,
								boxHeight : 12,
								color : axisColor
							}
						},

						tooltip : {

							mode : "index",

							intersect : false,

							callbacks : {

								title : function(items) {

									if (items.length === 0) {
										return "";
									}

									return items[0].label;
								},

								beforeBody : function(items) {

									if (items.length === 0) {
										return "";
									}

									const salary = annualSalaryData[items[0].dataIndex];

									if (salary === null || salary === undefined) {
										return "";
									}

									return "연봉액 (천원)  " + formatThousandWon(salary);
								},

								label : function(context) {

									return context.dataset.label + "  "
											+ formatThousandWon(context.parsed.y);
								}
							}
						}
					},

					scales : {

						x : {

							stacked : true,

							grid : {
								display : false
							},

							ticks : {
								color : axisColor
							}
						},

						y : {

							type : "linear",

							position : "left",

							stacked : true,

							beginAtZero : true,

							grid : {
								display : false
							},

							title : {
								display : true,
								text : "연봉액 (천원)",
								color : axisColor
							},

							ticks : {

								color : axisColor,

								callback : function(value) {
									return Number(value).toLocaleString();
								}
							}
						}
					}
				}
			});
		}
	</script>

</body>
</html>