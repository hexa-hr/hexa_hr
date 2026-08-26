<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<style>
.nav-container {
	font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
    min-width: 1400px;
}

/* 1뎁스 (아이콘 메뉴) */
.icon-menu-bar {
	background-color: #ffffff;
	display: flex;
	padding: 15px 20px;
	border-bottom: 1px solid #e0e0e0;
	gap: 25px;
}

.icon-menu-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	text-decoration: none;
	color: #333333;
	font-size: 12px;
	width: 120px;
	transition: color 0.2s;
}

.icon-menu-item:hover {
	color: #10b981; /* 초록색 포인트 */
}

.icon-menu-item i {
	font-size: 28px;
	margin-bottom: 8px;
	color: #666666;
	transition: color 0.2s;
}

.icon-menu-item:hover i {
	color: #10b981; /* 초록색 포인트 */
}

/* 2뎁스 (초록색 바 메뉴) */
.text-menu-bar {
	background-color: #10b981; /* 메인 초록색 배경 */
	display: flex;
	padding: 0 20px;
}

.text-menu-item {
	position: relative;
}

.text-menu-item>a {
	display: block;
	color: #ffffff;
	text-decoration: none;
	padding: 12px 25px;
	font-size: 14px;
	font-weight: bold;
}

.text-menu-item:hover {
	background-color: #059669; /* 진한 초록색 호버 */
}

/* 드롭다운 메뉴 */
.dropdown-content {
	display: none;
	position: absolute;
	background-color: #ffffff;
	min-width: 190px;
	box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
	z-index: 1000;
	border-top: 3px solid #10b981; /* 초록색 테두리 */
}

.dropdown-content a {
	color: #333333;
	padding: 12px 16px;
	text-decoration: none;
	display: block;
	font-size: 13px;
	border-bottom: 1px solid #f1f1f1;
}

.dropdown-content a:hover {
	background-color: #f8f9fa;
	color: #10b981; /* 텍스트 초록색 호버 */
	font-weight: bold;
}

.text-menu-item:hover .dropdown-content {
	display: block;
}
</style>

<div class="nav-container">
    <!-- 1뎁스: 퀵 링크 아이콘 메뉴 -->
    <div class="icon-menu-bar">
        <a href="${pageContext.request.contextPath}/employee/userInfo.do" class="icon-menu-item"> 
            <i class="fa-solid fa-house" style="color: #34d399;"></i> 
            <span>HOME</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/employee/register.do" class="icon-menu-item"> 
            <i class="fa-solid fa-user-plus"></i>
            <span>社員登録</span>
        </a> 

        <a href="${pageContext.request.contextPath}/employee/list.do" class="icon-menu-item"> 
            <i class="fa-solid fa-users"></i>
            <span>社員状況</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/attendance/manage.do" class="icon-menu-item"> 
            <i class="fa-solid fa-calendar-check"></i>
            <span>勤怠記録・管理</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/vacationList.do" class="icon-menu-item"> 
            <i class="fa-solid fa-calendar-minus"></i> 
            <span>休暇照会</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/paymentInput.do"  class="icon-menu-item"> 
            <i class="fa-solid fa-calculator"></i> 
            <span>給与入力・管理</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do" class="icon-menu-item"> 
            <i class="fa-solid fa-hard-hat"></i> 
            <span>給与入力（日雇い）</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/ledger.do" class="icon-menu-item"> 
            <i class="fa-solid fa-file-invoice-dollar"></i> 
            <span>給与台帳</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/employeeHistory.do" class="icon-menu-item"> 
            <i class="fa-solid fa-money-check-dollar"></i> 
            <span>社員別給与明細</span>
        </a>
    </div>

    <!-- 2뎁스: 상세 드롭다운 메뉴 바 -->
    <div class="text-menu-bar">
        <!-- 기본환경 설정 -->
        <div class="text-menu-item">
            <a href="#">基本環境設定 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/employee/userInfo.do">ユーザー情報</a> 
                <a href="${pageContext.request.contextPath}/employee/register.do">社員登録</a> 
                <a href="${pageContext.request.contextPath}/vacationTypeSetting.do">休暇・勤怠設定</a> 
                <a href="${pageContext.request.contextPath}/wageTypeSetting.do">給与項目設定</a>
            </div>
        </div>

        <!-- 인사관리 -->
        <div class="text-menu-item">
            <a href="#">人事管理 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/employee/list.do">従業員状況・管理</a>
            </div>
        </div>

        <!-- 근태관리 -->
        <div class="text-menu-item">
            <a href="#">勤怠管理 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/attendance/manage.do">勤怠記録・管理</a>
                <a href="${pageContext.request.contextPath}/attendance/monthly.do">勤怠照会</a> 
                <a href="${pageContext.request.contextPath}/vacationList.do">休暇照会</a>
                <a href="${pageContext.request.contextPath}/dailywork/manage.do">日雇い勤務記録・管理</a>
                <a href="${pageContext.request.contextPath}/dailywork/monthly.do">日雇い勤務照会</a>
            </div>
        </div>

        <!-- 급여관리 -->
        <div class="text-menu-item">
            <a href="#">給与管理 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/wage/paymentInput.do">給与入力・管理</a> 
                <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">給与入力・管理（日雇い）</a> 
                <a href="${pageContext.request.contextPath}/wage/ledger.do">給与台帳</a> 
                <a href="${pageContext.request.contextPath}/wage/employeeHistory.do">社員別給与明細</a> 
                <a href="${pageContext.request.contextPath}/wage/itemLedger.do">項目別台帳</a> 
                <a href="${pageContext.request.contextPath}/wage/insuranceDeduction.do">社会保険控除明細</a> 
            </div>
        </div>

        <!-- 급여통계 -->
        <div class="text-menu-item">
            <a href="#">給与統計 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/wage/yearlyTotalStatistics.do">年度別全体給与統計</a> 
                <a href="${pageContext.request.contextPath}/wage/monthlyTotalStatistics.do">月別全体給与統計</a>
                <a href="${pageContext.request.contextPath}/wage/yearlyPersonalStatistics.do">年度別個人年収統計</a> 
                <a href="${pageContext.request.contextPath}/wage/monthlyPersonalStatistics.do">月別個人給与統計</a>
                <a href="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">給与項目構成統計</a>
            </div>
        </div>

        <!-- 퇴직관리 -->
        <div class="text-menu-item">
            <a href="#">退職管理 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/employee/retirement.do">社員退職処理</a> 
                <a href="${pageContext.request.contextPath}/retirement/manage.do">退職給与入力・管理</a>
            </div>
        </div>
    </div>
</div>