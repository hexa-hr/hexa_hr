<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>근태 상세 조회</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 13px; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
    .container { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); min-height: 600px; }
    
    /* 상단 타이틀 및 탭 영역 */
    .page-header { display: flex; align-items: center; gap: 15px; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
    .page-title-icon { font-size: 35px; }
    .page-title-text h1 { margin: 0; font-size: 22px; color: #333; letter-spacing: -1px; }
    .page-title-text p { margin: 5px 0 0 0; font-size: 13px; color: #777; }
    
    .tab-menu { display: flex; gap: 5px; margin-bottom: 20px; border-bottom: 2px solid #ddd; padding-bottom: 10px; }
    .tab-btn { padding: 10px 30px; font-size: 14px; font-weight: bold; border: none; cursor: pointer; border-radius: 4px; }
    .tab-active { background-color: #599b9a; color: white; } /* 활성화 탭 (청록색) */
    .tab-inactive { background-color: #a5a5a5; color: white; } /* 비활성화 탭 (회색) */
    .tab-inactive:hover { background-color: #888; }
    
    /* 레이아웃 패널 */
    .content-wrap { display: flex; gap: 20px; }
    
    /* 좌측 필터 패널 */
    .filter-panel { flex: 0 0 300px; border-right: 2px solid #333; padding-right: 20px; }
    .filter-table { width: 100%; border-collapse: collapse; }
    .filter-table td { padding: 10px 5px; border-bottom: 1px solid #eee; vertical-align: middle; }
    .filter-table input[type="text"], .filter-table input[type="date"], .filter-table select {
        width: 100%; padding: 4px; box-sizing: border-box; border: 1px solid #ccc;
    }
    
    .btn-wrap { display: flex; gap: 10px; margin-top: 20px; justify-content: center; }
    .btn-search { background-color: #e5502c; color: #fff; border: none; padding: 8px 20px; cursor: pointer; border-radius: 3px; font-weight: bold; }
    .btn-all { background-color: #999; color: #fff; border: none; padding: 8px 20px; cursor: pointer; border-radius: 3px; font-weight: bold; }

    /* 우측 결과 패널 */
    .result-panel { flex: 1; overflow-x: auto; }
    .result-table { width: 100%; border-collapse: collapse; text-align: center; }
    .result-table th, .result-table td { border: 1px solid #ddd; padding: 8px; }
    .result-table th { background-color: #f4f8fe; color: #333; font-weight: bold; }
</style>
</head>
<body>

<div class="container">
    <!-- 상단 타이틀 및 탭 메뉴 -->
    <div class="page-header">
       
        <div class="page-title-text">
            <h1>근태조회</h1>
            
        </div>
    </div>
    
    <div class="tab-menu">
        <button class="tab-btn tab-inactive" onclick="location.href='${pageContext.request.contextPath}/attendance/monthly.do'">월별 조회</button>
        <button class="tab-btn tab-active" onclick="location.href='${pageContext.request.contextPath}/attendance/detail.do'">상세 조회</button>
    </div>

    <div class="content-wrap">
        <!-- 좌측 조건 검색 패널 -->
        <div class="filter-panel">
            <form id="searchForm">
                <table class="filter-table">
                    <tr>
                        <td style="width: 25px;"><input type="checkbox" id="chkInputDate"></td>
                        <td style="width: 75px;"><label for="chkInputDate">입력일자</label></td>
                        <td><input type="date" id="inputDate"></td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkAttPeriod"></td>
                        <td><label for="chkAttPeriod">근태기간</label></td>
                        <td style="display: flex; gap: 5px; align-items: center;">
                            <input type="date" id="startDate"> ~ <input type="date" id="endDate">
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkDept"></td>
                        <td><label for="chkDept">부서</label></td>
                        <td>
                            <select id="deptId">
                                <option value="">선택하세요.</option>
                                <c:forEach var="dept" items="${deptList}">
                                    <option value="${dept.id}">${dept.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkName"></td>
                        <td><label for="chkName">성명</label></td>
                        <td><input type="text" id="empName" placeholder="성명을 입력하세요."></td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkAttGroup"></td>
                        <td><label for="chkAttGroup">근태그룹</label></td>
                        <td>
                            <select id="attGroupId">
                                <option value="">선택하세요.</option>
                                <c:forEach var="group" items="${attGroupList}">
                                    <option value="${group.id}">${group.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkAttType"></td>
                        <td><label for="chkAttType">근태항목</label></td>
                        <td>
                            <select id="attTypeId">
                                <option value="">선택하세요.</option>
                                <c:forEach var="type" items="${attTypeList}">
                                    <option value="${type.id}">${type.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkVacType"></td>
                        <td><label for="chkVacType">휴가항목</label></td>
                        <td>
                            <select id="vacTypeId">
                                <option value="">선택하세요.</option>
                                <c:forEach var="vac" items="${vacTypeList}">
                                    <option value="${vac.id}">${vac.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><input type="checkbox" id="chkSummary"></td>
                        <td><label for="chkSummary">적요</label></td>
                        <td><input type="text" id="summary"></td>
                    </tr>
                </table>
                
                <div class="btn-wrap">
                    <button type="button" class="btn-search" onclick="searchData()">검색</button>
                    <button type="button" class="btn-all" onclick="searchAll()">전체보기</button>
                </div>
            </form>
        </div>

        <!-- 우측 검색 결과 패널 -->
        <div class="result-panel">
            <table class="result-table">
                <thead>
                    <tr>
                        <th style="color:#5c7cba;">입력일자</th>
                        <th style="color:#5c7cba;">구분</th>
                        <th style="color:#5c7cba;">성명</th>
                        <th style="color:#5c7cba;">부서</th>
                        <th style="color:#5c7cba;">직위</th>
                        <th>근태항목</th>
                        <th>근태기간</th>
                        <th>근태일수</th>
                        <th>금액</th>
                        <th>적요</th>
                    </tr>
                </thead>
                <tbody id="resultBody">
                    <tr><td colspan="10" style="padding: 30px;">데이터를 불러오는 중입니다...</td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    // 1. 페이지 로드 시 실행 (월별조회에서 사원 클릭으로 넘어왔는지 판별)
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const targetName = urlParams.get('targetName'); // 전달받은 사원이름 추출

        if (targetName) {
            // 전달받은 사원명이 있으면 해당 사원 정보 세팅 및 즉시 검색
            document.getElementById('chkName').checked = true;
            document.getElementById('empName').value = targetName;
            searchData();
        } else {
            // 일반 접근 시 전체 검색
            searchAll();
        }
    };

    // 2. '전체보기' 클릭 시 모든 필터 초기화 후 검색
    function searchAll() {
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        document.getElementById('searchForm').reset();
        executeSearch();
    }

    // 3. '검색' 클릭 시
    function searchData() {
        executeSearch();
    }

    // 4. 서버 통신 (AJAX) - 체크된 항목만 서버로 전송
    function executeSearch() {
        const params = new URLSearchParams();
        
        // 체크박스가 선택된 항목의 input 값들만 파라미터에 추가
        if (document.getElementById('chkInputDate').checked) {
            params.append('chkInputDate', 'true');
            params.append('inputDate', document.getElementById('inputDate').value);
        }
        if (document.getElementById('chkAttPeriod').checked) {
            params.append('chkAttPeriod', 'true');
            params.append('startDate', document.getElementById('startDate').value);
            params.append('endDate', document.getElementById('endDate').value);
        }
        if (document.getElementById('chkDept').checked) {
            params.append('chkDept', 'true');
            params.append('deptId', document.getElementById('deptId').value);
        }
        if (document.getElementById('chkName').checked) {
            params.append('chkName', 'true');
            params.append('empName', document.getElementById('empName').value);
        }
        if (document.getElementById('chkAttGroup').checked) {
            params.append('chkAttGroup', 'true');
            params.append('attGroupId', document.getElementById('attGroupId').value);
        }
        if (document.getElementById('chkAttType').checked) {
            params.append('chkAttType', 'true');
            params.append('attTypeId', document.getElementById('attTypeId').value);
        }
        if (document.getElementById('chkVacType').checked) {
            params.append('chkVacType', 'true');
            params.append('vacTypeId', document.getElementById('vacTypeId').value);
        }
        if (document.getElementById('chkSummary').checked) {
            params.append('chkSummary', 'true');
            params.append('summary', document.getElementById('summary').value);
        }

        const tbody = document.getElementById("resultBody");
        tbody.innerHTML = '<tr><td colspan="10" style="padding: 20px;">검색 중입니다...</td></tr>';

        fetch("${pageContext.request.contextPath}/attendance/detail.do?" + params.toString(), {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        })
        .then(res => res.json())
        .then(data => {
            tbody.innerHTML = "";
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="10" style="padding: 30px;">검색된 근태 내역이 없습니다.</td></tr>';
                return;
            }

            data.forEach(item => {
                let row = `<tr>
                    <td style="color:#5c7cba;">\${item.inputDate}</td>
                    <td style="color:#5c7cba;">\${item.empType}</td>
                    <td style="color:#5c7cba;">\${item.empName}</td>
                    <td style="color:#5c7cba;">\${item.deptName}</td>
                    <td style="color:#5c7cba;">\${item.positionName}</td>
                    <td>\${item.attTypeName}</td>
                    <td>\${item.attPeriod}</td>
                    <td>\${item.attDays}(d)</td>
                    <td>\${item.amount}</td>
                    <td>\${item.summary}</td>
                </tr>`;
                tbody.innerHTML += row;
            });
        })
        .catch(err => {
            tbody.innerHTML = '<tr><td colspan="10" style="color:red;">검색 중 서버 오류가 발생했습니다.</td></tr>';
        });
    }
</script>

</body>
</html>