<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여항목 구성 통계</title>

<script
	src="https://cdn.jsdelivr.net/npm/chart.js@4.5.1/dist/chart.umd.min.js"></script>

<script
	src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>

<style>
body {
	font-family: Arial, sans-serif;
	margin: 30px;
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

.result-container {
	margin-top: 30px;
	overflow-x: auto;
}

.donut-container {
	display: flex;
	gap: 20px;
	margin-top: 30px;
	margin-bottom: 10px;
}

.donut-box {
	flex: 1;
	position: relative;
	height: 400px;
	min-width: 260px;
}

.composition-table {
	border-collapse: collapse;
	white-space: nowrap;
	font-size: 13px;
	border-top: 2px solid #4a80c0;
}

.composition-table th, .composition-table td {
	border: 1px solid #dde3ea;
	padding: 8px 12px;
	text-align: right;
	color: #33639c;
}

/* 왼쪽 구분 열 - 지급항목 / 공제항목 */
.composition-table .row-head {
	background-color: #eef2f8;
	text-align: left;
	font-weight: bold;
	color: #333;
}

/* 왼쪽 구분 열 - 금액 / 구성비율 */
.composition-table .sub-head {
	background-color: #fff;
	text-align: left;
	font-weight: normal;
	color: #333;
}

/* 급여항목명 헤더 */
.composition-table .item-head, .composition-table .total-head {
	background-color: #eef2f8;
	text-align: center;
	font-weight: bold;
	color: #333;
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

	<jsp:useBean id="today" class="java.util.Date" />

	<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

	<fmt:formatDate value="${today}" pattern="MM" var="currentMonth" />

	<%-- selectedWageMonth 는 'YYYY-MM' 형식이라 연/월로 잘라 드롭다운 선택값으로 쓴다 --%>
	<c:set var="selectedYearValue"
		value="${empty selectedWageMonth ? currentYear : fn:substring(selectedWageMonth, 0, 4)}" />

	<c:set var="selectedMonthValue"
		value="${empty selectedWageMonth ? currentMonth : fn:substring(selectedWageMonth, 5, 7)}" />

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">

		<div class="search-row">

			<label for="wageYear"><span class="required-mark">*</span>
				귀속년월을 선택해 주세요.</label> <select id="wageYear">

				<c:forEach begin="0" end="9" var="offset">

					<c:set var="yearOption" value="${currentYear - 9 + offset}" />

					<option value="${yearOption}"
						<c:if test="${yearOption == selectedYearValue}">selected</c:if>>${yearOption}
						년</option>

				</c:forEach>

			</select> <select id="wageMonthValue">

				<c:forEach begin="1" end="12" var="month">

					<fmt:formatNumber value="${month}" pattern="00" var="monthOption" />

					<option value="${monthOption}"
						<c:if test="${monthOption == selectedMonthValue}">selected</c:if>>${monthOption}
						월</option>

				</c:forEach>

			</select>

			<%-- Handler 가 받는 파라미터는 그대로 wageMonth 이므로 제출 직전에 연-월을 합쳐 넣는다 --%>
			<input type="hidden" id="wageMonth" name="wageMonth"
				value="${selectedYearValue}-${selectedMonthValue}"> <label
				for="selectedEmployeeName">대상자를 선택해 주세요.</label> <input
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


	<!-- 급여항목 구성 통계 -->
	<c:if test="${not empty itemCompositionStatistics}">

		<div class="result-container">


			<c:choose>

				<c:when test="${itemCompositionStatistics.hasData}">

					<div class="donut-container">

						<div class="donut-box">
							<canvas id="summaryDonut"></canvas>
						</div>

						<div class="donut-box">
							<canvas id="paymentDonut"></canvas>
						</div>

						<div class="donut-box">
							<canvas id="deductionDonut"></canvas>
						</div>

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

			// 연/월 드롭다운 값을 Handler 가 받는 wageMonth(YYYY-MM) 형식으로 합쳐 넣는다
			const wageYearSelect = document.getElementById("wageYear");

			const wageMonthSelect = document.getElementById("wageMonthValue");

			const wageMonthInput = document.getElementById("wageMonth");

			function syncWageMonth() {

				if (wageYearSelect && wageMonthSelect && wageMonthInput) {

					wageMonthInput.value = wageYearSelect.value + "-"
							+ wageMonthSelect.value;
				}
			}

			if (wageYearSelect) {
				wageYearSelect.addEventListener("change", syncWageMonth);
			}

			if (wageMonthSelect) {
				wageMonthSelect.addEventListener("change", syncWageMonth);
			}

			syncWageMonth();

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
		const totalPayment = ${empty itemCompositionStatistics ? 0 : itemCompositionStatistics.totalPayment};

		const totalDeduction = ${empty itemCompositionStatistics ? 0 : itemCompositionStatistics.totalDeduction};

		const paymentItems = [
			<c:forEach var="item" items="${itemCompositionStatistics.paymentItems}"
				varStatus="status">

				{
					name : "${item.wageTypeName}",
					amount : ${item.amount},
					rate : ${item.compositionRate}
				}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const deductionItems = [
			<c:forEach var="item" items="${itemCompositionStatistics.deductionItems}"
				varStatus="status">

				{
					name : "${item.wageTypeName}",
					amount : ${item.amount},
					rate : ${item.compositionRate}
				}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const axisColor = "#5b7096";

		// 짙은 색에서 옅은 색 순서. 항목 10개 + '기타' 까지 커버한다.
		const bluePalette = [ "#1F4E79", "#215E92", "#246EAB", "#2A80C0",
				"#3592D2", "#4BA3DD", "#69B5E5", "#8AC7EC", "#ACD8F3",
				"#CBE7F8", "#E3F2FC" ];

		const orangePalette = [ "#C74A0B", "#DC5A0E", "#EA6C18", "#F27F2C",
				"#F79245", "#F9A461", "#FBB681", "#FCC8A2", "#FDD9C1",
				"#FEE7DA", "#FEF2EB" ];

		const MAX_DONUT_ITEMS = 10;

		// 조각이 이 비율보다 얇으면 퍼센트 라벨을 그리지 않는다. 참고 화면도 얇은 조각에는 라벨이 없다.
		const MIN_LABEL_RATIO = 0.05;

		function formatRate(rate) {
			return Number(rate).toFixed(1) + "%";
		}

		/*
		 * 항목이 10개를 넘으면 금액 내림차순 상위 10개만 남기고 나머지를 '기타'로 합친다.
		 * 차트에만 적용하며 아래 표는 실제 항목을 전부 그대로 보여준다.
		 */
		function groupDonutItems(items) {

			if (items.length <= MAX_DONUT_ITEMS) {
				return items;
			}

			const sorted = items.slice().sort(function(a, b) {
				return b.amount - a.amount;
			});

			const kept = sorted.slice(0, MAX_DONUT_ITEMS);

			const rest = sorted.slice(MAX_DONUT_ITEMS);

			kept.push({
				name : "기타",

				amount : rest.reduce(function(sum, item) {
					return sum + item.amount;
				}, 0),

				rate : rest.reduce(function(sum, item) {
					return sum + item.rate;
				}, 0)
			});

			return kept;
		}

		// 도넛 가운데 제목을 그리는 플러그인
		const centerTextPlugin = {

			id : "centerText",

			afterDatasetsDraw : function(chart) {

				const config = chart.options.plugins.centerText;

				if (!config || !config.text) {
					return;
				}

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

			if (!canvas) {
				return;
			}

			const items = groupDonutItems(sourceItems);

			// 총액이 0이면 비율 계산에서 NaN 이 나오므로 라벨 판정에 그대로 쓰지 않는다.
			const total = items.reduce(function(sum, item) {
				return sum + Number(item.amount);
			}, 0);

			new Chart(canvas, {

				type : "doughnut",

				data : {

					labels : items.map(function(item) {
						return item.name;
					}),

					datasets : [ {

						data : items.map(function(item) {
							return Number(item.amount);
						}),

						backgroundColor : items.map(function(item, index) {
							return palette[index % palette.length];
						}),

						borderColor : "#ffffff",

						borderWidth : 2
					} ]
				},

				plugins : [ ChartDataLabels, centerTextPlugin ],

				options : {

					responsive : true,

					maintainAspectRatio : false,

					cutout : "58%",

					layout : {
						padding : {
							top : 10,
							bottom : 10
						}
					},

					plugins : {

						centerText : {
							text : centerText
						},

						legend : {
							position : "bottom",

							labels : {
								boxWidth : 12,
								boxHeight : 12,
								color : axisColor,
								padding : 8,

								font : {
									size : 11
								}
							}
						},

						tooltip : {

							callbacks : {

								label : function(context) {

									return context.label + "  "
											+ formatRate(items[context.dataIndex].rate);
								}
							}
						},

						datalabels : {

							color : "#ffffff",

							font : {
								size : 12,
								weight : "bold"
							},

							display : function(context) {

								if (total <= 0) {
									return false;
								}

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

		const summaryItems = [ {
			name : "지급항목",
			amount : totalPayment,
			rate : summaryTotal > 0 ? (totalPayment * 100) / summaryTotal : 0
		}, {
			name : "공제항목",
			amount : totalDeduction,
			rate : summaryTotal > 0 ? (totalDeduction * 100) / summaryTotal : 0
		} ];

		createDonutChart("summaryDonut", "지급항목\n+\n공제항목", summaryItems, [
				"#1CA9E8", "#F5900C" ]);

		createDonutChart("paymentDonut", "지급\n세부항목", paymentItems, bluePalette);

		createDonutChart("deductionDonut", "공제\n세부항목", deductionItems,
				orangePalette);
	</script>

</body>
</html>