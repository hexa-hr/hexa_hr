<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<html>
<head>
<meta charset="UTF-8">
<title>勤怠登録</title>

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
	background-color: #f8f9fa;
	font-family: 'Malgun Gothic', dotum, sans-serif;
	color: #333;
}

.wrap {
	display: flex;
	align-items: flex-start;
	width: 100%;
}

.container {
	padding: 30px 40px;
	background-color: white;
	box-sizing: border-box;
	flex: 1;
	min-height: 600px;
	min-width: 0;
}

/* 2. 타이틀 영역 */
.page-header {
	margin-bottom: 10px;
}

.page-header h1 {
	font-size: 22px;
	font-weight: bold;
	margin: 0;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.page-desc {
	font-size: 14px;
	color: #666;
	margin: 0 0 20px 0;
}

.form-table {
	width: 100%;
	max-width: 700px;
	border-collapse: collapse;
	margin: 0 0 25px 0;
	background: white;
}

.form-table th, .form-table td {
	padding: 12px 15px;
	border: 1px solid #ccc;
	font-size: 14px;
	text-align: left;
}

.form-table th {
	width: 140px;
	background-color: #f8f9fa;
	font-weight: bold;
	color: #333;
}

.top-border {
	border-top: 2px solid #4e73df !important;
}

.text-red {
	color: #e74a3b !important;
	font-weight: bold;
}

/* 4. 버튼 스타일 통일 */
.btn-blue {
	background-color: #a5a5a5;
	color: white;
	border: none;
	padding: 6px 12px;
	font-size: 13px;
	border-radius: 3px;
	cursor: pointer;
	font-weight: bold;
	margin-left: 5px;
}
.btn-blue:hover { background-color: #858796; }

.btn-group {
	text-align: left;
	margin-top: 20px;
	max-width: 700px;
}

.btn-submit {
	background-color: #4e73df;
	color: white;
	border: none;
	padding: 8px 25px;
	font-size: 14px;
	border-radius: 3px;
	cursor: pointer;
	font-weight: bold;
}
.btn-submit:hover { background-color: #2e59d9; }

.btn-reset {
	background-color: #a5a5a5;
	color: white;
	border: none;
	padding: 8px 20px;
	font-size: 14px;
	border-radius: 3px;
	cursor: pointer;
	font-weight: bold;
	margin-left: 5px;
}
.btn-reset:hover { background-color: #858796; }

/* 5. 입력 필드 스타일 */
input[type="date"], input[type="text"], input[type="number"], select {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

input[type="number"] {
	width: 120px;
}

select {
	min-width: 200px;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">

			<div class="page-header">
				<h1>勤怠登録</h1>
			</div>
			<p class="page-desc">勤怠項目および適用期間を選択し、근태 일수 또는 시간을 등록합니다。</p>

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
									<!-- [수정] data-unit="${att.unit}" 속성 추가 -->
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
						<!-- [수정] 텍스트 변경을 위해 th에 id 부여 -->
						<th id="attendanceLabelTh">勤怠日数</th>
						<td><input type="number" id="attendanceDays"
							name="attendanceDays" min="0" step="0.5"> <!-- [수정] 단위를 동적으로 바꾸기 위해 span으로 감싸기 -->
							<span id="unitText" style="margin-left: 5px; font-weight: bold;">日</span>
							<button type="button" class="btn-blue">休暇日数現状</button></td>
					</tr>
					<tr>
						<th>金額(手当)</th>
						<td>
							<!-- [修正 2] 入力可能な数字専用のinput欄に変更 --> <input type="number"
							id="wageAmount" name="wageAmount" placeholder="0"> ウォン
						</td>
					</tr>
					<tr>
						<th>摘要</th>
						<td><input type="text" id="remark" name="remark"
							style="width: 90%;"></td>
					</tr>
				</table>

				<div class="btn-group">
					<button type="submit" class="btn-submit">保存</button>
					<button type="button" id="btnReset" class="btn-reset">内容クリア</button>
				</div>
			</form>

		</div>
	</div>

	<script>
document.addEventListener('DOMContentLoaded', function() {
    // 既存変数の宣言
    const attendanceForm = document.getElementById('attendanceForm');
    const attendanceType = document.getElementById('attendanceType');
    const rewardVacationRow = document.getElementById('rewardVacationRow');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const btnReset = document.getElementById('btnReset');
    
    // [추가] 단위 변경을 위한 변수
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
        const vUnit = selectedOption.getAttribute('data-unit'); // [추가] 단위 가져오기

        // [추가] 단위에 따른 텍스트 변경 (DBに 한국어 '시간/일' または 일본어 '時間/日' 로 저장될 경우 모두 대응)
        if (vUnit === '시간' || vUnit === '時間') {
            unitText.textContent = '時間';
            attendanceLabelTh.textContent = '勤怠時間'; // '근태시간'
        } else if (vUnit === '일' || vUnit === '日') {
            unitText.textContent = '日';
            attendanceLabelTh.textContent = '勤怠日数'; // '근태일수'
        } else {
            unitText.textContent = '日'; // 기본값
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
        
        // 초기화 시 텍스트 복구
        unitText.textContent = '日';
        attendanceLabelTh.textContent = '勤怠日数';
    });

    updateFormState();
});
</script>

</body>
</html>