<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員退職処理</title>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">

<style>
	/* 1. 전체 레이아웃 (공통) */
	body {
		margin: 0;
		min-width: 1400px;
		background-color: #f8f9fa;
		font-family: 'Malgun Gothic', sans-serif;
		color: #333;
	}

	.wrap {
		display: flex;
		align-items: flex-start;
		width: 100%;
	}

	.container {
		padding: 30px 40px;
		background-color: white;
		box-sizing: border-box;
		flex: 1;
		min-height: 600px;
		min-width: 0;
	}

	/* 2. 타이틀 영역 */
	.page-header {
		margin-bottom: 10px;
	}

	.page-header h1 {
		font-size: 22px;
		font-weight: bold;
		margin: 0;
		color: #333;
		border-bottom: 2px solid #4e73df;
		padding-bottom: 10px;
	}

	.page-desc {
		font-size: 14px;
		color: #666;
		margin: 0 0 20px 0;
	}

	/* 3. 상단 검색 폼 영역 */
	.filter-bar {
		background: #f4f4f4;
		padding: 15px 20px;
		border: 1px solid #ddd;
		border-radius: 3px;
		margin-bottom: 25px;
		box-sizing: border-box;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.search-left {
		display: flex;
		align-items: center;
		gap: 10px;
	}

	.filter-bar select, .filter-bar input[type="text"] {
		padding: 6px 10px;
		border: 1px solid #ccc;
		border-radius: 3px;
		font-size: 14px;
		outline: none;
	}

	.filter-bar select { width: 120px; }

	.btn-search {
		background-color: #4e73df;
		color: white;
		border: none;
		border-radius: 3px;
		padding: 6px 16px;
		cursor: pointer;
		font-weight: bold;
		font-size: 14px;
	}
	.btn-search:hover { background-color: #2e59d9; }

	.btn-view-all {
		background-color: #a5a5a5;
		color: white;
		border: none;
		border-radius: 3px;
		padding: 6px 16px;
		cursor: pointer;
		font-weight: bold;
		font-size: 14px;
	}
	.btn-view-all:hover { background-color: #858796; }

	/* 4. 데이터 테이블 스타일 */
	.table-container {
		width: 100%;
		overflow-x: auto;
	}

	table.data-table {
		width: 100%;
		border-collapse: collapse;
		text-align: center;
		background: white;
		margin-bottom: 30px;
	}

	table.data-table th, table.data-table td {
		border: 1px solid #ccc;
		padding: 10px 12px;
		font-size: 14px;
		white-space: nowrap;
	}

	table.data-table th {
		background-color: #f8f9fa;
		color: #333;
		font-weight: bold;
	}

	table.data-table tbody tr {
		cursor: pointer;
	}

	table.data-table tbody tr:hover td {
		background-color: #f1f5f9;
	}

	.link-blue { color: #4e73df; text-decoration: none; font-weight: bold; }
	.status-retire { color: #e74a3b; font-weight: bold; }
	
	.icon-box { 
		display: inline-block; 
		width: 22px; 
		height: 22px; 
		line-height: 22px; 
		text-align: center; 
		border-radius: 3px; 
		font-weight: bold; 
		font-size: 12px; 
		color: white; 
	}
	.icon-x { background-color: #a5a5a5; } 
	.icon-o { background-color: #e74a3b; } 

	/* 5. 모달 팝업 CSS */
	.modal-bg { 
		display: none; 
		position: fixed; 
		top: 0; 
		left: 0; 
		width: 100%; 
		height: 100%; 
		background: rgba(0, 0, 0, 0.45); 
		z-index: 1000; 
	}
	
	.modal-content { 
		position: absolute; 
		top: 50%; 
		left: 50%; 
		transform: translate(-50%, -50%); 
		background: white; 
		padding: 30px; 
		border-radius: 5px; 
		width: 450px; 
		box-shadow: 0 5px 15px rgba(0,0,0,0.3); 
		box-sizing: border-box;
	}
	
	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20px;
		border-bottom: 2px solid #4e73df;
		padding-bottom: 8px;
	}

	.modal-header h3 { 
		margin: 0; 
		font-size: 18px; 
		color: #333; 
	}

	.modal-table { 
		width: 100%; 
		text-align: left; 
		margin-bottom: 20px; 
		border-collapse: collapse;
	}
	
	.modal-table th, .modal-table td { 
		border: 1px solid #ccc; 
		padding: 10px; 
		font-size: 14px;
	}
	
	.modal-table th { 
		background-color: #f8f9fa; 
		width: 35%; 
		color: #333;
		font-weight: bold;
	}
	
	.modal-table input, .modal-table select { 
		width: 100%; 
		padding: 6px 10px; 
		border: 1px solid #ccc; 
		border-radius: 3px;
		box-sizing: border-box;
		outline: none;
	}
	
	.modal-btns { 
		text-align: center; 
		margin-top: 20px; 
	}
	
	.btn-save { background-color: #4e73df; color: white; padding: 8px 25px; border: none; border-radius: 3px; cursor: pointer; font-weight: bold;}
	.btn-save:hover { background-color: #2e59d9; }

	.btn-cancel { background-color: #e74a3b; color: white; padding: 8px 25px; border: none; border-radius: 3px; cursor: pointer; font-weight: bold;}
	.btn-cancel:hover { background-color: #c0392b; }

	.btn-close { background-color: #a5a5a5; color: white; padding: 8px 15px; border: none; border-radius: 3px; cursor: pointer; margin-left: 5px; font-weight: bold;}
	.btn-close:hover { background-color: #858796; }
	
	/* 6. 페이지네이션 CSS */
	.pagination { text-align: center; margin-top: 30px; }
	.pagination a { display: inline-block; padding: 6px 12px; margin: 0 2px; border: 1px solid #ccc; color: #333; text-decoration: none; border-radius: 3px; font-size: 14px;}
	.pagination a.active { background-color: #4e73df; color: white; border: 1px solid #4e73df; font-weight: bold; }
	.pagination a:hover:not(.active) { background-color: #f1f1f1; }
</style>
<script>
    function changeFilter(selectObj) {
        location.href = '<%=request.getContextPath()%>/employee/retirement.do?statusFilter=' + selectObj.value;
    }

    function openModal(row) {
        const empId = row.dataset.id;
        const status = row.dataset.status;
        const type = row.dataset.type;
        const date = row.dataset.date;
        const reason = row.dataset.reason;
        const contact = row.dataset.contact;

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
</script>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />
    
    <div class="wrap">
        <div class="container">
            
            <div class="page-header">
                <h1>社員退職処理</h1>
            </div>
            <p class="page-desc">退職対象社員を選択し、退職処理に伴う情報を入力できます。退職社員として分類し、全退職社員を照会できます。</p>

            <div class="filter-bar">
                <div class="search-left">
                    <select name="searchType"><option value="name">氏名</option></select>
                    <input type="text" placeholder="検索語を入力してください。">
                    <button type="button" class="btn-search">検索</button>
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
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>番号</th>
                            <th>状態</th>
                            <th>社員番号</th>
                            <th>氏名</th>
                            <th>部署</th>
                            <th>役職</th>
                            <th>入社日</th>
                            <th>退職日</th>
                            <th>勤続年数</th>
                            <th>中間精算</th>
                            <th>退職精算</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty retireList}">
                            <tr><td colspan="11" style="padding: 40px; text-align: center; color: #777;">照会された社員が存在しません。</td></tr>
                        </c:if>
                        <c:forEach var="emp" items="${retireList}" varStatus="status">
                            <tr class="emp-row"
                                data-id="${emp.employeeId}" data-status="${emp.status}"
                                data-type="${emp.retirementType != null ? emp.retirementType : ''}"
                                data-date="<fmt:formatDate value='${emp.resignationDate}' pattern='yyyy-MM-dd'/>"
                                data-reason="${emp.retirementReason != null ? emp.retirementReason : ''}"
                                data-contact="${emp.contactAfterRetirement != null ? emp.contactAfterRetirement : ''}"
                                onclick="openModal(this)">
                                
                                <td>${(currentPage - 1) * 20 + status.count}</td>
                                <td class="${emp.status == '퇴직' ? 'status-retire' : ''}">${emp.status == '재직' ? '在職' : (emp.status == '퇴직' ? '退職' : emp.status)}</td>
                                <td class="link-blue">No-1400<fmt:formatNumber value="${emp.employeeId}" pattern="00"/></td>
                                <td class="link-blue">${emp.koreanName}</td>
                                <td>${emp.departmentName != null ? emp.departmentName : '-'}</td>
                                <td>${emp.positionName != null ? emp.positionName : '-'}</td>
                                <td><fmt:formatDate value="${emp.hireDate}" pattern="yyyy-MM-dd"/></td>
                                <td><fmt:formatDate value="${emp.resignationDate}" pattern="yyyy-MM-dd"/></td>
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
            </div>

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
    </div>

    <!-- 隠されているモーダルポップアップウィンドウ -->
    <div id="retirementModal" class="modal-bg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modalTitle">退職者退職処理</h3>
                <button type="button" onclick="closeModal()" style="background: none; border: none; font-size: 20px; color: #999; cursor: pointer;">×</button>
            </div>
            
            <form action="<%=request.getContextPath()%>/employee/retirement_process.do" method="post">
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