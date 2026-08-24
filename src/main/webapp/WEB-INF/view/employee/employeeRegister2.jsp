<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 부가정보 등록 (사원정보 2)</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
body { margin: 0; background-color: #f8f9fa; font-family: sans-serif; }
.wrap { display: flex; align-items: flex-start; max-width: 1400px; margin: 0 auto; background-color: white; border: 1px solid #ddd; }
.sidebar { width: 260px; padding: 20px; background-color: #f4f4f4; border-right: 1px solid #ddd; height: 100vh; position: sticky; top: 0; box-sizing: border-box; overflow-y: auto; }
.container { flex: 1; padding: 40px; box-sizing: border-box; }
.menu-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 15px; }
.menu-btn { background-color: #666; color: white; padding: 12px 5px; text-align: center; border-radius: 3px; cursor: pointer; text-decoration: none; font-size: 13px; border: none; font-weight: bold; display: flex; align-items: center; justify-content: center; height: 45px; word-break: keep-all; }
.menu-btn:hover { background-color: #555; }
.section-title { font-size: 18px; font-weight: bold; margin-top: 40px; margin-bottom: 10px; color: #333; border-bottom: 2px solid #4e73df; padding-bottom: 5px; }
.section-title:first-child { margin-top: 0; }
table { width: 100%; border-collapse: collapse; margin-bottom: 30px; text-align: center; }
th, td { border: 1px solid #ccc; padding: 10px 5px; font-size: 13px; }
th { background-color: #f8f9fa; color: #333; }
input[type="text"], input[type="date"], input[type="password"], input[type="number"], select { padding: 4px; width: 90%; border: 1px solid #ccc; box-sizing: border-box; }
.add-btn { float: right; padding: 4px 12px; background-color: #1cc88a; color: white; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; font-weight: bold; }
.add-btn:hover { background-color: #17a673; }
.del-btn { background-color: #e74a3b; color: white; border: none; border-radius: 3px; cursor: pointer; padding: 4px 8px; font-size: 12px; }
.table-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 5px; margin-top: 15px; }
.table-header .title { color: #0056b3; font-weight: bold; font-size: 14px; }
.btn-outline-add { padding: 4px 10px; font-size: 12px; background: white; border: 1px solid #ccc; cursor: pointer; color: #d9534f; font-weight: bold; border-radius: 3px; }
.btn-outline-del { padding: 4px 10px; font-size: 12px; background: white; border: 1px solid #ccc; cursor: pointer; color: #555; border-radius: 3px; margin-left: 5px;}
</style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="sidebar">
			<div style="background: white; padding: 15px; border: 1px solid #ccc; text-align: center; margin-bottom: 20px;">
				<img src="<%=request.getContextPath()%>/images/default_profile.png" alt="사진" style="width: 80px; height: 100px; background: #eee;">
				<p style="margin: 10px 0 0 0; font-weight: bold; font-size: 14px;">사원번호: ${employeeId}</p>
			</div>

			<h3>사원정보 1</h3>
			<div class="menu-grid">
				<a href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#account" class="menu-btn">급여<br>4대 보험</a> 
                <a href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#dependents" class="menu-btn">부양가족</a> 
                <a href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#degree" class="menu-btn">학력</a> 
                <a href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#career" class="menu-btn">경력</a> 
                <a href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#military" class="menu-btn">병역</a>
			</div>

			<h3>사원정보 2</h3>
			<div class="menu-grid">
				<a href="#cert" class="menu-btn">자격 면허</a> <a href="#training" class="menu-btn">교육 훈련</a> <a href="#reward" class="menu-btn">상벌</a>
				<a href="#appointment" class="menu-btn">발령</a> <a href="#referrer" class="menu-btn">추천 신원보증</a> <a href="#retirement" class="menu-btn">퇴직</a>
			</div>
		</div>

		<div class="container">
			<iframe name="hidden_iframe" style="display: none;"></iframe>
			<form action="<%=request.getContextPath()%>/employee/register2_process.do" method="post" target="hidden_iframe">
				<input type="hidden" name="employeeId" value="${employeeId}">

				<!-- 1. 자격·면허 & 어학능력 -->
				<div class="section-title" id="cert">자격·면허 & 어학능력</div>
				
				<div class="table-header">
					<div class="title">+ 자격 & 면허</div>
					<div>
						<button type="button" class="btn-outline-add" onclick="addRow('certTable')">+ 추가</button>
						<button type="button" class="btn-outline-del" onclick="deleteSelectedRows('certTable')">선택삭제</button>
					</div>
				</div>
				<table id="certTable" style="border-top: 2px solid #007bff; margin-bottom: 20px;">
					<tr>
						<th style="width: 5%;"><input type="checkbox" onclick="toggleAll(this, 'certTable')"></th>
						<th style="width: 25%;">자격/면허명</th>
						<th style="width: 15%;">취득일</th>
						<th style="width: 20%;">발행기관</th>
						<th style="width: 20%;">증번호</th>
						<th style="width: 15%;">비고</th>
					</tr>
					<c:if test="${not empty certList}">
						<c:forEach var="cert" items="${certList}">
							<tr>
								<td><input type="checkbox" class="row-check"></td>
								<!-- 🌟 모델의 실제 변수명으로 수정 완료 (certificationName 등) -->
								<td><input type="text" name="certName" value="${cert.certificationName}"></td>
								<td><input type="date" name="certAcqDate" value="<fmt:formatDate value='${cert.acquisitionDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="certIssuer" value="${cert.issuingOrganization}"></td>
								<td><input type="text" name="certNumber" value="${cert.certificationNumber}"></td>
								<td><input type="text" name="certRemarks" value="${cert.remarks1}"></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="table-header">
					<div class="title">+ 어학능력</div>
					<div>
						<button type="button" class="btn-outline-add" onclick="addRow('langTable')">+ 추가</button>
						<button type="button" class="btn-outline-del" onclick="deleteSelectedRows('langTable')">선택삭제</button>
					</div>
				</div>
				<table id="langTable" style="border-top: 2px solid #007bff;">
					<tr>
						<th style="width: 5%;"><input type="checkbox" onclick="toggleAll(this, 'langTable')"></th>
						<th style="width: 15%;">외국어명</th>
						<th style="width: 15%;">시험</th>
						<th style="width: 15%;">공인점수</th>
						<th style="width: 15%;">취득일</th>
						<th style="width: 10%;">독해</th>
						<th style="width: 10%;">작문</th>
						<th style="width: 15%;">회화</th>
					</tr>
					<c:if test="${not empty langList}">
						<c:forEach var="lang" items="${langList}">
							<tr>
								<td><input type="checkbox" class="row-check"></td>
								<!-- 🌟 모델의 실제 변수명으로 수정 완료 (language 등) -->
								<td><input type="text" name="langName" value="${lang.language}"></td>
								<td><input type="text" name="langTest" value="${lang.testName}"></td>
								<td><input type="text" name="langScore" value="${lang.officialScore}"></td>
								<td><input type="date" name="langAcqDate" value="<fmt:formatDate value='${lang.acquisitionDate1}' pattern='yyyy-MM-dd'/>"></td>
								<td><select name="langReading"><option value="상" ${lang.readingAbility == '상' ? 'selected' : ''}>상</option><option value="중" ${lang.readingAbility == '중' ? 'selected' : ''}>중</option><option value="하" ${lang.readingAbility == '하' ? 'selected' : ''}>하</option></select></td>
								<td><select name="langWriting"><option value="상" ${lang.writingAbility == '상' ? 'selected' : ''}>상</option><option value="중" ${lang.writingAbility == '중' ? 'selected' : ''}>중</option><option value="하" ${lang.writingAbility == '하' ? 'selected' : ''}>하</option></select></td>
								<td><select name="langSpeaking"><option value="상" ${lang.speakingAbility == '상' ? 'selected' : ''}>상</option><option value="중" ${lang.speakingAbility == '중' ? 'selected' : ''}>중</option><option value="하" ${lang.speakingAbility == '하' ? 'selected' : ''}>하</option></select></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 2. 교육 훈련 -->
				<div class="section-title" id="training">교육 훈련<button type="button" class="add-btn" onclick="addRow('trainingTable')">+ 추가</button></div>
				<table id="trainingTable">
					<tr><th>교육구분</th><th>교육명</th><th>시작일</th><th>종료일</th><th>교육기관</th><th>교육비</th><th>환급금</th><th>삭제</th></tr>
					<c:if test="${not empty trainingList}">
						<c:forEach var="t" items="${trainingList}">
							<tr>
								<td><input type="text" name="trainingType" value="${t.trainingType}"></td>
								<td><input type="text" name="trainingName" value="${t.trainingName}"></td>
								<td><input type="date" name="trainingStartDate" value="<fmt:formatDate value='${t.trainingStartDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="date" name="trainingEndDate" value="<fmt:formatDate value='${t.trainingEndDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="trainingOrganization" value="${t.trainingOrganization}"></td>
								<td><input type="number" name="trainingCost" value="${t.trainingCost}"></td>
								<td><input type="number" name="refundableTrainingCost" value="${t.refundableTrainingCost}"></td>
								<td><button type="button" class="del-btn" onclick="deleteRow(this, 'trainingTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 3. 상벌 -->
				<div class="section-title" id="reward">상벌<button type="button" class="add-btn" onclick="addRow('rewardTable')">+ 추가</button></div>
				<table id="rewardTable">
					<tr><th>구분</th><th>상벌명</th><th>상벌권자</th><th>일자</th><th>내용</th><th>비고</th><th>삭제</th></tr>
					<c:if test="${not empty rewardList}">
						<c:forEach var="r" items="${rewardList}">
							<tr>
								<td><input type="text" name="rewardPenaltyType" value="${r.rewardPenaltyType}"></td>
								<td><input type="text" name="rewardPenaltyName" value="${r.rewardPenaltyName}"></td>
								<td><input type="text" name="rewardPenaltyGiver" value="${r.rewardPenaltyGiver}"></td>
								<td><input type="date" name="rewardPenaltyDate" value="<fmt:formatDate value='${r.rewardPenaltyDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="rewardPenaltyDescription" value="${r.rewardPenaltyDescription}"></td>
								<td><input type="text" name="remarks2" value="${r.remarks2}"></td>
								<td><button type="button" class="del-btn" onclick="deleteRow(this, 'rewardTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 4. 발령 -->
				<div class="section-title" id="appointment">발령<button type="button" class="add-btn" onclick="addRow('appointmentTable')">+ 추가</button></div>
				<table id="appointmentTable">
					<tr><th>발령구분</th><th>일자</th><th>부서</th><th>직위</th><th>직책</th><th>비고</th><th>삭제</th></tr>
					<c:if test="${not empty apptList}">
						<c:forEach var="a" items="${apptList}">
							<tr>
								<td><input type="text" name="appointmentType" value="${a.appointmentType}"></td>
								<td><input type="date" name="appointmentDate" value="<fmt:formatDate value='${a.appointmentDate}' pattern='yyyy-MM-dd'/>"></td>
								<td>
									<select name="departmentId">
										<option value="">선택</option>
										<c:forEach var="dept" items="${deptList}">
											<option value="${dept.id}" ${a.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
										</c:forEach>
									</select>
								</td>
								<td>
									<select name="positionId">
										<option value="">선택</option>
										<c:forEach var="pos" items="${posList}">
											<option value="${pos.id}" ${a.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
										</c:forEach>
									</select>
								</td>
								<td><input type="text" name="positionType" value="${a.positionType}"></td>
								<td><input type="text" name="remarks3" value="${a.remarks3}"></td>
								<td><button type="button" class="del-btn" onclick="deleteRow(this, 'appointmentTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 5. 추천 & 신원보증 -->
				<div class="section-title" id="referrer">추천 & 신원보증 (추천인)<button type="button" class="add-btn" onclick="addRow('referrerTable')">+ 추가</button></div>
				<table id="referrerTable">
					<tr><th>성명</th><th>관계</th><th>회사명</th><th>직위</th><th>전화번호</th><th>삭제</th></tr>
					<c:if test="${not empty refList}">
						<c:forEach var="ref" items="${refList}">
							<tr>
								<td><input type="text" name="referrerName" value="${ref.referrerName}"></td>
								<td><input type="text" name="referrerRelationship" value="${ref.referrerRelationship}"></td>
								<td><input type="text" name="referrerCompanyName" value="${ref.referrerCompanyName}"></td>
								<td><input type="text" name="referrerPosition" value="${ref.referrerPosition}"></td>
								<td><input type="text" name="referrerPhoneNumber" value="${ref.referrerPhoneNumber}"></td>
								<td><button type="button" class="del-btn" onclick="deleteRow(this, 'referrerTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="section-title" style="margin-top: 10px;">신원보증 (보증인)<button type="button" class="add-btn" onclick="addRow('guarantorTable')">+ 추가</button></div>
				<table id="guarantorTable">
					<tr><th>성명</th><th>관계</th><th>주민등록번호</th><th>보증금액</th><th>연락처</th><th>삭제</th></tr>
					<c:if test="${not empty guaList}">
						<c:forEach var="gua" items="${guaList}">
							<tr>
								<!-- 🌟 모델의 실제 변수명으로 수정 완료 (guarantorName 등) -->
								<td><input type="text" name="guaName" value="${gua.guarantorName}"></td>
								<td><input type="text" name="guaRelation" value="${gua.guarantorRelationship}"></td>
								<td><input type="text" name="guaRrn" value="${gua.guarantorResidentNumber}" placeholder="[주민등록번호 입력]"></td>
								<td><input type="number" name="guaAmount" value="${gua.guaranteeAmount}"></td>
								<td><input type="text" name="guaPeriod" value="${gua.guarantorPhoneNumber}" placeholder="연락처"></td>
								<td><button type="button" class="del-btn" onclick="deleteRow(this, 'guarantorTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 6. 퇴직 -->
				<div class="section-title" id="retirement">퇴직 정보</div>
				<table>
					<tr>
						<th>퇴직구분</th>
						<td><select name="retirementType" style="width: 80%;">
								<option value="">선택</option>
								<option value="정년퇴직" ${retirement.retirementType == '정년퇴직' ? 'selected' : ''}>정년퇴직</option>
								<option value="자진퇴사" ${retirement.retirementType == '자진퇴사' ? 'selected' : ''}>자진퇴사</option>
								<option value="권고사직" ${retirement.retirementType == '권고사직' ? 'selected' : ''}>권고사직</option>
						</select></td>
						<th>퇴직일자</th>
						<td><input type="date" name="retirementDate" value="<fmt:formatDate value='${retirement.retirementDate}' pattern='yyyy-MM-dd'/>"></td>
					</tr>
					<tr>
						<th>퇴직사유</th>
						<td colspan="3"><input type="text" name="retirementReason" value="${retirement.retirementReason}" style="width: 95%;"></td>
					</tr>
					<tr>
						<th>퇴직 후 연락처</th>
						<td><input type="text" name="retirementContact" value="${retirement.retirementContact}" style="width: 80%;"></td>
						<th>퇴직금(원)</th>
						<td><input type="number" name="severancePay" value="${retirement.severancePay}" style="width: 80%; padding: 4px;"></td>
					</tr>
				</table>

				<div style="text-align: center; margin-top: 50px; margin-bottom: 50px;">
					<button type="submit" style="padding: 12px 40px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold;">부가정보 저장</button>
					<button type="reset" style="padding: 12px 40px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px; font-weight: bold;">초기화</button>
				</div>
			</form>
		</div>
	</div>

	<script>
		// JS 오류가 발생하지 않도록 문자열 병합 방식으로 수정 완료!
		var deptOptions = '<option value="">선택</option>';
		<c:forEach var="dept" items="${deptList}">
			deptOptions += '<option value="${dept.id}">${dept.name}</option>';
		</c:forEach>

		var posOptions = '<option value="">선택</option>';
		<c:forEach var="pos" items="${posList}">
			posOptions += '<option value="${pos.id}">${pos.name}</option>';
		</c:forEach>

		window.onload = function() {
			<c:if test="${empty certList}"> addRow('certTable'); </c:if>
			<c:if test="${empty langList}"> addRow('langTable'); </c:if> 
			<c:if test="${empty trainingList}"> addRow('trainingTable'); </c:if>
			<c:if test="${empty rewardList}"> addRow('rewardTable'); </c:if>
			<c:if test="${empty apptList}"> addRow('appointmentTable'); </c:if>
			<c:if test="${empty refList}"> addRow('referrerTable'); </c:if>
			<c:if test="${empty guaList}"> addRow('guarantorTable'); </c:if>
		};

		function addRow(tableId) {
			var table = document.getElementById(tableId);
			var row = table.insertRow(-1);
			var html = "";
			
			if (tableId === 'certTable') {
				html = '<td><input type="checkbox" class="row-check"></td><td><input type="text" name="certName"></td><td><input type="date" name="certAcqDate"></td><td><input type="text" name="certIssuer"></td><td><input type="text" name="certNumber"></td><td><input type="text" name="certRemarks"></td>';
			} else if (tableId === 'langTable') {
				html = '<td><input type="checkbox" class="row-check"></td><td><input type="text" name="langName"></td><td><input type="text" name="langTest"></td><td><input type="text" name="langScore"></td><td><input type="date" name="langAcqDate"></td><td><select name="langReading"><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td><td><select name="langWriting"><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td><td><select name="langSpeaking"><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td>';
			} else if (tableId === 'trainingTable') {
				html = '<td><input type="text" name="trainingType"></td><td><input type="text" name="trainingName"></td><td><input type="date" name="trainingStartDate"></td><td><input type="date" name="trainingEndDate"></td><td><input type="text" name="trainingOrganization"></td><td><input type="number" name="trainingCost"></td><td><input type="number" name="refundableTrainingCost"></td>';
			} else if (tableId === 'rewardTable') {
				html = '<td><input type="text" name="rewardPenaltyType"></td><td><input type="text" name="rewardPenaltyName"></td><td><input type="text" name="rewardPenaltyGiver"></td><td><input type="date" name="rewardPenaltyDate"></td><td><input type="text" name="rewardPenaltyDescription"></td><td><input type="text" name="remarks2"></td>';
			} else if (tableId === 'appointmentTable') {
				html = '<td><input type="text" name="appointmentType"></td><td><input type="date" name="appointmentDate"></td><td><select name="departmentId">' + deptOptions + '</select></td><td><select name="positionId">' + posOptions + '</select></td><td><input type="text" name="positionType"></td><td><input type="text" name="remarks3"></td>';
			} else if (tableId === 'referrerTable') {
				html = '<td><input type="text" name="referrerName"></td><td><input type="text" name="referrerRelationship"></td><td><input type="text" name="referrerCompanyName"></td><td><input type="text" name="referrerPosition"></td><td><input type="text" name="referrerPhoneNumber"></td>';
			} else if (tableId === 'guarantorTable') {
				html = '<td><input type="text" name="guaName"></td><td><input type="text" name="guaRelation"></td><td><input type="text" name="guaRrn" placeholder="[주민등록번호 입력]"></td><td><input type="number" name="guaAmount"></td><td><input type="text" name="guaPeriod" placeholder="연락처"></td>';
			}

			if (tableId !== 'certTable' && tableId !== 'langTable') {
			    html += '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'' + tableId + '\')">X</button></td>';
			}
			row.innerHTML = html;
		}

		function deleteRow(btn, tableId) {
			var table = document.getElementById(tableId);
			if (table.rows.length > 2) { 
				btn.parentNode.parentNode.remove();
			} else {
				alert("최소 1줄은 입력란이 필요합니다.");
			}
		}

		function toggleAll(source, tableId) {
			var checkboxes = document.querySelectorAll('#' + tableId + ' .row-check');
			for(var i = 0; i < checkboxes.length; i++) {
				checkboxes[i].checked = source.checked;
			}
		}

		function deleteSelectedRows(tableId) {
			var table = document.getElementById(tableId);
			var checkboxes = table.querySelectorAll('.row-check:checked');
			if (checkboxes.length === 0) { alert("삭제할 항목을 선택해주세요."); return; }
			if (table.rows.length - 1 === checkboxes.length) { alert("최소 1줄의 입력란은 남겨두어야 합니다."); return; }
			for (var i = checkboxes.length - 1; i >= 0; i--) {
				var row = checkboxes[i].closest('tr');
				row.parentNode.removeChild(row);
			}
			table.querySelector('th input[type="checkbox"]').checked = false;
		}
	</script>
</body>
</html>