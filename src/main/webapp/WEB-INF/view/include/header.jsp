<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>

.header-container {
    font-family: 'Malgun Gothic', sans-serif;
    min-width: 1400px;
}

.top-header {
	background-color: #2b2b2b; /* 검은색 배경 */
	color: #ffffff;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 20px;
}

.top-header .logo-area h1 {
	margin: 0;
	font-size: 24px;
	color: #fff;
}

.top-header .logo-area span {
	color: #10b981; /* 글자 옆 초록색 포인트 색상 */
	font-weight: normal;
	font-size: 18px;
}

.top-header .user-menu {
	display: flex;
	align-items: center;
	font-size: 13px;
}

.top-header .user-menu span {
	margin-right: 15px;
}

.top-header .user-menu a {
	color: #cccccc;
	text-decoration: none;
	margin-left: 10px;
	margin-right: 10px;
}

.top-header .user-menu a:hover {
	color: #ffffff;
}

.top-header .user-menu .btn-shortcut {
	background-color: #ff4d4d; /* 붉은색 버튼 */
	color: white;
	padding: 3px 8px;
	border-radius: 3px;
	margin-left: 5px;
}

.top-header .user-menu .btn-install {
	background-color: #ff9900; /* 오렌지색 버튼 */
	color: white;
	padding: 3px 8px;
	border-radius: 3px;
	margin-left: 5px;
}
</style>

<div class="header-container">
    <div class="top-header">
        <div class="logo-area">
            <!-- 로고 이미지가 있다면 <img> 태그 사용, 여기서는 텍스트로 임시 대체 -->
            <h1>
                HEXA.HR <span>人事＆給与管理</span>
            </h1>
        </div>
    
        <div class="user-menu">
            <span><strong>お客</strong>様は無料体験会員です。</span> <a href="#"
                class="btn-shortcut"><i class="fa-solid fa-plus"></i>お気に入り</a> 
                <a href="#" class="btn-install"><i class="fa-solid fa-plus"></i> インストール</a>
        </div>
    </div>
</div>