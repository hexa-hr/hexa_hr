<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사용자 정보</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; background-color: #f8f9fa; margin: 0; padding: 40px; }
    .wrap { max-width: 1300px; margin: 0 auto; background-color: white; padding: 40px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0,0,0,0.05); }
    h2 { font-size: 22px; color: #333; border-bottom: 2px solid #333; padding-bottom: 15px; margin-bottom: 30px; }
    
    .grid-container { display: grid; grid-template-columns: 6fr 4fr; gap: 30px; }
    .section-title { font-size: 16px; font-weight: bold; margin-bottom: 10px; color: #333; margin-top: 30px;}
    
    /* 🌟 테이블 스타일 (이미지 판박이) */
    .info-table { width: 100%; border-top: 2px solid #4e73df; border-collapse: collapse; text-align: left; font-size: 13px; }
    .info-table th { background-color: #f8f9fa; padding: 10px; border: 1px solid #ddd; width: 25%; color: #333; text-align: center;}
    .info-table td { padding: 8px 10px; border: 1px solid #ddd; }
    
    .info-table input[type="text"], .info-table input[type="password"], .info-table select { padding: 4px; border: 1px solid #ccc; outline: none; }
    .req { color: #e74a3b; margin-right: 3px; font-weight: bold; } /* 빨간 별표 */
    
    .btn-manage { background-color: #4e73df; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; margin-left: 5px; }
    .btn-gray { background-color: #6c757d; color: white; border: none; padding: 4px 10px; border-radius: 3px; cursor: pointer; font-size: 12px; }
    
    .bottom-btns { text-align: center; margin-top: 50px; }
    .btn-save { background-color: #3b71ca; color: white; padding: 10px 40px; border: none; border-radius: 3px; font-size: 15px; font-weight: bold; cursor: pointer; }
    .btn-cancel { background-color: #9e9e9e; color: white; padding: 10px 40px; border: none; border-radius: 3px; font-size: 15px; font-weight: bold; cursor: pointer; margin-left: 10px; }

    /* 로고/도장 박스 */
    .img-box-wrap { display: flex; gap: 40px; margin-top: 10px; }
    .img-box { border: 1px solid #ddd; padding: 20px; display: flex; align-items: center; gap: 20px; width: 400px; }
    .img-placeholder { width: 150px; height: 100px; border: 1px dashed #ccc; display: flex; justify-content: center; align-items: center; background: #fafafa; color: #999; font-size: 12px;}
    .img-desc { font-size: 12px; color: #555; line-height: 1.6; }
    
    /* 모달창 */
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
                <!-- 🌟 회사 정보 (왼쪽 넓은 칸) -->
                <div>
                    <div class="section-title" style="margin-top:0;">회사정보</div>
                    <table class="info-table">
                        <tr>
                            <th><span class="req">*</span>상호</th>
                            <td><input type="text" name="companyName" value="${info.companyName}"></td>
                            <th><span class="req">*</span>대표자직급/대표자</th>
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

                <!-- 🌟 담당자 정보 (오른쪽 좁은 칸) -->
                <div>
                    <div class="section-title" style="margin-top:0;">담당자정보</div>
                    <table class="info-table">
                        <tr><th><span class="req">*</span>성명</th><td><input type="text" name="contactName" value="${info.contactName}" style="width: 90%;"></td></tr>
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

            <!-- 🌟 급여지급정보 -->
            <div class="section-title">급여지급정보</div>
            <table class="info-table">
                <tr>
                    <th>급여 산정기간</th>
                    <td>
                        <select name="payP1"><option>당월</option></select> <select name="payP2"><option>01</option></select> ~ 
                        <select name="payP3"><option>당월</option></select> <select name="payP4"><option>말일</option></select>
                    </td>
                    <th>급여지급일</th>
                    <td colspan="3">
                        <select name="payD1"><option>익월</option></select> <select name="payD2"><option>05</option></select> 일
                    </td>
                </tr>
                <tr>
                    <th>금융기관</th>
                    <td><select name="bankName"><option>기업은행</option><option>국민은행</option></select></td>
                    <th>계좌번호</th>
                    <td><input type="text" name="accountNumber" value="${info.accountNumber}" style="width: 150px;"></td>
                    <th>예금주</th>
                    <td><input type="text" name="accountHolder" value="${info.accountHolder}"></td>
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

            <!-- 🌟 회사로고 / 도장 -->
            <div class="img-box-wrap">
                <div>
                    <div class="section-title" style="margin-top:0;">회사로고</div>
                    <div class="img-box">
                        <div class="img-placeholder">NO IMAGE</div>
                        <div class="img-desc">
                            로고는 가로 150px 썸네일로 생성됩니다.<br>투명 png 이미지 사용을 권장합니다.<br><br>
                            <button type="button" class="btn-manage">등록</button> <button type="button" class="btn-gray">삭제</button>
                        </div>
                    </div>
                </div>
                <div>
                    <div class="section-title" style="margin-top:0;">회사도장</div>
                    <div class="img-box">
                        <div class="img-placeholder">NO IMAGE</div>
                        <div class="img-desc">
                            가로 150px 썸네일, png 파일 권장합니다.<br>무료도장 제공 : stamp.yesform.com<br><br>
                            <button type="button" class="btn-manage">등록</button> <button type="button" class="btn-gray">삭제</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 하단 버튼 -->
            <div class="bottom-btns">
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="cancelEdit()">취소</button>
            </div>
        </form>
    </div>

    <!-- 숨겨둔 투명 폼 (부서/직위 관리용) -->
    <form id="manageForm" method="post" action="<%=request.getContextPath()%>/employee/manageDeptPos.do" style="display:none;">
        <input type="hidden" name="type" id="mType">
        <input type="hidden" name="action" id="mAction">
        <input type="hidden" name="id" id="mId">
        <input type="hidden" name="name" id="mName">
    </form>

    <!-- 부서 모달창 -->
    <div id="deptModal" class="modal-bg">
        <div class="modal-content">
            <h3>부서 설정하기</h3>
            <div class="item-list">
                <c:forEach var="dept" items="${deptList}">
                    <div class="item-row"><span>${dept.name}</span><span><a onclick="manageItem('dept', 'edit', ${dept.id}, '${dept.name}')">수정</a> | <a onclick="manageItem('dept', 'delete', ${dept.id}, '${dept.name}')" style="color: #e74a3b;">삭제</a></span></div>
                </c:forEach>
                <div class="add-input-row" id="deptAddRow"><input type="text" id="deptNewName" placeholder="새 부서명"><span><a class="save-link" onclick="saveNewItem('dept')">저장</a> | <a class="cancel-link" onclick="hideAddRow('dept')">취소</a></span></div>
                <div class="add-btn" id="deptAddBtn" onclick="showAddRow('dept')">+ 추가하기</div>
            </div>
            <button class="modal-close-btn" onclick="closeModal('deptModal')">닫기</button>
        </div>
    </div>

    <!-- 직위 모달창 -->
    <div id="posModal" class="modal-bg">
        <div class="modal-content">
            <h3>직위 설정하기</h3>
            <div class="item-list">
                <c:forEach var="pos" items="${posList}">
                    <div class="item-row"><span>${pos.name}</span><span><a onclick="manageItem('pos', 'edit', ${pos.id}, '${pos.name}')">수정</a> | <a onclick="manageItem('pos', 'delete', ${pos.id}, '${pos.name}')" style="color: #e74a3b;">삭제</a></span></div>
                </c:forEach>
                <div class="add-input-row" id="posAddRow"><input type="text" id="posNewName" placeholder="새 직위명"><span><a class="save-link" onclick="saveNewItem('pos')">저장</a> | <a class="cancel-link" onclick="hideAddRow('pos')">취소</a></span></div>
                <div class="add-btn" id="posAddBtn" onclick="showAddRow('pos')">+ 추가하기</div>
            </div>
            <button class="modal-close-btn" onclick="closeModal('posModal')">닫기</button>
        </div>
    </div>
</body>
</html>