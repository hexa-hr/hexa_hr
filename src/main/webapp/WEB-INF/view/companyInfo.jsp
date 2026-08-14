<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회사정보 설정</title>
<style>
/* ================= 기본 레이아웃 & 텍스트 스타일 ================= */
body {
	font-family: 'Malgun Gothic', sans-serif;
	font-size: 12px;
	color: #333;
}

.container {
	width: 1100px;
	margin: 0 auto;
	padding: 20px;
}

/* 섹션 제목 */
.section-title {
	font-size: 15px;
	font-weight: bold;
	margin-bottom: 10px;
	color: #333;
}

/* 테이블 공통 디자인 (사진과 동일한 파란색 포인트 테두리) */
.info-table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 30px;
	border-top: 2px solid #5b8db8;
}

.info-table th, .info-table td {
	border: 1px solid #e1e1e1;
	padding: 6px 10px;
	vertical-align: middle;
	height: 32px;
}

.info-table th {
	background-color: #f8f9fa;
	text-align: center;
	color: #333;
	font-weight: normal;
}

.info-table td {
	background-color: #fff;
}

/* 필수항목 빨간 별 */
.req {
	color: red;
	margin-right: 3px;
	font-weight: bold;
}

/* 입력 폼 디자인 */
input[type="text"], input[type="password"] {
	border: 1px solid #ccc;
	padding: 3px 5px;
	height: 20px;
	font-size: 12px;
}

select {
	border: 1px solid #ccc;
	height: 26px;
	padding: 2px;
	font-size: 12px;
}

/* 버튼 디자인 */
.btn {
	display: inline-block;
	padding: 4px 12px;
	background-color: #5b8db8;
	color: white;
	border: none;
	cursor: pointer;
	text-align: center;
	border-radius: 3px;
	font-size: 12px;
}

.btn-gray {
	display: inline-block;
	padding: 4px 12px;
	background-color: #888;
	color: white;
	border: none;
	cursor: pointer;
	text-align: center;
	border-radius: 3px;
	font-size: 12px;
}

.btn-small {
	padding: 2px 8px;
	font-size: 11px;
	background-color: #5b8db8;
	color: white;
	border: none;
	border-radius: 2px;
	cursor: pointer;
}

.btn-white {
	display: inline-block;
	padding: 3px 8px;
	background-color: #fff;
	color: #333;
	border: 1px solid #ccc;
	cursor: pointer;
	border-radius: 3px;
	font-size: 11px;
}

/* ================= 팝업(모달) 디자인 ================= */
.modal-overlay {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 9999;
}

.modal-content {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: #fff;
	width: 350px;
	border-radius: 5px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-header {
	padding: 15px;
	background: #f8f9fa;
	font-weight: bold;
	border-bottom: 1px solid #ddd;
}

.modal-body {
	padding: 20px;
	text-align: center;
}

.modal-footer {
	padding: 15px;
	text-align: center;
	border-top: 1px solid #ddd;
}
</style>
</head>
<body>
	<div class="container">
<!-- 💡 주의: 나중에 로고/도장 이미지 업로드를 구현할 때는 여기에 enctype="multipart/form-data" 를 추가해야 해! -->
    <form action="<%=request.getContextPath()%>/master/companyInfoModify.do" method="post">
        <input type="hidden" name="companyId" value="${companyInfo.companyId}">
        <input type="hidden" name="personId" value="${contactPersonInfo.personId}">
        
        <div style="display: flex; gap: 30px; align-items: flex-start;">
            <!-- 좌측: 회사정보 -->
            <div style="flex: 1;">
                <h3>회사정보</h3>
                <table border="1" style="width: 100%; border-collapse: collapse; text-align: center;">
                    <tr>
                        <th style="background-color: #f8f9fa;">* 상호</th>
                        <td><input type="text" name="companyName" value="${companyInfo.companyName}" required style="width: 90%;"></td>
                        <th style="background-color: #f8f9fa;">* 대표자직급/대표자</th>
                        <td>
                            <input type="text" name="representativeTitle" value="${companyInfo.representativeTitle}" style="width: 40%;"> /
                            <input type="text" name="representativeName" value="${companyInfo.representativeName}" style="width: 40%;">
                        </td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">* 사업자번호</th>
                        <td><input type="text" name="businessNumber" value="${companyInfo.businessNumber}" required style="width: 90%;"></td>
                        <th style="background-color: #f8f9fa;">법인등록번호</th>
                        <td><input type="text" name="corporationNumber" value="${companyInfo.corporationNumber}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">설립일</th>
                        <td>
                            <input type="date" name="establishmentDate" value="<fmt:formatDate value='${companyInfo.establishmentDate}' pattern='yyyy-MM-dd'/>">
                        </td>
                        <th style="background-color: #f8f9fa;">홈페이지</th>
                        <td><input type="text" name="website" value="${companyInfo.website}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">* 사업장 주소</th>
                        <td colspan="3" style="text-align: left; padding-left: 10px;">
                            <input type="text" name="officeAddress" value="${companyInfo.officeAddress}" required style="width: 95%;">
                        </td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">* 전화번호</th>
                        <td><input type="text" name="phoneNumber" value="${companyInfo.phoneNumber}" required style="width: 90%;"></td>
                        <th style="background-color: #f8f9fa;">팩스번호</th>
                        <td><input type="text" name="faxNumber" value="${companyInfo.faxNumber}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">업태</th>
                        <td><input type="text" name="businessType" value="${companyInfo.businessType}" style="width: 90%;"></td>
                        <th style="background-color: #f8f9fa;">종목</th>
                        <td><input type="text" name="businessItem" value="${companyInfo.businessItem}" style="width: 90%;"></td>
                    </tr>
                </table>
            </div>

            <!-- 우측: 담당자정보 -->
            <div style="flex: 0.6;">
                <h3>담당자정보</h3>
                <table border="1" style="width: 100%; border-collapse: collapse; text-align: center;">
                    <tr>
                        <th style="background-color: #f8f9fa;">* 성명</th>
                        <td><input type="text" name="contName" value="${contactPersonInfo.contName}" required style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">부서</th>
                        <td><input type="number" name="deptId" value="${contactPersonInfo.deptId}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">직위</th>
                        <td><input type="number" name="posId" value="${contactPersonInfo.posId}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">전화번호</th>
                        <td><input type="text" name="conPhone" value="${contactPersonInfo.conPhone}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">휴대폰번호</th>
                        <td><input type="text" name="mobile" value="${contactPersonInfo.mobile}" style="width: 90%;"></td>
                    </tr>
                    <tr>
                        <th style="background-color: #f8f9fa;">이메일</th>
                        <td><input type="email" name="email" value="${contactPersonInfo.email}" style="width: 90%;"></td>
                    </tr>
                </table>
            </div>
        </div>

        <br>

        <!-- 하단: 급여지급정보 (화면 구현용) -->
        <h3>급여지급정보</h3>
        <table border="1" style="width: 100%; border-collapse: collapse; text-align: center;">
            <tr>
                <th style="background-color: #f8f9fa; width: 15%;">급여 산정기간</th>
                <td style="width: 35%; text-align: left; padding-left: 10px;">
                    <!-- DB 연동 전, 화면 UI용 가짜 데이터 -->
                    당월 01일 ~ 당월 말일
                </td>
                <th style="background-color: #f8f9fa; width: 15%;">급여지급일</th>
                <td style="width: 35%; text-align: left; padding-left: 10px;">
                    익월 05일
                </td>
            </tr>
            <tr>
                <th style="background-color: #f8f9fa;">금융기관</th>
                <td><input type="text" value="기업은행" style="width: 80%;"></td>
                <th style="background-color: #f8f9fa;">계좌번호 / 예금주</th>
                <td>
                    <input type="text" value="123-123456-12-123" style="width: 50%;"> / 
                    <input type="text" value="(주)예스폼" style="width: 30%;">
                </td>
            </tr>
            <tr>
                <th style="background-color: #f8f9fa;">급여이체뱅킹</th>
                <td colspan="3" style="text-align: left; padding-left: 10px; color: red;">
                    급여이체 서비스는 외부 뱅킹을 통해 이뤄지고 있습니다. (연동 제외)
                </td>
            </tr>
        </table>

        <br>

        <!-- 하단: 회사로고 / 도장 -->
        <div style="display: flex; gap: 50px;">
            <div>
                <h3>회사로고</h3>
                <div style="border: 1px solid #ccc; width: 250px; height: 100px; display: flex; align-items: center; justify-content: center;">
                    <span>로고 이미지 영역</span>
                </div>
                <input type="file" name="companyLogo" style="margin-top: 10px;">
            </div>
            <div>
                <h3>회사도장</h3>
                <div style="border: 1px solid #ccc; width: 150px; height: 100px; display: flex; align-items: center; justify-content: center;">
                    <span>도장 이미지 영역</span>
                </div>
                <input type="file" name="companyStamp" style="margin-top: 10px;">
            </div>
        </div>

        <div style="margin-top: 50px; padding-bottom: 50px; text-align: center;">
            <!-- 저장 버튼을 누르면 action 경로로 데이터가 POST 전송됨 -->
            <button type="submit" style="padding: 10px 30px; background-color: #4e73df; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;">저장</button>
            
            <!-- 취소 버튼: type="reset"으로 설정하면 수정 전의 초기 데이터로 돌아감 -->
            <button type="reset" style="padding: 10px 30px; background-color: #a5a5a5; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-left: 10px;">취소</button>
        </div>
    </form>
</div>
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
					if (text)
						text.style.display = 'none';

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
			if (text)
				text.style.display = 'inline-block';

			input.value = '';
			document.getElementById(deleteFlagId).value = "true";
		}
	</script>
</body>
</html>