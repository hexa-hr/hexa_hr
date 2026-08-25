=<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<html>
<head>
<meta charset="UTF-8">
<title>勤怠登録</title>
<style>
body {
	font-family: 'Malgun Gothic', dotum, sans-serif;
	font-size: 13px;
	color: #333;
}

.form-table {
	width: 100%;
	max-width: 550px;
	border-collapse: collapse;
	margin: 20px 0;
}

.form-table th, .form-table td {
	padding: 8px 10px;
	border-bottom: 1px solid #e2e2e2;
	text-align: left;
}

.form-table th {
	width: 120px;
	background-color: #fbfbfb;
	font-weight: bold;
}

.top-border {
	border-top: 2px solid #000;
}

.text-red {
	color: #d9534f;
}

.btn-blue {
	background-color: #5c7cba;
	color: white;
	border: none;
	padding: 4px 8px;
	font-size: 12px;
	border-radius: 2px;
	cursor: pointer;
}

.btn-group {
	text-align: center;
	margin-top: 15px;
}

.btn-submit {
	background-color: #5c7cba;
	color: white;
	border: none;
	padding: 6px 20px;
	font-size: 13px;
	border-radius: 3px;
	cursor: pointer;
}

.btn-reset {
	background-color: #999;
	color: white;
	border: none;
	padding: 6px 15px;
	font-size: 13px;
	border-radius: 3px;
	cursor: pointer;
}

input[type="date"], input[type="text"], input[type="number"], select {
	padding: 3px 5px;
	border: 1px solid #ccc;
	font-size: 12px;
}

input[type="number"] {
	width: 110px;
}
</style>
</head>
<body>

	<form id="attendanceForm"
		action="${pageContext.request.contextPath}/attendance/save.do"
		method="post">
		<table class="form-table">
			<tr class="top-border">
				<th>入力日</th>
				<td><fmt:formatDate value="${now}" pattern="yyyy-MM-dd" /></td>
			</tr>
			<tr>
				<th>勤怠項目</th>
				<td><select id="attendanceType" name="attendanceType">
						<option value="" data-has-vacation="false" data-unit="">選択してください。</option>
						<!-- サーバーから渡される勤怠項目リスト(attendanceList)を反復 -->
						<c:forEach var="att" items="${attendanceList}">
							<option value="${att.attendanceTypeId}" data-unit="${att.unit}"
								data-has-vacation="${not empty att.vacationTypeId ? 'true' : 'false'}"
								data-vacation-name="${att.vacationTypeName}"
								data-start="<fmt:formatDate value='${att.applyPeriod1}' pattern='yyyy-MM-dd'/>"
								data-end="<fmt:formatDate value='${att.applyPeriod2}' pattern='yyyy-MM-dd'/>">
								${att.attendanceTypeName}</option>
						</c:forEach>
				</select></td>
			</tr>

			<!-- リフレッシュ休暇選択時にのみ露出する行 -->
			<tr id="rewardVacationRow" style="display: none;">
				<th class="text-red">休暇適用期間</th>
				<td class="text-red"><span id="appliedStartDate">2017-01-01</span>
					~ <span id="appliedEndDate">2017-12-31</span></td>
			</tr>

			<tr>
				<th>期間</th>
				<td><input type="date" id="startDate" name="startDate">
					~ <input type="date" id="endDate" name="endDate"></td>
			</tr>

			<tr>
				<th id="attendanceLabelTh">勤怠日数</th>
				<td><input type="number" id="attendanceDays"
					name="attendanceDays" min="0" step="0.5"> <span
					id="unitText">日</span>
					<button type="button" class="btn-blue">休暇日数現状</button></td>
			</tr>
			<tr>
				<th>金額(手当)</th>
				<td><input type="number" id="wageAmount" name="wageAmount"
					placeholder="0"> ウォン</td>
			</tr>
			<tr>
				<th>摘要</th>
				<td><input type="text" id="remark" name="remark"
					style="width: 80%;"></td>
			</tr>
		</table>

		<div class="btn-group">
			<button type="submit" class="btn-submit">保存</button>

			<button type="button" id="btnReset" class="btn-reset">内容クリア</button>
		</div>
	</form>

	<script>
document.addEventListener('DOMContentLoaded', function() {
    // 既存変数の宣言
    const attendanceForm = document.getElementById('attendanceForm');
    const attendanceType = document.getElementById('attendanceType');
    const rewardVacationRow = document.getElementById('rewardVacationRow');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const btnReset = document.getElementById('btnReset');
    
    // 単位変更のための変数
    const unitText = document.getElementById('unitText');
    const attendanceLabelTh = document.getElementById('attendanceLabelTh');

    function updateFormState() {
        // 現在選択されている<option>タグを取得します。
        const selectedOption = attendanceType.options[attendanceType.selectedIndex];
        
        // <option>に隠しておいたデータを取り出します。
        const hasVacation = selectedOption.getAttribute('data-has-vacation') === 'true';
        const vName = selectedOption.getAttribute('data-vacation-name');
        const vStart = selectedOption.getAttribute('data-start');
        const vEnd = selectedOption.getAttribute('data-end');
        const vUnit = selectedOption.getAttribute('data-unit'); 

        // 単位によるテキスト変更 (日本語のみ対応)
        if (vUnit === '時間') {
            unitText.textContent = '時間';
            attendanceLabelTh.textContent = '勤怠時間'; 
        } else if (vUnit === '日') {
            unitText.textContent = '日';
            attendanceLabelTh.textContent = '勤怠日数'; 
        } else {
            unitText.textContent = '日'; // 基本値
            attendanceLabelTh.textContent = '勤怠日数';
        }

        // 選択した勤怠項目に連結された休暇がある場合
        if (hasVacation && vStart && vEnd) {
            rewardVacationRow.style.display = ''; // 行を表示
            
            // 画面テキストの変更
            document.getElementById('appliedStartDate').textContent = vName + " " + vStart;
            document.getElementById('appliedEndDate').textContent = vEnd;
            
            // 期間入力inputのmin、max属性を動的設定
            startDate.min = vStart;
            startDate.max = vEnd;
            endDate.min = vStart;
            endDate.max = vEnd;

            // もしすでに入力された日付が制限範囲から外れた場合、範囲内に強制調整
            if (startDate.value && (startDate.value < vStart || startDate.value > vEnd)) {
                startDate.value = vStart;
            }
            if (endDate.value && (endDate.value < vStart || endDate.value > vEnd)) {
                endDate.value = vEnd;
            }
        } else {
            // 連結された休暇がない一般勤怠項目(遅刻、残業など)の場合
            rewardVacationRow.style.display = 'none'; // 行を隠す
            
            // 期間制限の解除
            startDate.removeAttribute('min');
            startDate.removeAttribute('max');
            endDate.removeAttribute('min');
            endDate.removeAttribute('max');
        }
    }

    attendanceType.addEventListener('change', updateFormState);

    // 内容クリアボタンクリック時に入力値全体削除および状態初期化
    btnReset.addEventListener('click', function() {
        attendanceForm.reset();
        rewardVacationRow.style.display = 'none';
        
        startDate.removeAttribute('min');
        startDate.removeAttribute('max');
        endDate.removeAttribute('min');
        endDate.removeAttribute('max');
        
        // 初期化時にテキスト復旧
        unitText.textContent = '日';
        attendanceLabelTh.textContent = '勤怠日数';
    });

    updateFormState();
});
</script>

</body>
</html>