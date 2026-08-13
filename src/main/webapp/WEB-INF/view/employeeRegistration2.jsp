<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 등록 - 사원정보 2</title>
<style>
    /* ================= 공통 스타일 (페이지 1과 동일) ================= */
    html { scroll-behavior: smooth; }
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; margin: 0; padding: 0; background-color: #fff; }
    ul, li { list-style: none; padding: 0; margin: 0; }
    
    .top-header { padding: 20px 30px; border-bottom: 1px solid #e1e1e1; margin-bottom: 20px; }
    .top-header h2 { margin: 0 0 10px 0; font-size: 22px; color: #222; display: flex; align-items: center; gap: 10px; }
    .top-header p { margin: 0; color: #666; font-size: 12px; }
    .req-text { color: #d9534f; }
    .wrap { display: flex; max-width: 1300px; margin: 0 auto; padding: 0 20px; gap: 30px; align-items: flex-start; }

    .sidebar { width: 320px; background-color: #f8f9fa; padding: 20px; border: 1px solid #e1e1e1; position: sticky; top: 20px; align-self: flex-start; }
    .profile-box { display: flex; gap: 10px; margin-bottom: 20px; }
    .photo-area { width: 110px; text-align: center; }
    .photo-box { width: 110px; height: 140px; border: 1px solid #ccc; background-color: #fff; margin-bottom: 5px; position: relative; overflow: hidden; color: #999; text-align: center; line-height: 1.4; }
    .brief-info-table { flex: 1; border-collapse: collapse; background-color: #fff; }
    .brief-info-table th, .brief-info-table td { border: 1px solid #ddd; padding: 5px; font-size: 11px; }
    .brief-info-table th { background-color: #f1f1f1; text-align: center; width: 45px; }
    
    .menu-title { font-weight: bold; font-size: 14px; margin: 20px 0 10px 0; padding-bottom: 5px; border-bottom: 1px solid #ccc; }
    .menu-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 5px; }
    .menu-btn { background-color: #6c757d; color: white; border: none; padding: 12px 0; text-align: center; cursor: pointer; border-radius: 3px; font-size: 12px; font-weight: bold; transition: 0.2s; }
    .menu-btn.active { background-color: #5b8db8; }
    .menu-btn:hover { opacity: 0.9; }

    .content { flex: 1; padding-bottom: 100px; }
    .section-title { font-size: 16px; font-weight: bold; margin: 0 0 10px 0; color: #333; scroll-margin-top: 30px; }
    .sub-section-title { font-size: 14px; font-weight: bold; margin: 30px 0 10px 0; color: #333; display: flex; align-items: center; justify-content: space-between; scroll-margin-top: 30px; }
    .sub-section-title span.title-left::before { content: '+ '; color: #d9534f; font-weight: normal; }
    .dark-header { background-color: #555; color: #fff; padding: 10px 15px; font-weight: bold; font-size: 14px; margin: 40px 0 20px 0; scroll-margin-top: 30px; }
    
    .info-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; border-top: 2px solid #5b8db8; }
    .info-table th, .info-table td { border: 1px solid #e1e1e1; padding: 7px 10px; vertical-align: middle; }
    .info-table th { background-color: #f8f9fa; text-align: center; font-weight: normal; }
    .info-table td { background-color: #fff; }
    .req { color: red; margin-right: 3px; font-weight: bold; }
    
    input[type="text"] { border: 1px solid #ccc; padding: 3px 5px; height: 20px; font-size: 12px; width: 90%; }
    select { border: 1px solid #ccc; height: 26px; padding: 2px; font-size: 12px; }
    
    .btn-small { padding: 3px 8px; font-size: 11px; background-color: #5b8db8; color: white; border: none; border-radius: 2px; cursor: pointer; }
    .btn-white { padding: 3px 8px; font-size: 11px; background-color: #fff; color: #333; border: 1px solid #ccc; border-radius: 2px; cursor: pointer; display: inline-flex; align-items: center; gap: 3px; justify-content: center; }
    .bottom-buttons { text-align: center; margin-top: 50px; padding-top: 20px; border-top: 1px solid #eee; display: flex; justify-content: center; gap: 10px; }
    .bottom-buttons button { padding: 12px 30px; font-size: 14px; font-weight: bold; border: none; border-radius: 3px; cursor: pointer; color: white; }

    .grid-table { font-size: 11px; text-align: center; }
    .grid-table th { padding: 6px 2px; word-break: keep-all; }
    .grid-table td { padding: 4px 2px; }
    .grid-table input[type="text"], .grid-table select { width: 90%; box-sizing: border-box; text-align: center; margin: 0 auto; display: block; }
    .grid-table .date-input { width: 80px; display: inline-block; }
</style>
</head>
<body>

<div class="top-header">
    <h2><img src="https://img.icons8.com/color/30/000000/manager.png" alt="icon"> 사원 등록</h2>
    <p>사원정보를 등록하는 메뉴입니다. 해당되는 항목만 입력하시면 됩니다. (<span class="req-text">* 표시는 필수 입력사항</span>)</p>
</div>

<div class="wrap">
    
    <form action="employeeRegistration2.do" method="post" id="employeeForm2" onsubmit="handleSave(event)" style="display: flex; width: 100%; gap: 30px;">
        
        <!-- ================= 왼쪽 사이드바 ================= -->
        <div class="sidebar">
            <div class="profile-box">
                <div class="photo-area">
                    <div class="photo-box">
                        <img src="" style="display: none; width: 100%; height: 100%; object-fit: cover; position: absolute; top: 0; left: 0;">
                        <span style="display: inline-block; padding-top: 45px; width: 100%;">사원사진을<br>등록해주세요</span>
                    </div>
                    <button type="button" class="btn-white">등록</button> <button type="button" class="btn-white">삭제</button>
                </div>
                <table class="brief-info-table">
                    <tr><th>사원번호</th><td style="color:#666;">No-140034</td></tr>
                    <tr><th>성명</th><td>이응열</td></tr><tr><th>부서</th><td>사장실</td></tr><tr><th>직위</th><td>사장</td></tr><tr><th>입사일</th><td>2000-02-22</td></tr>
                </table>
            </div>
            <div style="text-align: right;"><button type="button" class="btn-white" style="width: 100%;">인사기록카드</button></div>

            <div class="menu-title">사원정보 1</div>
            <div class="menu-grid">
                <!-- 페이지 1로 돌아가기 -->
                <button type="button" class="menu-btn" onclick="location.href='employeeRegistration.do'">급여<br>4대 보험</button>
                <button type="button" class="menu-btn" onclick="location.href='employeeRegistration.do'">부양가족</button>
                <button type="button" class="menu-btn" onclick="location.href='employeeRegistration.do'">학력</button>
                <button type="button" class="menu-btn" onclick="location.href='employeeRegistration.do'">경력</button>
                <button type="button" class="menu-btn" onclick="location.href='employeeRegistration.do'">병역</button>
            </div>

            <div class="menu-title">사원정보 2</div>
            <div class="menu-grid" id="menuGroup2">
                <!-- 현재 페이지(사원정보 2) 내 스크롤 이동 -->
                <button type="button" class="menu-btn active" onclick="scrollToSection(this, 'sec-cert')">자격 면허</button>
                <button type="button" class="menu-btn" onclick="scrollToSection(this, 'sec-training')">교육 훈련</button>
                <button type="button" class="menu-btn" onclick="scrollToSection(this, 'sec-reward')">상벌</button>
                <button type="button" class="menu-btn" onclick="scrollToSection(this, 'sec-dispatch')">발령</button>
                <button type="button" class="menu-btn" style="font-size:11px;" onclick="scrollToSection(this, 'sec-guarantee')">추천 신원보증</button>
                <button type="button" class="menu-btn" onclick="scrollToSection(this, 'sec-retire')">퇴직</button>
            </div>
        </div>

        <!-- ================= 오른쪽 콘텐츠 (사원정보 2) ================= -->
        <div class="content">
            
            <div class="dark-header" id="sec-cert">사원 정보 2</div>

            <!-- 1. 자격·면허 & 어학능력 -->
            <h3 class="section-title">자격·면허 & 어학능력</h3>
            
            <div class="sub-section-title">
                <span class="title-left" style="color:#5b8db8;">자격 & 면허</span>
                <div><button type="button" class="btn-white"><span style="color:#d9534f; font-weight:bold;">+</span> 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
            </div>
            <table class="info-table grid-table">
                <tr><th style="width:25px;"><input type="checkbox"></th><th>자격/면허명</th><th>취득일</th><th>발행기관</th><th>증번호</th><th>비고</th></tr>
                <tr><td><input type="checkbox"></td><td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
                <tr><td><input type="checkbox"></td><td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
                <tr><td><input type="checkbox"></td><td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
            </table>

            <div class="sub-section-title">
                <span class="title-left" style="color:#d9534f;">어학능력</span>
                <div><button type="button" class="btn-white"><span style="color:#d9534f; font-weight:bold;">+</span> 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
            </div>
            <table class="info-table grid-table">
                <tr><th style="width:25px;"><input type="checkbox"></th><th>외국어명</th><th>시험</th><th>공인점수</th><th>취득일</th><th>독해</th><th>작문</th><th>회화</th></tr>
                <tr>
                    <td><input type="checkbox"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text" style="width:50px;"></td><td><input type="text" class="date-input"></td>
                    <td><select><option>선택</option><option>상</option><option>중</option><option>하</option></select></td>
                    <td><select><option>선택</option><option>상</option><option>중</option><option>하</option></select></td>
                    <td><select><option>선택</option><option>상</option><option>중</option><option>하</option></select></td>
                </tr>
            </table>

            <!-- 2. 교육훈련 -->
            <div class="sub-section-title" id="sec-training">
                <span style="font-size:16px;">교육훈련</span>
                <div><button type="button" class="btn-white"><span style="color:#d9534f; font-weight:bold;">+</span> 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
            </div>
            <table class="info-table grid-table">
                <tr><th style="width:25px;"><input type="checkbox"></th><th>교육구분</th><th>교육명</th><th>교육기간(부터)</th><th>교육기간(까지)</th><th>교육기관</th><th>교육비</th><th>환급교육비</th></tr>
                <tr>
                    <td><input type="checkbox"></td><td><select><option>선택</option><option>사외직무</option></select></td>
                    <td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text" class="date-input"></td>
                    <td><input type="text"></td><td><input type="text" style="width:80%; text-align:right;"> 원</td><td><input type="text" style="width:80%; text-align:right;"> 원</td>
                </tr>
                <tr>
                    <td><input type="checkbox"></td><td><select><option>선택</option><option>사외직무</option></select></td>
                    <td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text" class="date-input"></td>
                    <td><input type="text"></td><td><input type="text" style="width:80%; text-align:right;"> 원</td><td><input type="text" style="width:80%; text-align:right;"> 원</td>
                </tr>
            </table>

            <!-- 3. 상벌 -->
            <div class="sub-section-title" id="sec-reward">
                <span style="font-size:16px;">상벌</span>
                <div><button type="button" class="btn-white"><span style="color:#d9534f; font-weight:bold;">+</span> 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
            </div>
            <table class="info-table grid-table">
                <tr><th style="width:25px;"><input type="checkbox"></th><th>구분</th><th>상벌명</th><th>상벌권자</th><th>상벌일자</th><th>상벌내용</th><th>비고</th></tr>
                <tr><td><input type="checkbox"></td><td><select><option>선택</option></select></td><td><input type="text"></td><td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td></tr>
                <tr><td><input type="checkbox"></td><td><select><option>선택</option></select></td><td><input type="text"></td><td><input type="text"></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td></tr>
            </table>

            <!-- 4. 발령 -->
            <div class="sub-section-title" id="sec-dispatch">
                <span style="font-size:16px;">발령</span>
                <div><button type="button" class="btn-white"><span style="color:#d9534f; font-weight:bold;">+</span> 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
            </div>
            <table class="info-table grid-table">
                <tr><th style="width:25px;"><input type="checkbox"></th><th>발령구분</th><th>발령일자</th><th>부서</th><th>직위</th><th>직책</th><th>비고</th></tr>
                <tr><td><input type="checkbox"></td><td><select><option>선택</option></select></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
                <tr><td><input type="checkbox"></td><td><select><option>선택</option></select></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
            </table>

            <!-- 5. 추천 & 신원보증 -->
            <h3 class="section-title" id="sec-guarantee" style="margin-top: 40px;">추천 & 신원보증</h3>
            
            <div class="sub-section-title"><span class="title-left" style="color:#d9534f;">추천인</span></div>
            <table class="info-table grid-table">
                <tr><th>성명</th><th>관계</th><th>회사명</th><th>직위</th><th>전화번호</th></tr>
                <tr><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td></tr>
            </table>

            <div class="sub-section-title"><span class="title-left" style="color:#d9534f;">보증보험</span></div>
            <table class="info-table grid-table">
                <tr><th>가입기관</th><th>보험번호</th><th>보험금액</th><th>가입일자</th><th>만료일자</th><th>비고</th></tr>
                <tr><td><input type="text"></td><td><input type="text"></td><td><input type="text" style="width:80%; text-align:right;"> 원</td><td><input type="text" class="date-input"></td><td><input type="text" class="date-input"></td><td><input type="text"></td></tr>
            </table>

            <div class="sub-section-title"><span class="title-left" style="color:#d9534f;">보증인</span></div>
            <table class="info-table grid-table">
                <tr><th>성명</th><th>관계</th><th>주민등록번호</th><th>보증금액</th><th>보증일자</th><th>만료일자</th><th>전화번호</th></tr>
                <tr><td><input type="text"></td><td><input type="text"></td><td><input type="text"></td><td><input type="text" style="width:80%; text-align:right;"> 원</td><td><input type="text" class="date-input"></td><td><input type="text" class="date-input"></td><td><input type="text"></td></tr>
            </table>

            <!-- 6. 퇴직 -->
            <div class="sub-section-title" id="sec-retire"><span style="font-size:16px;">퇴직</span></div>
            <table class="info-table grid-table">
                <tr><th>퇴직구분</th><th>퇴직일자</th><th>퇴직사유</th><th>퇴직 후 연락처</th><th>퇴직금</th><th>퇴직금명세서</th></tr>
                <tr>
                    <td><select><option>선택</option></select></td><td><input type="text" class="date-input"></td><td><input type="text"></td><td><input type="text"></td>
                    <td><input type="text" style="width:80%; text-align:right;"> 원</td>
                    <td><button type="button" class="btn-white"><img src="https://img.icons8.com/material-outlined/12/000000/download.png" style="vertical-align:middle;"> 명세서 다운로드</button></td>
                </tr>
            </table>

            <!-- 하단 공통 버튼 영역 -->
            <div class="bottom-buttons">
                <!-- type="submit": 데이터 임시저장 원복용 -->
                <button type="submit" class="btn-save" style="background:#4a7ab5;">저장</button>
                <button type="reset" class="btn-cancel" style="background:#a6a6a6;">취소</button>
                <button type="button" class="btn-list" style="background:#a6a6a6;">리스트</button>
                <button type="button" class="btn-new" style="background:#4a7ab5;">신규사원 등록</button>
            </div>
            
        </div>
    </form>
</div>

<script>
    function scrollToSection(clickedButton, sectionId) {
        var buttons = document.querySelectorAll('#menuGroup2 .menu-btn');
        buttons.forEach(function(btn) {
            btn.classList.remove('active');
        });
        
        clickedButton.classList.add('active');
        
        var targetSection = document.getElementById(sectionId);
        if(targetSection) {
            targetSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }

    // 저장 버튼 클릭 시 동작 (페이지 1과 동일한 원복 스냅샷 기능)
    function handleSave(event) {
        event.preventDefault();

        var form = document.getElementById('employeeForm2');
        var elements = form.elements;

        for (var i = 0; i < elements.length; i++) {
            var el = elements[i];
            if (el.type !== 'checkbox' && el.type !== 'radio' && el.type !== 'file') {
                el.defaultValue = el.value;
            }
            if (el.type === 'checkbox' || el.type === 'radio') {
                el.defaultChecked = el.checked;
            }
        }
        var selects = form.querySelectorAll('select');
        selects.forEach(function(select) {
            for (var j = 0; j < select.options.length; j++) {
                select.options[j].defaultSelected = select.options[j].selected;
            }
        });

        alert("사원정보 2가 성공적으로 임시 저장되었습니다.\n취소를 누르면 방금 전 저장 상태로 되돌아갑니다.");
    }
</script>

</body>
</html>