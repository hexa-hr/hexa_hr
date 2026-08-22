<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>근태기록/관리</title>
<style>
body {
	font-family: 'Malgun Gothic', dotum, sans-serif;
	font-size: 13px;
	color: #333;
	margin: 0;
	padding: 20px;
	background-color: #f5f5f5;
}

/* Layout */
.container {
	display: flex;
	gap: 30px;
	background: #fff;
	padding: 20px;
	border-radius: 5px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.left-panel {
	flex: 6;
	overflow-y: auto;
	max-height: 600px;
}

.right-panel {
	flex: 4;
	border-left: 1px solid #ddd;
	padding-left: 30px;
}

/* Table Styles */
table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
}

th, td {
	border: 1px solid #e2e2e2;
	padding: 10px;
}

th {
	background-color: #f4f4f4;
}

.selected-row {
	background-color: #e8f0fe !important;
	font-weight: bold;
}

/* Button Styles */
.btn-manage {
	background: white;
	border: 1px solid #ccc;
	padding: 4px 8px;
	cursor: pointer;
	border-radius: 3px;
	font-size: 12px;
}

.btn-manage:hover {
	background: #eee;
}

.btn-delete {
	background: #ff4d4f;
	color: white;
	border: none;
	padding: 4px 8px;
	cursor: pointer;
	border-radius: 3px;
	font-size: 12px;
}

.btn-delete:hover {
	background: #ff7875;
}

.btn-submit {
	background-color: #5c7cba;
	color: white;
	border: none;
	padding: 6px 20px;
	border-radius: 3px;
	cursor: pointer;
}

.btn-reset {
	background-color: #999;
	color: white;
	border: none;
	padding: 6px 15px;
	border-radius: 3px;
	cursor: pointer;
}

/* Modal Styles */
.modal-overlay {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 100;
	justify-content: center;
	align-items: center;
}

.modal-content {
	background: white;
	width: 850px;
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 15px;
	display: flex;
	justify-content: space-between;
}

.btn-close-modal {
	cursor: pointer;
	font-size: 20px;
	background: none;
	border: none;
}

/* Form Styles */
.form-table th {
	width: 120px;
	text-align: left;
	background-color: #fbfbfb;
}

.form-table td {
	text-align: left;
}

input[type="date"], input[type="text"], input[type="number"], select {
	padding: 3px;
	border: 1px solid #ccc;
}
</style>
</head>
<body>

	<div class="container">
		<!-- 좌측 리스트 영역 -->
		<div class="left-panel">
			<h2>근태기록/관리</h2>
			<table id="employeeTable">
				<thead>
					<tr>
						<th>선택</th>
						<th>구분</th>
						<th>사원번호</th>
						<th>성명</th>
						<th>부서</th>
						<th>직위</th>
						<th>근태기록</th>
					</tr>
				</thead>
				<tbody>
					<!-- DB 동적 목록 조회를 위한 JSTL 반복문 -->
					<c:forEach var="emp" items="${empList}">
						<tr>
							<td><input type="checkbox" class="emp-checkbox"
								value="${emp.employeeId}" data-name="${emp.koreanName}"></td>
							<td>${emp.employmentType}</td>
							<td>No-${emp.employeeId}</td>
							<td>${emp.koreanName}</td>
							<td>${emp.departmentName}</td>
							<td>${emp.positionName}</td>
							<td>
								<button type="button" class="btn-manage"
									onclick="openModal('${emp.employeeId}', '${emp.koreanName}', '${emp.departmentName}', '${emp.positionName}')">관리</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- 우측 폼 영역 -->
		<div class="right-panel">
			<!-- 폼 데이터가 attendanceSaveProcess.jsp로 넘어가도록 action 주소 변경 -->
			<form id="attendanceForm"
				action="${pageContext.request.contextPath}/attendance/save.do"
				method="post" onsubmit="return validateForm();">

				<input type="hidden" id="selectedEmpNo" name="employee_id">
				<input type="hidden" id="attendanceId" name="attendance_id">
				<!-- 서버로 넘어가는 입력일자 자동 설정 -->
				<input type="hidden" id="inputDate" name="input_date"
					value="<fmt:formatDate value='${now}' pattern='yyyy-MM-dd' />">

				<div id="selectedEmpInfoDisplay"
					style="margin-bottom: 10px; font-weight: bold; color: #5c7cba;">
					[사원을 먼저 좌측 체크박스에서 선택하세요]</div>

				<table class="form-table">
					<tr style="border-top: 2px solid #000;">
						<th>입력일자</th>
						<td id="currentDateDisplay"><fmt:formatDate value="${now}"
								pattern="yyyy-MM-dd" /></td>
					</tr>
					<tr>
						<th>근태항목</th>
						<td>
							<!-- [수정된 부분 1] data-unit 추가 -->
							<select id="attendanceType" name="attendance_type_id"
								onchange="toggleVacationPeriod()" required>
									<option value="" data-has-vacation="false" data-unit="일">선택하세요.</option>
									<c:forEach var="att" items="${attendanceList}">
										<option value="${att.attendanceTypeId}" 
												data-has-vacation="${not empty att.vacationTypeId and att.vacationTypeId != 0 ? 'true' : 'false'}"
												data-vacation-name="${att.vacationTypeName}"
												data-start="<fmt:formatDate value='${att.applyPeriod1}' pattern='yyyy-MM-dd'/>"
												data-end="<fmt:formatDate value='${att.applyPeriod2}' pattern='yyyy-MM-dd'/>"
												data-unit="${att.unit}">
											${att.attendanceTypeName}
										</option>
									</c:forEach>
							</select>
						</td>
					</tr>
					<!-- JS가 데이터를 꽂아넣을 수 있도록 id(vacationPeriodDisplay) 부여, 초기엔 비워둠 -->
					<tr id="vacationPeriodRow" style="display: none;">
						<th style="color: #e53935;">휴가적용기간</th>
						<td id="vacationPeriodDisplay" style="color: #e53935; font-weight: bold;"></td>
					</tr>
					<tr>
						<th>기간</th>
						<td><input type="date" id="startDate" name="start_date"
							required> ~ <input type="date" id="endDate"
							name="end_date" required></td>
					</tr>
					<tr>
						<th>근태일수</th>
						<td><input type="number" id="attendanceDays"
							name="attendance_days" min="0" step="0.5" value="1" required>
							<!-- [수정된 부분 2] 단위 글자 변경을 위해 id="unitText" span 추가 -->
							<span id="unitText">일</span></td>
					</tr>
					<tr>
						<th>금액(수당)</th>
						<td><input type="number" id="wageAmount" name="amount"
							value="0"> 원</td>
					</tr>
					<tr>
						<th>적요</th>
						<td><input type="text" id="remark" name="summary"
							style="width: 80%;"></td>
					</tr>
				</table>
				<div style="text-align: center; margin-top: 20px;">
					<button type="submit" class="btn-submit">저장</button>
					<button type="button" class="btn-reset" onclick="resetForm();">내용
						지우기</button>
				</div>
			</form>
		</div>
	</div>

	<!-- 모달 팝업 영역 -->
	<div id="recordModal" class="modal-overlay">
		<div class="modal-content">
			<div class="modal-header">
				<span>사원별 근태기록</span>
				<button class="btn-close-modal" onclick="closeModal()">×</button>
			</div>
			<div style="margin-bottom: 10px; font-weight: bold;"
				id="modalEmpInfo">
				<!-- JS로 이름, 부서, 직위 입력 -->
			</div>
			<table>
				<thead>
					<tr>
						<th>번호</th>
						<th>입력일자</th>
						<th>근태항목</th>
						<th>근태기간</th>
						<th>근태일수</th>
						<th>금액</th>
						<th>적요</th>
						<th>수정/삭제</th>
					</tr>
				</thead>
				<tbody id="modalTableBody">
					<!-- 동적 렌더링 영역 -->
				</tbody>
			</table>
		</div>
	</div>

	<script>
    var currentModalEmpNo = '';

    // 1. 체크박스 선택 시 사번 저장 및 UI 표시
    var checkboxes = document.querySelectorAll('.emp-checkbox');
    checkboxes.forEach(function(cb) {
        cb.addEventListener('change', function() {
            checkboxes.forEach(function(otherCb) { if(otherCb !== cb) otherCb.checked = false; });
            document.querySelectorAll('#employeeTable tbody tr').forEach(function(tr) { tr.classList.remove('selected-row'); });
            
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

    // 2. 저장 전 사원 선택 여부 검증
    function validateForm() {
        var selectedEmpNo = document.getElementById('selectedEmpNo').value;
        if (!selectedEmpNo) {
            alert('좌측 목록에서 근태를 등록할 사원을 체크(선택)해 주세요.');
            return false;
        }
        return true;
    }

    // 3. 폼 리셋
    function resetForm() {
        document.getElementById('attendanceForm').reset();
        document.getElementById('attendanceId').value = ''; // 수정 모드 해제
        
        // 체크박스 상태에 따라 상단 텍스트 복구
        var checkedBox = document.querySelector('.emp-checkbox:checked');
        if(checkedBox) {
            document.getElementById('selectedEmpInfoDisplay').innerText = '선택된 사원: ' + checkedBox.dataset.name + ' (No-' + checkedBox.value + ')';
            document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        } else {
            document.getElementById('selectedEmpNo').value = '';
            document.getElementById('selectedEmpInfoDisplay').innerText = "[사원을 먼저 좌측 체크박스에서 선택하세요]";
            document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        }
        
        toggleVacationPeriod();
    }

    // 4. 모달 열기 및 Ajax 데이터 동적 로드
    function openModal(empNo, name, dept, position) {
        currentModalEmpNo = empNo;
        document.getElementById('recordModal').style.display = 'flex';
        document.getElementById('modalEmpInfo').innerText = '• 성명: ' + name + ' (No-' + empNo + ') • 부서: ' + dept + ' • 직위: ' + position;
        
        loadAttendanceList(empNo);
    }

    // 5. 서버에서 특정 사원의 근태 목록 가져오기
    function loadAttendanceList(empNo) {
        var tbody = document.getElementById('modalTableBody');
        tbody.innerHTML = '<tr><td colspan="8">데이터를 불러오는 중입니다...</td></tr>';

        fetch('${pageContext.request.contextPath}/attendance/list.do?empNo=' + empNo)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                tbody.innerHTML = '';
                if (!data || data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="8">등록된 근태 기록이 없습니다.</td></tr>';
                    return;
                }

                data.forEach(function(item, index) {
                    var tr = document.createElement('tr');
                    
                    var inputDate = item.inputDate || '';
                    var attendanceTypeName = item.attendanceTypeName || '';
                    var startDate = item.startDate || '';
                    var endDate = item.endDate || '';
                    var attendanceDays = item.attendanceDays || 0;
                    var amount = item.amount || 0;
                    var summary = item.summary || '';
                    var attendanceId = item.attendanceId;

                    tr.innerHTML = 
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + inputDate + '</td>' +
                        '<td>' + attendanceTypeName + '</td>' +
                        '<td>' + startDate + ' ~ ' + endDate + '</td>' +
                        '<td>' + attendanceDays + '</td>' +
                        '<td>' + amount + '</td>' +
                        '<td>' + summary + '</td>' +
                        '<td>' +
                            '<button type="button" class="btn-manage" onclick="editRecord(' + attendanceId + ', \'' + attendanceTypeName + '\', \'' + startDate + '\', \'' + endDate + '\', ' + attendanceDays + ', ' + amount + ', \'' + summary + '\')">🔄수정</button> ' +
                            '<button type="button" class="btn-delete" onclick="deleteRecord(' + attendanceId + ')">🗑️삭제</button>' +
                        '</td>';
                        
                    tbody.appendChild(tr);
                });
            })
            .catch(function(err) {
                tbody.innerHTML = '<tr><td colspan="8">데이터 조회 중 오류가 발생했습니다.</td></tr>';
            });
    }

    // 6. 모달 닫기
    function closeModal() {
        document.getElementById('recordModal').style.display = 'none';
    }

    // 7. 모달 내 '수정' 클릭 시 우측 폼으로 데이터 전달
    function editRecord(attId, type, start, end, days, amount, remark) {
        closeModal();
        
        // 데이터 ID 및 사원 번호 강제 세팅
        document.getElementById('attendanceId').value = attId;
        document.getElementById('selectedEmpNo').value = currentModalEmpNo;
        
        // 모달창 텍스트에서 이름만 추출하여 표시
        var modalTitle = document.getElementById('modalEmpInfo').innerText;
        var empName = modalTitle.split('• 성명: ')[1].split(' (')[0];
        document.getElementById('selectedEmpInfoDisplay').innerText = '[수정 모드] 선택된 사원: ' + empName + ' (No-' + currentModalEmpNo + ')';
        document.getElementById('selectedEmpInfoDisplay').style.color = '#e53935'; 
        
        var selectEl = document.getElementById('attendanceType');
        for(var i=0; i<selectEl.options.length; i++) {
            if(selectEl.options[i].text === type) {
                selectEl.selectedIndex = i;
                break;
            }
        }
        
        document.getElementById('startDate').value = start;
        document.getElementById('endDate').value = end;
        document.getElementById('attendanceDays').value = days;
        document.getElementById('wageAmount').value = amount;
        document.getElementById('remark').value = remark;
        
        toggleVacationPeriod();
        
        document.querySelector('.right-panel').style.outline = "2px solid #e53935";
        setTimeout(function() { document.querySelector('.right-panel').style.outline = "none"; }, 1000);
    }

    // 8. 모달 내 '삭제' 버튼 클릭 시 실시간 DB 삭제 처리
    function deleteRecord(attendanceId) {
        if (!confirm('해당 근태 기록을 정말 삭제하시겠습니까?')) return;

        fetch('${pageContext.request.contextPath}/attendance/delete.do?attendanceId=' + attendanceId, {
            method: 'POST'
        })
        .then(function(response) {
            if (response.ok) {
                alert('정상적으로 삭제되었습니다.');
                loadAttendanceList(currentModalEmpNo);
            } else {
                alert('삭제 처리에 실패했습니다.');
            }
        })
        .catch(function(err) { alert('서버 통신 오류가 발생했습니다.'); });
    }

    // [수정된 부분 3] 근태항목 선택 시 휴가기간 자동 표시 및 달력 제한, 단위 변경 토글 
    function toggleVacationPeriod() {
        var typeSelect = document.getElementById('attendanceType');
        var selectedOption = typeSelect.options[typeSelect.selectedIndex];
        
        var vacationRow = document.getElementById('vacationPeriodRow');
        var displayTd = document.getElementById('vacationPeriodDisplay');
        
        var startDateInput = document.getElementById('startDate');
        var endDateInput = document.getElementById('endDate');
        
        // 단위 텍스트 요소
        var unitTextSpan = document.getElementById('unitText');

        // option 태그에 숨겨둔 데이터를 꺼냅니다.
        var hasVacation = selectedOption.getAttribute('data-has-vacation') === 'true';
        var vName = selectedOption.getAttribute('data-vacation-name');
        var vStart = selectedOption.getAttribute('data-start');
        var vEnd = selectedOption.getAttribute('data-end');
        var unit = selectedOption.getAttribute('data-unit');
        
        // 단위가 '시간'이면 시간으로, 아니면 기본값인 '일'로 변경
        if (unit === '시간') {
            unitTextSpan.innerText = '시간';
        } else {
            unitTextSpan.innerText = '일';
        }
        
        // 선택한 근태항목에 연결된 휴가공제가 존재할 경우
        if (hasVacation && vStart && vEnd) {
            vacationRow.style.display = 'table-row';
            
            displayTd.innerText = vName + " (" + vStart + " ~ " + vEnd + ")";
            
            // 달력 선택 범위 강제 제한
            startDateInput.min = vStart;
            startDateInput.max = vEnd;
            endDateInput.min = vStart;
            endDateInput.max = vEnd;
            
            // 만약 이미 입력된 날짜가 제한 범위를 벗어났다면 범위 안으로 강제 조정
            if (startDateInput.value && (startDateInput.value < vStart || startDateInput.value > vEnd)) {
                startDateInput.value = vStart;
            }
            if (endDateInput.value && (endDateInput.value < vStart || endDateInput.value > vEnd)) {
                endDateInput.value = vEnd;
            }
        } else {
            // 연결된 휴가가 없는 일반 근태항목일 경우
            vacationRow.style.display = 'none';
            displayTd.innerText = "";
            
            // 일반 근태항목은 달력 제한 해제
            startDateInput.removeAttribute('min');
            startDateInput.removeAttribute('max');
            endDateInput.removeAttribute('min');
            endDateInput.removeAttribute('max');
        }
    }
</script>

</body>
</html>