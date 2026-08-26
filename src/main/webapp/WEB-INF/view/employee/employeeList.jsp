<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>社員名簿 (Employee List)</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">
<link rel="shortcut icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/favicon.ico">

<style>
body {
	margin: 0;
	background-color: #f8f9fa;
	font-family: sans-serif;
}

.wrap {
	max-width: 1200px;
	margin: 0 auto;
	background-color: white;
	border: 1px solid #ddd;
	box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.container {
	padding: 40px;
	box-sizing: border-box;
}

.section-title {
	font-size: 24px;
	font-weight: bold;
	margin-bottom: 20px;
	color: #333;
	border-bottom: 2px solid #4e73df;
	padding-bottom: 10px;
}

.top-controls {
	display: flex;
	justify-content: flex-end;
	margin-bottom: 15px;
}

.top-controls select {
	padding: 5px;
	font-size: 13px;
	border: 1px solid #ccc;
	outline: none;
	cursor: pointer;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 30px;
	text-align: center;
}

th, td {
	border: 1px solid #ddd;
	padding: 12px 5px;
	font-size: 14px;
}

th {
	background-color: #f4f6f9;
	color: #333;
	font-weight: bold;
}

.clickable-row {
	cursor: pointer;
}

.clickable-row:hover {
	background-color: #f1f6ff;
}

.btn-wrap {
	text-align: center;
	margin-top: 20px;
}

.btn-wrap button {
	padding: 10px 20px;
	border: none;
	border-radius: 4px;
	color: white;
	font-weight: bold;
	font-size: 14px;
	cursor: pointer;
	margin: 0 5px;
}

.btn-blue {
	background-color: #4e73df;
}

.btn-gray {
	background-color: #a5a5a5;
}
</style>
<script>
    // 全体選択/解除機能
    function toggleAll(source) {
        let checkboxes = document.getElementsByName('empIds');
        for(let i=0; i<checkboxes.length; i++) {
            checkboxes[i].checked = source.checked;
        }
    }

    // 選択削除前の警告窓
    function confirmDelete() {
        let checked = document.querySelectorAll('input[name="empIds"]:checked');
        if(checked.length === 0) {
            alert("削除する社員を1名以上選択してください。");
            return false;
        }
        return confirm("選択した社員情報を削除しますか？\n(関連するすべての付加情報も一緒に削除されます。)");
    }

    // 件数選択ドロップダウン変更時のURL移動関数
    function changeLimit(selectObj) {
        let limit = selectObj.value;
        location.href = '<%=request.getContextPath()%>
	/employee/list.do?limit='
				+ limit;
	}
</script>
</head>
<body>

	<jsp:include page="/WEB-INF/view/include/header.jsp" />
	<jsp:include page="/WEB-INF/view/include/nav.jsp" />

	<div class="wrap">
		<div class="container">
			<div class="section-title">社員名簿</div>

			<div class="top-controls">
				<select name="limit" onchange="changeLimit(this)">
					<option value="10" ${limit == 10 ? 'selected' : ''}>10件ずつ表示</option>
					<option value="30" ${limit == 30 ? 'selected' : ''}>30件ずつ表示</option>
					<option value="50" ${limit == 50 ? 'selected' : ''}>50件ずつ表示</option>
					<option value="100" ${limit == 100 ? 'selected' : ''}>100件ずつ表示</option>
				</select>
			</div>

			<form action="<%=request.getContextPath()%>/employee/delete.do"
				method="post" onsubmit="return confirmDelete();">
				<table>
					<thead>
						<tr>
							<th><input type="checkbox" id="selectAll"
								onclick="toggleAll(this)"></th>
							<th>社員番号</th>
							<th>氏名</th>
							<th>部署</th>
							<th>役職</th>
							<th>雇用形態</th>
							<th>在職状態</th>
							<th>入社日</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${empty employeeList}">
							<tr>
								<td colspan="8" style="padding: 40px;">登録された社員がいません。</td>
							</tr>
						</c:if>

						<c:forEach var="emp" items="${employeeList}">
							<tr class="clickable-row"
								onclick="location.href='<%=request.getContextPath()%>/employee/register.do?employeeId=${emp.employeeId}'">

								<td onclick="event.stopPropagation();"><input
									type="checkbox" name="empIds" value="${emp.employeeId}">
								</td>

								<td>${emp.employeeId}</td>
								<td style="font-weight: bold;">${emp.koreanName}</td>
								<td>${emp.departmentName != null ? emp.departmentName : '-'}</td>
								<td>${emp.positionName != null ? emp.positionName : '-'}</td>

								<td>${emp.employmentType == '정규직' ? '正社員' : 
                                      (emp.employmentType == '계약직' ? '契約社員' : 
                                      (emp.employmentType == '파견직' ? '派遣社員' : 
                                      (emp.employmentType == '위촉직' ? '業務委託' : 
                                      (emp.employmentType == '임시직' ? '臨時社員' : 
                                      (emp.employmentType == '일용직' ? '日雇い' : emp.employmentType)))))}
								</td>

								<td><span
									style="color: ${emp.status == '재직' ? 'blue' : (emp.status == '퇴직' ? 'red' : 'black')}; font-weight: bold;">

										<c:choose>
											<c:when test="${emp.status == '재직'}">在職</c:when>
											<c:when test="${emp.status == '퇴직'}">退職</c:when>
											<c:otherwise>${emp.status}</c:otherwise>
										</c:choose>
								</span></td>
								<td><fmt:formatDate value="${emp.hireDate}"
										pattern="yyyy-MM-dd" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

				<div class="btn-wrap">
					<button type="button" class="btn-blue"
						onclick="location.href='<%=request.getContextPath()%>/employee/register.do'">新規社員登録</button>
					<button type="submit" class="btn-gray">選択削除</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>