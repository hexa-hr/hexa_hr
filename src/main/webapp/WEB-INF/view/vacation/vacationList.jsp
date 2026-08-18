<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>휴가조회</title>
<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	margin: 20px;
	color: #333;
	background-color: #f8fafc;
}

.page-header {
	margin-bottom: 25px;
	background: #fff;
	padding: 20px;
	border: 1px solid #e2e8f0;
	border-radius: 6px;
}

.page-header h1 {
	font-size: 22px;
	margin: 0 0 5px 0;
	display: flex;
	align-items: center;
	gap: 8px;
}

.page-header p {
	font-size: 13px;
	color: #666;
	margin: 0;
}

/* 상단 검색 및 필터 바 스타일 */
.filter-bar {
	background: #fff;
	padding: 12px 20px;
	border: 1px solid #e2e8f0;
	border-radius: 6px;
	display: flex;
	align-items: center;
	gap: 12px;
	margin-bottom: 20px;
}

.filter-bar label {
	font-size: 13px;
	font-weight: bold;
}

.select-box, .input-text {
	padding: 6px 10px;
	border: 1px solid #cbd5e1;
	border-radius: 4px;
	font-size: 13px;
}

.btn {
	padding: 6px 14px;
	border: none;
	border-radius: 4px;
	font-size: 13px;
	font-weight: bold;
	cursor: pointer;
	background-color: #3b82f6;
	color: #fff;
	text-decoration: none;
	display: inline-flex;
	align-items: center;
	gap: 4px;
}

.btn:hover {
	background-color: #2563eb;
}

/* 데이터 테이블 스타일 */
table.data-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	font-size: 13px;
	background: #fff;
	border: 1px solid #e2e8f0;
}

table.data-table th {
	background-color: #eff6ff;
	border: 1px solid #e2e8f0;
	padding: 12px 8px;
	color: #1e3a8a;
	font-weight: bold;
}

table.data-table td {
	border: 1px solid #e2e8f0;
	padding: 10px 8px;
}

/* 행 클릭 가능하도록 스타일 추가 */
table.data-table tbody tr {
	cursor: pointer;
}

table.data-table tr:hover {
	background-color: #f1f5f9;
}

/* 페이징 영역 */
.pagination {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 8px;
	margin-top: 25px;
	font-size: 13px;
}

.pagination a {
	text-decoration: none;
	color: #64748b;
	padding: 4px 8px;
}

.pagination .current {
	background-color: #3b82f6;
	color: #fff;
	padding: 4px 10px;
	border-radius: 4px;
	font-weight: bold;
}
</style>
</head>
<body>

	<!-- 페이지 헤더 -->
	<div class="page-header">
		<h1>📋 휴가조회</h1>
		<p>전체 사원 휴가현황을 한 눈에 보실 수 있습니다. 사원별 상세 휴가내역도 확인할 수 있습니다.</p>
	</div>

	<!-- 검색 및 필터 폼 -->
	<form method="get"
		action="${pageContext.request.contextPath}/vacationList.do">
		<div class="filter-bar">
			<label>* 휴가항목 선택</label> <select name="vacationTypeId"
				class="select-box" onchange="handleSelectChange(this)">
				<!-- 1. 설정 페이지로 이동하는 고정 옵션 -->
				<option value="setting" style="font-weight: bold; color: #2563eb;">휴가
					설정하기</option>

				<!-- 2. 기본 안내 옵션 (선택 안 함) -->
				<option value="" ${empty param.vacationTypeId ? 'selected' : ''}>전체
					휴가항목</option>

				<!-- 3. 사용여부가 'Y'인 휴가항목 리스트 출력 -->
				<c:forEach var="vType" items="${activeVacationTypeList}">
					<option value="${vType.vacationTypeId}"
						${param.vacationTypeId == vType.vacationTypeId ? 'selected' : ''}>
						${vType.vacationTypeName}</option>
				</c:forEach>
			</select>

			<!-- "휴가 설정하기" 선택 시 페이지 이동을 처리하는 스크립트 -->
			<script>
				function handleSelectChange(selectElement) {
					if (selectElement.value === 'setting') {
						location.href = '${pageContext.request.contextPath}/vacationTypeSetting.do';
					} else {
						selectElement.form.submit();
					}
				}
			</script>

			<input type="text" name="keyword" value="${param.keyword}"
				class="input-text" placeholder="검색어 입력">
			<button type="submit" class="btn">🔍</button>
			<a href="${pageContext.request.contextPath}/vacationList.do"
				class="btn" style="background-color: #64748b;">전체보기</a>
		</div>
	</form>

	<!-- 휴가 현황 데이터 테이블 -->
	<table class="data-table">
		<thead>
			<tr>
				<th style="width: 10%;">구분</th>
				<th style="width: 12%;">사원번호</th>
				<th style="width: 10%;">성명</th>
				<th style="width: 12%;">부서</th>
				<th style="width: 10%;">직위</th>
				<th style="width: 16%;">휴가항목</th>
				<th style="width: 10%;">전체</th>
				<th style="width: 10%;">사용</th>
				<th style="width: 10%;">잔여</th>
			</tr>
		</thead>
		<tbody>
			<!-- 데이터가 없는 경우 -->
			<c:if test="${empty vacationList}">
				<tr>
					<td colspan="9" style="padding: 30px; color: #888;">조회된 휴가 정보가 없습니다.</td>
				</tr>
			</c:if>

			<!-- 데이터 반복 출력 (행 클릭 시 상세 페이지 이동) -->
			<c:forEach var="vac" items="${vacationList}">
				<tr onclick="location.href='${pageContext.request.contextPath}/vacationDetail.do?employeeId=${vac.employeeId}'">
					<td>${vac.employmentType}</td>
					<td>No-${vac.employeeNumber}</td>
					<td style="color: #2563eb; text-decoration: underline; font-weight: bold;">${vac.koreanName}</td>
					<td>${vac.departmentName}</td>
					<td>${vac.positionName}</td>
					<td>${vac.vacationTypeName}</td>
					<td>${vac.totalDays}</td>
					<td style="color: #2563eb; font-weight: bold;">${vac.usedDays}</td>
					<td style="color: #e11d48; font-weight: bold;">
						<fmt:formatNumber value="${vac.remainingDays}" pattern="#,##0.#" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<!-- 하단 페이징 -->
	<div class="pagination">
		<a href="#">&lt; 이전페이지</a> <span class="current">1</span> <a href="#">다음페이지 &gt;</a>
	</div>

</body>
</html>