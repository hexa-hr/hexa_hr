<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>연도별 전체급여 통계</title>

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

	<h1>연도별 전체급여 통계</h1>

	<p class="description">귀속년도를 선택하면 선택연도를 포함한 최근 10개년의 전체 급여액과 인원현황을
		확인할 수 있습니다.</p>

	<jsp:useBean id="today" class="java.util.Date" />

	<fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

	<form class="search-form" method="get"
		action="${pageContext.request.contextPath}/wage/yearlyTotalStatistics.do">

		<div class="search-row">

			<label for="year"><span class="required-mark">*</span> 귀속년도를
				선택해 주세요.</label> <select id="year" name="year"
				onchange="this.form.submit();">

				<c:forEach begin="0" end="9" var="offset">

					<c:set var="yearOption" value="${currentYear - 9 + offset}" />

					<option value="${yearOption}"
						<c:if test="${yearOption == selectedYear}">selected</c:if>>${yearOption}
						년</option>

				</c:forEach>

			</select>

		</div>

		<c:if test="${not empty errorMessage}">
			<div class="error-message">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

	</form>


	<c:if test="${not empty yearlyTotalStatistics}">

		<div class="chart-container">
			<canvas id="yearlyTotalChart"></canvas>
		</div>

		<div class="result-container">

			<table class="result-table">

				<colgroup>

					<col class="col-title" />

					<c:forEach var="row" items="${yearlyTotalStatistics.rows}">
						<col />
					</c:forEach>

				</colgroup>

				<thead>
					<tr>
						<th>구분</th>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<th><c:out value="${row.year}" />년</th>

						</c:forEach>
					</tr>
				</thead>


				<tbody>

					<tr>
						<td class="row-title">전체 급여액 (천원)</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><fmt:formatNumber value="${row.totalPayment / 1000}"
									pattern="#,##0" /></td>

						</c:forEach>
					</tr>


					<tr>
						<td class="growth-title">└ 증감률</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

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
					</tr>


					<tr>
						<td class="row-title">인원 (명)</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

							<td><fmt:formatNumber value="${row.averageEmployeeCount}"
									pattern="0.0" /></td>

						</c:forEach>
					</tr>


					<tr>
						<td class="growth-title">└ 증감률</td>

						<c:forEach var="row" items="${yearlyTotalStatistics.rows}">

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
					</tr>

				</tbody>

			</table>

		</div>

	</c:if>

	<script>
		const yearLabels = [
			<c:forEach var="row" items="${yearlyTotalStatistics.rows}"
				varStatus="status">

				"${row.year}년"

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const yearlyPaymentData = [
			<c:forEach var="row" items="${yearlyTotalStatistics.rows}"
				varStatus="status">

				${row.totalPayment / 1000}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const yearlyEmployeeData = [
			<c:forEach var="row" items="${yearlyTotalStatistics.rows}"
				varStatus="status">

				${row.averageEmployeeCount}

				<c:if test="${!status.last}">
					,
				</c:if>

			</c:forEach>
		];

		const axisColor = "#5b7096";

		// 인원은 월평균이라 소수가 나온다. 표시할 때만 소수 1자리로 반올림한다.
		function roundEmployeeCount(value) {
			return Math.round(Number(value) * 10) / 10;
		}

		const chartCanvas = document.getElementById("yearlyTotalChart");

		if (chartCanvas) {

			new Chart(chartCanvas, {

				data : {

					labels : yearLabels,

					datasets : [

							{
								type : "bar",

								label : "전체 급여액 (천원)",

								data : yearlyPaymentData,

								yAxisID : "paymentAxis",

								backgroundColor : "#9BBB59",

								borderColor : "#9BBB59",

								borderWidth : 1,

								barPercentage : 0.92,

								categoryPercentage : 0.9,

								order : 2,

								datalabels : {
									color : "#333333",
									anchor : "center",
									align : "center",

									font : {
										size : 12
									},

									// 금액이 0인 연도는 막대가 없어 라벨만 축 위에 남는다
									display : function(context) {
										return Number(context.dataset.data[context.dataIndex]) > 0;
									},

									formatter : function(value) {
										return Math.round(value).toLocaleString();
									}
								}
							},

							{
								type : "line",

								label : "인원 (명)",

								data : yearlyEmployeeData,

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
									color : "#2E75B6",
									anchor : "end",
									align : "top",
									offset : 2,

									font : {
										size : 12
									},

									formatter : function(value) {
										return roundEmployeeCount(value);
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

									return context.dataset.label + "  "
											+ roundEmployeeCount(value).toFixed(1);
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
								text : "전체 급여액 (천원)",
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
								text : "인원 (명)",
								color : axisColor
							},

							grid : {
								drawOnChartArea : false
							},

							ticks : {
								color : axisColor,
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