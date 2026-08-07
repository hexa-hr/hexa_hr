<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Calendar"%>
<%
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    if (request.getAttribute("defaultStartDate") == null) {
        request.setAttribute("defaultStartDate", currentYear + "-01-01");
    }
    if (request.getAttribute("defaultEndDate") == null) {
        request.setAttribute("defaultEndDate", currentYear + "-12-31");
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>휴가/근태 설정</title>
<style>
    body { font-family: sans-serif; margin: 20px; color: #333; }
    
    /* 헤더 스타일 */
    .page-header { margin-bottom: 20px; }
    .page-header h1 { font-size: 24px; margin-bottom: 5px; }
    .page-header p { font-size: 14px; color: #666; margin: 0; }

    /* 레이아웃 */
    .container { display: flex; gap: 30px; align-items: flex-start; margin-bottom: 40px; }
    .table-section { flex: 1.2; }
    .form-section { flex: 1; background: #f9f9f9; padding: 20px; border: 1px solid #ddd; }

    /* 표 스타일 */
    table { width: 100%; border-collapse: collapse; text-align: center; }
    th, td { border: 1px solid #ccc; padding: 10px; font-size: 14px; }
    th { background: #eee; }
    
    /* 행 클릭 인터랙션 */
    .vacation-row, .attendance-row { cursor: pointer; }
    .vacation-row:hover, .attendance-row:hover { background-color: #f1f5f9; }

    /* 폼 스타일 */
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; font-weight: bold; margin-bottom: 5px; font-size: 14px; }
    .form-group input[type="text"], .form-group input[type="date"], .form-group select { width: 100%; padding: 8px; box-sizing: border-box; }
    .date-range { display: flex; align-items: center; gap: 5px; }
    .group-input-group { display: flex; gap: 5px; }
    .group-input-group select { flex: 1; }
    .radio-group label { font-weight: normal; margin-right: 15px; cursor: pointer; }
    .error-msg { color: red; font-size: 12px; margin-top: 3px; display: block; }

    /* 구분선 */
    .section-divider { border: 0; border-top: 2px solid #ccc; margin: 40px 0; }

    /* 버튼 스타일 */
    .btn-group { display: flex; gap: 8px; margin-top: 20px; justify-content: center; }
    .btn { padding: 8px 18px; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: bold; color: #fff; }
    .btn-primary { background-color: #3b82f6; } /* 추가 / 수정 */
    .btn-secondary { background-color: #9ca3af; } /* 삭제 / 내용 지우기 / 관리 */
</style>
</head>
<body>

    <!-- ================= [1] 휴가항목 설정 영역 ================= -->
    <div class="page-header">
        <h1>휴가/근태 설정</h1>
        <p>급여와 연관된 휴가 및 근태항목을 설정하는 메뉴입니다. 회사실정에 맞추어 설정하실 수 있습니다.</p>
    </div>

    <div class="container">
        <!-- 왼쪽: 휴가항목 목록 표 -->
        <div class="table-section">
            <h3>휴가항목 목록</h3>
            <table>
                <thead>
                    <tr>
                        <th>휴가항목</th>
                        <th>적용기간</th>
                        <th>사원별 휴가일수</th>
                        <th>사용여부</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="vacation" items="${vacationList}">
                        <tr class="vacation-row"
                            data-id="${vacation.vacationTypeId}"
                            data-name="${vacation.vacationTypeName}"
                            data-period1="${vacation.applyPeriod1}"
                            data-period2="${vacation.applyPeriod2}"
                            data-usage="${vacation.usage}">
                            <td>${vacation.vacationTypeName}</td>
                            <td>${vacation.applyPeriod1} ~ ${vacation.applyPeriod2}</td>
                            <td>${vacation.vacationDays}일</td>
                            <td>
                                <c:choose>
                                    <c:when test="${vacation.usage == 'Y'}">사용</c:when>
                                    <c:otherwise>사용안함</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty vacationList}">
                        <tr>
                            <td colspan="4">등록된 휴가항목이 없습니다.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- 오른쪽: 휴가항목 입력 폼 -->
        <div class="form-section">
            <form id="vacationForm" method="post">
                <input type="hidden" name="vacationTypeId" id="vacationTypeId" value="" />

                <div class="form-group">
                    <label>휴가항목</label>
                    <input type="text" name="vacationTypeName" id="vacationTypeName" value="${param.vacationTypeName}" placeholder="휴가항목을 입력해주세요.">
                </div>

                <div class="form-group">
                    <label>적용기간</label>
                    <div class="date-range">
                        <input type="date" name="applyPeriod1" id="applyPeriod1" value="${empty param.applyPeriod1 ? defaultStartDate : param.applyPeriod1}">
                        <span>~</span>
                        <input type="date" name="applyPeriod2" id="applyPeriod2" value="${empty param.applyPeriod2 ? defaultEndDate : param.applyPeriod2}">
                    </div>
                </div>

                <div class="form-group">
                    <label>사용여부</label>
                    <div class="radio-group">
                        <label><input type="radio" name="usage" id="usageY" value="Y" checked> 사용</label>
                        <label><input type="radio" name="usage" id="usageN" value="N"> 사용안함</label>
                    </div>
                </div>

                <div class="btn-group">
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('vacationForm', '${pageContext.request.contextPath}/vacationTypeSave.do')">추가</button>
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('vacationForm', '${pageContext.request.contextPath}/vacationTypeUpdate.do')">수정</button>
                    <button type="button" class="btn btn-secondary" onclick="fnSubmit('vacationForm', '${pageContext.request.contextPath}/vacationTypeDelete.do')">삭제</button>
                    <button type="button" class="btn btn-secondary" onclick="fnResetVacationForm()">내용 지우기</button>
                </div>
            </form>
        </div>
    </div>


    <hr class="section-divider">


    <!-- ================= [2] 근태항목 설정 영역 ================= -->
    <div class="page-header">
        <h1>근태항목 설정</h1>
        <p>급여 계산 시 반영될 근태항목을 설정합니다.</p>
    </div>

    <div class="container">
        <!-- 왼쪽: 근태항목 목록 표 -->
        <div class="table-section">
            <h3>근태항목 목록</h3>
            <table>
                <thead>
                    <tr>
                        <th>근태항목</th>
                        <th>단위</th>
                        <th>근태그룹</th>
                        <th>휴가공제</th>
                        <th>사용여부</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="att" items="${attendanceList}">
                        <tr class="attendance-row"
                            data-id="${att.attendanceTypeId}"
                            data-name="${att.name}"
                            data-unit="${att.unit}"
                            data-groupid="${att.attendanceGroupId}"
                            data-vacationid="${att.vacationTypeId}"
                            data-usage="${att.usage}">
                            <td>${att.name}</td>
                            <td>${att.unit}</td>
                            <td>${att.attendanceGroupName}</td>
                            <td>${att.vacationTypeName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${att.usage == 'Y'}">사용</c:when>
                                    <c:otherwise>사용안함</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty attendanceList}">
                        <tr>
                            <td colspan="5">등록된 근태항목이 없습니다.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <!-- 오른쪽: 근태항목 입력 폼 -->
        <div class="form-section">
            <form id="attendanceForm" method="post">
                <input type="hidden" name="attendanceTypeId" id="attendanceTypeId" value="" />

                <!-- 1. 근태항목 -->
                <div class="form-group">
                    <label>근태항목</label>
                    <input type="text" name="name" id="attName" placeholder="근태항목을 입력해주세요.">
                </div>

                <!-- 2. 단위 -->
                <div class="form-group">
                    <label>단위</label>
                    <select name="unit" id="attUnit">
                        <option value="">선택하세요</option>
                        <option value="일">일</option>
                        <option value="시간">시간</option>
                    </select>
                </div>

                <!-- 3. 근태그룹 (관리 버튼 포함) -->
                <div class="form-group">
                    <label>근태그룹</label>
                    <div class="group-input-group">
                        <select name="attendanceGroupId" id="attGroupId">
                            <option value="">선택하세요</option>
                            <c:forEach var="group" items="${attendanceGroupList}">
                                <%-- 수정 포인트: groupName -> attendanceGroupName --%>
                                <option value="${group.attendanceGroupId}">${group.attendanceGroupName}</option>
                            </c:forEach>
                        </select>
                        <button type="button" class="btn btn-secondary" onclick="openAttendanceGroupPopup()">관리</button>
                    </div>
                </div>

                <!-- 4. 휴가공제 (기존 휴가항목 연동) -->
                <div class="form-group">
                    <label>휴가공제</label>
                    <select name="vacationTypeId" id="attVacationTypeId">
                        <option value="">선택하세요</option>
                        <c:forEach var="vac" items="${vacationList}">
                            <option value="${vac.vacationTypeId}">${vac.vacationTypeName}</option>
                        </c:forEach>
                    </select>
                </div>

                <!-- 5. 사용여부 -->
                <div class="form-group">
                    <label>사용여부</label>
                    <div class="radio-group">
                        <label><input type="radio" name="usage" id="attUsageY" value="Y" checked> 사용</label>
                        <label><input type="radio" name="usage" id="attUsageN" value="N"> 사용안함</label>
                    </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="btn-group">
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('attendanceForm', '${pageContext.request.contextPath}/attendanceTypeSave.do')">추가</button>
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('attendanceForm', '${pageContext.request.contextPath}/attendanceTypeUpdate.do')">수정</button>
                    <button type="button" class="btn btn-secondary" onclick="fnSubmit('attendanceForm', '${pageContext.request.contextPath}/attendanceTypeDelete.do')">삭제</button>
                    <button type="button" class="btn btn-secondary" onclick="fnResetAttendanceForm()">내용 지우기</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const DEFAULT_START_DATE = "${defaultStartDate}";
        const DEFAULT_END_DATE = "${defaultEndDate}";

        document.addEventListener("DOMContentLoaded", function () {
            // 휴가항목 행 클릭 시 데이터 채우기
            document.querySelectorAll(".vacation-row").forEach(row => {
                row.addEventListener("click", function () {
                    document.getElementById("vacationTypeId").value = this.dataset.id || "";
                    document.getElementById("vacationTypeName").value = this.dataset.name || "";
                    document.getElementById("applyPeriod1").value = this.dataset.period1 || DEFAULT_START_DATE;
                    document.getElementById("applyPeriod2").value = this.dataset.period2 || DEFAULT_END_DATE;
                    if (this.dataset.usage === "N") {
                        document.getElementById("usageN").checked = true;
                    } else {
                        document.getElementById("usageY").checked = true;
                    }
                });
            });

            // 근태항목 행 클릭 시 데이터 채우기
            document.querySelectorAll(".attendance-row").forEach(row => {
                row.addEventListener("click", function () {
                    document.getElementById("attendanceTypeId").value = this.dataset.id || "";
                    document.getElementById("attName").value = this.dataset.name || "";
                    document.getElementById("attUnit").value = this.dataset.unit || "";
                    document.getElementById("attGroupId").value = this.dataset.groupid || "";
                    document.getElementById("attVacationTypeId").value = this.dataset.vacationid || "";
                    if (this.dataset.usage === "N") {
                        document.getElementById("attUsageN").checked = true;
                    } else {
                        document.getElementById("attUsageY").checked = true;
                    }
                });
            });
        });

        // 공용 폼 전송 함수
        function fnSubmit(formId, url) {
            const form = document.getElementById(formId);
            form.action = url;
            form.submit();
        }

        // 휴가항목 폼 초기화
        function fnResetVacationForm() {
            document.getElementById("vacationTypeId").value = "";
            document.getElementById("vacationTypeName").value = "";
            document.getElementById("applyPeriod1").value = DEFAULT_START_DATE;
            document.getElementById("applyPeriod2").value = DEFAULT_END_DATE;
            document.getElementById("usageY").checked = true;
        }

        // 근태항목 폼 초기화
        function fnResetAttendanceForm() {
            document.getElementById("attendanceTypeId").value = "";
            document.getElementById("attName").value = "";
            document.getElementById("attUnit").value = "";
            document.getElementById("attGroupId").value = "";
            document.getElementById("attVacationTypeId").value = "";
            document.getElementById("attUsageY").checked = true;
        }

        // 근태그룹 관리 팝업 창 호출 함수
        function openAttendanceGroupPopup() {
            const popupUrl = "${pageContext.request.contextPath}/attendanceGroupManage.do";
            const width = 450;
            const height = 500;
            const left = (window.innerWidth - width) / 2;
            const top = (window.innerHeight - height) / 2;
            
            window.open(popupUrl, "attendanceGroupPopup", `width=\${width},height=\${height},left=\${left},top=\${top},resizable=yes,scrollbars=yes`);
        }
    </script>
</body>
</html>