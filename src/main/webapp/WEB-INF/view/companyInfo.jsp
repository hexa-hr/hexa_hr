<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>인사기록카드 등록</title>
<style>
    /* 기본 초기화 및 폰트 설정 */
    body {
        font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
        font-size: 13px;
        color: #333;
        margin: 0;
        padding: 20px;
        background-color: #fff;
    }
    
    input[type="text"], input[type="password"], select, textarea {
        border: 1px solid #ccc;
        padding: 4px;
        font-size: 12px;
        box-sizing: border-box;
    }
    
    .btn-blue {
        background-color: #5b82ad;
        color: white;
        border: none;
        padding: 5px 12px;
        cursor: pointer;
        font-size: 12px;
    }
    .btn-gray {
        background-color: #909090;
        color: white;
        border: none;
        padding: 5px 12px;
        cursor: pointer;
        font-size: 12px;
    }
    .btn-white {
        background-color: #fff;
        color: #333;
        border: 1px solid #ccc;
        padding: 4px 8px;
        cursor: pointer;
        font-size: 12px;
    }
    
    .required { color: red; }

    /* 전체 레이아웃 (좌측 메뉴 + 우측 컨텐츠) */
    .container {
        display: flex;
        width: 100%;
        max-width: 1300px;
        margin: 0 auto;
        gap: 20px;
    }

    /* 좌측 사이드바 */
    .sidebar {
        width: 250px;
        flex-shrink: 0;
    }
    
    .photo-area {
        border: 1px solid #ddd;
        height: 150px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #888;
        margin-bottom: 5px;
    }
    
    .photo-btns { text-align: center; margin-bottom: 10px; }
    
    .profile-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 10px;
    }
    .profile-table th, .profile-table td {
        border: 1px solid #ddd;
        padding: 8px;
    }
    .profile-table th { background-color: #f9f9f9; text-align: center; width: 40%; }
    
    .menu-section-title { font-weight: bold; margin: 20px 0 10px 0; }
    
    .menu-grid {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        gap: 5px;
    }
    .menu-grid button {
        background-color: #7a8896;
        color: white;
        border: none;
        padding: 10px 0;
        cursor: pointer;
    }
    
    /* 우측 메인 폼 영역 */
    .main-content {
        flex-grow: 1;
    }
    
    .section-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 10px;
    }
    
    .form-table {
        width: 100%;
        border-collapse: collapse;
        border-top: 2px solid #5b82ad;
        margin-bottom: 20px;
    }
    .form-table th, .form-table td {
        border: 1px solid #ddd;
        padding: 8px 10px;
        vertical-align: middle;
    }
    .form-table th {
        background-color: #f9f9f9;
        text-align: center;
        width: 15%;
    }
    .form-table td { width: 35%; }
    
    /* 하단 버튼 영역 */
    .action-btns {
        text-align: center;
        margin: 30px 0;
        border-bottom: 1px solid #eee;
        padding-bottom: 30px;
    }
    .action-btns button {
        padding: 10px 20px;
        font-size: 14px;
        font-weight: bold;
        margin: 0 5px;
    }
    
    /* 부양가족 (맨 아래 영역) */
    .dependent-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
    }
    .dependent-header h3 { margin: 0; font-size: 15px; }
    
    .list-table {
        width: 100%;
        border-collapse: collapse;
        border-top: 2px solid #5b82ad;
    }
    .list-table th, .list-table td {
        border: 1px solid #ddd;
        padding: 8px;
        text-align: center;
    }
    .list-table th { background-color: #f9f9f9; }

</style>
</head>
<body>

<div class="container">
    
    <!-- 좌측 사이드바 시작 -->
    <div class="sidebar">
        <!-- 사진 영역 -->
        <div class="photo-area">사원사진을<br>등록해주세요</div>
        <div class="photo-btns">
            <button class="btn-white">등록</button>
            <button class="btn-white">삭제</button>
        </div>
        
        <!-- 프로필 요약 테이블 -->
        <table class="profile-table">
            <tr><th>사원번호</th><td>No-140043</td></tr>
            <tr><th>성명</th><td></td></tr>
            <tr><th>부서</th><td></td></tr>
            <tr><th>직위</th><td></td></tr>
            <tr><th>입사일</th><td></td></tr>
        </table>
        <button class="btn-white" style="width:100%; margin-bottom:20px;">인사기록카드</button>
        
        <!-- 사원정보 1 -->
        <div class="menu-section-title">사원정보 1</div>
        <div class="menu-grid">
            <button>급여<br>4대 보험</button>
            <button>부양가족</button>
            <button style="background-color: #5b82ad;">학력</button> <!-- 활성화 컬러 반영 -->
            <button>경력</button>
            <button>병역</button>
        </div>
        
        <!-- 사원정보 2 -->
        <div class="menu-section-title">사원정보 2</div>
        <div class="menu-grid">
            <button>자격 면허</button>
            <button>교육 훈련</button>
            <button>상벌</button>
            <button>발령</button>
            <button>추천 신원보증</button>
            <button>퇴직</button>
        </div>
    </div>
    <!-- 좌측 사이드바 끝 -->


    <!-- 우측 메인 컨텐츠 시작 -->
    <div class="main-content">
        
        <div class="section-title">기본정보</div>
        <div style="margin-bottom: 5px;">
            <select><option>선택</option></select>
        </div>
        
        <!-- 기본정보 입력 폼 테이블 -->
        <table class="form-table">
            <tr>
                <th>사원번호</th>
                <td style="color:#666;">No-140043</td>
                <th><span class="required">*</span> 고용형태</th>
                <td>
                    <select style="width: 120px;">
                        <option>정규직</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th><span class="required">*</span> 성명(한글)</th>
                <td><input type="text" value="dd" style="width: 80%;"></td>
                <th>성명(영문)</th>
                <td><input type="text" style="width: 80%;"></td>
            </tr>
            <tr>
                <th><span class="required">*</span> 입사일</th>
                <td><input type="text" value="dd" style="width: 80%;"></td>
                <th>퇴사일</th>
                <td><input type="text" style="width: 80%;"></td>
            </tr>
            <tr>
                <th>부서</th>
                <td>
                    <select style="width: 120px;"><option>선택해주세요.</option></select>
                    <button class="btn-blue">관리</button>
                </td>
                <th>직위</th>
                <td>
                    <select style="width: 120px;"><option>선택해주세요.</option></select>
                    <button class="btn-blue">관리</button>
                </td>
            </tr>
            <tr>
                <th>내/외국인</th>
                <td>
                    <select style="width: 120px;"><option>선택해주세요.</option></select>
                </td>
                <th>주민번호</th>
                <td>
                    <input type="text" style="width: 60px;"> - 
                    <input type="password" value="1234" style="width: 80px; background-color:#edf2fa;">
                </td>
            </tr>
            <tr>
                <th>주소</th>
                <td colspan="3">
                    <button class="btn-white">우편번호</button>
                    <input type="text" style="width: 70%;">
                </td>
            </tr>
            <tr>
                <th>전화번호</th>
                <td>
                    <select style="width:60px;"><option>선택</option></select> - 
                    <input type="text" style="width:60px;"> - 
                    <input type="text" style="width:60px;">
                </td>
                <th>휴대폰</th>
                <td>
                    <select style="width:60px;"><option>선택</option></select> - 
                    <input type="text" style="width:60px;"> - 
                    <input type="text" style="width:60px;">
                </td>
            </tr>
            <tr>
                <th>이메일</th>
                <td>
                    <input type="text" value="1234" style="width: 80%; background-color:#edf2fa;">
                </td>
                <th>SNS</th>
                <td>
                    <input type="text" style="width: 80%;">
                </td>
            </tr>
            <tr>
                <th>기타사항</th>
                <td colspan="3">
                    <textarea style="width: 98%; height: 80px; resize: none;"></textarea>
                </td>
            </tr>
        </table>
        
        <!-- 중앙 저장/취소 버튼들 -->
        <div class="action-btns">
            <button class="btn-blue">저장</button>
            <button class="btn-gray">취소</button>
            <button class="btn-gray">리스트</button>
            <button class="btn-blue">신규사원 등록</button>
        </div>


        <!-- 부양가족 영역 (요청하신 대로 맨 밑에 고정) -->
        <div class="dependent-section">
            <div class="dependent-header">
                <h3>부양가족</h3>
                <div>
                    <button class="btn-white">+ 추가</button>
                    <button class="btn-white">선택삭제</button>
                </div>
            </div>
            
            <table class="list-table">
                <thead>
                    <tr>
                        <th style="width: 5%;"><input type="checkbox"></th>
                        <th style="width: 25%;">관계</th>
                        <th style="width: 40%;">성명</th>
                        <th style="width: 30%;">구분</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="4" style="height: 50px; color: #999;">등록된 부양가족이 없습니다.</td>
                    </tr>
                    <!-- 데이터 추가 시 여기에 <tr>이 추가됩니다 -->
                </tbody>
            </table>
        </div>

    </div>
    <!-- 우측 메인 컨텐츠 끝 -->
    
</div>

</body>
</html>