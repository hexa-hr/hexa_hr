<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>給与項目設定</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
    margin: 0;
    min-width: 1400px;
    font-family: 'Malgun Gothic', sans-serif;
    background-color: white;
}

.page-header {
    padding: 30px 40px 10px 40px;
    margin-bottom: 20px;
}

.page-header h1 {
    font-size: 22px;
    margin: 0 0 10px 0;
    color: #333;
}

.page-header p {
    font-size: 14px;
    color: #666;
    margin: 0;
}

.container {
    padding: 0 40px;
    display: flex;
    gap: 30px;
    align-items: flex-start;
    margin-bottom: 40px;
    background-color: transparent;
}

.table-section {
    flex: 1.2;
    background-color: white;
    padding: 20px;
    box-sizing: border-box;
}

.form-section {
    flex: 1;
    background: #f4f4f4;
    padding: 20px;
    border: 1px solid #ddd;
    box-sizing: border-box;
}

.section-title {
    font-size: 18px;
    font-weight: bold;
    margin-top: 0;
    margin-bottom: 10px;
    color: #333;
    border-bottom: 2px solid #4e73df;
    padding-bottom: 5px;
}

.section-title .count {
    font-size: 13px;
    color: #888;
    font-weight: normal;
}

table.data-table, table.form-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 0;
}

table.data-table th, table.data-table td {
    border: 1px solid #ccc;
    padding: 10px;
    font-size: 14px;
    white-space: nowrap;
    text-align: center;
}

table.data-table th {
    background-color: #f8f9fa;
    font-weight: bold;
    color: #333;
}

table.data-table td a {
    display: block;
    padding: 10px;
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
    background-color: #4e73df;
    color: #fff;
    font-size: 10px;
    font-weight: bold;
    line-height: 16px;
    border-radius: 3px;
    text-align: center;
}

.badge-off {
    display: inline-block;
    width: 16px;
    height: 16px;
    background-color: #a5a5a5;
    color: #fff;
    font-size: 10px;
    font-weight: bold;
    line-height: 16px;
    border-radius: 3px;
    text-align: center;
}

table.form-table th {
    text-align: left;
    padding: 10px;
    font-size: 14px;
    font-weight: bold;
    color: #333;
    width: 30%;
    vertical-align: middle;
}

table.form-table td {
    padding: 10px;
    font-size: 14px;
}

.input-text, .select-box {
    width: 100%;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 3px;
    box-sizing: border-box;
    font-size: 14px;
}

.input-text:disabled {
    background-color: #eee;
    color: #999;
    cursor: not-allowed;
}

.radio-label {
    margin-right: 15px;
    cursor: pointer;
    font-size: 14px;
}

.lump-sum-row {
    display: none;
}

.section-divider {
    border: 0;
    border-top: 1px solid #ddd;
    margin: 10px 40px 30px 40px;
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
    border-radius: 3px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    color: #fff;
    text-decoration: none;
    display: inline-block;
}

.btn-blue {
    background-color: #4e73df;
}

.btn-blue:hover {
    background-color: #2e59d9;
}

.btn-gray {
    background-color: #a5a5a5;
}

.btn-gray:hover {
    background-color: #858796;
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="page-header">
		<h1>給与項目設定</h1>
		<p>給与に関連する支給および控除項目を設定するメニューです。会社の状況に合わせて設定できます。</p>
	</div>

	<!-- [上部] 支給項目設定エリア -->
	<div class="container">
		<div class="table-section">
			<div class="section-title">
				支給項目設定 <span class="count">(項目数: ${wageList != null ? wageList.size() : 0}個)</span>
			</div>

			<table class="data-table">
				<thead>
					<tr>
						<th style="width: 18%;">支給項目</th>
						<th style="width: 27%;">課税有無</th>
						<th style="width: 15%;">非課税限度額</th>
						<th style="width: 10%;">端数処理</th>
						<th style="width: 20%;">勤怠連動/일괄지급</th>
						<th style="width: 10%;">사용有無</th>
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
											非課税_${wage.taxFreeName}
										</c:when>
										<c:otherwise>
											全体課税
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

	<!-- 支給項目入力/修正フォーム -->
		<div class="form-section">
			<form id="wageForm" method="post"
				onsubmit="enableInputsBeforeSubmit()">
				<input type="hidden" name="wageTypeId"
					value="${param.selectedWageId}"> <input type="hidden"
					name="itemType" value="P">

				<table class="form-table">
					<tr>
						<th>支給項目</th>
						<td><input type="text" name="wageTypeName"
							value="${param.wageTypeName}" class="input-text"
							placeholder="支給項目を入力してください。"></td>
					</tr>
					<tr>
						<th>課税有無</th>
						<td><label class="radio-label"> <input type="radio"
								name="taxableYn" value="Y"
								${empty param.taxableYn || param.taxableYn == 'Y' ? 'checked' : ''}
								onclick="toggleTaxFree()"> 全体課税
						</label> <label class="radio-label"> <input type="radio"
								name="taxableYn" value="N"
								${param.taxableYn == 'N' ? 'checked' : ''}
								onclick="toggleTaxFree()"> 非課税
						</label></td>
					</tr>
					<tr>
						<th>非課税名</th>
						<td><input type="text" id="taxFreeNameInput"
							name="taxFreeName" value="${param.taxFreeName}"
							class="input-text" placeholder="非課税名称を入力してください。"></td>
					</tr>
					<tr>
						<th>非課税限度額</th>
						<td><input type="number" id="taxFreeLimitInput"
							name="taxFreeLimit"
							value="${empty param.taxFreeLimit ? 0 : param.taxFreeLimit}"
							class="input-text" style="width: 80%; text-align: right;">
							ウォン</td>
					</tr>
					<tr>
						<th>端数処理</th>
						<td><select name="numberCut" class="select-box">
								<option value="없음"
									${empty param.numberCut || param.numberCut == '없음' ? 'selected' : ''}>없음</option>
								<option value="1ウォン単位"
									${param.numberCut == '1ウォン単位' || param.numberCut == 'ウォン' ? 'selected' : ''}>1ウォン単位</option>
								<option value="10ウォン単位"
									${param.numberCut == '10ウォン単位' || param.numberCut == '10ウォン単位' ? 'selected' : ''}>10ウォン単位</option>
								<option value="100ウォン単位"
									${param.numberCut == '100ウォン単位' ? 'selected' : ''}>100ウォン単位</option>
						</select></td>
					</tr>
					<tr>
						<tr>
						<th>勤怠連動/일괄지급</th>
						<td><select name="attendanceOrLumpsum"
							id="attendanceOrLumpsumSelect" class="select-box"
							onchange="toggleLumpSum()">
								<option value="">選択してください。</option>
								<option value="연차"
									${param.attendanceOrLumpsumContent == '연차' || param.attendanceOrLumpsum == '연차' ? 'selected' : ''}>연차</option>
								<option value="반차"
									${param.attendanceOrLumpsumContent == '반차' || param.attendanceOrLumpsum == '반차' ? 'selected' : ''}>반차</option>
								<option value="지각"
									${param.attendanceOrLumpsumContent == '지각' || param.attendanceOrLumpsum == '지각' ? 'selected' : ''}>지각</option>
								<option value="조퇴"
									${param.attendanceOrLumpsumContent == '조퇴' || param.attendanceOrLumpsum == '조퇴' ? 'selected' : ''}>조퇴</option>
								<option value="외근"
									${param.attendanceOrLumpsumContent == '외근' || param.attendanceOrLumpsum == '외근' ? 'selected' : ''}>외근</option>
								<option value="휴일근무"
									${param.attendanceOrLumpsumContent == '휴일근무' || param.attendanceOrLumpsum == '휴일근무' ? 'selected' : ''}>휴일근무</option>
								<option value="연장근무"
									${param.attendanceOrLumpsumContent == '연장근무' || param.attendanceOrLumpsum == '연장근무' ? 'selected' : ''}>연장근무</option>
								<option value="포상휴가"
									${param.attendanceOrLumpsumContent == '포상휴가' || param.attendanceOrLumpsum == '포상휴가' ? 'selected' : ''}>포상휴가</option>
								<option value="야간근무"
									${param.attendanceOrLumpsumContent == '야간근무' || param.attendanceOrLumpsum == '야간근무' ? 'selected' : ''}>야간근무</option>
								<option value="청원휴가"
									${param.attendanceOrLumpsumContent == '청원휴가' || param.attendanceOrLumpsum == '청원휴가' ? 'selected' : ''}>청원휴가</option>
								<option value="일괄지급"
									${param.attendanceOrLumpsum == '일괄지급' ? 'selected' : ''}>일괄지급</option>
						</select></td>
					</tr>

					<tr class="lump-sum-row" id="lumpSumRow">
						<th>일괄지급額</th>
						<td><input type="text" id="lumpSumInput"
							name="attendanceOrLumpsumContent"
							value="${param.attendanceOrLumpsum == '일괄지급' ? param.attendanceOrLumpsumContent : ''}"
							class="input-text" style="width: 80%; text-align: right;"
							placeholder="金額入力"> ウォン</td>
					</tr>

					<tr>
						<th>사용有無</th>
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
						formaction="${pageContext.request.contextPath}/wageTypeSave.do">追加</button>
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeUpdate.do">修正</button>
					<button type="submit" class="btn btn-gray"
						formaction="${pageContext.request.contextPath}/wageTypeDelete.do"
						onclick="return confirmWageDelete('${param.wageTypeName}');">削除</button>
					<a href="${pageContext.request.contextPath}/wageTypeSetting.do"
						class="btn btn-gray">内容クリア</a>
				</div>
			</form>
		</div>
	</div>

	<!-- セクション区切り線 -->
	<hr class="section-divider">

	<!-- [下部] 控除項目設定エリア -->
	<div class="container">
		<div class="table-section">
			<div class="section-title">
				控除項目設定 <span class="count">(項目数: ${deductionList != null ? deductionList.size() : 0}個)</span>
			</div>

			<table class="data-table">
				<thead>
					<tr>
						<th style="width: 30%;">控除項目</th>
						<th style="width: 20%;">端数処理</th>
						<th style="width: 15%;">使用有無</th>
						<th style="width: 35%;">備考</th>
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

		<!-- 控除項目入力/修正フォーム -->
		<div class="form-section">
			<form id="deductionForm" method="post">
				<input type="hidden" name="wageTypeId"
					value="${param.selectedDedId}"> <input type="hidden"
					name="itemType" value="D">

				<table class="form-table">
					<tr>
						<th>控除項目</th>
						<td><input type="text" name="wageTypeName"
							value="${param.dedName}" class="input-text"
							placeholder="控除項目を入力してください。"></td>
					</tr>
					<tr>
						<th>計算方法</th>
						<td><input type="text" name="attendanceOrLumpsumContent"
							value="${param.calcMethod}" class="input-text"
							placeholder="計算方法を入力してください。">
							<div style="font-size: 11px; color: #666; margin-top: 4px;">
								<input type="checkbox"> 例）
							</div></td>
					</tr>
					<tr>
						<th>端数処理</th>
						<td><select name="numberCut" class="select-box">
								<option value="없음"
									${empty param.numberCut || param.numberCut == '없음' ? 'selected' : ''}>없음</option>
								<option value="1ウォン単位"
									${param.numberCut == '1ウォン単位' || param.numberCut == '1ウォン単位' ? 'selected' : ''}>1ウォン単位</option>
								<option value="10ウォン単位"
									${param.numberCut == '10ウォン単位' || param.numberCut == '10ウォン単位' ? 'selected' : ''}>10ウォン単位</option>
								<option value="100ウォン単位"
									${param.numberCut == '100ウォン単位' || param.numberCut == '100ウォン単位' ? 'selected' : ''}>100ウォン単位</option>
						</select></td>
					</tr>
					<tr>
						<th>備考</th>
						<td><input type="text" name="taxFreeName"
							value="${empty param.note ? '기본항목' : param.note}"
							class="input-text" placeholder="備考入力"></td>
					</tr>
					<tr>
						<th>사용有無</th>
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
						formaction="${pageContext.request.contextPath}/wageTypeSave.do">追加</button>
					<button type="submit" class="btn btn-blue"
						formaction="${pageContext.request.contextPath}/wageTypeUpdate.do">修正</button>
					<button type="submit" class="btn btn-gray"
						formaction="${pageContext.request.contextPath}/wageTypeDelete.do"
						onclick="return confirmDeductDelete('${param.dedName}');">削除</button>
					<a href="${pageContext.request.contextPath}/wageTypeSetting.do"
						class="btn btn-gray">内容クリア</a>
				</div>
			</form>
		</div>
	</div>

	<!-- JavaScript コントロール テーブル -->
	<script>
		// 支給項目削除前の警告ウィンドウ機能
		function confirmWageDelete(wageName) {
			if (wageName === '기본급') {
				alert('기본급は必須項目であるため削除できません。');
				return false;
			}
			return confirm('本当に削除しますか？');
		}

		// 控除項目削除前の警告ウィンドウ機能
		function confirmDeductDelete(dedName) {
			const fixedItems = [
				'국민연금', '건강보험', '장기요양보험', '고용보험', 
				'소득세', '지방소득세', '사업소득', '일용급여'
			];
			
			if (fixedItems.includes(dedName)) {
				alert(dedName + ' 項目は必須控除項目であるため削除できません。');
				return false;
			}
			return confirm('本当に削除しますか？');
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