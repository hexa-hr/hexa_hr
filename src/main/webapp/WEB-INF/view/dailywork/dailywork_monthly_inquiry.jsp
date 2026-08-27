<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<title>日雇い勤務照会 - 月別</title>

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
}

/* 2. 타이틀 영역 */
.page-header {
	margin-bottom: 20px;
}

.title-area h2 {
	margin: 0;
	font-size: 22px;
	color: #333;
}

.title-area p {
	display: none;
}

.divider {
	display: none;
}

/* 3. 탭 버튼 영역 */
.tab-group {
	display: flex;
	gap: 5px;
	margin-bottom: 20px;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.tab-btn {
	padding: 10px 30px;
	font-size: 14px;
	font-weight: bold;
	border: none;
	cursor: pointer;
	border-radius: 3px 3px 0 0;
	color: white;
}

.tab-active {
	background-color: #4e73df;
}

.tab-inactive {
	background-color: #a5a5a5;
}
.tab-inactive:hover {
	background-color: #858796;
}

/* 4. 테이블 디자인 */
table {
	border-collapse: collapse;
	width: 100%;
	font-size: 14px;
	text-align: center;
	background: white;
	margin-bottom: 30px;
}

th, td {
	border: 1px solid #ccc;
	padding: 8px 4px;
	white-space: nowrap;
}

th {
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
}

.cal-cell {
	width: 25px;
	height: 25px;
}

.red-dot {
	display: inline-block;
	width: 8px;
	height: 8px;
	background-color: #e74a3b;
	border-radius: 50%;
	margin: auto;
}

/* 週末の薄い背景色 */
.bg-sun {
	background-color: #fff5f5;
}
.bg-sat {
	background-color: #f8f9fa;
}

.emp-row-group {
	cursor: pointer;
}

.emp-row-group:hover td {
	background-color: #f1f5f9 !important;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />
    
    <div class="wrap">
        <div class="container">
        	<!-- 上部タイトル領域 -->
        	<div class="page-header">
        		<div class="title-area">
        
        			<div>
        				<h2>日雇い勤務照会</h2>
        
        			</div>
        		</div>
        		<hr class="divider">
        	</div>
        
        	<!-- タブボタン領域 -->
        	<div class="tab-group">
        		<button type="button" class="tab-btn tab-active"
        			onclick="location.href='monthly.do'">月別照会</button>
        		<button type="button" class="tab-btn tab-inactive"
        			onclick="location.href='detail.do'">詳細照会</button>
        	</div>
        
        	<table>
        		<thead>
        			<!-- ヘッダー1行目 (1~16日) -->
        			<tr>
        				<th rowspan="2">区分</th>
        				<th rowspan="2">社員番号</th>
        				<th rowspan="2">姓名</th>
        				<th rowspan="2">部署</th>
        
        				<c:forEach var="day" begin="1" end="16">
        					<c:set var="bgClass" value="" />
        					<c:if test="${fn:contains(dayColors[day], 'red')}">
        						<c:set var="bgClass" value="bg-sun" />
        					</c:if>
        					<c:if test="${fn:contains(dayColors[day], 'blue')}">
        						<c:set var="bgClass" value="bg-sat" />
        					</c:if>
        					<th class="cal-cell ${bgClass}" style="${dayColors[day]}">${day}</th>
        				</c:forEach>
        
        				<th rowspan="2">合計</th>
        				<th rowspan="2">所得税</th>
        				<th rowspan="2">住民税</th>
        				<th rowspan="2">実支給合計</th>
        			</tr>
        			<!-- ヘッダー2行目 (17~31日) -->
        			<tr>
        				<c:forEach var="day" begin="17" end="31">
        					<c:set var="bgClass" value="" />
        					<c:if test="${fn:contains(dayColors[day], 'red')}">
        						<c:set var="bgClass" value="bg-sun" />
        					</c:if>
        					<c:if test="${fn:contains(dayColors[day], 'blue')}">
        						<c:set var="bgClass" value="bg-sat" />
        					</c:if>
        					<th class="cal-cell ${bgClass}" style="${dayColors[day]}">${day <= lastDay ? day : ''}</th>
        				</c:forEach>
        				<th></th>
        			</tr>
        		</thead>
        
        
        		<c:forEach var="vo" items="${summaryList}">
        			<tbody class="emp-row-group"
        				onclick="location.href='detail.do?empName=${vo.empName}'">
        				<!-- 社員データ1行目 (1~16日) -->
        				<tr>
        					<td rowspan="2">日雇い</td>
        					<td rowspan="2">${vo.empNo}</td>
        					<td rowspan="2">${vo.empName}</td>
        					<td rowspan="2">${vo.department}</td>
        
        					<c:forEach var="day" begin="1" end="16">
        						<c:set var="bgClass" value="" />
        						<c:if test="${fn:contains(dayColors[day], 'red')}">
        							<c:set var="bgClass" value="bg-sun" />
        						</c:if>
        						<c:if test="${fn:contains(dayColors[day], 'blue')}">
        							<c:set var="bgClass" value="bg-sat" />
        						</c:if>
        						<td class="cal-cell ${bgClass}"><c:if
        								test="${vo.workedDays.contains(day)}">
        								<div class="red-dot"></div>
        							</c:if></td>
        					</c:forEach>
        
        					<td rowspan="2"><fmt:formatNumber value="${vo.totalDays}" /></td>
        					<td rowspan="2"><fmt:formatNumber value="${vo.incomeTax}" /></td>
        					<td rowspan="2"><fmt:formatNumber value="${vo.localTax}" /></td>
        					<td rowspan="2"><fmt:formatNumber
        							value="${vo.totalActualPayment}" /></td>
        				</tr>
        				<!-- 社員データ2行目 (17~31日) -->
        				<tr>
        					<c:forEach var="day" begin="17" end="31">
        						<c:set var="bgClass" value="" />
        						<c:if test="${fn:contains(dayColors[day], 'red')}">
        							<c:set var="bgClass" value="bg-sun" />
        						</c:if>
        						<c:if test="${fn:contains(dayColors[day], 'blue')}">
        							<c:set var="bgClass" value="bg-sat" />
        						</c:if>
        						<td class="cal-cell ${bgClass}"><c:if
        								test="${day <= lastDay && vo.workedDays.contains(day)}">
        								<div class="red-dot"></div>
        							</c:if></td>
        					</c:forEach>
        					<td></td>
        				</tr>
        			</tbody>
        		</c:forEach>
        	</table>
        </div>
    </div>

</body>
</html>