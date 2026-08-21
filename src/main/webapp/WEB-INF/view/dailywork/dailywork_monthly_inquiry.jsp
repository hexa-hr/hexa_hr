<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<title>일용직 근무 조회 - 월별</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
    font-family: 'Malgun Gothic', dotum, sans-serif;
    margin: 0;
}

/* 상단 제목 및 탭 디자인 */
.page-header {
	margin-bottom: 20px;
}

.title-area {
	display: flex;
	align-items: center;
	gap: 15px;
	margin-bottom: 15px;
}

.title-area h2 {
	margin: 0;
	font-size: 24px;
	color: #333;
}

.title-area p {
	margin: 0;
	font-size: 13px;
	color: #777;
}

.divider {
	border: 0;
	border-top: 1px solid #ddd;
	margin-bottom: 20px;
}

.tab-group {
	display: flex;
	gap: 5px;
	margin-bottom: 20px;
}

.tab-btn {
	padding: 12px 35px;
	font-size: 15px;
	font-weight: bold;
	color: white;
	border: none;
	cursor: pointer;
	border-radius: 3px;
}

.tab-active {
	background-color: #5a9b9c;
}

.tab-inactive {
	background-color: #a6a6a6;
}

/* 테이블 디자인 */
table {
	border-collapse: collapse;
	width: 100%;
	font-size: 13px;
	text-align: center;
}

th, td {
	border: 1px solid #ddd;
	padding: 5px;
}

th {
	background-color: #f9f9f9;
}

.cal-cell {
	width: 25px;
	height: 25px;
}

.red-dot {
	display: inline-block;
	width: 8px;
	height: 8px;
	background-color: #e74c3c;
	border-radius: 50%;
	margin: auto;
}

/* 주말 연한 배경색 */
.bg-sun {
	background-color: #fff0f0;
} /* 연한 빨강 */
.bg-sat {
	background-color: #f0f8ff;
} /* 연한 파랑 */

/* 🌟 수정된 디자인: 사원 1명의 데이터(2줄)를 묶어서 전체 하이라이트 및 클릭 처리 */
.emp-row-group {
	cursor: pointer;
}

.emp-row-group:hover td {
	background-color: #f0f4f8 !important;
} /* 마우스를 올리면 그룹 안의 모든 칸이 옅은 파란회색으로 변함 */
</style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<!-- 상단 제목 영역 -->
	<div class="page-header">
		<div class="title-area">

			<div>
				<h2>일용직 근무 조회</h2>

			</div>
		</div>
		<hr class="divider">
	</div>

	<!-- 탭 버튼 영역 -->
	<div class="tab-group">
		<button type="button" class="tab-btn tab-active"
			onclick="location.href='monthly.do'">월별 조회</button>
		<button type="button" class="tab-btn tab-inactive"
			onclick="location.href='detail.do'">상세 조회</button>
	</div>

	<table>
		<thead>
			<!-- 헤더 1번째 줄 (1~16일) -->
			<tr>
				<th rowspan="2">구분</th>
				<th rowspan="2">사원번호</th>
				<th rowspan="2">성명</th>
				<th rowspan="2">부서</th>

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

				<th rowspan="2">합계</th>
				<th rowspan="2">소득세</th>
				<th rowspan="2">지방소득세</th>
				<th rowspan="2">실지급합계</th>
			</tr>
			<!-- 헤더 2번째 줄 (17~31일) -->
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
				<!-- 사원 데이터 1번째 줄 (1~16일) -->
				<tr>
					<td rowspan="2">일용직</td>
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
				<!-- 사원 데이터 2번째 줄 (17~31일) -->
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

</body>
</html>