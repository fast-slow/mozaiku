<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import= "model.javabeans" %> 
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.io.File" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
javabeans javabeans = (javabeans) session.getAttribute("javabeans");
String resultpath = javabeans.getresultpath();
System.out.println(resultpath);
%>
<%-- 
<img src = "${pageContext.request.contextPath}/compare.jpg"　alt = "リスト"  >
--%>
<img src = "result/compare.jpg"　alt = "リスト"  >
</body>
</html>