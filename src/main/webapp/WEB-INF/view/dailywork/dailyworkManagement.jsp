<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>日雇い勤務記録/管理</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	font-family: 'Malgun Gothic', dotum, sans-serif;
	font-size: 13px;
	color: #333;
	margin: 0;
	background-color: #f5f5f5;
}

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

.btn-blue {
	background-color: #5c7cba;
	color: white;
	border: none;
	padding: 4px 8px;
	cursor: pointer;
	border-radius: 3px;
	font-size: 12px;
}

.btn-reset {
	background-color: #999;
	color: white;
	border: none;
	padding: 6px 15px;
	border-radius: 3px;
	cursor: pointer;
}

.btn-submit {
	background-color: #5c7cba;
	color: white;
	border: none;
	padding: 6px 20px;
	border-radius: 3px;
	cursor: pointer;
}

.form-table th {
	width: 120px;
	text-align: left;
	background-color: #fbfbfb;
}

.form-table td {
	text-align: left;
}

.form-table input[type="text"], .form-table input[type="date"],
	.form-table select {
	padding: 4px;
	border: 1px solid #ccc;
	width: 150px;
	text-align: right;
}

.auto-calc-row {
	background-color: #ffffe0;
}

.text-red {
	color: #d9534f;
}

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
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.project-list-ul {
	list-style: none;
	padding: 0;
	max-height: 200px;
	overflow-y: auto;
	border: 1px solid #ccc;
	margin-bottom: 10px;
}

.project-list-ul li {
	padding: 8px;
	border-bottom: 1px solid #eee;
	display: flex;
	justify-content: space-between;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="container">
		<div class="left-panel">
			<h2>日雇い勤務記録/管理</h2>
			<table id="employeeTable">
				<thead>
					<tr>
						<th>選択</th>
						<th>区分</th>
						<th>社員番号</th>
						<th>姓名</th>
						<th>部署</th>
						<th>勤務記録</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="emp" items="${empList}">
						<tr>
							<td><input type="checkbox" class="emp-checkbox"
								value="${emp.employeeId}" data-name="${emp.koreanName}"
								data-wage="${emp.basicPay}"></td>
							<td>${emp.employmentType}</td>
							<td>No-${emp.employeeId}</td>
							<td>${emp.koreanName}</td>
							<td>${emp.departmentName}</td>
							<td><button type="button" class="btn-blue"
									onclick="openWorkRecordModal('${emp.employeeId}', '${emp.koreanName}')">管理</button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="right-panel">
			<form id="dailyWorkForm"
				action="${pageContext.request.contextPath}/dailywork/save.do"
				method="post" onsubmit="return validateForm();">
				<input type="hidden" id="selectedEmpNo" name="employee_id">
				<input type="hidden" id="workId" name="work_id">

				<div id="selectedEmpInfoDisplay"
					style="margin-bottom: 10px; font-weight: bold; color: #5c7cba;">
					[社員を左側のチェックボックスから選択してください]</div>

				<table class="form-table">
					<tr style="border-top: 2px solid #000;">
						<th>勤務日付</th>
						<td><input type="date" name="work_date" required></td>
					</tr>
					<tr>
						<th>現場/プロジェクト</th>
						<td><select name="field_or_project_id" required>
								<option value="">選択してください。</option>
								<c:forEach var="prj" items="${projectList}">
									<option value="${prj.fieldOrProjectId}">${prj.projectName}</option>
								</c:forEach>
						</select>
							<button type="button" class="btn-blue"
								onclick="document.getElementById('projectModal').style.display='flex'">リスト管理</button>
						</td>
					</tr>
					<tr>
						<th>日当</th>
						<!-- [修正された部分 1] 初期値を110,000から0に変更 -->
						<td><input type="text" id="dailyWage" name="daily_wage"
							class="text-red calc-trigger" value="0" required> ウォン</td>
					</tr>
					<tr>
						<th>支給率</th>
						<td><input type="text" id="paymentRate" name="payment_rate"
							class="text-red calc-trigger" value="1.0" required></td>
					</tr>
					<tr class="auto-calc-row">
						<th>所得税</th>
						<td><input type="text" id="incomeTax" name="income_tax"
							class="text-red" readonly></td>
					</tr>
					<tr class="auto-calc-row">
						<th>住民税</th>
						<td><input type="text" id="localTax" name="local_tax"
							class="text-red" readonly></td>
					</tr>
					<tr class="auto-calc-row">
						<th>実支給額</th>
						<td><input type="text" id="actualPayment"
							name="actual_payment" class="text-red" readonly></td>
					</tr>
				</table>

				<div style="text-align: center; margin-top: 20px;">
					<button type="submit" class="btn-submit">保存</button>
					<button type="button" class="btn-reset" onclick="resetWorkForm()">内容クリア</button>
				</div>
			</form>
		</div>
	</div>

	<!-- 現場/プロジェクトリスト管理モーダル -->
	<div id="projectModal" class="modal-overlay">
		<div class="modal-content" style="width: 400px;">
			<div style="font-size: 18px; font-weight: bold; margin-bottom: 15px;">現場/プロジェクト管理</div>
			<ul class="project-list-ul">
				<c:forEach var="prj" items="${projectList}">
					<li><span>${prj.projectName}</span>
						<button type="button"
							onclick="deleteProject(${prj.fieldOrProjectId})"
							style="background: none; border: none; color: red; cursor: pointer;">削除</button>
					</li>
				</c:forEach>
			</ul>
			<div id="addProjectDiv" style="display: none; margin-bottom: 10px;">
				<input type="text" id="newProjectName" placeholder="新プロジェクト名入力">
				<button type="button" class="btn-blue" onclick="addProject()">保存</button>
				<button type="button" class="btn-reset"
					onclick="document.getElementById('addProjectDiv').style.display='none'">キャンセル</button>
			</div>
			<button type="button" class="btn-blue"
				style="background-color: #666;"
				onclick="document.getElementById('addProjectDiv').style.display='block'">+
				追加する</button>
			<div
				style="text-align: center; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px;">
				<button type="button" class="btn-blue" onclick="resetProjects()">初期化</button>
				<button type="button" class="btn-reset"
					onclick="document.getElementById('projectModal').style.display='none'">閉じる</button>
			</div>
		</div>
	</div>

	<!-- 社員別勤務記録リストモーダル -->
	<div id="workRecordModal" class="modal-overlay">
		<div class="modal-content" style="width: 900px;">
			<div
				style="font-size: 18px; font-weight: bold; margin-bottom: 15px; display: flex; justify-content: space-between;">
				<span>社員別勤務記録</span>
				<button
					onclick="document.getElementById('workRecordModal').style.display='none'"
					style="cursor: pointer; font-size: 20px; background: none; border: none;">×</button>
			</div>
			<div
				style="display: flex; justify-content: space-between; margin-bottom: 10px;">
				<div id="modalWorkEmpInfo"
					style="font-weight: bold; color: #5c7cba;"></div>
				<div>
					<select id="searchYear" onchange="loadWorkRecordList()"></select> 年
					<select id="searchMonth" onchange="loadWorkRecordList()"></select>
					月
				</div>
			</div>
			<table>
				<thead>
					<tr>
						<th>勤務日付</th>
						<th>現場/プロジェクト</th>
						<th>日当</th>
						<th>支給率</th>
						<th>所得税</th>
						<th>住民税</th>
						<th>実支給額</th>
						<th>編集/削除</th>
					</tr>
				</thead>
				<tbody id="modalWorkTableBody">
				</tbody>
			</table>
		</div>
	</div>

	<script>
    document.querySelectorAll('.emp-checkbox').forEach(cb => {
        cb.addEventListener('change', function() {
            document.querySelectorAll('.emp-checkbox').forEach(otherCb => { if(otherCb !== cb) otherCb.checked = false; });
            document.querySelectorAll('#employeeTable tbody tr').forEach(tr => tr.classList.remove('selected-row'));
            
            if(this.checked) {
                this.closest('tr').classList.add('selected-row');
                document.getElementById('selectedEmpNo').value = this.value; 
                document.getElementById('selectedEmpInfoDisplay').innerText = '選択された社員: ' + this.dataset.name + ' (No-' + this.value + ')';
                
                let empWage = this.getAttribute('data-wage');
                
                // [修正された部分 2] DBデータがなければ基本値0にセッティング
                if (empWage && empWage !== '0' && empWage !== '') {
                    document.getElementById('dailyWage').value = formatComma(empWage);
                } else {
                    document.getElementById('dailyWage').value = '0';
                }
                calculatePay();

            } else {
                resetWorkForm();
            }
        });
    });

    function validateForm() {
        if (!document.getElementById('selectedEmpNo').value) {
            alert('左側のリストから勤務を記録する社員を選択してください。');
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
        let localTax = Math.floor(incomeTax * 0.1);    // 所得税の10%
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
        document.getElementById('selectedEmpInfoDisplay').innerText = "[社員を左側のチェックボックスから選択してください]";
        document.getElementById('selectedEmpInfoDisplay').style.color = '#5c7cba';
        
        // [修正された部分 3] フォーム初期化時に基本値を0にロールバック後、再計算
        document.getElementById('dailyWage').value = '0';
        calculatePay();
    }

    // --- 勤務記録モーダルリスト (年月フィルター / 照会 / 編集 / 削除) ---
    let currentWorkEmpNo = '';
    
    function openWorkRecordModal(empNo, empName) {
        currentWorkEmpNo = empNo;
        document.getElementById('workRecordModal').style.display = 'flex';
        document.getElementById('modalWorkEmpInfo').innerText = '• 姓名: ' + empName + ' (No-' + empNo + ')';
        
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
        tbody.innerHTML = '<tr><td colspan="8">読み込み中です...</td></tr>';

        fetch('${pageContext.request.contextPath}/dailywork/list.do?empNo=' + currentWorkEmpNo + '&yearMonth=' + year + '-' + month)
            .then(res => res.json())
            .then(data => {
                tbody.innerHTML = '';
                if (!data || data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="8">該当月の勤務記録がありません。</td></tr>';
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
                                onclick="editWorkRecord(\${item.workId}, '\${item.workDate}', \${item.fieldProjectId}, \${item.dailyWage}, \${item.paymentRate})">編集</button>
                            <button type="button" style="border: none; padding: 2px 6px; cursor: pointer; background: #d9534f; color:#fff;" 
                                onclick="deleteWorkRecord(\${item.workId})">削除</button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            })
            .catch(err => { tbody.innerHTML = '<tr><td colspan="8">データ照会エラー</td></tr>'; });
    }

    function editWorkRecord(workId, workDate, projectId, wage, rate) {
        document.getElementById('workRecordModal').style.display = 'none'; 
        
        document.getElementById('workId').value = workId; 
        document.getElementById('selectedEmpNo').value = currentWorkEmpNo;
        
        let empName = document.getElementById('modalWorkEmpInfo').innerText.split('姓名: ')[1].split(' (')[0];
        document.getElementById('selectedEmpInfoDisplay').innerText = '[編集モード] 選択された社員: ' + empName + ' (No-' + currentWorkEmpNo + ')';
        document.getElementById('selectedEmpInfoDisplay').style.color = '#d9534f';

        document.querySelector('input[name="work_date"]').value = workDate;
        document.querySelector('select[name="field_or_project_id"]').value = projectId;
        document.getElementById('dailyWage').value = formatComma(wage);
        document.getElementById('paymentRate').value = rate;

        calculatePay(); 
    }

    function deleteWorkRecord(workId) {
        if(!confirm("この勤務記録を本当に削除しますか？")) return;
        fetch('${pageContext.request.contextPath}/dailywork/delete.do?workId=' + workId, { method: 'POST' })
        .then(res => res.text())
        .then(res => {
            if(res === 'success') { alert('削除されました。'); loadWorkRecordList(); }
            else { alert('削除失敗'); }
        });
    }

    function addProject() {
        const name = document.getElementById('newProjectName').value.trim();
        if(!name) { alert("プロジェクト名を入力してください。"); return; }
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=add&projectName=' + encodeURIComponent(name)
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("追加失敗");
        });
    }

    function deleteProject(id) {
        if(!confirm("この項目をリストから削除しますか？")) return;
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'action=delete&projectId=' + id
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("削除失敗");
        });
    }

    function resetProjects() {
        if(!confirm("すべての現場/プロジェクトを空にしますか？")) return;
        fetch('${pageContext.request.contextPath}/dailywork/projectManage.do', {
            method: 'POST', headers: {'Content-Type': 'application/x-www-form-urlencoded'}, body: 'action=reset'
        }).then(res => res.text()).then(res => {
            if(res === 'success') location.reload(); else alert("初期化失敗");
        });
    }
</script>
</body>
</html>