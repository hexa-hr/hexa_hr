<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<html>
<head>
<meta charset="UTF-8">
<title>근태 등록</title>
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
				<th>입력일자</th>
				<td><fmt:formatDate value="${now}" pattern="yyyy-MM-dd" /></td>
			</tr>
			<tr>
				<th>근태항목</th>
				<td><select id="attendanceType" name="attendanceType">
						<option value="" data-has-vacation="false">선택하세요.</option>
						<!-- 서버에서 넘겨주는 근태항목 리스트(attendanceList)를 반복 -->
						<c:forEach var="att" items="${attendanceList}">
							<option value="${att.attendanceTypeId}"
								data-has-vacation="${not empty att.vacationTypeId ? 'true' : 'false'}"
								data-vacation-name="${att.vacationTypeName}"
								data-start="<fmt:formatDate value='${att.applyPeriod1}' pattern='yyyy-MM-dd'/>"
								data-end="<fmt:formatDate value='${att.applyPeriod2}' pattern='yyyy-MM-dd'/>">
								${att.attendanceTypeName}</option>
						</c:forEach>
				</select></td>
			</tr>

			<!-- 포상휴가 선택 시에만 노출되는 행 -->
			<tr id="rewardVacationRow" style="display: none;">
				<th class="text-red">휴가적용기간</th>
				<td class="text-red"><span id="appliedStartDate">2017-01-01</span>
					~ <span id="appliedEndDate">2017-12-31</span></td>
			</tr>

			<tr>
				<th>기간</th>
				<td><input type="date" id="startDate" name="startDate">
					~ <input type="date" id="endDate" name="endDate"></td>
			</tr>

			<tr>
				<th>근태일수</th>
				<td><input type="number" id="attendanceDays"
					name="attendanceDays" min="0" step="0.5"> 일
					<button type="button" class="btn-blue">휴가일수 현황</button></td>
			</tr>
			<tr>
				<th>금액(수당)</th>
				<td>
					<!-- [수정 2] 입력 가능한 숫자 전용 input 칸으로 변경 --> <input type="number"
					id="wageAmount" name="wageAmount" placeholder="0"> 원
				</td>
			</tr>
			<tr>
				<th>적요</th>
				<td><input type="text" id="remark" name="remark"
					style="width: 80%;"></td>
			</tr>
		</table>

		<div class="btn-group">
			<button type="submit" class="btn-submit">저장</button>

			<button type="button" id="btnReset" class="btn-reset">내용 지우기</button>
		</div>
	</form>

	<script>
document.addEventListener('DOMContentLoaded', function() {
    // 오늘 날짜 구해서 '입력일자'에 표시
    const currentDateElement = document.getElementById('currentDate');
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1, 1자리일 경우 앞에 '0' 붙임
    const day = String(today.getDate()).padStart(2, '0');
    currentDateElement.textContent = `${year}-${month}-${day}`;

    // 기존 변수 선언
    const attendanceForm = document.getElementById('attendanceForm');
    const attendanceType = document.getElementById('attendanceType');
    const rewardVacationRow = document.getElementById('rewardVacationRow');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const btnReset = document.getElementById('btnReset');
    
    // 포상휴가 허용 범위 날짜
    const minRange = "2017-01-01";
    const maxRange = "2017-12-31";

    function updateFormState() {
        // 현재 선택된 <option> 태그를 가져옵니다.
        const selectedOption = attendanceType.options[attendanceType.selectedIndex];
        
        // <option>에 숨겨둔 데이터들을 꺼냅니다.
        const hasVacation = selectedOption.getAttribute('data-has-vacation') === 'true';
        const vName = selectedOption.getAttribute('data-vacation-name');
        const vStart = selectedOption.getAttribute('data-start');
        const vEnd = selectedOption.getAttribute('data-end');

        // 선택한 근태항목에 연결된 휴가가 있는 경우
        if (hasVacation && vStart && vEnd) {
            rewardVacationRow.style.display = ''; // 행 보이기
            
            // 화면 텍스트 변경 (예: 어쩌구휴가 2026-01-01 ~ 2026-12-31)
            document.getElementById('appliedStartDate').textContent = vName + " " + vStart;
            document.getElementById('appliedEndDate').textContent = vEnd;
            
            // 기간 입력 input의 min, max 속성 동적 설정
            startDate.min = vStart;
            startDate.max = vEnd;
            endDate.min = vStart;
            endDate.max = vEnd;

            // 만약 이미 입력된 날짜가 제한 범위를 벗어났다면 범위 안으로 강제 조정
            if (startDate.value && (startDate.value < vStart || startDate.value > vEnd)) {
                startDate.value = vStart;
            }
            if (endDate.value && (endDate.value < vStart || endDate.value > vEnd)) {
                endDate.value = vEnd;
            }
        } else {
            // 연결된 휴가가 없는 일반 근태항목(지각, 연장근무 등)일 경우
            rewardVacationRow.style.display = 'none'; // 행 숨기기
            
            // 기간 제한 해제
            startDate.removeAttribute('min');
            startDate.removeAttribute('max');
            endDate.removeAttribute('min');
            endDate.removeAttribute('max');
        }
    }

    attendanceType.addEventListener('change', updateFormState);

    // 내용 지우기 버튼 클릭 시 입력값 전체 삭제 및 상태 초기화
    btnReset.addEventListener('click', function() {
        attendanceForm.reset();
        rewardVacationRow.style.display = 'none';
        
        startDate.removeAttribute('min');
        startDate.removeAttribute('max');
        endDate.removeAttribute('min');
        endDate.removeAttribute('max');
    });

    updateFormState();
});
</script>

</body>
</html>