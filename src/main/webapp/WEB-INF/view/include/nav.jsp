<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    .nav-container {
        width: 100%;
        min-width: 1400px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1); 
    }

    /* 1뎁스: 아이콘 네비게이션 */
    .icon-nav { 
        background-color: #ffffff; 
        padding: 18px 28px; 
        border-bottom: 1px solid #e0e0e0; 
    }
    .icon-nav ul { 
        display: flex; list-style: none; gap: 38px; align-items: center; 
        white-space: nowrap; margin: 0; padding: 0;
    }
    .icon-nav a { 
        display: flex; flex-direction: column; align-items: center; text-decoration: none; 
        color: #333333; font-size: 14px; font-weight: 600; gap: 8px; transition: color 0.2s; 
    }
    .icon-nav i { font-size: 26px; color: #444444; transition: color 0.2s; }
    .icon-nav a:hover, .icon-nav a:hover i { color: #3b71ca; }
    .icon-nav a.home-icon i { color: #4e73df; } /* 홈 버튼 포인트 컬러 */

    /* 2뎁스: 블루 그라데이션 서브 헤더 (하단 검은줄 제거됨) */
    .custom-sub-header {
        background: linear-gradient(180deg, #507bf2 0%, #3a5ec9 100%); 
        padding: 0 20px; 
        box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.35), 0 2px 5px rgba(0, 0, 0, 0.15); 
    }
    .custom-sub-header ul { 
        list-style: none; margin: 0; padding: 0; display: flex; 
        align-items: center; white-space: nowrap; 
    }
    .custom-sub-header > ul > li { 
        border-right: 1px solid rgba(0, 0, 0, 0.35); position: relative; 
    }
    .custom-sub-header > ul > li:last-child { border-right: none; }
    
    .custom-sub-header > ul > li > a {
        color: #ffffff; text-decoration: none; font-size: 15px; font-weight: bold;
        padding: 10px 20px; display: inline-block; text-shadow: 0 1px 2px rgba(0, 0, 0, 0.4);
        transition: background-color 0.2s;
    }
    .custom-sub-header > ul > li > a:hover {
        background-color: rgba(0, 0, 0, 0.15);
    }

    /* 드롭다운 메뉴 스타일 */
    .dropdown-content {
        display: none; position: absolute; top: 100%; left: 0; background-color: #ffffff;
        min-width: 200px; box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2); z-index: 1000;
        border-top: 3px solid #3a5ec9; border-radius: 0 0 4px 4px;
    }
    .dropdown-content a {
        color: #333333; padding: 12px 16px; text-decoration: none; display: block;
        font-size: 13px; font-weight: normal; border-bottom: 1px solid #f1f1f1; 
        text-shadow: none; transition: all 0.2s;
    }
    .dropdown-content a:hover {
        background-color: #f8f9fa; color: #3a5ec9; font-weight: bold; padding-left: 20px; 
    }
    
    .custom-sub-header > ul > li:hover .dropdown-content {
        display: block;
    }
</style>

<div class="nav-container">
    <!-- 1뎁스: 아이콘 네비게이션 -->
    <div class="icon-nav">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/employee/userInfo.do" class="home-icon">
                    <i class="fa-solid fa-house"></i><span>HOME</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/employee/register.do">
                    <i class="fa-solid fa-user-plus"></i><span>社員登録</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/employee/list.do">
                    <i class="fa-solid fa-users"></i><span>社員状況</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/attendance/manage.do">
                    <i class="fa-solid fa-calendar-check"></i><span>勤怠記録・管理</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/vacationList.do">
                    <i class="fa-solid fa-calendar-minus"></i><span>休暇照会</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/wage/paymentInput.do">
                    <i class="fa-solid fa-calculator"></i><span>給与入力・管理</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">
                    <i class="fa-solid fa-hard-hat"></i><span>給与入力（日雇い）</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/wage/ledger.do">
                    <i class="fa-solid fa-file-invoice-dollar"></i><span>給与台帳</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/wage/employeeHistory.do">
                    <i class="fa-solid fa-money-check-dollar"></i><span>社員別給与明細</span>
                </a>
            </li>
        </ul>
    </div>

    <!-- 2뎁스: 서브 헤더 (드롭다운 포함) -->
    <div class="custom-sub-header">
        <ul>
            <li>
                <a href="#">基本環境設定 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/employee/userInfo.do">ユーザー情報</a> 
                    <a href="${pageContext.request.contextPath}/employee/register.do">社員登録</a> 
                    <a href="${pageContext.request.contextPath}/vacationTypeSetting.do">休暇・勤怠設定</a> 
                    <a href="${pageContext.request.contextPath}/wageTypeSetting.do">給与項目設定</a>
                </div>
            </li>
            <li>
                <a href="#">人事管理 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/employee/list.do">従業員状況・管理</a>
                </div>
            </li>
            <li>
                <a href="#">勤怠管理 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/attendance/manage.do">勤怠記録・管理</a>
                    <a href="${pageContext.request.contextPath}/attendance/monthly.do">勤怠照会</a> 
                    <a href="${pageContext.request.contextPath}/vacationList.do">休暇照会</a>
                    <a href="${pageContext.request.contextPath}/dailywork/manage.do">日雇い勤務記録・管理</a>
                    <a href="${pageContext.request.contextPath}/dailywork/monthly.do">日雇い勤務照会</a>
                </div>
            </li>
            <li>
                <a href="#">給与管理 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/wage/paymentInput.do">給与入力・管理</a> 
                    <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">給与入力・管理（日雇い）</a> 
                    <a href="${pageContext.request.contextPath}/wage/ledger.do">給与台帳</a> 
                    <a href="${pageContext.request.contextPath}/wage/employeeHistory.do">社員別給与明細</a> 
                    <a href="${pageContext.request.contextPath}/wage/itemLedger.do">項目別台帳</a> 
                    <a href="${pageContext.request.contextPath}/wage/insuranceDeduction.do">社会保険控除明細</a>
                </div>
            </li>
            <li>
                <a href="#">給与統計 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/wage/yearlyTotalStatistics.do">年度別全体給与統計</a> 
                    <a href="${pageContext.request.contextPath}/wage/monthlyTotalStatistics.do">月別全体給与統計</a>
                    <a href="${pageContext.request.contextPath}/wage/yearlyPersonalStatistics.do">年度別個人年収統計</a> 
                    <a href="${pageContext.request.contextPath}/wage/monthlyPersonalStatistics.do">月別個人給与統計</a>
                    <a href="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">給与項目構成統計</a>
                </div>
            </li>
            <li>
                <a href="#">退職管理 <i class="fa-solid fa-caret-down"></i></a>
                <div class="dropdown-content">
                    <a href="${pageContext.request.contextPath}/employee/retirement.do">社員退職処理</a> 
                    <a href="${pageContext.request.contextPath}/retirement/manage.do">退職給与入力・管理</a>
                </div>
            </li>
        </ul>
    </div>
</div>