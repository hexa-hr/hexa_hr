<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>休暇日数設定</title>
<style>
body {
	font-family: 'Malgun Gothic', sans-serif;
	padding: 20px;
	color: #333;
}

h3 {
	margin-bottom: 15px;
	font-size: 20px;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 10px;
}

th, td {
	border: 1px solid #e2e8f0;
	padding: 10px;
	text-align: center;
	font-size: 14px;
}

th {
	background-color: #f8fafc;
	color: #475569;
	font-weight: bold;
}

.msg-box {
	padding: 10px;
	background-color: #d4edda;
	color: #155724;
	border: 1px solid #c3e6cb;
	margin-bottom: 15px;
	border-radius: 4px;
}

/* 하단 버튼 영역 디자인 */
.footer-btn-container {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 20px;
	padding-top: 15px;
}

.left-btn-group, .right-btn-group {
	display: flex;
	gap: 8px;
}

.btn {
	padding: 9px 16px;
	border: none;
	border-radius: 4px;
	font-size: 14px;
	font-weight: bold;
	cursor: pointer;
	display: inline-flex;
	align-items: center;
	gap: 6px;
	color: #fff;
}

.btn-gray {
	background-color: #78828a;
}

.btn-gray:hover {
	background-color: #646d74;
}

.btn-blue {
	background-color: #4a8bf5;
}

.btn-blue:hover {
	background-color: #3576e0;
}

.input-days {
	width: 60px;
	padding: 4px 6px;
	text-align: right;
	border: 1px solid #cbd5e1;
	border-radius: 3px;
}
</style>
</head>
<body>

	<h3>休暇日数設定</h3>

	<c:if test="${param.saved == 'true'}">
		<div class="msg-box">処理完了しました。</div>
	</c:if>

	<form id="vacationForm" method="post">
		<input type="hidden" name="attendanceTypeId"
			value="${attendanceTypeId}">

		<table>
			<thead>
				<tr>
					<!-- 💡 disabled를 제거하고 id="selectAll" 추가 -->
					<th style="width: 40px;"><input type="checkbox" id="selectAll"></th>
					<th>区分</th>
					<th>社員番号</th>
					<th>氏名</th>
					<th>部署</th>
					<th>役職</th>
					<th>入社日</th>
					<th>休暇日数</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${empVacationList}">
					<tr>
						<td>
							<!-- 선택 삭제/저장용 체크박스 --> <input type="checkbox"
							name="selectedEmpId" value="${item.emp.employeeId}">
						</td>
						<td>${item.emp.employmentType}</td>
						<td>No-${item.emp.employeeId} <input type="hidden"
							name="employeeId" value="${item.emp.employeeId}">
						</td>
						<td>${item.emp.koreanName}</td>
						<td>${empty item.departmentName ? '-' : item.departmentName}</td>
						<td>${empty item.positionName ? '-' : item.positionName}</td>
						<td><fmt:formatDate value="${item.emp.hireDate}"
								pattern="yyyy-MM-dd" /></td>
						<td><input type="number" name="vacationDays"
							value="${item.attendanceDays}" class="input-days"> 일</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

<!-- 하단 버튼 영역 -->
		<div class="footer-btn-container">
			<!-- 좌측 버튼 그룹 -->
			<div class="left-btn-group">
				<!-- 💡 type="submit"에서 type="button"으로 변경하고 id 부여 -->
				<button type="button" id="deleteZeroBtn" class="btn btn-gray">
					✕ 休暇日数削除</button>

				<button type="submit" class="btn btn-gray"
					formaction="${pageContext.request.contextPath}/vacationDaysManage.do">
					✚ 休暇日数保存</button>
			</div>

			<!-- 우측 버튼 그룹 -->
			<div class="right-btn-group">
				<button type="submit" class="btn btn-blue"
					formaction="${pageContext.request.contextPath}/vacationDaysAutoCalc.do">
					✚ 休暇日数自動計算</button>
			</div>
		</div>
	</form>

	<!-- 💡 전체 선택 및 0일 변경 스크립트 -->
	<script>
		// 1. 전체 선택/해제 기능
		document.getElementById('selectAll').addEventListener('change', function() {
			const isChecked = this.checked;
			const checkboxes = document.querySelectorAll('input[name="selectedEmpId"]');

			checkboxes.forEach(function(checkbox) {
				checkbox.checked = isChecked;
			});
		});

		// 2. '휴가일수 삭제' 버튼 클릭 시 체크된 행의 휴가일수를 0으로 변경
		document.getElementById('deleteZeroBtn').addEventListener('click', function() {
			const checkboxes = document.querySelectorAll('input[name="selectedEmpId"]:checked');

			if (checkboxes.length === 0) {
				alert('対象を選択してください。.');
				return;
			}

			// 체크된 각 행을 찾아 휴가일수 입력값을 '0'으로 변경
			checkboxes.forEach(function(checkbox) {
				const tr = checkbox.closest('tr');
				const daysInput = tr.querySelector('input[name="vacationDays"]');
				if (daysInput) {
					daysInput.value = 0;
				}
			});
		});
	</script>

</body>
</html>