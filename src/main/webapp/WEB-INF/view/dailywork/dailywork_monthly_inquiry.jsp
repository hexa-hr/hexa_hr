<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<title>일용직 근무 조회 - 월별</title>
<style>
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

/* 캘린더 날짜 칸 너비 고정 */
.cal-cell {
	width: 25px;
	height: 25px;
}

/* 근무가 있는 날 찍히는 빨간 점 */
.red-dot {
	display: inline-block;
	width: 8px;
	height: 8px;
	background-color: #e74c3c;
	border-radius: 50%;
	margin: auto;
}
</style>
</head>
<body>

	<!-- 검색창 (년/월 선택 부분 - 폼 액션 생략) -->

	<table>
		<thead>
			<!-- 헤더 1번째 줄 (1~16일) -->
			<tr>
				<th rowspan="2">구분</th>
				<th rowspan="2">사원번호</th>
				<th rowspan="2">성명</th>
				<th rowspan="2">부서</th>
				<c:forEach var="day" begin="1" end="16">
					<th class="cal-cell" style="${dayColors[day]}">${day}</th>
				</c:forEach>
				<th rowspan="2">합계</th>
				<th rowspan="2">소득세</th>
				<th rowspan="2">지방소득세</th>
				<th rowspan="2">실지급합계</th>
			</tr>
			<!-- 헤더 2번째 줄 (17~31일) -->
			<tr>
				<c:forEach var="day" begin="17" end="31">
					<th class="cal-cell" style="${dayColors[day]}">${day <= lastDay ? day : ''}
					</th>
				</c:forEach>
				<th></th>
				<!-- 16열을 맞추기 위한 빈 칸 (31일 이후) -->
			</tr>
		</thead>
		<tbody>
			<c:forEach var="vo" items="${summaryList}">
				<!-- 사원 데이터 1번째 줄 (1~16일 근무 여부) -->
				<tr>
					<td rowspan="2">일용직</td>
					<td rowspan="2">${vo.empNo}</td>
					<td rowspan="2">${vo.empName}</td>
					<td rowspan="2">${vo.department}</td>

					<c:forEach var="day" begin="1" end="16">
						<td class="cal-cell"><c:if
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
				<!-- 사원 데이터 2번째 줄 (17~31일 근무 여부) -->
				<tr>
					<c:forEach var="day" begin="17" end="31">
						<td class="cal-cell"><c:if
								test="${day <= lastDay && vo.workedDays.contains(day)}">
								<div class="red-dot"></div>
							</c:if></td>
					</c:forEach>
					<td></td>
					<!-- 16열 맞춤 빈 칸 -->
				</tr>
			</c:forEach>
		</tbody>
	</table>

</body>
</html>