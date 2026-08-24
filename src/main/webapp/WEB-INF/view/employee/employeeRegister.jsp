<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 신규 등록 / 상세</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
body { margin: 0; }
.wrap { display: flex; align-items: flex-start; }
.sidebar { width: 280px; padding: 20px; background-color: #f4f4f4; border-right: 1px solid #ddd; height: 100vh; position: sticky; top: 0; box-sizing: border-box; }
.container { padding: 20px; font-family: sans-serif; flex: 1; box-sizing: border-box; }
.section-title { font-size: 18px; font-weight: bold; margin-top: 30px; margin-bottom: 10px; color: #333; border-bottom: 2px solid #4e73df; padding-bottom: 5px; max-width: 900px; }
table { border-collapse: collapse; width: 100%; max-width: 900px; margin-bottom: 20px; }
th, td { border: 1px solid #ccc; padding: 10px; font-size: 14px; }
th { background-color: #f8f9fa; width: 15%; text-align: left; }
input[type="text"], input[type="password"], input[type="date"], input[type="email"], input[type="number"], select { padding: 5px; width: 80%; }
.menu-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 15px; }
.menu-btn { background-color: #666; color: white; padding: 12px 5px; text-align: center; border-radius: 3px; cursor: pointer; text-decoration: none; font-size: 13px; border: none; font-weight: bold; display: flex; align-items: center; justify-content: center; height: 45px; width: 100%; box-sizing: border-box; font-family: inherit; }
.menu-btn:hover { background-color: #555; }
.add-btn { float: right; padding: 3px 8px; font-size: 12px; background-color: #1cc88a; color: white; border: none; border-radius: 3px; cursor: pointer; }
.del-btn { background-color: #e74a3b; color: white; border: none; border-radius: 3px; cursor: pointer; padding: 4px 8px; font-size: 12px; }
</style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="sidebar">
			<div style="background: white; padding: 15px; border: 1px solid #ccc; text-align: center; margin-bottom: 20px;">
				<img src="<%=request.getContextPath()%>/images/default_profile.png" alt="사진" style="width: 80px; height: 100px; background: #eee;">
				<p style="font-size: 12px; color: #777; margin-top: 10px;">사원사진을 등록해주세요</p>
			</div>

			<h3>사원정보 1</h3>
			<div class="menu-grid">
				<button type="button" class="menu-btn" onclick="location.href='#account'">급여<br>4대 보험</button>
				<button type="button" class="menu-btn" onclick="location.href='#dependents'">부양가족</button>
				<button type="button" class="menu-btn" onclick="location.href='#degree'">학력</button>
				<button type="button" class="menu-btn" onclick="location.href='#career'">경력</button>
				<button type="button" class="menu-btn" onclick="location.href='#military'">병역</button>
			</div>

			<h3>사원정보 2</h3>
			<div class="menu-grid">
				<button type="button" class="menu-btn" onclick="moveToPage2('cert')">자격 면허</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('training')">교육 훈련</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('reward')">상벌</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('appointment')">발령</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('referrer')">추천 신원보증</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('retirement')">퇴직</button>
			</div>
		</div>

		<div class="container">
			<h2>사원 정보 등록 / 상세조회</h2>
			<p style="color: red; font-size: 12px;">* 표시는 필수입력 항목입니다.</p>

			<iframe name="hidden_iframe" style="display: none;"></iframe>

			<form action="<%=request.getContextPath()%>/employee/register.do" method="post" target="hidden_iframe" onsubmit="return validateForm();">
				<input type="hidden" name="companyId" value="1"> 
				<input type="hidden" name="personId" value="1">
<input type="hidden" id="hiddenEmpId" name="employeeId" value="${emp.employeeId}">
				<div class="section-title">기본 정보</div>
				<table>
					<tr>
						<th>* 한글 성명</th>
						<td><input type="text" name="koreanName" value="${emp.koreanName}" required></td>
						<th>영문 성명</th>
						<td><input type="text" name="englishName" value="${emp.englishName}"></td>
					</tr>
					<tr>
						<th>* 고용 형태</th>
						<td><select name="employmentType" required>
								<option value="정규직" ${emp.employmentType == '정규직' ? 'selected' : ''}>정규직</option>
								<option value="계약직" ${emp.employmentType == '계약직' ? 'selected' : ''}>계약직</option>
								<option value="인턴" ${emp.employmentType == '인턴' ? 'selected' : ''}>인턴</option>
						</select></td>
						<th>* 재직 상태</th>
						<td><select name="status" required>
								<option value="재직" ${emp.status == '재직' ? 'selected' : ''}>재직</option>
								<option value="휴직" ${emp.status == '휴직' ? 'selected' : ''}>휴직</option>
								<option value="퇴사" ${emp.status == '퇴사' ? 'selected' : ''}>퇴사</option>
						</select></td>
					</tr>
					<tr>
						<th>* 입사일</th>
						<td><input type="date" name="hireDate" value="<fmt:formatDate value='${emp.hireDate}' pattern='yyyy-MM-dd'/>" required></td>
						<th>퇴사일</th>
						<td><input type="date" name="resignationDate" value="<fmt:formatDate value='${emp.resignationDate}' pattern='yyyy-MM-dd'/>"></td>
					</tr>
					<tr>
						<th>* 부서</th>
						<td>
							<select name="departmentId" required>
								<option value="">선택</option>
								<c:forEach var="dept" items="${deptList}">
									<option value="${dept.id}" ${emp.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
								</c:forEach>
							</select>
						</td>
						<th>* 직위</th>
						<td>
							<select name="positionId" required>
								<option value="">선택</option>
								<c:forEach var="pos" items="${posList}">
									<option value="${pos.id}" ${emp.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>내/외국인</th>
						<td>
							<input type="radio" name="foreignOrDomestic" value="내국인" ${emp == null || emp.foreignOrDomestic == '내국인' ? 'checked' : ''}> 내국인 
							<input type="radio" name="foreignOrDomestic" value="외국인" ${emp != null && emp.foreignOrDomestic == '외국인' ? 'checked' : ''}> 외국인
						</td>
						<th>* 주민등록번호</th>
						<td>
							<input type="text" name="residentNumber1" value="${emp.residentNumber1}" maxlength="6" style="width: 30%;" placeholder="앞 6자리" required> - 
							<input type="password" name="residentNumber2" value="${emp.residentNumber2}" maxlength="7" style="width: 30%;" placeholder="뒤 7자리" required>
						</td>
					</tr>
					<tr>
						<th>주소</th>
						<td colspan="3"><input type="text" name="address" value="${emp.address}" style="width: 95%;"></td>
					</tr>
					<tr>
						<th>자택 전화번호</th>
						<td><input type="text" name="telPhone" value="${emp.telPhone}"></td>
						<th>휴대폰 번호</th>
						<td><input type="text" name="mobile" value="${emp.mobile}"></td>
					</tr>
					<tr>
						<th>* 이메일</th>
						<td><input type="email" name="email" value="${emp.email}" required></td>
						<th>SNS</th>
						<td><input type="text" name="sns" value="${emp.sns}"></td>
					</tr>
					<tr>
						<th>기타 상세</th>
						<td colspan="3"><textarea name="otherDetails" rows="3" style="width: 95%; padding: 5px;">${emp.otherDetails}</textarea></td>
					</tr>
				</table>

				<!-- 급여 계좌 정보 -->
				<div class="section-title" id="account">급여 계좌 정보</div>
				<table>
					<tr>
						<th>* 급여(기본급/일급)</th>
						<td colspan="3">
							<input type="number" name="basicPay" value="${emp.basicPay}" placeholder="예: 3000000" style="width: 30%;" required> 
							<span style="font-size: 13px; color: #666; margin-left: 5px;">원 (숫자만 입력)</span>
						</td>
					</tr>
					<tr>
						<th>은행명</th>
						<td><input type="text" name="bankName" value="${account.bankName}" placeholder="예: 국민은행"></td>
						<th>계좌번호</th>
						<td><input type="text" name="accountNumber" value="${account.accountNumber}" placeholder="- 제외하고 입력"></td>
					</tr>
					<tr>
						<th>예금주</th>
						<td colspan="3"><input type="text" name="depositStocks" value="${account.depositStocks}" style="width: 36%;"></td>
					</tr>
				</table>

				<!-- 보험 정보 -->
				<c:set var="chkNps" value="" /><c:set var="chkHealth" value="" /><c:set var="chkEmp" value="" /><c:set var="chkInd" value="" />
				<c:set var="insNum" value="" /><c:set var="insAmt" value="" /><c:set var="insStart" value="" /><c:set var="insEnd" value="" /><c:set var="insRem" value="" />

				<c:if test="${not empty insList}">
					<c:forEach var="ins" items="${insList}">
						<c:if test="${ins.insuranceAgency == '국민연금'}"><c:set var="chkNps" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '건강보험'}"><c:set var="chkHealth" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '고용보험'}"><c:set var="chkEmp" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '산재보험'}"><c:set var="chkInd" value="checked" /></c:if>
						<c:if test="${empty insNum and not empty ins.insuranceNumber}"><c:set var="insNum" value="${ins.insuranceNumber}" /></c:if>
						<c:if test="${empty insAmt and not empty ins.insuranceAmount}"><c:set var="insAmt" value="${ins.insuranceAmount}" /></c:if>
						<c:if test="${empty insStart and not empty ins.insuranceStartDate}"><c:set var="insStart"><fmt:formatDate value="${ins.insuranceStartDate}" pattern="yyyy-MM-dd"/></c:set></c:if>
						<c:if test="${empty insEnd and not empty ins.insuranceEndDate}"><c:set var="insEnd"><fmt:formatDate value="${ins.insuranceEndDate}" pattern="yyyy-MM-dd"/></c:set></c:if>
						<c:if test="${empty insRem and not empty ins.remarks4}"><c:set var="insRem" value="${ins.remarks4}" /></c:if>
					</c:forEach>
				</c:if>
				<c:if test="${empty emp}">
					<c:set var="chkNps" value="checked" /><c:set var="chkHealth" value="checked" /><c:set var="chkEmp" value="checked" /><c:set var="chkInd" value="checked" />
				</c:if>

				<div class="section-title">보험 정보</div>
				<table>
					<tr>
						<th>* 4대 보험</th>
						<td colspan="3">
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="국민연금" ${chkNps} style="width: auto;"> 국민연금</label>
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="건강보험" ${chkHealth} style="width: auto;"> 건강보험</label>
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="고용보험" ${chkEmp} style="width: auto;"> 고용보험</label>
							<label style="cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="산재보험" ${chkInd} style="width: auto;"> 산재보험</label>
						</td>
					</tr>
					<tr>
						<th>보험 번호</th>
						<td><input type="text" name="insuranceNumber" value="${insNum}" placeholder="- 제외하고 입력"></td>
						<th>보험 가입 금액</th>
						<td><input type="number" name="insuranceAmount" value="${insAmt}" placeholder="숫자만 입력"></td>
					</tr>
					<tr>
						<th>가입일(시작일)</th>
						<td><input type="date" name="insuranceStartDate" value="${insStart}"></td>
						<th>만료일(종료일)</th>
						<td><input type="date" name="insuranceEndDate" value="${insEnd}"></td>
					</tr>
					<tr>
						<th>비고</th>
						<td colspan="3"><input type="text" name="remarks4" value="${insRem}" style="width: 95%;"></td>
					</tr>
				</table>

				<!-- 가족 사항 -->
				<div class="section-title" id="dependents">* 가족 사항<button type="button" class="add-btn" onclick="addDependentRow()">+ 가족 추가</button></div>
				<table id="dependentTable">
					<tr><th style="width: 15%;">* 관계</th><th style="width: 20%;">* 성명</th><th style="width: 15%;">내/외국인</th><th style="width: 20%;">주민번호 앞자리</th><th style="width: 20%;">주민번호 뒷자리</th><th style="width: 10%;">삭제</th></tr>
					<c:if test="${not empty depList}">
						<c:forEach var="dep" items="${depList}">
							<tr>
								<td style="text-align: center;"><input type="text" name="relationship" value="${dep.relationship}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="parentsName" value="${dep.parentsName}" style="width: 90%;"></td>
								<td style="text-align: center;">
									<select name="foreignOrDomestic1" style="width: 90%;">
										<option value="내국인" ${dep.foreignOrDomestic1 == '내국인' ? 'selected' : ''}>내국인</option>
										<option value="외국인" ${dep.foreignOrDomestic1 == '외국인' ? 'selected' : ''}>외국인</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="text" name="parentsNumber1" value="${dep.parentsNumber1}" maxlength="6" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="password" name="parentsNumber2" value="${dep.parentsNumber2}" maxlength="7" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'dependentTable')">X 삭제</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 학력 사항 -->
				<div class="section-title" id="degree">학력 사항<button type="button" class="add-btn" style="background-color: #f6c23e;" onclick="addDegreeRow()">+ 학력 추가</button></div>
				<table id="degreeTable">
					<tr><th style="width: 15%;">졸업구분</th><th style="width: 25%;">학교명</th><th style="width: 20%;">입학일</th><th style="width: 20%;">졸업일</th><th style="width: 15%;">전공</th><th style="width: 10%;">수료상태</th><th style="width: 10%;">삭제</th></tr>
					<c:if test="${not empty degList}">
						<c:forEach var="deg" items="${degList}">
							<tr>
								<td style="text-align: center;">
									<select name="graduate" style="width: 90%;">
										<option value="고졸" ${deg.graduate == '고졸' ? 'selected' : ''}>고졸</option>
										<option value="전문대졸" ${deg.graduate == '전문대졸' ? 'selected' : ''}>전문대졸</option>
										<option value="대졸" ${deg.graduate == '대졸' ? 'selected' : ''}>대졸</option>
										<option value="대학원졸" ${deg.graduate == '대학원졸' ? 'selected' : ''}>대학원졸</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="text" name="schoolName" value="${deg.schoolName}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="admissionDate" value="<fmt:formatDate value='${deg.admissionDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="graduationDate" value="<fmt:formatDate value='${deg.graduationDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="major" value="${deg.major}" style="width: 90%;"></td>
								<td style="text-align: center;">
									<select name="completion" style="width: 90%;">
										<option value="졸업" ${deg.completion == '졸업' ? 'selected' : ''}>졸업</option>
										<option value="수료" ${deg.completion == '수료' ? 'selected' : ''}>수료</option>
										<option value="중퇴" ${deg.completion == '중퇴' ? 'selected' : ''}>중퇴</option>
									</select>
								</td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'degreeTable')">X 삭제</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 🌟 경력 사항 (페이지 1로 이동) -->
				<div class="section-title" id="career">경력 사항<button type="button" class="add-btn" style="background-color: #4e73df;" onclick="addCareerRow()">+ 경력 추가</button></div>
				<table id="careerTable">
					<tr><th style="width: 20%;">회사명</th><th style="width: 15%;">입사일자</th><th style="width: 15%;">퇴사일자</th><th style="width: 15%;">직급</th><th style="width: 25%;">담당업무</th><th style="width: 10%;">삭제</th></tr>
					<c:if test="${not empty careerList}">
						<c:forEach var="c" items="${careerList}">
							<tr>
								<td style="text-align: center;"><input type="text" name="companyName" value="${c.companyName}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="startDate" value="<fmt:formatDate value='${c.startDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="endDate" value="<fmt:formatDate value='${c.endDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="finalPosition" value="${c.finalPosition}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="responsibilities" value="${c.responsibilities}" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'careerTable')">X 삭제</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 병역 사항 -->
				<div class="section-title" id="military">병역 사항<button type="button" class="add-btn" style="background-color: #36b9cc;" onclick="addMilitaryRow()">+ 병역 추가</button></div>
				<table id="militaryTable">
					<tr><th style="width: 10%;">병역구분</th><th style="width: 10%;">군별</th><th style="width: 15%;">복무시작일</th><th style="width: 15%;">복무종료일</th><th style="width: 15%;">최종계급</th><th style="width: 15%;">병과</th><th style="width: 12%;">면제사유</th><th style="width: 8%;">삭제</th></tr>
					<c:if test="${not empty milList}">
						<c:forEach var="mil" items="${milList}">
							<tr>
								<td style="text-align: center;">
									<select name="serviceType" style="width:90%;">
										<option value="">선택</option>
										<option value="필" ${mil.serviceType == '필' ? 'selected' : ''}>필</option>
										<option value="미필" ${mil.serviceType == '미필' ? 'selected' : ''}>미필</option>
										<option value="면제" ${mil.serviceType == '면제' ? 'selected' : ''}>면제</option>
									</select>
								</td>
								<td style="text-align: center;">
									<select name="branch" style="width:90%;">
										<option value="">선택</option>
										<option value="육군" ${mil.branch == '육군' ? 'selected' : ''}>육군</option>
										<option value="해군" ${mil.branch == '해군' ? 'selected' : ''}>해군</option>
										<option value="공군" ${mil.branch == '공군' ? 'selected' : ''}>공군</option>
										<option value="해병대" ${mil.branch == '해병대' ? 'selected' : ''}>해병대</option>
										<option value="기타" ${mil.branch == '기타' ? 'selected' : ''}>기타</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="date" name="servicePeriod1" value="<fmt:formatDate value='${mil.servicePeriod1}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="servicePeriod2" value="<fmt:formatDate value='${mil.servicePeriod2}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="finalRank" value="${mil.finalRank}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="department1" value="${mil.department1}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="exemptionReason" value="${mil.exemptionReason}" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'militaryTable')">X 삭제</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div style="text-align: center; max-width: 900px; margin-top: 20px;">
					<button type="submit" style="padding: 10px 30px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;">저장</button>
					<button type="reset" style="padding: 10px 30px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px;">취소</button>
				</div>
			</form>
		</div>
	</div>

	<script>
		window.onload = function() { 
			<c:if test="${empty depList}"> addDependentRow(); </c:if>
			<c:if test="${empty degList}"> addDegreeRow(); </c:if>
			<c:if test="${empty careerList}"> addCareerRow(); </c:if>
			<c:if test="${empty milList}"> addMilitaryRow(); </c:if>
		};

		function addDependentRow() {
			var table = document.getElementById("dependentTable");
			var row = table.insertRow(-1);
			var cell1 = row.insertCell(0); var cell2 = row.insertCell(1); var cell3 = row.insertCell(2); var cell4 = row.insertCell(3); var cell5 = row.insertCell(4); var cell6 = row.insertCell(5);
			cell1.innerHTML = '<input type="text" name="relationship" style="width: 90%;" placeholder="부, 모, 배우자 등">';
			cell2.innerHTML = '<input type="text" name="parentsName" style="width: 90%;">';
			cell3.innerHTML = '<select name="foreignOrDomestic1" style="width: 90%;"><option value="내국인">내국인</option><option value="외국인">외국인</option></select>';
			cell4.innerHTML = '<input type="text" name="parentsNumber1" maxlength="6" style="width: 90%;">';
			cell5.innerHTML = '<input type="password" name="parentsNumber2" maxlength="7" style="width: 90%;">';
			cell6.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'dependentTable\')">X 삭제</button>';
			for(var i=0; i<6; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function addDegreeRow() {
			var table = document.getElementById("degreeTable");
			var row = table.insertRow(-1);
			var cell1 = row.insertCell(0); var cell2 = row.insertCell(1); var cell3 = row.insertCell(2); var cell4 = row.insertCell(3); var cell5 = row.insertCell(4); var cell6 = row.insertCell(5); var cell7 = row.insertCell(6);
			cell1.innerHTML = '<select name="graduate" style="width: 90%;"><option value="고졸">고졸</option><option value="전문대졸">전문대졸</option><option value="대졸">대졸</option><option value="대학원졸">대학원졸</option></select>';
			cell2.innerHTML = '<input type="text" name="schoolName" style="width: 90%;" placeholder="학교명 입력">';
			cell3.innerHTML = '<input type="date" name="admissionDate" style="width: 90%;">';
			cell4.innerHTML = '<input type="date" name="graduationDate" style="width: 90%;">';
			cell5.innerHTML = '<input type="text" name="major" style="width: 90%;">';
			cell6.innerHTML = '<select name="completion" style="width: 90%;"><option value="졸업">졸업</option><option value="수료">수료</option><option value="중퇴">중퇴</option></select>';
			cell7.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'degreeTable\')">X 삭제</button>';
			for(var i=0; i<7; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function addCareerRow() {
			var table = document.getElementById("careerTable");
			var row = table.insertRow(-1);
			var html = '<td><input type="text" name="companyName" style="width: 90%;"></td>'
					+ '<td><input type="date" name="startDate" style="width: 90%;"></td>'
					+ '<td><input type="date" name="endDate" style="width: 90%;"></td>'
					+ '<td><input type="text" name="finalPosition" style="width: 90%;"></td>'
					+ '<td><input type="text" name="responsibilities" style="width: 90%;"></td>'
					+ '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'careerTable\')">X 삭제</button></td>';
			row.innerHTML = html;
			for(var i=0; i<row.cells.length; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function addMilitaryRow() {
			var table = document.getElementById("militaryTable");
			var row = table.insertRow(-1);
			var html = '<td><select name="serviceType" style="width:90%;"><option value="">선택</option><option value="필">필</option><option value="미필">미필</option><option value="면제">면제</option></select></td>'
					+ '<td><select name="branch" style="width:90%;"><option value="">선택</option><option value="육군">육군</option><option value="해군">해군</option><option value="공군">공군</option><option value="해병대">해병대</option><option value="기타">기타</option></select></td>'
					+ '<td><input type="date" name="servicePeriod1" style="width: 90%;"></td>'
					+ '<td><input type="date" name="servicePeriod2" style="width: 90%;"></td>'
					+ '<td><input type="text" name="finalRank" style="width: 90%;"></td>'
					+ '<td><input type="text" name="department1" style="width: 90%;"></td>'
					+ '<td><input type="text" name="exemptionReason" style="width: 90%;"></td>'
					+ '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'militaryTable\')">X 삭제</button></td>';
			row.innerHTML = html;
			for(var i=0; i<row.cells.length; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function deleteRow(button, tableId) {
			var row = button.parentNode.parentNode;
			if (document.getElementById(tableId).rows.length > 2) { 
				row.parentNode.removeChild(row); 
			} else { 
				alert("최소 1줄은 입력란이 필요합니다."); 
			}
		}

		let isSubmitting = false;

		function validateForm() {
		    if (isSubmitting) {
		        alert("현재 저장 중입니다. 잠시만 기다려주세요.");
		        return false;
		    }

		    var relationships = document.getElementsByName("relationship");
		    var parentsNames = document.getElementsByName("parentsName");
		    var hasDependent = false;
		    for (var i = 0; i < relationships.length; i++) {
		        if (relationships[i].value.trim() !== "" && parentsNames[i].value.trim() !== "") {
		            hasDependent = true; break;
		        }
		    }
		    if (!hasDependent) { alert("가족 사항을 최소 1명 이상 입력해 주세요. (관계 및 성명 필수)"); return false; }

		    isSubmitting = true;
		    setTimeout(function() { isSubmitting = false; }, 3000); 
		    return true;
		}

		function moveToPage2(tab) {
			const empId = document.getElementById("hiddenEmpId").value;
			if (empId) { location.href = "register2.do?employeeId=" + empId + "&tab=" + tab; } 
			else { alert("필수 입력란을 모두 입력하고 맨 아래의 [저장] 버튼을 눌러 DB에 등록한 후에만 부가정보 메뉴로 이동할 수 있습니다."); }
		}
	</script>
</body>
</html>