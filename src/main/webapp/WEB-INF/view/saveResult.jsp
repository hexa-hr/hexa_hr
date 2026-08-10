<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>저장 결과</title>
</head>
<body>
    <script>
        // Handler에서 셋팅해준 "alertMsg"를 자바스크립트 알림창으로 띄웁니다.
        alert('${alertMsg}');
        
        // 확인 버튼을 누르면 원래의 정보 페이지로 강제 이동(리다이렉트 효과)시킵니다.
        location.href = 'companyInfo.do';
    </script>
</body>
</html>