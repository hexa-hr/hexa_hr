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

table.data-table tr:hover {
    background-color: #f8fafc;
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
    <form method="get" action="${pageContext.request.contextPath}/vacationList.do">
        <div class="filter-bar">
            <label>* 휴가항목 선택</label>
            <!-- 셀렉트 변경 시 바로 검색되도록 하려면 onchange="this.form.submit()" 추가 가능 -->
            <select name="vacationTypeId" class="select-box" onchange="this.form.submit()">
                <option value="">전체 휴가항목</option>
                <c:forEach var="vType" items="${vacationTypeList}">
                    <option value="${vType.vacation_type_id}" ${param.vacationTypeId == vType.vacation_type_id ? 'selected' : ''}>
                        ${vType.vacation_type_name}
                    </option>
                </c:forEach>
            </select>

            <input type="text" name="keyword" value="${param.keyword}" class="input-text" placeholder="검색어 입력">
            <button type="submit" class="btn">🔍</button>
            <a href="${pageContext.request.contextPath}/vacationList.do" class="btn" style="background-color: #64748b;">전체보기</a>
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

            <!-- 데이터 반복 출력 -->
            <c:forEach var="vac" items="${vacationList}">
                <tr>
                    <td>${vac.employment_type}</td>
                    <td>${vac.employee_number}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/vacationDetail.do?employeeId=${vac.employee_id}" style="color: #2563eb; text-decoration: underline;">
                            ${vac.korean_name}
                        </a>
                    </td>
                    <td>${vac.department_name}</td>
                    <td>${vac.position_name}</td>
                    <td>${vac.vacation_type_name}</td>
                    <td>${vac.total_days}</td>
                    <td style="color: #2563eb; font-weight: bold;">${vac.vacation_value}</td>
                    <td style="color: #e11d48; font-weight: bold;">
                        <fmt:formatNumber value="${vac.total_days - vac.vacation_value}" pattern="#,##0.#" />
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 하단 페이징 -->
    <div class="pagination">
        <a href="#">&lt; 이전페이지</a>
        <span class="current">1</span>
        <a href="#">다음페이지 &gt;</a>
    </div>

</body>
</html>