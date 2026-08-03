<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag trimDirectiveWhitespaces="true"%>
<%@ attribute name="value" required="true" type="java.lang.String"%>

<%
value = value.replace("&", "&amp;");
value = value.replace("<", "&lt;");
value = value.replace("\n", "\n<br>");
value = value.replace(" ", "&nbsp;");
%>
<%=value%>
