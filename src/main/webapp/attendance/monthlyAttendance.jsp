<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>월별 근태 현황 조회</title>
<style>
    body { font-family: 'Malgun Gothic', dotum, sans-serif; font-size: 12px; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
    
    .container { background: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    
    /* 검색 필터 영역 */
    .filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 15px; background: #f8f9fa; padding: 10px 15px; border: 1px solid #e9ecef; border-radius: 4px; }
    .filter-bar select, .filter-bar button { padding: 5px 10px; font-size: 12px; border: 1px solid #ccc; border-radius: 3px; }
    .btn-search { background-color: #5c7cba; color: white; border: none !important; cursor: pointer; }
    
    /* 전체 메인 테이블 */
    .grid-main-table { width: 100%; border-collapse: collapse; text-align: center; background: white; }
    .grid-main-table th, .grid-main-table td { border: 1px solid #d1d5db; padding: 6px 4px; }
    .grid-main-table th { background-color: #f3f4f6; font-weight: bold; }
    
    /* 달력 내부 소형 그리드 테이블 */
    .calendar-sub-table { width: 100%; border-collapse: collapse; margin: 0; }
    .calendar-sub-table td, .calendar-sub-table th { border: 1px solid #e5e7eb; height: 22px; width: 6.25%; text-align: center; padding: 0; font-size: 11px; }
    
    /* 토요일 / 일요일 색상 정의 */
    .sat-header { color: #2563eb !important; font-weight: bold; background-color: #eff6ff; } /* 토요일: 파란색 */
    .sun-header { color: #dc2626 !important; font-weight: bold; background-color: #fef2f2; } /* 일요일: 빨간색 */
    
    .sat-bg { background-color: #f0f7ff; }
    .sun-bg { background-color: #fff5f5; }

    /* 빨간 점 스타일 */
    .attendance-dot {
        display: inline-block;
        width: 7px;
        height: 7px;
        background-color: #dc2626;
        border-radius: 50%;
    }
    
    /* 오른쪽 요약 텍스트 */
    .summary-text { text-align: left; font-size: 11px; line-height: 1.4; padding-left: 5px; }
</style>
</head>
<body>

<div class="container">
    <h2>월별 근태 현황 조회</h2>
    
    <!-- 상단 연/월 필터 영역 -->
    <div class="filter-bar">
        <label for="selectYear"><strong>조회년월:</strong></label>
        <select id="selectYear"></select> 년
        <select id="selectMonth"></select> 월
        <button type="button" class="btn-search" onclick="loadMonthlyGrid()">조회</button>
    </div>

    <!-- 월별 근태 그리드 영역 -->
    <table class="grid-main-table">
        <thead>
            <tr>
                <th rowspan="2" style="width: 60px;">구분</th>
                <th rowspan="2" style="width: 80px;">사원번호</th>
                <th rowspan="2" style="width: 80px;">성명</th>
                <th rowspan="2" style="width: 80px;">부서</th>
                <th rowspan="2" style="width: 70px;">직위</th>
                <th style="padding:0;">
                    <!-- 날짜 헤더 (1~16일 / 17~31일) -->
                    <table class="calendar-sub-table" id="headerDateGrid"></table>
                </th>
                <th rowspan="2" style="width: 120px;">근태 집계</th>
            </tr>
        </thead>
        <tbody id="monthlyGridBody">
            <tr>
                <td colspan="7" style="padding: 30px;">상단의 조회 버튼을 눌러주세요.</td>
            </tr>
        </tbody>
    </table>
</div>

<script>
    window.onload = function() {
        initYearMonthSelect();
        loadMonthlyGrid(); // 페이지 진입 시 자동 조회
    };

    function initYearMonthSelect() {
        var today = new Date();
        var curYear = today.getFullYear();
        var curMonth = today.getMonth() + 1;

        var yearSelect = document.getElementById("selectYear");
        var monthSelect = document.getElementById("selectMonth");

        for (var y = curYear - 2; y <= curYear + 2; y++) {
            var opt = document.createElement("option");
            opt.value = y;
            opt.innerText = y;
            if (y === curYear) opt.selected = true;
            yearSelect.appendChild(opt);
        }

        for (var m = 1; m <= 12; m++) {
            var opt = document.createElement("option");
            opt.value = m < 10 ? "0" + m : m;
            opt.innerText = m;
            if (m === curMonth) opt.selected = true;
            monthSelect.appendChild(opt);
        }
    }

    function loadMonthlyGrid() {
        var year = document.getElementById("selectYear").value;
        var month = document.getElementById("selectMonth").value;
        var tbody = document.getElementById("monthlyGridBody");

        tbody.innerHTML = '<tr><td colspan="7" style="padding:20px;">데이터를 불러오는 중입니다...</td></tr>';

        fetch("${pageContext.request.contextPath}/attendance/monthly.do?year=" + year + "&month=" + month)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            renderGridTable(year, parseInt(month, 10), data.employees, data.attendances);
        })
        .catch(function(err) {
            tbody.innerHTML = '<tr><td colspan="7" style="padding:20px; color:red;">조회 중 오류가 발생했습니다.</td></tr>';
        });
    }

    function renderGridTable(year, month, employees, attendances) {
        var tbody = document.getElementById("monthlyGridBody");
        tbody.innerHTML = "";

        var lastDay = new Date(year, month, 0).getDate(); // 해당 월의 마지막 날짜 (28~31)

        // 1. 헤더 날짜 영역 (1~16일 / 17~31일) 생성
        var headerHtml = "<tr>";
        for (var d = 1; d <= 16; d++) {
            var dayOfWeek = new Date(year, month - 1, d).getDay(); // 0:일, 6:토
            var colorClass = dayOfWeek === 6 ? 'sat-header' : (dayOfWeek === 0 ? 'sun-header' : '');
            headerHtml += '<th class="' + colorClass + '">' + d + '</th>';
        }
        headerHtml += "</tr><tr>";
        for (var d = 17; d <= 31; d++) {
            if (d <= lastDay) {
                var dayOfWeek = new Date(year, month - 1, d).getDay();
                var colorClass = dayOfWeek === 6 ? 'sat-header' : (dayOfWeek === 0 ? 'sun-header' : '');
                headerHtml += '<th class="' + colorClass + '">' + d + '</th>';
            } else {
                headerHtml += '<th></th>';
            }
        }
        headerHtml += "</tr>";
        document.getElementById("headerDateGrid").innerHTML = headerHtml;

        if (!employees || employees.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="padding:20px;">조회된 사원 정보가 없습니다.</td></tr>';
            return;
        }

        // YYYY-MM-DD 포맷 변환 도우미 함수
        var formatZero = function(num) { return num < 10 ? '0' + num : num; };

        // 2. 사원 목록 및 근태 셀 rendering
        employees.forEach(function(emp) {
            var empAtts = attendances.filter(function(a) { return a.employeeId === emp.employeeId; });
            var dayAttendanceMap = {};
            var typeSummaryMap = {};

            empAtts.forEach(function(att) {
                var typeName = att.attendanceTypeName;
                typeSummaryMap[typeName] = (typeSummaryMap[typeName] || 0) + att.attendanceDays;

                // 날짜 비교 (JSP EL 충돌 방지를 위해 일반 문자열 더하기 연산자 사용)
                for (var d = 1; d <= lastDay; d++) {
                    var curDateStr = year + '-' + formatZero(month) + '-' + formatZero(d);
                    if (curDateStr >= att.startDate && curDateStr <= att.endDate) {
                        dayAttendanceMap[d] = true;
                    }
                }
            });

            // 행 생성
            var rowHtml = '<tr>' +
                '<td>' + (emp.employmentType || '') + '</td>' +
                '<td>No-' + emp.employeeId + '</td>' +
                '<td>' + (emp.koreanName || '') + '</td>' +
                '<td>' + (emp.departmentName || '') + '</td>' +
                '<td>' + (emp.positionName || '') + '</td>' +
                '<td style="padding:0;">' +
                    '<table class="calendar-sub-table">' +
                        '<tr>';

            // 상단 (1~16일 데이터)
            for (var d = 1; d <= 16; d++) {
                var dayOfWeek = new Date(year, month - 1, d).getDay();
                var bgClass = dayOfWeek === 6 ? 'sat-bg' : (dayOfWeek === 0 ? 'sun-bg' : '');
                var dot = dayAttendanceMap[d] ? '<span class="attendance-dot"></span>' : '';
                rowHtml += '<td class="' + bgClass + '">' + dot + '</td>';
            }

            rowHtml += '</tr><tr>';

            // 하단 (17~31일 데이터)
            for (var d = 17; d <= 31; d++) {
                if (d <= lastDay) {
                    var dayOfWeek = new Date(year, month - 1, d).getDay();
                    var bgClass = dayOfWeek === 6 ? 'sat-bg' : (dayOfWeek === 0 ? 'sun-bg' : '');
                    var dot = dayAttendanceMap[d] ? '<span class="attendance-dot"></span>' : '';
                    rowHtml += '<td class="' + bgClass + '">' + dot + '</td>';
                } else {
                    rowHtml += '<td style="background-color:#f9fafb;"></td>';
                }
            }

            rowHtml += '</tr></table></td>';

            // 오른쪽 집계 요약
            var summaryHtml = '<td class="summary-text">';
            for (var key in typeSummaryMap) {
                if (typeSummaryMap.hasOwnProperty(key)) {
                    summaryHtml += '• ' + key + ': ' + typeSummaryMap[key] + '일<br>';
                }
            }
            if (Object.keys(typeSummaryMap).length === 0) {
                summaryHtml += '<span style="color:#aaa;">기록 없음</span>';
            }
            summaryHtml += '</td></tr>';

            tbody.innerHTML += (rowHtml + summaryHtml);
        });
    }
</script>

</body>
</html>