<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여 항목 설정</title>
<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	margin: 20px;
	color: #333;
}

.page-header {
	margin-bottom: 25px;
}

.page-header h1 {
	font-size: 22px;
	margin: 0 0 5px 0;
	display: flex;
	align-items: center;
	gap: 8px;
}

.page-header p {
	font-size: 13px;
	color: #666;
	margin: 0;
}

.section-title {
	font-size: 16px;
	font-weight: bold;
	margin-bottom: 12px;
}

.section-title .count {
	font-size: 13px;
	color: #888;
	font-weight: normal;
}

.container {
	display: flex;
	gap: 30px;
	align-items: flex-start;
}

/* 좌측 테이블 영역 */
.table-section {
	flex: 1.3;
}

table.data-table {
	width: 100%;
	border-collapse: collapse;
	text-align: center;
	font-size: 13px;
}

table.data-table th {
	background-color: #f8fafc;
	border: 1px solid #e2e8f0;
	padding: 10px 6px;
	color: #475569;
	font-weight: bold;
}

table.data-table td {
	border: 1px solid #e2e8f0;
	padding: 0;
}

table.data-table td a {
	display: block;
	padding: 10px 6px;
	color: inherit;
	text-decoration: none;
	width: 100%;
	box-sizing: border-box;
}

table.data-table tr:hover {
	background-color: #f1f5f9;
}

.badge-use {
	display: inline-block;
	width: 16px;
	height: 16px;
	background-color: #f43f5e;
	color: #fff;
	font-size: 10px;
	font-weight: bold;
	line-height: 16px;
	border-radius: 3px;
}

/* 우측 입력 폼 영역 */
.form-section {
	flex: 1;
	border-top: 2px solid #333;
	padding-top: 10px;
}

.form-table {
	width: 100%;
	border-collapse: collapse;
}

.form-table th {
	text-align: left;
	padding: 10px 8px;
	font-size: 13px;
	font-weight: bold;
	color: #444;
	width: 30%;
	vertical-align: middle;
}

.form-table td {
	padding: 10px 8px;
	font-size: 13px;
	border-bottom: 1px solid #eee;
}

.input-text {
	width: 100%;
	padding: 6px 8px;
	border: 1px solid #cbd5e1;
	border-radius: 3px;
	box-sizing: border-box;
	font-size: 13px;
}

.select-box {
	width: 100%;
	padding: 6px 8px;
	border: 1px solid #cbd5e1;
	border-radius: 3px;
	font-size: 13px;
}

.radio-label {
	margin-right: 15px;
	cursor: pointer;
}

/* 하단 버튼 영역 */
.btn-group {
	display: flex;
	gap: 8px;
	justify-content: center;
	margin-top: 25px;
}

.btn {
	padding: 8px 18px;
	border: none;
	border-radius: 4px;
	font-size: 13px;
	font-weight: bold;
	cursor: pointer;
	color: #fff;
	text-decoration: none;
	display: inline-block;
}

.btn-blue {
	background-color: #3b82f6;
}

.btn-blue:hover {
	background-color: #2563eb;
}

.btn-gray {
	background-color: #64748b;
}

.btn-gray:hover {
	background-color: #475569;
}
</style>
</head>
<body>

	<!-- 헤더 영역 -->
	<div class="page-header">
		<h1>💰 급여 항목 설정</h1>
		<p>급여와 연관된 지급 및 공제 항목을 설정하는 메뉴입니다. 회사실정에 맞추어 설정하실 수 있습니다.</p>
	</div>

	<div class="container">
		<!-- [1] 좌측: 목록 테이블 -->
		<div class="table-section">
			<div class="section-title">
				지급항목 설정 <span class="count">(항목 수: ${wageList != null ? wageList.size() : 0}개)</span>
			</div>

			<table class="data-table">
				<thead>
					<tr>
						<th style="width: 20%;">지급항목</th>
						<th style="width: 25%;">과세여부</th>
						<th style="width: 15%;">비과세한도액</th>
						<th style="width: 12%;">절사단위</th>
						<th style="width: 18%;">근태연결/일괄지급</th>
						<th style="width: 10%;">사용여부</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="wage" items="${wageList}">
						<c:set var="wageUrl"
							value="wageTypeSetting.do?selectedWageId=${wage.wageTypeId}&wageTypeName=${wage.wageTypeName}&taxableYn=${wage.taxableYn}&itemType=${wage.itemType}&taxFreeLimit=${wage.taxFreeLimit}&attendanceOrLumpsumContent=${wage.attendanceOrLumpsumContent}&numberCut=${wage.numberCut}&attendanceOrLumpsum=${wage.attendanceOrLumpsum}&usage=${wage.usage}" />
						<tr>
							<td><a href="${wageUrl}">${wage.wageTypeName}</a></td>
							<td>
								<a href="${wageUrl}">
									<c:choose>
										<c:when test="${wage.taxableYn eq 'N'}">
											${not empty wage.attendanceOrLumpsumContent ? wage.attendanceOrLumpsumContent : '비과세'}
										</c:when>
										<c:otherwise>
											전체과세
										</c:otherwise>
									</c:choose>
								</a>
							</td>
							<td style="text-align: right;"><a href="${wageUrl}"><fmt:formatNumber
										value="${wage.taxFreeLimit}" pattern="#,##0" /></a></td>
							<td><a href="${wageUrl}">${empty wage.numberCut ? '없음' : wage.numberCut}</a></td>
							<td><a href="${wageUrl}">${empty wage.attendanceOrLumpsum ? '-' : wage.attendanceOrLumpsum}</a></td>
							<td><a href="${wageUrl}"> <c:if
										test="${wage.usage == 'Y'}">
										<span class="badge-use">O</span>
									</c:if>
							</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- [2] 우측: 입력/수정 폼 -->
		<div class="form-section">
			<form id="wageForm" method="post">
				<input type="hidden" name="wageTypeId"
					value="${param.selectedWageId}">

				<table class="form-table">
					<tr>
						<th>지급항목</th>
						<td><input type="text" name="wageTypeName"
							value="${param.wageTypeName}" class="input-text"
							placeholder="지급 항목을 입력해주세요."></td>
					</tr>
					<tr>
						<th>과세여부</th>
						<td><label class="radio-label"> <input type="radio"
								name="taxableYn" value="Y"
								${empty param.taxableYn || param.taxableYn == 'Y' ? 'checked' : ''}>
								전체과세
						</label> <label class="radio-label"> <input type="radio"
								name="taxableYn" value="N"
								${param.taxableYn == 'N' ? 'checked' : ''}> 비과세
						</label></td>
					</tr>
					<tr>
						<th>비과세명</th>
						<td><input type="text" name="attendanceOrLumpsumContent"
							value="${param.attendanceOrLumpsumContent}" class="input-text"
							placeholder="비과세 명칭을 입력하세요."></td>
					</tr>
					<tr>
						<th>비과세 한도액</th>
						<td><input type="number" name="taxFreeLimit"
							value="${empty param.taxFreeLimit ? 0 : param.taxFreeLimit}"
							class="input-text" style="width: 80%; text-align: right;">
							원</td>
					</tr>
					<tr>
						<th>계산방법</th>
						<td><input type="text" name="calcMethod"
							value="${param.calcMethod}" class="input-text"
							placeholder="계산방법을 입력해주세요."></td>
					</tr>
					<tr>
						<th>절사단위</th>
						<td><select name="numberCut" class="select-box">
								<option value="없음"
									${empty param.numberCut || param.numberCut == '없음' ? 'selected' : ''}>없음</option>
								<option value="1원"
									${param.numberCut == '1원' || param.numberCut == '원' ? 'selected' : ''}>1원 단위</option>
								<option value="10원"
									${param.numberCut == '10원' || param.numberCut == '십원' ? 'selected' : ''}>10원 단위</option>
								<option value="100원"
									${param.numberCut == '100원' ? 'selected' : ''}>100원 단위</option>
						</select></td>
					</tr>
					<tr>
						<th>근태연결/일괄지급</th>
						<td><select name="attendanceOrLumpsum" class="select-box">
								<option value="">선택하세요.</option>
								<option value="일괄지급"
									${param.attendanceOrLumpsum == '일괄지급' ? 'selected' : ''}>일괄지급</option>
								<option value="연장근무"
									${param.attendanceOrLumpsum == '연장근무' ? 'selected' : ''}>연장근무</option>
								<option value="휴일근무"
									${param.attendanceOrLumpsum == '휴일근무' ? 'selected' : ''}>휴일근무</option>
						</select></td>
					</tr>
					<tr>
						<th>사용여부</th>
						<td><label class="radio-label"> <input type="radio"
								name="usage" value="Y"
								${empty param.usage || param.usage == 'Y' ? 'checked' : ''}>
								사용
						</label> <label class="radio-label"> <input type="radio"
								name="usage" value="N" ${param.usage == 'N' ? 'checked' : ''}>
								사용안함
						</label></td>
					</tr>
				</table>

				<!-- 하단 버튼 그룹 -->
				<div class="btn-group">
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeSave.do">추가</button>
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeUpdate.do">수정</button>
					<button type="submit" class="btn btn-gray"
						formaction="${pageContext.request.contextPath}/wageTypeDelete.do">삭제</button>
					<a href="${pageContext.request.contextPath}/wageTypeSetting.do"
						class="btn btn-gray">내용 지우기</a>
				</div>
			</form>
		</div>
	</div>

</body>
</html>