<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>근태그룹 관리</title>
<style>
    body { font-family: 'malgun gothic', sans-serif; padding: 20px; color: #333; margin: 0; }
    .manage-box { width: 280px; margin: 0 auto; }
    h2 { font-size: 20px; font-weight: bold; margin-bottom: 15px; color: #444; }

    /* 목록 영역 */
    .group-container { border: 1px solid #dcdcdc; border-bottom: none; background: #fff; max-height: 280px; overflow-y: auto; }
    .group-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-bottom: 1px solid #eee; font-size: 14px; }
    
    .item-left { display: flex; align-items: center; gap: 8px; flex: 1; }
    .drag-handle { color: #888; font-size: 12px; cursor: move; user-select: none; }
    .group-name-text { color: #333; font-size: 14px; }
    .edit-input { width: 110px; padding: 3px 5px; border: 1px solid #000; border-radius: 3px; font-size: 13px; outline: none; }

    .item-right { font-size: 12px; color: #888; }
    .action-btn { color: #4b7bec; text-decoration: none; cursor: pointer; }
    .action-btn:hover { text-decoration: underline; }
    .btn-divider { color: #ccc; margin: 0 4px; }

    /* 추가하기 하단 바 */
    .add-bar { background-color: #a0a0a0; padding: 8px 12px; text-align: right; border: 1px solid #a0a0a0; cursor: pointer; }
    .add-bar-btn { color: #fff; font-size: 13px; font-weight: bold; background: none; border: none; cursor: pointer; display: inline-flex; align-items: center; gap: 4px; }
    
    /* 안내 문구 및 하단 버튼 */
    .notice-text { font-size: 11px; color: #888; margin-top: 8px; text-align: center; }
    .bottom-btn-group { text-align: center; margin-top: 15px; }
    .btn-reset { background-color: #4183c4; color: #fff; border: none; padding: 8px 30px; border-radius: 6px; font-size: 14px; font-weight: bold; cursor: pointer; }
    .btn-reset:hover { background-color: #326ca6; }
</style>
</head>
<body>

<div class="manage-box">
    <h2>근태그룹 관리</h2>

    <!-- 근태그룹 목록 -->
    <div class="group-container" id="groupContainer">
        <c:forEach var="group" items="${groupList}">
            <div class="group-item" id="item-${group.attendanceGroupId}" data-id="${group.attendanceGroupId}">
                <!-- 일반 상태 -->
                <div class="item-left" id="view-box-${group.attendanceGroupId}">
                    <span class="drag-handle">▲<br>▼</span>
                    <span class="group-name-text" id="name-text-${group.attendanceGroupId}">${group.attendanceGroupName}</span>
                </div>
                <div class="item-right" id="view-btn-${group.attendanceGroupId}">
                    <a class="action-btn" onclick="fnEnableEdit(${group.attendanceGroupId})">수정</a>
                    <span class="btn-divider">|</span>
                    <a class="action-btn" onclick="fnDeleteGroup(${group.attendanceGroupId})">삭제</a>
                </div>

                <!-- 수정 상태 (초기 숨김) -->
                <div class="item-left" id="edit-box-${group.attendanceGroupId}" style="display: none;">
                    <span class="drag-handle">▲<br>▼</span>
                    <input type="text" class="edit-input" id="input-${group.attendanceGroupId}" value="${group.attendanceGroupName}">
                </div>
                <div class="item-right" id="edit-btn-${group.attendanceGroupId}" style="display: none;">
                    <a class="action-btn" onclick="fnSaveUpdate(${group.attendanceGroupId})">저장</a>
                    <span class="btn-divider">|</span>
                    <a class="action-btn" onclick="fnCancelEdit(${group.attendanceGroupId})">취소</a>
                </div>
            </div>
        </c:forEach>

        <!-- 신규 추가 폼 (초기 숨김) -->
        <div class="group-item" id="newItemRow" style="display: none;">
            <div class="item-left">
                <span class="drag-handle">▲<br>▼</span>
                <input type="text" class="edit-input" id="newGroupName" placeholder="그룹명 입력">
            </div>
            <div class="item-right">
                <a class="action-btn" onclick="fnSaveNewGroup()">저장</a>
                <span class="btn-divider">|</span>
                <a class="action-btn" onclick="fnHideAddRow()">취소</a>
            </div>
        </div>
    </div>

    <!-- 추가하기 회색 바 -->
    <div class="add-bar" onclick="fnShowAddRow()">
        <button type="button" class="add-bar-btn">
            <span style="font-size: 14px;">+</span> 추가하기
        </button>
    </div>

    <p class="notice-text">* 드래그로 순서변경이 가능합니다.</p>

    <!-- 초기화 버튼 -->
    <div class="bottom-btn-group">
        <button type="button" class="btn-reset" onclick="fnResetAllGroups()">초기화</button>
    </div>
</div>

<script>
    // 부모 창 부드럽게 갱신
    function refreshParent() {
        if (window.opener && !window.opener.closed) {
            window.opener.location.reload();
        }
    }

    // 1. 수정 모드 전환
    function fnEnableEdit(id) {
        document.getElementById("view-box-" + id).style.display = "none";
        document.getElementById("view-btn-" + id).style.display = "none";
        document.getElementById("edit-box-" + id).style.display = "flex";
        document.getElementById("edit-btn-" + id).style.display = "block";
        document.getElementById("input-" + id).focus();
    }

    // 2. 수정 취소
    function fnCancelEdit(id) {
        document.getElementById("edit-box-" + id).style.display = "none";
        document.getElementById("edit-btn-" + id).style.display = "none";
        document.getElementById("view-box-" + id).style.display = "flex";
        document.getElementById("view-btn-" + id).style.display = "block";
    }

    // 3. 수정 저장 (AJAX)
    function fnSaveUpdate(id) {
        const newName = document.getElementById("input-" + id).value.trim();
        if (!newName) {
            alert("그룹명을 입력해주세요.");
            return;
        }

        sendAjax("${pageContext.request.contextPath}/attendanceGroupUpdate.do", "attendanceGroupId=" + id + "&groupName=" + encodeURIComponent(newName), function() {
            document.getElementById("name-text-" + id).innerText = newName;
            fnCancelEdit(id);
            refreshParent();
        });
    }

    // 4. 단일 항목 삭제 (AJAX)
    function fnDeleteGroup(id) {
        if (!confirm("해당 근태그룹을 삭제하시겠습니까?")) return;

        sendAjax("${pageContext.request.contextPath}/attendanceGroupDelete.do", "attendanceGroupId=" + id, function() {
            const item = document.getElementById("item-" + id);
            if (item) item.remove();
            refreshParent();
        });
    }

    // 5. 추가 폼 보이기 / 숨기기
    function fnShowAddRow() {
        const row = document.getElementById("newItemRow");
        row.style.display = "flex";
        document.getElementById("newGroupName").focus();
    }
    function fnHideAddRow() {
        document.getElementById("newItemRow").style.display = "none";
        document.getElementById("newGroupName").value = "";
    }

    // 6. 신규 추가 저장 (AJAX)
    function fnSaveNewGroup() {
        const name = document.getElementById("newGroupName").value.trim();
        if (!name) {
            alert("그룹명을 입력해주세요.");
            return;
        }

        sendAjax("${pageContext.request.contextPath}/attendanceGroupSave.do", "groupName=" + encodeURIComponent(name), function() {
            refreshParent();
            location.reload();
        });
    }

    // 7. 전체 초기화 (모든 데이터 삭제)
    function fnResetAllGroups() {
        if (!confirm("모든 근태그룹을 초기화(삭제)하시겠습니까?")) return;

        sendAjax("${pageContext.request.contextPath}/attendanceGroupReset.do", "", function() {
            refreshParent();
            location.reload();
        });
    }

    // 공용 AJAX 전송 함수
    function sendAjax(url, params, successCallback) {
        const xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                successCallback();
            }
        };
        xhr.send(params);
    }
</script>

</body>
</html>