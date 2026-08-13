<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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

<form id="attendanceForm" action="${pageContext.request.contextPath}/attendance/save.do" method="post">
    <table class="form-table">
        <tr class="top-border">
            <th>입력일자</th>
            <td>2026-08-05</td>
        </tr>
        <tr>
            <th>근태항목</th>
            <td>
                <!-- [수정 1] 이미지와 동일한 목록 전체 구성 -->
                <select id="attendanceType" name="attendanceType">
                    <option value="">선택하세요.</option>
                    <option value="연차">연차</option>
                    <option value="반차">반차</option>
                    <option value="지각">지각</option>
                    <option value="조퇴">조퇴</option>
                    <option value="외근">외근</option>
                    <option value="휴일근무">휴일근무</option>
                    <option value="연장근무">연장근무</option>
                    <option value="포상휴가">포상휴가</option>
                    <option value="야간근무">야간근무</option>
                    <option value="청원휴가">청원휴가</option>
                </select>
            </td>
        </tr>
        
        <!-- 포상휴가 선택 시에만 노출되는 행 -->
        <tr id="rewardVacationRow" style="display: none;">
            <th class="text-red">휴가적용기간</th>
            <td class="text-red">
                <span id="appliedStartDate">2017-01-01</span> ~ <span id="appliedEndDate">2017-12-31</span>
            </td>
        </tr>

        <tr>
            <th>기간</th>
            <td>
                <!-- [수정 3] 기본 value를 제거하여 깔끔한 빈 칸 상태 유지 -->
                <input type="date" id="startDate" name="startDate"> 
                ~ 
                <input type="date" id="endDate" name="endDate">
            </td>
        </tr>

        <tr>
            <th>근태일수</th>
            <td>
                <input type="number" id="attendanceDays" name="attendanceDays" min="0" step="0.5"> 일
                <button type="button" class="btn-blue">휴가일수 현황</button>
            </td>
        </tr>
        <tr>
            <th>금액(수당)</th>
            <td>
                <!-- [수정 2] 입력 가능한 숫자 전용 input 칸으로 변경 -->
                <input type="number" id="wageAmount" name="wageAmount" placeholder="0"> 원
            </td>
        </tr>
        <tr>
            <th>적요</th>
            <td>
                <input type="text" id="remark" name="remark" style="width: 80%;">
            </td>
        </tr>
    </table>

    <div class="btn-group">
        <button type="submit" class="btn-submit">저장</button>
        <!-- [수정 3] 내용 지우기 전용 버튼으로 연동 -->
        <button type="button" id="btnReset" class="btn-reset">내용 지우기</button>
    </div>
</form>

<script>
document.addEventListener('DOMContentLoaded', function() {
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
        if (attendanceType.value === '포상휴가') {
            rewardVacationRow.style.display = '';
            
            startDate.min = minRange;
            startDate.max = maxRange;
            endDate.min = minRange;
            endDate.max = maxRange;

            if (startDate.value && (startDate.value < minRange || startDate.value > maxRange)) {
                startDate.value = minRange;
            }
            if (endDate.value && (endDate.value < minRange || endDate.value > maxRange)) {
                endDate.value = maxRange;
            }
        } else {
            rewardVacationRow.style.display = 'none';
            
            startDate.removeAttribute('min');
            startDate.removeAttribute('max');
            endDate.removeAttribute('min');
            endDate.removeAttribute('max');
        }
    }

    attendanceType.addEventListener('change', updateFormState);

    // [수정 3] 내용 지우기 버튼 클릭 시 입력값 전체 삭제 및 상태 초기화
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