<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일용직 근무기록/관리</title>
<style>
    body { font-family: 'Malgun Gothic', dotum, sans-serif; font-size: 13px; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5;}
    .container { display: flex; gap: 30px; background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    .left-panel { flex: 6; overflow-y: auto; max-height: 600px; }
    .right-panel { flex: 4; border-left: 1px solid #ddd; padding-left: 30px; }
    
    table { width: 100%; border-collapse: collapse; text-align: center; }
    th, td { border: 1px solid #e2e2e2; padding: 10px; }
    th { background-color: #f4f4f4; }
    .selected-row { background-color: #e8f0fe !important; font-weight: bold; }

    .btn-blue { background-color: #5c7cba; color: white; border: none; padding: 4px 8px; cursor: pointer; border-radius: 3px; font-size: 12px; }
    .btn-reset { background-color: #999; color: white; border: none; padding: 6px 15px; border-radius: 3px; cursor: pointer; }
    .btn-submit { background-color: #5c7cba; color: white; border: none; padding: 6px 20px; border-radius: 3px; cursor: pointer; }
    
    .form-table th { width: 120px; text-align: left; background-color: #fbfbfb; }
    .form-table td { text-align: left; }
    .form-table input[type="text"], .form-table input[type="date"], .form-table select { padding: 4px; border: 1px solid #ccc; width: 150px; text-align: right; }
    .auto-calc-row { background-color: #ffffe0; } /* 자동계산 노란색 배경 */
    .text-red { color: #d9534f; }

    /* 모달 스타일 */
    .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 100; justify-content: center; align-items: center; }
    .modal-content { background: white; width: 400px; padding: 20px; border-radius: 8px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
    .project-list-ul { list-style: none; padding: 0; max-height: 200px; overflow-y: auto; border: 1px solid #ccc; margin-bottom: 10px; }
    .project-list-ul li { padding: 8px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; }
</style>
</head>
<body>

<div class="container">
    <!-- 1. 좌측 사원 리스트 영역 -->
    <div class="left-panel">
        <h2>일용직 근무기록/관리</h2>
        <table id="employeeTable">
            <thead>
                <tr>
                    <th>선택</th>
                    <th>구분</th>
                    <th>사원번호</th>
                    <th>성명</th>
                    <th>부서</th>
                    <th>근무기록</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="emp" items="${empList}">
                    <tr>
                        <td><input type="checkbox" class="emp-checkbox" value="${emp.employeeId}" data-name="${emp.koreanName}"></td>
                        <td>${emp.employmentType}</td>
                        <td>No-${emp.employeeId}</td>
                        <td>${emp.koreanName}</td>
                        <td>${emp.departmentName}</td>
                        <td><button type="button" class="btn-blue">관리</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 2. 우측 입력 폼 영역 -->
    <div class="right-panel">
        <form id="dailyWorkForm" action="${pageContext.request.contextPath}/dailywork/save.do" method="post" onsubmit="return validateForm();">
            <input type="hidden" id="selectedEmpNo" name="employee_id">
            
            <div id="selectedEmpInfoDisplay" style="margin-bottom: 10px; font-weight: bold; color: #5c7cba;">
                [사원을 먼저 좌측 체크박스에서 선택하세요]
            </div>

            <table class="form-table">
                <tr style="border-top: 2px solid #000;">
                    <th>근무일자</th>
                    <td><input type="date" name="work_date" required></td>
                </tr>
                <tr>
                    <th>현장/프로젝트</th>
                    <td>
                        <select name="field_or_project_id" required>
                            <option value="">선택하세요.</option>
                            <!-- 프로젝트 리스트 동적 렌더링 -->
                            <c:forEach var="prj" items="${projectList}">
                                <option value="${prj.fieldOrProjectId}">${prj.projectName}</option>
                            </c:forEach>
                        </select>
                        <button type="button" class="btn-blue" onclick="openProjectModal()">목록관리</button>
                    </td>
                </tr>
                <tr>
                    <th>일당</th>
                    <td><input type="text" id="dailyWage" name="daily_wage" class="text-red calc-trigger" value="110,000" required> 원</td>
                </tr>
                <tr>
                    <th>지급율</th>
                    <td><input type="text" id="paymentRate" name="payment_rate" class="text-red calc-trigger" value="1.0" required></td>
                </tr>
                <tr class="auto-calc-row">
                    <th>소득세</th>
                    <td><input type="text" id="incomeTax" name="income_tax" class="text-red" readonly></td>
                </tr>
                <tr class="auto-calc-row">
                    <th>지방소득세</th>
                    <td><input type="text" id="localTax" name="local_tax" class="text-red" readonly></td>
                </tr>
                <tr class="auto-calc-row">
                    <th>실지급액</th>
                    <td><input type="text" id="actualPayment" name="actual_payment" class="text-red" readonly></td>
                </tr>
            </table>
            
            <div style="text-align: center; margin-top: 20px;">
                <button type="submit" class="btn-submit">저장</button>
                <button type="button" class="btn-reset" onclick="document.getElementById('dailyWorkForm').reset(); calculatePay();">내용 지우기</button>
            </div>
        </form>
    </div>
</div>

<!-- 3. 현장/프로젝트 목록 관리 모달 -->
<div id="projectModal" class="modal-overlay">
    <div class="modal-content">
        <div style="font-size: 18px; font-weight: bold; margin-bottom: 15px;">현장/프로젝트 관리</div>
        
        <ul class="project-list-ul">
            <c:forEach var="prj" items="${projectList}">
                <li>
                    <span>${prj.projectName}</span>
                    <button type="button" onclick="deleteProject(${prj.fieldOrProjectId})" style="background:none; border:none; color:red; cursor:pointer;">삭제</button>
                </li>
            </c:forEach>
        </ul>

        <!-- 추가 폼 -->
        <div id="addProjectDiv" style="display: none; margin-bottom: 10px;">
            <input type="text" id="newProjectName" placeholder="새 프로젝트명 입력">
            <button type="button" class="btn-blue" onclick="addProject()">저장</button>
            <button type="button" class="btn-reset" onclick="document.getElementById('addProjectDiv').style.display='none'">취소</button>
        </div>

        <div style="display: flex; justify-content: space-between; align-items: center;">
            <button type="button" class="btn-blue" style="background-color: #666;" onclick="document.getElementById('addProjectDiv').style.display='block'">+ 추가하기</button>
        </div>

        <div style="text-align: center; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px;">
            <button type="button" class="btn-blue" onclick="resetProjects()">초기화</button>
            <button type="button" class="btn-reset" onclick="closeProjectModal()">닫기</button>
        </div>
    </div>
</div>

<script>
    // --- 1. 체크박스 선택 로직 ---
    document.querySelectorAll('.emp-checkbox').forEach(cb => {
        cb.addEventListener('change', function() {
            document.querySelectorAll('.emp-checkbox').forEach(otherCb => { if(otherCb !== cb) otherCb.checked = false; });
            document.querySelectorAll('#employeeTable tbody tr').forEach(tr => tr.classList.remove('selected-row'));
            
            if(this.checked) {
                this.closest('tr').classList.add('selected-row');
                document.getElementById('selectedEmpNo').value = this.value; 
                document.getElementById('selectedEmpInfoDisplay').innerText = '선택된 사원: ' + this.dataset.name + ' (No-' + this.value + ')';
            } else {
                document.getElementById('selectedEmpNo').value = "";
                document.getElementById('selectedEmpInfoDisplay').innerText = "[사원을 먼저 좌측 체크박스에서 선택하세요]";
            }
        });
    });

    function validateForm() {
        if (!document.getElementById('selectedEmpNo').value) {
            alert('좌측 목록에서 근무를 기록할 사원을 선택해 주세요.');
            return false;
        }
        return true;
    }

    // --- 2. 급여 자동 계산 로직 (콤마 처리 포함) ---
    function formatComma(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function calculatePay() {
        // 일당(콤마 제거)과 지급율 가져오기
        let wageInput = document.getElementById('dailyWage').value.replace(/,/g, '');
        let wage = parseFloat(wageInput) || 0;
        let rate = parseFloat(document.getElementById('paymentRate').value) || 0;

        let totalWage = wage * rate;

        // 세금 계산 (예시: 소득세 2.7%, 지방소득세는 소득세의 10%)
        let incomeTax = Math.floor(totalWage * 0.027);
        let localTax = Math.floor(incomeTax * 0.1);
        let actualPayment = totalWage - incomeTax - localTax;

        // 화면에 콤마 찍어서 값 넣기
        document.getElementById('incomeTax').value = formatComma(incomeTax);
        document.getElementById('localTax').value = formatComma(localTax);
        document.getElementById('actualPayment').value = formatComma(actualPayment);
    }

    // 입력 시마다 자동계산 실행 및 콤마 재적용
    document.querySelectorAll('.calc-trigger').forEach(input => {
        input.addEventListener('input', function(e) {
            if(this.id === 'dailyWage') {
                // 숫자만 남기고 콤마 다시 찍기
                let value = this.value.replace(/[^0-9]/g, '');
                if(value) this.value = formatComma(value);
            }
            calculatePay();
        });
    });

    // 화면 첫 로드 시 자동계산 1회 실행
    window.onload = calculatePay;


    // --- 3. 프로젝트(현장) 관리 Ajax 통신 로직 ---
    function openProjectModal() { document.getElementById('projectModal').style.display = 'flex'; }
    function closeProjectModal() { document.getElementById('projectModal').style.display = 'none'; }

    function addProject() {
        const name = document.getElementById('newProjectName').value.trim();
        if(!name) { alert("프로젝트명을 입력하세요."); return; }

        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=add&projectName=' + encodeURIComponent(name)
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); // 성공 시 화면 새로고침하여 리스트 갱신
            else alert("추가 실패");
        });
    }

    function deleteProject(id) {
        if(!confirm("이 항목을 목록에서 삭제하시겠습니까? (기존 기록은 유지됩니다)")) return;
        
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=delete&projectId=' + id
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload();
            else alert("삭제 실패");
        });
    }

    function resetProjects() {
        if(!confirm("목록에 있는 모든 현장/프로젝트를 비우시겠습니까?")) return;
        
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=reset'
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload();
            else alert("초기화 실패");
        });
    }
</script>

</body>
</html>