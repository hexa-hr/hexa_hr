<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<title>休暇/勤怠設定</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
/* 1. 전체 레이아웃 (1, 2페이지와 통일) */
body {
    margin: 0;
    min-width: 1400px;
    background-color: #f8f9fa;
    font-family: 'Malgun Gothic', sans-serif;
}

.wrap {
    display: flex;
    align-items: flex-start;
    width: 100%;
}

.container {
    padding: 30px 40px;
    font-family: sans-serif;
    flex: 1;
    box-sizing: border-box;
    background-color: white;
    display: flex;
    gap: 30px;
}

/* 2. 페이지 헤더 (컨테이너 안으로 들어가게 구조를 변경할 수도 있으나, CSS로만 처리) */
.page-header {
    background-color: white;
    padding: 30px 40px 10px 40px;
}

.page-header h1 {
    font-size: 22px; /* 타이틀 크기 통일 */
    margin: 0 0 10px 0;
    color: #333;
}

.page-header p {
    font-size: 14px;
    color: #666;
    margin: 0;
}

/* 3. 테이블 및 폼 레이아웃 */
.table-section {
    flex: 1.2;
}

.form-section {
    flex: 1;
    background: #f4f4f4; /* 사이드바와 동일한 배경색 톤 */
    padding: 20px;
    border: 1px solid #ddd;
    box-sizing: border-box;
}

h3 {
    font-size: 18px;
    font-weight: bold;
    margin-top: 0;
    margin-bottom: 10px;
    color: #333;
    border-bottom: 2px solid #4e73df;
    padding-bottom: 5px;
}

/* 4. 테이블 스타일 (1, 2페이지와 완벽 동일) */
table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
}

th, td {
    border: 1px solid #ccc;
    padding: 10px;
    font-size: 14px;
    white-space: nowrap;
    text-align: center;
}

th {
    background-color: #f8f9fa;
}

td a {
    text-decoration: none;
    color: inherit;
    display: block;
    width: 100%;
    box-sizing: border-box;
}

tr:hover {
    background-color: #f1f5f9;
}

/* 5. 폼 입력 영역 */
.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    font-weight: bold;
    margin-bottom: 5px;
    font-size: 14px;
    color: #333;
}

.form-group input[type="text"], .form-group input[type="date"], .form-group select {
    width: 100%;
    padding: 5px; /* 높이 통일 */
    border: 1px solid #ccc;
    box-sizing: border-box;
}

.date-range {
    display: flex;
    align-items: center;
    gap: 10px;
}

.group-input-group {
    display: flex;
    gap: 5px;
}

.group-input-group select {
    flex: 1;
}

.radio-group label {
    font-weight: normal;
    margin-right: 15px;
    cursor: pointer;
    font-size: 14px;
}

.section-divider {
    border: 0;
    border-top: 1px solid #ddd; /* 선명도 조절 */
    margin: 0 40px; /* 좌우 여백 확보 */
}

/* 6. 버튼 스타일 (이전 페이지들과 톤 통일) */
.btn-group {
    display: flex;
    gap: 8px;
    margin-top: 20px;
    justify-content: center;
}

.btn {
    padding: 8px 18px;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    text-decoration: none;
    display: inline-block;
}

.btn-primary {
    background-color: #4e73df; /* 메인 파란색으로 통일 */
}
.btn-primary:hover {
    background-color: #2e59d9;
}

.btn-secondary {
    background-color: #a5a5a5; /* 메인 회색으로 통일 */
}
.btn-secondary:hover {
    background-color: #858796;
}

.btn-manage {
    display: inline-block;
    width: auto;
    padding: 4px 10px;
    background: #4e73df;
    color: #fff !important;
    text-decoration: none;
    border-radius: 3px;
    font-size: 12px;
    font-weight: bold;
}
.btn-manage:hover {
    background-color: #2e59d9;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<!-- [1] 休暇項目設定 -->
	<div class="page-header">
		<h1>休暇/勤怠設定</h1>
		<p>給与に関連する休暇および勤怠項目を設定するメニューです。</p>
	</div>

	<div class="container">
		<div class="table-section">
			<h3>休暇項目一覧</h3>
			<table>
				<thead>
					<tr>
						<th>休暇項目</th>
						<th>適用期間</th>
						<th>社員別休暇日数</th>
						<th>使用有無</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="vacation" items="${vacationList}">
						<c:set var="vacUrl"
							value="vacationTypeSetting.do?selectedVacationId=${vacation.vacationTypeId}&vacationTypeName=${vacation.vacationTypeName}&applyPeriod1=${vacation.applyPeriod1}&applyPeriod2=${vacation.applyPeriod2}&usage=${vacation.usage}" />
						<tr>
							<td><a href="${vacUrl}">${vacation.vacationTypeName}</a></td>
							<td><a href="${vacUrl}">${vacation.applyPeriod1} ~
									${vacation.applyPeriod2}</a></td>
							<td style="padding: 5px 0;"><a
								href="vacationDaysManage.do?attendanceTypeId=${vacation.vacationTypeId}"
								target="_blank" class="btn-manage">관리</a></td>
							<td><a href="${vacUrl}">${vacation.usage == 'Y' ? '사용' : '사용안함'}</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="form-section">
			<form id="vacationForm" method="post">
				<input type="hidden" name="vacationTypeId"
					value="${param.selectedVacationId}" />
				<div class="form-group">
					<label>휴가항목</label> <input type="text" name="vacationTypeName"
						value="${param.vacationTypeName}" placeholder="휴가항목을 입력해주세요.">
				</div>
				<div class="form-group">
					<label>適用期間</label>
					<div class="date-range">
						<input type="date" name="applyPeriod1"
							value="${empty param.applyPeriod1 ? defaultStartDate : param.applyPeriod1}">
						<span>~</span> <input type="date" name="applyPeriod2"
							value="${empty param.applyPeriod2 ? defaultEndDate : param.applyPeriod2}">
					</div>
				</div>
				<div class="form-group">
					<label>使用有無</label>
					<div class="radio-group">
						<label><input type="radio" name="usage" value="Y"
							${empty param.usage || param.usage == 'Y' ? 'checked' : ''}>
							사용</label> <label><input type="radio" name="usage" value="N"
							${param.usage == 'N' ? 'checked' : ''}> 사용안함</label>
					</div>
				</div>
				<div class="btn-group">
					<button type="submit" class="btn btn-primary" formmethod="post"
						formaction="${pageContext.request.contextPath}/vacationTypeSave.do">追加</button>
					<button type="submit" class="btn btn-primary" formmethod="post"
						formaction="${pageContext.request.contextPath}/vacationTypeUpdate.do">修正</button>
					<button type="submit" class="btn btn-secondary" formmethod="post"
						formaction="${pageContext.request.contextPath}/vacationTypeDelete.do">削除</button>
					<a href="${pageContext.request.contextPath}/vacationTypeSetting.do"
						class="btn btn-secondary">クリア</a>
				</div>
			</form>
		</div>
	</div>

	<hr class="section-divider">

	<!-- [2] 勤怠項目設定 -->
	<div class="page-header">
		<h1>勤怠項目設定</h1>
		<p>給与計算時に反映される勤怠項目を設定します。</p>
	</div>

	<div class="container">
		<div class="table-section">
			<h3>勤怠項目一覧</h3>
			<table>
				<thead>
					<tr>
						<th>勤怠項目</th>
						<th>単位</th>
						<th>勤怠グループ</th>
						<th>休暇控除</th>
						<th>使用有無</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="att" items="${attendanceList}">
						<c:set var="attUrl"
							value="vacationTypeSetting.do?selectedAttId=${att.attendanceTypeId}&attName=${att.attendanceTypeName}&attUnit=${att.unit}&attGroupId=${att.attendanceGroupId}&attVacationId=${att.vacationTypeId}&attUsage=${att.usage}" />
						<tr>
							<td><a href="${attUrl}">${att.attendanceTypeName}</a></td>
							<td><a href="${attUrl}">${att.unit}</a></td>
							<td><a href="${attUrl}"> <c:set var="groupName" value="" />
									<c:forEach var="group" items="${attendanceGroupList}">
										<c:if
											test="${group.attendanceGroupId == att.attendanceGroupId}">
											<c:set var="groupName" value="${group.attendanceGroupName}" />
										</c:if>
									</c:forEach> ${empty groupName ? '-' : groupName}
							</a></td>
							<td><a href="${attUrl}"> <c:set var="vacName" value="" />
									<c:forEach var="vac" items="${vacationList}">
										<c:if test="${vac.vacationTypeId == att.vacationTypeId}">
											<c:set var="vacName" value="${vac.vacationTypeName}" />
										</c:if>
									</c:forEach> ${empty vacName ? '-' : vacName}
							</a></td>
							<td><a href="${attUrl}">${att.usage == 'Y' ? '사용' : '사용안함'}</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="form-section">
			<form id="attendanceForm" method="post">
				<input type="hidden" name="attendanceTypeId"
					value="${param.selectedAttId}" />
				<div class="form-group">
					<label>勤怠項目</label> <input type="text" name="name"
						value="${param.attName}" placeholder="勤怠項目を入力してください。">
				</div>
				<div class="form-group">
					<label>단위</label> <select name="unit">
						<option value="">선택하세요</option>
						<option value="일" ${param.attUnit == '일' ? 'selected' : ''}>일</option>
						<option value="시간" ${param.attUnit == '시간' ? 'selected' : ''}>시간</option>
					</select>
				</div>
				<div class="form-group">
					<label>勤怠グループ</label>
					<div class="group-input-group">
						<select name="attendanceGroupId">
							<option value="">選択してください</option>
							<c:forEach var="group" items="${attendanceGroupList}">
								<option value="${group.attendanceGroupId}"
									${param.attGroupId == group.attendanceGroupId ? 'selected' : ''}>${group.attendanceGroupName}</option>
							</c:forEach>
						</select> <a
							href="${pageContext.request.contextPath}/attendanceGroupManage.do"
							target="_blank" class="btn btn-secondary">管理</a>
					</div>
				</div>
				<div class="form-group">
					<label>休暇控除</label> <select name="vacationTypeId">
						<option value="">選択してください</option>
						<c:forEach var="vac" items="${vacationList}">
							<option value="${vac.vacationTypeId}"
								${param.attVacationId == vac.vacationTypeId ? 'selected' : ''}>${vac.vacationTypeName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="form-group">
					<label>使用有無</label>
					<div class="radio-group">
						<label><input type="radio" name="usage" value="Y"
							${empty param.attUsage || param.attUsage == 'Y' ? 'checked' : ''}>
							사용</label> <label><input type="radio" name="usage" value="N"
							${param.attUsage == 'N' ? 'checked' : ''}> 사용안함</label>
					</div>
				</div>
				<div class="btn-group">
					<button type="submit" class="btn btn-primary" formmethod="post"
						formaction="${pageContext.request.contextPath}/attendanceTypeSave.do">追加</button>
					<button type="submit" class="btn btn-primary" formmethod="post"
						formaction="${pageContext.request.contextPath}/attendanceTypeUpdate.do">修正</button>
					<button type="submit" class="btn btn-secondary" formmethod="post"
						formaction="${pageContext.request.contextPath}/attendanceTypeDelete.do">削除</button>
					<a href="${pageContext.request.contextPath}/vacationTypeSetting.do"
						class="btn btn-secondary">クリア</a>
				</div>
			</form>
		</div>
	</div>

	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<%-- セッションに errorMessage が存在する場合に実行 --%>
	<c:if test="${not empty sessionScope.errorMessage}">
		<script>
			alert("${sessionScope.errorMessage}");
		</script>
		<%-- 一度表示したエラーはセッションから削除 --%>
		<c:remove var="errorMessage" scope="session" />
	</c:if>

<script>
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('error') === 'inUse') {
            alert('現在使用中の休暇項目のため削除できません。');
            history.replaceState({}, null, location.pathname);
        }
    };
</script>

</body>
</html>