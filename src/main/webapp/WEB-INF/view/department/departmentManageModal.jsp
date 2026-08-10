<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>부서 설정하기</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; }
        .department-list { list-style: none; padding: 0; margin-bottom: 20px; }
        .department-list li { padding: 8px 0; border-bottom: 1px dashed #ddd; display: flex; justify-content: space-between; }
        .btn-del { color: red; text-decoration: none; font-size: 12px; }
        .form-group { margin-top: 15px; }
        .btn-clear { width: 100%; padding: 10px; background-color: #3b5998; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 20px; }
    </style>
</head>
<body>
    <h3>부서 설정하기</h3>
    
    <ul class="department-list">
        <c:if test="${empty departmentList}">
            <li>등록된 부서가 없습니다.</li>
        </c:if>
        <c:forEach var="department" items="${departmentList}">
            <li>
                <span>${department.departmentName}</span>
                <a href="departmentManage.do?cmd=delete&departmentId=${department.departmentId}" class="btn-del">삭제</a>
            </li>
        </c:forEach>
    </ul>

    <form action="departmentManage.do" method="post" class="form-group">
        <input type="hidden" name="cmd" value="add">
        <input type="text" name="departmentName" placeholder="새 부서명 입력" required>
        <button type="submit">저장</button>
    </form>

    <form action="departmentManage.do" method="post" onsubmit="return confirm('정말 모든 부서를 초기화하시겠습니까?');">
        <input type="hidden" name="cmd" value="clear">
        <button type="submit" class="btn-clear">초기화</button>
    </form>

    <div style="text-align: center; margin-top: 15px;">
        <button onclick="opener.location.reload(); window.close();">닫기 (본창 적용)</button>
    </div>
</body>
</html>