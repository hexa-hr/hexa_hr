<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>HEXA.HR</title>
    
    <!-- Font Awesome 6 CDN 추가 (아이콘 표시를 위해 필수) -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- 파비콘 추가 -->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    
    <style>
        /* 페이지 전체 기본 여백 제거 및 폰트 설정 */
        body {
            margin: 0;
            padding: 0;
            font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
            background-color: #f4f5f7; /* PAYZON과 유사한 연한 회색 배경 */
        }
        
        /* 본문 컨텐츠가 들어갈 영역 */
        .content-area {
            padding: 20px;
            max-width: 1200px;
            margin: 0 auto;
            background-color: #ffffff;
            min-height: 500px; /* 임시 높이 */
            box-shadow: 0 0 10px rgba(0,0,0,0.05); /* 약간의 그림자 효과 */
            margin-top: 15px;
        }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/include/header.jsp" />

    <!-- 2. 네비게이션 영역 -->
    <jsp:include page="/WEB-INF/view/include/nav.jsp" />
    
</body>
</html>