<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>月別勤怠状況照会</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	font-family: 'Malgun Gothic', dotum, sans-serif;
	font-size: 12px;
	color: #333;
	margin: 0;
	background-color: #f5f5f5;
}

.container {
	background: #fff;
	padding: 20px;
	border-radius: 5px;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* 上部タイトルおよびタブ領域 */
.page-header {
	display: flex;
	align-items: center;
	gap: 15px;
	margin-bottom: 20px;
	border-bottom: 1px solid #eee;
	padding-bottom: 15px;
}

.page-title-icon {
	font-size: 35px;
}

.page-title-text h1 {
	margin: 0;
	font-size: 22px;
	color: #333;
	letter-spacing: -1px;
}

.page-title-text p {
	margin: 5px 0 0 0;
	font-size: 13px;
	color: #777;
}

.tab-menu {
	display: flex;
	gap: 5px;
	margin-bottom: 20px;
	border-bottom: 2px solid #ddd;
	padding-bottom: 10px;
}

.tab-btn {
	padding: 10px 30px;
	font-size: 14px;
	font-weight: bold;
	border: none;
	cursor: pointer;
	border-radius: 4px;
}

.tab-active {
	background-color: #599b9a;
	color: white;
} /* アクティブタブ (青緑色) */
.tab-inactive {
	background-color: #a5a5a5;
	color: white;
} /* 非アクティブタブ (灰色) */
.tab-inactive:hover {
	background-color: #888;
}

/* 検索フィルター領域 */
.filter-bar {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-bottom: 15px;
	background: #f8f9fa;
	padding: 10px 15px;
	border: 1px solid #e9ecef;
	border-radius: 4px;
}

.filter-bar select, .filter-bar button {
	padding: 5px 10px;
	font-size: 12px;
	border: 1px solid #ccc;
	border-radius: 3px;
}

.btn-search {
	background-color: #5c7cba;
	color: white;
	border: none !important;
	cursor: pointer;
}

/* 全体メインテーブル */
.grid-main-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	background: white;
}

.grid-main-table th, .grid-main-table td {
	border: 1px solid #d1d5db;
	padding: 6px 4px;
}

.grid-main-table th {
	background-color: #f3f4f6;
	font-weight: bold;
}

.emp-row:hover td {
	background-color: #f0f7ff !important;
	transition: background-color 0.2s;
} /* 行ホバー効果 */

/* カレンダー内部小型グリッドテーブル */
.calendar-sub-table {
	width: 100%;
	border-collapse: collapse;
	margin: 0;
}

.calendar-sub-table td, .calendar-sub-table th {
	border: 1px solid #e5e7eb;
	height: 22px;
	width: 6.25%;
	text-align: center;
	padding: 0;
	font-size: 11px;
}

/* 土曜日 / 日曜日の色定義 */
.sat-header {
	color: #2563eb !important;
	font-weight: bold;
	background-color: #eff6ff;
}

.sun-header {
	color: #dc2626 !important;
	font-weight: bold;
	background-color: #fef2f2;
}

.sat-bg {
	background-color: #f0f7ff;
}

.sun-bg {
	background-color: #fff5f5;
}

/* 赤い点スタイル */
.attendance-dot {
	display: inline-block;
	width: 7px;
	height: 7px;
	background-color: #dc2626;
	border-radius: 50%;
}

/* 右側要約テキスト */
.summary-text {
	text-align: left;
	font-size: 11px;
	line-height: 1.4;
	padding-left: 5px;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="container">
		<!-- 上部タイトルおよびタブメニュー -->
		<div class="page-header">

			<div class="page-title-text">
				<h1>勤怠照会</h1>

			</div>
		</div>

		<div class="tab-menu">
			<button class="tab-btn tab-active"
				onclick="location.href='${pageContext.request.contextPath}/attendance/monthly.do'">月別照会</button>
			<button class="tab-btn tab-inactive"
				onclick="location.href='${pageContext.request.contextPath}/attendance/detail.do'">詳細照会</button>
		</div>

		<!-- 上部年/月フィルター領域 -->
		<div class="filter-bar">
			<label for="selectYear"><strong>照会年月:</strong></label> <select
				id="selectYear"></select> 年 <select id="selectMonth"></select> 月
			<button type="button" class="btn-search" onclick="loadMonthlyGrid()">照会</button>
			<!--     <span style="margin-left:auto; color:#d9534f; font-weight:bold;">※ 社員リストをクリックすると該当社員の詳細照会ウィンドウに移動します。</span> -->
		</div>

		<!-- 月別勤怠グリッド領域 -->
		<table class="grid-main-table">
			<thead>
				<tr>
					<th rowspan="2" style="width: 60px;">区分</th>
					<th rowspan="2" style="width: 80px;">社員番号</th>
					<th rowspan="2" style="width: 80px;">姓名</th>
					<th rowspan="2" style="width: 80px;">部署</th>
					<th rowspan="2" style="width: 70px;">職位</th>
					<th style="padding: 0;">
						<!-- 日付ヘッダー (1~16日 / 17~31日) -->
						<table class="calendar-sub-table" id="headerDateGrid"></table>
					</th>
					<th rowspan="2" style="width: 120px;">勤怠集計</th>
				</tr>
			</thead>
			<tbody id="monthlyGridBody">
				<tr>
					<td colspan="7" style="padding: 30px;">上部の照会ボタンを押してください。</td>
				</tr>
			</tbody>
		</table>
	</div>

	<script>
    window.onload = function() {
        initYearMonthSelect();
        loadMonthlyGrid();
    };

    function initYearMonthSelect() {
        var today = new Date();
        var curYear = today.getFullYear();
        var curMonth = today.getMonth() + 1;

        var yearSelect = document.getElementById("selectYear");
        var monthSelect = document.getElementById("selectMonth");

        for (var y = curYear - 10; y <= curYear + 2; y++) {
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

    // 社員行クリック時に詳細照会ページへ移動および名前パラメータ伝達
    function goToDetail(empName) {
        if(!empName) return;
        location.href = '${pageContext.request.contextPath}/attendance/detail.do?targetName=' + encodeURIComponent(empName);
    }

    function loadMonthlyGrid() {
        var year = document.getElementById("selectYear").value;
        var month = document.getElementById("selectMonth").value;
        var tbody = document.getElementById("monthlyGridBody");

        tbody.innerHTML = '<tr><td colspan="7" style="padding:20px;">データを読み込み中です...</td></tr>';

        fetch("${pageContext.request.contextPath}/attendance/monthly.do?year=" + year + "&month=" + month)
            .then(function(res) { return res.json(); })
            .then(function(data) {
                renderGridTable(year, parseInt(month, 10), data.employees, data.attendances);
            })
            .catch(function(err) {
                tbody.innerHTML = '<tr><td colspan="7" style="padding:20px; color:red;">照会中にエラーが発生しました。</td></tr>';
            });
    }

    function renderGridTable(year, month, employees, attendances) {
        var tbody = document.getElementById("monthlyGridBody");
        tbody.innerHTML = "";

        var lastDay = new Date(year, month, 0).getDate();

        // 1. ヘッダー日付領域
        var headerHtml = "<tr>";
        for (var d = 1; d <= 16; d++) {
            var dayOfWeek = new Date(year, month - 1, d).getDay();
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
            tbody.innerHTML = '<tr><td colspan="7" style="padding:20px;">照会された社員情報がありません。</td></tr>';
            return;
        }

        var formatZero = function(num) { return num < 10 ? '0' + num : num; };

        // 2. 社員リストおよび勤怠セルのrendering (行にonclick追加)
        employees.forEach(function(emp) {
            var empAtts = attendances.filter(function(a) { return a.employeeId === emp.employeeId; });
            var dayAttendanceMap = {};
            var typeSummaryMap = {};

            empAtts.forEach(function(att) {
                var typeName = att.attendanceTypeName;
                typeSummaryMap[typeName] = (typeSummaryMap[typeName] || 0) + att.attendanceDays;

                for (var d = 1; d <= lastDay; d++) {
                    var curDateStr = year + '-' + formatZero(month) + '-' + formatZero(d);
                    if (curDateStr >= att.startDate && curDateStr <= att.endDate) {
                        dayAttendanceMap[d] = true;
                    }
                }
            });

            var empNameStr = emp.koreanName || '';
            // マウスオーバー時のカーソル変更およびクリックイベント付与
            var rowHtml = '<tr class="emp-row" style="cursor:pointer;" onclick="goToDetail(\'' + empNameStr + '\')">' +
                '<td>' + (emp.employmentType || '') + '</td>' +
                '<td>No-' + emp.employeeId + '</td>' +
                '<td>' + empNameStr + '</td>' +
                '<td>' + (emp.departmentName || '') + '</td>' +
                '<td>' + (emp.positionName || '') + '</td>' +
                '<td style="padding:0;">' +
                    '<table class="calendar-sub-table">' +
                        '<tr>';

            for (var d = 1; d <= 16; d++) {
                var dayOfWeek = new Date(year, month - 1, d).getDay();
                var bgClass = dayOfWeek === 6 ? 'sat-bg' : (dayOfWeek === 0 ? 'sun-bg' : '');
                var dot = dayAttendanceMap[d] ? '<span class="attendance-dot"></span>' : '';
                rowHtml += '<td class="' + bgClass + '">' + dot + '</td>';
            }

            rowHtml += '</tr><tr>';

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

            var summaryHtml = '<td class="summary-text">';
            for (var key in typeSummaryMap) {
                if (typeSummaryMap.hasOwnProperty(key)) {
                    summaryHtml += '• ' + key + ': ' + typeSummaryMap[key] + '日<br>';
                }
            }
            if (Object.keys(typeSummaryMap).length === 0) {
                summaryHtml += '<span style="color:#aaa;">記録なし</span>';
            }
            summaryHtml += '</td></tr>';

            tbody.innerHTML += (rowHtml + summaryHtml);
        });
    }
</script>

</body>
</html>