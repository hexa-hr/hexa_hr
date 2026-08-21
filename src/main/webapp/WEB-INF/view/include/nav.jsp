<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<style>
.nav-container {
	font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
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
	width: 100px;
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
	min-width: 180px;
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
        <a href="${pageContext.request.contextPath}/index.jsp" class="icon-menu-item"> 
            <i class="fa-solid fa-house" style="color: #34d399;"></i> 
            <span>HOME</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/employee/register.do" class="icon-menu-item"> 
            <i class="fa-solid fa-user-plus"></i>
            <span>사원등록</span>
        </a> 

        <a href="${pageContext.request.contextPath}/employee/list.do" class="icon-menu-item"> 
            <i class="fa-solid fa-users"></i>
            <span>사원현황</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/attendance/manage.do" class="icon-menu-item"> 
            <i class="fa-solid fa-calendar-check"></i>
            <span>근태기록/관리</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/vacationList.do" class="icon-menu-item"> 
            <i class="fa-solid fa-calendar-minus"></i> 
            <span>휴가조회</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/paymentInput.do"  class="icon-menu-item"> 
            <i class="fa-solid fa-calculator"></i> 
            <span>급여입력/관리</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do" class="icon-menu-item"> 
            <i class="fa-solid fa-hard-hat"></i> 
            <span>급여입력(일용직)</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/ledger.do" class="icon-menu-item"> 
            <i class="fa-solid fa-file-invoice-dollar"></i> 
            <span>급여대장</span>
        </a> 
        
        <a href="${pageContext.request.contextPath}/wage/employeeHistory.do" class="icon-menu-item"> 
            <i class="fa-solid fa-money-check-dollar"></i> 
            <span>사원별급여내역</span>
        </a>
    </div>

    <!-- 2뎁스: 상세 드롭다운 메뉴 바 -->
    <div class="text-menu-bar">
        <!-- 기본환경 설정 -->
        <div class="text-menu-item">
            <a href="#">기본환경 설정 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/index.jsp">사용자 정보</a> 
                <a href="${pageContext.request.contextPath}/employee/register.do">사원 등록</a> 
                <a href="${pageContext.request.contextPath}/vacationTypeSetting.do">휴가/근태 설정</a> 
                <a href="${pageContext.request.contextPath}/wageTypeSetting.do">급여항목 설정</a>
            </div>
        </div>

        <!-- 인사관리 -->
        <div class="text-menu-item">
            <a href="#">인사관리 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/employee/list.do">사원현황/관리</a>
            </div>
        </div>

        <!-- 근태관리 -->
        <div class="text-menu-item">
            <a href="#">근태관리 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/attendance/manage.do">근태기록/관리</a>
                <a href="${pageContext.request.contextPath}/attendance/monthly.do">근태조회</a> 
                <a href="${pageContext.request.contextPath}/vacationList.do">휴가조회</a>
                <a href="${pageContext.request.contextPath}/dailywork/manage.do">일용직 근무기록/관리</a>
                <a href="${pageContext.request.contextPath}/dailywork/monthly.do">일용직 근무 조회</a>
            </div>
        </div>

        <!-- 급여관리 -->
        <div class="text-menu-item">
            <a href="#">급여관리 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/wage/paymentInput.do">급여 입력/관리</a> 
                <a href="${pageContext.request.contextPath}/wage/dailyPaymentInput.do">급여 입력/관리(일용직)</a> 
                <a href="${pageContext.request.contextPath}/wage/ledger.do">급여대장</a> 
                <a href="${pageContext.request.contextPath}/wage/employeeHistory.do">사원별 급여내역</a> 
                <a href="${pageContext.request.contextPath}/wage/itemLedger.do">항목별 대장</a> 
                <a href="${pageContext.request.contextPath}/wage/insuranceDeduction.do">4대 보험 공제내역</a> 
            </div>
        </div>

        <!-- 급여통계 -->
        <div class="text-menu-item">
            <a href="#">급여통계 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/wage/yearlyTotalStatistics.do">연도별 전체급여 통계</a> 
                <a href="${pageContext.request.contextPath}/wage/monthlyTotalStatistics.do">월별 전체급여 통계</a>
                <a href="${pageContext.request.contextPath}/wage/yearlyPersonalStatistics.do">연도별 개인연봉 통계</a> 
                <a href="${pageContext.request.contextPath}/wage/monthlyPersonalStatistics.do">월별 개인급여 통계</a>
                <a href="${pageContext.request.contextPath}/wage/itemCompositionStatistics.do">급여항목 구성 통계</a>
            </div>
        </div>

        <!-- 퇴직관리 -->
        <div class="text-menu-item">
            <a href="#">퇴직관리 <i class="fa-solid fa-caret-down"></i></a>
            <div class="dropdown-content">
                <a href="${pageContext.request.contextPath}/employee/retirement.do">사원 퇴직처리</a> 
                <a href="#" style="color: #34d399;" onclick="alert('현재 개발/QA 준비 중인 기능입니다.'); return false;">퇴직급여 입력/관리</a>
            </div>
        </div>
    </div>
</div>