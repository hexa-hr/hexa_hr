<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 등록</title>
<style>
    /* ================= 1. 기본 폰트 및 화면 리셋 ================= */
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; margin: 0; padding: 0; background-color: #fff; }
    ul, li { list-style: none; padding: 0; margin: 0; }
    
    /* ================= 2. 전체 레이아웃 (Flexbox) ================= */
    .top-header { padding: 20px 30px; border-bottom: 1px solid #e1e1e1; margin-bottom: 20px; }
    .top-header h2 { margin: 0 0 10px 0; font-size: 22px; color: #222; display: flex; align-items: center; gap: 10px; }
    .top-header p { margin: 0; color: #666; font-size: 12px; }
    .req-text { color: #d9534f; }

    .wrap { display: flex; max-width: 1300px; margin: 0 auto; padding: 0 20px; gap: 30px; align-items: flex-start; }

    /* ================= 3. 왼쪽 사이드바 (★ 스크롤 따라다니는 핵심 기능 ★) ================= */
    .sidebar {
        width: 320px;
        background-color: #f8f9fa;
        padding: 20px;
        border: 1px solid #e1e1e1;
        
        /* 스크롤을 내려도 화면 위에서 20px 띄워진 채로 고정됨 */
        position: sticky; 
        top: 20px; 
        
        /* Flex 컨테이너 안에서 sticky가 작동하려면 이 속성이 필수! */
        align-self: flex-start; 
    }

    /* 사이드바 내부 - 사진 및 간략정보 영역 */
    .profile-box { display: flex; gap: 10px; margin-bottom: 20px; }
    .photo-area { width: 110px; text-align: center; }
    .photo-box { width: 110px; height: 140px; border: 1px solid #ccc; background-color: #fff; margin-bottom: 5px; display: flex; align-items: center; justify-content: center; color: #999; text-align: center; line-height: 1.4; }
    .brief-info-table { flex: 1; border-collapse: collapse; background-color: #fff; }
    .brief-info-table th, .brief-info-table td { border: 1px solid #ddd; padding: 5px; font-size: 11px; }
    .brief-info-table th { background-color: #f1f1f1; text-align: center; width: 45px; }

    /* 사이드바 내부 - 메뉴 버튼들 */
    .menu-title { font-weight: bold; font-size: 14px; margin: 20px 0 10px 0; padding-bottom: 5px; border-bottom: 1px solid #ccc; }
    .menu-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 5px; }
    .menu-btn { background-color: #6c757d; color: white; border: none; padding: 12px 0; text-align: center; cursor: pointer; border-radius: 3px; font-size: 12px; font-weight: bold; }
    .menu-btn.active { background-color: #5b8db8; } /* 선택된 메뉴 색상 */
    .menu-btn:hover { opacity: 0.9; }

    /* ================= 4. 오른쪽 메인 콘텐츠 ================= */
    .content { flex: 1; padding-bottom: 100px; }
    
    .section-title { font-size: 16px; font-weight: bold; margin: 0 0 10px 0; color: #333; }
    .sub-section-title { font-size: 14px; font-weight: bold; margin: 30px 0 10px 0; color: #333; display: flex; align-items: center; gap: 5px; }
    .sub-section-title::before { content: '+'; color: #5b8db8; font-weight: bold; }
    
    .dark-header { background-color: #555; color: #fff; padding: 10px 15px; font-weight: bold; font-size: 14px; margin: 40px 0 20px 0; }

    /* 테이블 공통 디자인 (파란 테두리 포인트) */
    .info-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; border-top: 2px solid #5b8db8; }
    .info-table th, .info-table td { border: 1px solid #e1e1e1; padding: 7px 10px; vertical-align: middle; }
    .info-table th { background-color: #f8f9fa; text-align: center; font-weight: normal; }
    .info-table td { background-color: #fff; }
    
    .req { color: red; margin-right: 3px; font-weight: bold; }
    
    /* 입력 폼 디자인 */
    input[type="text"], input[type="password"] { border: 1px solid #ccc; padding: 3px 5px; height: 20px; font-size: 12px; width: 90%; }
    select { border: 1px solid #ccc; height: 26px; padding: 2px; font-size: 12px; }
    textarea { width: 95%; height: 60px; border: 1px solid #ccc; padding: 5px; resize: vertical; }
    
    /* 버튼 */
    .btn-small { padding: 3px 8px; font-size: 11px; background-color: #5b8db8; color: white; border: none; border-radius: 2px; cursor: pointer; }
    .btn-white { padding: 3px 8px; font-size: 11px; background-color: #fff; color: #333; border: 1px solid #ccc; border-radius: 2px; cursor: pointer; }
    
    /* 하단 대형 버튼 */
    .bottom-buttons { text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; display: flex; justify-content: center; gap: 10px; }
    .bottom-buttons button { padding: 12px 30px; font-size: 14px; font-weight: bold; border: none; border-radius: 3px; cursor: pointer; color: white; }
    .btn-save { background-color: #4a7ab5; }
    .btn-cancel { background-color: #999; }
    .btn-list { background-color: #999; }
    .btn-new { background-color: #4a7ab5; }
</style>
</head>
<body>

<!-- 상단 헤더 타이틀 -->
<div class="top-header">
    <h2><img src="https://img.icons8.com/color/30/000000/manager.png" alt="icon"> 사원 등록</h2>
    <p>사원정보를 등록하는 메뉴입니다. 해당되는 항목만 입력하시면 됩니다. (<span class="req-text">* 표시는 필수 입력사항</span>)</p>
</div>

<!-- 메인 레이아웃 (사이드바 + 콘텐츠) -->
<div class="wrap">
    
    <!-- ================= [왼쪽] 스크롤 따라다니는 사이드바 ================= -->
    <div class="sidebar">
        
        <!-- 1. 사진 및 요약 정보 -->
        <div class="profile-box">
            <div class="photo-area">
                <div class="photo-box">
                    사원사진을<br>등록해주세요
                </div>
                <button type="button" class="btn-white">등록</button>
                <button type="button" class="btn-white">삭제</button>
            </div>
            
            <table class="brief-info-table">
                <tr><th>사원번호</th><td style="color:#666;">No-140043</td></tr>
                <tr><th>성명</th><td></td></tr>
                <tr><th>부서</th><td></td></tr>
                <tr><th>직위</th><td></td></tr>
                <tr><th>입사일</th><td></td></tr>
            </table>
        </div>
        <div style="text-align: right;"><button type="button" class="btn-white" style="width: 100%;">인사기록카드</button></div>

        <!-- 2. 사원정보 1 (메뉴) -->
        <div class="menu-title">사원정보 1</div>
        <div class="menu-grid">
            <button type="button" class="menu-btn active">급여<br>4대 보험</button>
            <button type="button" class="menu-btn">부양가족</button>
            <button type="button" class="menu-btn">학력</button>
            <button type="button" class="menu-btn">경력</button>
            <button type="button" class="menu-btn">병역</button>
        </div>

        <!-- 3. 사원정보 2 (메뉴) -->
        <div class="menu-title">사원정보 2</div>
        <div class="menu-grid">
            <button type="button" class="menu-btn">자격 면허</button>
            <button type="button" class="menu-btn">교육 훈련</button>
            <button type="button" class="menu-btn">상벌</button>
            <button type="button" class="menu-btn">발령</button>
            <button type="button" class="menu-btn" style="font-size:11px;">추천 신원보증</button>
            <button type="button" class="menu-btn">퇴직</button>
        </div>
    </div>


    <!-- ================= [오른쪽] 메인 입력 폼 콘텐츠 ================= -->
    <div class="content">
        
        <!-- 기본정보 섹션 -->
        <h3 class="section-title">기본정보</h3>
        <table class="info-table">
            <colgroup><col width="13%"><col width="37%"><col width="13%"><col width="37%"></colgroup>
            <tr>
                <th>사원번호</th>
                <td style="color:#888;">No-140043</td>
                <th><span class="req">*</span>고용형태</th>
                <td><select><option>선택해주세요.</option></select></td>
            </tr>
            <tr>
                <th><span class="req">*</span>성명(한글)</th>
                <td><input type="text"></td>
                <th>성명(영문)</th>
                <td><input type="text"></td>
            </tr>
            <tr>
                <th><span class="req">*</span>입사일</th>
                <td><input type="text"></td>
                <th>퇴사일</th>
                <td><input type="text"></td>
            </tr>
            <tr>
                <th>부서</th>
                <td>
                    <select style="width: 120px;"><option>선택해주세요.</option></select>
                    <button type="button" class="btn-small">관리</button>
                </td>
                <th>직위</th>
                <td>
                    <select style="width: 120px;"><option>선택해주세요.</option></select>
                    <button type="button" class="btn-small">관리</button>
                </td>
            </tr>
            <tr>
                <th>내/외국인</th>
                <td><select><option>선택해주세요.</option></select></td>
                <th>주민번호</th>
                <td><input type="text" style="width: 80px;"> - <input type="password" style="width: 90px;"></td>
            </tr>
            <tr>
                <th>주소</th>
                <td colspan="3">
                    <button type="button" class="btn-white">우편번호</button>
                    <input type="text" style="width: 60%; margin-top: 5px;">
                </td>
            </tr>
            <tr>
                <th>전화번호</th>
                <td>
                    <select><option>선택</option></select> - <input type="text" style="width: 40px;"> - <input type="text" style="width: 40px;">
                </td>
                <th>휴대폰</th>
                <td>
                    <select><option>선택</option></select> - <input type="text" style="width: 40px;"> - <input type="text" style="width: 40px;">
                </td>
            </tr>
            <tr>
                <th>이메일</th>
                <td><input type="text"></td>
                <th>SNS</th>
                <td><input type="text"></td>
            </tr>
            <tr>
                <th>기타사항</th>
                <td colspan="3"><textarea></textarea></td>
            </tr>
        </table>

        <!-- 사원 정보 1 (구분선) -->
        <div class="dark-header">사원 정보 1</div>

        <!-- 급여 & 4대보험 섹션 -->
        <h3 class="section-title" style="display:flex; align-items:center; gap:5px;">급여 & 4대보험 <span style="background:#5b8db8; color:#fff; border-radius:50%; width:16px; height:16px; display:inline-block; text-align:center; line-height:16px; font-size:10px;">?</span></h3>
        
        <div class="sub-section-title">급여</div>
        <table class="info-table">
            <colgroup><col width="15%"><col width="30%"><col width="55%"></colgroup>
            <tr>
                <th><span class="req">*</span>4대보험</th>
                <td colspan="2">
                    <label><input type="checkbox" checked> 국민연금</label> &nbsp;
                    <label><input type="checkbox" checked> 건강보험(감면: <select><option>선택</option></select>)</label> &nbsp;
                    <label><input type="checkbox" checked> 노인장기요양보험 포함(감면: <select><option>선택</option></select>)</label> &nbsp;
                    <label><input type="checkbox" checked> 고용보험</label>
                </td>
            </tr>
            <tr>
                <th><span class="req">*</span>갑근세</th>
                <td colspan="2">
                    <label><input type="radio" name="tax" checked> 근로소득자(근로소득간이세액표) 세액: <select><option>100%</option></select></label>
                </td>
            </tr>
            <tr>
                <th>국민연금 기준소득월액</th>
                <td><input type="text" style="width: 150px; text-align:right;"> 원</td>
                <td rowspan="3" style="color:#666; font-size:11px; line-height:1.5;">
                    입력시 4대보험 공제시 우선 적용되며,<br>미입력시 해당 근속월의 비과세를 제외한 과세합계로 적용됩니다.
                </td>
            </tr>
            <tr>
                <th>건강보험 보수월액</th>
                <td><input type="text" style="width: 150px; text-align:right;"> 원</td>
            </tr>
            <tr>
                <th>고용보험 보수월액</th>
                <td><input type="text" style="width: 150px; text-align:right;"> 원</td>
            </tr>
            <tr>
                <th>급여계좌</th>
                <td colspan="2">
                    <select><option>선택해주세요</option></select>
                    계좌번호 <input type="text" style="width: 200px;">
                    <button type="button" class="btn-small" style="background:#4a7ab5;">예금주 조회</button>
                </td>
            </tr>
        </table>

        <!-- 하단 섹션들 (부양가족, 학력 등) - 스크롤 테스트를 위해 뼈대만 배치 -->
        <div style="display:flex; justify-content:space-between; align-items:flex-end;">
            <div class="sub-section-title">부양가족</div>
            <div><button type="button" class="btn-white">+ 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
        </div>
        <table class="info-table" style="text-align:center;">
            <tr>
                <th style="width:30px;"><input type="checkbox"></th>
                <th><span class="req">*</span>관계</th>
                <th><span class="req">*</span>성명</th>
                <th>주민등록번호</th>
                <th>장애여부</th>
            </tr>
            <tr>
                <td><input type="checkbox"></td>
                <td><select><option>선택</option></select></td>
                <td><input type="text"></td>
                <td><input type="text" style="width:60px;"> - <input type="password" style="width:70px;"></td>
                <td><input type="checkbox"></td>
            </tr>
            <!-- 필요한 만큼 tr 복사해서 사용 -->
        </table>

        <div style="display:flex; justify-content:space-between; align-items:flex-end;">
            <div class="sub-section-title">학력</div>
            <div><button type="button" class="btn-white">+ 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
        </div>
        <table class="info-table" style="text-align:center;">
            <tr>
                <th style="width:30px;"><input type="checkbox"></th>
                <th>구분</th>
                <th>입학년월</th>
                <th>졸업년월</th>
                <th>학교명</th>
            </tr>
            <tr>
                <td><input type="checkbox"></td>
                <td><select><option>선택</option></select></td>
                <td><input type="text" style="width:40px;">년 <input type="text" style="width:30px;">월</td>
                <td><input type="text" style="width:40px;">년 <input type="text" style="width:30px;">월</td>
                <td><input type="text"></td>
            </tr>
        </table>
        
        <div style="display:flex; justify-content:space-between; align-items:flex-end;">
            <div class="sub-section-title">경력</div>
            <div><button type="button" class="btn-white">+ 추가</button> <button type="button" class="btn-white">선택삭제</button></div>
        </div>
        <table class="info-table" style="text-align:center;">
            <tr>
                <th style="width:30px;"><input type="checkbox"></th>
                <th>회사명</th>
                <th>근무기간</th>
                <th>최종직위</th>
                <th>담당직무</th>
            </tr>
            <tr>
                <td><input type="checkbox"></td>
                <td><input type="text"></td>
                <td><input type="text" style="width:30px;">년 <input type="text" style="width:30px;">개월</td>
                <td><input type="text"></td>
                <td><input type="text"></td>
            </tr>
        </table>

        <!-- 하단 공통 저장 버튼 영역 -->
        <div class="bottom-buttons">
            <button type="button" class="btn-save">저장</button>
            <button type="button" class="btn-cancel">취소</button>
            <button type="button" class="btn-list">리스트</button>
            <button type="button" class="btn-new">신규사원 등록</button>
        </div>

    </div>
</div>

</body>
</html>