<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회사정보 설정</title>
<style>
    /* ================= 기본 레이아웃 & 텍스트 스타일 ================= */
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; }
    .container { width: 1100px; margin: 0 auto; padding: 20px; }
    
    /* 섹션 제목 */
    .section-title { font-size: 15px; font-weight: bold; margin-bottom: 10px; color: #333; }
    
    /* 테이블 공통 디자인 (사진과 동일한 파란색 포인트 테두리) */
    .info-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; border-top: 2px solid #5b8db8; }
    .info-table th, .info-table td { border: 1px solid #e1e1e1; padding: 6px 10px; vertical-align: middle; height: 32px; }
    .info-table th { background-color: #f8f9fa; text-align: center; color: #333; font-weight: normal; }
    .info-table td { background-color: #fff; }
    
    /* 필수항목 빨간 별 */
    .req { color: red; margin-right: 3px; font-weight: bold; }
    
    /* 입력 폼 디자인 */
    input[type="text"], input[type="password"] { border: 1px solid #ccc; padding: 3px 5px; height: 20px; font-size: 12px; }
    select { border: 1px solid #ccc; height: 26px; padding: 2px; font-size: 12px; }
    
    /* 버튼 디자인 */
    .btn { display: inline-block; padding: 4px 12px; background-color: #5b8db8; color: white; border: none; cursor: pointer; text-align: center; border-radius: 3px; font-size: 12px; }
    .btn-gray { display: inline-block; padding: 4px 12px; background-color: #888; color: white; border: none; cursor: pointer; text-align: center; border-radius: 3px; font-size: 12px; }
    .btn-small { padding: 2px 8px; font-size: 11px; background-color: #5b8db8; color: white; border: none; border-radius: 2px; cursor: pointer; }
    .btn-white { display: inline-block; padding: 3px 8px; background-color: #fff; color: #333; border: 1px solid #ccc; cursor: pointer; border-radius: 3px; font-size: 11px; }

    /* ================= 팝업(모달) 디자인 ================= */
    .modal-overlay {
        display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
        background: rgba(0,0,0,0.5); z-index: 9999;
    }
    .modal-content {
        position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
        background: #fff; width: 350px; border-radius: 5px; box-shadow: 0 4px 8px rgba(0,0,0,0.2);
    }
    .modal-header { padding: 15px; background: #f8f9fa; font-weight: bold; border-bottom: 1px solid #ddd; }
    .modal-body { padding: 20px; text-align: center; }
    .modal-footer { padding: 15px; text-align: center; border-top: 1px solid #ddd; }
</style>
</head>
<body>
<div class="container">
    
    <form action="saveCompanyInfo.do" method="post" enctype="multipart/form-data">
        
        <!-- ================= 1. 상단 영역 (회사정보 & 담당자정보) ================= -->
        <div style="display: flex; gap: 30px;">
            
            <!-- 좌측: 회사정보 (너비 약 65%) -->
            <div style="flex: 2;">
                <div class="section-title">회사정보</div>
                <table class="info-table">
                    <colgroup>
                        <col width="15%"><col width="35%"><col width="18%"><col width="32%">
                    </colgroup>
                    <tr>
                        <th><span class="req">*</span>상호</th>
                        <td><input type="text" name="companyName" value="${company.companyName}" style="width: 90%;"></td>
                        <th><span class="req">*</span>대표자직급/대표자</th>
                        <td>
                            <input type="text" name="ceoTitle" value="${company.ceoTitle}" style="width: 50px;"> / 
                            <input type="text" name="ceoName" value="${company.ceoName}" style="width: 70px;">
                            <span style="color: #ccc; font-size: 10px; float: right;">[Y]</span>
                        </td>
                    </tr>
                    <tr>
                        <th><span class="req">*</span>사업자번호</th>
                        <td><input type="text" name="businessNumber" value="${company.businessNumber}" style="width: 90%;"></td>
                        <th>법인등록번호</th>
                        <td><input type="text" name="corpNumber" value="${company.corpNumber}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th>설립일</th>
                        <td><input type="text" name="foundingDate" value="${company.foundingDate}" style="width: 90%;"></td>
                        <th>홈페이지</th>
                        <td><input type="text" name="homepage" value="${company.homepage}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th><span class="req">*</span>사업장 주소</th>
                        <td colspan="3">
                            <input type="text" name="zipCode" value="${company.zipCode}" style="width: 60px;">
                            <button type="button" class="btn-white">우편번호</button>
                            <input type="text" name="address" value="${company.address}" style="width: 60%;">
                        </td>
                    </tr>
                    <tr>
                        <th><span class="req">*</span>전화번호</th>
                        <td>
                            <select><option>대표(없음)</option></select> - 
                            <input type="text" name="phone2" value="${company.phone2}" style="width: 40px;"> - 
                            <input type="text" name="phone3" value="${company.phone3}" style="width: 40px;">
                            <span style="color: #ccc; font-size: 10px; float: right;">[N]</span>
                        </td>
                        <th>팩스번호</th>
                        <td>
                            <select><option>서울(02)</option></select> - 
                            <input type="text" name="fax2" value="${company.fax2}" style="width: 40px;"> - 
                            <input type="text" name="fax3" value="${company.fax3}" style="width: 40px;">
                        </td>
                    </tr>
                    <tr>
                        <th>업태</th>
                        <td><input type="text" name="businessCondition" value="${company.businessCondition}" style="width: 90%;"></td>
                        <th>종목</th>
                        <td><input type="text" name="businessType" value="${company.businessType}" style="width: 90%;"></td>
                    </tr>
                </table>
            </div>

            <!-- 우측: 담당자정보 (너비 약 35%) -->
            <div style="flex: 1;">
                <div class="section-title">담당자정보</div>
                <table class="info-table">
                    <colgroup>
                        <col width="30%"><col width="70%">
                    </colgroup>
                    <tr>
                        <th><span class="req">*</span>성명</th>
                        <td><input type="text" name="managerName" value="${company.managerName}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th>부서</th>
                        <td>
                            <select style="width: 60%;"><option>기획전략팀</option></select>
                            <button type="button" class="btn-small">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>직위</th>
                        <td>
                            <select style="width: 60%;"><option>과장</option></select>
                            <button type="button" class="btn-small">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>전화번호</th>
                        <td>
                            <select><option>대표(없음)</option></select> - 
                            <input type="text" name="mgrPhone2" value="${company.mgrPhone2}" style="width: 35px;"> - 
                            <input type="text" name="mgrPhone3" value="${company.mgrPhone3}" style="width: 35px;">
                        </td>
                    </tr>
                    <tr>
                        <th>휴대폰번호</th>
                        <td>
                            <select><option>010</option></select> - 
                            <input type="text" name="mgrMobile2" value="${company.mgrMobile2}" style="width: 35px;"> - 
                            <input type="text" name="mgrMobile3" value="${company.mgrMobile3}" style="width: 35px;">
                        </td>
                    </tr>
                    <tr>
                        <th>이메일</th>
                        <td><input type="text" name="mgrEmail" value="${company.mgrEmail}" style="width: 90%;"></td>
                    </tr>
                </table>
            </div>
        </div>

        <!-- ================= 2. 중단 영역 (급여지급정보) ================= -->
        <div style="margin-top: 5px;">
            <div class="section-title">급여지급정보</div>
            <table class="info-table">
                <colgroup>
                    <col width="12%"><col width="28%"><col width="12%"><col width="23%"><col width="10%"><col width="15%">
                </colgroup>
                <tr>
                    <th>급여 산정기간</th>
                    <td>
                        <select><option>당월</option></select> 
                        <select><option>01</option></select> ~ 
                        <select><option>당월</option></select> 
                        <select><option>말일</option></select>
                    </td>
                    <th>급여지급일</th>
                    <td colspan="3">
                        <select><option>익월</option></select> 
                        <select><option>05</option></select> 일
                    </td>
                </tr>
                <tr>
                    <th>금융기관</th>
                    <td><select style="width: 120px;"><option>기업은행</option></select></td>
                    <th>계좌번호</th>
                    <td><input type="text" name="accountNumber" value="${company.accountNumber}" style="width: 90%;"></td>
                    <th>예금주</th>
                    <td><input type="text" name="accountHolder" value="${company.accountHolder}" style="width: 90%;"></td>
                </tr>
                <tr>
                    <th>급여이체뱅킹</th>
                    <td colspan="5" style="background-color: #fefefe;">
                        <div style="color: #d9534f; margin-bottom: 8px;">급여이체 서비스는 KB국민은행 기업뱅킹을 통해 이뤄지고 있습니다.</div>
                        <div style="background-color: #ffffe0; padding: 10px; border: 1px solid #f0e68c; display: inline-block; width: 100%;">
                            기업뱅킹 ID <input type="text" style="width: 120px; margin-right: 15px;">
                            Password <input type="password" style="width: 120px; margin-right: 15px;">
                            <button type="button" class="btn">바로 ERP 연계</button>
                            
                            <div style="margin-top: 10px; font-size: 11px; color: #666;">
                                * <a href="#" style="color: #4a7ab5; text-decoration: none;">국민은행 기업뱅킹 ID/PW 찾기</a> &nbsp;|&nbsp; 
                                * <a href="#" style="color: #4a7ab5; text-decoration: none;">국민은행 CMS 가입하기</a>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

        <!-- ================= 3. 하단 영역 (로고 및 도장) ================= -->
        <div style="display: flex; gap: 80px; margin-top: 10px;">
            
            <!-- 회사 로고 -->
            <div>
                <div class="section-title">회사로고</div>
                
                <input type="hidden" id="deleteLogoFlag" name="deleteLogo" value="false">
                
                <div style="display: flex; gap: 15px; align-items: flex-end;">
                    <div style="width: 160px; height: 90px; border: 1px solid #e1e1e1; display: flex; align-items: center; justify-content: center; background-color: #fff;">
                        <c:choose>
                            <c:when test="${not empty company.logoFileName}">
                                <img id="logoPreview" src="/upload/${company.logoFileName}" style="max-width: 100%; max-height: 100%;">
                                <span id="logoDefaultText" style="display: none; font-weight: bold; font-size: 26px; letter-spacing: -2px;">예스<span style="color:#d9534f">폼</span></span>
                            </c:when>
                            <c:otherwise>
                                <img id="logoPreview" src="" style="display: none; max-width: 100%; max-height: 100%;">
                                <span id="logoDefaultText" style="font-weight: bold; font-size: 26px; letter-spacing: -2px;">예스<span style="color:#d9534f">폼</span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div>
                        <div style="font-size: 11px; color: #666; margin-bottom: 10px; line-height: 1.4;">
                            로고는 가로 150px 썸네일로 생성됩니다.<br>투명 png 이미지 사용을 권장합니다.
                        </div>
                        <button type="button" class="btn" onclick="openModal('logoModal')">등록</button>
                        <button type="button" class="btn-gray" onclick="deleteImage('logoPreview', 'logoDefaultText', 'logoInput', 'deleteLogoFlag')">삭제</button>
                        <button type="button" class="btn-gray">수정요청</button>
                    </div>
                </div>

                <!-- 로고 모달 -->
                <div class="modal-overlay" id="logoModal">
                    <div class="modal-content">
                        <div class="modal-header">이미지 등록하기</div>
                        <div class="modal-body">
                            <input type="file" id="logoInput" name="logoFile" accept="image/*" style="width: 100%;" onchange="previewImage(event, 'logoPreview', 'logoDefaultText', 'deleteLogoFlag')">
                            <div style="margin-top: 15px; text-align: left; font-size: 11px; color: gray; line-height: 1.5;">
                                * 파일 용량 : <span style="color:red;">1MB 미만</span>이어야 합니다.<br>
                                * 파일명 : <span style="color:red;">영문 또는 숫자</span>로 되어 있어야 합니다.
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn" style="padding: 6px 30px;" onclick="closeModal('logoModal')">확인</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 회사 도장 -->
            <div>
                <div style="display: flex; justify-content: space-between; align-items: center; width: 450px;">
                    <div class="section-title" style="margin: 0;">회사도장</div>
                    <button type="button" class="btn" style="margin-bottom: 10px;">수정요청 이력보기</button>
                </div>
                
                <input type="hidden" id="deleteStampFlag" name="deleteStamp" value="false">
                
                <div style="display: flex; gap: 15px; align-items: flex-end;">
                    <div style="width: 100px; height: 100px; border: 1px solid #e1e1e1; display: flex; align-items: center; justify-content: center; background-color: #fff;">
                        <c:choose>
                            <c:when test="${not empty company.stampFileName}">
                                <img id="stampPreview" src="/upload/${company.stampFileName}" style="max-width: 100%; max-height: 100%;">
                                <span id="stampDefaultText" style="display: none; color: #d9534f; font-size: 40px; font-weight: bold;">印</span>
                            </c:when>
                            <c:otherwise>
                                <img id="stampPreview" src="" style="display: none; max-width: 100%; max-height: 100%;">
                                <span id="stampDefaultText" style="color: #d9534f; font-size: 40px; font-weight: bold;">印</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div>
                        <div style="font-size: 11px; color: #666; margin-bottom: 10px; line-height: 1.4;">
                            가로 150px 썸네일, png 파일 권장합니다.<br>
                            무료도장 제공 : <a href="#" style="color: #4a7ab5; text-decoration: none;">stamp.yesform.com</a>
                        </div>
                        <button type="button" class="btn" onclick="openModal('stampModal')">등록</button>
                        <button type="button" class="btn-gray" onclick="deleteImage('stampPreview', 'stampDefaultText', 'stampInput', 'deleteStampFlag')">삭제</button>
                        <button type="button" class="btn-gray">수정요청</button>
                    </div>
                </div>

                <!-- 도장 모달 -->
                <div class="modal-overlay" id="stampModal">
                    <div class="modal-content">
                        <div class="modal-header">이미지 등록하기 (도장)</div>
                        <div class="modal-body">
                            <input type="file" id="stampInput" name="stampFile" accept="image/*" style="width: 100%;" onchange="previewImage(event, 'stampPreview', 'stampDefaultText', 'deleteStampFlag')">
                            <div style="margin-top: 15px; text-align: left; font-size: 11px; color: gray; line-height: 1.5;">
                                * 파일 용량 : <span style="color:red;">1MB 미만</span>이어야 합니다.<br>
                                * 파일명 : <span style="color:red;">영문 또는 숫자</span>로 되어 있어야 합니다.
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn" style="padding: 6px 30px;" onclick="closeModal('stampModal')">확인</button>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <!-- 최하단 영역 (여백 확보용, 저장 버튼이 있다면 여기에 위치) -->
        <div style="margin-top: 50px; padding-bottom: 50px;"></div>

    </form>
</div>

<!-- ================= 자바스크립트 (미리보기, 삭제, 모달 제어) ================= -->
<script>
    function openModal(modalId) {
        document.getElementById(modalId).style.display = 'block';
    }

    function closeModal(modalId) {
        document.getElementById(modalId).style.display = 'none';
    }

    function previewImage(event, imgId, textId, deleteFlagId) {
        var input = event.target;
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                var img = document.getElementById(imgId);
                var text = document.getElementById(textId);
                
                img.src = e.target.result;
                img.style.display = 'inline-block';
                if (text) text.style.display = 'none';
                
                document.getElementById(deleteFlagId).value = "false";
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    function deleteImage(imgId, textId, inputId, deleteFlagId) {
        var img = document.getElementById(imgId);
        var text = document.getElementById(textId);
        var input = document.getElementById(inputId);
        
        img.style.display = 'none';
        img.src = '';
        if (text) text.style.display = 'inline-block';
        
        input.value = '';
        document.getElementById(deleteFlagId).value = "true";
    }
</script>
</body>
</html>