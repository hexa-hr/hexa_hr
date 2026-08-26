<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 폰트 및 아이콘 라이브러리 -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@900&family=Noto+Sans+JP:wght@700;900&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<style>
    /* 전체 기본 설정 (헤더에서 공통 적용) */
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { 
        font-family: 'Noto Sans JP', 'Malgun Gothic', sans-serif; 
        background-color: #f4f6f9; 
    }
    
    .header-container {
        width: 100%; 
        min-width: 1400px;
    }

    /* 다크 그라데이션 탑 바 */
    .top-bar { 
        display: flex; 
        justify-content: space-between; 
        align-items: center; 
        background: linear-gradient(180deg, #4f4f4f 0%, #1f1f1f 100%); 
        padding: 10px 24px; 
        color: #ffffff; 
        box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.3); 
    }
    
    .logo-group { display: flex; align-items: baseline; gap: 12px; }
    .logo-en { font-family: 'Montserrat', sans-serif; font-weight: 900; font-size: 24px; color: #ffffff; }
    .logo-jp { font-weight: 700; font-size: 15px; color: #5bc0de; }
    
    .top-right { display: flex; align-items: center; gap: 16px; }
    .user-status { font-size: 13px; color: #cccccc; }
    .user-status strong { color: #ffffff; }
    
    .btn { 
        padding: 6px 14px; border: none; border-radius: 4px; font-weight: bold; 
        font-size: 13px; cursor: pointer; color: #fff; display: flex; 
        align-items: center; gap: 6px; white-space: nowrap; text-decoration: none;
    }
    .btn-favorite { background-color: #e74c3c; }
    .btn-install { background-color: #f39c12; }
    .btn:hover { filter: brightness(1.1); }
</style>

<div class="header-container">
    <div class="top-bar">
        <div class="logo-group">
            <span class="logo-en">HEXA.HR</span>
            <span class="logo-jp">人事＆給与管理</span>
        </div>
        <div class="top-right">
            <span class="user-status"><strong>お客様</strong>は無料体験会員です。</span>
            <a href="#" class="btn btn-favorite"><i class="fa-solid fa-plus"></i> お気に入り</a>
            <a href="#" class="btn btn-install"><i class="fa-solid fa-plus"></i> インストール</a>
        </div>
    </div>
</div>