<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>휴가일수 설정</title>
<style>
    body { font-family: sans-serif; padding: 20px; }
    table { width: 100%; border-collapse: collapse; margin-top: 10px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
    th { background-color: #f5f5f5; }
    .btn-container { margin-top: 15px; text-align: right; }
    .btn { padding: 8px 15px; background: #4e73df; color: #fff; border: none; cursor: pointer; border-radius: 4px; }
    .msg-box { padding: 10px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; margin-bottom: 10px; border-radius: 4px; }
</style>
</head>
<body>

<h3>휴가일수 설정</h3>

<!-- Java Redirect로 전달받은 저장 완료 메시지 (JSTL로 출력) -->
<c:if test="${param.saved == 'true'}">
    <div class="msg-box">휴가일수가 성공적으로 저장되었습니다.</div>
</c:if>

<form action="vacationDaysManage.do" method="post">
    <input type="hidden" name="attendanceTypeId" value="${attendanceTypeId}">

    <table>
        <thead>
            <tr>
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
        <td>${item.emp.employmentType}</td>
        <td>
            No-${item.emp.employeeId}
            <input type="hidden" name="employeeId" value="${item.emp.employeeId}">
        </td>
        <td>${item.emp.koreanName}</td>
        
        <!-- 숫자 ID 대신 조회된 명칭 출력 -->
        <td>${empty item.departmentName ? '-' : item.departmentName}</td>
        <td>${empty item.positionName ? '-' : item.positionName}</td>
        
        <td><fmt:formatDate value="${item.emp.hireDate}" pattern="yyyy-MM-dd"/></td>
        <td>
            <input type="number" name="vacationDays" value="${item.attendanceDays}" style="width: 60px; text-align: right;"> 일
        </td>
    </tr>
</c:forEach>
        </tbody>
    </table>

    <div class="btn-container">
        <button type="submit" class="btn">휴가일수 저장</button>
    </div>
</form>

</body>
</html>