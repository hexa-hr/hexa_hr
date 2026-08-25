<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<title>日雇い勤務照会 - 詳細照会</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	font-family: 'Malgun Gothic', dotum, sans-serif;
	margin: 0;
}

/* 上部タイトルおよびタブデザイン */
.page-header {
	margin-bottom: 20px;
}

.title-area {
	display: flex;
	align-items: center;
	gap: 15px;
	margin-bottom: 15px;
}

.title-area h2 {
	margin: 0;
	font-size: 24px;
	color: #333;
}

.title-area p {
	margin: 0;
	font-size: 13px;
	color: #777;
}

.divider {
	border: 0;
	border-top: 1px solid #ddd;
	margin-bottom: 20px;
}

.tab-group {
	display: flex;
	gap: 5px;
	margin-bottom: 20px;
}

.tab-btn {
	padding: 12px 35px;
	font-size: 15px;
	font-weight: bold;
	color: white;
	border: none;
	cursor: pointer;
	border-radius: 3px;
}

.tab-active {
	background-color: #5a9b9c;
}

.tab-inactive {
	background-color: #a6a6a6;
}

/* 全体レイアウト (左側検索窓、右側リスト) */
.container {
	display: flex;
	gap: 20px;
	padding: 20px;
	font-family: 'Malgun Gothic', sans-serif;
}

.search-box {
	width: 320px;
	border: 1px solid #ddd;
	padding: 20px;
	background: #f9f9f9;
	height: fit-content;
}

.search-row {
	margin-bottom: 20px;
}

.search-row input[type="text"], .search-row input[type="date"],
	.search-row select {
	width: 100%;
	margin-top: 8px;
	padding: 5px;
	box-sizing: border-box;
}

.search-row label {
	font-weight: bold;
	font-size: 14px;
}

.btn-group {
	display: flex;
	gap: 10px;
	margin-top: 30px;
}

.btn-search {
	background: #d35400;
	color: white;
	border: none;
	padding: 10px;
	cursor: pointer;
	flex: 1;
	border-radius: 3px;
	font-weight: bold;
}

.btn-all {
	background: #95a5a6;
	color: white;
	border: none;
	padding: 10px;
	cursor: pointer;
	flex: 1;
	border-radius: 3px;
	font-weight: bold;
}

/* 右側リスト(テーブル)デザイン */
.table-container {
	flex: 1;
	overflow-x: auto;
}

table {
	width: 100%;
	border-collapse: collapse;
	font-size: 13px;
	text-align: center;
}

th, td {
	border: 1px solid #ddd;
	padding: 10px;
}

th {
	background-color: #f0f4f8;
	color: #333;
	font-weight: bold;
}

.text-blue {
	color: #2980b9;
}
</style>
</head>

<jsp:include page="/WEB-INF/view/include/header.jsp" />
<jsp:include page="/WEB-INF/view/include/nav.jsp" />

<body>

	<!-- 上部タイトル領域 -->
	<div class="page-header">
		<div class="title-area">

			<div>
				<h2>日雇い勤務照会</h2>

			</div>
		</div>
		<hr class="divider">
	</div>

	<!-- タブボタン領域 -->
	<div class="tab-group">
		<button type="button" class="tab-btn tab-inactive"
			onclick="location.href='monthly.do'">月別照会</button>
		<button type="button" class="tab-btn tab-active"
			onclick="location.href='detail.do'">詳細照会</button>
	</div>

	<div class="container">
		<!-- ==================== [左側] 検索フォーム領域 ==================== -->
		<div class="search-box">
			<form id="searchForm" action="detail.do" method="get">

				<div class="search-row">
					<input type="checkbox" id="chkDate"
						onchange="toggleInput('chkDate', 'startDate', 'endDate')">
					<label for="chkDate">勤務日付</label>
					<div style="display: flex; gap: 5px; align-items: center;">
						<input type="date" id="startDate" name="startDate" disabled>
						~ <input type="date" id="endDate" name="endDate" disabled>
					</div>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkName"
						onchange="toggleInput('chkName', 'empName')"> <label
						for="chkName">姓名</label> <input type="text" id="empName"
						name="empName" placeholder="姓名を入力してください。" disabled>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkDept"
						onchange="toggleInput('chkDept', 'deptId')"> <label
						for="chkDept">部署</label>
					<!-- [修正された部分] ハードコーディングを消してDBデータ連動 -->
					<select id="deptId" name="deptId" disabled>
						<option value="">選択してください。</option>
						<c:forEach var="dept" items="${deptList}">
							<option value="${dept.id}">${dept.name}</option>
						</c:forEach>
					</select>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkProj"
						onchange="toggleInput('chkProj', 'projectId')"> <label
						for="chkProj">現場/プロジェクト</label> <select id="projectId"
						name="projectId" disabled>
						<option value="">選択してください。</option>
						<c:forEach var="proj" items="${projectList}">
							<!-- VOクラスの変数名に合わせてprojectNameで維持 -->
							<option value="${proj.fieldOrProjectId}">${proj.projectName}</option>
						</c:forEach>
					</select>
				</div>

				<div class="btn-group">
					<button type="submit" class="btn-search">検索</button>
					<button type="button" class="btn-all" onclick="viewAll()">全体表示</button>
				</div>
			</form>
		</div>

		<!-- ==================== [右側] 詳細データテーブル ==================== -->
		<div class="table-container">
			<table>
				<thead>
					<tr>
						<th class="text-blue">勤務日付</th>
						<th class="text-blue">社員番号</th>
						<th class="text-blue">姓名</th>
						<th class="text-blue">部署</th>
						<th class="text-blue">現場/プロジェクト</th>
						<th>日当</th>
						<th>支給率</th>
						<th>所得税</th>
						<th>住民税</th>
						<th>実支給額</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty detailList}">
							<tr>
								<td colspan="10" style="padding: 30px;">照会された詳細勤務記録がありません。</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach var="row" items="${detailList}">
								<tr>
									<td>${row.workDate}</td>
									<td>${row.empNo}</td>
									<td>${row.empName}</td>
									<td>${row.deptName}</td>
									<td>${row.projName}</td>
									<td><fmt:formatNumber value="${row.dailyWage}" /></td>
									<td>${row.paymentRate}</td>
									<td class="text-blue"><fmt:formatNumber
											value="${row.incomeTax}" /></td>
									<td class="text-blue"><fmt:formatNumber
											value="${row.localTax}" /></td>
									<td><fmt:formatNumber value="${row.actualPayment}" /></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
	</div>

	<script>
        // チェックボックスクリック時に入力窓の活性化/非活性化処理
        function toggleInput(chkId, ...inputIds) {
            const isChecked = document.getElementById(chkId).checked;
            
            inputIds.forEach(id => {
                const element = document.getElementById(id);
                if (element) {
                    element.disabled = !isChecked;
                    if (!isChecked) {
                        element.value = '';
                    }
                }
            });
        }

        // ページロード時にURLを確認して社員名をセッティング
        window.onload = function() {
            
            const urlParams = new URLSearchParams(window.location.search);
            
            if (urlParams.has('empName')) {
                
                document.getElementById('chkName').checked = true;
                
                
                const empNameInput = document.getElementById('empName');
                empNameInput.disabled = false;
                empNameInput.value = urlParams.get('empName');
            }
        };

        // 全体表示ボタンクリック時のロジック
        function viewAll() {
            window.location.href = "detail.do"; 
        }
    </script>
</body>
</html>