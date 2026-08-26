<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与項目構成統計</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<script
	src="https://cdn.jsdelivr.net/npm/chart.js@4.5.1/dist/chart.umd.min.js"></script>

<script
	src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>

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
	margin-bottom: 10px;
}

.page-header h1 {
	font-size: 22px;
	font-weight: bold;
	margin: 0;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.page-desc {
	font-size: 14px;
	color: #666;
	margin: 0 0 20px 0;
}

/* 3. 상단 검색 폼 영역 */
.filter-bar {
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

.required-mark {
	color: #e74a3b;
	margin-right: 3px;
}

.filter-bar select, .filter-bar input[type="text"] {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

.filter-bar select { width: 120px; }

.employee-name {
	width: 180px;
	background-color: #fff;
	font-weight: bold;
	color: #4e73df;
}

.btn-search, .btn-select {
	padding: 6px 16px;
	border: none;
	border-radius: 3px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	color: white;
	outline: none;
}

.btn-search { background-color: #4e73df; }
.btn-search:hover { background-color: #2e59d9; }

.btn-select { background-color: #a5a5a5; }
.btn-select:hover { background-color: #858796; }

.error-message {
	margin-top: 10px;
	color: #e74a3b;
	font-weight: bold;
	font-size: 14px;
}

/* 4. 차트 및 테이블 컨테이너 */
.result-container {
	margin-top: 10px;
	overflow-x: auto;
}

.donut-container {
	display: flex;
	gap: 20px;
	margin-top: 20px;
	margin-bottom: 30px;
}

.donut-box {
	flex: 1;
	position: relative;
	height: 380px;
	min-width: 260px;
	background: #fff;
	border: 1px solid #ddd;
	border-radius: 5px;
	padding: 15px;
	box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

/* 5. 구성비율 데이터 테이블 스타일 */
table.composition-table {
	border-collapse: collapse;
	white-space: nowrap;
	font-size: 14px;
	width: 100%;
	text-align: center;
}

table.composition-table th, table.composition-table td {
	border: 1px solid #ccc;
	padding: 10px 12px;
}

/* 행(가로) 헤더: 지급항목, ┗ 금액 등 */
table.composition-table .row-head {
	background-color: #f8f9fa;
	text-align: left;
	font-weight: bold;
	color: #333;
}

table.composition-table .sub-head {
	background-color: #fdfdfd;
	text-align: left;
	color: #555;
}

/* 열(세로) 헤더: 세부 수당명 등 */
table.composition-table .item-head, table.composition-table .total-head {
	background-color: #f8f9fa;
	font-weight: bold;
	color: #333;
}

/* 합계(Total) 및 차인지급액 열 */
table.composition-table .total {
	font-weight: bold;
	background-color: #f4f4f4;
}

table.composition-table .net-head {
	background-color: #4e73df;
	color: #fff;
	font-weight: bold;
}

table.composition-table .net-amount {
	font-weight: bold;
	color: #4e73df;
	background-color: #eef2f8;
}

/* 데이터 셀 숫자 우측 정렬 */
table.composition-table .item-cell, table.composition-table .total, table.composition-table .net-amount {
	text-align: right;
}

/* 테두리 비우기 */
table.composition-table .outside, table.composition-table .net-blank {
	border: none;
	background-color: transparent;
}

.no-data {
	padding: 30px;
	border: 1px solid #ccc;
	text-align: center;
	color: #777;
	font-size: 14px;
	background-color: #f8f9fa;
	border-radius: 3px;
}

/* 6. 모달 팝업 스타일 */
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
	cursor: pointer;
}
.modal-header button:hover { color: #333; }

.employee-filter {
	display: flex;
	gap: 10px;
	margin-bottom: 15px;
}

.employee-filter input[type="text"], .employee-filter select {
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
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">

			<div class="page-header">
				<h1>給与項目構成統計</h1>
			</div>
			<p class="page-desc">帰属年月と社員を選択すると、該当社員の給与項目構成表を確認できます。</p>

			<jsp:useBean id="today" class="java.util.Date" />
			<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />
			<fmt:formatDate value="${today}" pattern="MM" var="currentMonth" />

			<c:set var="selectedYearValue" value="${empty selectedWageMonth ? currentYear : fn:substring(selectedWageMonth, 0, 4)}" />
			<c:set var="selectedMonthValue" value="${empty selectedWageMonth ? currentMonth : fn:substring(selectedWageMonth, 5, 7)}" />

			<form class="filter-bar" method="get" action="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">
				<div class="search-row">
					<label for="wageYear"><span class="required-mark">*</span>帰属年月</label> 
					<select id="wageYear">
						<c:forEach begin="0" end="9" var="offset">
							<c:set var="yearOption" value="${currentYear - 9 + offset}" />
							<option value="${yearOption}" <c:if test="${yearOption == selectedYearValue}">selected</c:if>>
								${yearOption}年
							</option>
						</c:forEach>
					</select> 
					
					<select id="wageMonthValue">
						<c:forEach begin="1" end="12" var="month">
							<fmt:formatNumber value="${month}" pattern="00" var="monthOption" />
							<option value="${monthOption}" <c:if test="${monthOption == selectedMonthValue}">selected</c:if>>
								${monthOption}月
							</option>
						</c:forEach>
					</select>

					<input type="hidden" id="wageMonth" name="wageMonth" value="${selectedYearValue}-${selectedMonthValue}"> 
					
					<label for="selectedEmployeeName" style="margin-left: 20px;"><span class="required-mark">*</span>対象社員</label> 
					<input type="hidden" id="employeeId" name="employeeId" value="<c:out value='${selectedEmployeeId}' />"> 
					<input type="text" id="selectedEmployeeName" class="employee-name" value="<c:out value='${selectedEmployeeName}' />" placeholder="対象社員を選択" readonly>

					<button type="button" id="openEmployeeModal" class="btn-select">社員検索</button>
					<button type="submit" name="search" value="true" class="btn-search" style="margin-left: 10px;">照会</button>
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
						<h2>給与統計対象社員の選択</h2>
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

			<!-- 급여항목 구성 통계 -->
			<c:if test="${not empty itemCompositionStatistics}">
				<div class="result-container">
					<c:choose>
						<c:when test="${itemCompositionStatistics.hasData}">
							
							<div class="donut-container">
								<div class="donut-box"><canvas id="summaryDonut"></canvas></div>
								<div class="donut-box"><canvas id="paymentDonut"></canvas></div>
								<div class="donut-box"><canvas id="deductionDonut"></canvas></div>
							</div>

							<c:set var="paymentCount" value="${fn:length(itemCompositionStatistics.tablePaymentItems)}" />
							<c:set var="deductionCount" value="${fn:length(itemCompositionStatistics.tableDeductionItems)}" />
							<c:set var="minItemColumnCount" value="${10}" />
							<c:set var="itemColumnCount" value="${paymentCount > deductionCount ? paymentCount : deductionCount}" />
							<c:set var="itemColumnCount" value="${itemColumnCount < minItemColumnCount ? minItemColumnCount : itemColumnCount}" />

							<table class="composition-table">
								<tbody>
									<!-- 지급항목 : 항목명 -->
									<tr>
										<th class="row-head">支給項目</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tablePaymentItems}">
											<th class="item-head"><c:out value="${row.wageTypeName}" /></th>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
											<th class="item-head"></th>
										</c:forEach>
										<th class="total-head">合計</th>
										<td class="outside"></td>
									</tr>

									<!-- 지급항목 : 금액 -->
									<tr>
										<th class="sub-head">┗ 金額（ウォン）</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tablePaymentItems}">
											<td class="item-cell"><fmt:formatNumber value="${row.amount}" pattern="#,##0" /></td>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
											<td class="item-cell"></td>
										</c:forEach>
										<td class="total"><fmt:formatNumber value="${itemCompositionStatistics.totalPayment}" pattern="#,##0" /></td>
										<td class="outside"></td>
									</tr>

									<!-- 지급항목 : 구성비율 -->
									<tr>
										<th class="sub-head">┗ 構成比</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tablePaymentItems}">
											<td class="item-cell"><fmt:formatNumber value="${row.compositionRate}" pattern="0.0" />%</td>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - paymentCount}">
											<td class="item-cell"></td>
										</c:forEach>
										<td class="total">
											<c:choose>
												<c:when test="${itemCompositionStatistics.totalPayment == 0}">0.0%</c:when>
												<c:otherwise>100.0%</c:otherwise>
											</c:choose>
										</td>
										<td class="outside"></td>
									</tr>

									<!-- 공제항목 : 항목명 -->
									<tr>
										<th class="row-head">控除項目</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tableDeductionItems}">
											<th class="item-head"><c:out value="${row.wageTypeName}" /></th>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
											<th class="item-head"></th>
										</c:forEach>
										<th class="total-head">合計</th>
										<th class="net-head">差引支給額</th>
									</tr>

									<!-- 공제항목 : 금액 -->
									<tr>
										<th class="sub-head">┗ 金額（ウォン）</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tableDeductionItems}">
											<td class="item-cell"><fmt:formatNumber value="${row.amount}" pattern="#,##0" /></td>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
											<td class="item-cell"></td>
										</c:forEach>
										<td class="total"><fmt:formatNumber value="${itemCompositionStatistics.totalDeduction}" pattern="#,##0" /></td>
										<td class="net-amount"><fmt:formatNumber value="${itemCompositionStatistics.netPayment}" pattern="#,##0" /></td>
									</tr>

									<!-- 공제항목 : 구성비율 -->
									<tr>
										<th class="sub-head">┗ 構成比</th>
										<c:forEach var="row" items="${itemCompositionStatistics.tableDeductionItems}">
											<td class="item-cell"><fmt:formatNumber value="${row.compositionRate}" pattern="0.0" />%</td>
										</c:forEach>
										<c:forEach begin="1" end="${itemColumnCount - deductionCount}">
											<td class="item-cell"></td>
										</c:forEach>
										<td class="total">
											<c:choose>
												<c:when test="${itemCompositionStatistics.totalDeduction == 0}">0.0%</c:when>
												<c:otherwise>100.0%</c:otherwise>
											</c:choose>
										</td>
										<td class="net-blank"></td>
									</tr>
								</tbody>
							</table>

						</c:when>
						<c:otherwise>
							<div class="no-data">照会された給与データがありません。</div>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

		</div>
	</div>

	<!-- 스크립트: 필터 동기화 및 모달 제어 -->
	<script>
		document.addEventListener("DOMContentLoaded", function() {
			const wageYearSelect = document.getElementById("wageYear");
			const wageMonthSelect = document.getElementById("wageMonthValue");
			const wageMonthInput = document.getElementById("wageMonth");

			function syncWageMonth() {
				if (wageYearSelect && wageMonthSelect && wageMonthInput) {
					wageMonthInput.value = wageYearSelect.value + "-" + wageMonthSelect.value;
				}
			}

			if (wageYearSelect) wageYearSelect.addEventListener("change", syncWageMonth);
			if (wageMonthSelect) wageMonthSelect.addEventListener("change", syncWageMonth);
			syncWageMonth();

			const modal = document.getElementById("employeeModal");
			const openButton = document.getElementById("openEmployeeModal");
			const closeButton = document.getElementById("closeEmployeeModal");
			const cancelButton = document.getElementById("cancelEmployeeButton");
			const selectButton = document.getElementById("selectEmployeeButton");
			const employeeIdInput = document.getElementById("employeeId");
			const employeeNameInput = document.getElementById("selectedEmployeeName");
			const keywordInput = document.getElementById("employeeKeyword");
			const departmentFilter = document.getElementById("departmentFilter");
			const statusFilter = document.getElementById("statusFilter");
			const employeeRows = Array.from(document.querySelectorAll(".employee-row"));
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

				employeeRows.forEach(function(row) {
					const name = row.dataset.name.toLowerCase();
					const rowDepartment = row.dataset.department;
					const rowStatus = row.dataset.status;

					const keywordMatched = keyword === "" || name.includes(keyword);
					const departmentMatched = department === "" || rowDepartment === department;
					const statusMatched = status === "" || rowStatus === status;

					row.style.display = keywordMatched && departmentMatched && statusMatched ? "" : "none";
				});
			}

			openButton.addEventListener("click", function() { modal.style.display = "block"; });
			closeButton.addEventListener("click", function() { modal.style.display = "none"; });
			cancelButton.addEventListener("click", function() { modal.style.display = "none"; });

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
					alert("社員を選択してください。");
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

	<!-- 스크립트: 차트 렌더링 -->
	<script>
		const totalPayment = ${empty itemCompositionStatistics ? 0 : itemCompositionStatistics.totalPayment};
		const totalDeduction = ${empty itemCompositionStatistics ? 0 : itemCompositionStatistics.totalDeduction};

		const paymentItems = [
			<c:forEach var="item" items="${itemCompositionStatistics.paymentItems}" varStatus="status">
				{ name : "${item.wageTypeName}", amount : ${item.amount}, rate : ${item.compositionRate} }
				<c:if test="${!status.last}">,</c:if>
			</c:forEach>
		];

		const deductionItems = [
			<c:forEach var="item" items="${itemCompositionStatistics.deductionItems}" varStatus="status">
				{ name : "${item.wageTypeName}", amount : ${item.amount}, rate : ${item.compositionRate} }
				<c:if test="${!status.last}">,</c:if>
			</c:forEach>
		];

		const axisColor = "#666";

		/* 차트 색상: 메인 시스템 톤으로 교체 */
		const bluePalette = [ "#4e73df", "#3b5998", "#2e59d9", "#5bc0de", "#69b3e7", "#8bb4e7", "#a0c4e8" ];
		const orangePalette = [ "#e74a3b", "#d62d20", "#f6c23e", "#f4b183", "#f8d2b0", "#f9dfcc", "#fbece2" ];
		
		const MIN_LABEL_RATIO = 0.05;

		function formatRate(rate) {
			return Number(rate).toFixed(1) + "%";
		}

		const centerTextPlugin = {
			id : "centerText",
			afterDatasetsDraw : function(chart) {
				const config = chart.options.plugins.centerText;
				if (!config || !config.text) return;
				
				const ctx = chart.ctx;
				const area = chart.chartArea;
				const centerX = (area.left + area.right) / 2;
				const centerY = (area.top + area.bottom) / 2;
				
				const lines = config.text.split("\n");
				const lineHeight = 21;
				const startY = centerY - ((lines.length - 1) * lineHeight) / 2;

				ctx.save();
				ctx.fillStyle = "#333333";
				ctx.font = "bold 15px Arial";
				ctx.textAlign = "center";
				ctx.textBaseline = "middle";

				lines.forEach(function(line, index) {
					ctx.fillText(line, centerX, startY + index * lineHeight);
				});
				ctx.restore();
			}
		};

		function createDonutChart(canvasId, centerText, sourceItems, palette) {
			const canvas = document.getElementById(canvasId);
			if (!canvas) return;

			const items = sourceItems;
			const total = items.reduce(function(sum, item) {
				return sum + Number(item.amount);
			}, 0);

			new Chart(canvas, {
				type : "doughnut",
				data : {
					labels : items.map(function(item) { return item.name; }),
					datasets : [ {
						data : items.map(function(item) { return Number(item.amount); }),
						backgroundColor : items.map(function(item, index) { return palette[index % palette.length]; }),
						borderColor : "#ffffff",
						borderWidth : 2
					} ]
				},
				plugins : [ ChartDataLabels, centerTextPlugin ],
				options : {
					responsive : true,
					maintainAspectRatio : false,
					cutout : "60%",
					layout : { padding : { top : 10, bottom : 10 } },
					plugins : {
						centerText : { text : centerText },
						legend : {
							position : "bottom",
							labels : { boxWidth : 12, boxHeight : 12, color : axisColor, padding : 10, font : { size : 12 } }
						},
						tooltip : {
							callbacks : {
								label : function(context) {
									return context.label + " : " + formatRate(items[context.dataIndex].rate);
								}
							}
						},
						datalabels : {
							color : "#ffffff",
							font : { size : 12, weight : "bold" },
							display : function(context) {
								if (total <= 0) return false;
								const value = Number(context.dataset.data[context.dataIndex]);
								return value > 0 && value / total >= MIN_LABEL_RATIO;
							},
							formatter : function(value, context) {
								return formatRate(items[context.dataIndex].rate);
							}
						}
					}
				}
			});
		}

		const summaryTotal = totalPayment + totalDeduction;
		const summaryItems = [ 
			{ name : "支給項目", amount : totalPayment, rate : summaryTotal > 0 ? (totalPayment * 100) / summaryTotal : 0 }, 
			{ name : "控除項目", amount : totalDeduction, rate : summaryTotal > 0 ? (totalDeduction * 100) / summaryTotal : 0 } 
		];

		/* 요약 도넛 차트도 메인 파랑(#4e73df)과 붉은색(#e74a3b) 사용 */
		createDonutChart("summaryDonut", "支給項目\n+\n控除項目", summaryItems, [ "#4e73df", "#e74a3b" ]);
		createDonutChart("paymentDonut", "支給\n詳細項目", paymentItems, bluePalette);
		createDonutChart("deductionDonut", "控除\n詳細項目", deductionItems, orangePalette);
	</script>

</body>
</html>