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

select, button {
	padding: 6px 10px;
}

select {
	border: 1px solid #b9c2cf;
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

	<h1>月別全体給与統計</h1>

	<p class="description">帰属年度を選択すると、月別の全体給与額と人数を確認できます。</p>

	<jsp:useBean id="today" class="java.util.Date" />

	<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/monthlyTotalStatistics.do">

		<div class="search-row">

			<label for="year"><span class="required-mark">*</span>
				帰属年度を選択してください。</label> <select id="year" name="year"
				onchange="this.form.submit();">

				<c:forEach begin="0" end="9" var="offset">

					<c:set var="yearOption" value="${currentYear - 9 + offset}" />

					<option value="${yearOption}"
						<c:if test="${yearOption == selectedYear}">selected</c:if>>${yearOption}
						年</option>

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

		<div class="chart-container">
			<canvas id="monthlyTotalChart"></canvas>
		</div>

		<div class="result-container">

			<table class="result-table">

				<colgroup>

					<col class="col-title" />

					<c:forEach begin="1" end="13">
						<col />
					</c:forEach>

				</colgroup>

				<thead>
					<tr>
						<th>区分</th>

						<c:forEach var="row" items="${monthlyTotalStatistics.rows}"
							varStatus="status">

							<th>${status.count}月</th>

						</c:forEach>

						<th class="total-column">合計</th>
					</tr>
				</thead>


				<tbody>

					<tr>
						<td class="row-title">全体給与額（千ウォン）</td>

						<c:forEach var="row" items="${monthlyTotalStatistics.rows}">

							<td><fmt:formatNumber value="${row.totalPayment / 1000}"
									pattern="#,##0" /></td>

						</c:forEach>

						<td class="total-column"><fmt:formatNumber
								value="${monthlyTotalStatistics.totalPayment / 1000}"
								pattern="#,##0" /></td>
					</tr>


					<tr>
						<td class="growth-title">└ 増減率</td>

						<c:forEach var="row" items="${monthlyTotalStatistics.rows}">

							<td><c:choose>

									<c:when test="${row.paymentGrowthRate == null}"></c:when>

									<c:when test="${row.paymentGrowthRate > 0}">
										<span class="positive"> <fmt:formatNumber
												value="${row.paymentGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:when test="${row.paymentGrowthRate < 0}">
										<span class="negative"> <fmt:formatNumber
												value="${row.paymentGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:otherwise>
										<span class="neutral"> <fmt:formatNumber
												value="${row.paymentGrowthRate}" pattern="0.0" />%
										</span>
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

						<td class="total-column"></td>
					</tr>


					<tr>
						<td class="row-title">人数（名）</td>

						<c:forEach var="row" items="${monthlyTotalStatistics.rows}">

							<td><c:out value="${row.employeeCount}" /></td>

						</c:forEach>

						<td class="total-column"><fmt:formatNumber
								value="${monthlyTotalStatistics.averageEmployeeCount}"
								pattern="0.0" /></td>
					</tr>


					<tr>
						<td class="growth-title">└ 増減率</td>

						<c:forEach var="row" items="${monthlyTotalStatistics.rows}">

							<td><c:choose>

									<c:when test="${row.employeeGrowthRate == null}"></c:when>

									<c:when test="${row.employeeGrowthRate > 0}">
										<span class="positive"> <fmt:formatNumber
												value="${row.employeeGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:when test="${row.employeeGrowthRate < 0}">
										<span class="negative"> <fmt:formatNumber
												value="${row.employeeGrowthRate}" pattern="0.0" />%
										</span>
									</c:when>

									<c:otherwise>
										<span class="neutral"> <fmt:formatNumber
												value="${row.employeeGrowthRate}" pattern="0.0" />%
										</span>
									</c:otherwise>

								</c:choose></td>

						</c:forEach>

						<td class="total-column"></td>
					</tr>

				</tbody>

			</table>

		</div>

	</c:if>

	<script>
		const monthlyPaymentData = [
			<c:forEach var="row" items="${monthlyTotalStatistics.rows}"
				varStatus="status">

				${row.totalPayment / 1000}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const monthlyEmployeeData = [
			<c:forEach var="row" items="${monthlyTotalStatistics.rows}"
				varStatus="status">

				${row.employeeCount}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const monthLabels = [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月",
				"9月", "10月", "11月", "12月" ];

		const axisColor = "#5b7096";

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

								backgroundColor : "#4472C4",

								borderColor : "#4472C4",

								borderWidth : 1,

								barPercentage : 0.92,

								categoryPercentage : 0.9,

								order : 2,

								datalabels : {
									color : "#ffffff",
									anchor : "center",
									align : "center",

									font : {
										size : 12
									},

									formatter : function(value) {
										return Math.round(value).toLocaleString();
									}
								}
							},

							{
								type : "line",

								label : "人数（名）",

								data : monthlyEmployeeData,

								yAxisID : "employeeAxis",

								borderColor : "#ED7D31",

								backgroundColor : "#ED7D31",

								pointBackgroundColor : "#ED7D31",

								pointBorderColor : "#ED7D31",

								pointRadius : 4,

								pointHoverRadius : 6,

								borderWidth : 2,

								tension : 0,

								order : 1,

								datalabels : {
									color : "#ED7D31",
									anchor : "end",
									align : "top",
									offset : 2,

									font : {
										size : 12
									},

									formatter : function(value) {
										return value;
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

							reverse : true,

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

								label : function(context) {

									const value = context.parsed.y;

									if (context.dataset.yAxisID === "paymentAxis") {

										return context.dataset.label + "  "
												+ Math.round(value).toLocaleString();
									}

									return context.dataset.label + "  " + value;
								}
							}
						}
					},

					scales : {

						x : {
							grid : {
								display : false
							},

							ticks : {
								color : axisColor
							}
						},

						paymentAxis : {

							type : "linear",

							position : "left",

							beginAtZero : true,

							grid : {
								display : false
							},

							title : {
								display : true,
								text : "全体給与額（千ウォン）",
								color : axisColor
							},

							ticks : {

								color : axisColor,

								callback : function(value) {
									return Number(value).toLocaleString();
								}
							}
						},

						employeeAxis : {

							type : "linear",

							position : "right",

							beginAtZero : true,

							title : {
								display : true,
								text : "人数（名）",
								color : axisColor
							},

							grid : {
								drawOnChartArea : false
							},

							ticks : {
								color : axisColor,
								precision : 0,
								maxTicksLimit : 4
							}
						}
					}
				}
			});
		}
	</script>

</body>
</html>