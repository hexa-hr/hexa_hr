<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 부가정보 등록 (사원정보 2)</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	margin: 0;
	background-color: #f8f9fa;
	font-family: sans-serif;
}

/* 🌟 화면 분할 뼈대 */
.wrap {
	display: flex;
	align-items: flex-start;
	max-width: 1400px;
	margin: 0 auto;
	background-color: white;
	border: 1px solid #ddd;
}

/* 🌟 왼쪽 고정 사이드바 */
.sidebar {
	width: 260px;
	padding: 20px;
	background-color: #f4f4f4;
	border-right: 1px solid #ddd;
	height: 100vh;
	position: sticky;
	top: 0;
	box-sizing: border-box;
	overflow-y: auto;
}

/* 🌟 오른쪽 콘텐츠 영역 */
.container {
	flex: 1;
	padding: 40px;
	box-sizing: border-box;
}

/* 사이드바 메뉴 버튼 */
.menu-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 8px;
	margin-bottom: 15px;
}

.menu-btn {
	background-color: #666;
	color: white;
	padding: 12px 5px;
	text-align: center;
	border-radius: 3px;
	cursor: pointer;
	text-decoration: none;
	font-size: 13px;
	border: none;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
	height: 45px;
	word-break: keep-all;
}

.menu-btn:hover {
	background-color: #555;
}

/* 콘텐츠 테이블 스타일 */
.section-title {
	font-size: 18px;
	font-weight: bold;
	margin-top: 40px;
	margin-bottom: 10px;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 5px;
}

.section-title:first-child {
	margin-top: 0;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 30px;
	text-align: center;
}

th, td {
	border: 1px solid #ccc;
	padding: 10px 5px;
	font-size: 13px;
}

th {
	background-color: #f8f9fa;
	color: #333;
}

input[type="text"], input[type="date"], input[type="password"], input[type="number"],
	select {
	padding: 4px;
	width: 90%;
	border: 1px solid #ccc;
	box-sizing: border-box;
}

.add-btn {
	float: right;
	padding: 4px 12px;
	background-color: #1cc88a;
	color: white;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	font-size: 12px;
	font-weight: bold;
}

.add-btn:hover {
	background-color: #17a673;
}

.del-btn {
	background-color: #e74a3b;
	color: white;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	padding: 4px 8px;
	font-size: 12px;
}

/* 🌟 분리된 테이블용 헤더 버튼 스타일 추가 */
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
		<!-- ================= 1. 왼쪽 사이드바 ================= -->
		<div class="sidebar">
			<div
				style="background: white; padding: 15px; border: 1px solid #ccc; text-align: center; margin-bottom: 20px;">
				<img src="<%=request.getContextPath()%>/images/default_profile.png"
					alt="사진" style="width: 80px; height: 100px; background: #eee;">
				<p style="margin: 10px 0 0 0; font-weight: bold; font-size: 14px;">사원번호:
					${employeeId}</p>
			</div>

			<h3>사원정보 1</h3>
			<div class="menu-grid">
				<!-- 1페이지로 돌아가는 링크 (employeeId 파라미터 유지) -->
				<a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#account"
					class="menu-btn">급여<br>4대 보험
				</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#dependents"
					class="menu-btn">부양가족</a> <a
					href="<%=request.getContextPath()%>/employee/register.do?employeeId=${employeeId}#degree"
					class="menu-btn">학력</a> <a href="#career" class="menu-btn">경력</a> <a
					href="#military" class="menu-btn">병역</a>
			</div>

			<h3>사원정보 2 (현재 페이지)</h3>
			<div class="menu-grid">
				<!-- 🌟 2페이지 내의 각 섹션으로 스크롤 이동 (앵커) -->
				<a href="#cert" class="menu-btn">자격 면허</a> <a href="#training"
					class="menu-btn">교육 훈련</a> <a href="#reward" class="menu-btn">상벌</a>
				<a href="#appointment" class="menu-btn">발령</a> <a href="#referrer"
					class="menu-btn">추천 신원보증</a> <a href="#retirement" class="menu-btn">퇴직</a>
			</div>
		</div>

		<!-- ================= 2. 오른쪽 메인 콘텐츠 ================= -->
		<div class="container">
			<!-- 🌟 새로고침 없이 백그라운드에서 저장하기 위한 투명 프레임 -->
			<iframe name="hidden_iframe" style="display: none;"></iframe>

			<!-- 🌟 target="hidden_iframe"을 추가해서 새로고침을 완벽하게 막음! -->
			<form
				action="<%=request.getContextPath()%>/employee/register2_process.do"
				method="post" target="hidden_iframe">
				<input type="hidden" name="employeeId" value="${employeeId}">

				<!-- 1. 경력 사항 -->
				<div class="section-title" id="career">
					경력 사항
					<button type="button" class="add-btn"
						onclick="addRow('careerTable')">+ 추가</button>
				</div>
				<table id="careerTable">
					<tr>
						<th>회사명</th>
						<th>입사일자</th>
						<th>퇴사일자</th>
						<th>직급</th>
						<th>담당업무</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 2. 병역 사항 -->
				<div class="section-title" id="military">
					병역 사항
					<button type="button" class="add-btn"
						onclick="addRow('militaryTable')">+ 추가</button>
				</div>
				<table id="militaryTable">
					<tr>
						<th>병역구분</th>
						<th>군별</th>
						<th>복무시작일</th>
						<th>복무종료일</th>
						<th>최종계급</th>
						<th>병과</th>
						<th>면제사유</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 3. 자격·면허 & 어학능력 (🌟 수정된 영역) -->
				<div class="section-title" id="cert">자격·면허 & 어학능력</div>
				
				<!-- 3-1. 자격 & 면허 테이블 -->
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
				</table>

				<!-- 3-2. 어학능력 테이블 -->
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
				</table>

				<!-- 4. 교육 훈련 -->
				<div class="section-title" id="training">
					교육 훈련
					<button type="button" class="add-btn"
						onclick="addRow('trainingTable')">+ 추가</button>
				</div>
				<table id="trainingTable">
					<tr>
						<th>교육구분</th>
						<th>교육명</th>
						<th>시작일</th>
						<th>종료일</th>
						<th>교육기관</th>
						<th>교육비</th>
						<th>환급금</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 5. 상벌 -->
				<div class="section-title" id="reward">
					상벌
					<button type="button" class="add-btn"
						onclick="addRow('rewardTable')">+ 추가</button>
				</div>
				<table id="rewardTable">
					<tr>
						<th>구분</th>
						<th>상벌명</th>
						<th>상벌권자</th>
						<th>일자</th>
						<th>내용</th>
						<th>비고</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 6. 발령 -->
				<div class="section-title" id="appointment">
					발령
					<button type="button" class="add-btn"
						onclick="addRow('appointmentTable')">+ 추가</button>
				</div>
				<table id="appointmentTable">
					<tr>
						<th>발령구분</th>
						<th>일자</th>
						<th>부서ID</th>
						<th>직위ID</th>
						<th>직책</th>
						<th>비고</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 7. 추천 & 신원보증 -->
				<div class="section-title" id="referrer">
					추천 & 신원보증 (추천인)
					<button type="button" class="add-btn"
						onclick="addRow('referrerTable')">+ 추가</button>
				</div>
				<table id="referrerTable">
					<tr>
						<th>성명</th>
						<th>관계</th>
						<th>회사명</th>
						<th>직위</th>
						<th>전화번호</th>
						<th>삭제</th>
					</tr>
				</table>

				<div class="section-title" style="margin-top: 10px;">
					신원보증 (보증인)
					<button type="button" class="add-btn"
						onclick="addRow('guarantorTable')">+ 추가</button>
				</div>
				<table id="guarantorTable">
					<tr>
						<th>성명</th>
						<th>관계</th>
						<th>주민등록번호</th>
						<th>보증금액</th>
						<th>보증기간</th>
						<th>삭제</th>
					</tr>
				</table>

				<!-- 8. 퇴직 -->
				<div class="section-title" id="retirement">퇴직 정보</div>
				<table>
					<tr>
						<th>퇴직구분</th>
						<td><select name="retirementType" style="width: 80%;">
								<option value="">선택</option>
								<option value="정년퇴직">정년퇴직</option>
								<option value="자진퇴사">자진퇴사</option>
								<option value="권고사직">권고사직</option>
						</select></td>
						<th>퇴직일자</th>
						<td><input type="date" name="retirementDate"></td>
					</tr>
					<tr>
						<th>퇴직사유</th>
						<td colspan="3"><input type="text" name="retirementReason"
							style="width: 95%;"></td>
					</tr>
					<tr>
						<th>퇴직 후 연락처</th>
						<td><input type="text" name="retirementContact"
							style="width: 80%;"></td>
						<th>퇴직금(원)</th>
						<td><input type="number" name="severancePay"
							style="width: 80%; padding: 4px;"></td>
					</tr>
				</table>

				<!-- 하단 저장/초기화 버튼 -->
				<div
					style="text-align: center; margin-top: 50px; margin-bottom: 50px;">
					<!-- 🌟 onclick 알림 제거 (서버에서 parent.alert 띄워줌) -->
					<button type="submit"
						style="padding: 12px 40px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; font-weight: bold;">부가정보
						저장</button>
					<button type="reset"
						style="padding: 12px 40px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px; font-weight: bold;">초기화</button>
				</div>

			</form>
		</div>
		<!-- container 끝 -->
	</div>
	<!-- wrap 끝 -->

	<!-- 🌟 동적 행 추가/삭제 자바스크립트 -->
	<script>
		// 페이지 로드 시 각 테이블에 기본 빈 칸 1줄씩 생성
		window.onload = function() {
			addRow('careerTable');
			addRow('militaryTable');
			addRow('certTable');
			addRow('langTable'); // 🌟 어학능력 추가
			addRow('trainingTable');
			addRow('rewardTable');
			addRow('appointmentTable');
			addRow('referrerTable');
			addRow('guarantorTable');
		};

		// 테이블 ID를 받아서 알맞은 HTML 행을 추가해주는 마법의 함수
		function addRow(tableId) {
			var table = document.getElementById(tableId);
			var row = table.insertRow(-1);

			var html = "";
			if (tableId === 'careerTable') {
				html = '<td><input type="text" name="companyName"></td>'
						+ '<td><input type="date" name="startDate"></td>' 
						+ '<td><input type="date" name="endDate"></td>'
						+ '<td><input type="text" name="finalPosition"></td>'
						+ '<td><input type="text" name="responsibilities"></td>';
			} else if (tableId === 'militaryTable') {
				html = '<td><select name="serviceType" style="width:90%;"><option value="">선택</option><option value="필">필</option><option value="미필">미필</option><option value="면제">면제</option></select></td>'
						+ '<td><select name="branch" style="width:90%;"><option value="">선택</option><option value="육군">육군</option><option value="해군">해군</option><option value="공군">공군</option><option value="해병대">해병대</option><option value="기타">기타</option></select></td>'
						+ '<td><input type="date" name="servicePeriod1"></td>'
						+ '<td><input type="date" name="servicePeriod2"></td>'
						+ '<td><input type="text" name="finalRank"></td>'
						+ '<td><input type="text" name="department1"></td>'
						+ '<td><input type="text" name="exemptionReason"></td>';
			} else if (tableId === 'certTable') {
                // 🌟 자격 면허 (체크박스 구조)
				html = '<td><input type="checkbox" class="row-check"></td>'
						+ '<td><input type="text" name="certName"></td>'
						+ '<td><input type="date" name="certAcqDate"></td>'
						+ '<td><input type="text" name="certIssuer"></td>'
						+ '<td><input type="text" name="certNumber"></td>'
						+ '<td><input type="text" name="certRemarks"></td>';
			} else if (tableId === 'langTable') {
                // 🌟 어학 능력 (체크박스 구조)
                html = '<td><input type="checkbox" class="row-check"></td>'
                        + '<td><input type="text" name="langName"></td>'
                        + '<td><input type="text" name="langTest"></td>'
                        + '<td><input type="text" name="langScore"></td>'
                        + '<td><input type="date" name="langAcqDate"></td>'
                        + '<td><select name="langReading"><option value="">선택</option><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td>'
                        + '<td><select name="langWriting"><option value="">선택</option><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td>'
                        + '<td><select name="langSpeaking"><option value="">선택</option><option value="상">상</option><option value="중">중</option><option value="하">하</option></select></td>';
            } else if (tableId === 'trainingTable') {
				html = '<td><input type="text" name="trainingType"></td>'
						+ '<td><input type="text" name="trainingName"></td>'
						+ '<td><input type="date" name="trainingStartDate"></td>'
						+ '<td><input type="date" name="trainingEndDate"></td>'
						+ '<td><input type="text" name="trainingOrganization"></td>'
						+ '<td><input type="number" name="trainingCost"></td>'
						+ '<td><input type="number" name="refundableTrainingCost"></td>';
			} else if (tableId === 'rewardTable') {
				html = '<td><input type="text" name="rewardPenaltyType"></td>'
						+ '<td><input type="text" name="rewardPenaltyName"></td>'
						+ '<td><input type="text" name="rewardPenaltyGiver"></td>'
						+ '<td><input type="date" name="rewardPenaltyDate"></td>'
						+ '<td><input type="text" name="rewardPenaltyDescription"></td>'
						+ '<td><input type="text" name="remarks2"></td>';
			} else if (tableId === 'appointmentTable') {
				html = '<td><input type="text" name="appointmentType"></td>'
						+ '<td><input type="date" name="appointmentDate"></td>'
						+ '<td><input type="number" name="departmentId"></td>'
						+ '<td><input type="number" name="positionId"></td>'
						+ '<td><input type="text" name="positionType"></td>'
						+ '<td><input type="text" name="remarks3"></td>';
			} else if (tableId === 'referrerTable') {
				html = '<td><input type="text" name="referrerName"></td>'
						+ '<td><input type="text" name="referrerRelationship"></td>'
						+ '<td><input type="text" name="referrerCompanyName"></td>'
						+ '<td><input type="text" name="referrerPosition"></td>'
						+ '<td><input type="text" name="referrerPhoneNumber"></td>';
			} else if (tableId === 'guarantorTable') {
				html = '<td><input type="text" name="guaName"></td>'
						+ '<td><input type="text" name="guaRelation"></td>'
						+ '<td><input type="text" name="guaRrn" placeholder="[주민등록번호 입력]"></td>'
						+ '<td><input type="number" name="guaAmount"></td>'
						+ '<td><input type="text" name="guaPeriod" placeholder="예: 2년"></td>';
			}

			// 🌟 공통 삭제 버튼 (단, 체크박스로 삭제하는 certTable, langTable은 제외)
            if (tableId !== 'certTable' && tableId !== 'langTable') {
			    html += '<td><button type="button" class="del-btn" onclick="deleteRow(this, \'' + tableId + '\')">X</button></td>';
            }
			row.innerHTML = html;
		}

		function deleteRow(btn, tableId) {
			var table = document.getElementById(tableId);
			if (table.rows.length > 2) { // 헤더(1줄) + 최소 데이터(1줄) = 2줄 유지
				btn.parentNode.parentNode.remove();
			} else {
				alert("최소 1줄은 입력란이 필요합니다.");
			}
		}

        // 🌟 전체 선택 체크박스 로직 (자격면허, 어학능력용)
		function toggleAll(source, tableId) {
			var checkboxes = document.querySelectorAll('#' + tableId + ' .row-check');
			for(var i = 0; i < checkboxes.length; i++) {
				checkboxes[i].checked = source.checked;
			}
		}

		// 🌟 선택 삭제 로직 (자격면허, 어학능력용)
		function deleteSelectedRows(tableId) {
			var table = document.getElementById(tableId);
			var checkboxes = table.querySelectorAll('.row-check:checked');
			
			if (checkboxes.length === 0) {
				alert("삭제할 항목을 선택해주세요.");
				return;
			}
			
			if (table.rows.length - 1 === checkboxes.length) {
				alert("최소 1줄의 입력란은 남겨두어야 합니다.");
				return;
			}
			
			// 인덱스 꼬임을 방지하기 위해 아래쪽부터 삭제
			for (var i = checkboxes.length - 1; i >= 0; i--) {
				var row = checkboxes[i].closest('tr');
				row.parentNode.removeChild(row);
			}
			
			// 전체 선택 체크박스 해제
			table.querySelector('th input[type="checkbox"]').checked = false;
		}
	</script>

</body>
</html>