<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>月別個人給与統計</title>

<link rel="stylesheet"
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
    href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
    href="${pageContext.request.contextPath}/favicon.ico">

<script
    src="https://cdn.jsdelivr.net/npm/chart.js@4.5.1/dist/chart.umd.min.js"></script>
<script
    src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>

<style>
/* 1. 전체 레이아웃 (공통) */
body {
    margin: 0;
    min-width: 1400px;
    background-color: #f8f9fa;
    font-family: 'Malgun Gothic', sans-serif;
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

/* 3. 상단 검색 폼 영역 */
.filter-bar {
    background: #f4f4f4;
    padding: 15px 20px;
    border: 1px solid #ddd;
    border-radius: 3px;
    margin-bottom: 25px;
    box-sizing: border-box;
}

.search-row {
    display: flex;
    align-items: center;
    gap: 15px;
}

.search-row label {
    font-size: 14px;
    font-weight: bold;
    color: #333;
}

.required-mark {
    color: #e74a3b;
    margin-right: 3px;
}

.filter-bar select, .filter-bar input[type="text"] {
    padding: 6px 10px;
    border: 1px solid #ccc;
    border-radius: 3px;
    font-size: 14px;
    outline: none;
}

.filter-bar select {
    width: 150px;
}

.employee-name {
    width: 180px;
    background-color: #fff;
    font-weight: bold;
    color: #4e73df;
}

.btn-search, .btn-select {
    padding: 6px 16px;
    border: none;
    border-radius: 3px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    color: white;
    outline: none;
}

.btn-search { background-color: #4e73df; }
.btn-search:hover { background-color: #2e59d9; }

.btn-select { background-color: #a5a5a5; }
.btn-select:hover { background-color: #858796; }

.error-message {
    margin-top: 10px;
    color: #e74a3b;
    font-weight: bold;
    font-size: 14px;
}

/* 4. 차트 영역 */
.chart-container {
    position: relative;
    width: 100%;
    height: 430px;
    margin-top: 20px;
    margin-bottom: 30px;
}

/* 5. 데이터 테이블 스타일 */
.table-container {
    width: 100%;
    overflow-x: auto;
}

table.data-table {
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;
    min-width: 1200px;
    white-space: nowrap;
    font-size: 14px;
    text-align: center;
    background: white;
    margin-bottom: 30px;
}

table.data-table th, table.data-table td {
    border: 1px solid #ccc;
    padding: 10px;
}

table.data-table th {
    background-color: #f8f9fa;
    color: #333;
    font-weight: bold;
}

table.data-table td {
    text-align: right;
}

table.data-table .col-title {
    width: 180px;
}

table.data-table td.row-title {
    text-align: left;
    background-color: #f8f9fa;
    font-weight: bold;
    color: #333;
    white-space: normal;
    word-break: keep-all;
}

table.data-table td.sub-title {
    text-align: left;
    padding-left: 25px;
    background-color: #fdfdfd;
    color: #555;
    white-space: normal;
    word-break: keep-all;
}

table.data-table tbody tr:hover td:not(.row-title):not(.sub-title) {
    background-color: #f1f5f9;
}

/* 합계(Total) 열 스타일 */
table.data-table th.total-column, 
table.data-table td.total-column {
    background-color: #f4f4f4;
    font-weight: bold;
    color: #4e73df;
}

/* 6. 모달 팝업 스타일 */
.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.45);
}

.modal-content {
    background-color: #fff;
    width: 700px;
    margin: 80px auto;
    padding: 30px;
    border: 0;
    border-radius: 5px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    box-sizing: border-box;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    border-bottom: 2px solid #4e73df;
    padding-bottom: 8px;
}

.modal-header h2 {
    margin: 0;
    font-size: 18px;
    color: #333;
}

.modal-header button {
    background: none;
    border: none;
    font-size: 24px;
    color: #999;
    padding: 0;
    cursor: pointer;
}
.modal-header button:hover { color: #333; }

.employee-filter {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
}

.employee-filter input[type="text"], .employee-filter select {
    padding: 6px 10px;
    border: 1px solid #ccc;
    border-radius: 3px;
    outline: none;
}

.employee-table-container {
    max-height: 350px;
    overflow-y: auto;
    border: 1px solid #ccc;
}

.employee-table {
    width: 100%;
    border-collapse: collapse;
}

.employee-table th, .employee-table td {
    border: 1px solid #ccc;
    padding: 10px;
    text-align: center;
    font-size: 14px;
}

.employee-table th {
    background-color: #f8f9fa;
    position: sticky;
    top: 0;
}

.employee-row { cursor: pointer; }
.employee-row:hover { background-color: #f1f5f9; }
.employee-row.selected { background-color: #e2e8f0; font-weight: bold; }

.modal-buttons {
    margin-top: 20px;
    text-align: center;
}

.modal-buttons button {
    margin: 0 5px;
    padding: 8px 25px;
}
</style>
</head>

<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

    <div class="wrap">
        <div class="container">

            <div class="page-header">
                <h1>月別個人給与統計</h1>
            </div>
            <p class="page-desc">帰属年度と社員を選択すると、該当社員の月別給与状況を確認できます。</p>

            <jsp:useBean id="today" class="java.util.Date" />
            <fmt:formatDate value="${today}" pattern="yyyy" var="currentYear" />

            <form class="filter-bar" method="get"
                action="${pageContext.request.contextPath}/wage/monthlyPersonalStatistics.do">

                <div class="search-row">
                    <label for="year"><span class="required-mark">*</span>帰属年度</label> 
                    <select id="year" name="year">
                        <c:forEach begin="0" end="9" var="offset">
                            <c:set var="yearOption" value="${currentYear - 9 + offset}" />
                            <option value="${yearOption}" <c:if test="${yearOption == selectedYear}">selected</c:if>>
                                ${yearOption}年
                            </option>
                        </c:forEach>
                    </select> 
                    
                    <label for="selectedEmployeeName" style="margin-left: 20px;"><span class="required-mark">*</span>対象社員</label> 
                    <input type="hidden" id="employeeId" name="employeeId" value="<c:out value='${selectedEmployeeId}' />"> 
                    <input type="text" id="selectedEmployeeName" class="employee-name" value="<c:out value='${selectedEmployeeName}' />" placeholder="対象社員を選択" readonly>

                    <button type="button" id="openEmployeeModal" class="btn-select">社員検索</button>
                    <button type="submit" name="search" value="true" class="btn-search" style="margin-left: 10px;">照会</button>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        <c:out value="${errorMessage}" />
                    </div>
                </c:if>

            </form>

            <!-- 사원 선택 모달 -->
            <div id="employeeModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>給与統計対象社員の選択</h2>
                        <button type="button" id="closeEmployeeModal">×</button>
                    </div>

                    <div class="employee-filter">
                        <input type="text" id="employeeKeyword" placeholder="社員検索" style="flex: 1;">
                        <select id="departmentFilter">
                            <option value="">全部署</option>
                        </select> 
                        <select id="statusFilter">
                            <option value="">すべての状態</option>
                            <option value="재직">在職</option>
                            <option value="퇴직">退職</option>
                        </select>
                    </div>

                    <div class="employee-table-container">
                        <table class="employee-table">
                            <thead>
                                <tr>
                                    <th>区分</th>
                                    <th>氏名</th>
                                    <th>部署</th>
                                    <th>役職</th>
                                    <th>状態</th>
                                </tr>
                            </thead>
                            <tbody id="employeeTableBody">
                                <c:forEach var="employee" items="${employeeRows}">
                                    <tr class="employee-row"
                                        data-employee-id="<c:out value='${employee.employeeId}' />"
                                        data-name="<c:out value='${employee.koreanName}' />"
                                        data-department="<c:out value='${employee.departmentName}' />"
                                        data-status="<c:out value='${employee.status}' />">
                                        <td><c:out value="${employee.employmentType}" /></td>
                                        <td style="font-weight: bold;"><c:out value="${employee.koreanName}" /></td>
                                        <td><c:out value="${empty employee.departmentName ? '-' : employee.departmentName}" /></td>
                                        <td><c:out value="${empty employee.positionName ? '-' : employee.positionName}" /></td>
                                        <td><c:out value="${employee.status}" /></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="modal-buttons">
                        <button type="button" id="selectEmployeeButton" class="btn-search">社員選択</button>
                        <button type="button" id="cancelEmployeeButton" class="btn-select">キャンセル</button>
                    </div>
                </div>
            </div>

            <!-- 월별 개인급여 통계 -->
            <c:if test="${not empty monthlyPersonalStatistics}">

                <!-- 차트 영역 -->
                <div class="chart-container">
                    <canvas id="monthlyPersonalChart"></canvas>
                </div>

                <!-- 데이터 테이블 영역 -->
                <div class="table-container">
                    <table class="data-table">
                        <colgroup>
                            <col class="col-title" />
                            <c:forEach var="row" items="${monthlyPersonalStatistics.rows}">
                                <col />
                            </c:forEach>
                            <col />
                        </colgroup>

                        <thead>
                            <tr>
                                <th>区分</th>
                                <c:forEach var="row" items="${monthlyPersonalStatistics.rows}" varStatus="status">
                                    <th>${status.count}月</th>
                                </c:forEach>
                                <th class="total-column">合計</th>
                            </tr>
                        </thead>

                        <tbody>
                            <tr>
                                <td class="row-title">月間給与額（千ウォン）</td>
                                <c:forEach var="row" items="${monthlyPersonalStatistics.rows}">
                                    <td><fmt:formatNumber value="${row.totalPayment / 1000}" pattern="#,##0" /></td>
                                </c:forEach>
                                <td class="total-column"><fmt:formatNumber value="${monthlyPersonalStatistics.totalPayment / 1000}" pattern="#,##0" /></td>
                            </tr>

                            <tr>
                                <td class="sub-title">└ 控除額（千ウォン）</td>
                                <c:forEach var="row" items="${monthlyPersonalStatistics.rows}">
                                    <td><fmt:formatNumber value="${row.totalDeduction / 1000}" pattern="#,##0" /></td>
                                </c:forEach>
                                <td class="total-column"><fmt:formatNumber value="${monthlyPersonalStatistics.totalDeduction / 1000}" pattern="#,##0" /></td>
                            </tr>

                            <tr>
                                <td class="sub-title">└ 差引支給額（千ウォン）</td>
                                <c:forEach var="row" items="${monthlyPersonalStatistics.rows}">
                                    <td><fmt:formatNumber value="${row.netPayment / 1000}" pattern="#,##0" /></td>
                                </c:forEach>
                                <td class="total-column"><fmt:formatNumber value="${monthlyPersonalStatistics.netPayment / 1000}" pattern="#,##0" /></td>
                            </tr>
                        </tbody>
                    </table>
                </div>

            </c:if>

        </div>
    </div>

    <!-- 스크립트 영역 -->
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const modal = document.getElementById("employeeModal");
            const openButton = document.getElementById("openEmployeeModal");
            const closeButton = document.getElementById("closeEmployeeModal");
            const cancelButton = document.getElementById("cancelEmployeeButton");
            const selectButton = document.getElementById("selectEmployeeButton");
            const employeeIdInput = document.getElementById("employeeId");
            const employeeNameInput = document.getElementById("selectedEmployeeName");
            const keywordInput = document.getElementById("employeeKeyword");
            const departmentFilter = document.getElementById("departmentFilter");
            const statusFilter = document.getElementById("statusFilter");
            const employeeRows = Array.from(document.querySelectorAll(".employee-row"));
            let selectedRow = null;

            const departments = new Set();
            employeeRows.forEach(function(row) {
                const department = row.dataset.department;
                if (department && department !== '-') {
                    departments.add(department);
                }
            });

            Array.from(departments).sort().forEach(function(department) {
                const option = document.createElement("option");
                option.value = department;
                option.textContent = department;
                departmentFilter.appendChild(option);
            });

            employeeRows.forEach(function(row) {
                if (row.dataset.employeeId === employeeIdInput.value) {
                    row.classList.add("selected");
                    selectedRow = row;
                }
            });

            function filterEmployees() {
                const keyword = keywordInput.value.trim().toLowerCase();
                const department = departmentFilter.value;
                const status = statusFilter.value;

                employeeRows.forEach(function(row) {
                    const name = row.dataset.name.toLowerCase();
                    const rowDepartment = row.dataset.department;
                    const rowStatus = row.dataset.status;

                    const keywordMatched = keyword === "" || name.includes(keyword);
                    const departmentMatched = department === "" || rowDepartment === department;
                    const statusMatched = status === "" || rowStatus === status;

                    row.style.display = keywordMatched && departmentMatched && statusMatched ? "" : "none";
                });
            }

            openButton.addEventListener("click", function() { modal.style.display = "block"; });
            closeButton.addEventListener("click", function() { modal.style.display = "none"; });
            cancelButton.addEventListener("click", function() { modal.style.display = "none"; });

            employeeRows.forEach(function(row) {
                row.addEventListener("click", function() {
                    employeeRows.forEach(function(otherRow) {
                        otherRow.classList.remove("selected");
                    });
                    row.classList.add("selected");
                    selectedRow = row;
                });

                row.addEventListener("dblclick", function() {
                    employeeIdInput.value = row.dataset.employeeId;
                    employeeNameInput.value = row.dataset.name;
                    modal.style.display = "none";
                });
            });

            selectButton.addEventListener("click", function() {
                if (selectedRow == null) {
                    alert("社員を選択してください。");
                    return;
                }
                employeeIdInput.value = selectedRow.dataset.employeeId;
                employeeNameInput.value = selectedRow.dataset.name;
                modal.style.display = "none";
            });

            keywordInput.addEventListener("input", filterEmployees);
            departmentFilter.addEventListener("change", filterEmployees);
            statusFilter.addEventListener("change", filterEmployees);

            window.addEventListener("click", function(event) {
                if (event.target === modal) {
                    modal.style.display = "none";
                }
            });
        });
    </script>

    <script>
        const monthLabels = [ "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" ];

        const deductionData = [
            <c:forEach var="row" items="${monthlyPersonalStatistics.rows}" varStatus="status">
                ${row.totalDeduction / 1000}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        const netPaymentData = [
            <c:forEach var="row" items="${monthlyPersonalStatistics.rows}" varStatus="status">
                ${row.netPayment / 1000}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        const monthlyPaymentData = [
            <c:forEach var="row" items="${monthlyPersonalStatistics.rows}" varStatus="status">
                ${row.totalPayment / 1000}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        const axisColor = "#666666";

        function formatThousandWon(value) {
            return Math.round(Number(value)).toLocaleString("ko-KR");
        }

        const monthlyPersonalCanvas = document.getElementById("monthlyPersonalChart");

        if (monthlyPersonalCanvas) {
            new Chart(monthlyPersonalCanvas, {
                type : "bar",
                data : {
                    labels : monthLabels,
                    datasets : [
                            {
                                label : "控除額（千ウォン）",
                                data : deductionData,
                                stack : "payment",
                                backgroundColor : "rgba(231, 74, 59, 0.85)", /* 포인트 붉은색 톤으로 변경 */
                                borderColor : "rgba(231, 74, 59, 1)",
                                borderWidth : 1,
                                barPercentage : 0.6,
                                categoryPercentage : 0.8,
                                datalabels : {
                                    color : "#fff",
                                    anchor : "center",
                                    align : "center",
                                    font : { size : 12, weight: 'bold' },
                                    display : function(context) {
                                        return Number(context.dataset.data[context.dataIndex]) > 0;
                                    },
                                    formatter : function(value) {
                                        return formatThousandWon(value);
                                    }
                                }
                            },
                            {
                                label : "差引支給額（千ウォン）",
                                data : netPaymentData,
                                stack : "payment",
                                backgroundColor : "rgba(78, 115, 223, 0.85)", /* 메인 파란색 톤으로 변경 */
                                borderColor : "rgba(78, 115, 223, 1)",
                                borderWidth : 1,
                                barPercentage : 0.6,
                                categoryPercentage : 0.8,
                                datalabels : {
                                    color : "#fff",
                                    anchor : "center",
                                    align : "center",
                                    font : { size : 12, weight: 'bold' },
                                    display : function(context) {
                                        return Number(context.dataset.data[context.dataIndex]) > 0;
                                    },
                                    formatter : function(value) {
                                        return formatThousandWon(value);
                                    }
                                }
                            } ]
                },
                plugins : [ ChartDataLabels ],
                options : {
                    responsive : true,
                    maintainAspectRatio : false,
                    layout : { padding : { top : 24 } },
                    interaction : { mode : "index", intersect : false },
                    plugins : {
                        legend : {
                            position : "bottom",
                            labels : { boxWidth : 12, boxHeight : 12, color : "#333", font: {size: 13} }
                        },
                        tooltip : {
                            mode : "index",
                            intersect : false,
                            backgroundColor: "rgba(255, 255, 255, 0.9)",
                            titleColor: "#333",
                            bodyColor: "#333",
                            borderColor: "#ccc",
                            borderWidth: 1,
                            callbacks : {
                                title : function(items) {
                                    if (items.length === 0) return "";
                                    return items[0].label;
                                },
                                beforeBody : function(items) {
                                    if (items.length === 0) return "";
                                    const payment = monthlyPaymentData[items[0].dataIndex];
                                    if (payment === null || payment === undefined) return "";
                                    return "月間給与額（千ウォン）: " + formatThousandWon(payment);
                                },
                                label : function(context) {
                                    return context.dataset.label + " : " + formatThousandWon(context.parsed.y);
                                }
                            }
                        }
                    },
                    scales : {
                        x : {
                            stacked : true,
                            grid : { display : false },
                            ticks : { color : axisColor, font: {size: 13} }
                        },
                        y : {
                            type : "linear",
                            position : "left",
                            stacked : true,
                            beginAtZero : true,
                            grid : { color: "#eee" },
                            title : { display : true, text : "月間給与額（千ウォン）", color : axisColor, font: {weight: 'bold'} },
                            ticks : {
                                color : axisColor,
                                callback : function(value) { return Number(value).toLocaleString(); }
                            }
                        }
                    }
                }
            });
        }
    </script>

</body>
</html>