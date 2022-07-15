<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>JSPtest</title>
</head>
<%@ page import= "model.javabeans" %> 
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%
javabeans javabeans = (javabeans) session.getAttribute("javabeans");
ArrayList<String> imglistpath = javabeans.getimgpath();
String imginpath = javabeans.getimginpath();
%>
<form action="./result" method="post">
元画像:
      <SELECT name="startimg">
        <OPTION value="" selected>----</OPTION>
		<%
    	for (int i = 0; i < imglistpath.size(); i++) {
		%>
		<OPTION name=String(imglistpath.get(i))><%=imglistpath.get(i) %></OPTION>
		<%
    	}
		%>
		</SELECT>
目標画像:
      <SELECT name="goalimg">
        <OPTION value="" selected>----</OPTION>
		<%
    	for (int i = 0; i < imglistpath.size(); i++) {
		%>
		
		<OPTION name =String(imglistpath.get(i))><%=imglistpath.get(i) %></OPTION>
		<%
    	}
		%>
		</SELECT>
		<%
    	javabeans.setaction("caliculate");
		session.setAttribute("javabeans", javabeans);
		%>
　<input type="submit" value="送信">
        </form>

<body>
<%
for (int i = 0; i < imglistpath.size(); i++) {
		%>
		<%=imglistpath.get(i)%>
		<img src = "<%="img/"+imglistpath.get(i)%>" alt = "リスト" width="40" height="40" >
		<%
    	}
		%>
</body>
</html>