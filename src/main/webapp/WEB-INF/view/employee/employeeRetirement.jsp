<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員退職処理</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
    body { margin: 0; background-color: #f8f9fa; font-family: 'Malgun Gothic', sans-serif; }
    .wrap { max-width: 1200px; margin: 0 auto; background-color: white; border: 1px solid #ddd; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.05); }
    
    .header-area { display: flex; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
    .header-icon { font-size: 40px; margin-right: 15px; }
    .header-text h2 { margin: 0; font-size: 22px; color: #333; }
    .header-text p { margin: 5px 0 0 0; font-size: 13px; color: #777; }

    .search-area { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    .search-left { display: flex; gap: 5px; }
    .search-left select, .search-left input, .search-right select { padding: 6px; border: 1px solid #ccc; font-size: 13px; outline: none; cursor: pointer; }
    .btn-search { background-color: #4e73df; color: white; border: none; padding: 6px 12px; cursor: pointer; }
    .btn-view-all { background-color: #6c757d; color: white; border: none; padding: 6px 12px; cursor: pointer; }

    table { width: 100%; border-collapse: collapse; text-align: center; }
    th, td { border: 1px solid #ddd; padding: 12px 5px; font-size: 13px; }
    th { background-color: #fcfcfc; font-weight: bold; color: #555; }
    
    .link-blue { color: #3498db; text-decoration: none; }
    .status-retire { color: #e74a3b; font-weight: bold; }
    
    .icon-box { display: inline-block; width: 22px; height: 22px; line-height: 22px; text-align: center; border-radius: 3px; font-weight: bold; font-size: 12px; color: white; }
    .icon-x { background-color: #85c1e9; } 
    .icon-o { background-color: #f1948a; } 

    /* モーダル CSS */
    .modal-bg { display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999; }
    .modal-content { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 30px; border-radius: 8px; width: 400px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
    .modal-content h3 { margin-top: 0; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px; }
    .modal-table { width: 100%; text-align: left; margin-bottom: 20px; }
    .modal-table th { background: none; border: none; padding: 8px 0; border-bottom: 1px solid #eee; width: 35%; color: #333;}
    .modal-table td { background: none; border: none; padding: 8px 0; border-bottom: 1px solid #eee; }
    .modal-table input, .modal-table select { width: 95%; padding: 5px; border: 1px solid #ccc; }
    
    .modal-btns { text-align: center; margin-top: 20px; }
    .btn-save { background-color: #4e73df; color: white; padding: 8px 30px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;}
    .btn-cancel { background-color: #e74a3b; color: white; padding: 8px 30px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;}
    .btn-close { background-color: #999; color: white; padding: 8px 15px; border: none; border-radius: 4px; cursor: pointer; margin-left: 5px;}
    
    /* 🌟 ページングボタン CSS */
    .pagination { text-align: center; margin-top: 30px; }
    .pagination a { display: inline-block; padding: 6px 12px; margin: 0 2px; border: 1px solid #ddd; color: #333; text-decoration: none; border-radius: 3px; font-size: 13px;}
    .pagination a.active { background-color: #e74a3b; color: white; border: 1px solid #e74a3b; font-weight: bold; }
    .pagination a:hover:not(.active) { background-color: #f1f1f1; }
</style>
<script>
    function changeFilter(selectObj) {
        location.href = '<%=request.getContextPath()%>/employee/retirement.do?statusFilter=' + selectObj.value;
    }

    // モーダルウィンドウを表示
    function openModal(row) {
        const empId = row.dataset.id;
        const status = row.dataset.status;
        const type = row.dataset.type;
        const date = row.dataset.date;
        const reason = row.dataset.reason;
        const contact = row.dataset.contact;

        // 🌟 [추가] 해당 행의 입사일 값(7번째 셀)을 읽어와서 저장
        window.selectedEmpHireDate = row.querySelector('td:nth-child(7)').innerText.trim();

        document.getElementById('modalEmpId').value = empId;
        const modal = document.getElementById('retirementModal');
        modal.style.display = 'block';

        if(status === '재직') {
            document.getElementById('modalTitle').innerText = '退職者退職処理';
            document.getElementById('modalAction').value = 'save';
            document.getElementById('actionBtn').className = 'btn-save';
            document.getElementById('actionBtn').innerText = '保存';
            
            document.getElementById('modalType').value = '';
            document.getElementById('modalDate').value = '';
            document.getElementById('modalReason').value = '';
            document.getElementById('modalContact').value = '';
            
            document.getElementById('modalType').disabled = false;
            document.getElementById('modalDate').readOnly = false;
            document.getElementById('modalReason').readOnly = false;
            document.getElementById('modalContact').readOnly = false;
        } else {
            document.getElementById('modalTitle').innerText = '退職者退職処理取消';
            document.getElementById('modalAction').value = 'cancel';
            document.getElementById('actionBtn').className = 'btn-cancel';
            document.getElementById('actionBtn').innerText = '退職取消';
            
            document.getElementById('modalType').value = type;
            document.getElementById('modalDate').value = date;
            document.getElementById('modalReason').value = reason;
            document.getElementById('modalContact').value = contact;
            
            document.getElementById('modalType').disabled = true;
            document.getElementById('modalDate').readOnly = true;
            document.getElementById('modalReason').readOnly = true;
            document.getElementById('modalContact').readOnly = true;
        }
    }

    function closeModal() {
        document.getElementById('retirementModal').style.display = 'none';
    }

    // 🌟 [추가된 유효성 검사 함수] 퇴직일이 입사일보다 빠른지 검증
    function validateRetirementForm() {
        const action = document.getElementById('modalAction').value;
        if (action === 'save') {
            const hireDate = window.selectedEmpHireDate; // 'YYYY-MM-DD'
            const retireDate = document.getElementById('modalDate').value;

            if (hireDate && retireDate && hireDate !== '-') {
                if (retireDate < hireDate) {
                    alert("退職日は入社日より前であってはなりません。");
                    return false;
                }
            }
        }
        return true;
    }
</script>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />
    
    <div class="wrap">
        <div class="header-area">
            <div class="header-icon">💻</div>
            <div class="header-text">
                <h2>社員退職処理</h2>
                <p>退職対象社員を選択し、退職処理に伴う情報を入力できます。退職社員として分類し、全退職社員を照会できます。</p>
            </div>
        </div>

        <div class="search-area">
            <div class="search-left">
                <select name="searchType"><option value="name">氏名</option></select>
                <input type="text" placeholder="検索語を入力してください。">
                <button type="button" class="btn-search">🔍</button>
                <button type="button" class="btn-view-all" onclick="location.href='<%=request.getContextPath()%>/employee/retirement.do'">全体表示</button>
            </div>
            <div class="search-right">
                <select name="statusFilter" onchange="changeFilter(this)">
                    <option value="" ${empty statusFilter ? 'selected' : ''}>状態別</option>
                    <option value="재직" ${statusFilter == '재직' ? 'selected' : ''}>在職</option>
                    <option value="퇴직" ${statusFilter == '퇴직' ? 'selected' : ''}>退職</option>
                </select>
            </div>
        </div>

        <!-- データテーブル -->
        <table>
            <thead>
                <tr>
                    <th>番号</th><th>状態</th><th>社員番号</th><th>氏名</th><th>部署</th><th>役職</th>
                    <th>入社日</th><th>退職日</th><th>勤続年数</th><th>中間精算</th><th>退職精算</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty retireList}">
                    <tr><td colspan="11" style="padding:40px;">照会された社員が存在しません。</td></tr>
                </c:if>
                <c:forEach var="emp" items="${retireList}" varStatus="status">
                    <tr style="cursor: pointer;" class="emp-row"
                        data-id="${emp.employeeId}" data-status="${emp.status}"
                        data-type="${emp.retirementType != null ? emp.retirementType : ''}"
                        data-date="${emp.resignationDate != null ? '' : ''}<fmt:formatDate value='${emp.resignationDate}' pattern='yyyy-MM-dd'/>"
                        data-reason="${emp.retirementReason != null ? emp.retirementReason : ''}"
                        data-contact="${emp.contactAfterRetirement != null ? emp.contactAfterRetirement : ''}"
                        onclick="openModal(this)">
                        
                        <td>${(currentPage - 1) * 20 + status.count}</td>
                        <td class="${emp.status == '퇴직' ? 'status-retire' : ''}">${emp.status == '재직' ? '在職' : (emp.status == '퇴직' ? '退職' : emp.status)}</td>
                        <td class="link-blue">No-1400<fmt:formatNumber value="${emp.employeeId}" pattern="00"/></td>
                        <td class="link-blue">${emp.koreanName}</td>
                        <td class="link-blue">${emp.departmentName != null ? emp.departmentName : '-'}</td>
                        <td class="link-blue">${emp.positionName != null ? emp.positionName : '-'}</td>
                        <td class="link-blue"><fmt:formatDate value="${emp.hireDate}" pattern="yyyy-MM-dd"/></td>
                        <td class="link-blue"><fmt:formatDate value="${emp.resignationDate}" pattern="yyyy-MM-dd"/></td>
                        <td>${emp.yearsOfService}年</td>
                        <td><span class="icon-box icon-x">X</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${emp.retirementSettlement == 'O'}"><span class="icon-box icon-o">O</span></c:when>
                                <c:otherwise><span class="icon-box icon-x">X</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- ページング処理領域 -->
        <div class="pagination">
            <c:if test="${startPage > 1}">
                <a href="?page=${startPage - 1}&statusFilter=${statusFilter}">前へ</a>
            </c:if>
            
            <c:forEach begin="${startPage}" end="${endPage}" var="p">
                <a href="?page=${p}&statusFilter=${statusFilter}" class="${p == currentPage ? 'active' : ''}">${p}</a>
            </c:forEach>
            
            <c:if test="${endPage < totalPages}">
                <a href="?page=${endPage + 1}&statusFilter=${statusFilter}">次へ</a>
            </c:if>
        </div>
    </div>

    <!-- 隠されているモーダルポップアップウィンドウ -->
    <div id="retirementModal" class="modal-bg">
        <div class="modal-content">
            <h3 id="modalTitle">退職者退職処理</h3>
            <form action="<%=request.getContextPath()%>/employee/retirement_process.do" method="post" onsubmit="return validateRetirementForm();">
                <input type="hidden" name="employeeId" id="modalEmpId">
                <input type="hidden" name="action" id="modalAction" value="save">
                
                <table class="modal-table">
                    <tr>
                        <th>退職区分</th>
                        <td>
                            <select name="retirementType" id="modalType" required>
                                <option value="">選択</option>
                                <option value="정년퇴직">定年退職</option>
                                <option value="자발적 퇴직">自己都合退社</option>
                                <option value="임원퇴직">役員退職</option>
                                <option value="중간정산">中間精算</option>
                                <option value="기타">その他</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th>退職日</th>
                        <td><input type="date" name="retirementDate" id="modalDate" required></td>
                    </tr>
                    <tr>
                        <th>退職事由</th>
                        <td><input type="text" name="retirementReason" id="modalReason"></td>
                    </tr>
                    <tr>
                        <th>退職後の連絡先</th>
                        <td><input type="text" name="contactAfterRetirement" id="modalContact"></td>
                    </tr>
                </table>
                <div class="modal-btns">
                    <button type="submit" id="actionBtn" class="btn-save">保存</button>
                    <button type="button" class="btn-close" onclick="closeModal()">閉じる</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>