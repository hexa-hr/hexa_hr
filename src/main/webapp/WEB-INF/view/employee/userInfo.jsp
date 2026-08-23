<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사용자 정보</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
    body { font-family: 'Malgun Gothic', sans-serif; background-color: #f8f9fa; margin: 0; }
    .wrap { max-width: 1300px; margin: 0 auto; background-color: white; padding: 40px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0,0,0,0.05); }
    h2 { font-size: 22px; color: #333; border-bottom: 2px solid #333; padding-bottom: 15px; margin-bottom: 30px; }
    
    .grid-container { display: grid; grid-template-columns: 6fr 4fr; gap: 30px; }
    .section-title { font-size: 16px; font-weight: bold; margin-bottom: 10px; color: #333; margin-top: 30px;}
    
    .info-table { width: 100%; border-top: 2px solid #4e73df; border-collapse: collapse; text-align: left; font-size: 13px; }
    .info-table th { background-color: #f8f9fa; padding: 10px; border: 1px solid #ddd; width: 20%; color: #333; text-align: center;}
    .info-table td { padding: 8px 10px; border: 1px solid #ddd; }
    
    .info-table input[type="text"], .info-table input[type="password"], .info-table select, .info-table input[type="number"] { padding: 4px; border: 1px solid #ccc; outline: none; }
    .req { color: #e74a3b; margin-right: 3px; font-weight: bold; }
    
    .btn-manage { background-color: #4e73df; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; margin-left: 5px; }
    .btn-gray { background-color: #6c757d; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; }
    
    .bottom-btns { text-align: center; margin-top: 50px; }
    .btn-save { background-color: #3b71ca; color: white; padding: 10px 40px; border: none; border-radius: 3px; font-size: 15px; font-weight: bold; cursor: pointer; }
    .btn-cancel { background-color: #9e9e9e; color: white; padding: 10px 40px; border: none; border-radius: 3px; font-size: 15px; font-weight: bold; cursor: pointer; margin-left: 10px; }

    .img-box-wrap { display: flex; gap: 40px; margin-top: 10px; }
    .img-box { border: 1px solid #ddd; padding: 20px; display: flex; align-items: center; gap: 20px; width: 400px; }
    .img-placeholder { width: 150px; height: 100px; border: 1px dashed #ccc; display: flex; justify-content: center; align-items: center; background: #fafafa; color: #999; font-size: 12px;}
    .img-desc { font-size: 12px; color: #555; line-height: 1.6; }
    
    .modal-bg { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999; }
    .modal-content { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 20px; border-radius: 8px; width: 350px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
    .modal-content h3 { margin-top: 0; font-size: 18px; margin-bottom: 20px; }
    .item-list { border: 1px solid #ddd; max-height: 250px; overflow-y: auto; padding: 10px; margin-bottom: 15px; }
    .item-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px dashed #eee; font-size: 13px; }
    .item-row a { color: #3b71ca; text-decoration: none; cursor: pointer; margin: 0 5px; }
    .add-btn { background-color: #9e9e9e; color: white; text-align: center; padding: 8px; cursor: pointer; font-size: 13px; font-weight: bold; margin-top: 5px; }
    .add-input-row { display: none; padding: 8px 0; border-bottom: 1px dashed #eee; font-size: 13px; justify-content: space-between; align-items: center; }
    .add-input-row input { width: 50%; padding: 4px; border: 2px solid #333; outline: none; }
    .add-input-row a { cursor: pointer; text-decoration: none; font-size: 12px; }
    .add-input-row .save-link { color: #3b71ca; font-weight: bold; margin-right: 5px; }
    .add-input-row .cancel-link { color: #999; }
    .modal-close-btn { width: 100%; padding: 10px; background: #3b71ca; color: white; border: none; border-radius: 3px; cursor: pointer; font-weight: bold; }
</style>
<script>
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const modalToOpen = urlParams.get('openModal');
        if (modalToOpen) openModal(modalToOpen);
    };
    function openModal(modalId) { document.getElementById(modalId).style.display = 'block'; }
    function closeModal(modalId) { document.getElementById(modalId).style.display = 'none'; history.replaceState({}, null, location.pathname); }
    function cancelEdit() { if(confirm("수정한 내용을 취소하시겠습니까?")) location.href = location.pathname; }

    function manageItem(type, action, id, currentName) {
        let form = document.getElementById('manageForm');
        form.type.value = type; form.action.value = action; form.id.value = id;
        if (action === 'edit') {
            let newName = prompt('새로운 이름을 입력하세요:', currentName);
            if (newName && newName.trim() !== '' && newName !== currentName) { form.name.value = newName.trim(); form.submit(); }
        } else if (action === 'delete') {
            if (confirm('[' + currentName + '] 항목을 삭제하시겠습니까?')) form.submit();
        }
    }
    function showAddRow(type) { document.getElementById(type + 'AddBtn').style.display = 'none'; document.getElementById(type + 'AddRow').style.display = 'flex'; }
    function hideAddRow(type) { document.getElementById(type + 'AddBtn').style.display = 'block'; document.getElementById(type + 'AddRow').style.display = 'none'; document.getElementById(type + 'NewName').value = ''; }
    function saveNewItem(type) {
        let newName = document.getElementById(type + 'NewName').value;
        if (newName.trim() === '') { alert('추가할 이름을 입력해주세요.'); return; }
        let form = document.getElementById('manageForm');
        form.type.value = type; form.action.value = 'add'; form.id.value = '0'; form.name.value = newName.trim(); form.submit();
    }
</script>
</head>
<body>
    
    <div class="wrap">
        <h2>사용자 정보</h2>
        
        <form action="<%=request.getContextPath()%>/employee/userInfo.do" method="post">
            <div class="grid-container">
                <div>
                    <div class="section-title" style="margin-top:0;">회사정보</div>
                    <table class="info-table">
                        <tr>
                            <th style="width:25%;"><span class="req">*</span>상호</th>
                            <td><input type="text" name="companyName" value="${info.companyName}"></td>
                            <th style="width:25%;"><span class="req">*</span>대표자직급/대표자</th>
                            <td>
                                <input type="text" name="repTitle" value="${info.repTitle != null ? info.repTitle : '대표이사'}" style="width: 60px;"> / 
                                <input type="text" name="repName" value="${info.repName}" style="width: 80px;">
                            </td>
                        </tr>
                        <tr>
                            <th><span class="req">*</span>사업자번호</th>
                            <td><input type="text" name="businessNumber" value="${info.businessNumber}"></td>
                            <th>법인등록번호</th>
                            <td><input type="text" name="corpNumber" value="${info.corpNumber}"></td>
                        </tr>
                        <tr>
                            <th>설립일</th>
                            <td><input type="text" name="establishmentDate" value="${info.establishmentDate}" placeholder="YYYY-MM-DD"></td>
                            <th>홈페이지</th>
                            <td><input type="text" name="website" value="${info.website}"></td>
                        </tr>
                        <tr>
                            <th><span class="req">*</span>사업장 주소</th>
                            <td colspan="3">
                                <input type="text" name="zipCode" value="${info.zipCode}" style="width: 60px;"> <button type="button" class="btn-gray">우편번호</button> 
                                <input type="text" name="officeAddress" value="${info.officeAddress}" style="width: 300px;">
                            </td>
                        </tr>
                        <tr>
                            <th><span class="req">*</span>전화번호</th>
                            <td>
                                <select name="phone1"><option>대표(없음)</option><option value="02" ${info.phone1 == '02' ? 'selected' : ''}>02</option></select> - 
                                <input type="text" name="phone2" value="${info.phone2}" style="width: 40px;"> - <input type="text" name="phone3" value="${info.phone3}" style="width: 40px;">
                            </td>
                            <th>팩스번호</th>
                            <td>
                                <select name="fax1"><option>선택</option><option value="02" ${info.fax1 == '02' ? 'selected' : ''}>서울(02)</option></select> - 
                                <input type="text" name="fax2" value="${info.fax2}" style="width: 40px;"> - <input type="text" name="fax3" value="${info.fax3}" style="width: 40px;">
                            </td>
                        </tr>
                        <tr>
                            <th>업태</th>
                            <td><input type="text" name="bizType" value="${info.bizType}"></td>
                            <th>종목</th>
                            <td><input type="text" name="bizItem" value="${info.bizItem}"></td>
                        </tr>
                    </table>
                </div>

                <div>
                    <div class="section-title" style="margin-top:0;">담당자정보</div>
                    <table class="info-table">
                        <tr><th style="width:30%;"><span class="req">*</span>성명</th><td><input type="text" name="contactName" value="${info.contactName}" style="width: 90%;"></td></tr>
                        <tr>
                            <th>부서</th>
                            <td>
                                <select name="departmentId" style="width: 60%;">
                                    <option value="">선택</option>
                                    <c:forEach var="dept" items="${deptList}"><option value="${dept.id}" ${info.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option></c:forEach>
                                </select>
                                <button type="button" class="btn-manage" onclick="openModal('deptModal')">관리</button>
                            </td>
                        </tr>
                        <tr>
                            <th>직위</th>
                            <td>
                                <select name="positionId" style="width: 60%;">
                                    <option value="">선택</option>
                                    <c:forEach var="pos" items="${posList}"><option value="${pos.id}" ${info.positionId == pos.id ? 'selected' : ''}>${pos.name}</option></c:forEach>
                                </select>
                                <button type="button" class="btn-manage" onclick="openModal('posModal')">관리</button>
                            </td>
                        </tr>
                        <tr>
                            <th>전화번호</th>
                            <td><select name="cPhone1"><option>선택</option></select> - <input type="text" name="cPhone2" value="${info.cPhone2}" style="width: 40px;"> - <input type="text" name="cPhone3" value="${info.cPhone3}" style="width: 40px;"></td>
                        </tr>
                        <tr>
                            <th>휴대폰번호</th>
                            <td><select name="mobile1"><option value="010">010</option></select> - <input type="text" name="mobile2" value="${info.mobile2}" style="width: 40px;"> - <input type="text" name="mobile3" value="${info.mobile3}" style="width: 40px;"></td>
                        </tr>
                        <tr><th>이메일</th><td><input type="text" name="email" value="${info.email}" style="width: 90%;"></td></tr>
                    </table>
                </div>
            </div>

            <!-- 🌟 급여지급정보 레이아웃 -->
            <div class="section-title">급여지급정보</div>
            <table class="info-table">
                <tr>
                    <th style="width: 15%;">급여 산정기간</th>
                    <td style="width: 35%;">
                        <select name="calc1MonthType" style="padding:4px;">
                            <option value="">선택</option>
                            <option value="P" ${info.calc1MonthType == 'P' ? 'selected' : ''}>전월</option>
                            <option value="N" ${info.calc1MonthType == 'N' || info.calc1MonthType == null ? 'selected' : ''}>당월</option>
                        </select>
                        <select name="salaryCalc1" style="padding:4px;">
                            <option value="">선택</option>
                            <c:forEach var="day" begin="1" end="31">
                                <option value="${day}" ${info.salaryCalc1 == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}일</option>
                            </c:forEach>
                            <!-- 🌟 말일의 value를 0으로 명확히 지정 -->
                            <option value="0" ${info.salaryCalc1 != null && info.salaryCalc1 == 0 ? 'selected' : ''}>말일</option>
                        </select>
                        <span style="margin: 0 10px;">~</span>
                        <select name="calc2MonthType" style="padding:4px;">
                            <option value="">선택</option>
                            <option value="P" ${info.calc2MonthType == 'P' ? 'selected' : ''}>전월</option>
                            <option value="N" ${info.calc2MonthType == 'N' || info.calc2MonthType == null ? 'selected' : ''}>당월</option>
                        </select>
                        <select name="salaryCalc2" style="padding:4px;">
                            <option value="">선택</option>
                            <c:forEach var="day" begin="1" end="31">
                                <option value="${day}" ${info.salaryCalc2 == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}일</option>
                            </c:forEach>
                            <!-- 🌟 말일의 value를 0으로 명확히 지정 -->
                            <option value="0" ${info.salaryCalc2 != null && info.salaryCalc2 == 0 ? 'selected' : ''}>말일</option>
                        </select>
                    </td>
                    
                    <th style="width: 15%;">급여지급일</th>
                    <td style="width: 35%;" colspan="3">
                        <select name="paymentMonthType" style="padding:4px;">
                            <option value="0" ${info.paymentMonthType == '0' ? 'selected' : ''}>당월</option>
                            <option value="1" ${info.paymentMonthType == '1' || info.paymentMonthType == null ? 'selected' : ''}>익월</option>
                        </select>
                        <select name="salaryPaymentDate" style="padding:4px;">
                            <option value="">선택</option>
                            <c:forEach var="day" begin="1" end="31">
                                <option value="${day}" ${info.salaryPaymentDate == day ? 'selected' : ''}>${day < 10 ? '0' : ''}${day}일</option>
                            </c:forEach>
                            <!-- 🌟 말일의 value를 0으로 명확히 지정 -->
                            <option value="0" ${info.salaryPaymentDate != null && info.salaryPaymentDate == 0 ? 'selected' : ''}>말일</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>금융기관</th>
                    <td>
                        <select name="bankName" style="padding:4px;">
                            <option value="">선택해주세요</option>
                            <option value="국민은행" ${info.bankName == '국민은행' ? 'selected' : ''}>국민은행</option>
                            <option value="기업은행" ${info.bankName == '기업은행' ? 'selected' : ''}>기업은행</option>
                            <option value="농협은행" ${info.bankName == '농협은행' ? 'selected' : ''}>농협은행</option>
                            <option value="신한은행" ${info.bankName == '신한은행' ? 'selected' : ''}>신한은행</option>
                            <option value="우리은행" ${info.bankName == '우리은행' ? 'selected' : ''}>우리은행</option>
                            <option value="하나은행" ${info.bankName == '하나은행' ? 'selected' : ''}>하나은행</option>
                        </select>
                    </td>
                    <th>계좌번호</th>
                    <td><input type="text" name="accountNumber" value="${info.accountNumber}" style="width: 80%;"></td>
                    <th>예금주</th>
                    <td><input type="text" name="depositStocks" value="${info.depositStocks}"></td>
                </tr>
                <tr>
                    <th>급여이체뱅킹</th>
                    <td colspan="5">
                        <p style="color:#e74a3b; font-size:12px; margin:0 0 10px 0;">급여이체 서비스는 KB국민은행 기업뱅킹을 통해 이뤄지고 있습니다.</p>
                        <div style="background:#fff9e6; padding:10px; border:1px solid #ffeeba; display:inline-block;">
                            기업뱅킹 ID <input type="text" name="kbId" value="${info.kbId}"> Password <input type="password" name="kbPw" value="${info.kbPw}">
                            <button type="button" class="btn-manage" style="margin-left: 10px;">바로 ERP 연계</button>
                        </div>
                    </td>
                </tr>
            </table>

            <div class="img-box-wrap">
                <div>
                    <div class="section-title" style="margin-top:0;">회사로고</div>
                    <div class="img-box">
                        <div class="img-placeholder">NO IMAGE</div>
                        <div class="img-desc">로고는 가로 150px 썸네일로 생성됩니다.<br>투명 png 이미지 사용을 권장합니다.<br><br><button type="button" class="btn-manage">등록</button> <button type="button" class="btn-gray">삭제</button></div>
                    </div>
                </div>
                <div>
                    <div class="section-title" style="margin-top:0;">회사도장</div>
                    <div class="img-box">
                        <div class="img-placeholder">NO IMAGE</div>
                        <div class="img-desc">가로 150px 썸네일, png 파일 권장합니다.<br>무료도장 제공 : stamp.yesform.com<br><br><button type="button" class="btn-manage">등록</button> <button type="button" class="btn-gray">삭제</button></div>
                    </div>
                </div>
            </div>

            <div class="bottom-btns">
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="cancelEdit()">취소</button>
            </div>
        </form>
    </div>

    <form id="manageForm" method="post" action="<%=request.getContextPath()%>/employee/manageDeptPos.do" style="display:none;">
        <input type="hidden" name="type" id="mType">
        <input type="hidden" name="action" id="mAction">
        <input type="hidden" name="id" id="mId">
        <input type="hidden" name="name" id="mName">
    </form>

    <div id="deptModal" class="modal-bg">
        <div class="modal-content">
            <h3>부서 설정하기</h3>
            <div class="item-list">
                <c:forEach var="dept" items="${deptList}"><div class="item-row"><span>${dept.name}</span><span><a onclick="manageItem('dept', 'edit', ${dept.id}, '${dept.name}')">수정</a> | <a onclick="manageItem('dept', 'delete', ${dept.id}, '${dept.name}')" style="color: #e74a3b;">삭제</a></span></div></c:forEach>
                <div class="add-input-row" id="deptAddRow"><input type="text" id="deptNewName" placeholder="새 부서명"><span><a class="save-link" onclick="saveNewItem('dept')">저장</a> | <a class="cancel-link" onclick="hideAddRow('dept')">취소</a></span></div>
                <div class="add-btn" id="deptAddBtn" onclick="showAddRow('dept')">+ 추가하기</div>
            </div>
            <button class="modal-close-btn" onclick="closeModal('deptModal')">닫기</button>
        </div>
    </div>

    <div id="posModal" class="modal-bg">
        <div class="modal-content">
            <h3>직위 설정하기</h3>
            <div class="item-list">
                <c:forEach var="pos" items="${posList}"><div class="item-row"><span>${pos.name}</span><span><a onclick="manageItem('pos', 'edit', ${pos.id}, '${pos.name}')">수정</a> | <a onclick="manageItem('pos', 'delete', ${pos.id}, '${pos.name}')" style="color: #e74a3b;">삭제</a></span></div></c:forEach>
                <div class="add-input-row" id="posAddRow"><input type="text" id="posNewName" placeholder="새 직위명"><span><a class="save-link" onclick="saveNewItem('pos')">저장</a> | <a class="cancel-link" onclick="hideAddRow('pos')">취소</a></span></div>
                <div class="add-btn" id="posAddBtn" onclick="showAddRow('pos')">+ 추가하기</div>
            </div>
            <button class="modal-close-btn" onclick="closeModal('posModal')">닫기</button>
        </div>
    </div>
</body>
</html>