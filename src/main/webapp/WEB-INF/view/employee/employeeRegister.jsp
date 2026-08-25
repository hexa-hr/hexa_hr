<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員新規登録 / 詳細</title>

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
    <img src="<%=request.getContextPath()%>/images/default_profile.png" alt="写真" style="width: 80px; height: 100px; background: #eee;">
    
    <!-- 社員番号を表示するコード -->
    <c:choose>
        <c:when test="${not empty emp.employeeId}">
            <p style="margin: 10px 0 0 0; font-weight: bold; font-size: 15px; color: #0056b3;">社員番号: ${emp.employeeId}</p>
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
				<button type="button" class="menu-btn" onclick="moveToPage2('retirement')">退職</button>
			</div>
		</div>

		<div class="container">
			<h2>社員情報登録 / 詳細照会</h2>
			<p style="color: red; font-size: 12px;">* 印は必須入力項目です。</p>

			<iframe name="hidden_iframe" style="display: none;"></iframe>

			<form action="<%=request.getContextPath()%>/employee/register.do" method="post" target="hidden_iframe" onsubmit="return validateForm();">
				<input type="hidden" name="companyId" value="1"> 
				<input type="hidden" name="personId" value="1">
<input type="hidden" id="hiddenEmpId" name="employeeId" value="${emp.employeeId}">
				<div class="section-title">基本情報</div>
				<table>
					<tr>
						<th>* 氏名（ハングル）</th>
						<td><input type="text" name="koreanName" value="${emp.koreanName}" required></td>
						<th>英文氏名</th>
						<td><input type="text" name="englishName" value="${emp.englishName}"></td>
					</tr>
					<tr>
						<th>* 雇用形態</th>
						<td colspan="3"><select name="employmentType" required>
								<option value="정규직" ${emp.employmentType == '정규직' ? 'selected' : ''}>正社員</option>
								<option value="계약직" ${emp.employmentType == '계약직' ? 'selected' : ''}>契約社員</option>
								<option value="파견직" ${emp.employmentType == '파견직' ? 'selected' : ''}>派遣社員</option>
								<option value="위촉직" ${emp.employmentType == '위촉직' ? 'selected' : ''}>業務委託</option>
								<option value="임시직" ${emp.employmentType == '임시직' ? 'selected' : ''}>臨時社員</option>
								<option value="일용직" ${emp.employmentType == '일용직' ? 'selected' : ''}>日雇い</option>
						</select></td>
					</tr>
					<tr>
						<th>* 入社日</th>
						<td><input type="date" name="hireDate" value="<fmt:formatDate value='${emp.hireDate}' pattern='yyyy-MM-dd'/>" required></td>
						<th>退社日</th>
						<td>
							<!-- 🌟 변경 포인트: 지시하신 대로 클릭 시 알림 메시지가 뜨도록 적용했습니다. -->
							<input type="text" name="resignationDate" value="<fmt:formatDate value='${emp.resignationDate}' pattern='yyyy-MM-dd'/>" 
							       readonly style="background-color: #eeeeee; cursor: pointer;" 
							       onclick="alert('退職処理は該当ページでは行えません。\n[社員情報 2]ページをご利用ください。');">
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
							<input type="radio" name="foreignOrDomestic" value="내국인" ${emp == null || emp.foreignOrDomestic == '내국인' ? 'checked' : ''}> 内国人 
							<input type="radio" name="foreignOrDomestic" value="외국인" ${emp != null && emp.foreignOrDomestic == '외국인' ? 'checked' : ''}> 外国人
						</td>
						<th>住民登録番号</th>
						<td>
							<input type="text" name="residentNumber1" value="${emp.residentNumber1}" maxlength="6" style="width: 30%;" placeholder="前6桁"> - 
							<input type="password" name="residentNumber2" value="${emp.residentNumber2}" maxlength="7" style="width: 30%;" placeholder="後7桁">
						</td>
					</tr>
					<tr>
						<th>住所</th>
						<td colspan="3"><input type="text" name="address" value="${emp.address}" style="width: 95%;"></td>
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
						<td colspan="3"><textarea name="otherDetails" rows="3" style="width: 95%; padding: 5px;">${emp.otherDetails}</textarea></td>
					</tr>
				</table>

				<!-- 給与口座情報 -->
				<div class="section-title" id="account">給与口座情報</div>
				<table>
					<tr>
						<th>* 給与(基本給/日給)</th>
						<td colspan="3">
							<input type="number" name="basicPay" value="${emp.basicPay}" placeholder="例: 3000000" style="width: 30%;" required> 
							<span style="font-size: 13px; color: #666; margin-left: 5px;">ウォン (数字のみ入力)</span>
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
						<td colspan="3"><input type="text" name="dummy_depositStocks" value="" style="width: 36%;"></td>
					</tr>
				</table>

				<!-- 保険情報 -->
				<c:set var="chkNps" value="" /><c:set var="chkHealth" value="" /><c:set var="chkLtci" value="" /><c:set var="chkEmp" value="" />
				<c:set var="insNum" value="" /><c:set var="insAmt" value="" /><c:set var="insStart" value="" /><c:set var="insEnd" value="" /><c:set var="insRem" value="" />

				<c:if test="${not empty insList}">
					<c:forEach var="ins" items="${insList}">
						<c:if test="${ins.insuranceAgency == '국민연금'}"><c:set var="chkNps" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '건강보험'}"><c:set var="chkHealth" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '장기요양보험'}"><c:set var="chkLtci" value="checked" /></c:if>
						<c:if test="${ins.insuranceAgency == '고용보험'}"><c:set var="chkEmp" value="checked" /></c:if>
						<c:if test="${empty insNum and not empty ins.insuranceNumber}"><c:set var="insNum" value="${ins.insuranceNumber}" /></c:if>
						<c:if test="${empty insAmt and not empty ins.insuranceAmount}"><c:set var="insAmt" value="${ins.insuranceAmount}" /></c:if>
						<c:if test="${empty insStart and not empty ins.insuranceStartDate}"><c:set var="insStart"><fmt:formatDate value="${ins.insuranceStartDate}" pattern="yyyy-MM-dd"/></c:set></c:if>
						<c:if test="${empty insEnd and not empty ins.insuranceEndDate}"><c:set var="insEnd"><fmt:formatDate value="${ins.insuranceEndDate}" pattern="yyyy-MM-dd"/></c:set></c:if>
						<c:if test="${empty insRem and not empty ins.remarks4}"><c:set var="insRem" value="${ins.remarks4}" /></c:if>
					</c:forEach>
				</c:if>

				<div class="section-title">保険情報</div>
				<table>
					<tr>
						<th>* 4大保険</th>
						<td colspan="3">
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="국민연금" ${chkNps} style="width: auto;"> 国民年金</label>
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="건강보험" ${chkHealth} style="width: auto;"> 健康保険</label>
							<label style="margin-right: 15px; cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="장기요양보험" ${chkLtci} style="width: auto;"> 長期療養保険</label>
							<label style="cursor: pointer;"><input type="checkbox" name="insuranceAgency" value="고용보험" ${chkEmp} style="width: auto;"> 雇用保険</label>
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
						<td colspan="3"><input type="text" name="remarks4" value="${insRem}" style="width: 95%;"></td>
					</tr>
				</table>

				<!-- 家族事項 -->
				<div class="section-title" id="dependents">家族事項<button type="button" class="add-btn" onclick="addDependentRow()">+ 家族追加</button></div>
				<table id="dependentTable">
					<tr><th style="width: 15%;">続柄</th><th style="width: 20%;">氏名</th><th style="width: 15%;">内国人/外国人</th><th style="width: 20%;">住民番号 前半</th><th style="width: 20%;">住民番号 後半</th><th style="width: 10%;">削除</th></tr>
					<c:if test="${not empty depList}">
						<c:forEach var="dep" items="${depList}">
							<tr>
								<td style="text-align: center;"><input type="text" name="relationship" value="${dep.relationship}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="parentsName" value="${dep.parentsName}" style="width: 90%;"></td>
								<td style="text-align: center;">
									<select name="foreignOrDomestic1" style="width: 90%;">
										<option value="내국인" ${dep.foreignOrDomestic1 == '내국인' ? 'selected' : ''}>内国人</option>
										<option value="외국인" ${dep.foreignOrDomestic1 == '외국인' ? 'selected' : ''}>外国人</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="text" name="parentsNumber1" value="${dep.parentsNumber1}" maxlength="6" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="password" name="parentsNumber2" value="${dep.parentsNumber2}" maxlength="7" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'dependentTable')">X 削除</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 学歴事項 -->
				<div class="section-title" id="degree">学歴事項<button type="button" class="add-btn" style="background-color: #f6c23e;" onclick="addDegreeRow()">+ 学歴追加</button></div>
				<table id="degreeTable">
					<tr><th style="width: 15%;">卒業区分</th><th style="width: 25%;">学校名</th><th style="width: 20%;">入学日</th><th style="width: 20%;">卒業日</th><th style="width: 15%;">専攻</th><th style="width: 10%;">修了状態</th><th style="width: 10%;">削除</th></tr>
					<c:if test="${not empty degList}">
						<c:forEach var="deg" items="${degList}">
							<tr>
								<td style="text-align: center;">
									<select name="graduate" style="width: 90%;">
										<option value="고졸" ${deg.graduate == '고졸' ? 'selected' : ''}>高卒</option>
										<option value="전문대졸" ${deg.graduate == '전문대졸' ? 'selected' : ''}>専門大卒</option>
										<option value="대졸" ${deg.graduate == '대졸' ? 'selected' : ''}>大卒</option>
										<option value="대학원졸" ${deg.graduate == '대학원졸' ? 'selected' : ''}>大学院卒</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="text" name="schoolName" value="${deg.schoolName}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="admissionDate" value="<fmt:formatDate value='${deg.admissionDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="graduationDate" value="<fmt:formatDate value='${deg.graduationDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="major" value="${deg.major}" style="width: 90%;"></td>
								<td style="text-align: center;">
									<select name="completion" style="width: 90%;">
										<option value="졸업" ${deg.completion == '졸업' ? 'selected' : ''}>卒業</option>
										<option value="수료" ${deg.completion == '수료' ? 'selected' : ''}>修了</option>
										<option value="중퇴" ${deg.completion == '중퇴' ? 'selected' : ''}>中退</option>
									</select>
								</td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'degreeTable')">X 削除</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="section-title" id="career">経歴事項<button type="button" class="add-btn" style="background-color: #4e73df;" onclick="addCareerRow()">+ 経歴追加</button></div>
				<table id="careerTable">
					<tr><th style="width: 20%;">会社名</th><th style="width: 15%;">入社日</th><th style="width: 15%;">退社日</th><th style="width: 15%;">職級</th><th style="width: 25%;">担当業務</th><th style="width: 10%;">削除</th></tr>
					<c:if test="${not empty careerList}">
						<c:forEach var="c" items="${careerList}">
							<tr>
								<td style="text-align: center;"><input type="text" name="companyName" value="${c.companyName}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="startDate" value="<fmt:formatDate value='${c.startDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="endDate" value="<fmt:formatDate value='${c.endDate}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="finalPosition" value="${c.finalPosition}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="responsibilities" value="${c.responsibilities}" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'careerTable')">X 削除</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 兵役事項 -->
				<div class="section-title" id="military">兵役事項<button type="button" class="add-btn" style="background-color: #36b9cc;" onclick="addMilitaryRow()">+ 兵役追加</button></div>
				<table id="militaryTable">
					<tr><th style="width: 10%;">兵役区分</th><th style="width: 10%;">軍別</th><th style="width: 15%;">服務開始日</th><th style="width: 15%;">服務終了日</th><th style="width: 15%;">最終階級</th><th style="width: 15%;">兵科</th><th style="width: 12%;">免除事由</th><th style="width: 8%;">削除</th></tr>
					<c:if test="${not empty milList}">
						<c:forEach var="mil" items="${milList}">
							<tr>
								<td style="text-align: center;">
									<select name="serviceType" style="width:90%;">
										<option value="">選択</option>
										<option value="필" ${mil.serviceType == '필' ? 'selected' : ''}>兵役済</option>
										<option value="미필" ${mil.serviceType == '미필' ? 'selected' : ''}>未済</option>
										<option value="면제" ${mil.serviceType == '면제' ? 'selected' : ''}>免除</option>
									</select>
								</td>
								<td style="text-align: center;">
									<select name="branch" style="width:90%;">
										<option value="">選択</option>
										<option value="육군" ${mil.branch == '육군' ? 'selected' : ''}>陸軍</option>
										<option value="해군" ${mil.branch == '해군' ? 'selected' : ''}>海軍</option>
										<option value="공군" ${mil.branch == '공군' ? 'selected' : ''}>空軍</option>
										<option value="해병대" ${mil.branch == '해병대' ? 'selected' : ''}>海兵隊</option>
										<option value="기타" ${mil.branch == '기타' ? 'selected' : ''}>その他</option>
									</select>
								</td>
								<td style="text-align: center;"><input type="date" name="servicePeriod1" value="<fmt:formatDate value='${mil.servicePeriod1}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="date" name="servicePeriod2" value="<fmt:formatDate value='${mil.servicePeriod2}' pattern='yyyy-MM-dd'/>" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="finalRank" value="${mil.finalRank}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="department1" value="${mil.department1}" style="width: 90%;"></td>
								<td style="text-align: center;"><input type="text" name="exemptionReason" value="${mil.exemptionReason}" style="width: 90%;"></td>
								<td style="text-align: center;"><button type="button" class="del-btn" onclick="deleteRow(this, 'militaryTable')">X 削除</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div style="text-align: center; max-width: 900px; margin-top: 20px;">
					<button type="submit" style="padding: 10px 30px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;">保存</button>
					<button type="reset" style="padding: 10px 30px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px;">キャンセル</button>
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
			cell1.innerHTML = '<input type="text" name="relationship" style="width: 90%;" placeholder="父、母、配偶者など">';
			cell2.innerHTML = '<input type="text" name="parentsName" style="width: 90%;">';
			cell3.innerHTML = '<select name="foreignOrDomestic1" style="width: 90%;"><option value="내국인">内国人</option><option value="외국인">外国人</option></select>';
			cell4.innerHTML = '<input type="text" name="parentsNumber1" maxlength="6" style="width: 90%;">';
			cell5.innerHTML = '<input type="password" name="parentsNumber2" maxlength="7" style="width: 90%;">';
			cell6.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'dependentTable\')">X 削除</button>';
			for(var i=0; i<6; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function addDegreeRow() {
			var table = document.getElementById("degreeTable");
			var row = table.insertRow(-1);
			var cell1 = row.insertCell(0); var cell2 = row.insertCell(1); var cell3 = row.insertCell(2); var cell4 = row.insertCell(3); var cell5 = row.insertCell(4); var cell6 = row.insertCell(5); var cell7 = row.insertCell(6);
			cell1.innerHTML = '<select name="graduate" style="width: 90%;"><option value="고졸">高卒</option><option value="전문대졸">専門大卒</option><option value="대졸">大卒</option><option value="대학원졸">大学院卒</option></select>';
			cell2.innerHTML = '<input type="text" name="schoolName" style="width: 90%;" placeholder="学校名を入力">';
			cell3.innerHTML = '<input type="date" name="admissionDate" style="width: 90%;">';
			cell4.innerHTML = '<input type="date" name="graduationDate" style="width: 90%;">';
			cell5.innerHTML = '<input type="text" name="major" style="width: 90%;">';
			cell6.innerHTML = '<select name="completion" style="width: 90%;"><option value="졸업">卒業</option><option value="수료">修了</option><option value="중퇴">中退</option></select>';
			cell7.innerHTML = '<button type="button" class="del-btn" onclick="deleteRow(this, \'degreeTable\')">X 削除</button>';
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
					+ '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'careerTable\')">X 削除</button></td>';
			row.innerHTML = html;
			for(var i=0; i<row.cells.length; i++) { row.cells[i].style.textAlign = "center"; }
		}

		function addMilitaryRow() {
			var table = document.getElementById("militaryTable");
			var row = table.insertRow(-1);
			var html = '<td><select name="serviceType" style="width:90%;"><option value="">選択</option><option value="필">兵役済</option><option value="미필">未済</option><option value="면제">免除</option></select></td>'
					+ '<td><select name="branch" style="width:90%;"><option value="">選択</option><option value="육군">陸軍</option><option value="해군">海軍</option><option value="공군">空軍</option><option value="해병대">海兵隊</option><option value="기타">その他</option></select></td>'
					+ '<td><input type="date" name="servicePeriod1" style="width: 90%;"></td>'
					+ '<td><input type="date" name="servicePeriod2" style="width: 90%;"></td>'
					+ '<td><input type="text" name="finalRank" style="width: 90%;"></td>'
					+ '<td><input type="text" name="department1" style="width: 90%;"></td>'
					+ '<td><input type="text" name="exemptionReason" style="width: 90%;"></td>'
					+ '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'militaryTable\')">X 削除</button></td>';
			row.innerHTML = html;
			for(var i=0; i<row.cells.length; i++) { row.cells[i].style.textAlign = "center"; }
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

            // 4대보험을 1개 이상 선택했는지 체크
            var insuranceChecked = document.querySelectorAll('input[name="insuranceAgency"]:checked').length > 0;
            if (!insuranceChecked) {
                alert("4大保険を1つ以上選択してください。"); // 4대보험을 1개 이상 선택해주세요.
                return false;
            }

		    isSubmitting = true;
		    setTimeout(function() { isSubmitting = false; }, 3000); 
		    return true;
		}

		function moveToPage2(tab) {
			const empId = document.getElementById("hiddenEmpId").value;
			if (empId) { 
				// 🌟 변경 포인트: &tab= 파라미터 방식에서 브라우저가 직접 스크롤을 내릴 수 있도록 해시태그(#) 방식으로 수정
				location.href = "<%=request.getContextPath()%>/employee/register2.do?employeeId=" + empId + "#" + tab; 
			} 
			else { 
				alert("必須入力欄をすべて入力し、一番下の[保存]ボタンを押してDBに登録した後にのみ、付加情報メニューに移動できます。"); 
			}
		}
	</script>
</body>
</html>