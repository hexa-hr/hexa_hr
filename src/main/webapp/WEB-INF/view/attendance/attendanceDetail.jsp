<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>勤怠詳細照会</title>

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

.container {
    padding: 30px 40px; 
    background-color: white; 
    box-sizing: border-box;
    min-height: 600px;
}

/* 2. 타이틀 영역 */
.page-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.page-title-text h1 {
    font-size: 22px; 
    font-weight: bold;
    margin: 0;
    color: #333;
}

/* 3. 탭 메뉴 영역 */
.tab-menu {
    display: flex;
    gap: 5px;
    margin-bottom: 20px;
    border-bottom: 2px solid #4e73df; 
    padding-bottom: 10px;
}

.tab-btn {
    padding: 10px 30px;
    font-size: 14px;
    font-weight: bold;
    border: none;
    cursor: pointer;
    border-radius: 3px 3px 0 0;
}

/* 액티브 탭 (메인 파란색) */
.tab-active {
    background-color: #4e73df;
    color: white;
}
/* 비액티브 탭 (서브 회색) */
.tab-inactive {
    background-color: #a5a5a5;
    color: white;
}
.tab-inactive:hover {
    background-color: #858796;
}

/* 4. 레이아웃 패널 분할 */
.content-wrap {
    display: flex;
    gap: 30px; /* 패널 간격 여유롭게 조정 */
    align-items: flex-start;
}

/* 5. 좌측 필터 패널 (설정 폼과 동일한 느낌으로 변경) */
.filter-panel {
    flex: 0 0 320px; /* 너비 약간 확대 */
    background: #f4f4f4; /* 폼 영역 배경색 톤 통일 */
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 3px;
    box-sizing: border-box;
}

.filter-table {
    width: 100%;
    border-collapse: collapse;
}

.filter-table td {
    padding: 8px 5px;
    border-bottom: 1px dashed #ccc; /* 선을 좀 더 연하게 */
    vertical-align: middle;
    font-size: 14px;
    white-space: nowrap;
}
.filter-table tr:last-child td {
    border-bottom: none;
}

.filter-table input[type="text"], .filter-table input[type="date"], .filter-table select {
    width: 100%;
    padding: 5px;
    box-sizing: border-box;
    border: 1px solid #ccc;
    border-radius: 3px;
    font-size: 13px;
}

/* 버튼 래퍼 */
.btn-wrap {
    display: flex;
    gap: 8px;
    margin-top: 20px;
    justify-content: center;
}

.btn-search {
    background-color: #4e73df; /* 검색 버튼 파란색 통일 */
    color: #fff;
    border: none;
    padding: 8px 20px;
    cursor: pointer;
    border-radius: 3px;
    font-weight: bold;
    font-size: 14px;
}
.btn-search:hover { background-color: #2e59d9; }

.btn-all {
    background-color: #a5a5a5; /* 전체보기 버튼 회색 통일 */
    color: #fff;
    border: none;
    padding: 8px 20px;
    cursor: pointer;
    border-radius: 3px;
    font-weight: bold;
    font-size: 14px;
}
.btn-all:hover { background-color: #858796; }

/* 6. 우측 결과 패널 (데이터 테이블 디자인 공통 적용) */
.result-panel {
    flex: 1;
    overflow-x: auto; /* 내용이 넘칠 경우 자체 스크롤 생성 */
}

.result-table {
    width: 100%;
    border-collapse: collapse;
    text-align: center;
}

.result-table th, .result-table td {
    border: 1px solid #ccc;
    padding: 10px;
    font-size: 14px;
    white-space: nowrap; /* 셀 내용 꺾임 방지 */
}

.result-table th {
    background-color: #f8f9fa; /* 헤더 배경색 공통 적용 */
    color: #333;
    font-weight: bold;
}

.result-table tbody tr:hover {
    background-color: #f1f5f9; /* 행 호버 색상 통일 */
}
</style>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="container">
		<!-- 上部タイトルおよびタブ領域 -->
		<div class="page-header">

			<div class="page-title-text">
				<h1>勤怠照会</h1>

			</div>
		</div>

		<div class="tab-menu">
			<button class="tab-btn tab-inactive"
				onclick="location.href='${pageContext.request.contextPath}/attendance/monthly.do'">月別照会</button>
			<button class="tab-btn tab-active"
				onclick="location.href='${pageContext.request.contextPath}/attendance/detail.do'">詳細照会</button>
		</div>

		<div class="content-wrap">
			<!-- 左側条件検索パネル -->
			<div class="filter-panel">
				<form id="searchForm">
					<table class="filter-table">
						<tr>
							<td style="width: 25px;"><input type="checkbox"
								id="chkInputDate"></td>
							<td style="width: 75px;"><label for="chkInputDate">入力日</label></td>
							<td><input type="date" id="inputDate"></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkAttPeriod"></td>
							<td><label for="chkAttPeriod">勤怠期間</label></td>
							<td style="display: flex; gap: 5px; align-items: center;"><input
								type="date" id="startDate"> ~ <input type="date"
								id="endDate"></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkDept"></td>
							<td><label for="chkDept">部署</label></td>
							<td><select id="deptId">
									<option value="">選択してください。</option>
									<c:forEach var="dept" items="${deptList}">
										<option value="${dept.id}">${dept.name}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkName"></td>
							<td><label for="chkName">姓名</label></td>
							<td><input type="text" id="empName"
								placeholder="姓名を入力してください。"></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkAttGroup"></td>
							<td><label for="chkAttGroup">勤怠グループ</label></td>
							<td><select id="attGroupId">
									<option value="">選択してください。</option>
									<c:forEach var="group" items="${attGroupList}">
										<option value="${group.id}">${group.name}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkAttType"></td>
							<td><label for="chkAttType">勤怠項目</label></td>
							<td><select id="attTypeId">
									<option value="">選択してください。</option>
									<c:forEach var="type" items="${attTypeList}">
										<option value="${type.id}">${type.name}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkVacType"></td>
							<td><label for="chkVacType">休暇項目</label></td>
							<td><select id="vacTypeId">
									<option value="">選択してください。</option>
									<c:forEach var="vac" items="${vacTypeList}">
										<option value="${vac.id}">${vac.name}</option>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<td><input type="checkbox" id="chkSummary"></td>
							<td><label for="chkSummary">摘要</label></td>
							<td><input type="text" id="summary"></td>
						</tr>
					</table>

					<div class="btn-wrap">
						<button type="button" class="btn-search" onclick="searchData()">検索</button>
						<button type="button" class="btn-all" onclick="searchAll()">全体表示</button>
					</div>
				</form>
			</div>

			<!-- 右側検索結果パネル -->
			<div class="result-panel">
				<table class="result-table">
					<thead>
						<tr>
							<th style="color: #5c7cba;">入力日</th>
							<th style="color: #5c7cba;">区分</th>
							<th style="color: #5c7cba;">姓名</th>
							<th style="color: #5c7cba;">部署</th>
							<th style="color: #5c7cba;">職位</th>
							<th>勤怠項目</th>
							<th>勤怠期間</th>
							<th>勤怠日数</th>
							<th>金額</th>
							<th>摘要</th>
						</tr>
					</thead>
					<tbody id="resultBody">
						<tr>
							<td colspan="10" style="padding: 30px;">データを読み込み中です···</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<script>
    // 1. ページロード時実行（月別照会で社員クリックにより遷移したか判別）
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const targetName = urlParams.get('targetName'); // 渡された社員名を抽出

        if (targetName) {
            // 渡された社員名があれば該当社員情報をセットし直ちに検索
            document.getElementById('chkName').checked = true;
            document.getElementById('empName').value = targetName;
            searchData();
        } else {
            // 通常アクセスの場合は全体検索
            searchAll();
        }
    };

    // 2. 「全体表示」クリック時、全フィルター初期化後に検索
    function searchAll() {
        document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
        document.getElementById('searchForm').reset();
        executeSearch();
    }

    // 3. 「検索」クリック時
    function searchData() {
        executeSearch();
    }

    // 4. サーバー通信 (AJAX) - チェックされた項目のみサーバーへ送信
    function executeSearch() {
        const params = new URLSearchParams();
        
        // チェックボックスが選択された項目のinput値のみパラメータに追加
        if (document.getElementById('chkInputDate').checked) {
            params.append('chkInputDate', 'true');
            params.append('inputDate', document.getElementById('inputDate').value);
        }
        if (document.getElementById('chkAttPeriod').checked) {
            params.append('chkAttPeriod', 'true');
            params.append('startDate', document.getElementById('startDate').value);
            params.append('endDate', document.getElementById('endDate').value);
        }
        if (document.getElementById('chkDept').checked) {
            params.append('chkDept', 'true');
            params.append('deptId', document.getElementById('deptId').value);
        }
        if (document.getElementById('chkName').checked) {
            params.append('chkName', 'true');
            params.append('empName', document.getElementById('empName').value);
        }
        if (document.getElementById('chkAttGroup').checked) {
            params.append('chkAttGroup', 'true');
            params.append('attGroupId', document.getElementById('attGroupId').value);
        }
        if (document.getElementById('chkAttType').checked) {
            params.append('chkAttType', 'true');
            params.append('attTypeId', document.getElementById('attTypeId').value);
        }
        if (document.getElementById('chkVacType').checked) {
            params.append('chkVacType', 'true');
            params.append('vacTypeId', document.getElementById('vacTypeId').value);
        }
        if (document.getElementById('chkSummary').checked) {
            params.append('chkSummary', 'true');
            params.append('summary', document.getElementById('summary').value);
        }

        const tbody = document.getElementById("resultBody");
        tbody.innerHTML = '<tr><td colspan="10" style="padding: 20px;">検索中です...</td></tr>';

        fetch("${pageContext.request.contextPath}/attendance/detail.do?" + params.toString(), {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        })
        .then(res => res.json())
        .then(data => {
            tbody.innerHTML = "";
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="10" style="padding: 30px;">検索された勤怠履歴がありません。</td></tr>';
                return;
            }

            data.forEach(item => {
            	let displaySummary = (item.summary === null || item.summary === 'null') ? '' : item.summary;

                let row = `<tr>
                    <td style="color:#5c7cba;">\${item.inputDate}</td>
                    <td style="color:#5c7cba;">\${item.empType}</td>
                    <td style="color:#5c7cba;">\${item.empName}</td>
                    <td style="color:#5c7cba;">\${item.deptName}</td>
                    <td style="color:#5c7cba;">\${item.positionName}</td>
                    <td>\${item.attTypeName}</td>
                    <td>\${item.attPeriod}</td>
                    <td>\${item.attDays}(d)</td>
                    <td>\${item.amount}</td>
                    <td>\${displaySummary}</td> 
                </tr>`;
                tbody.innerHTML += row;
            });
        })
        .catch(err => {
            tbody.innerHTML = '<tr><td colspan="10" style="color:red;">検索中にサーバーエラーが発生しました。</td></tr>';
        });
    }
</script>

</body>
</html>