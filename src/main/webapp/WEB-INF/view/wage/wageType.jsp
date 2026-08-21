<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여 항목 설정</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	color: #333;
	margin: 0;
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
	min-width: 1100px;
	margin-bottom: 40px;
}

.section-divider {
	border: 0;
	border-top: 1px solid #cbd5e1;
	margin: 40px 0;
}

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

.badge-off {
	display: inline-block;
	width: 16px;
	height: 16px;
	background-color: #60a5fa;
	color: #fff;
	font-size: 10px;
	font-weight: bold;
	line-height: 16px;
	border-radius: 3px;
}

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

.input-text:disabled {
	background-color: #f1f5f9;
	color: #94a3b8;
	cursor: not-allowed;
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

.lump-sum-row {
	display: none;
}

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

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="page-header">
		<h1>💰 급여 항목 설정</h1>
		<p>급여와 연관된 지급 및 공제 항목을 설정하는 메뉴입니다. 회사실정에 맞추어 설정하실 수 있습니다.</p>
	</div>

	<!-- [상단] 지급항목 설정 영역 -->
	<div class="container">
		<div class="table-section">
			<div class="section-title">
				지급항목 설정 <span class="count">(항목 수: ${wageList != null ? wageList.size() : 0}개)</span>
			</div>

			<table class="data-table">
				<thead>
					<tr>
						<th style="width: 18%;">지급항목</th>
						<th style="width: 27%;">과세여부</th>
						<th style="width: 15%;">비과세한도액</th>
						<th style="width: 10%;">절사단위</th>
						<th style="width: 20%;">근태연결/일괄지급</th>
						<th style="width: 10%;">사용여부</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="wage" items="${wageList}">
						<c:set var="wageUrl"
							value="wageTypeSetting.do?selectedWageId=${wage.wageTypeId}&wageTypeName=${wage.wageTypeName}&taxableYn=${wage.taxableYn}&itemType=${wage.itemType}&taxFreeLimit=${wage.taxFreeLimit}&attendanceOrLumpsumContent=${wage.attendanceOrLumpsumContent}&taxFreeName=${wage.taxFreeName}&numberCut=${wage.numberCut}&attendanceOrLumpsum=${wage.attendanceOrLumpsum}&usage=${wage.usage}" />
						<tr>
							<td><a href="${wageUrl}">${wage.wageTypeName}</a></td>
							<td><a href="${wageUrl}"> <c:choose>
										<c:when test="${wage.taxableYn eq 'N'}">
											비과세_${wage.taxFreeName}
										</c:when>
										<c:otherwise>
											전체과세
										</c:otherwise>
									</c:choose>
							</a></td>
							<td style="text-align: right;"><a href="${wageUrl}"> <c:if
										test="${wage.taxableYn eq 'N' && wage.taxFreeLimit > 0}">
										<fmt:formatNumber value="${wage.taxFreeLimit}" pattern="#,##0" />
									</c:if>
							</a></td>
							<td><a href="${wageUrl}">${empty wage.numberCut ? '없음' : wage.numberCut}</a></td>
							<td><a href="${wageUrl}"> <c:choose>
										<c:when test="${wage.attendanceOrLumpsum eq '일괄지급'}">
											일괄지급_<fmt:formatNumber
												value="${wage.attendanceOrLumpsumContent}" pattern="#,##0" />
										</c:when>
										<c:otherwise>
											${wage.attendanceOrLumpsumContent}
										</c:otherwise>
									</c:choose>
							</a></td>
							<td><a href="${wageUrl}"> <c:choose>
										<c:when test="${wage.usage == 'Y'}">
											<span class="badge-use">O</span>
										</c:when>
										<c:otherwise>
											<span class="badge-off">X</span>
										</c:otherwise>
									</c:choose>
							</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- 지급항목 입력/수정 폼 -->
		<div class="form-section">
			<form id="wageForm" method="post"
				onsubmit="enableInputsBeforeSubmit()">
				<input type="hidden" name="wageTypeId"
					value="${param.selectedWageId}"> <input type="hidden"
					name="itemType" value="P">

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
								${empty param.taxableYn || param.taxableYn == 'Y' ? 'checked' : ''}
								onclick="toggleTaxFree()"> 전체과세
						</label> <label class="radio-label"> <input type="radio"
								name="taxableYn" value="N"
								${param.taxableYn == 'N' ? 'checked' : ''}
								onclick="toggleTaxFree()"> 비과세
						</label></td>
					</tr>
					<tr>
						<th>비과세명</th>
						<td><input type="text" id="taxFreeNameInput"
							name="taxFreeName" value="${param.taxFreeName}"
							class="input-text" placeholder="비과세 명칭을 입력하세요."></td>
					</tr>
					<tr>
						<th>비과세 한도액</th>
						<td><input type="number" id="taxFreeLimitInput"
							name="taxFreeLimit"
							value="${empty param.taxFreeLimit ? 0 : param.taxFreeLimit}"
							class="input-text" style="width: 80%; text-align: right;">
							원</td>
					</tr>
					<tr>
						<th>절사단위</th>
						<td><select name="numberCut" class="select-box">
								<option value="없음"
									${empty param.numberCut || param.numberCut == '없음' ? 'selected' : ''}>없음</option>
								<option value="1원"
									${param.numberCut == '1원' || param.numberCut == '원' ? 'selected' : ''}>1원
									단위</option>
								<option value="10원"
									${param.numberCut == '10원' || param.numberCut == '십원' ? 'selected' : ''}>10원
									단위</option>
								<option value="100원"
									${param.numberCut == '100원' ? 'selected' : ''}>100원 단위</option>
						</select></td>
					</tr>
					<tr>
						<th>근태연결/일괄지급</th>
						<td><select name="attendanceOrLumpsum"
							id="attendanceOrLumpsumSelect" class="select-box"
							onchange="toggleLumpSum()">
								<option value="">선택하세요.</option>
								<option value="연차"
									${param.attendanceOrLumpsum == '연차' ? 'selected' : ''}>연차</option>
								<option value="반차"
									${param.attendanceOrLumpsum == '반차' ? 'selected' : ''}>반차</option>
								<option value="지각"
									${param.attendanceOrLumpsum == '지각' ? 'selected' : ''}>지각</option>
								<option value="조퇴"
									${param.attendanceOrLumpsum == '조퇴' ? 'selected' : ''}>조퇴</option>
								<option value="외근"
									${param.attendanceOrLumpsum == '외근' ? 'selected' : ''}>외근</option>
								<option value="휴일근무"
									${param.attendanceOrLumpsum == '휴일근무' ? 'selected' : ''}>휴일근무</option>
								<option value="연장근무"
									${param.attendanceOrLumpsum == '연장근무' ? 'selected' : ''}>연장근무</option>
								<option value="포상휴가"
									${param.attendanceOrLumpsum == '포상휴가' ? 'selected' : ''}>포상휴가</option>
								<option value="야간근무"
									${param.attendanceOrLumpsum == '야간근무' ? 'selected' : ''}>야간근무</option>
								<option value="청원휴가"
									${param.attendanceOrLumpsum == '청원휴가' ? 'selected' : ''}>청원휴가</option>
								<option value="일괄지급"
									${param.attendanceOrLumpsum == '일괄지급' ? 'selected' : ''}>일괄지급</option>
						</select></td>
					</tr>

					<tr class="lump-sum-row" id="lumpSumRow">
						<th>일괄지급액</th>
						<td><input type="text" id="lumpSumInput"
							name="attendanceOrLumpsumContent"
							value="${param.attendanceOrLumpsum == '일괄지급' ? param.attendanceOrLumpsumContent : ''}"
							class="input-text" style="width: 80%; text-align: right;"
							placeholder="금액 입력"> 원</td>
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

				<div class="btn-group">
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeSave.do">추가</button>
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeUpdate.do">수정</button>
					<button type="submit" class="btn btn-gray"
						formaction="${pageContext.request.contextPath}/wageTypeDelete.do"
						onclick="return confirmWageDelete('${param.wageTypeName}');">삭제</button>
					<a href="${pageContext.request.contextPath}/wageTypeSetting.do"
						class="btn btn-gray">내용 지우기</a>
				</div>
			</form>
		</div>
	</div>

	<!-- 섹션 구분선 -->
	<hr class="section-divider">

	<!-- [하단] 공제항목 설정 영역 -->
	<div class="container">
		<div class="table-section">
			<div class="section-title">
				공제항목 설정 <span class="count">(항목 수: ${deductionList != null ? deductionList.size() : 0}개)</span>
			</div>

			<table class="data-table">
				<thead>
					<tr>
						<th style="width: 30%;">공제항목</th>
						<th style="width: 20%;">절사단위</th>
						<th style="width: 15%;">사용여부</th>
						<th style="width: 35%;">비고</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="ded" items="${deductionList}">
						<c:set var="dedUrl"
							value="wageTypeSetting.do?selectedDedId=${ded.wageTypeId}&dedName=${ded.wageTypeName}&numberCut=${ded.numberCut}&usage=${ded.usage}&calcMethod=${ded.attendanceOrLumpsumContent}&note=${ded.taxFreeName}" />
						<tr>
							<td><a href="${dedUrl}">${ded.wageTypeName}</a></td>
							<td><a href="${dedUrl}">${empty ded.numberCut ? '없음' : ded.numberCut}</a></td>
							<td><a href="${dedUrl}"> <c:choose>
										<c:when test="${ded.usage == 'Y'}">
											<span class="badge-use">O</span>
										</c:when>
										<c:otherwise>
											<span class="badge-off">X</span>
										</c:otherwise>
									</c:choose>
							</a></td>
							<td><a href="${dedUrl}">${ded.taxFreeName}</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<!-- 공제항목 입력/수정 폼 -->
		<div class="form-section">
			<form id="deductionForm" method="post">
				<input type="hidden" name="wageTypeId"
					value="${param.selectedDedId}"> <input type="hidden"
					name="itemType" value="D">

				<table class="form-table">
					<tr>
						<th>공제항목</th>
						<td><input type="text" name="wageTypeName"
							value="${param.dedName}" class="input-text"
							placeholder="공제항목을 입력해주세요."></td>
					</tr>
					<tr>
						<th>계산방법</th>
						<td><input type="text" name="attendanceOrLumpsumContent"
							value="${param.calcMethod}" class="input-text"
							placeholder="계산방법을 입력해주세요.">
							<div style="font-size: 11px; color: #666; margin-top: 4px;">
								<input type="checkbox"> 예시)
							</div></td>
					</tr>
					<tr>
						<th>절사단위</th>
						<td><select name="numberCut" class="select-box">
								<option value="없음"
									${empty param.numberCut || param.numberCut == '없음' ? 'selected' : ''}>없음</option>
								<option value="1원 단위"
									${param.numberCut == '1원 단위' || param.numberCut == '1원' ? 'selected' : ''}>1원
									단위</option>
								<option value="10원 단위"
									${param.numberCut == '10원 단위' || param.numberCut == '10원' ? 'selected' : ''}>10원
									단위</option>
								<option value="100원 단위"
									${param.numberCut == '100원 단위' || param.numberCut == '100원' ? 'selected' : ''}>100원
									단위</option>
						</select></td>
					</tr>
					<tr>
						<th>비고</th>
						<td><input type="text" name="taxFreeName"
							value="${empty param.note ? '기본항목' : param.note}"
							class="input-text" placeholder="비고 입력"></td>
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

				<div class="btn-group">
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeSave.do">추가</button>
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeUpdate.do">수정</button>
					<button type="submit" class="btn btn-gray"
						formaction="${pageContext.request.contextPath}/wageTypeDelete.do"
						onclick="return confirmDeductDelete('${param.dedName}');">삭제</button>
					<a href="${pageContext.request.contextPath}/wageTypeSetting.do"
						class="btn btn-gray">내용 지우기</a>
				</div>
			</form>
		</div>
	</div>

	<!-- 자바스크립트 제어 영역 -->
	<script>
		// 지급항목 삭제 전 경고창 기능
		function confirmWageDelete(wageName) {
			if (wageName === '기본급') {
				alert('기본급은 필수 항목이므로 삭제할 수 없습니다.');
				return false;
			}
			return confirm('정말 삭제하시겠습니까?');
		}

		// 공제항목 삭제 전 경고창 기능
		function confirmDeductDelete(dedName) {
			const fixedItems = [
				'국민연금', '건강보험', '장기요양보험', '고용보험', 
				'소득세', '지방소득세', '사업소득', '일용급여'
			];
			
			if (fixedItems.includes(dedName)) {
				alert(dedName + ' 항목은 필수 공제항목이므로 삭제할 수 없습니다.');
				return false;
			}
			return confirm('정말 삭제하시겠습니까?');
		}

		function toggleTaxFree() {
			const isNotTaxable = document
					.querySelector('input[name="taxableYn"][value="N"]').checked;
			const taxFreeNameInput = document
					.getElementById('taxFreeNameInput');
			const taxFreeLimitInput = document
					.getElementById('taxFreeLimitInput');

			if (isNotTaxable) {
				taxFreeNameInput.disabled = false;
				taxFreeLimitInput.disabled = false;
			} else {
				taxFreeNameInput.disabled = true;
				taxFreeNameInput.value = '';
				taxFreeLimitInput.disabled = true;
				taxFreeLimitInput.value = '0';
			}
		}

		function toggleLumpSum() {
			const selectVal = document
					.getElementById('attendanceOrLumpsumSelect').value;
			const lumpSumRow = document.getElementById('lumpSumRow');
			const lumpSumInput = document.getElementById('lumpSumInput');

			if (selectVal === '일괄지급') {
				lumpSumRow.style.display = 'table-row';
			} else {
				lumpSumRow.style.display = 'none';
				lumpSumInput.value = '';
			}
		}

		function enableInputsBeforeSubmit() {
			document.getElementById('taxFreeNameInput').disabled = false;
			document.getElementById('taxFreeLimitInput').disabled = false;
		}

		window.onload = function() {
			toggleTaxFree();
			toggleLumpSum();
		};
	</script>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty sessionScope.errorMessage}">
    <script>
        alert("${sessionScope.errorMessage}");
    </script>
    <c:remove var="errorMessage" scope="session"/>
</c:if>

</body>
</html>