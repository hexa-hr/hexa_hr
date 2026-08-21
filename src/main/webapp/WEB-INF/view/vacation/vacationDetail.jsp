<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원별 휴가현황</title>
<style>
    body {
        font-family: 'Malgun Gothic', sans-serif;
        background-color: #f8fafc;
        margin: 20px;
        color: #333;
    }
    .modal-container {
        background: #fff;
        padding: 25px;
        border: 1px solid #cbd5e1;
        border-radius: 6px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }
    .title-area {
        text-align: center;
        font-size: 18px;
        font-weight: bold;
        color: #1e3a8a;
        margin-bottom: 20px;
    }
    table.detail-table {
        width: 100%;
        border-collapse: collapse;
        text-align: center;
        font-size: 13px;
        background: #fff;
    }
    table.detail-table th {
        background-color: #eff6ff;
        border: 1px solid #cbd5e1;
        padding: 10px 8px;
        color: #1e3a8a;
        font-weight: bold;
    }
    table.detail-table td {
        border: 1px solid #cbd5e1;
        padding: 8px;
    }
    table.detail-table tr:hover {
        background-color: #f8fafc;
    }
    /* 하단 합계 영역 */
    .summary-bar {
        margin-top: -1px;
        background-color: #fef08a;
        border: 1px solid #cbd5e1;
        padding: 12px;
        text-align: center;
        font-size: 13px;
        font-weight: bold;
        display: flex;
        justify-content: space-around;
        align-items: center;
    }
</style>
</head>
<body>

    <div class="modal-container">
        <!-- 상단 타이틀 -->
        <div class="title-area">
            [${not empty detailList[0].departmentName ? detailList[0].departmentName : '부서미지정'}] 
            ${not empty detailList[0].koreanName ? detailList[0].koreanName : '사원'} 사원 휴가현황
        </div>

        <!-- 상세 내역 테이블 -->
        <table class="detail-table">
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
                        <td style="color: #2563eb; font-weight: bold;">${d.days}</td>
                        <td>${d.remarks}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- 하단 합계 바 -->
        <div class="summary-bar">
            <span>합 계</span>
            <span>
                • 총 휴가일수 : <fmt:formatNumber value="${empty detailList ? 19 : detailList[0].totalDays}" pattern="#,##0.000" />&nbsp;&nbsp;&nbsp;&nbsp;
                • 사용일수 : <span style="color: #2563eb;"><fmt:formatNumber value="${totalUsed}" pattern="#,##0.000" /></span>&nbsp;&nbsp;&nbsp;&nbsp;
                • 잔여일수 : <span style="color: #e11d48;"><fmt:formatNumber value="${(empty detailList ? 19 : detailList[0].totalDays) - totalUsed}" pattern="#,##0.000" /></span>
            </span>
        </div>
    </div>

</body>
</html>