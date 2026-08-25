<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>退職給与入力/管理</title>
<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	font-size: 12px;
	color: #333;
	background-color: #f5f5f5;
	margin: 0;
	padding: 20px;
}

.container {
	background: #fff;
	padding: 20px;
	box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
	width: 100%;
	max-width: 1200px;
	margin: 0 auto;
}

h2 {
	font-size: 20px;
	margin-bottom: 5px;
}

.sub-desc {
	color: #666;
	margin-bottom: 20px;
}

table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	margin-bottom: 5px;
}

th, td {
	border: 1px solid #ddd;
	padding: 8px;
}

th {
	background-color: #f9f9f9;
	color: #3b5998;
	font-weight: bold;
}

.table-hover tr:hover {
	background-color: #f1f5fa;
	cursor: pointer;
}

.selected-row {
	background-color: #3b5998 !important;
	color: white;
}

.calc-header-bar {
	background-color: #555;
	color: white;
	padding: 10px;
	display: flex;
	align-items: center;
	gap: 15px;
	font-weight: bold;
	margin-bottom: 15px;
}

.calc-header-bar .emp-name {
	font-size: 16px;
	color: #ffeb3b;
	width: 200px;
}

.calc-header-bar input {
	padding: 4px;
	text-align: center;
	font-size: 12px;
}

.highlight-box {
	background-color: #ffeb3b;
	color: black;
	padding: 4px 10px;
	border-radius: 3px;
}

.flex-row {
	display: flex;
	gap: 20px;
	margin-bottom: 20px;
}

.flex-col {
	flex: 1;
}

.section-title {
	font-weight: bold;
	font-size: 14px;
	margin-bottom: 10px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.btn {
	padding: 5px 15px;
	font-size: 12px;
	border: none;
	cursor: pointer;
	border-radius: 3px;
	color: white;
}

.btn-blue {
	background-color: #5c7cba;
}

.btn-orange {
	background-color: #e5502c;
	font-size: 14px;
	padding: 10px 30px;
	font-weight: bold;
	display: block;
	margin: 20px auto;
}

input[type="text"], input[type="number"] {
	width: 90%;
	padding: 5px;
	border: 1px solid #ccc;
	text-align: right;
}

.text-center {
	text-align: center !important;
}

.text-red {
	color: #d9534f;
	font-weight: bold;
}

.result-table th {
	background-color: #f4f8fe;
	color: #333;
}

.result-table td {
	background-color: #ffffec;
}

.final-table th {
	background-color: #000;
	color: white;
}

.note-text {
	font-size: 11px;
	color: #d9534f;
	margin-bottom: 15px;
}
</style>
</head>
<body>

	<div class="container">
		<h2>退職給与入力/管理</h2>
		<p class="sub-desc">退職社員に対する退職給与情報を入力、保存、管理するメニューです。選択した社員の退職金内訳が自動的に計算されます。</p>

		<!-- 退職社員リスト -->
		<table class="table-hover" id="employeeListTable"
			style="margin-bottom: 15px;">
			<thead>
				<tr>
					<th>支給日</th>
					<th>区分</th>
					<th>姓名</th>
					<th>職位</th>
					<th>部署</th>
					<th>算定期間</th>
					<th>勤続日数</th>
					<th>実支給額</th>
					<th>支給方法</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="emp" items="${retiredList}">
					<tr
						onclick="selectEmployee(this, ${emp.employeeId}, '${emp.empName}', '${emp.positionName}', '${emp.hireDate}', '${emp.resignationDate}', '${emp.bankName} ${emp.accountNumber}')">
						<td>0000-00-00</td>
						<td>退職精算</td>
						<td>${emp.empName}</td>
						<td>${emp.positionName}</td>
						<td>${emp.deptName}</td>
						<td class="td-period"></td>
						<td class="td-days"></td>
						<td class="td-actual"></td>
						<td class="td-method">${empty emp.accountNumber ? '直接入力' : emp.bankName}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<!-- 退職計算ヘッダーバー -->
		<div class="calc-header-bar">
			<div class="emp-name" id="calcEmpNameTitle">社員を選択してください</div>
			<div>
				区分 <select><option>退職精算</option></select>
			</div>
			<div>
				入社日 <input type="date" id="hireDate" readonly> ~ 退職日 <input
					type="date" id="resignationDate" readonly>
			</div>
			<div>
				勤続年数 <span class="highlight-box" id="yearsOfService">0年</span>
			</div>
			<div>
				勤続日数 <span class="highlight-box" id="daysOfService">0日</span>
			</div>
			<div>
				除外日数 <input type="number" id="excludedDays" value="0"
					style="width: 50px;" onchange="calculateDays()">日
			</div>
		</div>

		<!-- 中間領域: 給与内訳 & その他課税所得 -->
		<div class="flex-row">
			<!-- 左側: 給与内訳 -->
			<div class="flex-col">
				<div class="section-title">
					給与内訳 <span
						style="font-size: 11px; font-weight: normal; color: #666;">(事由発生日以前の直近3ヶ月)
						支給合計金額</span>
					<button type="button" class="btn btn-blue"
						onclick="loadSalaryData()">給与内訳読み込み</button>
				</div>
				<table>
					<thead>
						<tr>
							<th>算定期間</th>
							<th>算定日数</th>
							<th>給与総額</th>
						</tr>
					</thead>
					<tbody id="salaryListBody">
						<!-- 基本的に4つの空の行を表示しておきます -->
						<c:forEach begin="1" end="4">
							<tr>
								<td class="text-center"><input type="text"
									class="text-center" style="width: 40%;" readonly> ~ <input
									type="text" class="text-center" style="width: 40%;" readonly>
								</td>
								<td><input type="number" class="text-center calc-days"
									value="0" readonly></td>
								<td><input type="text" class="calc-amount" value="0"
									readonly></td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td class="text-center"
								style="background-color: #ffffec; font-weight: bold;">総合計</td>
							<td style="background-color: #ffffec;"><input type="text"
								id="totalCalcDays" class="text-center" readonly
								style="background: transparent; border: none; font-weight: bold;"
								value="0"></td>
							<td style="background-color: #ffffec;"><input type="text"
								id="displayTotalSalary" value="0" readonly
								style="background: transparent; border: none; font-weight: bold;">
							</td>
						</tr>
					</tfoot>
				</table>
				<div class="note-text">但し、中間日付の計算日の場合、該当月の支給合計金額から日数で割った値を基本として表示</div>
			</div>

			<!-- 右側: その他課税所得 (ダミー) -->
			<div class="flex-col">
				<div class="section-title">
					その他課税所得 <span
						style="font-size: 11px; font-weight: normal; color: #666;">(事由発生日以前1年分の金額入力)</span>
				</div>
				<table>
					<thead>
						<tr>
							<th>支給年月</th>
							<th>支給項目</th>
							<th>金額</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="4">
							<tr>
								<td><input type="text"></td>
								<td><input type="text"></td>
								<td><input type="text" value="0" class="text-center"></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<!-- 追加手当および控除領域 -->
		<div class="flex-row">
			<div class="flex-col">
				<table>
					<tr>
						<th>退職慰労金</th>
						<th>解雇予告手当</th>
					</tr>
					<tr>
						<td><input type="text" id="consolationPay" value="0"
							oninput="formatNumber(this)"></td>
						<td><input type="text" id="dismissalPay" value="0"
							oninput="formatNumber(this)"></td>
					</tr>
				</table>
			</div>
			<div class="flex-col">
				<table>
					<tr>
						<th>非課税退職給与</th>
						<th>既納付税額</th>
						<th>税額控除</th>
					</tr>
					<tr>
						<td><input type="text" value="0"></td>
						<td><input type="text" value="0"></td>
						<td><input type="text" value="0"></td>
					</tr>
				</table>
			</div>
		</div>

		<!-- 課税繰延口座 -->
		<div class="section-title" style="margin-top: 20px;">
			課税繰延口座 <span
				style="font-size: 11px; font-weight: normal; color: #666;">(該当しない場合は入力しません。)</span>
		</div>
		<table>
			<tr>
				<th>退職年金事業者名</th>
				<th>事業者登録番号</th>
				<th>口座番号</th>
				<th>入金(振替)日</th>
				<th>口座入金金額</th>
			</tr>
			<tr>
				<td><input type="text"></td>
				<td><input type="text"></td>
				<td><input type="text" id="bankAccountInfo"
					placeholder="銀行名および口座番号"></td>
				<td><input type="date"></td>
				<td><input type="text" value="0"></td>
			</tr>
		</table>

		<!-- 退職金計算ボタン -->
		<button type="button" class="btn btn-orange"
			onclick="calculateRetirementPay()">退職金計算</button>

		<!-- 最終計算結果テーブル -->
		<table class="result-table">
			<tr>
				<th>3ヶ月総計</th>
				<th>1日平均賃金</th>
				<th>1日通常賃金</th>
				<th>退職所得</th>
				<th>算出税額</th>
			</tr>
			<tr>
				<td id="res_3month">0</td>
				<td id="res_dailyAvg">0</td>
				<td><input type="text" value="0" class="text-center"></td>
				<td id="res_retirementIncome">0</td>
				<td id="res_calcTax">0</td>
			</tr>
			<tr>
				<th>退職所得税</th>
				<th>住民税</th>
				<th>繰延退職所得税</th>
				<th>繰延住民税</th>
				<th>その他控除</th>
			</tr>
			<tr>
				<td id="res_incomeTax">0</td>
				<td id="res_localTax">0</td>
				<td>0</td>
				<td>0</td>
				<td>0</td>
			</tr>
		</table>

		<!-- 最終受領額テーブル -->
		<table class="final-table">
			<tr>
				<th>課税対象退職給与</th>
				<th>差引源泉徴収税額</th>
				<th>実受領額</th>
				<th>支給方法</th>
				<th>支給日</th>
			</tr>
			<tr>
				<td class="text-red" id="final_taxable">0 ウォン</td>
				<td class="text-red" id="final_tax">0 ウォン</td>
				<td class="text-red" id="final_actual">0 ウォン</td>
				<td><input type="text" id="final_method"></td>
				<td><input type="date" id="final_date"></td>
			</tr>
		</table>
	</div>

	<script>
    let selectedEmpId = 0;

    function selectEmployee(row, empId, name, position, hireStr, resigStr, accountInfo) {
        document.querySelectorAll('#employeeListTable tbody tr').forEach(tr => tr.classList.remove('selected-row'));
        row.classList.add('selected-row');

        selectedEmpId = empId;
        
        document.getElementById('calcEmpNameTitle').innerText = name + " " + position + " 退職計算";
        document.getElementById('hireDate').value = hireStr;
        document.getElementById('resignationDate').value = resigStr;
        
        document.getElementById('bankAccountInfo').value = accountInfo !== 'null' ? accountInfo : '';
        document.getElementById('final_method').value = accountInfo !== 'null' ? accountInfo : '口座振込';

        calculateDays();
    }

    function calculateDays() {
        const hire = new Date(document.getElementById('hireDate').value);
        const resig = new Date(document.getElementById('resignationDate').value);
        const exclude = parseInt(document.getElementById('excludedDays').value) || 0;

        if (!isNaN(hire) && !isNaN(resig)) {
            const diffTime = Math.abs(resig - hire);
            let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) - exclude;
            if(diffDays < 0) diffDays = 0;

            const years = Math.floor(diffDays / 365);
            
            document.getElementById('yearsOfService').innerText = years + "年";
            document.getElementById('daysOfService').innerText = diffDays + "日";
            
            const selectedRow = document.querySelector('.selected-row');
            if(selectedRow) {
                selectedRow.querySelector('.td-period').innerText = document.getElementById('hireDate').value + " ~ " + document.getElementById('resignationDate').value;
                selectedRow.querySelector('.td-days').innerText = diffDays;
            }
        }
    }

    // 3. AJAX: 給与内訳読み込み (期間別)
    function loadSalaryData() {
        if (selectedEmpId === 0) {
            alert('社員を先に選択してください。');
            return;
        }

        const resigDate = document.getElementById('resignationDate').value;
        
        fetch('${pageContext.request.contextPath}/retirement/manage.do?action=getSalary', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'employeeId=' + selectedEmpId + '&resignationDate=' + resigDate
        })
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('salaryListBody');
            tbody.innerHTML = '';
            
            // サーバーから渡された期間データを元に<tr>を生成
            data.periods.forEach(p => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="text-center">
                        <input type="text" value="\${p.startDate}" class="text-center" style="width: 40%;" readonly> ~ 
                        <input type="text" value="\${p.endDate}" class="text-center" style="width: 40%;" readonly>
                    </td>
                    <td><input type="number" class="text-center calc-days" value="\${p.days}" oninput="updateSalaryTotals()"></td>
                    <td><input type="text" class="calc-amount" value="\${p.amount.toLocaleString()}" oninput="formatAndTotal(this)"></td>
                `;
                tbody.appendChild(tr);
            });
            
            // 総合計の設定
            document.getElementById('totalCalcDays').value = data.totalDays;
            document.getElementById('displayTotalSalary').value = data.totalSalary.toLocaleString();
            
            // 邪魔だった成功のalertは削除しました。
        })
        .catch(error => {
            alert('給与内訳の読み込み中にエラーが発生しました。');
        });
    }

    // 桁区切りのカンマフォーマットとリアルタイム合計更新
    function formatAndTotal(input) {
        let val = input.value.replace(/[^0-9]/g, '');
        if(val !== '') {
            input.value = parseInt(val, 10).toLocaleString();
        }
        updateSalaryTotals();
    }
    
    // 入力値が変更されるたびに日数の合計と金額の合計を再計算
    function updateSalaryTotals() {
        let totalDays = 0;
        let totalSalary = 0;
        
        document.querySelectorAll('.calc-days').forEach(input => {
            totalDays += parseInt(input.value) || 0;
        });
        
        document.querySelectorAll('.calc-amount').forEach(input => {
            totalSalary += parseInt(input.value.replace(/,/g, '')) || 0;
        });
        
        document.getElementById('totalCalcDays').value = totalDays;
        document.getElementById('displayTotalSalary').value = totalSalary.toLocaleString();
    }

    function formatNumber(input) {
        let val = input.value.replace(/[^0-9]/g, '');
        if(val !== '') {
            input.value = parseInt(val, 10).toLocaleString();
        }
    }

    // 4. 退職金の計算ロジック
    function calculateRetirementPay() {
        if (selectedEmpId === 0) {
            alert('社員を選択し、給与を読み込んでください。');
            return;
        }

        const totalSalary = parseInt(document.getElementById('displayTotalSalary').value.replace(/,/g, '')) || 0;
        const consolationPay = parseInt(document.getElementById('consolationPay').value.replace(/,/g, '')) || 0;
        const dismissalPay = parseInt(document.getElementById('dismissalPay').value.replace(/,/g, '')) || 0;
        const totalServiceDays = parseInt(document.getElementById('daysOfService').innerText.replace('日','')) || 0;
        
        // 算定日数 (ここでは表示された合計算定日数を使用)
        const calcDays = parseInt(document.getElementById('totalCalcDays').value) || 90; 

        const dailyAvg = Math.floor(totalSalary / calcDays);
        const baseRetirementPay = Math.floor(dailyAvg * 30 * (totalServiceDays / 365));
        const retirementIncome = baseRetirementPay + consolationPay + dismissalPay;

        const incomeTax = Math.floor(retirementIncome * 0.03);
        const localTax = Math.floor(incomeTax * 0.1); 
        const totalTax = incomeTax + localTax;
        
        const actualPayment = retirementIncome - totalTax;

        document.getElementById('res_3month').innerText = totalSalary.toLocaleString();
        document.getElementById('res_dailyAvg').innerText = dailyAvg.toLocaleString();
        document.getElementById('res_retirementIncome').innerText = retirementIncome.toLocaleString();
        document.getElementById('res_calcTax').innerText = incomeTax.toLocaleString();
        document.getElementById('res_incomeTax').innerText = incomeTax.toLocaleString();
        document.getElementById('res_localTax').innerText = localTax.toLocaleString();

        document.getElementById('final_taxable').innerText = retirementIncome.toLocaleString() + " ウォン";
        document.getElementById('final_tax').innerText = totalTax.toLocaleString() + " ウォン";
        document.getElementById('final_actual').innerText = actualPayment.toLocaleString() + " ウォン";

        const selectedRow = document.querySelector('.selected-row');
        if(selectedRow) {
            selectedRow.querySelector('.td-actual').innerText = actualPayment.toLocaleString();
            const today = new Date().toISOString().split('T')[0];
            selectedRow.cells[0].innerText = today;
            document.getElementById('final_date').value = today;
        }

        alert('退職金の計算が完了しました。');
    }
</script>
</body>
</html>