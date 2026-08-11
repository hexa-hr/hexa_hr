<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>근태그룹 관리</title>
<style>
    body { font-family: 'malgun gothic', sans-serif; padding: 20px; color: #333; margin: 0; }
    .manage-box { width: 280px; margin: 0 auto; text-align: left; }
    .manage-box h2 { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #444; margin-top: 0; }
    .group-container { border: 1px solid #dcdcdc; border-bottom: none; background: #fff; max-height: 250px; overflow-y: auto; }
    .group-item { display: flex; align-items: center; justify-content: space-between; padding: 10px 12px; border-bottom: 1px solid #eee; font-size: 14px; }
    .item-left { display: flex; align-items: center; gap: 8px; flex: 1; }
    .drag-handle { color: #888; font-size: 12px; line-height: 1; }
    .edit-input { width: 110px; padding: 3px 5px; border: 1px solid #000; border-radius: 3px; font-size: 13px; outline: none; }
    .action-btn { color: #4b7bec; text-decoration: none; font-size: 12px; cursor: pointer; }
    .btn-divider { color: #ccc; margin: 0 2px; }
    .add-bar { background-color: #a0a0a0; padding: 8px 12px; text-align: right; }
    .add-bar-btn { color: #fff; font-size: 13px; font-weight: bold; text-decoration: none; display: inline-block; }
    .notice-text { font-size: 11px; color: #888; margin-top: 8px; text-align: center; }
    .bottom-btn-group { text-align: center; margin-top: 15px; }
    .btn-reset { background-color: #4183c4; color: #fff; border: none; padding: 8px 30px; border-radius: 6px; font-size: 14px; font-weight: bold; text-decoration: none; display: inline-block; }
</style>
</head>
<body>

<div class="manage-box">
    <h2>근태그룹 관리</h2>

    <div class="group-container">
        <!-- Handler에서 건네주는 groupList 목록 조회 -->
        <c:forEach var="group" items="${groupList}">
            <div class="group-item">
                <c:choose>
                    <c:when test="${param.mode == 'edit' && param.editId == group.attendanceGroupId}">
                        <!-- 수정 모드 -->
                        <form action="${pageContext.request.contextPath}/attendanceGroupUpdate.do" method="post" style="display:flex; width:100%; justify-content:space-between; align-items:center;">
                            <input type="hidden" name="attendanceGroupId" value="${group.attendanceGroupId}">
                            <div class="item-left">
                                <span class="drag-handle">▲<br>▼</span>
                                <input type="text" name="groupName" class="edit-input" value="${group.attendanceGroupName}">
                            </div>
                            <div>
                                <button type="submit" class="action-btn" style="background:none; border:none; padding:0;">저장</button>
                                <span class="btn-divider">|</span>
                                <a href="${pageContext.request.contextPath}/attendanceGroupManage.do" class="action-btn">취소</a>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <!-- 일반 모드 -->
                        <div class="item-left">
                            <span class="drag-handle">▲<br>▼</span>
                            <span>${group.attendanceGroupName}</span>
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/attendanceGroupManage.do?mode=edit&editId=${group.attendanceGroupId}" class="action-btn">수정</a>
                            <span class="btn-divider">|</span>
                            <a href="${pageContext.request.contextPath}/attendanceGroupDelete.do?attendanceGroupId=${group.attendanceGroupId}" class="action-btn">삭제</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:forEach>

        <!-- 추가 모드 -->
        <c:if test="${param.mode == 'add'}">
            <div class="group-item">
                <form action="${pageContext.request.contextPath}/attendanceGroupSave.do" method="post" style="display:flex; width:100%; justify-content:space-between; align-items:center;">
                    <div class="item-left">
                        <span class="drag-handle">▲<br>▼</span>
                        <input type="text" name="groupName" class="edit-input" placeholder="그룹명 입력">
                    </div>
                    <div>
                        <button type="submit" class="action-btn" style="background:none; border:none; padding:0;">저장</button>
                        <span class="btn-divider">|</span>
                        <a href="${pageContext.request.contextPath}/attendanceGroupManage.do" class="action-btn">취소</a>
                    </div>
                </form>
            </div>
        </c:if>
    </div>

    <!-- 추가하기 버튼 -->
    <div class="add-bar">
        <a href="${pageContext.request.contextPath}/attendanceGroupManage.do?mode=add" class="add-bar-btn">+ 추가하기</a>
    </div>

    <p class="notice-text">* 드래그로 순서변경이 가능합니다.</p>

    <!-- 초기화 버튼 -->
    <div class="bottom-btn-group">
        <a href="${pageContext.request.contextPath}/attendanceGroupReset.do" class="btn-reset">초기화</a>
    </div>
</div>

</body>
</html>