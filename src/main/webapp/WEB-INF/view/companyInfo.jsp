<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- ★ JSTL 라이브러리 추가 -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>급여관리 자동화 - 회사정보 설정</title>
<style>
    /* 전체 폰트 및 기본 스타일 */
    body { font-family: 'Malgun Gothic', sans-serif; font-size: 12px; color: #333; }
    
    /* 섹션 제목 */
    .section-title { font-size: 15px; font-weight: bold; margin-bottom: 10px; margin-top: 30px; }
    
    /* 테이블 공통 스타일 */
    .form-table { width: 100%; border-collapse: collapse; border-top: 2px solid #5d8ebf; }
    .form-table th, .form-table td { border: 1px solid #e0e0e0; padding: 7px 10px; }
    .form-table th { background-color: #f7f9fc; text-align: left; font-weight: normal; }
    
    /* 입력 양식 스타일 */
    .required { color: red; font-weight: bold; margin-right: 3px; }
    input[type="text"], input[type="password"], input[type="date"], select { 
        border: 1px solid #ccc; padding: 4px; border-radius: 2px; font-family: 'Malgun Gothic', sans-serif; 
    }
    .full-width { width: 90%; }
    
    /* 버튼 스타일 */
    .btn { background-color: #5d8ebf; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; }
    .btn-gray { background-color: #999; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; }
    .btn-large { padding: 8px 30px; font-size: 14px; font-weight: bold; margin: 0 5px; }
</style>
</head>
<body>

<div style="width: 1100px; margin: 0 auto; padding-bottom: 50px;">
    
<form action="saveCompanyInfo.do" method="post">  
<input type="file" id="mainLogoInput" name="logoFile" hidden>
        <input type="file" id="mainStampInput" name="stampFile" hidden>
        <input type="hidden" id="deleteLogoFlag" name="deleteLogo" value="false">
        <input type="hidden" id="deleteStampFlag" name="deleteStamp" value="false">
              
        <!-- ================= 상단 영역 (회사정보 & 담당자정보 나란히) ================= -->
        <div style="display: flex; gap: 30px; align-items: flex-start;">
            
            <!-- 왼쪽: 회사정보 -->
            <div style="flex: 6.5;">
                <h3 class="section-title">회사정보</h3>
                <table class="form-table">
                    <tr>
                        <th style="width: 110px;"><span class="required">*</span>상호</th>
                        <td><input type="text" name="companyName" value="${company.companyName}" class="full-width"></td>
                        <th style="width: 130px;"><span class="required">*</span>대표자직급/대표자</th>
                        <td>
                            <input type="text" name="ceoTitle" value="${company.ceoTitle}" style="width: 70px;"> / 
                            <input type="text" name="ceoName" value="${company.ceoName}" style="width: 70px;">
                        </td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>사업자번호</th>
                        <td><input type="text" name="businessNumber" value="${company.businessNumber}"></td>
                        <th>법인등록번호</th>
                        <td><input type="text" name="corpNumber" value="${company.corpNumber}"></td>
                    </tr>
                    <tr>
                        <th>설립일</th>
                        <td><input type="date" name="foundingDate" value="${company.foundingDate}"></td>
                        <th>홈페이지</th>
                        <td><input type="text" name="homepage" value="${company.homepage}" class="full-width"></td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>사업장 주소</th>
                        <td colspan="3">
                            <input type="text" name="zipCode" value="${company.zipCode}" style="width: 60px; text-align: center; background-color:#f9f9f9;" readonly>
                            <button type="button" class="btn-gray" style="background-color: #eee; color: #333; border: 1px solid #ccc;">우편번호</button>
                            <input type="text" name="address" value="${company.address}" style="width: 320px;">
                        </td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>전화번호</th>
                        <td>
                            <select name="phone1"><option>대표(없음)</option></select> - 
                            <input type="text" name="phone2" value="${company.phone2}" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="phone3" value="${company.phone3}" style="width: 45px; text-align: center;">
                        </td>
                        <th>팩스번호</th>
                        <td>
                            <select name="fax1"><option>서울(02)</option></select> - 
                            <input type="text" name="fax2" value="${company.fax2}" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="fax3" value="${company.fax3}" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>업태</th>
                        <td><input type="text" name="businessCondition" value="${company.businessCondition}"></td>
                        <th>종목</th>
                        <td><input type="text" name="businessType" value="${company.businessType}"></td>
                    </tr>
                </table>
            </div>

            <!-- 오른쪽: 담당자정보 -->
            <div style="flex: 3.5;">
                <h3 class="section-title">담당자정보</h3>
                <table class="form-table">
                    <tr>
                        <th style="width: 100px;"><span class="required">*</span>성명</th>
                        <td><input type="text" name="managerName" value="${company.managerName}"></td>
                    </tr>
                    <tr>
                        <th>부서</th>
                        <td>
                            <select name="department" style="width: 120px;">
                                <option value="">선택하세요</option>
                                <c:forEach var="dept" items="${departmentList}">
                                    <option value="${dept.departmentId}">${dept.departmentName}</option>
                                </c:forEach>
                            </select>
                            <button type="button" class="btn" onclick="window.open('departmentManage.do', 'departmentPopup', 'width=400,height=500,scrollbars=yes');">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>직위</th>
                        <td>
                            <select name="position" style="width: 120px;">
                                <option value="">선택하세요</option>
                                <c:forEach var="pos" items="${positionList}">
                                    <option value="${pos.positionId}">${pos.positionName}</option>
                                </c:forEach>
                            </select>
                            <button type="button" class="btn" onclick="window.open('positionManage.do', 'positionPopup', 'width=400,height=500,scrollbars=yes');">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>전화번호</th>
                        <td>
                            <select name="mgrPhone1"><option>대표(없음)</option></select> - 
                            <input type="text" name="mgrPhone2" value="${company.mgrPhone2}" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="mgrPhone3" value="${company.mgrPhone3}" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>휴대폰번호</th>
                        <td>
                            <select name="mgrMobile1"><option>010</option></select> - 
                            <input type="text" name="mgrMobile2" value="${company.mgrMobile2}" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="mgrMobile3" value="${company.mgrMobile3}" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>이메일</th>
                        <td><input type="text" name="mgrEmail" value="${company.mgrEmail}" class="full-width"></td>
                    </tr>
                </table>
            </div>

        </div>
        <!-- ================= 상단 영역 끝 ================= -->


        <!-- ================= 중단 영역 (급여지급정보) ================= -->
        <h3 class="section-title">급여지급정보</h3>
        <table class="form-table">
            <tr>
                <th style="width: 110px;">급여 산정기간</th>
                <td colspan="3">
                    <select name="calcPeriodStart1"><option>당월</option></select>
                    <select name="calcPeriodStart2"><option>01</option></select> ~ 
                    <select name="calcPeriodEnd1"><option>당월</option></select>
                    <select name="calcPeriodEnd2"><option>말일</option></select>
                </td>
                <th style="width: 100px; text-align: center;">급여지급일</th>
                <td>
                    <select name="payDate1"><option>익월</option></select>
                    <select name="payDate2"><option>05</option></select> 일
                </td>
            </tr>
            <tr>
                <th>금융기관</th>
                <td>
                    <select name="bank"><option>기업은행</option></select>
                </td>
                <th style="width: 80px; text-align: center;">계좌번호</th>
                <td><input type="text" name="accountNumber" value="${company.accountNumber}" style="width: 150px;"></td>
                <th style="width: 80px; text-align: center;">예금주</th>
                <td><input type="text" name="accountHolder" value="${company.accountHolder}" style="width: 100px;"></td>
            </tr>
            <tr>
                <th>급여이체뱅킹</th>
                <td colspan="5">
                    <div style="color: #d9534f; margin-bottom: 8px; font-size: 12px;">급여이체 서비스는 KB국민은행 기업뱅킹을 통해 이뤄지고 있습니다.</div>
                    <div style="background-color: #fffaf0; padding: 10px; border: 1px solid #faebcc;">
                        기업뱅킹 ID <input type="text" name="bankingId" style="width: 120px; margin-right: 15px; margin-left: 5px;">
                        Password <input type="password" name="bankingPw" style="width: 120px; margin-right: 15px; margin-left: 5px;">
                        <button type="button" class="btn">바로 ERP 연계</button>
                        
                        <div style="margin-top: 8px; font-size: 11px; color: #666;">
                            * 국민은행 <a href="#" style="color: #5d8ebf; text-decoration: none;">기업뱅킹 ID/PW 찾기</a> &nbsp;&nbsp;|&nbsp;&nbsp;
                            * 국민은행 <a href="#" style="color: #5d8ebf; text-decoration: none;">CMS 가입하기</a>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        <!-- ================= 중단 영역 끝 ================= -->


        <!-- ================= 하단 로고 및 도장 영역 ================= -->
        <div style="display: flex; gap: 80px; margin-top: 30px;">
            <!-- 회사로고 -->
            <div>
                <h3 class="section-title" style="margin-top: 0;">회사로고</h3>
                <div style="display: flex; gap: 15px; align-items: flex-end;">
                    <div style="width: 150px; height: 80px; border: 1px solid #ccc; display: flex; align-items: center; justify-content: center;">
                        <span style="font-weight: bold; font-size: 24px; letter-spacing: -2px;">예스<span style="color:#d9534f">폼</span></span>
                    </div>
                    <div>
                        <div style="font-size: 11px; color: #666; margin-bottom: 10px; line-height: 1.4;">
                            로고는 가로 150px 썸네일로 생성됩니다.<br>투명 png 이미지 사용을 권장합니다.
                        </div>
                        <button type="button" class="btn">등록</button>
                        <button type="button" class="btn-gray">삭제</button>
                        <button type="button" class="btn-gray">수정요청</button>
                    </div>
                </div>
            </div>

            <!-- 회사도장 -->
            <div>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                    <h3 class="section-title" style="margin: 0;">회사도장</h3>
                    <button type="button" class="btn" style="background-color: #4a7ab5;">수정요청 이력보기</button>
                </div>
                <div style="display: flex; gap: 15px; align-items: flex-end;">
                    <div style="width: 100px; height: 100px; border: 1px solid #ccc; background-color: #fff; display: flex; align-items: center; justify-content: center; color: red; font-size: 30px; font-weight: bold;">
                        印
                    </div>
                    <div>
                        <div style="font-size: 11px; color: #666; margin-bottom: 10px; line-height: 1.4;">
                            가로 150px 썸네일, png 파일 권장합니다.<br>
                            무료도장 제공 : <a href="#" style="color: #4a7ab5;">stamp.yesform.com</a>
                        </div>
                        <button type="button" class="btn">등록</button>
                        <button type="button" class="btn-gray">삭제</button>
                        <button type="button" class="btn-gray">수정요청</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- ================= 최하단 저장/취소 버튼 ================= -->
        <div style="text-align: center; margin-top: 50px;">
            <button type="submit" class="btn btn-large" style="background-color: #3b74b8;">저장</button>
            <button type="button" class="btn-gray btn-large" onclick="location.href='companyInfo.do'">취소</button>
        </div>

    </form>
</div>

</body>
</html>