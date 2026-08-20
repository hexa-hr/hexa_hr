<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<title>일용직 근무 조회 - 상세 조회</title>
<style>
/* 상단 제목 및 탭 디자인 */
.page-header {
	margin-bottom: 20px;
	font-family: 'Malgun Gothic', sans-serif;
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

/* 전체 레이아웃 (좌측 검색창, 우측 리스트) */
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

/* 우측 리스트(테이블) 디자인 */
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
<body>

	<!-- 상단 제목 영역 -->
	<div class="page-header">
		<div class="title-area">

			<div>
				<h2>일용직 근무 조회</h2>

			</div>
		</div>
		<hr class="divider">
	</div>

	<!-- 탭 버튼 영역 -->
	<div class="tab-group">
		<button type="button" class="tab-btn tab-inactive"
			onclick="location.href='monthly.do'">월별 조회</button>
		<button type="button" class="tab-btn tab-active"
			onclick="location.href='detail.do'">상세 조회</button>
	</div>

	<div class="container">
		<!-- ==================== [왼쪽] 검색 폼 영역 ==================== -->
		<div class="search-box">
			<form id="searchForm" action="detail.do" method="get">

				<div class="search-row">
					<input type="checkbox" id="chkDate"
						onchange="toggleInput('chkDate', 'startDate', 'endDate')">
					<label for="chkDate">근무일자</label>
					<div style="display: flex; gap: 5px; align-items: center;">
						<input type="date" id="startDate" name="startDate" disabled>
						~ <input type="date" id="endDate" name="endDate" disabled>
					</div>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkName"
						onchange="toggleInput('chkName', 'empName')"> <label
						for="chkName">성명</label> <input type="text" id="empName"
						name="empName" placeholder="성명을 입력하세요." disabled>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkDept"
						onchange="toggleInput('chkDept', 'deptId')"> <label
						for="chkDept">부서</label> <select id="deptId" name="deptId"
						disabled>
						<option value="">선택하세요.</option>
						<option value="관리">관리팀</option>
						<option value="연구">연구소</option>
						<option value="개발">개발팀</option>
					</select>
				</div>

				<div class="search-row">
					<input type="checkbox" id="chkProj"
						onchange="toggleInput('chkProj', 'projectId')"> <label
						for="chkProj">현장/프로젝트</label> <select id="projectId"
						name="projectId" disabled>
						<option value="">선택하세요.</option>
						<c:forEach var="proj" items="${projectList}">
							<!-- VO 클래스의 변수명에 맞게 projectName으로 유지 -->
							<option value="${proj.fieldOrProjectId}">${proj.projectName}</option>
						</c:forEach>
					</select>
				</div>

				<div class="btn-group">
					<button type="submit" class="btn-search">검색</button>
					<button type="button" class="btn-all" onclick="viewAll()">전체보기</button>
				</div>
			</form>
		</div>

		<!-- ==================== [오른쪽] 상세 데이터 테이블 ==================== -->
		<div class="table-container">
			<table>
				<thead>
					<tr>
						<th class="text-blue">근무일자</th>
						<th class="text-blue">사원번호</th>
						<th class="text-blue">성명</th>
						<th class="text-blue">부서</th>
						<th class="text-blue">현장/프로젝트</th>
						<th>일당</th>
						<th>지급율</th>
						<th>소득세</th>
						<th>지방소득세</th>
						<th>실지급액</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty detailList}">
							<tr>
								<td colspan="10" style="padding: 30px;">조회된 상세 근무 기록이 없습니다.</td>
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
        // 체크박스 클릭 시 입력창 활성화/비활성화 처리
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

        // 페이지 로드 시 URL을 확인하여 사원명 세팅
        window.onload = function() {
            
            const urlParams = new URLSearchParams(window.location.search);
            
            if (urlParams.has('empName')) {
                
                document.getElementById('chkName').checked = true;
                
                
                const empNameInput = document.getElementById('empName');
                empNameInput.disabled = false;
                empNameInput.value = urlParams.get('empName');
            }
        };

        // 전체보기 버튼 클릭 시 로직
        function viewAll() {
            window.location.href = "detail.do"; 
        }
    </script>
</body>
</html>