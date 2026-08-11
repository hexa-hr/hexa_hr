<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>처리 중...</title>
</head>
<body>
<%
    request.setCharacterEncoding("UTF-8");

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        // 1. 파라미터 받기 (모든 데이터 파싱을 try 안전 구역으로 이동시켰습니다)
        String empIdStr = request.getParameter("employee_id");
        if (empIdStr == null || empIdStr.isEmpty()) empIdStr = request.getParameter("empNo"); 
        int employeeId = 0;
        if (empIdStr != null && !empIdStr.isEmpty()) employeeId = Integer.parseInt(empIdStr);

        String inputDate = request.getParameter("input_date");
        if (inputDate == null) inputDate = "2026-08-06"; 
        
        String typeIdStr = request.getParameter("attendance_type_id");
        if (typeIdStr == null || typeIdStr.isEmpty()) typeIdStr = request.getParameter("attendanceType");
        int attendanceTypeId = 0;
        if (typeIdStr != null && !typeIdStr.isEmpty()) attendanceTypeId = Integer.parseInt(typeIdStr);

        String startDate = request.getParameter("start_date");
        if (startDate == null) startDate = request.getParameter("startDate");
        
        String endDate = request.getParameter("end_date");
        if (endDate == null) endDate = request.getParameter("endDate");

        String daysStr = request.getParameter("attendance_days");
        if (daysStr == null || daysStr.isEmpty()) daysStr = request.getParameter("attendanceDays");
        double attendanceDays = 0;
        if (daysStr != null && !daysStr.isEmpty()) attendanceDays = Double.parseDouble(daysStr);
        
        String amountStr = request.getParameter("amount");
        if (amountStr == null || amountStr.isEmpty()) amountStr = request.getParameter("wageAmount");
        long amount = 0;
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            amount = Long.parseLong(amountStr.replaceAll(",", "")); 
        }
        
        String summary = request.getParameter("summary");
        if (summary == null) summary = request.getParameter("remark");
        
        String attendanceIdStr = request.getParameter("attendance_id");

        // 2. Oracle DB 연결 설정
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String dbUser = "jspexam"; 
        String dbPass = "1234"; 

        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection(url, dbUser, dbPass);

        String sql = "";
        int result = 0;

        // 원래 목록 페이지 URL (.do 컨트롤러 경로)로 돌려보냅니다
        String redirectUrl = request.getContextPath() + "/attendance/manage.do";

        // 3-1. 수정 (UPDATE) 로직
        // "undefined" 문자열이 넘어오는 경우의 에러까지 완벽히 차단
        if (attendanceIdStr != null && !attendanceIdStr.trim().isEmpty() && !attendanceIdStr.equals("undefined")) {
            int attendanceId = Integer.parseInt(attendanceIdStr);
            
            sql = "UPDATE attendance SET "
                + "attendance_type_id = ?, "
                + "start_date = TO_DATE(?, 'YYYY-MM-DD'), "
                + "end_date = TO_DATE(?, 'YYYY-MM-DD'), "
                + "attendance_days = ?, "
                + "amount = ?, "
                + "summary = ? "
                + "WHERE attendance_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, attendanceTypeId);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);
            pstmt.setDouble(4, attendanceDays);
            pstmt.setLong(5, amount);
            pstmt.setString(6, summary);
            pstmt.setInt(7, attendanceId);
            
            result = pstmt.executeUpdate();
            
            if(result > 0) {
                out.println("<script>alert('근태 기록이 성공적으로 수정되었습니다.'); location.href='" + redirectUrl + "';</script>");
            } else {
                out.println("<script>alert('수정 실패: 일치하는 근태 기록을 찾을 수 없습니다.'); history.back();</script>");
            }
            
        } 
        // 3-2. 등록 (INSERT) 로직
        else {
            sql = "INSERT INTO attendance (attendance_id, employee_id, input_date, attendance_type_id, start_date, end_date, attendance_days, amount, summary) "
                + "VALUES (attendance_seq.NEXTVAL, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setString(2, inputDate);
            pstmt.setInt(3, attendanceTypeId);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);
            pstmt.setDouble(6, attendanceDays);
            pstmt.setLong(7, amount);
            pstmt.setString(8, summary);

            result = pstmt.executeUpdate();
            
            if(result > 0) {
                out.println("<script>alert('근태 기록이 성공적으로 등록되었습니다.'); location.href='" + redirectUrl + "';</script>");
            } else {
                out.println("<script>alert('등록 실패: 데이터가 저장되지 않았습니다.'); history.back();</script>");
            }
        }

    } catch(Exception e) {
        e.printStackTrace();
        // 자바스크립트 문법이 깨지지 않도록 에러 메시지의 홑따옴표와 줄바꿈 강제 치환
        String errorMsg = e.getMessage() != null ? e.getMessage().replace("'", "\\'").replace("\n", " ") : "알 수 없는 에러";
        out.println("<script>alert('처리 중 DB/서버 오류 발생:\\n" + errorMsg + "'); history.back();</script>");
    } finally {
        if(pstmt != null) try { pstmt.close(); } catch(Exception ignored) {}
        if(conn != null) try { conn.close(); } catch(Exception ignored) {}
    }
%>
</body>
</html>