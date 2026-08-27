<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>月別全体給与統計</title>

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

.filter-bar select {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
	width: 150px;
}

.error-message {
	margin-top: 10px;
	color: #e74a3b;
	font-weight: bold;
	font-size: 14px;
}

/* 4. 차트 영역 */
.chart-container {
	position: relative;
	width: 100%;
	height: 430px;
	margin-top: 20px;
	margin-bottom: 30px;
}

/* 5. 데이터 테이블 스타일 */
.table-container {
	width: 100%;
	overflow-x: auto;
}

table.data-table {
	border-collapse: collapse;
	table-layout: fixed;
	width: 100%;
	min-width: 1200px;
	white-space: nowrap;
	font-size: 14px;
	text-align: center;
	background: white;
	margin-bottom: 30px;
}

table.data-table th, table.data-table td {
	border: 1px solid #ccc;
	padding: 10px;
}

table.data-table th {
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
}

table.data-table td {
	text-align: right;
}

/* 첫 번째 열 (항목명) 강조 */
table.data-table td.row-title {
	text-align: left;
	background-color: #f8f9fa;
	font-weight: bold;
	color: #333;
	white-space: normal;
	word-break: keep-all;
}

/* 증감률 항목 들여쓰기 */
table.data-table td.growth-title {
	text-align: left;
	padding-left: 25px;
	background-color: #fdfdfd;
	color: #555;
	white-space: normal;
	word-break: keep-all;
}

table.data-table .col-title {
	width: 180px;
}

table.data-table tbody tr:hover td:not(.row-title):not(.growth-title) {
	background-color: #f1f5f9;
}

/* 합계(Total) 열 스타일 */
table.data-table th.total-column, 
table.data-table td.total-column {
	background-color: #f4f4f4;
	font-weight: bold;
	color: #4e73df;
}

/* 등락 색상 포인트 */
.text-red {
	color: #e74a3b;
	font-weight: bold;
}

.text-blue {
	color: #4e73df;
	font-weight: bold;
}

.text-neutral {
	color: #333;
}
</style>
</head>

<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">

			<div class="page-header">
				<h1>月別全体給与統計</h1>
			</div>
			<p class="page-desc">帰属年度を選択すると、月別の全体給与額と人数を確認できます。</p>

			<jsp:useBean id="today" class="java.util.Date" />
			<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

			<form class="filter-bar" method="get"
				action="${pageContext.request.contextPath}/wage/monthlyTotalStatistics.do">

				<div class="search-row">
					<label for="year"><span class="required-mark">*</span>帰属年度</label> 
					<select id="year" name="year" onchange="this.form.submit();">
						<c:forEach begin="0" end="9" var="offset">
							<c:set var="yearOption" value="${currentYear - 9 + offset}" />
							<option value="${yearOption}" <c:if test="${yearOption == selectedYear}">selected</c:if>>
								${yearOption}年
							</option>
						</c:forEach>
					</select>
				</div>

				<c:if test="${not empty errorMessage}">
					<div class="error-message">
						<c:out value="${errorMessage}" />
					</div>
				</c:if>

			</form>

			<c:if test="${not empty monthlyTotalStatistics}">

				<!-- 차트 영역 -->
				<div class="chart-container">
					<canvas id="monthlyTotalChart"></canvas>
				</div>

				<!-- 데이터 테이블 영역 -->
				<div class="table-container">
					<table class="data-table">
						<colgroup>
							<col class="col-title" />
							<c:forEach begin="1" end="13">
								<col />
							</c:forEach>
						</colgroup>

						<thead>
							<tr>
								<th>区分</th>
								<c:forEach var="row" items="${monthlyTotalStatistics.rows}" varStatus="status">
									<th>${status.count}月</th>
								</c:forEach>
								<th class="total-column">合計</th>
							</tr>
						</thead>

						<tbody>
							<tr>
								<td class="row-title">全体給与額（千ウォン）</td>
								<c:forEach var="row" items="${monthlyTotalStatistics.rows}">
									<td><fmt:formatNumber value="${row.totalPayment / 1000}" pattern="#,##0" /></td>
								</c:forEach>
								<td class="total-column">
									<fmt:formatNumber value="${monthlyTotalStatistics.totalPayment / 1000}" pattern="#,##0" />
								</td>
							</tr>

							<tr>
								<td class="growth-title">└ 増減率</td>
								<c:forEach var="row" items="${monthlyTotalStatistics.rows}">
									<td>
										<c:choose>
											<c:when test="${row.paymentGrowthRate == null}">-</c:when>
											<c:when test="${row.paymentGrowthRate > 0}">
												<span class="text-red"> <fmt:formatNumber value="${row.paymentGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:when test="${row.paymentGrowthRate < 0}">
												<span class="text-blue"> <fmt:formatNumber value="${row.paymentGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:otherwise>
												<span class="text-neutral"> <fmt:formatNumber value="${row.paymentGrowthRate}" pattern="0.0" />%</span>
											</c:otherwise>
										</c:choose>
									</td>
								</c:forEach>
								<td class="total-column">-</td>
							</tr>

							<tr>
								<td class="row-title">人数（名）</td>
								<c:forEach var="row" items="${monthlyTotalStatistics.rows}">
									<td><c:out value="${row.employeeCount}" /></td>
								</c:forEach>
								<td class="total-column">
									<fmt:formatNumber value="${monthlyTotalStatistics.averageEmployeeCount}" pattern="0.0" />
								</td>
							</tr>

							<tr>
								<td class="growth-title">└ 増減率</td>
								<c:forEach var="row" items="${monthlyTotalStatistics.rows}">
									<td>
										<c:choose>
											<c:when test="${row.employeeGrowthRate == null}">-</c:when>
											<c:when test="${row.employeeGrowthRate > 0}">
												<span class="text-red"> <fmt:formatNumber value="${row.employeeGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:when test="${row.employeeGrowthRate < 0}">
												<span class="text-blue"> <fmt:formatNumber value="${row.employeeGrowthRate}" pattern="0.0" />%</span>
											</c:when>
											<c:otherwise>
												<span class="text-neutral"> <fmt:formatNumber value="${row.employeeGrowthRate}" pattern="0.0" />%</span>
											</c:otherwise>
										</c:choose>
									</td>
								</c:forEach>
								<td class="total-column">-</td>
							</tr>
						</tbody>
					</table>
				</div>

			</c:if>

		</div>
	</div>

	<script>
		const monthlyPaymentData = [
			<c:forEach var="row" items="${monthlyTotalStatistics.rows}" varStatus="status">
				${row.totalPayment / 1000}<c:if test="${!status.last}">,</c:if>
			</c:forEach>
		];

		const monthlyEmployeeData = [
			<c:forEach var="row" items="${monthlyTotalStatistics.rows}" varStatus="status">
				${row.employeeCount}<c:if test="${!status.last}">,</c:if>
			</c:forEach>
		];

		const monthLabels = [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ];

		const axisColor = "#666666";

		const chartCanvas = document.getElementById("monthlyTotalChart");

		if (chartCanvas) {
			new Chart(chartCanvas, {
				data : {
					labels : monthLabels,
					datasets : [
							{
								type : "bar",
								label : "全体給与額（千ウォン）",
								data : monthlyPaymentData,
								yAxisID : "paymentAxis",
								backgroundColor : "rgba(78, 115, 223, 0.85)", /* 메인 파란색 톤으로 변경 */
								borderColor : "rgba(78, 115, 223, 1)",
								borderWidth : 1,
								barPercentage : 0.6,
								categoryPercentage : 0.8,
								order : 2,
								datalabels : {
									color : "#333",
									anchor : "end",
									align : "bottom",
									offset: -20,
									font : { size : 12, weight: 'bold' },
									formatter : function(value) {
										if (value == 0) return "";
										return Math.round(value).toLocaleString();
									}
								}
							},
							{
								type : "line",
								label : "人数（名）",
								data : monthlyEmployeeData,
								yAxisID : "employeeAxis",
								borderColor : "#e74a3b", /* 경고/포인트 붉은색 톤으로 변경 */
								backgroundColor : "#e74a3b",
								pointBackgroundColor : "#e74a3b",
								pointBorderColor : "#fff",
								pointRadius : 5,
								pointHoverRadius : 7,
								borderWidth : 3,
								tension : 0.3, /* 선을 약간 부드럽게 처리 */
								order : 1,
								datalabels : {
									color : "#e74a3b",
									anchor : "end",
									align : "top",
									offset : 4,
									font : { size : 13, weight: 'bold' },
									formatter : function(value) {
										if (value == 0) return "";
										return value;
									}
								}
							} ]
				},
				plugins : [ ChartDataLabels ],
				options : {
					responsive : true,
					maintainAspectRatio : false,
					layout : { padding : { top : 24 } },
					interaction : { mode : "index", intersect : false },
					plugins : {
						legend : {
							position : "bottom",
							reverse : true,
							labels : { boxWidth : 12, boxHeight : 12, color : "#333", font: {size: 13} }
						},
						tooltip : {
							mode : "index",
							intersect : false,
							backgroundColor: "rgba(255, 255, 255, 0.9)",
							titleColor: "#333",
							bodyColor: "#333",
							borderColor: "#ccc",
							borderWidth: 1,
							callbacks : {
								title : function(items) {
									if (items.length === 0) return "";
									return items[0].label;
								},
								label : function(context) {
									const value = context.parsed.y;
									if (context.dataset.yAxisID === "paymentAxis") {
										return context.dataset.label + " : " + Math.round(value).toLocaleString();
									}
									return context.dataset.label + " : " + value;
								}
							}
						}
					},
					scales : {
						x : {
							grid : { display : false },
							ticks : { color : axisColor, font: {size: 13} }
						},
						paymentAxis : {
							type : "linear",
							position : "left",
							beginAtZero : true,
							grid : { color: "#eee" },
							title : { display : true, text : "全体給与額（千ウォン）", color : axisColor, font: {weight: 'bold'} },
							ticks : {
								color : axisColor,
								callback : function(value) { return Number(value).toLocaleString(); }
							}
						},
						employeeAxis : {
							type : "linear",
							position : "right",
							beginAtZero : true,
							title : { display : true, text : "人数（名）", color : axisColor, font: {weight: 'bold'} },
							grid : { drawOnChartArea : false },
							ticks : { color : axisColor, precision : 0, maxTicksLimit : 5 }
						}
					}
				}
			});
		}
	</script>

</body>
</html>