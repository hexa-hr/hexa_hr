<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    
    <form action="saveCompanyInfo.do" method="post" enctype="multipart/form-data">
        
        <!-- ================= 상단 영역 (회사정보 & 담당자정보 나란히) ================= -->
        <div style="display: flex; gap: 30px; align-items: flex-start;">
            
            <!-- 왼쪽: 회사정보 -->
            <div style="flex: 6.5;">
                <h3 class="section-title">회사정보</h3>
                <table class="form-table">
                    <tr>
                        <th style="width: 110px;"><span class="required">*</span>상호</th>
                        <td><input type="text" name="companyName" value="(주)예스폼" class="full-width"></td>
                        <th style="width: 130px;"><span class="required">*</span>대표자직급/대표자</th>
                        <td>
                            <input type="text" name="ceoTitle" value="대표이사" style="width: 70px;"> / 
                            <input type="text" name="ceoName" value="이용열" style="width: 70px;">
                        </td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>사업자번호</th>
                        <td><input type="text" name="businessNumber" value="120-86-50680"></td>
                        <th>법인등록번호</th>
                        <td><input type="text" name="corpNumber" value="110111-275101"></td>
                    </tr>
                    <tr>
                        <th>설립일</th>
                        <td><input type="date" name="foundingDate" value="2000-01-03"></td>
                        <th>홈페이지</th>
                        <td><input type="text" name="homepage" value="www.yesform.com" class="full-width"></td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>사업장 주소</th>
                        <td colspan="3">
                            <input type="text" name="zipCode" value="00133" style="width: 60px; text-align: center; background-color:#f9f9f9;" readonly>
                            <button type="button" class="btn-gray" style="background-color: #eee; color: #333; border: 1px solid #ccc;">우편번호</button>
                            <input type="text" name="address" value="서울특별시 성동구 성수동1가 14-18 코오롱디지털3차 901호" style="width: 320px;">
                        </td>
                    </tr>
                    <tr>
                        <th><span class="required">*</span>전화번호</th>
                        <td>
                            <select name="phone1"><option>대표(없음)</option></select> - 
                            <input type="text" name="phone2" value="1588" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="phone3" value="2390" style="width: 45px; text-align: center;">
                        </td>
                        <th>팩스번호</th>
                        <td>
                            <select name="fax1"><option>서울(02)</option></select> - 
                            <input type="text" name="fax2" value="2117" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="fax3" value="0691" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>업태</th>
                        <td><input type="text" name="businessCondition" value="사업서비스업"></td>
                        <th>종목</th>
                        <td><input type="text" name="businessType" value="온라인정보제공"></td>
                    </tr>
                </table>
            </div>

            <!-- 오른쪽: 담당자정보 -->
            <div style="flex: 3.5;">
                <h3 class="section-title">담당자정보</h3>
                <table class="form-table">
                    <tr>
                        <th style="width: 100px;"><span class="required">*</span>성명</th>
                        <td><input type="text" name="managerName" value="김동현"></td>
                    </tr>
                    <tr>
                        <th>부서</th>
                        <td>
                            <select name="department" style="width: 120px;"><option>기획전략팀</option></select>
                            <button type="button" class="btn">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>직위</th>
                        <td>
                            <select name="position" style="width: 120px;"><option>과장</option></select>
                            <button type="button" class="btn">관리</button>
                        </td>
                    </tr>
                    <tr>
                        <th>전화번호</th>
                        <td>
                            <select name="mgrPhone1"><option>대표(없음)</option></select> - 
                            <input type="text" name="mgrPhone2" value="1588" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="mgrPhone3" value="1588" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>휴대폰번호</th>
                        <td>
                            <select name="mgrMobile1"><option>010</option></select> - 
                            <input type="text" name="mgrMobile2" value="1588" style="width: 45px; text-align: center;"> - 
                            <input type="text" name="mgrMobile3" value="2390" style="width: 45px; text-align: center;">
                        </td>
                    </tr>
                    <tr>
                        <th>이메일</th>
                        <td><input type="text" name="mgrEmail" value="kim_2016@payzon.co.kr" class="full-width"></td>
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
                <td><input type="text" name="accountNumber" value="123-123456-12-123" style="width: 150px;"></td>
                <th style="width: 80px; text-align: center;">예금주</th>
                <td><input type="text" name="accountHolder" value="(주)예스폼" style="width: 100px;"></td>
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
            <button type="button" class="btn-gray btn-large">취소</button>
        </div>

    </form>
</div>

</body>
</html>