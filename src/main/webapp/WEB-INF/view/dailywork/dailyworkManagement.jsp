<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>일용직 근무기록/관리</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
    body { font-family: 'Malgun Gothic', dotum, sans-serif; font-size: 13px; color: #333; margin: 0; background-color: #f5f5f5;}
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
    .auto-calc-row { background-color: #ffffe0; } 
    .text-red { color: #d9534f; }
    .modal-overlay { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 100; justify-content: center; align-items: center; }
    .modal-content { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
    .project-list-ul { list-style: none; padding: 0; max-height: 200px; overflow-y: auto; border: 1px solid #ccc; margin-bottom: 10px; }
    .project-list-ul li { padding: 8px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; }
</style>
</head>
<body>

<jsp:include page="/WEB-INF/view/include/header.jsp" />
<jsp:include page="/WEB-INF/view/include/nav.jsp" />

<div class="container">
    <div class="left-panel">
        <h2>일용직 근무기록/관리</h2>
        <table id="employeeTable">
            <thead>
                <tr><th>선택</th><th>구분</th><th>사원번호</th><th>성명</th><th>부서</th><th>근무기록</th></tr>
            </thead>
            <tbody>
                <c:forEach var="emp" items="${empList}">
                    <tr>
                        <td><input type="checkbox" class="emp-checkbox" value="${emp.employeeId}" data-name="${emp.koreanName}"></td>
                        <td>${emp.employmentType}</td>
                        <td>No-${emp.employeeId}</td>
                        <td>${emp.koreanName}</td>
                        <td>${emp.departmentName}</td>
                        <!-- 관리 버튼에 클릭 이벤트 추가 -->
                        <td><button type="button" class="btn-blue" onclick="openWorkRecordModal('${emp.employeeId}', '${emp.koreanName}')">관리</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="right-panel">
        <form id="dailyWorkForm" action="${pageContext.request.contextPath}/dailywork/save.do" method="post" onsubmit="return validateForm();">
            <!-- 사원ID와 수정용 근무기록ID Hidden 태그 -->
            <input type="hidden" id="selectedEmpNo" name="employee_id">
            <input type="hidden" id="workId" name="work_id">
            
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
                            <c:forEach var="prj" items="${projectList}">
                                <option value="${prj.fieldOrProjectId}">${prj.projectName}</option>
                            </c:forEach>
                        </select>
                        <button type="button" class="btn-blue" onclick="document.getElementById('projectModal').style.display='flex'">목록관리</button>
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
                <button type="button" class="btn-reset" onclick="resetWorkForm()">내용 지우기</button>
            </div>
        </form>
    </div>
</div>

<!-- 현장/프로젝트 목록 관리 모달 -->
<div id="projectModal" class="modal-overlay">
    <!-- (이전과 동일한 프로젝트 관리 모달 내용, 길이상 생략하지 않고 포함합니다) -->
    <div class="modal-content" style="width: 400px;">
        <div style="font-size: 18px; font-weight: bold; margin-bottom: 15px;">현장/프로젝트 관리</div>
        <ul class="project-list-ul">
            <c:forEach var="prj" items="${projectList}">
                <li>
                    <span>${prj.projectName}</span>
                    <button type="button" onclick="deleteProject(${prj.fieldOrProjectId})" style="background:none; border:none; color:red; cursor:pointer;">삭제</button>
                </li>
            </c:forEach>
        </ul>
        <div id="addProjectDiv" style="display: none; margin-bottom: 10px;">
            <input type="text" id="newProjectName" placeholder="새 프로젝트명 입력">
            <button type="button" class="btn-blue" onclick="addProject()">저장</button>
            <button type="button" class="btn-reset" onclick="document.getElementById('addProjectDiv').style.display='none'">취소</button>
        </div>
        <button type="button" class="btn-blue" style="background-color: #666;" onclick="document.getElementById('addProjectDiv').style.display='block'">+ 추가하기</button>
        <div style="text-align: center; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px;">
            <button type="button" class="btn-blue" onclick="resetProjects()">초기화</button>
            <button type="button" class="btn-reset" onclick="document.getElementById('projectModal').style.display='none'">닫기</button>
        </div>
    </div>
</div>

<!-- 사원별 근무기록 리스트 모달 (새로 추가됨) -->
<div id="workRecordModal" class="modal-overlay">
    <div class="modal-content" style="width: 900px;">
        <div style="font-size: 18px; font-weight: bold; margin-bottom: 15px; display: flex; justify-content: space-between;">
            <span>사원별 근무기록</span>
            <button onclick="document.getElementById('workRecordModal').style.display='none'" style="cursor: pointer; font-size: 20px; background: none; border: none;">×</button>
        </div>
        <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
            <div id="modalWorkEmpInfo" style="font-weight: bold; color: #5c7cba;"></div>
            <div>
                <select id="searchYear" onchange="loadWorkRecordList()"></select> 년
                <select id="searchMonth" onchange="loadWorkRecordList()"></select> 월
            </div>
        </div>
        <table>
            <thead>
                <tr>
                    <th>근무일자</th><th>현장/프로젝트</th><th>일당</th><th>지급율</th><th>소득세</th><th>지방소득세</th><th>실지급액</th><th>수정/삭제</th>
                </tr>
            </thead>
            <tbody id="modalWorkTableBody">
                <!-- 동적 데이터 렌더링 영역 -->
            </tbody>
        </table>
    </div>
</div>

<script>
    // --- 공통 체크박스 및 급여 계산 로직 ---
    document.querySelectorAll('.emp-checkbox').forEach(cb => {
        cb.addEventListener('change', function() {
            document.querySelectorAll('.emp-checkbox').forEach(otherCb => { if(otherCb !== cb) otherCb.checked = false; });
            document.querySelectorAll('#employeeTable tbody tr').forEach(tr => tr.classList.remove('selected-row'));
            if(this.checked) {
                this.closest('tr').classList.add('selected-row');
                document.getElementById('selectedEmpNo').value = this.value; 
                document.getElementById('selectedEmpInfoDisplay').innerText = '선택된 사원: ' + this.dataset.name + ' (No-' + this.value + ')';
            } else {
                resetWorkForm();
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

    function formatComma(num) { return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","); }

    function calculatePay() {
        let wageInput = document.getElementById('dailyWage').value.replace(/,/g, '');
        let wage = parseFloat(wageInput) || 0;
        let rate = parseFloat(document.getElementById('paymentRate').value) || 0;

        let totalWage = wage * rate;
        let incomeTax = Math.floor(totalWage * 0.027); // 2.7%
        let localTax = Math.floor(incomeTax * 0.1);    // 소득세의 10%
        let actualPayment = totalWage - incomeTax - localTax;

        document.getElementById('incomeTax').value = formatComma(incomeTax);
        document.getElementById('localTax').value = formatComma(localTax);
        document.getElementById('actualPayment').value = formatComma(actualPayment);
    }

    document.querySelectorAll('.calc-trigger').forEach(input => {
        input.addEventListener('input', function(e) {
            if(this.id === 'dailyWage') {
                let value = this.value.replace(/[^0-9]/g, '');
                if(value) this.value = formatComma(value);
            }
            calculatePay();
        });
    });
    window.onload = calculatePay;

    function resetWorkForm() {
        document.getElementById('dailyWorkForm').reset();
        document.getElementById('workId').value = '';
        document.getElementById('selectedEmpNo').value = '';
        document.getElementById('selectedEmpInfoDisplay').innerText = "[사원을 먼저 좌측 체크박스에서 선택하세요]";
        document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        calculatePay();
    }

    // --- 근무기록 모달 리스트 (년월 필터 / 조회 / 수정 / 삭제) ---
    let currentWorkEmpNo = '';
    
    function openWorkRecordModal(empNo, empName) {
        currentWorkEmpNo = empNo;
        document.getElementById('workRecordModal').style.display = 'flex';
        document.getElementById('modalWorkEmpInfo').innerText = '• 성명: ' + empName + ' (No-' + empNo + ')';
        
        // 년월 셀렉트박스 셋팅 (최초 1회)
        const yearSel = document.getElementById('searchYear');
        const monthSel = document.getElementById('searchMonth');
        if (yearSel.options.length === 0) {
            const now = new Date();
            for (let i = now.getFullYear() - 5; i <= now.getFullYear() + 1; i++) yearSel.add(new Option(i, i));
            for (let i = 1; i <= 12; i++) {
                let m = i < 10 ? '0' + i : i;
                monthSel.add(new Option(m, m));
            }
            yearSel.value = now.getFullYear();
            let curMonth = now.getMonth() + 1;
            monthSel.value = curMonth < 10 ? '0' + curMonth : curMonth;
        }
        loadWorkRecordList();
    }

    function loadWorkRecordList() {
        const year = document.getElementById('searchYear').value;
        const month = document.getElementById('searchMonth').value;
        const tbody = document.getElementById('modalWorkTableBody');
        tbody.innerHTML = '<tr><td colspan="8">불러오는 중...</td></tr>';

        // 서버로 JSON 데이터 요청
        fetch('${pageContext.request.contextPath}/dailywork/list.do?empNo=' + currentWorkEmpNo + '&yearMonth=' + year + '-' + month)
            .then(res => res.json())
            .then(data => {
                tbody.innerHTML = '';
                if (!data || data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="8">해당 월의 근무 기록이 없습니다.</td></tr>';
                    return;
                }
                data.forEach((item) => {
                    let tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>\${item.workDate}</td>
                        <td>\${item.projectName}</td>
                        <td>\${formatComma(item.dailyWage)}</td>
                        <td>\${item.paymentRate}</td>
                        <td>\${formatComma(item.incomeTax)}</td>
                        <td>\${formatComma(item.localTax)}</td>
                        <td>\${formatComma(item.actualPayment)}</td>
                        <td>
                            <button type="button" style="border: 1px solid #ccc; padding: 2px 6px; cursor: pointer; background: #fff;" 
                                onclick="editWorkRecord(\${item.workId}, '\${item.workDate}', \${item.fieldProjectId}, \${item.dailyWage}, \${item.paymentRate})">수정</button>
                            <button type="button" style="border: none; padding: 2px 6px; cursor: pointer; background: #d9534f; color:#fff;" 
                                onclick="deleteWorkRecord(\${item.workId})">삭제</button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            })
            .catch(err => { tbody.innerHTML = '<tr><td colspan="8">데이터 조회 오류</td></tr>'; });
    }

    // 수정 버튼: 폼에 데이터 세팅
    function editWorkRecord(workId, workDate, projectId, wage, rate) {
        document.getElementById('workRecordModal').style.display = 'none'; // 모달 닫기
        
        document.getElementById('workId').value = workId; // 업데이트용 ID 세팅
        document.getElementById('selectedEmpNo').value = currentWorkEmpNo;
        
        let empName = document.getElementById('modalWorkEmpInfo').innerText.split('성명: ')[1].split(' (')[0];
        document.getElementById('selectedEmpInfoDisplay').innerText = '[수정 모드] 선택된 사원: ' + empName + ' (No-' + currentWorkEmpNo + ')';
        document.getElementById('selectedEmpInfoDisplay').style.color = '#d9534f';

        document.querySelector('input[name="work_date"]').value = workDate;
        document.querySelector('select[name="field_or_project_id"]').value = projectId;
        document.getElementById('dailyWage').value = formatComma(wage);
        document.getElementById('paymentRate').value = rate;

        calculatePay(); // 세금 자동 재계산
    }

    // 삭제 버튼: DB 삭제 처리
    function deleteWorkRecord(workId) {
        if(!confirm("이 근무 기록을 정말 삭제하시겠습니까?")) return;
        fetch('${pageContext.request.contextPath}/dailywork/delete.do?workId=' + workId, { method: 'POST' })
        .then(res => res.text())
        .then(res => {
            if(res === 'success') { alert('삭제되었습니다.'); loadWorkRecordList(); }
            else { alert('삭제 실패'); }
        });
    }

    // --- 프로젝트(현장) 관리 통신 로직 ---
    function addProject() {
        const name = document.getElementById('newProjectName').value.trim();
        if(!name) { alert("프로젝트명을 입력하세요."); return; }
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=add&projectName=' + encodeURIComponent(name)
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("추가 실패");
        });
    }

    function deleteProject(id) {
        if(!confirm("이 항목을 목록에서 삭제하시겠습니까?")) return;
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=delete&projectId=' + id
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("삭제 실패");
        });
    }

    function resetProjects() {
        if(!confirm("모든 현장/프로젝트를 비우시겠습니까?")) return;
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}, body: 'action=reset'
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("초기화 실패");
        });
    }
</script>
</body>
</html>