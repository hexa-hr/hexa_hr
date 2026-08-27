<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員別休暇状況</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
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
    min-height: 600px;
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

/* 4. 데이터 테이블 스타일 (공통) */
table.data-table {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
    margin-bottom: 0; /* 하단 합계바와 붙이기 위해 마진 제거 */
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

table.data-table tr:hover td {
    background-color: #f1f5f9; /* 다른 테이블 호버 색상과 통일 */
}

/* 5. 하단 합계 영역 */
.summary-bar {
    background-color: #f8f9fa; /* 테이블 헤더와 톤 통일 (기존 노란색 제거) */
    border: 1px solid #ccc;
    border-top: none; /* 테이블 하단 선과 겹치지 않게 */
    padding: 15px;
    text-align: center;
    font-size: 14px;
    font-weight: bold;
    display: flex;
    justify-content: space-around;
    align-items: center;
    color: #333;
}

/* 텍스트 강조 색상 (공통 메인/경고 컬러) */
.text-blue { color: #4e73df !important; font-weight: bold; }
.text-red { color: #e74a3b !important; font-weight: bold; }
</style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

    <div class="wrap">
        <div class="container">
            <!-- 상단 타이틀 -->
            <div class="page-header">
                <h1>[${not empty detailList[0].departmentName ? detailList[0].departmentName : '部署未指定'}]
                ${not empty detailList[0].koreanName ? detailList[0].koreanName : '社員'}
                休暇状況</h1>
            </div>

            <!-- 상세 내역 테이블 -->
            <table class="data-table">
                <thead>
                    <tr>
                        <th style="width: 8%;">번호</th>
                        <th style="width: 16%;">입력일자</th>
                        <th style="width: 18%;">휴가항목</th>
                        <th style="width: 12%;">근태항목</th>
                        <th style="width: 24%;">기간</th>
                        <th style="width: 10%;">일수</th>
                        <th style="width: 12%;">적요</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty detailList}">
                        <tr>
                            <td colspan="7" style="padding: 30px; color: #888;">조회된 휴가 상세 내역이 없습니다.</td>
                        </tr>
                    </c:if>

                    <!-- 총 사용일수 계산을 위한 변수 초기화 -->
                    <c:set var="totalUsed" value="0" />

                    <c:forEach var="d" items="${detailList}" varStatus="status">
                        <c:set var="totalUsed" value="${totalUsed + d.days}" />
                        <tr>
                            <td>${status.count}</td>
                            <td>${d.regDate}</td>
                            <td>${d.vacationType}</td>
                            <td>${d.attendance}</td>
                            <td>${d.period}</td>
                            <td class="text-blue">${d.days}</td>
                            <td>${d.remarks}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%-- 1. 총 휴가일수 값을 변수로 지정 (데이터가 없으면 0 처리) --%>
            <c:set var="totalDays" value="${empty detailList ? 15 : detailList[0].totalDays}" />
            <%-- 2. 잔여일수 계산 (총 휴가일수 - 사용일수) --%>
            <c:set var="remainingDays" value="${totalDays - totalUsed}" />

            <!-- 하단 합계 바 -->
            <div class="summary-bar">
                <span>합 계</span> 
                <span> • 총 휴가일수 : <fmt:formatNumber value="${totalDays}" pattern="#,##0.000" />&nbsp;&nbsp;&nbsp;&nbsp;
                    • 사용일수 : <span class="text-blue"><fmt:formatNumber value="${totalUsed}" pattern="#,##0.000" /></span>&nbsp;&nbsp;&nbsp;&nbsp;
                    • 잔여일수 : <span class="text-red"><fmt:formatNumber value="${remainingDays}" pattern="#,##0.000" /></span>
                </span>
            </div>
        </div>
    </div>
</body>
</html>