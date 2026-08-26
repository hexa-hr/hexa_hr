<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員新規登録 / 詳細</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

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

/* 2. 사이드바 영역 */
.sidebar {
	width: 260px;
	padding: 30px 20px;
	background-color: #f4f4f4;
	border-right: 1px solid #ddd;
	height: 100vh;
	position: sticky;
	top: 0;
	box-sizing: border-box;
	box-shadow: 2px 0 5px rgba(0,0,0,0.02);
}

.profile-box {
	background: white;
	padding: 20px;
	border: 1px solid #ccc;
	border-radius: 3px;
	text-align: center;
	margin-bottom: 25px;
}

.profile-box img {
	width: 90px;
	height: 110px;
	background: #eee;
	object-fit: cover;
	border-radius: 3px;
	border: 1px solid #ddd;
}

.sidebar h3 {
	font-size: 15px;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 8px;
	margin-top: 20px;
	margin-bottom: 15px;
}

.menu-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 8px;
	margin-bottom: 20px;
}

.menu-btn {
	background-color: white;
	color: #333;
	border: 1px solid #ccc;
	padding: 8px 5px;
	text-align: center;
	border-radius: 3px;
	cursor: pointer;
	font-size: 13px;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	height: 40px;
	box-sizing: border-box;
	transition: all 0.2s;
}
.menu-btn:hover {
	background-color: #f1f5f9;
	border-color: #4e73df;
	color: #4e73df;
}

/* 3. 메인 콘텐츠 영역 */
.container {
	padding: 30px 40px;
	background-color: white;
	flex: 1;
	min-height: 100vh;
	box-sizing: border-box;
}

.page-header {
	margin-bottom: 15px;
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

.req {
	color: #e74a3b;
	margin-right: 4px;
	font-weight: bold;
}

/* 4. 섹션 타이틀 및 테이블 */
.section-title {
	font-size: 18px;
	font-weight: bold;
	color: #333;
	margin: 40px 0 15px 0;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 8px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 25px;
	table-layout: fixed;
}

th, td {
	border: 1px solid #ccc;
	padding: 10px 12px;
	font-size: 14px;
	vertical-align: middle;
}

th {
	background-color: #f8f9fa;
	color: #333;
	font-weight: bold;
	text-align: left;
}

/* 5. 입력 폼 스타일 */
input[type="text"], input[type="password"], input[type="date"], input[type="email"], input[type="number"], select, textarea {
	width: 100%;
	padding: 8px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
	box-sizing: border-box;
	font-family: inherit;
}
input:focus, select:focus, textarea:focus { border-color: #4e73df; }

input[type="radio"], input[type="checkbox"] {
	width: auto;
	margin-right: 5px;
	cursor: pointer;
}
label {
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	margin-right: 15px;
}

textarea {
	resize: vertical;
}

.flex-row-gap {
	display: flex;
	align-items: center;
	gap: 10px;
}

/* 6. 버튼 스타일 */
.add-btn {
	background-color: #4e73df;
	color: white;
	border: none;
	border-radius: 3px;
	padding: 6px 14px;
	font-size: 13px;
	font-weight: bold;
	cursor: pointer;
}
.add-btn:hover { background-color: #2e59d9; }

.del-btn {
	background-color: #a5a5a5;
	color: white;
	border: none;
	border-radius: 3px;
	padding: 6px 12px;
	font-size: 13px;
	font-weight: bold;
	cursor: pointer;
}
.del-btn:hover { background-color: #858796; }

.bottom-btns {
	text-align: center;
	margin-top: 40px;
	margin-bottom: 20px;
}

.btn-save {
	background-color: #4e73df;
	color: white;
	border: none;
	border-radius: 3px;
	padding: 10px 35px;
	font-size: 16px;
	font-weight: bold;
	cursor: pointer;
}
.btn-save:hover { background-color: #2e59d9; }

.btn-cancel {
	background-color: #a5a5a5;
	color: white;
	border: none;
	border-radius: 3px;
	padding: 10px 35px;
	font-size: 16px;
	font-weight: bold;
	cursor: pointer;
	margin-left: 10px;
}
.btn-cancel:hover { background-color: #858796; }
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<!-- 사이드바 -->
		<div class="sidebar">
			<div class="profile-box">
				<!-- <img src="<%=request.getContextPath()%>/images/default_profile.png" alt="写真"> -->
                <div style="display: inline-block; width: 80px; height: 100px; background: #eee;"></div>
				<c:choose>
					<c:when test="${not empty emp.employeeId}">
						<p style="margin: 10px 0 0 0; font-weight: bold; font-size: 15px; color: #4e73df;">社員番号: ${emp.employeeId}</p>
					</c:when>
					<c:otherwise>
						<p style="margin: 10px 0 0 0; font-weight: bold; font-size: 14px; color: #e74a3b;">[新規社員登録]</p>
					</c:otherwise>
				</c:choose>
			</div>

			<h3>社員情報 1</h3>
			<div class="menu-grid">
				<button type="button" class="menu-btn" onclick="location.href='#account'">給与<br>4大保険</button>
				<button type="button" class="menu-btn" onclick="location.href='#dependents'">扶養家族</button>
				<button type="button" class="menu-btn" onclick="location.href='#degree'">学歴</button>
				<button type="button" class="menu-btn" onclick="location.href='#career'">経歴</button>
				<button type="button" class="menu-btn" onclick="location.href='#military'">兵役</button>
			</div>

			<h3>社員情報 2</h3>
			<div class="menu-grid">
				<button type="button" class="menu-btn" onclick="moveToPage2('cert')">資格・免許</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('training')">教育訓練</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('reward')">賞罰</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('appointment')">発令</button>
				<button type="button" class="menu-btn" onclick="moveToPage2('referrer')">推薦・身元保証</button>
				<button type="button" class="menu-btn" onclick="moveToRetirement()">退職</button>
			</div>
		</div>

		<!-- 메인 컨테이너 -->
		<div class="container">
			<div class="page-header">
				<h1>社員情報登録 / 詳細照会</h1>
			</div>
			<p class="page-desc"><span class="req">*</span> 印は必須入力項目です。</p>

			<iframe name="hidden_iframe" style="display: none;"></iframe>

			<form action="<%=request.getContextPath()%>/employee/register.do"
				method="post" target="hidden_iframe" onsubmit="return validateForm();">
				
				<input type="hidden" name="companyId" value="1"> 
				<input type="hidden" name="personId" value="1"> 
				<input type="hidden" id="hiddenEmpId" name="employeeId" value="${emp.employeeId}">
				
				<!-- 기본 정보 -->
				<div class="section-title" style="margin-top: 10px;">基本情報</div>
				<table>
					<colgroup>
						<col style="width: 15%;">
						<col style="width: 35%;">
						<col style="width: 15%;">
						<col style="width: 35%;">
					</colgroup>
					<tr>
						<th><span class="req">*</span> 氏名（ハングル）</th>
						<td><input type="text" name="koreanName" value="${emp.koreanName}" required></td>
						<th>英文氏名</th>
						<td><input type="text" name="englishName" value="${emp.englishName}"></td>
					</tr>
					<tr>
						<th><span class="req">*</span> 雇用形態</th>
						<td colspan="3">
							<select name="employmentType" style="width: 300px;" required>
								<option value="정규직" ${emp.employmentType == '정규직' ? 'selected' : ''}>正社員</option>
								<option value="계약직" ${emp.employmentType == '계약직' ? 'selected' : ''}>契約社員</option>
								<option value="파견직" ${emp.employmentType == '파견직' ? 'selected' : ''}>派遣社員</option>
								<option value="위촉직" ${emp.employmentType == '위촉직' ? 'selected' : ''}>業務委託</option>
								<option value="임시직" ${emp.employmentType == '임시직' ? 'selected' : ''}>臨時社員</option>
								<option value="일용직" ${emp.employmentType == '일용직' ? 'selected' : ''}>日雇い</option>
							</select>
						</td>
					</tr>
					<tr>
						<th><span class="req">*</span> 入社日</th>
						<td><input type="date" name="hireDate" value="<fmt:formatDate value='${emp.hireDate}' pattern='yyyy-MM-dd'/>" required></td>
						<th>退社日</th>
						<td>
							<input type="text" name="resignationDate" value="<fmt:formatDate value='${emp.resignationDate}' pattern='yyyy-MM-dd'/>" 
								readonly style="background-color: #f1f5f9; cursor: pointer; color: #666;" onclick="alert('退職日の設定はここでは行えません。');">
						</td>
					</tr>
					<tr>
						<th>部署</th>
						<td>
							<select name="departmentId">
								<option value="">選択</option>
								<c:forEach var="dept" items="${deptList}">
									<option value="${dept.id}" ${emp.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
								</c:forEach>
							</select>
						</td>
						<th>役職</th>
						<td>
							<select name="positionId">
								<option value="">選択</option>
								<c:forEach var="pos" items="${posList}">
									<option value="${pos.id}" ${emp.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<th>内国人/外国人</th>
						<td>
							<label><input type="radio" name="foreignOrDomestic" value="내국인" ${emp == null || emp.foreignOrDomestic == '내국인' ? 'checked' : ''}> 内国人</label>
							<label><input type="radio" name="foreignOrDomestic" value="외국인" ${emp != null && emp.foreignOrDomestic == '외국인' ? 'checked' : ''}> 外国人</label>
						</td>
						<th>住民登録番号</th>
						<td>
							<div class="flex-row-gap">
								<input type="text" name="residentNumber1" value="${emp.residentNumber1}" maxlength="6" style="flex: 1;" placeholder="前6桁"> 
								<span>-</span> 
								<input type="password" name="residentNumber2" value="${emp.residentNumber2}" maxlength="7" style="flex: 1;" placeholder="後7桁">
							</div>
						</td>
					</tr>
					<tr>
						<th>住所</th>
						<td colspan="3"><input type="text" name="address" value="${emp.address}"></td>
					</tr>
					<tr>
						<th>自宅電話番号</th>
						<td><input type="text" name="telPhone" value="${emp.telPhone}"></td>
						<th>携帯電話番号</th>
						<td><input type="text" name="mobile" value="${emp.mobile}"></td>
					</tr>
					<tr>
						<th>メールアドレス</th>
						<td><input type="email" name="email" value="${emp.email}"></td>
						<th>SNS</th>
						<td><input type="text" name="sns" value="${emp.sns}"></td>
					</tr>
					<tr>
						<th>その他詳細</th>
						<td colspan="3"><textarea name="otherDetails" rows="3">${emp.otherDetails}</textarea></td>
					</tr>
				</table>

				<!-- 給与口座情報 -->
				<div class="section-title" id="account">給与口座情報</div>
				<table>
					<colgroup>
						<col style="width: 15%;">
						<col style="width: 35%;">
						<col style="width: 15%;">
						<col style="width: 35%;">
					</colgroup>
					<tr>
						<th><span class="req">*</span> 給与(基本給/日給)</th>
						<td colspan="3">
							<div class="flex-row-gap">
								<input type="number" name="basicPay" value="${emp.basicPay}" placeholder="例: 3000000" style="width: 300px;" required> 
								<span style="font-size: 13px; color: #666;">ウォン (数字のみ入力)</span>
							</div>
						</td>
					</tr>
					<tr>
						<th>銀行名</th>
						<td><input type="text" name="dummy_bankName" value="" placeholder="例: 国民銀行"></td>
						<th>口座番号</th>
						<td><input type="text" name="dummy_accountNumber" value="" placeholder="- を除いて入力"></td>
					</tr>
					<tr>
						<th>口座名義人</th>
						<td colspan="3"><input type="text" name="dummy_depositStocks" value="" style="width: 35%;"></td>
					</tr>
				</table>

				<!-- 保険情報 설정 -->
				<c:set var="chkNps" value="" />
				<c:set var="chkHealth" value="" />
				<c:set var="chkLtci" value="" />
				<c:set var="chkEmp" value="" />
				<c:set var="insNum" value="" />
				<c:set var="insAmt" value="" />
				<c:set var="insStart" value="" />
				<c:set var="insEnd" value="" />
				<c:set var="insRem" value="" />

				<c:if test="${not empty insList}">
					<c:forEach var="ins" items="${insList}">
						<c:if test="${ins.insuranceAgency == '국민연금'}"><c:set var="chkNps" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '건강보험'}"><c:set var="chkHealth" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '장기요양보험'}"><c:set var="chkLtci" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '고용보험'}"><c:set var="chkEmp" value="checked" /></c:if>
						<c:if test="${empty insNum and not empty ins.insuranceNumber}"><c:set var="insNum" value="${ins.insuranceNumber}" /></c:if>
						<c:if test="${empty insAmt and not empty ins.insuranceAmount}"><c:set var="insAmt" value="${ins.insuranceAmount}" /></c:if>
						<c:if test="${empty insStart and not empty ins.insuranceStartDate}"><c:set var="insStart"><fmt:formatDate value="${ins.insuranceStartDate}" pattern="yyyy-MM-dd" /></c:set></c:if>
						<c:if test="${empty insEnd and not empty ins.insuranceEndDate}"><c:set var="insEnd"><fmt:formatDate value="${ins.insuranceEndDate}" pattern="yyyy-MM-dd" /></c:set></c:if>
						<c:if test="${empty insRem and not empty ins.remarks4}"><c:set var="insRem" value="${ins.remarks4}" /></c:if>
					</c:forEach>
				</c:if>

				<div class="section-title">保険情報</div>
				<table>
					<colgroup>
						<col style="width: 15%;">
						<col style="width: 35%;">
						<col style="width: 15%;">
						<col style="width: 35%;">
					</colgroup>
					<tr>
						<th><span class="req">*</span> 4大保険</th>
						<td colspan="3">
							<div class="flex-row-gap" style="gap: 20px;">
								<label><input type="checkbox" name="insuranceAgency" value="국민연금" ${chkNps}> 国民年金</label> 
								<label><input type="checkbox" name="insuranceAgency" value="건강보험" ${chkHealth}> 健康保険</label> 
								<label><input type="checkbox" name="insuranceAgency" value="장기요양보험" ${chkLtci}> 長期療養保険</label> 
								<label><input type="checkbox" name="insuranceAgency" value="고용보험" ${chkEmp}> 雇用保険</label>
							</div>
						</td>
					</tr>
					<tr>
						<th>保険番号</th>
						<td><input type="text" name="insuranceNumber" value="${insNum}" placeholder="- を除いて入力"></td>
						<th>保険加入金額</th>
						<td><input type="number" name="insuranceAmount" value="${insAmt}" placeholder="数字のみ入力"></td>
					</tr>
					<tr>
						<th>加入日(開始日)</th>
						<td><input type="date" name="insuranceStartDate" value="${insStart}"></td>
						<th>満了日(終了日)</th>
						<td><input type="date" name="insuranceEndDate" value="${insEnd}"></td>
					</tr>
					<tr>
						<th>備考</th>
						<td colspan="3"><input type="text" name="remarks4" value="${insRem}"></td>
					</tr>
				</table>

				<!-- 家族事項 -->
				<div class="section-title" id="dependents">
					家族事項
					<button type="button" class="add-btn" onclick="addDependentRow()">+ 追加</button>
				</div>
				<table id="dependentTable">
					<tr>
						<th style="width: 15%; text-align: center;">続柄</th>
						<th style="width: 20%; text-align: center;">氏名</th>
						<th style="width: 15%; text-align: center;">内国人/外国人</th>
						<th style="width: 20%; text-align: center;">住民番号 前半</th>
						<th style="width: 20%; text-align: center;">住民番号 後半</th>
						<th style="width: 10%; text-align: center;">削除</th>
					</tr>
					<c:if test="${not empty depList}">
						<c:forEach var="dep" items="${depList}">
							<tr>
								<td><input type="text" name="relationship" value="${dep.relationship}"></td>
								<td><input type="text" name="parentsName" value="${dep.parentsName}"></td>
								<td>
									<select name="foreignOrDomestic1">
										<option value="내국인" ${dep.foreignOrDomestic1 == '내국인' ? 'selected' : ''}>内国人</option>
										<option value="외국인" ${dep.foreignOrDomestic1 == '외국인' ? 'selected' : ''}>外国人</option>
									</select>
								</td>
								<td><input type="text" name="parentsNumber1" value="${dep.parentsNumber1}" maxlength="6"></td>
								<td><input type="password" name="parentsNumber2" value="${dep.parentsNumber2}" maxlength="7"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'dependentTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 学歴事項 -->
				<div class="section-title" id="degree">
					学歴事項
					<button type="button" class="add-btn" onclick="addDegreeRow()">+ 追加</button>
				</div>
				<table id="degreeTable">
					<tr>
						<th style="width: 15%; text-align: center;">卒業区分</th>
						<th style="width: 25%; text-align: center;">学校名</th>
						<th style="width: 18%; text-align: center;">入学日</th>
						<th style="width: 18%; text-align: center;">卒業日</th>
						<th style="width: 14%; text-align: center;">専攻</th>
						<th style="width: 10%; text-align: center;">修了状態</th>
						<th style="width: 8%; text-align: center;">削除</th>
					</tr>
					<c:if test="${not empty degList}">
						<c:forEach var="deg" items="${degList}">
							<tr>
								<td>
									<select name="graduate">
										<option value="고졸" ${deg.graduate == '고졸' ? 'selected' : ''}>高卒</option>
										<option value="전문대졸" ${deg.graduate == '전문대졸' ? 'selected' : ''}>専門大卒</option>
										<option value="대졸" ${deg.graduate == '대졸' ? 'selected' : ''}>大卒</option>
										<option value="대학원졸" ${deg.graduate == '대학원졸' ? 'selected' : ''}>大学院卒</option>
									</select>
								</td>
								<td><input type="text" name="schoolName" value="${deg.schoolName}"></td>
								<td><input type="date" name="admissionDate" value="<fmt:formatDate value='${deg.admissionDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="date" name="graduationDate" value="<fmt:formatDate value='${deg.graduationDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="major" value="${deg.major}"></td>
								<td>
									<select name="completion">
										<option value="졸업" ${deg.completion == '졸업' ? 'selected' : ''}>卒業</option>
										<option value="수료" ${deg.completion == '수료' ? 'selected' : ''}>修了</option>
										<option value="중퇴" ${deg.completion == '중퇴' ? 'selected' : ''}>中退</option>
									</select>
								</td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'degreeTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 経歴事項 -->
				<div class="section-title" id="career">
					経歴事項
					<button type="button" class="add-btn" onclick="addCareerRow()">+ 追加</button>
				</div>
				<table id="careerTable">
					<tr>
						<th style="width: 25%; text-align: center;">会社名</th>
						<th style="width: 15%; text-align: center;">入社日</th>
						<th style="width: 15%; text-align: center;">退社日</th>
						<th style="width: 15%; text-align: center;">職級</th>
						<th style="width: 22%; text-align: center;">担当業務</th>
						<th style="width: 8%; text-align: center;">削除</th>
					</tr>
					<c:if test="${not empty careerList}">
						<c:forEach var="c" items="${careerList}">
							<tr>
								<td><input type="text" name="companyName" value="${c.companyName}"></td>
								<td><input type="date" name="startDate" value="<fmt:formatDate value='${c.startDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="date" name="endDate" value="<fmt:formatDate value='${c.endDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="finalPosition" value="${c.finalPosition}"></td>
								<td><input type="text" name="responsibilities" value="${c.responsibilities}"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'careerTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 兵役事項 -->
				<div class="section-title" id="military">
					兵役事項
					<button type="button" class="add-btn" onclick="addMilitaryRow()">+ 追加</button>
				</div>
				<table id="militaryTable">
					<tr>
						<th style="width: 10%; text-align: center;">兵役区分</th>
						<th style="width: 10%; text-align: center;">軍別</th>
						<th style="width: 15%; text-align: center;">服務開始日</th>
						<th style="width: 15%; text-align: center;">服務終了日</th>
						<th style="width: 12%; text-align: center;">最終階級</th>
						<th style="width: 12%; text-align: center;">兵科</th>
						<th style="width: 18%; text-align: center;">免除事由</th>
						<th style="width: 8%; text-align: center;">削除</th>
					</tr>
					<c:if test="${not empty milList}">
						<c:forEach var="mil" items="${milList}">
							<tr>
								<td>
									<select name="serviceType">
										<option value="">選択</option>
										<option value="필" ${mil.serviceType == '필' ? 'selected' : ''}>兵役済</option>
										<option value="미필" ${mil.serviceType == '미필' ? 'selected' : ''}>未済</option>
										<option value="면제" ${mil.serviceType == '면제' ? 'selected' : ''}>免除</option>
									</select>
								</td>
								<td>
									<select name="branch">
										<option value="">選択</option>
										<option value="육군" ${mil.branch == '육군' ? 'selected' : ''}>陸軍</option>
										<option value="해군" ${mil.branch == '해군' ? 'selected' : ''}>海軍</option>
										<option value="공군" ${mil.branch == '공군' ? 'selected' : ''}>空軍</option>
										<option value="해병대" ${mil.branch == '해병대' ? 'selected' : ''}>海兵隊</option>
										<option value="기타" ${mil.branch == '기타' ? 'selected' : ''}>その他</option>
									</select>
								</td>
								<td><input type="date" name="servicePeriod1" value="<fmt:formatDate value='${mil.servicePeriod1}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="date" name="servicePeriod2" value="<fmt:formatDate value='${mil.servicePeriod2}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="finalRank" value="${mil.finalRank}"></td>
								<td><input type="text" name="department1" value="${mil.department1}"></td>
								<td><input type="text" name="exemptionReason" value="${mil.exemptionReason}"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'militaryTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="bottom-btns">
					<button type="submit" class="btn-save">保存</button>
					<button type="reset" class="btn-cancel">キャンセル</button>
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
			row.insertCell(0).innerHTML = '<input type="text" name="relationship" placeholder="父、母、配偶者など">';
			row.insertCell(1).innerHTML = '<input type="text" name="parentsName">';
			row.insertCell(2).innerHTML = '<select name="foreignOrDomestic1"><option value="내국인">内国人</option><option value="외국인">外国人</option></select>';
			row.insertCell(3).innerHTML = '<input type="text" name="parentsNumber1" maxlength="6">';
			row.insertCell(4).innerHTML = '<input type="password" name="parentsNumber2" maxlength="7">';
			var cell6 = row.insertCell(5);
			cell6.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'dependentTable\')">X</button>';
			cell6.style.textAlign = "center";
		}

		function addDegreeRow() {
			var table = document.getElementById("degreeTable");
			var row = table.insertRow(-1);
			row.insertCell(0).innerHTML = '<select name="graduate"><option value="고졸">高卒</option><option value="전문대졸">専門大卒</option><option value="대졸">大卒</option><option value="대학원졸">大学院卒</option></select>';
			row.insertCell(1).innerHTML = '<input type="text" name="schoolName" placeholder="学校名を入力">';
			row.insertCell(2).innerHTML = '<input type="date" name="admissionDate">';
			row.insertCell(3).innerHTML = '<input type="date" name="graduationDate">';
			row.insertCell(4).innerHTML = '<input type="text" name="major">';
			row.insertCell(5).innerHTML = '<select name="completion"><option value="졸업">卒業</option><option value="수료">修了</option><option value="중퇴">中退</option></select>';
			var cell7 = row.insertCell(6);
			cell7.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'degreeTable\')">X</button>';
			cell7.style.textAlign = "center";
		}

		function addCareerRow() {
			var table = document.getElementById("careerTable");
			var row = table.insertRow(-1);
			row.insertCell(0).innerHTML = '<input type="text" name="companyName">';
			row.insertCell(1).innerHTML = '<input type="date" name="startDate">';
			row.insertCell(2).innerHTML = '<input type="date" name="endDate">';
			row.insertCell(3).innerHTML = '<input type="text" name="finalPosition">';
			row.insertCell(4).innerHTML = '<input type="text" name="responsibilities">';
			var cell6 = row.insertCell(5);
			cell6.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'careerTable\')">X</button>';
			cell6.style.textAlign = "center";
		}

		function addMilitaryRow() {
			var table = document.getElementById("militaryTable");
			var row = table.insertRow(-1);
			row.insertCell(0).innerHTML = '<select name="serviceType"><option value="">選択</option><option value="필">兵役済</option><option value="미필">未済</option><option value="면제">免除</option></select>';
			row.insertCell(1).innerHTML = '<select name="branch"><option value="">選択</option><option value="육군">陸軍</option><option value="해군">海軍</option><option value="공군">空軍</option><option value="해병대">海兵隊</option><option value="기타">その他</option></select>';
			row.insertCell(2).innerHTML = '<input type="date" name="servicePeriod1">';
			row.insertCell(3).innerHTML = '<input type="date" name="servicePeriod2">';
			row.insertCell(4).innerHTML = '<input type="text" name="finalRank">';
			row.insertCell(5).innerHTML = '<input type="text" name="department1">';
			row.insertCell(6).innerHTML = '<input type="text" name="exemptionReason">';
			var cell8 = row.insertCell(7);
			cell8.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'militaryTable\')">X</button>';
			cell8.style.textAlign = "center";
		}

		function deleteRow(button, tableId) {
			var row = button.parentNode.parentNode;
			if (document.getElementById(tableId).rows.length > 2) { 
				row.parentNode.removeChild(row); 
			} else { 
				alert("最低1行は入力欄が必要です。"); 
			}
		}

		let isSubmitting = false;

		function validateForm() {
			if (isSubmitting) {
				alert("現在保存中です。しばらくお待ちください。");
				return false;
			}

			var insuranceChecked = document.querySelectorAll('input[name="insuranceAgency"]:checked').length > 0;
			if (!insuranceChecked) {
				alert("4大保険を1つ以上選択してください。");
				return false;
			}

			var insInputs = document.querySelectorAll('input[name="insuranceStartDate"], input[name="insuranceEndDate"]');
			var insStart = document.querySelector('input[name="insuranceStartDate"]');
			var insEnd = document.querySelector('input[name="insuranceEndDate"]');
			if (insStart && insEnd && insStart.value && insEnd.value && insStart.value > insEnd.value) {
				alert("保険の加入日(開始日)は満了日(終了日)より後であってはなりません。");
				return false;
			}

			var degreeRows = document.querySelectorAll("#degreeTable tr");
			for (var i = 1; i < degreeRows.length; i++) {
				var admDate = degreeRows[i].querySelector('input[name="admissionDate"]');
				var gradDate = degreeRows[i].querySelector('input[name="graduationDate"]');
				if (admDate && gradDate && admDate.value && gradDate.value && admDate.value > gradDate.value) {
					alert("学歴事項の入学日は卒業日より後であってはなりません。");
					return false;
				}
			}

			var careerRows = document.querySelectorAll("#careerTable tr");
			for (var i = 1; i < careerRows.length; i++) {
				var startDate = careerRows[i].querySelector('input[name="startDate"]');
				var endDate = careerRows[i].querySelector('input[name="endDate"]');
				if (startDate && endDate && startDate.value && endDate.value && startDate.value > endDate.value) {
					alert("経歴事項の入社日は退社日より後であってはなりません。");
					return false;
				}
			}

			var militaryRows = document.querySelectorAll("#militaryTable tr");
			for (var i = 1; i < militaryRows.length; i++) {
				var sPeriod1 = militaryRows[i].querySelector('input[name="servicePeriod1"]');
				var sPeriod2 = militaryRows[i].querySelector('input[name="servicePeriod2"]');
				if (sPeriod1 && sPeriod2 && sPeriod1.value && sPeriod2.value && sPeriod1.value > sPeriod2.value) {
					alert("兵役事項の服務開始日は服務終了日より後であってはなりません。");
					return false;
				}
			}

			isSubmitting = true;
			setTimeout(function() { isSubmitting = false; }, 3000); 
			return true;
		}

		function moveToPage2(tab) {
			const empId = document.getElementById("hiddenEmpId").value;
			if (empId) { location.href = "<%=request.getContextPath()%>/employee/register2.do?employeeId=" + empId + "#" + tab; } 
			else { alert("必須入力欄をすべて入力し、一番下の[保存]ボタンを押してDBに登録した後にのみ、付加情報メニューに移動できます。"); }
		}

		function moveToRetirement() {
			const empId = document.getElementById("hiddenEmpId").value;
			if (empId) {
				location.href = "<%=request.getContextPath()%>/employee/retirement.do?employeeId=" + empId;
			} else {
				alert("必須入力欄をすべて入力し、保存した後に利用できます。");
			}
		}
	</script>
</body>
</html>