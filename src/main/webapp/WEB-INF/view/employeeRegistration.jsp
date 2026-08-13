<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 등록</title>
<style>
/* ================= 1. 기본 폰트 및 화면 리셋 ================= */
html {
	scroll-behavior: smooth;
}

body {
	font-family: 'Malgun Gothic', sans-serif;
	font-size: 12px;
	color: #333;
	margin: 0;
	padding: 0;
	background-color: #fff;
}

ul, li {
	list-style: none;
	padding: 0;
	margin: 0;
}

/* ================= 2. 전체 레이아웃 (Flexbox) ================= */
.top-header {
	padding: 20px 30px;
	border-bottom: 1px solid #e1e1e1;
	margin-bottom: 20px;
}

.top-header h2 {
	margin: 0 0 10px 0;
	font-size: 22px;
	color: #222;
	display: flex;
	align-items: center;
	gap: 10px;
}

.top-header p {
	margin: 0;
	color: #666;
	font-size: 12px;
}

.req-text {
	color: #d9534f;
}

.wrap {
	display: flex;
	max-width: 1300px;
	margin: 0 auto;
	padding: 0 20px;
	gap: 30px;
	align-items: flex-start;
}

/* ================= 3. 왼쪽 사이드바 ================= */
.sidebar {
	width: 320px;
	background-color: #f8f9fa;
	padding: 20px;
	border: 1px solid #e1e1e1;
	position: sticky;
	top: 20px;
	align-self: flex-start;
}

.profile-box {
	display: flex;
	gap: 10px;
	margin-bottom: 20px;
}

.photo-area {
	width: 110px;
	text-align: center;
}

.photo-box {
	width: 110px;
	height: 140px;
	border: 1px solid #ccc;
	background-color: #fff;
	margin-bottom: 5px;
	position: relative;
	overflow: hidden;
	color: #999;
	text-align: center;
	line-height: 1.4;
}

.brief-info-table {
	flex: 1;
	border-collapse: collapse;
	background-color: #fff;
}

.brief-info-table th, .brief-info-table td {
	border: 1px solid #ddd;
	padding: 5px;
	font-size: 11px;
}

.brief-info-table th {
	background-color: #f1f1f1;
	text-align: center;
	width: 45px;
}

.menu-title {
	font-weight: bold;
	font-size: 14px;
	margin: 20px 0 10px 0;
	padding-bottom: 5px;
	border-bottom: 1px solid #ccc;
}

.menu-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 5px;
}

.menu-btn {
	background-color: #6c757d;
	color: white;
	border: none;
	padding: 12px 0;
	text-align: center;
	cursor: pointer;
	border-radius: 3px;
	font-size: 12px;
	font-weight: bold;
	transition: 0.2s;
}

.menu-btn.active {
	background-color: #5b8db8;
}

.menu-btn:hover {
	opacity: 0.9;
}

/* ================= 4. 오른쪽 콘텐츠 및 테이블 기본 ================= */
.content {
	flex: 1;
	padding-bottom: 100px;
}

.section-title {
	font-size: 16px;
	font-weight: bold;
	margin: 0 0 10px 0;
	color: #333;
	scroll-margin-top: 30px;
}

.sub-section-title {
	font-size: 14px;
	font-weight: bold;
	margin: 30px 0 10px 0;
	color: #333;
	display: flex;
	align-items: center;
	justify-content: space-between;
	scroll-margin-top: 30px;
}

.sub-section-title span.title-left::before {
	content: '+ ';
	color: #d9534f;
	font-weight: normal;
}

.dark-header {
	background-color: #555;
	color: #fff;
	padding: 10px 15px;
	font-weight: bold;
	font-size: 14px;
	margin: 40px 0 20px 0;
	scroll-margin-top: 30px;
}

.info-table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 20px;
	border-top: 2px solid #5b8db8;
}

.info-table th, .info-table td {
	border: 1px solid #e1e1e1;
	padding: 7px 10px;
	vertical-align: middle;
}

.info-table th {
	background-color: #f8f9fa;
	text-align: center;
	font-weight: normal;
}

.info-table td {
	background-color: #fff;
}

.req {
	color: red;
	margin-right: 3px;
	font-weight: bold;
}

input[type="text"], input[type="password"] {
	border: 1px solid #ccc;
	padding: 3px 5px;
	height: 20px;
	font-size: 12px;
	width: 90%;
}

select {
	border: 1px solid #ccc;
	height: 26px;
	padding: 2px;
	font-size: 12px;
}

textarea {
	width: 95%;
	height: 60px;
	border: 1px solid #ccc;
	padding: 5px;
	resize: vertical;
}

.btn-small {
	padding: 3px 8px;
	font-size: 11px;
	background-color: #5b8db8;
	color: white;
	border: none;
	border-radius: 2px;
	cursor: pointer;
}

.btn-white {
	padding: 3px 8px;
	font-size: 11px;
	background-color: #fff;
	color: #333;
	border: 1px solid #ccc;
	border-radius: 2px;
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	gap: 3px;
	justify-content: center;
}

.bottom-buttons {
	text-align: center;
	margin-top: 50px;
	padding-top: 20px;
	border-top: 1px solid #eee;
	display: flex;
	justify-content: center;
	gap: 10px;
}

.bottom-buttons button {
	padding: 12px 30px;
	font-size: 14px;
	font-weight: bold;
	border: none;
	border-radius: 3px;
	cursor: pointer;
	color: white;
}

.grid-table {
	font-size: 11px;
	text-align: center;
}

.grid-table th {
	padding: 6px 2px;
	word-break: keep-all;
}

.grid-table td {
	padding: 4px 2px;
}

.grid-table input[type="text"], .grid-table select {
	width: 90%;
	box-sizing: border-box;
	text-align: center;
	margin: 0 auto;
	display: block;
}

.grid-table .date-input {
	width: 35px;
	display: inline-block;
}

.grid-table .jumin-input {
	width: 55px;
	display: inline-block;
}

.help-icon {
	background: #5b8db8;
	color: #fff;
	border-radius: 3px;
	font-size: 9px;
	padding: 1px 4px;
	display: inline-block;
	cursor: help;
}
</style>
</head>
<body>

	<div class="top-header">
		<h2>
			<img src="https://img.icons8.com/color/30/000000/manager.png"
				alt="icon"> 사원 등록
		</h2>
		<p>
			사원정보를 등록하는 메뉴입니다. 해당되는 항목만 입력하시면 됩니다. (<span class="req-text">*
				표시는 필수 입력사항</span>)
		</p>
	</div>

	<div class="wrap">

		<form action="employeeRegistration.do" method="post" id="employeeForm"
			enctype="multipart/form-data" onsubmit="handleSave(event)"
			style="display: flex; width: 100%; gap: 30px;">

			<!-- ================= 왼쪽 사이드바 ================= -->
			<div class="sidebar">
				<div class="profile-box">
					<div class="photo-area">
						<input type="file" id="empPhotoInput" name="empPhotoFile"
							accept="image/*" style="display: none;"
							onchange="previewPhoto(event)"> <input type="hidden"
							id="deletePhotoFlag" name="deletePhotoFlag" value="false">

						<div class="photo-box">
							<img id="empPhotoPreview" src=""
								style="display: none; width: 100%; height: 100%; object-fit: cover; position: absolute; top: 0; left: 0;">
							<span id="empPhotoDefaultText"
								style="display: inline-block; padding-top: 45px; width: 100%;">사원사진을<br>등록해주세요
							</span>
						</div>

						<button type="button" class="btn-white"
							onclick="document.getElementById('empPhotoInput').click();">등록</button>
						<button type="button" class="btn-white" onclick="deletePhoto();">삭제</button>
					</div>

					<table class="brief-info-table">
						<tr>
							<th>사원번호</th>
							<td style="color: #666;">No-140043</td>
						</tr>
						<tr>
							<th>성명</th>
							<td></td>
						</tr>
						<tr>
							<th>부서</th>
							<td></td>
						</tr>
						<tr>
							<th>직위</th>
							<td></td>
						</tr>
						<tr>
							<th>입사일</th>
							<td></td>
						</tr>
					</table>
				</div>
				<div style="text-align: right;">
					<button type="button" class="btn-white" style="width: 100%;">인사기록카드</button>
				</div>

				<div class="menu-title">사원정보 1</div>
				<div class="menu-grid" id="menuGroup1">
					<button type="button" class="menu-btn active"
						onclick="scrollToSection(this, 'sec-salary')">
						급여<br>4대 보험
					</button>
					<button type="button" class="menu-btn"
						onclick="scrollToSection(this, 'sec-family')">부양가족</button>
					<button type="button" class="menu-btn"
						onclick="scrollToSection(this, 'sec-edu')">학력</button>
					<button type="button" class="menu-btn"
						onclick="scrollToSection(this, 'sec-career')">경력</button>
					<button type="button" class="menu-btn"
						onclick="scrollToSection(this, 'sec-military')">병역</button>
				</div>

				<!-- ★ 사원정보 2 메뉴: 클릭 시 페이지 이동 (유효성 검사 적용) ★ -->
				<div class="menu-title">사원정보 2</div>
				<div class="menu-grid">
					<button type="button" class="menu-btn"
						onclick="goToPage2('employeeRegistration2.do')">자격 면허</button>
					<button type="button" class="menu-btn"
						onclick="goToPage2('employeeRegistration2.do')">교육 훈련</button>
					<button type="button" class="menu-btn"
						onclick="goToPage2('employeeRegistration2.do')">상벌</button>
					<button type="button" class="menu-btn"
						onclick="goToPage2('employeeRegistration2.do')">발령</button>
					<button type="button" class="menu-btn" style="font-size: 11px;"
						onclick="goToPage2('employeeRegistration2.do')">추천 신원보증</button>
					<button type="button" class="menu-btn"
						onclick="goToPage2('employeeRegistration2.do')">퇴직</button>
				</div>
			</div>

			<!-- ================= 오른쪽 콘텐츠 (폼 영역) ================= -->
			<div class="content">

				<h3 class="section-title">기본정보</h3>
				<table class="info-table">
					<colgroup>
						<col width="13%">
						<col width="37%">
						<col width="13%">
						<col width="37%">
					</colgroup>
					<tr>
						<th>사원번호</th>
						<td style="color: #888;">No-140043</td>
						<!-- 필수 항목: req-input 클래스와 title 속성 추가 -->
						<th><span class="req">*</span>고용형태</th>
						<td><select name="empType" class="req-input" title="고용형태">
								<option value="">선택해주세요.</option>
								<option value="정규직">정규직</option>
								<option value="계약직">계약직</option>
								<option value="사장실">사장실</option>
								<option value="개발팀">개발팀</option>
								<option value="콘텐츠팀">콘텐츠팀</option>
								<option value="업무지원팀">업무지원팀</option>
								<option value="디자인팀">디자인팀</option>
								<option value="관리팀">관리팀</option>
								<option value="기획전략팀">기획전략팀</option>
						</select></td>
						<select name="familyRelation">
							<option value="">선택</option>
							<option value="배우자">배우자</option>
							<option value="아들">아들</option>
							<option value="딸">딸</option>
							<option value="부">부</option>
							<option value="모">모</option>
							<option value="형제">형제</option>
							<option value="자매">자매</option>
							<option value="장인">장인</option>
							<option value="장모">장모</option>
							<option value="시아버지">시아버지</option>
							<option value="시어머니">시어머니</option>
							<option value="조부">조부</option>
							<option value="조모">조모</option>
							<option value="손자">손자</option>
							<option value="손녀">손녀</option>
						</select>
					</tr>
					<tr>
						<!-- 필수 항목 -->
						<th><span class="req">*</span>성명(한글)</th>
						<td><input type="text" name="empName" class="req-input"
							title="성명(한글)"></td>
						<th>성명(영문)</th>
						<td><input type="text" name="empEngName"></td>
					</tr>
					<tr>
						<!-- 필수 항목 -->
						<th><span class="req">*</span>입사일</th>
						<td><input type="text" name="joinDate" class="req-input"
							title="입사일"></td>
						<th>퇴사일</th>
						<td><input type="text" name="retireDate"></td>
					</tr>
					<tr>
						<th>부서</th>
						<td><select name="deptmentId" style="width: 120px;"><option>선택해주세요.</option></select>
							<option value="">선택해주세요.</option>
							<option value="사장실">사장실</option>
							<option value="개발팀">개발팀</option>
							<option value="콘텐츠팀">콘텐츠팀</option>
							<option value="업무지원팀">업무지원팀</option>
							<option value="디자인팀">디자인팀</option>
							<option value="관리팀">관리팀</option>
							<option value="기획전략팀">기획전략팀</option> </select>




							<button type="button" class="btn-small">관리</button></td>
						<th>직위</th>
						<td><select name="positionId" style="width: 120px;"><option>선택해주세요.</option></select>
							<select name="positionId" style="width: 120px;">
								<option value="">선택해주세요.</option>
								<option value="이사">이사</option>
								<option value="차장">차장</option>
								<option value="사장">사장</option>
								<option value="부장">부장</option>
								<option value="과장">과장</option>
								<option value="대리">대리</option>
								<option value="주임">주임</option>
								<option value="사원">사원</option>
								<option value="실장">실장</option>
						</select>
							<button type="button" class="btn-small">관리</button></td>
					</tr>
					<tr>
						<th>내/외국인</th>
						<td><select name="nationality"><option>선택해주세요.</option></select></td>
						<option value="">선택해주세요.</option>
						<option value="내국인">내국인</option>
						<option value="외국인">외국인</option>
						</select>
						<th>주민번호</th>
						<td><input type="text" name="jumin1" style="width: 80px;">
							- <input type="password" name="jumin2" style="width: 90px;"></td>
					</tr>
					<tr>
						<th>주소</th>
						<td colspan="3"><button type="button" class="btn-white">우편번호</button>
							<input type="text" name="address"
							style="width: 60%; margin-top: 5px;"></td>
					</tr>
					<tr>
						<th>전화번호</th>
						<td><select name="phone1"><option>선택</option></select> - <input
							type="text" name="phone2" style="width: 40px;"> - <input
							type="text" name="phone3" style="width: 40px;"></td>
						<th>휴대폰</th>
						<td><select name="mobile1"><option>선택</option></select> - <input
							type="text" name="mobile2" style="width: 40px;"> - <input
							type="text" name="mobile3" style="width: 40px;"></td>
					</tr>
					<tr>
						<th>이메일</th>
						<td><input type="text" name="email"></td>
						<th>SNS</th>
						<td><input type="text" name="sns"></td>
					</tr>
					<tr>
						<th>기타사항</th>
						<td colspan="3"><textarea name="memo"></textarea></td>
					</tr>
				</table>

				<div class="dark-header" id="sec-salary">사원 정보 1</div>

				<h3 class="section-title"
					style="display: flex; align-items: center; gap: 5px;">
					급여 & 4대보험 <span
						style="background: #5b8db8; color: #fff; border-radius: 3px; padding: 1px 5px; font-size: 10px;">?</span>
				</h3>
				<div class="sub-section-title">
					<span class="title-left" style="color: #5b8db8;">급여</span>
				</div>
				<table class="info-table">
					<colgroup>
						<col width="20%">
						<col width="30%">
						<col width="50%">
					</colgroup>
					<tr>
						<th><span class="req">*</span> 4대보험</th>
						<td colspan="2"><label><input type="checkbox"
								name="insure_nps"> 국민연금</label> &nbsp; <label><input
								type="checkbox" name="insure_health"> 건강보험(감면: <select
								style="width: 60px;"><option>선택</option></select>)</label> &nbsp;/&nbsp;
							<label><input type="checkbox" name="insure_care">
								노인장기요양보험 포함(감면: <select style="width: 60px;"><option>선택</option></select>)</label>
							&nbsp; <label><input type="checkbox" name="insure_emp">
								고용보험</label></td>
					</tr>
					<tr>
						<th><span class="req">*</span> 갑근세</th>
						<td colspan="2" style="line-height: 1.8;"><label><input
								type="radio" name="tax_type"> 근로소득자(근로소득간이세액표) 세액: <select
								style="width: 60px;"><option>100%</option></select></label> &nbsp; <label><input
								type="checkbox"> 중소기업 청년 소득세 감면 <select
								style="width: 60px;"><option>선택</option></select></label><br> <label><input
								type="radio" name="tax_type"> 사업소득자(3.3%)</label> <label><input
								type="radio" name="tax_type"> 일용직(2.97%)</label> <label><input
								type="radio" name="tax_type"> 기타소득자(8.8%)</label> <label><input
								type="radio" name="tax_type"> 근로/사업소득자</label> <label><input
								type="radio" name="tax_type"> 면제</label></td>
					</tr>
					<tr>
						<th style="line-height: 1.5;"><span class="req">*</span> 두루누리<br>사회보험
							지원<br> <br> <label style="font-weight: normal;"><input
								type="checkbox"> 분리설정</label></th>
						<td colspan="2" style="line-height: 1.6;"><label><input
								type="radio" name="duru_type"> 해당 없음</label> <label><input
								type="radio" name="duru_type"> 신규가입자(80% 지원)</label> <label><input
								type="radio" name="duru_type"> 신규가입자(90% 지원)</label>
							<div style="color: #d9534f; font-size: 11px; margin-top: 5px;">*
								위 기본정보에서 외국인으로 설정된 사원은 국민연금 보험료 지원대상에서 제외됩니다.</div></td>
					</tr>
					<tr>
						<!-- 필수 항목 -->
						<th><span class="req">*</span> 기본급/일급</th>
						<td style="background-color: #e8f0fe;"><input type="text"
							name="baseSalary" class="req-input" title="기본급/일급"
							style="width: 80%; text-align: right; border: none; background: transparent;">
							원</td>
						<td style="color: #666; font-size: 11px;">월급제의 경우 월 기본급, 일용직
							근로자의 경우 일급을 입력합니다.</td>
					</tr>
					<tr>
						<th>국민연금 기준소득월액</th>
						<td><input type="text" name="npsAmount"
							style="width: 80%; border: none; text-align: right;"> 원</td>
						<td rowspan="3"
							style="color: #666; font-size: 11px; line-height: 1.5;">입력시
							4대보험 공제시 우선 적용되며,<br>미입력시 해당 근속월의 비과세를 제외한 과세합계로 적용됩니다.
						</td>
					</tr>
					<tr>
						<th>건강보험 보수월액</th>
						<td><input type="text" name="healthAmount"
							style="width: 80%; border: none; text-align: right;"> 원</td>
					</tr>
					<tr>
						<th>고용보험 보수월액</th>
						<td><input type="text" name="empAmount"
							style="width: 80%; border: none; text-align: right;"> 원</td>
					</tr>
					<tr>
						<th>급여계좌</th>
						<td colspan="2"><select name="bankName"><option>선택해주세요</option></select>
							계좌번호 <input type="text" name="accountNum" style="width: 200px;">
							<button type="button" class="btn-small"
								style="background: #4a7ab5;">예금주 조회</button></td>
					</tr>
				</table>

				<div class="sub-section-title" style="margin-top: 10px;">
					<span class="title-left" style="color: #5b8db8; font-size: 12px;">4대보험</span>
				</div>
				<table class="info-table grid-table">
					<tr>
						<th>구분</th>
						<th>기호번호</th>
						<th>취득일</th>
						<th>상실일</th>
					</tr>
					<tr>
						<td>국민연금</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
					<tr>
						<td>건강보험</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
					<tr>
						<td>고용보험</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
					<tr>
						<td>산재보험</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
				</table>

				<div class="sub-section-title" id="sec-family">
					<span style="font-size: 16px;">부양가족</span>
					<div>
						<button type="button" class="btn-white">
							<span style="color: #d9534f; font-weight: bold;">+</span> 추가
						</button>
						<button type="button" class="btn-white">선택삭제</button>
					</div>
				</div>
				<table class="info-table grid-table">
					<tr>
						<th style="width: 25px;"><input type="checkbox"></th>
						<th><span class="req">*</span>관계</th>
						<th style="width: 120px;"><span class="req">*</span>성명</th>
						<th>구분</th>
						<th>주민등록번호</th>
						<th>장애여부</th>
						<th>인적공제</th>
						<th>건강보험</th>
						<th>동거여부</th>
						<th>갑근세 <span class="help-icon">?</span></th>
						<th>20세 이하 자녀 <span class="help-icon">?</span></th>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text"></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text" class="jumin-input"> - <input
							type="text" class="jumin-input"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text"></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text" class="jumin-input"> - <input
							type="text" class="jumin-input"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
						<td><input type="checkbox"></td>
					</tr>
				</table>

				<div class="sub-section-title" id="sec-edu">
					<span style="font-size: 16px;">학력</span>
					<div>
						<button type="button" class="btn-white">
							<span style="color: #d9534f; font-weight: bold;">+</span> 추가
						</button>
						<button type="button" class="btn-white">선택삭제</button>
					</div>
				</div>
				<table class="info-table grid-table">
					<tr>
						<th style="width: 25px;"><input type="checkbox"></th>
						<th>구분</th>
						<th>입학년월</th>
						<th>졸업년월</th>
						<th>학교명</th>
						<th>전공</th>
						<th>이수</th>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text" class="date-input">년 <input
							type="text" class="date-input">월</td>
						<td><input type="text" class="date-input">년 <input
							type="text" class="date-input">월</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><select><option>선택</option></select></td>
					</tr>
				</table>

				<div class="sub-section-title" id="sec-career">
					<span style="font-size: 16px;">경력</span>
					<div>
						<button type="button" class="btn-white">
							<span style="color: #d9534f; font-weight: bold;">+</span> 추가
						</button>
						<button type="button" class="btn-white">
							<img
								src="https://img.icons8.com/material-outlined/12/000000/trash--v1.png"
								style="vertical-align: middle;"> 선택삭제
						</button>
					</div>
				</div>
				<table class="info-table grid-table">
					<tr>
						<th style="width: 25px;"><input type="checkbox"></th>
						<th>회사명</th>
						<th>입사일자</th>
						<th>퇴사일자</th>
						<th>근무기간</th>
						<th>최종직위</th>
						<th>담당직무</th>
						<th>퇴직사유</th>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text" class="date-input">년 <input
							type="text" class="date-input">개월</td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
				</table>

				<div class="sub-section-title" id="sec-military">
					<span style="font-size: 16px;">병역</span>
				</div>
				<table class="info-table grid-table">
					<tr>
						<th>병역구분</th>
						<th>군별</th>
						<th>복무기간(부터)</th>
						<th>복무기간(까지)</th>
						<th>최종계급</th>
						<th>병과</th>
						<th>미필사유</th>
					</tr>
					<tr>
						<td><select><option>선택</option></select></td>
						<td><select><option>선택</option></select></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
						<td><input type="text"></td>
					</tr>
				</table>

				<!-- 하단 공통 버튼 영역 -->
				<div class="bottom-buttons">
					<!-- type="submit"을 유지하여 검사 통과 시 실제 DB(서버)로 데이터를 전송합니다. -->
					<button type="submit" class="btn-save" style="background: #4a7ab5;">저장</button>
					<button type="reset" class="btn-cancel"
						style="background: #a6a6a6;">취소</button>
					<button type="button" class="btn-list" style="background: #a6a6a6;">리스트</button>
					<button type="button" class="btn-new" style="background: #4a7ab5;">신규사원
						등록</button>
				</div>

			</div>
		</form>
	</div>

	<!-- 부양가족 테이블 영역 -->
	<div class="sub-section-title">
		<span style="font-size: 16px;">부양가족</span>
		<div>
			<button type="button" class="btn-white" onclick="addFamilyRow()">+
				추가</button>
			<button type="button" class="btn-white" onclick="deleteFamilyRow()">선택삭제</button>
		</div>
	</div>
	<table class="info-table grid-table" id="familyTable">
		<thead>
			<tr>
				<th style="width: 25px;"><input type="checkbox"
					id="checkAllFamily" onclick="checkAll(this, 'familyChk')"></th>
				<th>관계</th>
				<th style="width: 120px;">성명</th>
				<th>구분</th>
			</tr>
		</thead>
		<tbody id="familyTbody">
			<!-- 자바스크립트로 행이 추가될 위치 -->
		</tbody>
	</table>

	<script>
    // 1. 행 추가 기능
    function addFamilyRow() {
        var tbody = document.getElementById('familyTbody');
        var row = document.createElement('tr');
        
        // name 속성을 통일하는 것이 핵심!
        row.innerHTML = `
            <td><input type="checkbox" name="familyChk"></td>
            <td>
            <select name="familyRelation">
            <option value="">선택</option>
            <option value="배우자">배우자</option>
            <option value="아들">아들</option>
            <option value="딸">딸</option>
            <option value="부">부</option>
            <option value="모">모</option>
            <option value="형제">형제</option>
            <option value="자매">자매</option>
            <option value="장인">장인</option>
            <option value="장모">장모</option>
            <option value="시아버지">시아버지</option>
            <option value="시어머니">시어머니</option>
            <option value="조부">조부</option>
            <option value="조모">조모</option>
            <option value="손자">손자</option>
            <option value="손녀">손녀</option>
        </select>
            </td>
            <td><input type="text" name="familyName"></td>
            <td>
                <select name="familyType">
                    <option value="내국인">내국인</option>
                    <option value="외국인">외국인</option>
                </select>
            </td>
        `;
        tbody.appendChild(row);
    }

    // 2. 행 삭제 기능 (화면에서만 지우기)
    function deleteFamilyRow() {
        var checkboxes = document.querySelectorAll('input[name="familyChk"]:checked');
        checkboxes.forEach(function(chk) {
            var row = chk.closest('tr');
            row.remove();
        });
    }
</script>
	<script>
		function scrollToSection(clickedButton, sectionId) {
			var buttons = document.querySelectorAll('#menuGroup1 .menu-btn');
			buttons.forEach(function(btn) {
				btn.classList.remove('active');
			});

			clickedButton.classList.add('active');

			var targetSection = document.getElementById(sectionId);
			if (targetSection) {
				targetSection.scrollIntoView({
					behavior : 'smooth',
					block : 'start'
				});
			}
		}

		function previewPhoto(event) {
			var input = event.target;
			if (input.files && input.files[0]) {
				var reader = new FileReader();
				reader.onload = function(e) {
					var previewImg = document.getElementById('empPhotoPreview');
					var defaultText = document
							.getElementById('empPhotoDefaultText');

					previewImg.src = e.target.result;
					previewImg.style.display = 'block';
					defaultText.style.display = 'none';

					document.getElementById('deletePhotoFlag').value = "false";
				};
				reader.readAsDataURL(input.files[0]);
			}
		}

		function deletePhoto() {
			var previewImg = document.getElementById('empPhotoPreview');
			var defaultText = document.getElementById('empPhotoDefaultText');
			var fileInput = document.getElementById('empPhotoInput');
			var deleteFlag = document.getElementById('deletePhotoFlag');

			previewImg.src = '';
			previewImg.style.display = 'none';
			defaultText.style.display = 'inline-block';

			fileInput.value = '';
			deleteFlag.value = "true";
		}

		// ★ 1. 필수항목 유효성 검사 함수 ★
		function checkMandatory() {
			// req-input 클래스가 붙은 모든 입력칸(텍스트, 셀렉트박스 등)을 찾습니다.
			var reqInputs = document.querySelectorAll('.req-input');

			for (var i = 0; i < reqInputs.length; i++) {
				var val = reqInputs[i].value.trim();
				// 값이 비어있거나, 선택값이 초기 상태인 경우
				if (val === '' || val === '선택해주세요.') {
					var fieldName = reqInputs[i].getAttribute('title')
							|| '필수 항목';
					alert(fieldName + '을(를) 입력해야 저장 및 이동이 가능합니다.');
					reqInputs[i].focus(); // 안 적은 칸으로 커서 이동
					return false; // 검사 실패
				}
			}
			return true; // 모두 통과
		}

		// ★ 2. 폼 저장 버튼 클릭 시 동작 (실제 DB 연결 모드) ★
		function handleSave(event) {
			// 필수 항목 검사를 통과하지 못하면 폼 전송(저장)을 즉시 멈춥니다.
			if (!checkMandatory()) {
				event.preventDefault();
				return;
			}

			// 검사를 통과했다면
			alert("저장에 성공하였습니다.");
			// event.preventDefault()를 호출하지 않았으므로, 폼 데이터가 Action 주소(.do)로 정상 제출되어 DB 로직을 타게 됩니다.
		}

		// ★ 3. 사원정보 2 메뉴 클릭 시 동작 ★
		function goToPage2(url) {
			// 현재 페이지의 필수항목이 다 기입되어 있어야만 2페이지로 넘어갑니다.
			if (checkMandatory()) {
				window.location.href = url;
			}
		}
	</script>

</body>
</html>