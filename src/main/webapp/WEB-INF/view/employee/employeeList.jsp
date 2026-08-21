<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 명부 (Employee List)</title>
<style>
    body { margin: 0; background-color: #f8f9fa; font-family: sans-serif; padding: 40px 0; }
    
    /* 🌟 사이드바를 없애고 가운데 정렬된 넓은 단일 박스로 변경! */
    .wrap { max-width: 1200px; margin: 0 auto; background-color: white; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0,0,0,0.05); }
    .container { padding: 40px; box-sizing: border-box; }
    
    .section-title { font-size: 24px; font-weight: bold; margin-bottom: 20px; color: #333; border-bottom: 2px solid #4e73df; padding-bottom: 10px; }
    
    /* 상단 컨트롤 영역 (검색창, 갯수 선택) */
    .top-controls { display: flex; justify-content: flex-end; margin-bottom: 15px; }
    .top-controls select { padding: 5px; font-size: 13px; border: 1px solid #ccc; outline: none; cursor: pointer; }
    
    table { width: 100%; border-collapse: collapse; margin-bottom: 30px; text-align: center; }
    th, td { border: 1px solid #ddd; padding: 12px 5px; font-size: 14px; }
    th { background-color: #f4f6f9; color: #333; font-weight: bold; }
    
    /* 마우스 올렸을 때 클릭할 수 있다는 느낌(포인터, 색상변화) 주기 */
    .clickable-row { cursor: pointer; }
    .clickable-row:hover { background-color: #f1f6ff; }
    
    .btn-wrap { text-align: center; margin-top: 20px; }
    .btn-wrap button { padding: 10px 20px; border: none; border-radius: 4px; color: white; font-weight: bold; font-size: 14px; cursor: pointer; margin: 0 5px; }
    .btn-blue { background-color: #3b71ca; }
    .btn-gray { background-color: #9e9e9e; }
    .btn-green { background-color: #14a44d; }
</style>
<script>
    // 전체 선택/해제 기능
    function toggleAll(source) {
        let checkboxes = document.getElementsByName('empIds');
        for(let i=0; i<checkboxes.length; i++) {
            checkboxes[i].checked = source.checked;
        }
    }

    // 선택 삭제 전 경고창
    function confirmDelete() {
        let checked = document.querySelectorAll('input[name="empIds"]:checked');
        if(checked.length === 0) {
            alert("삭제할 사원을 1명 이상 선택해주세요.");
            return false;
        }
        return confirm("선택한 사원 정보를 삭제하시겠습니까?\n(관련된 모든 부가정보도 함께 삭제됩니다.)");
    }

    // 갯수 선택 드롭다운 변경 시 URL 이동 함수
    function changeLimit(selectObj) {
        let limit = selectObj.value;
        location.href = '<%=request.getContextPath()%>/employee/list.do?limit=' + limit;
    }
</script>
</head>
<body>
    <div class="wrap">
        <div class="container">
            <div class="section-title">사원 명부</div>
            
            <!-- 상단 몇개씩 보기 드롭다운 -->
            <div class="top-controls">
                <select name="limit" onchange="changeLimit(this)">
                    <option value="10" ${limit == 10 ? 'selected' : ''}>10개씩 보기</option>
                    <option value="30" ${limit == 30 ? 'selected' : ''}>30개씩 보기</option>
                    <option value="50" ${limit == 50 ? 'selected' : ''}>50개씩 보기</option>
                    <option value="100" ${limit == 100 ? 'selected' : ''}>100개씩 보기</option>
                </select>
            </div>
            
            <form action="<%=request.getContextPath()%>/employee/delete.do" method="post" onsubmit="return confirmDelete();">
                <table>
                    <thead>
                        <tr>
                            <th><input type="checkbox" id="selectAll" onclick="toggleAll(this)"></th>
                            <th>사원번호</th>
                            <th>이름</th>
                            <th>부서</th>
                            <th>직위</th>
                            <th>고용형태</th>
                            <th>재직상태</th>
                            <th>입사일</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty employeeList}">
                            <tr>
                                <td colspan="8" style="padding: 40px;">등록된 사원이 없습니다.</td>
                            </tr>
                        </c:if>
                        
                        <c:forEach var="emp" items="${employeeList}">
                            <!-- 행(tr) 클릭 시 사원정보 페이지로 이동하도록 설정! -->
                            <tr class="clickable-row" onclick="location.href='<%=request.getContextPath()%>/employee/register.do?employeeId=${emp.employeeId}'">
                                
                                <!-- 체크박스 칸을 클릭했을 때는 페이지 이동을 막는 마법의 코드(stopPropagation) -->
                                <td onclick="event.stopPropagation();">
                                    <input type="checkbox" name="empIds" value="${emp.employeeId}">
                                </td>
                                
                                <td>${emp.employeeId}</td>
                                <td style="font-weight: bold;">${emp.koreanName}</td>
                                <td>${emp.departmentName != null ? emp.departmentName : '-'}</td>
                                <td>${emp.positionName != null ? emp.positionName : '-'}</td>
                                <td>${emp.employmentType}</td>
                                <td>
                                    <span style="color: ${emp.status == '재직' ? 'blue' : (emp.status == '퇴사' ? 'red' : 'black')}; font-weight: bold;">
                                        ${emp.status}
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${emp.hireDate}" pattern="yyyy-MM-dd"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- 하단 버튼 영역 -->
                <div class="btn-wrap">
                    <button type="button" class="btn-blue" onclick="location.href='<%=request.getContextPath()%>/employee/register.do'">신규사원 등록</button>
                    <button type="button" class="btn-blue" onclick="alert('일괄등록 기능은 준비중입니다.');">신규사원 일괄등록</button>
                    <button type="submit" class="btn-gray">선택 삭제</button>
                    <button type="button" class="btn-green" onclick="alert('엑셀 다운로드 기능은 준비중입니다.');">엑셀 다운로드</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>