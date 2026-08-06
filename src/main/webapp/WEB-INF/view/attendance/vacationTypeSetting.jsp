<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Calendar"%>
<%
    // 서버측 컨트롤러에서 defaultStartDate/EndDate를 전달하지 않았을 경우를 대비한 기본값 설정
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
    .page-header { margin-bottom: 30px; }
    .page-header h1 { font-size: 24px; margin-bottom: 5px; }
    .page-header p { font-size: 14px; color: #666; margin: 0; }

    /* 레이아웃 */
    .container { display: flex; gap: 30px; align-items: flex-start; }
    .table-section { flex: 1.2; }
    .form-section { flex: 1; background: #f9f9f9; padding: 20px; border: 1px solid #ddd; }

    /* 표 스타일 */
    table { width: 100%; border-collapse: collapse; text-align: center; }
    th, td { border: 1px solid #ccc; padding: 10px; font-size: 14px; }
    th { background: #eee; }
    
    /* 행 클릭 인터랙션 */
    .vacation-row { cursor: pointer; }
    .vacation-row:hover { background-color: #f1f5f9; }

    /* 폼 스타일 */
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; font-weight: bold; margin-bottom: 5px; font-size: 14px; }
    .form-group input[type="text"], .form-group input[type="date"] { width: 100%; padding: 8px; box-sizing: border-box; }
    .date-range { display: flex; align-items: center; gap: 5px; }
    .radio-group label { font-weight: normal; margin-right: 15px; cursor: pointer; }
    .error-msg { color: red; font-size: 12px; margin-top: 3px; display: block; }

    /* 버튼 스타일 (첨부 이미지 디자인 적용) */
    .btn-group { display: flex; gap: 8px; margin-top: 20px; justify-content: center; }
    .btn { padding: 8px 18px; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: bold; color: #fff; }
    .btn-primary { background-color: #3b82f6; } /* 추가 / 수정 */
    .btn-secondary { background-color: #9ca3af; } /* 삭제 / 내용 지우기 */
</style>
</head>
<body>

    <!-- 헤더 영역 -->
    <div class="page-header">
        <h1>휴가/근태 설정</h1>
        <p>급여와 연관된 휴가 및 근태항목을 설정하는 메뉴입니다. 회사실정에 맞추어 설정하실 수 있습니다.</p>
    </div>

    <!-- 메인 컨테이너 -->
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
            <form id="vacationForm" action="vacationTypeSave.do" method="post">
                
                <!-- 식별용 PK (Hidden) -->
                <input type="hidden" name="vacationTypeId" id="vacationTypeId" value="" />

                <!-- 1. 휴가항목 -->
                <div class="form-group">
                    <label>휴가항목</label>
                    <input type="text" name="vacationTypeName" id="vacationTypeName" value="${param.vacationTypeName}" placeholder="휴가항목을 입력해주세요.">
                    <c:if test="${errors.vacationTypeName}">
                        <span class="error-msg">휴가항목을 입력하세요.</span>
                    </c:if>
                </div>

                <!-- 2. 적용기간 -->
                <div class="form-group">
                    <label>적용기간</label>
                    <div class="date-range">
                        <input type="date" name="applyPeriod1" id="applyPeriod1" value="${empty param.applyPeriod1 ? defaultStartDate : param.applyPeriod1}">
                        <span>~</span>
                        <input type="date" name="applyPeriod2" id="applyPeriod2" value="${empty param.applyPeriod2 ? defaultEndDate : param.applyPeriod2}">
                    </div>
                    <c:if test="${errors.applyPeriod}">
                        <span class="error-msg">적용기간을 올바르게 선택하세요.</span>
                    </c:if>
                </div>

                <!-- 3. 사용여부 -->
                <div class="form-group">
                    <label>사용여부</label>
                    <div class="radio-group">
                        <label>
                            <input type="radio" name="usage" id="usageY" value="Y" <c:if test="${empty param.usage || param.usage == 'Y'}">checked</c:if>> 사용
                        </label>
                        <label>
                            <input type="radio" name="usage" id="usageN" value="N" <c:if test="${param.usage == 'N'}">checked</c:if>> 사용안함
                        </label>
                    </div>
                </div>

                <!-- 버튼 영역 -->
                <div class="btn-group">
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('vacationTypeSave.do')">추가</button>
                    <button type="button" class="btn btn-primary" onclick="fnSubmit('vacationTypeUpdate.do')">수정</button>
                    <button type="button" class="btn btn-secondary" onclick="fnSubmit('vacationTypeDelete.do')">삭제</button>
                    <button type="button" class="btn btn-secondary" onclick="fnResetForm()">내용 지우기</button>
                </div>
            </form>
        </div>

    </div>

    <script>
        // 기본 시작일 / 종료일 상수 (초기화 시 사용)
        const DEFAULT_START_DATE = "${defaultStartDate}";
        const DEFAULT_END_DATE = "${defaultEndDate}";

        document.addEventListener("DOMContentLoaded", function () {
            const rows = document.querySelectorAll(".vacation-row");

            rows.forEach(row => {
                row.addEventListener("click", function () {
                    // 선택한 행의 데이터를 오른쪽 입력창에 채움
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
        });

        // 폼 전송 함수 (추가 / 수정 / 삭제)
        function fnSubmit(url) {
            const form = document.getElementById("vacationForm");
            form.action = "${pageContext.request.contextPath}/" + url;
            form.submit();
        }

        // 내용 지우기 (초기 기본값 상태로 복원)
        function fnResetForm() {
            document.getElementById("vacationTypeId").value = "";
            document.getElementById("vacationTypeName").value = "";
            document.getElementById("applyPeriod1").value = DEFAULT_START_DATE;
            document.getElementById("applyPeriod2").value = DEFAULT_END_DATE;
            document.getElementById("usageY").checked = true;
        }
    </script>
</body>
</html>