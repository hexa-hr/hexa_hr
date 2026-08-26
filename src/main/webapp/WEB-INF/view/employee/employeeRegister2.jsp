<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員付加情報登録 (社員情報 2)</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
body { margin: 0; background-color: #f8f9fa; font-family: sans-serif; }
.wrap { display: flex; align-items: flex-start; max-width: 1400px; margin: 0 auto; background-color: white; border: 1px solid #ddd; }
.sidebar { width: 260px; padding: 20px; background-color: #f4f4f4; border-right: 1px solid #ddd; height: 100vh; position: sticky; top: 0; box-sizing: border-box; overflow-y: auto; }
.container { flex: 1; padding: 40px; box-sizing: border-box; }
.menu-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 15px; }
.menu-btn { background-color: #666; color: white; padding: 6px 5px; text-align: center; border-radius: 3px; cursor: pointer; text-decoration: none; font-size: 13px; border: none; font-weight: bold; display: flex; align-items: center; justify-content: center; height: 35px; word-break: keep-all; }
.menu-btn:hover { background-color: #555; }
.section-title { font-size: 18px; font-weight: bold; margin-top: 40px; margin-bottom: 10px; color: #333; border-bottom: 2px solid #4e73df; padding-bottom: 5px; }
.section-title:first-child { margin-top: 0; }
table { width: 100%; border-collapse: collapse; margin-bottom: 30px; text-align: center; }
th, td { border: 1px solid #ccc; padding: 10px 5px; font-size: 13px; }
th { background-color: #f8f9fa; color: #333; }
input[type="text"], input[type="date"], input[type="password"], input[type="number"], select { padding: 4px; width: 90%; border: 1px solid #ccc; box-sizing: border-box; }
.add-btn { float: right; padding: 4px 12px; background-color: #4e73df; color: white; border: none; border-radius: 3px; cursor: pointer; font-size: 12px; font-weight: bold; }
.add-btn:hover { background-color: #2e59d9; }
.del-btn { background-color: #a5a5a5; color: white; border: none; border-radius: 3px; cursor: pointer; padding: 4px 8px; font-size: 12px; }
.del-btn:hover { background-color: #999999; }
.table-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 5px; margin-top: 15px; }
.table-header .title { color: #0056b3; font-weight: bold; font-size: 14px; }
.btn-outline-add { padding: 4px 10px; font-size: 12px; background: white; border: 1px solid #ccc; cursor: pointer; color: #4e73df; font-weight: bold; border-radius: 3px; }
.btn-outline-del { padding: 4px 10px; font-size: 12px; background: white; border: 1px solid #ccc; cursor: pointer; color: #555; border-radius: 3px; margin-left: 5px;}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="sidebar">
			<div
				style="background: white; padding: 15px; border: 1px solid #ccc; text-align: center; margin-bottom: 20px;">
				<img src="<%=request.getContextPath()%>/images/default_profile.png"
					alt="写真" style="width: 80px; height: 100px; background: #eee;">
				<p style="margin: 10px 0 0 0; font-weight: bold; font-size: 14px;">社員番号:
					${employeeId}</p>
			</div>

			<h3>社員情報 1</h3>
			<div class="menu-grid">
				<a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#account"
					class="menu-btn">給与<br>4大保険
				</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#dependents"
					class="menu-btn">扶養家族</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#degree"
					class="menu-btn">学歴</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#career"
					class="menu-btn">経歴</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#military"
					class="menu-btn">兵役</a>
			</div>

			<h3>社員情報 2</h3>
			<div class="menu-grid">
				<a href="#cert" class="menu-btn">資格・免許</a> <a href="#training" class="menu-btn">教育訓練</a>
				<a href="#reward" class="menu-btn">賞罰</a> <a href="#appointment" class="menu-btn">発令</a>
				<!-- 🌟 퇴직 메뉴 앵커 제거됨 -->
				<a href="#referrer" class="menu-btn" style="grid-column: 1 / span 2;">推薦・身元保証</a>
			</div>
		</div>

		<div class="container">
			<iframe name="hidden_iframe" style="display: none;"></iframe>
			<form
				action="<%=request.getContextPath()%>/employee/register2_process.do"
				method="post" target="hidden_iframe">
				<input type="hidden" name="employeeId" value="${employeeId}">

				<!-- 1. 자격·면허 & 어학능력 -->
				<div class="section-title" id="cert">資格・免許 & 語学能力</div>

				<div class="table-header">
					<div class="title">+ 資格 & 免許</div>
					<div>
						<button type="button" class="btn-outline-add"
							onclick="addRow('certTable')">+ 追加</button>
						<button type="button" class="btn-outline-del"
							onclick="deleteSelectedRows('certTable')">選択削除</button>
					</div>
				</div>
				<table id="certTable"
					style="border-top: 2px solid #007bff; margin-bottom: 20px;">
					<tr>
						<th style="width: 5%;"><input type="checkbox"
							onclick="toggleAll(this, 'certTable')"></th>
						<th style="width: 25%;">資格/免許名</th>
						<th style="width: 15%;">取得日</th>
						<th style="width: 20%;">発行機関</th>
						<th style="width: 20%;">証番号</th>
						<th style="width: 15%;">備考</th>
					</tr>
					<c:if test="${not empty certList}">
						<c:forEach var="cert" items="${certList}">
							<tr>
								<td><input type="checkbox" class="row-check"></td>
								<td><input type="text" name="certName"
									value="${cert.certificationName}"></td>
								<td><input type="date" name="certAcqDate"
									value="<fmt:formatDate value='${cert.acquisitionDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="certIssuer"
									value="${cert.issuingOrganization}"></td>
								<td><input type="text" name="certNumber"
									value="${cert.certificationNumber}"></td>
								<td><input type="text" name="certRemarks"
									value="${cert.remarks1}"></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="table-header">
					<div class="title">+ 語学能力</div>
					<div>
						<button type="button" class="btn-outline-add"
							onclick="addRow('langTable')">+ 追加</button>
						<button type="button" class="btn-outline-del"
							onclick="deleteSelectedRows('langTable')">選択削除</button>
					</div>
				</div>
				<table id="langTable" style="border-top: 2px solid #007bff;">
					<tr>
						<th style="width: 5%;"><input type="checkbox"
							onclick="toggleAll(this, 'langTable')"></th>
						<th style="width: 15%;">外国語名</th>
						<th style="width: 15%;">試験</th>
						<th style="width: 15%;">公認スコア</th>
						<th style="width: 15%;">取得日</th>
						<th style="width: 10%;">読解</th>
						<th style="width: 10%;">作文</th>
						<th style="width: 15%;">会話</th>
					</tr>
					<c:if test="${not empty langList}">
						<c:forEach var="lang" items="${langList}">
							<tr>
								<td><input type="checkbox" class="row-check"></td>
								<td><input type="text" name="langName"
									value="${lang.language}"></td>
								<td><input type="text" name="langTest"
									value="${lang.testName}"></td>
								<td><input type="text" name="langScore"
									value="${lang.officialScore}"></td>
								<td><input type="date" name="langAcqDate"
									value="<fmt:formatDate value='${lang.acquisitionDate1}' pattern='yyyy-MM-dd'/>"></td>
								<td><select name="langReading"><option value="상"
											${lang.readingAbility == '상' ? 'selected' : ''}>上</option>
										<option value="중"
											${lang.readingAbility == '중' ? 'selected' : ''}>中</option>
										<option value="하"
											${lang.readingAbility == '하' ? 'selected' : ''}>下</option></select></td>
								<td><select name="langWriting"><option value="상"
											${lang.writingAbility == '상' ? 'selected' : ''}>上</option>
										<option value="중"
											${lang.writingAbility == '중' ? 'selected' : ''}>中</option>
										<option value="하"
											${lang.writingAbility == '하' ? 'selected' : ''}>下</option></select></td>
								<td><select name="langSpeaking"><option value="상"
											${lang.speakingAbility == '상' ? 'selected' : ''}>上</option>
										<option value="중"
											${lang.speakingAbility == '중' ? 'selected' : ''}>中</option>
										<option value="하"
											${lang.speakingAbility == '하' ? 'selected' : ''}>下</option></select></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 2. 교육 훈련 -->
				<div class="section-title" id="training">
					教育訓練
					<button type="button" class="add-btn"
						onclick="addRow('trainingTable')">+ 追加</button>
				</div>
				<table id="trainingTable">
					<tr>
						<th>教育区分</th>
						<th>教育名</th>
						<th>開始日</th>
						<th>終了日</th>
						<th>教育機関</th>
						<th>教育費</th>
						<th>還付金</th>
						<th>削除</th>
					</tr>
					<c:if test="${not empty trainingList}">
						<c:forEach var="t" items="${trainingList}">
							<tr>
								<td><input type="text" name="trainingType"
									value="${t.trainingType}"></td>
								<td><input type="text" name="trainingName"
									value="${t.trainingName}"></td>
								<td><input type="date" name="trainingStartDate"
									value="<fmt:formatDate value='${t.trainingStartDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="date" name="trainingEndDate"
									value="<fmt:formatDate value='${t.trainingEndDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="trainingOrganization"
									value="${t.trainingOrganization}"></td>
								<td><input type="number" name="trainingCost"
									value="${t.trainingCost}"></td>
								<td><input type="number" name="refundableTrainingCost"
									value="${t.refundableTrainingCost}"></td>
								<td><button type="button" class="del-btn"
										onclick="deleteRow(this, 'trainingTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 3. 상벌 -->
				<div class="section-title" id="reward">
					賞罰
					<button type="button" class="add-btn"
						onclick="addRow('rewardTable')">+ 追加</button>
				</div>
				<table id="rewardTable">
					<tr>
						<th>区分</th>
						<th>賞罰名</th>
						<th>賞罰権者</th>
						<th>日付</th>
						<th>内容</th>
						<th>備考</th>
						<th>削除</th>
					</tr>
					<c:if test="${not empty rewardList}">
						<c:forEach var="r" items="${rewardList}">
							<tr>
								<td><input type="text" name="rewardPenaltyType"
									value="${r.rewardPenaltyType}"></td>
								<td><input type="text" name="rewardPenaltyName"
									value="${r.rewardPenaltyName}"></td>
								<td><input type="text" name="rewardPenaltyGiver"
									value="${r.rewardPenaltyGiver}"></td>
								<td><input type="date" name="rewardPenaltyDate"
									value="<fmt:formatDate value='${r.rewardPenaltyDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><input type="text" name="rewardPenaltyDescription"
									value="${r.rewardPenaltyDescription}"></td>
								<td><input type="text" name="remarks2"
									value="${r.remarks2}"></td>
								<td><button type="button" class="del-btn"
										onclick="deleteRow(this, 'rewardTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 4. 발령 -->
				<div class="section-title" id="appointment">
					発令
					<button type="button" class="add-btn"
						onclick="addRow('appointmentTable')">+ 追加</button>
				</div>
				<table id="appointmentTable">
					<tr>
						<th>発令区分</th>
						<th>日付</th>
						<th>部署</th>
						<th>役職</th>
						<th>職責</th>
						<th>備考</th>
						<th>削除</th>
					</tr>
					<c:if test="${not empty apptList}">
						<c:forEach var="a" items="${apptList}">
							<tr>
								<td><input type="text" name="appointmentType"
									value="${a.appointmentType}"></td>
								<td><input type="date" name="appointmentDate"
									value="<fmt:formatDate value='${a.appointmentDate}' pattern='yyyy-MM-dd'/>"></td>
								<td><select name="departmentId">
										<option value="">選択</option>
										<c:forEach var="dept" items="${deptList}">
											<option value="${dept.id}"
												${a.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
										</c:forEach>
								</select></td>
								<td><select name="positionId">
										<option value="">選択</option>
										<c:forEach var="pos" items="${posList}">
											<option value="${pos.id}"
												${a.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
										</c:forEach>
								</select></td>
								<td><input type="text" name="positionType"
									value="${a.positionType}"></td>
								<td><input type="text" name="remarks3"
									value="${a.remarks3}"></td>
								<td><button type="button" class="del-btn"
										onclick="deleteRow(this, 'appointmentTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<!-- 5. 추천 & 신원보증 -->
				<div class="section-title" id="referrer">
					推薦 & 身元保証 (推薦人)
					<button type="button" class="add-btn"
						onclick="addRow('referrerTable')">+ 追加</button>
				</div>
				<table id="referrerTable">
					<tr>
						<th>氏名</th>
						<th>続柄</th>
						<th>会社名</th>
						<th>役職</th>
						<th>電話番号</th>
						<th>削除</th>
					</tr>
					<c:if test="${not empty refList}">
						<c:forEach var="ref" items="${refList}">
							<tr>
								<td><input type="text" name="referrerName"
									value="${ref.referrerName}"></td>
								<td><input type="text" name="referrerRelationship"
									value="${ref.referrerRelationship}"></td>
								<td><input type="text" name="referrerCompanyName"
									value="${ref.referrerCompanyName}"></td>
								<td><input type="text" name="referrerPosition"
									value="${ref.referrerPosition}"></td>
								<td><input type="text" name="referrerPhoneNumber"
									value="${ref.referrerPhoneNumber}"></td>
								<td><button type="button" class="del-btn"
										onclick="deleteRow(this, 'referrerTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

				<div class="section-title" style="margin-top: 10px;">
					身元保証 (保証人)
					<button type="button" class="add-btn"
						onclick="addRow('guarantorTable')">+ 追加</button>
				</div>
				<table id="guarantorTable">
					<tr>
						<th>氏名</th>
						<th>続柄</th>
						<th>住民登録番号</th>
						<th>保証金額</th>
						<th>連絡先</th>
						<th>削除</th>
					</tr>
					<c:if test="${not empty guaList}">
						<c:forEach var="gua" items="${guaList}">
							<tr>
								<td>
									<input type="hidden" name="guaStartDate" value="<fmt:formatDate value='${gua.guaranteeDate}' pattern='yyyy-MM-dd'/>">
									<input type="hidden" name="guaEndDate" value="<fmt:formatDate value='${gua.guaranteeExpirationDate}' pattern='yyyy-MM-dd'/>">
									<input type="text" name="guaName" value="${gua.guarantorName}">
								</td>
								<td><input type="text" name="guaRelation"
									value="${gua.guarantorRelationship}"></td>
								<td><input type="text" name="guaRrn"
									value="${gua.guarantorResidentNumber}" placeholder="[住民登録番号入力]"></td>
								<td><input type="number" name="guaAmount"
									value="${gua.guaranteeAmount}"></td>
								<td><input type="text" name="guaPeriod"
									value="${gua.guarantorPhoneNumber}" placeholder="連絡先"></td>
								<td><button type="button" class="del-btn"
										onclick="deleteRow(this, 'guarantorTable')">X</button></td>
							</tr>
						</c:forEach>
					</c:if>
				</table>
                <!-- 🌟 6. 퇴직 정보 테이블 전체 영역 제거됨 -->

				<div
					style="text-align: center; margin-top: 50px; margin-bottom: 50px;">
					<button type="submit"
						style="padding: 12px 40px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold;">付加情報保存</button>
					<button type="reset"
						style="padding: 12px 40px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px; font-weight: bold;">初期化</button>
				</div>
			</form>
		</div>
	</div>

	<script>
		var deptOptions = '<option value="">選択</option>';
		<c:forEach var="dept" items="${deptList}">
			deptOptions += '<option value="${dept.id}">${dept.name}</option>';
		</c:forEach>

		var posOptions = '<option value="">選択</option>';
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

            const urlParams = new URLSearchParams(window.location.search);
            const tab = urlParams.get('tab');
            if (tab) {
                const element = document.getElementById(tab);
                if (element) {
                    setTimeout(() => {
                        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    }, 100);
                }
            }
		};

		function addRow(tableId) {
			var table = document.getElementById(tableId);
			var row = table.insertRow(-1);
			var html = "";
			
			if (tableId === 'certTable') {
				html = '<td><input type="checkbox" class="row-check"></td><td><input type="text" name="certName"></td><td><input type="date" name="certAcqDate"></td><td><input type="text" name="certIssuer"></td><td><input type="text" name="certNumber"></td><td><input type="text" name="certRemarks"></td>';
			} else if (tableId === 'langTable') {
				html = '<td><input type="checkbox" class="row-check"></td><td><input type="text" name="langName"></td><td><input type="text" name="langTest"></td><td><input type="text" name="langScore"></td><td><input type="date" name="langAcqDate"></td><td><select name="langReading"><option value="상">上</option><option value="중">中</option><option value="하">下</option></select></td><td><select name="langWriting"><option value="상">上</option><option value="중">中</option><option value="하">下</option></select></td><td><select name="langSpeaking"><option value="상">上</option><option value="중">中</option><option value="하">下</option></select></td>';
			} else if (tableId === 'trainingTable') {
				html = '<td><input type="text" name="trainingType"></td><td><input type="text" name="trainingName"></td><td><input type="date" name="trainingStartDate"></td><td><input type="date" name="trainingEndDate"></td><td><input type="text" name="trainingOrganization"></td><td><input type="number" name="trainingCost"></td><td><input type="number" name="refundableTrainingCost"></td>';
			} else if (tableId === 'rewardTable') {
				html = '<td><input type="text" name="rewardPenaltyType"></td><td><input type="text" name="rewardPenaltyName"></td><td><input type="text" name="rewardPenaltyGiver"></td><td><input type="date" name="rewardPenaltyDate"></td><td><input type="text" name="rewardPenaltyDescription"></td><td><input type="text" name="remarks2"></td>';
			} else if (tableId === 'appointmentTable') {
				html = '<td><input type="text" name="appointmentType"></td><td><input type="date" name="appointmentDate"></td><td><select name="departmentId">' + deptOptions + '</select></td><td><select name="positionId">' + posOptions + '</select></td><td><input type="text" name="positionType"></td><td><input type="text" name="remarks3"></td>';
			} else if (tableId === 'referrerTable') {
				html = '<td><input type="text" name="referrerName"></td><td><input type="text" name="referrerRelationship"></td><td><input type="text" name="referrerCompanyName"></td><td><input type="text" name="referrerPosition"></td><td><input type="text" name="referrerPhoneNumber"></td>';
			} else if (tableId === 'guarantorTable') {
				html = '<td><input type="hidden" name="guaStartDate" value=""><input type="hidden" name="guaEndDate" value=""><input type="text" name="guaName"></td><td><input type="text" name="guaRelation"></td><td><input type="text" name="guaRrn" placeholder="[住民登録番号入力]"></td><td><input type="number" name="guaAmount"></td><td><input type="text" name="guaPeriod" placeholder="連絡先"></td>';
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
				alert("最低1行は入力欄が必要です。");
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
			if (checkboxes.length === 0) { alert("削除する項目を選択してください。"); return; }
			if (table.rows.length - 1 === checkboxes.length) { alert("最低1行の入力欄は残しておく必要があります。"); return; }
			for (var i = checkboxes.length - 1; i >= 0; i--) {
				var row = checkboxes[i].closest('tr');
				row.parentNode.removeChild(row);
			}
			table.querySelector('th input[type="checkbox"]').checked = false;
		}
	</script>
</body>
</html>