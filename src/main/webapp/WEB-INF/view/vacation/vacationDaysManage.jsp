<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>휴가일수 설정</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; color: #333; }
    h3 { margin-bottom: 15px; font-size: 20px; }
    
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #e2e8f0; padding: 10px; text-align: center; font-size: 14px; }
    th { background-color: #f8fafc; color: #475569; font-weight: bold; }
    
    .msg-box { padding: 10px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; margin-bottom: 15px; border-radius: 4px; }
    
    /* 하단 버튼 영역 디자인 */
    .footer-btn-container {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 20px;
        padding-top: 15px;
    }
    .left-btn-group, .right-btn-group {
        display: flex;
        gap: 8px;
    }
    
    .btn {
        padding: 9px 16px;
        border: none;
        border-radius: 4px;
        font-size: 14px;
        font-weight: bold;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 6px;
        color: #fff;
    }
    
    .btn-gray { background-color: #78828a; }
    .btn-gray:hover { background-color: #646d74; }
    
    .btn-blue { background-color: #4a8bf5; }
    .btn-blue:hover { background-color: #3576e0; }
    
    .input-days {
        width: 60px;
        padding: 4px 6px;
        text-align: right;
        border: 1px solid #cbd5e1;
        border-radius: 3px;
    }
</style>
</head>
<body>

<h3>휴가일수 설정</h3>

<c:if test="${param.saved == 'true'}">
    <div class="msg-box">처리가 완료되었습니다.</div>
</c:if>

<form id="vacationForm" method="post">
    <input type="hidden" name="attendanceTypeId" value="${attendanceTypeId}">

    <table>
        <thead>
            <tr>
                <th style="width: 40px;"><input type="checkbox" disabled></th>
                <th>구분</th>
                <th>사원번호</th>
                <th>성명</th>
                <th>부서</th>
                <th>직위</th>
                <th>입사일</th>
                <th>휴가일수</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${empVacationList}">
                <tr>
                    <td>
                        <!-- 선택 삭제/저장용 체크박스 -->
                        <input type="checkbox" name="selectedEmpId" value="${item.emp.employeeId}">
                    </td>
                    <td>${item.emp.employmentType}</td>
                    <td>
                        No-${item.emp.employeeId}
                        <input type="hidden" name="employeeId" value="${item.emp.employeeId}">
                    </td>
                    <td>${item.emp.koreanName}</td>
                    <td>${empty item.departmentName ? '-' : item.departmentName}</td>
                    <td>${empty item.positionName ? '-' : item.positionName}</td>
                    <td><fmt:formatDate value="${item.emp.hireDate}" pattern="yyyy-MM-dd"/></td>
                    <td>
                        <input type="number" name="vacationDays" value="${item.attendanceDays}" class="input-days"> 일
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 이미지와 동일한 하단 버튼 레이아웃 -->
    <div class="footer-btn-container">
        <!-- 좌측 버튼 그룹 -->
        <div class="left-btn-group">
            <button type="submit" class="btn btn-gray" 
                    formaction="${pageContext.request.contextPath}/vacationDaysDelete.do">
                ✕ 휴가일수 삭제
            </button>
            
            <button type="submit" class="btn btn-gray" 
                    formaction="${pageContext.request.contextPath}/vacationDaysManage.do">
                ✚ 휴가일수 저장
            </button>
        </div>

        <!-- 우측 버튼 그룹 -->
        <div class="right-btn-group">
            <button type="submit" class="btn btn-blue" 
                    formaction="${pageContext.request.contextPath}/vacationDaysAutoCalc.do">
                ✚ 휴가일수 자동계산
            </button>
        </div>
    </div>
</form>

</body>
</html>