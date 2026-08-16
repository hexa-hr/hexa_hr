<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 신규 등록</title>
<style>
/* 🌟 추가된 뼈대 CSS: 화면 좌우로 나누기 */
.wrap {
	display: flex;
	align-items: flex-start;
}

.sidebar {
	width: 280px;
	padding: 20px;
	background-color: #f4f4f4;
	border-right: 1px solid #ddd;
	height: 100vh;
	position: sticky;
	top: 0;
	box-sizing: border-box;
}
/* -------------------------------- */
.container {
	padding: 20px;
	font-family: sans-serif;
	flex: 1; /* 🌟 오른쪽 화면 다 차지하게 추가 */
	box-sizing: border-box;
}

.section-title {
	font-size: 18px;
	font-weight: bold;
	margin-top: 30px;
	margin-bottom: 10px;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 5px;
	max-width: 900px;
}

table {
	border-collapse: collapse;
	width: 100%;
	max-width: 900px;
	margin-bottom: 20px;
}

th, td {
	border: 1px solid #ccc;
	padding: 10px;
	font-size: 14px;
}

th {
	background-color: #f8f9fa;
	width: 15%;
	text-align: left;
}

input[type="text"], input[type="password"], input[type="date"], input[type="email"],
	input[type="number"], select {
	padding: 5px;
	width: 80%;
}

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
}

.menu-btn:hover {
	background-color: #555;
}
</style>
</head>
<body>

	<!-- 🌟 전체를 감싸는 wrap 시작 -->
	<div class="wrap">

		<div class="sidebar">

			<div
				style="background: white; padding: 15px; border: 1px solid #ccc; text-align: center; margin-bottom: 20px;">
				<img src="<%=request.getContextPath()%>/images/default_profile.png"
					alt="사진" style="width: 80px; height: 100px; background: #eee;">
				<p style="font-size: 12px; color: #777; margin-top: 10px;">사원사진을
					등록해주세요</p>
			</div>

			<h3>사원정보 1</h3>
			<div class="menu-grid">
				<button type="button" class="menu-btn"
					onclick="moveToPage2('career')">경력</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('military')">병역</button>
				<a href="#account" class="menu-btn">급여<br>4대 보험
				</a> <a href="#dependents" class="menu-btn">부양가족</a> <a href="#degree"
					class="menu-btn">학력</a>
			</div>

			<h3>사원정보 2</h3>
			<div class="menu-grid">
				<!-- 2페이지로 넘어가는 버튼들 -->
				<button type="button" class="menu-btn" onclick="moveToPage2('cert')">자격
					면허</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('training')">교육 훈련</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('reward')">상벌</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('appointment')">발령</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('referrer')">추천 신원보증</button>
				<button type="button" class="menu-btn"
					onclick="moveToPage2('retirement')">퇴직</button>
			</div>
		</div>

		<div class="container">
			<h2>사원 신규 등록</h2>
			<p style="color: red; font-size: 12px;">* 표시는 필수입력 항목입니다.</p>

			<!-- 🌟 새로고침 없이 백그라운드에서 저장하기 위한 투명 프레임 -->
			<iframe name="hidden_iframe" style="display: none;"></iframe>

			<!-- 🌟 target="hidden_iframe" 을 추가해서 이 폼은 투명 프레임 안에서만 돌게 합니다 -->
			<form action="<%=request.getContextPath()%>/employee/register.do"
				method="post" target="hidden_iframe"
				onsubmit="return validateForm();">
				<input type="hidden" name="companyId" value="1"> <input
					type="hidden" name="personId" value="1">

				<!-- 🌟 핸들러가 사원번호를 쏙 넣어줄 임시 보관함 -->
				<input type="hidden" id="hiddenEmpId" value="">

				<!-- 1. 기본 정보 섹션 -->
				<div class="section-title">기본 정보</div>
				<table>
					<tr>
						<th>* 한글 성명</th>
						<td><input type="text" name="koreanName" required></td>
						<th>영문 성명</th>
						<td><input type="text" name="englishName"></td>
					</tr>
					<tr>
						<th>* 고용 형태</th>
						<td><select name="employmentType">
								<option value="정규직">정규직</option>
								<option value="계약직">계약직</option>
								<option value="인턴">인턴</option>
						</select></td>
						<th>* 재직 상태</th>
						<td><select name="status">
								<option value="재직">재직</option>
								<option value="휴직">휴직</option>
								<option value="퇴사">퇴사</option>
						</select></td>
					</tr>
					<tr>
						<th>* 입사일</th>
						<td><input type="date" name="hireDate" required></td>
						<th>퇴사일</th>
						<td><input type="date" name="resignationDate"></td>
					</tr>
					<tr>
						<th>부서 ID</th>
						<td><input type="number" name="departmentId"
							placeholder="예: 1"></td>
						<th>직위 ID</th>
						<td><input type="number" name="positionId" placeholder="예: 1"></td>
					</tr>
					<tr>
						<th>내/외국인</th>
						<td><input type="radio" name="foreignOrDomestic" value="내국인"
							checked> 내국인 <input type="radio" name="foreignOrDomestic"
							value="외국인"> 외국인</td>
						<th>주민등록번호</th>
						<td><input type="text" name="residentNumber1" maxlength="6"
							style="width: 30%;" placeholder="앞 6자리"> - <input
							type="password" name="residentNumber2" maxlength="7"
							style="width: 30%;" placeholder="뒤 7자리"></td>
					</tr>
					<tr>
						<th>주소</th>
						<td colspan="3"><input type="text" name="address"
							style="width: 95%;"></td>
					</tr>
					<tr>
						<th>자택 전화번호</th>
						<td><input type="text" name="telPhone"></td>
						<th>휴대폰 번호</th>
						<td><input type="text" name="mobile"></td>
					</tr>
					<tr>
						<th>이메일</th>
						<td><input type="email" name="email"></td>
						<th>SNS</th>
						<td><input type="text" name="sns"></td>
					</tr>
					<tr>
						<th>기타 상세</th>
						<td colspan="3"><textarea name="otherDetails" rows="3"
								style="width: 95%; padding: 5px;"></textarea></td>
					</tr>
				</table>

				<!-- 🌟 2. 급여 계좌 정보 섹션 (id="account" 추가) -->
				<div class="section-title" id="account">급여 계좌 정보</div>
				<table>
					<tr>
						<th>은행명</th>
						<td><input type="text" name="bankName" placeholder="예: 국민은행"></td>
						<th>계좌번호</th>
						<td><input type="text" name="accountNumber"
							placeholder="- 제외하고 입력"></td>
					</tr>
					<tr>
						<th>예금주</th>
						<td colspan="3"><input type="text" name="depositStocks"
							style="width: 36%;"></td>
					</tr>
					<tr>
						<th>급여 산정일 1</th>
						<td><select name="calc1MonthType" style="width: 30%;">
								<option value="C">당월</option>
								<option value="P">전월</option>
						</select> <input type="number" name="salaryCalculation1"
							style="width: 40%;" placeholder="일(ex: 1)"></td>
						<th>급여 산정일 2</th>
						<td><select name="calc2MonthType" style="width: 30%;">
								<option value="C">당월</option>
								<option value="P">전월</option>
						</select> <input type="number" name="salaryCalculation2"
							style="width: 40%;" placeholder="일(ex: 말일은 31)"></td>
					</tr>
					<tr>
						<th>급여 지급일</th>
						<td colspan="3"><select name="paymentMonthType"
							style="width: 12%;">
								<option value="C">당월</option>
								<option value="N">익월</option>
						</select> <input type="number" name="salaryPaymentDate" style="width: 15%;"
							placeholder="지급일(ex: 10)"></td>
					</tr>
				</table>

				<!-- 3. 보험 정보 섹션 -->
				<div class="section-title">보험 정보 (선택)</div>
				<table>
					<tr>
						<th>보험 기관명</th>
						<td><input type="text" name="insuranceAgency"
							placeholder="예: 국민건강보험공단, SGI서울보증"></td>
						<th>보험 번호</th>
						<td><input type="text" name="insuranceNumber"
							placeholder="- 제외하고 입력"></td>
					</tr>
					<tr>
						<th>보험 가입 금액</th>
						<td><input type="number" name="insuranceAmount"
							placeholder="숫자만 입력"></td>
						<th>비고</th>
						<td><input type="text" name="remarks4"></td>
					</tr>
					<tr>
						<th>가입일(시작일)</th>
						<td><input type="date" name="insuranceStartDate"></td>
						<th>만료일(종료일)</th>
						<td><input type="date" name="insuranceEndDate"></td>
					</tr>
				</table>


				<!-- 🌟 4. 가족 사항 섹션 (id="dependents" 추가) -->
				<div class="section-title" id="dependents">
					가족 사항
					<button type="button" onclick="addDependentRow()"
						style="float: right; padding: 3px 8px; font-size: 12px; background-color: #1cc88a; color: white; border: none; border-radius: 3px; cursor: pointer;">+
						가족 추가</button>
				</div>
				<table id="dependentTable">
					<tr>
						<th style="width: 15%;">관계</th>
						<th style="width: 20%;">성명</th>
						<th style="width: 15%;">내/외국인</th>
						<th style="width: 20%;">주민번호 앞자리</th>
						<th style="width: 20%;">주민번호 뒷자리</th>
						<th style="width: 10%;">삭제</th>
					</tr>
				</table>

				<!-- 🌟 5. 학력 사항 섹션 (id="degree" 추가) -->
				<div class="section-title" id="degree">
					학력 사항
					<button type="button" onclick="addDegreeRow()"
						style="float: right; padding: 3px 8px; font-size: 12px; background-color: #f6c23e; color: white; border: none; border-radius: 3px; cursor: pointer;">+
						학력 추가</button>
				</div>
				<table id="degreeTable">
					<tr>
						<th style="width: 15%;">졸업구분</th>
						<th style="width: 25%;">학교명</th>
						<th style="width: 20%;">입학일</th>
						<th style="width: 20%;">졸업일</th>
						<th style="width: 15%;">전공</th>
						<th style="width: 10%;">수료상태</th>
						<th style="width: 10%;">삭제</th>
					</tr>
				</table>

				<!-- 하단 저장/초기화 버튼 -->
				<div style="text-align: center; max-width: 900px; margin-top: 20px;">
					<button type="submit"
						style="padding: 10px 30px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;">저장</button>
					<button type="reset"
						style="padding: 10px 30px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; margin-left: 10px; font-size: 16px;">초기화</button>
				</div>

			</form>
		</div>
		<!-- container 끝 -->

	</div>
	<!-- 🌟 wrap 끝 -->

	<!-- 자바스크립트로 동적 행 추가/삭제 기능 구현 -->
	<script>
		// 페이지가 처음 열릴 때 빈 칸 한 줄씩을 기본으로 띄워줌
		window.onload = function() {
			addDependentRow();
			addDegreeRow();
		};

		// 🌟 가족 사항 동적 추가/삭제 
		function addDependentRow() {
			var table = document.getElementById("dependentTable");
			var row = table.insertRow(-1);

			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			var cell4 = row.insertCell(3);
			var cell5 = row.insertCell(4);
			var cell6 = row.insertCell(5);

			cell1.innerHTML = '<input type="text" name="relationship" style="width: 90%;" placeholder="부, 모, 배우자 등">';
			cell2.innerHTML = '<input type="text" name="parentsName" style="width: 90%;">';
			cell3.innerHTML = '<select name="foreignOrDomestic1" style="width: 90%;"><option value="내국인">내국인</option><option value="외국인">외국인</option></select>';
			cell4.innerHTML = '<input type="text" name="parentsNumber1" maxlength="6" style="width: 90%;">';
			cell5.innerHTML = '<input type="password" name="parentsNumber2" maxlength="7" style="width: 90%;">';
			cell6.innerHTML = '<button type="button" onclick="deleteRow(this)" style="background-color: #e74a3b; color: white; border: none; border-radius: 3px; cursor: pointer;">X 삭제</button>';

			cell1.style.textAlign = "center";
			cell2.style.textAlign = "center";
			cell3.style.textAlign = "center";
			cell4.style.textAlign = "center";
			cell5.style.textAlign = "center";
			cell6.style.textAlign = "center";
		}

		function deleteRow(button) {
			var row = button.parentNode.parentNode;
			if (document.getElementById("dependentTable").rows.length > 2) {
				row.parentNode.removeChild(row);
			} else {
				alert("가족 정보는 최소 1줄이 필요합니다. (입력하지 않으면 저장되지 않습니다.)");
			}
		}

		// 🌟 학력 사항 동적 추가/삭제
		function addDegreeRow() {
			var table = document.getElementById("degreeTable");
			var row = table.insertRow(-1);

			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			var cell4 = row.insertCell(3);
			var cell5 = row.insertCell(4);
			var cell6 = row.insertCell(5);
			var cell7 = row.insertCell(6);

			cell1.innerHTML = '<select name="graduate" style="width: 90%;"><option value="고졸">고졸</option><option value="전문대졸">전문대졸</option><option value="대졸">대졸</option><option value="대학원졸">대학원졸</option></select>';
			cell2.innerHTML = '<input type="text" name="schoolName" style="width: 90%;" placeholder="학교명 입력">';
			cell3.innerHTML = '<input type="date" name="admissionDate" style="width: 90%;">';
			cell4.innerHTML = '<input type="date" name="graduationDate" style="width: 90%;">';
			cell5.innerHTML = '<input type="text" name="major" style="width: 90%;">';
			cell6.innerHTML = '<select name="completion" style="width: 90%;"><option value="졸업">졸업</option><option value="수료">수료</option><option value="중퇴">중퇴</option></select>';
			cell7.innerHTML = '<button type="button" onclick="deleteDegreeRow(this)" style="background-color: #e74a3b; color: white; border: none; border-radius: 3px; cursor: pointer;">X 삭제</button>';

			cell1.style.textAlign = "center";
			cell2.style.textAlign = "center";
			cell3.style.textAlign = "center";
			cell4.style.textAlign = "center";
			cell5.style.textAlign = "center";
			cell6.style.textAlign = "center";
			cell7.style.textAlign = "center";
		}

		function deleteDegreeRow(button) {
			var row = button.parentNode.parentNode;
			if (document.getElementById("degreeTable").rows.length > 2) {
				row.parentNode.removeChild(row);
			} else {
				alert("학력 정보는 최소 1줄이 필요합니다.");
			}
		}

		// 🌟 폼 전송 전 필수 입력(가족 사항 1명 이상)을 확인하는 함수
		function validateForm() {
			var relationships = document.getElementsByName("relationship");
			var parentsNames = document.getElementsByName("parentsName");

			var hasDependent = false; // 가족 정보가 입력되었는지 확인하는 깃발

			// 화면에 있는 가족 입력칸들을 하나씩 검사
			for (var i = 0; i < relationships.length; i++) {
				var relValue = relationships[i].value.trim();
				var nameValue = parentsNames[i].value.trim(); // 오타 수정됨!

				// 관계와 성명이 둘 다 한 글자라도 적혀있다면 '입력됨'으로 인정!
				if (relValue !== "" && nameValue !== "") {
					hasDependent = true;
					break; // 한 명이라도 찾으면 더 이상 검사할 필요 없이 통과
				}
			}

			// 가족 정보가 아무것도 없으면 경고창 띄우고 전송(저장) 중단!
			if (!hasDependent) {
				alert("가족 사항을 최소 1명 이상 입력해 주세요. (관계 및 성명 필수)");
				return false; // false를 리턴하면 서버로 데이터가 날아가지 않아!
			}

			return true; // 무사히 통과하면 폼 전송 시작!
		}

		// 🌟 사원정보 2 메뉴 누를 때 이동 검사
		function moveToPage2(tab) {
			// 주소창에 저장 완료 후 받아온 employeeId가 있는지 확인합니다.
			const empId = document.getElementById("hiddenEmpId").value;

			if (empId) {
				// 저장이 완료되어 사원번호가 있다면 2페이지로 이동!
				location.href = "register2.do?employeeId=" + empId + "&tab="
						+ tab;
			} else {
				alert("필수 입력란을 모두 입력하고 맨 아래의 [저장] 버튼을 눌러 DB에 등록한 후에만 부가정보 메뉴로 이동할 수 있습니다.");
			}
		}
	</script>
</body>
</html>