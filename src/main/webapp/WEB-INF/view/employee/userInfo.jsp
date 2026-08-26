<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー情報</title>

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

.container {
	padding: 30px 40px;
	background-color: white;
	box-sizing: border-box;
	flex: 1;
	min-height: 600px;
	min-width: 0;
}

/* 2. 타이틀 영역 */
.page-header {
	margin-bottom: 25px;
}

.page-header h1 {
	font-size: 22px;
	font-weight: bold;
	margin: 0;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.grid-container {
	display: grid;
	grid-template-columns: 6fr 4fr;
	gap: 30px;
}

/* 3. 섹션 타이틀 및 데이터 테이블 */
.section-title {
	font-size: 16px;
	font-weight: bold;
	margin-bottom: 15px;
	color: #333;
	margin-top: 30px;
	border-left: 4px solid #4e73df;
	padding-left: 10px;
}

.info-table {
	width: 100%;
	border-collapse: collapse;
	text-align: left;
	font-size: 14px;
	background: white;
	margin-bottom: 20px;
}

.info-table th, .info-table td {
	padding: 10px 12px;
	border: 1px solid #ccc;
}

.info-table th {
	background-color: #f8f9fa;
	width: 20%;
	color: #333;
	text-align: center;
	font-weight: bold;
}

.info-table td {
	background-color: #fff;
}

.info-table input[type="text"], .info-table input[type="password"],
.info-table select, .info-table input[type="number"] {
	padding: 6px 10px;
	border: 1px solid #ccc;
	border-radius: 3px;
	outline: none;
	font-size: 14px;
	box-sizing: border-box;
	font-family: inherit;
}
.info-table input:focus, .info-table select:focus {
	border-color: #4e73df;
}

.req {
	color: #e74a3b;
	margin-right: 4px;
	font-weight: bold;
}

/* 4. 버튼 스타일 통일 */
.btn-manage {
	background-color: #4e73df;
	color: white;
	border: none;
	padding: 6px 14px;
	border-radius: 3px;
	cursor: pointer;
	font-size: 13px;
	font-weight: bold;
	margin-left: 5px;
}
.btn-manage:hover { background-color: #2e59d9; }

.btn-gray {
	background-color: #a5a5a5;
	color: white;
	border: none;
	padding: 6px 14px;
	border-radius: 3px;
	cursor: pointer;
	font-size: 13px;
	font-weight: bold;
	margin-left: 5px;
}
.btn-gray:hover { background-color: #858796; }

.bottom-btns {
	text-align: center;
	margin-top: 50px;
	margin-bottom: 30px;
}

.btn-save {
	background-color: #4e73df;
	color: white;
	padding: 12px 40px;
	border: none;
	border-radius: 3px;
	font-size: 16px;
	font-weight: bold;
	cursor: pointer;
}
.btn-save:hover { background-color: #2e59d9; }

.btn-cancel {
	background-color: #a5a5a5;
	color: white;
	padding: 12px 40px;
	border: none;
	border-radius: 3px;
	font-size: 16px;
	font-weight: bold;
	cursor: pointer;
	margin-left: 10px;
}
.btn-cancel:hover { background-color: #858796; }

/* 5. 이미지 업로드 영역 */
.img-box-wrap {
	display: flex;
	gap: 40px;
	margin-top: 20px;
}

.img-box {
	border: 1px solid #ccc;
	padding: 20px;
	display: flex;
	align-items: center;
	gap: 20px;
	width: 450px;
	border-radius: 3px;
	background: #fafafa;
}

.img-placeholder {
	width: 150px;
	height: 100px;
	border: 1px dashed #bbb;
	display: flex;
	justify-content: center;
	align-items: center;
	background: #fff;
	color: #999;
	font-size: 13px;
	font-weight: bold;
}

.img-desc {
	font-size: 13px;
	color: #555;
	line-height: 1.6;
}

/* 6. 모달 팝업 스타일 */
.modal-bg {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.45);
	z-index: 1000;
}

.modal-content {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: white;
	padding: 30px;
	border-radius: 5px;
	width: 400px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
	box-sizing: border-box;
}

.modal-content h3 {
	margin: 0;
	font-size: 18px;
	margin-bottom: 20px;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 8px;
}

.item-list {
	border: 1px solid #ccc;
	max-height: 250px;
	overflow-y: auto;
	padding: 10px;
	margin-bottom: 20px;
	border-radius: 3px;
}

.item-row {
	display: flex;
	justify-content: space-between;
	padding: 10px 5px;
	border-bottom: 1px dashed #ddd;
	font-size: 14px;
}
.item-row:last-child { border-bottom: none; }

.item-row a {
	color: #4e73df;
	text-decoration: none;
	cursor: pointer;
	margin: 0 5px;
	font-weight: bold;
}

.add-btn {
	background-color: #a5a5a5;
	color: white;
	text-align: center;
	padding: 10px;
	cursor: pointer;
	font-size: 14px;
	font-weight: bold;
	margin-top: 10px;
	border-radius: 3px;
}
.add-btn:hover { background-color: #858796; }

.add-input-row {
	display: none;
	padding: 10px 5px;
	border-bottom: 1px dashed #ddd;
	font-size: 14px;
	justify-content: space-between;
	align-items: center;
}

.add-input-row input {
	width: 55%;
	padding: 6px;
	border: 1px solid #ccc;
	border-radius: 3px;
	outline: none;
}

.add-input-row a {
	cursor: pointer;
	text-decoration: none;
	font-size: 13px;
}

.add-input-row .save-link {
	color: #4e73df;
	font-weight: bold;
	margin-right: 5px;
}

.add-input-row .cancel-link {
	color: #999;
	font-weight: bold;
}

.modal-close-btn {
	width: 100%;
	padding: 12px;
	background: #4e73df;
	color: white;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-weight: bold;
	font-size: 15px;
}
.modal-close-btn:hover { background-color: #2e59d9; }
</style>
<script>
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const modalToOpen = urlParams.get('openModal');
        if (modalToOpen) openModal(modalToOpen);
    };
    function openModal(modalId) { document.getElementById(modalId).style.display = 'block'; }
    function closeModal(modalId) { document.getElementById(modalId).style.display = 'none'; history.replaceState({}, null, location.pathname); }
    function cancelEdit() { if(confirm("修正した内容をキャンセルしますか？")) location.href = location.pathname; }

    function manageItem(type, action, id, currentName) {
        let form = document.getElementById('manageForm');
        form.type.value = type; form.action.value = action; form.id.value = id;
        if (action === 'edit') {
            let newName = prompt('新しい名前を入力してください:', currentName);
            if (newName && newName.trim() !== '' && newName !== currentName) { form.name.value = newName.trim(); form.submit(); }
        } else if (action === 'delete') {
            if (confirm('[' + currentName + '] 項目を削除しますか？')) form.submit();
        }
    }
    function showAddRow(type) { document.getElementById(type + 'AddBtn').style.display = 'none'; document.getElementById(type + 'AddRow').style.display = 'flex'; }
    function hideAddRow(type) { document.getElementById(type + 'AddBtn').style.display = 'block'; document.getElementById(type + 'AddRow').style.display = 'none'; document.getElementById(type + 'NewName').value = ''; }
    function saveNewItem(type) {
        let newName = document.getElementById(type + 'NewName').value;
        if (newName.trim() === '') { alert('追加する名前を入力してください。'); return; }
        let form = document.getElementById('manageForm');
        form.type.value = type; form.action.value = 'add'; form.id.value = '0'; form.name.value = newName.trim(); form.submit();
    }
</script>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			
			<div class="page-header">
				<h1>ユーザー情報</h1>
			</div>

			<form action="<%=request.getContextPath()%>/employee/userInfo.do" method="post">
				<div class="grid-container">
					<div>
						<div class="section-title" style="margin-top: 0;">会社情報</div>
						<table class="info-table">
							<tr>
								<th style="width: 25%;"><span class="req">*</span>商号</th>
								<td><input type="text" name="companyName" value="${info.companyName}"></td>
								<th style="width: 25%;"><span class="req">*</span>代表者役職/代表者</th>
								<td>
									<input type="text" name="repTitle" value="${info.repTitle != null ? info.repTitle : '대표이사'}" style="width: 70px;"> 
									/ <input type="text" name="repName" value="${info.repName}" style="width: 100px;">
								</td>
							</tr>
							<tr>
								<th><span class="req">*</span>事業者番号</th>
								<td><input type="text" name="businessNumber" value="${info.businessNumber}"></td>
								<th>法人登録番号</th>
								<td><input type="text" name="corpNumber" value="${info.corpNumber}"></td>
							</tr>
							<tr>
								<th>設立日</th>
								<td><input type="text" name="establishmentDate" value="${info.establishmentDate}" placeholder="YYYY-MM-DD"></td>
								<th>ホームページ</th>
								<td><input type="text" name="website" value="${info.website}"></td>
							</tr>
							<tr>
								<th><span class="req">*</span>事業所住所</th>
								<td colspan="3">
									<div style="display: flex; align-items: center; gap: 5px;">
										<input type="text" name="zipCode" value="${info.zipCode}" style="width: 80px;">
										<button type="button" class="btn-gray" style="margin: 0;">郵便番号</button> 
										<input type="text" name="officeAddress" value="${info.officeAddress}" style="flex: 1;">
									</div>
								</td>
							</tr>
							<tr>
								<th><span class="req">*</span>電話番号</th>
								<td>
									<div style="display: flex; align-items: center; gap: 5px;">
										<select name="phone1" style="width: auto;">
											<option value="">代表(なし)</option>
											<option value="010" ${info.phone1 == '010' ? 'selected' : ''}>携帯電話(010)</option>
											<option value="050" ${info.phone1 == '050' ? 'selected' : ''}>インターネット(050)</option>
											<option value="0507" ${info.phone1 == '0507' ? 'selected' : ''}>インターネット(0507)</option>
											<option value="070" ${info.phone1 == '070' ? 'selected' : ''}>インターネット(070)</option>
											<option value="0303" ${info.phone1 == '0303' ? 'selected' : ''}>インターネット(0303)</option>
											<option value="0504" ${info.phone1 == '0504' ? 'selected' : ''}>インターネット(0504)</option>
											<option value="02" ${info.phone1 == '02' ? 'selected' : ''}>ソウル(02)</option>
											<option value="051" ${info.phone1 == '051' ? 'selected' : ''}>釜山(051)</option>
											<option value="053" ${info.phone1 == '053' ? 'selected' : ''}>大邱(053)</option>
											<option value="032" ${info.phone1 == '032' ? 'selected' : ''}>仁川(032)</option>
											<option value="062" ${info.phone1 == '062' ? 'selected' : ''}>光州(062)</option>
											<option value="042" ${info.phone1 == '042' ? 'selected' : ''}>大田(042)</option>
											<option value="052" ${info.phone1 == '052' ? 'selected' : ''}>蔚山(052)</option>
											<option value="044" ${info.phone1 == '044' ? 'selected' : ''}>世宗(044)</option>
											<option value="031" ${info.phone1 == '031' ? 'selected' : ''}>京畿(031)</option>
											<option value="033" ${info.phone1 == '033' ? 'selected' : ''}>江原(033)</option>
											<option value="043" ${info.phone1 == '043' ? 'selected' : ''}>忠北(043)</option>
											<option value="041" ${info.phone1 == '041' ? 'selected' : ''}>忠南(041)</option>
											<option value="063" ${info.phone1 == '063' ? 'selected' : ''}>全北(063)</option>
											<option value="061" ${info.phone1 == '061' ? 'selected' : ''}>全南(061)</option>
											<option value="054" ${info.phone1 == '054' ? 'selected' : ''}>慶北(054)</option>
											<option value="055" ${info.phone1 == '055' ? 'selected' : ''}>慶南(055)</option>
											<option value="064" ${info.phone1 == '064' ? 'selected' : ''}>済州(064)</option>
										</select> - 
										<input type="text" name="phone2" value="${info.phone2}" style="width: 50px;"> - 
										<input type="text" name="phone3" value="${info.phone3}" style="width: 50px;">
									</div>
								</td>
								<th>FAX番号</th>
								<td>
									<div style="display: flex; align-items: center; gap: 5px;">
										<select name="fax1" style="width: auto;">
											<option value="">代表(なし)</option>
											<option value="010" ${info.fax1 == '010' ? 'selected' : ''}>携帯電話(010)</option>
											<option value="050" ${info.fax1 == '050' ? 'selected' : ''}>インターネット(050)</option>
											<option value="0507" ${info.fax1 == '0507' ? 'selected' : ''}>インターネット(0507)</option>
											<option value="070" ${info.fax1 == '070' ? 'selected' : ''}>インターネット(070)</option>
											<option value="0303" ${info.fax1 == '0303' ? 'selected' : ''}>インターネット(0303)</option>
											<option value="0504" ${info.fax1 == '0504' ? 'selected' : ''}>インターネット(0504)</option>
											<option value="02" ${info.fax1 == '02' ? 'selected' : ''}>ソウル(02)</option>
											<option value="051" ${info.fax1 == '051' ? 'selected' : ''}>釜山(051)</option>
											<option value="053" ${info.fax1 == '053' ? 'selected' : ''}>大邱(053)</option>
											<option value="032" ${info.fax1 == '032' ? 'selected' : ''}>仁川(032)</option>
											<option value="062" ${info.fax1 == '062' ? 'selected' : ''}>光州(062)</option>
											<option value="042" ${info.fax1 == '042' ? 'selected' : ''}>大田(042)</option>
											<option value="052" ${info.fax1 == '052' ? 'selected' : ''}>蔚山(052)</option>
											<option value="044" ${info.fax1 == '044' ? 'selected' : ''}>世宗(044)</option>
											<option value="031" ${info.fax1 == '031' ? 'selected' : ''}>京畿(031)</option>
											<option value="033" ${info.fax1 == '033' ? 'selected' : ''}>江原(033)</option>
											<option value="043" ${info.fax1 == '043' ? 'selected' : ''}>忠北(043)</option>
											<option value="041" ${info.fax1 == '041' ? 'selected' : ''}>忠南(041)</option>
											<option value="063" ${info.fax1 == '063' ? 'selected' : ''}>全北(063)</option>
											<option value="061" ${info.fax1 == '061' ? 'selected' : ''}>全南(061)</option>
											<option value="054" ${info.fax1 == '054' ? 'selected' : ''}>慶北(054)</option>
											<option value="055" ${info.fax1 == '055' ? 'selected' : ''}>慶南(055)</option>
											<option value="064" ${info.fax1 == '064' ? 'selected' : ''}>済州(064)</option>
										</select> - 
										<input type="text" name="fax2" value="${info.fax2}" style="width: 50px;"> - 
										<input type="text" name="fax3" value="${info.fax3}" style="width: 50px;">
									</div>
								</td>
							</tr>
							<tr>
								<th>業態</th>
								<td><input type="text" name="bizType" value="${info.bizType}"></td>
								<th>種目</th>
								<td><input type="text" name="bizItem" value="${info.bizItem}"></td>
							</tr>
						</table>
					</div>

					<div>
						<div class="section-title" style="margin-top: 0;">担当者情報</div>
						<table class="info-table">
							<tr>
								<th style="width: 30%;"><span class="req">*</span>氏名</th>
								<td><input type="text" name="contactName" value="${info.contactName}" style="width: 100%;"></td>
							</tr>
							<tr>
								<th>部署</th>
								<td>
									<div style="display: flex; align-items: center;">
										<select name="departmentId" style="flex: 1;">
											<option value="">選択</option>
											<c:forEach var="dept" items="${deptList}">
												<option value="${dept.id}" ${info.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
											</c:forEach>
										</select>
										<button type="button" class="btn-manage" onclick="openModal('deptModal')">管理</button>
									</div>
								</td>
							</tr>
							<tr>
								<th>役職</th>
								<td>
									<div style="display: flex; align-items: center;">
										<select name="positionId" style="flex: 1;">
											<option value="">選択</option>
											<c:forEach var="pos" items="${posList}">
												<option value="${pos.id}" ${info.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
											</c:forEach>
										</select>
										<button type="button" class="btn-manage" onclick="openModal('posModal')">管理</button>
									</div>
								</td>
							</tr>
							<tr>
								<th>電話番号</th>
								<td>
									<div style="display: flex; align-items: center; gap: 5px;">
										<select name="cPhone1" style="width: auto;">
											<option value="">選択</option>
											<option value="010" ${info.cPhone1 == '010' ? 'selected' : ''}>携帯電話(010)</option>
											<option value="050" ${info.cPhone1 == '050' ? 'selected' : ''}>インターネット(050)</option>
											<option value="0507" ${info.cPhone1 == '0507' ? 'selected' : ''}>インターネット(0507)</option>
											<option value="070" ${info.cPhone1 == '070' ? 'selected' : ''}>インターネット(070)</option>
											<option value="0303" ${info.cPhone1 == '0303' ? 'selected' : ''}>インターネット(0303)</option>
											<option value="0504" ${info.cPhone1 == '0504' ? 'selected' : ''}>インターネット(0504)</option>
											<option value="02" ${info.cPhone1 == '02' ? 'selected' : ''}>ソウル(02)</option>
											<option value="051" ${info.cPhone1 == '051' ? 'selected' : ''}>釜山(051)</option>
											<option value="053" ${info.cPhone1 == '053' ? 'selected' : ''}>大邱(053)</option>
											<option value="032" ${info.cPhone1 == '032' ? 'selected' : ''}>仁川(032)</option>
											<option value="062" ${info.cPhone1 == '062' ? 'selected' : ''}>光州(062)</option>
											<option value="042" ${info.cPhone1 == '042' ? 'selected' : ''}>大田(042)</option>
											<option value="052" ${info.cPhone1 == '052' ? 'selected' : ''}>蔚山(052)</option>
											<option value="044" ${info.cPhone1 == '044' ? 'selected' : ''}>世宗(044)</option>
											<option value="031" ${info.cPhone1 == '031' ? 'selected' : ''}>京畿(031)</option>
											<option value="033" ${info.cPhone1 == '033' ? 'selected' : ''}>江原(033)</option>
											<option value="043" ${info.cPhone1 == '043' ? 'selected' : ''}>忠北(043)</option>
											<option value="041" ${info.cPhone1 == '041' ? 'selected' : ''}>忠南(041)</option>
											<option value="063" ${info.cPhone1 == '063' ? 'selected' : ''}>全北(063)</option>
											<option value="061" ${info.cPhone1 == '061' ? 'selected' : ''}>全南(061)</option>
											<option value="054" ${info.cPhone1 == '054' ? 'selected' : ''}>慶北(054)</option>
											<option value="055" ${info.cPhone1 == '055' ? 'selected' : ''}>慶南(055)</option>
											<option value="064" ${info.cPhone1 == '064' ? 'selected' : ''}>済州(064)</option>
										</select> - 
										<input type="text" name="cPhone2" value="${info.cPhone2}" style="width: 50px;"> - 
										<input type="text" name="cPhone3" value="${info.cPhone3}" style="width: 50px;">
									</div>
								</td>
							</tr>
							<tr>
								<th>携帯電話番号</th>
								<td>
									<div style="display: flex; align-items: center; gap: 5px;">
										<select name="mobile1" style="width: auto;">
											<option value="">選択</option>
											<option value="010" ${info.mobile1 == '010' ? 'selected' : ''}>010</option>
											<option value="011" ${info.mobile1 == '011' ? 'selected' : ''}>011</option>
											<option value="016" ${info.mobile1 == '016' ? 'selected' : ''}>016</option>
											<option value="017" ${info.mobile1 == '017' ? 'selected' : ''}>017</option>
											<option value="018" ${info.mobile1 == '018' ? 'selected' : ''}>018</option>
											<option value="019" ${info.mobile1 == '019' ? 'selected' : ''}>019</option>
										</select> - 
										<input type="text" name="mobile2" value="${info.mobile2}" style="width: 50px;"> - 
										<input type="text" name="mobile3" value="${info.mobile3}" style="width: 50px;">
									</div>
								</td>
							</tr>
							<tr>
								<th>メールアドレス</th>
								<td><input type="text" name="email" value="${info.email}" style="width: 100%;"></td>
							</tr>
						</table>
					</div>
				</div>

				<!-- 給与支給情報レイアウト -->
				<div class="section-title">給与支給情報</div>
				<table class="info-table">
					<tr>
						<th style="width: 15%;">給与算定期間</th>
						<td style="width: 35%;">
							<div style="display: flex; align-items: center; gap: 5px;">
								<select name="calc1MonthType">
									<option value="">選択</option>
									<option value="P" ${account != null && account.calc1MonthType == 'P' ? 'selected' : ''}>前月</option>
									<option value="C" ${account == null || account.calc1MonthType == 'C' ? 'selected' : ''}>当月</option>
								</select> 
								<select name="salaryCalc1">
									<option value="">選択</option>
									<c:forEach var="day" begin="1" end="31">
										<option value="${day}" ${account.salaryCalculation1 == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}日</option>
									</c:forEach>
									<option value="0" ${account.salaryCalculation1 != null && account.salaryCalculation1 == 0 ? 'selected' : ''}>末日</option>
								</select> 
								<span style="margin: 0 5px;">~</span> 
								<select name="calc2MonthType">
									<option value="">選択</option>
									<option value="P" ${account != null && account.calc2MonthType == 'P' ? 'selected' : ''}>前月</option>
									<option value="C" ${account == null || account.calc2MonthType == 'C' ? 'selected' : ''}>当月</option>
								</select> 
								<select name="salaryCalc2">
									<option value="">選択</option>
									<c:forEach var="day" begin="1" end="31">
										<option value="${day}" ${account.salaryCalculation2 == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}日</option>
									</c:forEach>
									<option value="0" ${account.salaryCalculation2 != null && account.salaryCalculation2 == 0 ? 'selected' : ''}>末日</option>
								</select>
							</div>
						</td>

						<th style="width: 15%;">給与支給日</th>
						<td style="width: 35%;" colspan="3">
							<div style="display: flex; align-items: center; gap: 5px;">
								<select name="paymentMonthType">
									<option value="C" ${account == null || account.paymentMonthType == 'C' ? 'selected' : ''}>当月</option>
									<option value="N" ${account != null && account.paymentMonthType == 'N' ? 'selected' : ''}>翌月</option>
								</select> 
								<select name="salaryPaymentDate">
									<option value="">選択</option>
									<c:forEach var="day" begin="1" end="31">
										<option value="${day}" ${account.salaryPaymentDate == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}日</option>
									</c:forEach>
									<option value="0" ${account.salaryPaymentDate != null && account.salaryPaymentDate == 0 ? 'selected' : ''}>末日</option>
								</select>
							</div>
						</td>
					</tr>
					<tr>
						<th>金融機関</th>
						<td>
							<select name="bankName">
								<option value="">選択してください</option>
								<option value="국민은행" ${account.bankName == '국민은행' ? 'selected' : ''}>国民銀行</option>
								<option value="기업은행" ${account.bankName == '기업은행' ? 'selected' : ''}>企業銀行</option>
								<option value="농협은행" ${account.bankName == '농협은행' ? 'selected' : ''}>農協銀行</option>
								<option value="신한은행" ${account.bankName == '신한은행' ? 'selected' : ''}>新韓銀行</option>
								<option value="우리은행" ${account.bankName == '우리은행' ? 'selected' : ''}>ウリィ銀行</option>
								<option value="하나은행" ${account.bankName == '하나은행' ? 'selected' : ''}>ハナ銀行</option>
							</select>
						</td>
						<th>口座番号</th>
						<td><input type="text" name="accountNumber" value="${account.accountNumber}" style="width: 100%;"></td>
						<th>口座名義人</th>
						<td><input type="text" name="depositStocks" value="${account.depositStocks}"></td>
					</tr>
					<tr>
						<th>給与振込バンキング</th>
						<td colspan="5">
							<p style="color: #e74a3b; font-size: 13px; margin: 0 0 10px 0; font-weight: bold;">給与振込サービスは、KB国民銀行の企業バンキングを通じて行われています。</p>
							<div style="background: #fdfdfd; padding: 15px; border: 1px solid #ddd; border-radius: 3px; display: inline-block;">
								企業バンキング ID <input type="text" name="kbId" value="${info.kbId}" style="margin: 0 10px 0 5px; width: 120px;">
								Password <input type="password" name="kbPw" value="${info.kbPw}" style="margin: 0 10px 0 5px; width: 150px;">
								<button type="button" class="btn-manage" style="margin-left: 10px;">すぐにERP連携</button>
							</div>
						</td>
					</tr>
				</table>

				<div class="img-box-wrap">
					<div style="flex: 1;">
						<div class="section-title" style="margin-top: 0;">会社ロゴ</div>
						<div class="img-box" style="width: 100%;">
							<div class="img-placeholder">NO IMAGE</div>
							<div class="img-desc">
								ロゴは横幅150pxのサムネイルで生成されます。<br>透明なPNG画像の使用を推奨します。<br><br>
								<button type="button" class="btn-manage" style="margin-left: 0;">登録</button>
								<button type="button" class="btn-gray">削除</button>
							</div>
						</div>
					</div>
					<div style="flex: 1;">
						<div class="section-title" style="margin-top: 0;">会社印</div>
						<div class="img-box" style="width: 100%;">
							<div class="img-placeholder">NO IMAGE</div>
							<div class="img-desc">
								横幅150pxのサムネイル、PNGファイルを推奨します。<br>無料印鑑提供：stamp.yesform.com<br><br>
								<button type="button" class="btn-manage" style="margin-left: 0;">登録</button>
								<button type="button" class="btn-gray">削除</button>
							</div>
						</div>
					</div>
				</div>

				<div class="bottom-btns">
					<button type="submit" class="btn-save">保存</button>
					<button type="button" class="btn-cancel" onclick="cancelEdit()">キャンセル</button>
				</div>
			</form>
		</div>
	</div>

	<!-- 모달 제어용 폼 -->
	<form id="manageForm" method="post" action="<%=request.getContextPath()%>/employee/manageDeptPos.do" style="display: none;">
		<input type="hidden" name="type" id="mType"> 
		<input type="hidden" name="action" id="mAction"> 
		<input type="hidden" name="id" id="mId"> 
		<input type="hidden" name="name" id="mName">
	</form>

	<!-- 부서 모달 -->
	<div id="deptModal" class="modal-bg">
		<div class="modal-content">
			<h3>部署設定</h3>
			<div class="item-list">
				<c:forEach var="dept" items="${deptList}">
					<div class="item-row">
						<span style="font-weight: bold; color: #555;">${dept.name}</span>
						<span>
							<a onclick="manageItem('dept', 'edit', ${dept.id}, '${dept.name}')">修正</a> | 
							<a onclick="manageItem('dept', 'delete', ${dept.id}, '${dept.name}')" style="color: #e74a3b;">削除</a>
						</span>
					</div>
				</c:forEach>
				<div class="add-input-row" id="deptAddRow">
					<input type="text" id="deptNewName" placeholder="新しい部署名">
					<span>
						<a class="save-link" onclick="saveNewItem('dept')">保存</a> | 
						<a class="cancel-link" onclick="hideAddRow('dept')">キャンセル</a>
					</span>
				</div>
				<div class="add-btn" id="deptAddBtn" onclick="showAddRow('dept')">+ 追加する</div>
			</div>
			<button class="modal-close-btn" onclick="closeModal('deptModal')">閉じる</button>
		</div>
	</div>

	<!-- 직급 모달 -->
	<div id="posModal" class="modal-bg">
		<div class="modal-content">
			<h3>役職設定</h3>
			<div class="item-list">
				<c:forEach var="pos" items="${posList}">
					<div class="item-row">
						<span style="font-weight: bold; color: #555;">${pos.name}</span>
						<span>
							<a onclick="manageItem('pos', 'edit', ${pos.id}, '${pos.name}')">修正</a> | 
							<a onclick="manageItem('pos', 'delete', ${pos.id}, '${pos.name}')" style="color: #e74a3b;">削除</a>
						</span>
					</div>
				</c:forEach>
				<div class="add-input-row" id="posAddRow">
					<input type="text" id="posNewName" placeholder="新しい役職名">
					<span>
						<a class="save-link" onclick="saveNewItem('pos')">保存</a> | 
						<a class="cancel-link" onclick="hideAddRow('pos')">キャンセル</a>
					</span>
				</div>
				<div class="add-btn" id="posAddBtn" onclick="showAddRow('pos')">+ 追加する</div>
			</div>
			<button class="modal-close-btn" onclick="closeModal('posModal')">閉じる</button>
		</div>
	</div>
</body>
</html>