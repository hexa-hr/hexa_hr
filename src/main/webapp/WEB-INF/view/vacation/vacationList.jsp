<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>休暇照会</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
/* 1. 전체 레이아웃 (공통) */
body {
    margin: 0;
    min-width: 1400px;
    background-color: #f8f9fa; /* 전체 배경색 통일 */
    font-family: 'Malgun Gothic', sans-serif;
    color: #333;
}

/* 2. 감싸는 영역 */
.wrap { 
    display: flex;
    align-items: flex-start;
    width: 100%;
}

.container {
    padding: 30px 40px; /* 좌우 여백을 본문과 통일 */
    background-color: white; /* 본문 흰색 배경 통일 */
    box-sizing: border-box;
    flex: 1;
}

/* 3. 타이틀 영역 */
.page-header {
    margin-bottom: 20px;
}

.page-header h1 {
    font-size: 22px; /* 다른 페이지의 타이틀 크기와 통일 */
    font-weight: bold;
    margin: 0;
    color: #333;
    border-bottom: 2px solid #4e73df; /* 파란색 밑줄 통일 */
    padding-bottom: 10px;
}

/* 4. 상단 검색 및 필터 바 스타일 */
.filter-bar {
    background: #f4f4f4; /* 폼 영역 배경색 톤 통일 */
    padding: 15px;
    border: 1px solid #ddd; /* 테두리 색상 통일 */
    border-radius: 3px; /* 모서리 둥글기 통일 */
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
}

.filter-bar label {
    font-size: 14px;
    font-weight: bold;
    color: #333;
}

.select-box, .input-text {
    padding: 5px; /* 입력 필드 여백 통일 */
    border: 1px solid #ccc; /* 테두리 색상 통일 */
    border-radius: 3px;
    font-size: 14px;
    outline: none;
}

/* 검색 폼 버튼 통일 */
.btn {
    padding: 6px 14px;
    border: none;
    border-radius: 3px;
    font-size: 13px;
    font-weight: bold;
    cursor: pointer;
    background-color: #4e73df; /* 메인 파란색 통일 */
    color: #fff;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.btn:hover {
    background-color: #2e59d9;
}

.btn-gray {
    background-color: #a5a5a5 !important; /* 보조 회색 통일 */
}
.btn-gray:hover {
    background-color: #858796 !important;
}

/* 5. 데이터 테이블 스타일 (공통) */
table.data-table {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
    background: white;
    margin-bottom: 30px;
}

table.data-table th, table.data-table td {
    border: 1px solid #ccc; /* 테두리 색상 통일 */
    padding: 10px;
    font-size: 14px;
    white-space: nowrap; /* 줄바꿈 방지 */
}

table.data-table th {
    background-color: #f8f9fa; /* 헤더 배경색 통일 */
    color: #333;
    font-weight: bold;
}

/* 행 클릭 가능하도록 스타일 추가 및 호버 효과 */
table.data-table tbody tr {
    cursor: pointer;
}

table.data-table tr:hover td {
    background-color: #f1f5f9; /* 다른 테이블 호버 색상과 통일 */
}

/* 6. 페이징 영역 (기존 스타일 유지, 여백/글꼴 조정) */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 25px;
    font-size: 14px;
}

.pagination a {
    text-decoration: none;
    color: #666;
    padding: 4px 8px;
}

.pagination .current {
    background-color: #4e73df; /* 메인 파란색 통일 */
    color: #fff;
    padding: 4px 10px;
    border-radius: 3px;
    font-weight: bold;
}
</style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

    <div class="wrap">
        <div class="container">
        	<!-- 페이지 헤더 -->
        	<div class="page-header">
        		<h1>休暇照会</h1>
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
        <%-- 				<option value="" ${empty param.vacationTypeId ? 'selected' : ''}>전체
        					휴가항목</option> --%>
        
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
        				<tr onclick="location.href='${pageContext.request.contextPath}/vacationDetail.do?employeeId=${vac.employeeId}&vacationTypeId=${param.vacationTypeId}'">
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
        </div>
    </div>
</body>
</html>