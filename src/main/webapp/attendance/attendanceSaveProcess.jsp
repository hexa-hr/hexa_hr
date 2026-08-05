<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    request.setCharacterEncoding("UTF-8");

    int employeeId = Integer.parseInt(request.getParameter("employee_id"));
    String inputDate = request.getParameter("input_date");
    int attendanceTypeId = Integer.parseInt(request.getParameter("attendance_type_id"));
    String startDate = request.getParameter("start_date");
    String endDate = request.getParameter("end_date");
    double attendanceDays = Double.parseDouble(request.getParameter("attendance_days"));
    
    // 금액(수당) 쉼표(,) 제거 후 숫자로 변환
    String amountStr = request.getParameter("amount");
    long amount = 0;
    if (amountStr != null && !amountStr.trim().isEmpty()) {
        amount = Long.parseLong(amountStr.replaceAll(",", ""));
    }
    
    String summary = request.getParameter("summary");

    // Oracle DB 연결 설정
    String url = "jdbc:oracle:thin:@localhost:1521:xe";
    String dbUser = "jspexam"; 
    String dbPass = "1234"; 

    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection(url, dbUser, dbPass);

        String sql = "INSERT INTO attendance (attendance_id, employee_id, input_date, attendance_type_id, start_date, end_date, attendance_days, amount, summary) "
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

        int result = pstmt.executeUpdate();
        
        if(result > 0) {
            out.println("<script>alert('근태 기록이 성공적으로 저장되었습니다.'); location.href='attendanceForm.jsp';</script>");
        }
    } catch(Exception e) {
        e.printStackTrace();
        out.println("<script>alert('저장 중 오류 발생: " + e.getMessage() + "'); history.back();</script>");
    } finally {
        if(pstmt != null) pstmt.close();
        if(conn != null) conn.close();
    }
%>