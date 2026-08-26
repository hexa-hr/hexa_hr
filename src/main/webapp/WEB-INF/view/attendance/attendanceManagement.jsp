<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>勤怠記録/管理</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
/* 1. 전체 레이아웃 (공통) */
body {
    margin: 0;
    min-width: 1400px;
    background-color: white; /* 전체 배경색 통일 */
    font-family: 'Malgun Gothic', sans-serif;
    color: #333;
}

/* 2. 컨테이너 영역 */
.container {
    display: flex;
    gap: 30px;
    padding: 30px 40px; /* 좌우 여백을 본문과 통일 */
    background-color: transparent; /* 배경색은 body를 따르거나, 필요시 투명 */
    box-sizing: border-box;
    align-items: flex-start;
}

/* 3. 좌/우 패널 레이아웃 분할 */
.left-panel {
    flex: 6;
    background-color: white; /* 테이블 영역 흰색 강조 */
    padding: 20px;
    box-sizing: border-box;
    /* overflow-y: auto; 와 max-height는 화면 전체 스크롤을 고려해 제거하거나 필요시 유지 */
}

.right-panel {
    flex: 4;
    background: #f4f4f4; /* 폼 영역을 사이드바/설정폼과 동일한 배경색 톤으로 변경 */
    padding: 20px;
    border: 1px solid #ddd;
    box-sizing: border-box;
}

/* 4. 타이틀 영역 (이전 페이지들과 완벽 동일) */
h2 {
    font-size: 22px; /* 다른 페이지의 h1, section-title 크기와 통일 */
    font-weight: bold;
    margin-top: 0;
    margin-bottom: 20px;
    color: #333;
    border-bottom: 2px solid #4e73df; /* 파란색 밑줄 통일 */
    padding-bottom: 10px;
}

/* 5. 데이터 테이블 스타일 (왼쪽 패널 - 사원명부 스타일과 동일) */
table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
    text-align: center;
}

th, td {
    border: 1px solid #ccc; /* 테두리 색상 통일 */
    padding: 10px;
    font-size: 14px;
    white-space: nowrap; /* 셀 내용 줄바꿈 방지 */
}

th {
    background-color: #f8f9fa; /* 헤더 배경색 통일 */
    color: #333;
    font-weight: bold;
}

td {
    background-color: white;
}

/* 클릭/선택 가능한 행(Row) 하이라이트 */
.selected-row td {
    background-color: #f1f5f9 !important; /* 다른 테이블 호버/선택 색상과 통일 (#e8f0fe -> #f1f5f9) */
    font-weight: bold;
}

/* 6. 폼 테이블 스타일 (오른쪽 패널) */
.form-table {
    margin-top: 15px;
}

.form-table th {
    width: 120px;
    text-align: left;
    background-color: transparent; /* 폼 영역 배경색(#f4f4f4)과 자연스럽게 어울리도록 투명 처리 */
    border: none; /* 폼 안쪽은 선 없이 깔끔하게 */
    padding: 8px 0;
}

.form-table td {
    text-align: left;
    border: none;
    padding: 8px 0;
    background-color: transparent;
}

/* 입력 필드 (이전 폼 스타일과 통일) */
input[type="date"], input[type="text"], input[type="number"], select {
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 3px;
    font-size: 14px;
    box-sizing: border-box;
}

input[type="number"] { width: 100px; } /* 숫자는 폭 고정 */

/* 7. 버튼 영역 (공통 톤앤매너) */
.btn-manage {
    background: #4e73df; /* 파란색 메인 버튼 통일 */
    color: white !important;
    border: none;
    padding: 4px 10px;
    cursor: pointer;
    border-radius: 3px;
    font-size: 12px;
    font-weight: bold;
}

.btn-manage:hover {
    background: #2e59d9;
}

.btn-delete {
    background: #a5a5a5; /* 회색 서브 버튼으로 톤다운 통일 (너무 튀는 빨간색 방지, 모달에서 사용됨) */
    color: white;
    border: none;
    padding: 4px 10px;
    cursor: pointer;
    border-radius: 3px;
    font-size: 12px;
    font-weight: bold;
}

.btn-delete:hover {
    background: #858796;
}

/* 폼 하단 제출/초기화 버튼 */
.btn-submit {
    background-color: #4e73df;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 3px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}

.btn-submit:hover {
    background-color: #2e59d9;
}

.btn-reset {
    background-color: #a5a5a5;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 3px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    margin-left: 5px;
}

.btn-reset:hover {
    background-color: #858796;
}

/* 8. 모달 팝업 스타일 */
.modal-overlay {
    display: none;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 1000; /* 네비게이션(1000) 위로 올라오도록 조정 */
    justify-content: center;
    align-items: center;
}

.modal-content {
    background: white;
    width: 850px;
    padding: 30px;
    border-radius: 5px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    border-bottom: 2px solid #4e73df; /* 모달 타이틀도 파란 줄 통일 */
    padding-bottom: 10px;
}

.btn-close-modal {
    cursor: pointer;
    font-size: 24px;
    background: none;
    border: none;
    color: #999;
}
.btn-close-modal:hover {
    color: #333;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="container">
		<!-- 左側のリスト領域 -->
		<div class="left-panel">
			<h2>勤怠記録/管理</h2>
			<table id="employeeTable">
				<thead>
					<tr>
						<th>選択</th>
						<th>区分</th>
						<th>社員番号</th>
						<th>姓名</th>
						<th>部署</th>
						<th>職位</th>
						<th>勤怠記録</th>
					</tr>
				</thead>
				<tbody>
					<!-- DB動的リスト照会のためのJSTL繰り返し文 -->
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
									onclick="openModal('${emp.employeeId}', '${emp.koreanName}', '${emp.departmentName}', '${emp.positionName}')">管理</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- 右側のフォーム領域 -->
		<div class="right-panel">
			<form id="attendanceForm"
				action="${pageContext.request.contextPath}/attendance/save.do"
				method="post" onsubmit="return validateForm();">

				<input type="hidden" id="selectedEmpNo" name="employee_id">
				<input type="hidden" id="attendanceId" name="attendance_id">

				<!-- [修正] valueを空にしてJSが今日の日付を設定するように変更 -->
				<input type="hidden" id="inputDate" name="input_date">

				<div id="selectedEmpInfoDisplay"
					style="margin-bottom: 10px; font-weight: bold; color: #5c7cba;">
					[社員を左側のチェックボックスから選択してください]</div>

				<table class="form-table">
					<tr style="border-top: 2px solid #000;">
						<th>入力日</th>
						<!-- [修正] 固定テキスト削除およびJSで連結 -->
						<td id="currentDateDisplay"></td>
					</tr>
					<tr>
						<th>勤怠項目</th>
						<td><select id="attendanceType" name="attendance_type_id"
							onchange="toggleVacationPeriod()" required>
								<option value="" data-has-vacation="false" data-unit="日">選択してください。</option>
								<c:forEach var="att" items="${attendanceList}">
									<option value="${att.attendanceTypeId}"
										data-has-vacation="${not empty att.vacationTypeId and att.vacationTypeId != 0 ? 'true' : 'false'}"
										data-vacation-name="${att.vacationTypeName}"
										data-start="<fmt:formatDate value='${att.applyPeriod1}' pattern='yyyy-MM-dd'/>"
										data-end="<fmt:formatDate value='${att.applyPeriod2}' pattern='yyyy-MM-dd'/>"
										data-unit="${att.unit}" data-att-usage="${att.usage}"
										data-vac-usage="${att.vacationUsage}">
										${att.attendanceTypeName}</option>
								</c:forEach>
						</select></td>
					</tr>
					<tr id="vacationPeriodRow" style="display: none;">
						<th style="color: #e53935;">休暇適用期間</th>
						<!-- [修正] 固定テキスト削除およびid(vacationPeriodDisplay)付与 -->
						<td id="vacationPeriodDisplay"
							style="color: #e53935; font-weight: bold;"></td>
					</tr>
					<tr>
						<th>期間</th>
						<td><input type="date" id="startDate" name="start_date"
							required> ~ <input type="date" id="endDate"
							name="end_date" required></td>
					</tr>
					<tr>
						<th>勤怠日数</th>
						<td><input type="number" id="attendanceDays"
							name="attendance_days" min="0" step="0.5" value="1" required>
							<span id="unitText">日</span></td>
					</tr>
					<tr>
						<th>金額(手当)</th>
						<td><input type="number" id="wageAmount" name="amount"
							value="0"> ウォン</td>
					</tr>
					<tr>
						<th>摘要</th>
						<td><input type="text" id="remark" name="summary"
							style="width: 80%;"></td>
					</tr>
				</table>
				<div style="text-align: center; margin-top: 20px;">
					<button type="submit" class="btn-submit">保存</button>
					<button type="button" class="btn-reset" onclick="resetForm();">内容クリア</button>
				</div>
			</form>
		</div>
	</div>

	<!-- モーダルポップアップ領域 -->
	<div id="recordModal" class="modal-overlay">
		<div class="modal-content">
			<div class="modal-header">
				<span>社員別勤怠記録</span>
				<button class="btn-close-modal" onclick="closeModal()">×</button>
			</div>
			<div style="margin-bottom: 10px; font-weight: bold;"
				id="modalEmpInfo">
				<!-- JSで名前、部署、職位を入力 -->
			</div>
			<table>
				<thead>
					<tr>
						<th>番号</th>
						<th>入力日</th>
						<th>勤怠項目</th>
						<th>勤怠期間</th>
						<th>勤怠日数</th>
						<th>金額</th>
						<th>摘要</th>
						<th>編集/削除</th>
					</tr>
				</thead>
				<tbody id="modalTableBody">
					<!-- 動的レンダリング領域 -->
				</tbody>
			</table>
		</div>
	</div>

	<script>
    var currentModalEmpNo = '';

    // [追加] 今日の日付自動セッティング関数
    function setTodayDate() {
        var today = new Date();
        var yyyy = today.getFullYear();
        var mm = String(today.getMonth() + 1).padStart(2, '0');
        var dd = String(today.getDate()).padStart(2, '0');
        var formattedDate = yyyy + '-' + mm + '-' + dd;
        
        var inputDateEl = document.getElementById('inputDate');
        var displayEl = document.getElementById('currentDateDisplay');
        
        if(inputDateEl) inputDateEl.value = formattedDate;
        if(displayEl) displayEl.innerText = formattedDate;
    }

    // DOMロード時に即時今日の日付をセッティング
    document.addEventListener("DOMContentLoaded", function() {
        setTodayDate();
    });

    // 1. チェックボックス選択時に社員番号保存およびUI表示
    var checkboxes = document.querySelectorAll('.emp-checkbox');
    checkboxes.forEach(function(cb) {
        cb.addEventListener('change', function() {
            checkboxes.forEach(function(otherCb) { if(otherCb !== cb) otherCb.checked = false; });
            document.querySelectorAll('#employeeTable tbody tr').forEach(function(tr) { tr.classList.remove('selected-row'); });
            
            if(this.checked) {
                this.closest('tr').classList.add('selected-row');
                document.getElementById('selectedEmpNo').value = this.value; 
                document.getElementById('selectedEmpInfoDisplay').innerText = '選択された社員: ' + this.dataset.name + ' (No-' + this.value + ')';
            } else {
                document.getElementById('selectedEmpNo').value = "";
                document.getElementById('selectedEmpInfoDisplay').innerText = "[社員を左側のチェックボックスから選択してください]";
            }
        });
    });

    // 2. 保存前に社員選択有無を検証
    function validateForm() {
        var selectedEmpNo = document.getElementById('selectedEmpNo').value;
        if (!selectedEmpNo) {
            alert('左側のリストから勤怠を登録する社員をチェック（選択）してください。');
            return false;
        }
        return true;
    }

    // 3. フォームリセット
    function resetForm() {
        document.getElementById('attendanceForm').reset();
        document.getElementById('attendanceId').value = ''; // 編集モード解除
        
        // チェックボックスの状態に応じて上部テキストを復元
        var checkedBox = document.querySelector('.emp-checkbox:checked');
        if(checkedBox) {
            document.getElementById('selectedEmpInfoDisplay').innerText = '選択された社員: ' + checkedBox.dataset.name + ' (No-' + checkedBox.value + ')';
            document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        } else {
            document.getElementById('selectedEmpNo').value = '';
            document.getElementById('selectedEmpInfoDisplay').innerText = "[社員を左側のチェックボックスから選択してください]";
            document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        }
        
        setTodayDate(); // 初期化時にも固定値の代わりに無条件で今日の日付を維持
        toggleVacationPeriod();
    }

    // 4. モーダルを開き、Ajaxデータを動的ロード
    function openModal(empNo, name, dept, position) {
        currentModalEmpNo = empNo;
        document.getElementById('recordModal').style.display = 'flex';
        document.getElementById('modalEmpInfo').innerText = '• 姓名: ' + name + ' (No-' + empNo + ') • 部署: ' + dept + ' • 職位: ' + position;
        
        loadAttendanceList(empNo);
    }

    // 5. サーバーから特定の社員の勤怠リストを取得
    function loadAttendanceList(empNo) {
        var tbody = document.getElementById('modalTableBody');
        tbody.innerHTML = '<tr><td colspan="8">データを読み込み中です···</td></tr>';

        fetch('${pageContext.request.contextPath}/attendance/list.do?empNo=' + empNo)
            .then(function(response) { return response.json(); })
            .then(function(data) {
                tbody.innerHTML = '';
                if (!data || data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="8">登録された勤怠記録はありません。</td></tr>';
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
                            '<button type="button" class="btn-manage" onclick="editRecord(' + attendanceId + ', \'' + attendanceTypeName + '\', \'' + startDate + '\', \'' + endDate + '\', ' + attendanceDays + ', ' + amount + ', \'' + summary + '\')">🔄編集</button> ' +
                            '<button type="button" class="btn-delete" onclick="deleteRecord(' + attendanceId + ')">🗑️削除</button>' +
                        '</td>';
                        
                    tbody.appendChild(tr);
                });
            })
            .catch(function(err) {
                tbody.innerHTML = '<tr><td colspan="8">データ照会中にエラーが発生しました。</td></tr>';
            });
    }

    // 6. モーダルを閉じる
    function closeModal() {
        document.getElementById('recordModal').style.display = 'none';
    }

    // 7. モーダル内の「編集」クリック時に右側のフォームへデータを伝達
    function editRecord(attId, type, start, end, days, amount, remark) {
        closeModal();
        
        // データIDおよび社員番号の強制セッティング
        document.getElementById('attendanceId').value = attId;
        document.getElementById('selectedEmpNo').value = currentModalEmpNo;
        
        // モーダル画面のテキストから名前のみを抽出して表示
        var modalTitle = document.getElementById('modalEmpInfo').innerText;
        var empName = modalTitle.split('• 姓名: ')[1].split(' (')[0];
        document.getElementById('selectedEmpInfoDisplay').innerText = '[編集モード] 選択された社員: ' + empName + ' (No-' + currentModalEmpNo + ')';
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

    // 8. モーダル内の「削除」ボタンクリック時にリアルタイムDB削除処理
    function deleteRecord(attendanceId) {
        if (!confirm('この勤怠記録を本当に削除しますか？')) return;

        fetch('${pageContext.request.contextPath}/attendance/delete.do?attendanceId=' + attendanceId, {
            method: 'POST'
        })
        .then(function(response) {
            if (response.ok) {
                alert('正常に削除されました。');
                loadAttendanceList(currentModalEmpNo);
            } else {
                alert('削除処理に失敗しました。');
            }
        })
        .catch(function(err) { alert('サーバー通信エラーが発生しました。'); });
    }

    // [追加] 消えていた期間制限および単位表示の動的関数を復旧
    function toggleVacationPeriod() {
        var typeSelect = document.getElementById('attendanceType');
        var selectedOption = typeSelect.options[typeSelect.selectedIndex];
        
        var vacationRow = document.getElementById('vacationPeriodRow');
        var displayTd = document.getElementById('vacationPeriodDisplay');
        
        var startDateInput = document.getElementById('startDate');
        var endDateInput = document.getElementById('endDate');
        
        // 単位テキスト要素
        var unitTextSpan = document.getElementById('unitText');

        if (!selectedOption) return; // 防御コード

        // optionタグに隠しておいたデータを取り出します。
        var hasVacation = selectedOption.getAttribute('data-has-vacation') === 'true';
        var vName = selectedOption.getAttribute('data-vacation-name');
        var vStart = selectedOption.getAttribute('data-start');
        var vEnd = selectedOption.getAttribute('data-end');
        var unit = selectedOption.getAttribute('data-unit');
        
        // 追加された使用有無データ
        var attUsage = selectedOption.getAttribute('data-att-usage');
        var vacUsage = selectedOption.getAttribute('data-vac-usage');
        
        // 単位が「時間」なら時間、そうでなければデフォルトの「日」に変更
        if (unit === '時間') {
            if(unitTextSpan) unitTextSpan.innerText = '時間';
        } else {
            if(unitTextSpan) unitTextSpan.innerText = '日';
        }
        
        // 両方「使用」（またはDB値「Y」）状態か確認
        var isBothUsed = (attUsage === 'Y' || attUsage === '使用') && (vacUsage === 'Y' || vacUsage === '使用');
        
        // 選択した勤怠項目に連結された休暇控除が存在し、両方「使用」の場合
        if (hasVacation && vStart && vEnd && isBothUsed) {
            if(vacationRow) vacationRow.style.display = 'table-row';
            
            if(displayTd) displayTd.innerText = vName + " (" + vStart + " ~ " + vEnd + ")";
            
            // カレンダー選択範囲を強制制限
            if(startDateInput) {
                startDateInput.min = vStart;
                startDateInput.max = vEnd;
            }
            if(endDateInput) {
                endDateInput.min = vStart;
                endDateInput.max = vEnd;
            }
            
            if (startDateInput && startDateInput.value && (startDateInput.value < vStart || startDateInput.value > vEnd)) {
                startDateInput.value = vStart;
            }
            if (endDateInput && endDateInput.value && (endDateInput.value < vStart || endDateInput.value > vEnd)) {
                endDateInput.value = vEnd;
            }
        } else {
            // 連結された休暇がない一般勤怠項目、または使用しない場合
            if(vacationRow) vacationRow.style.display = 'none';
            if(displayTd) displayTd.innerText = "";
            
            // 一般勤怠項目はカレンダー制限を解除
            if(startDateInput) {
                startDateInput.removeAttribute('min');
                startDateInput.removeAttribute('max');
            }
            if(endDateInput) {
                endDateInput.removeAttribute('min');
                endDateInput.removeAttribute('max');
            }
        }
    }
</script>

</body>
</html>