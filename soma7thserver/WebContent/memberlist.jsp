<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>

<center><h2>회원 목록입니다.</h2></center>
<hr>

	<table align="center" cellspacing="10">
	<tr>
		<td bgcolor="#BDBDBD">아이디</td>
		<td bgcolor="#BDBDBD">패스워드</td>
		<td bgcolor="#BDBDBD">이름</td>
		<td bgcolor="#BDBDBD">승</td>
		<td bgcolor="#BDBDBD">패</td>
		<td bgcolor="#BDBDBD">포인트</td>
	</tr>
	<c:forEach var="i" items="${member}">
	<tr>
		<td>${i.id}</td>
		<td>${i.password}</td>
		<td>${i.name}</td>
		<td>${i.win}</td>
		<td>${i.lose}</td>
		<td>${i.point}</td>
		<td><a href="/soma7th/ManageServlet?header=ban&id=${i.id}">삭제</a></td>
	</tr>
	</c:forEach>
	</table><br><br><br>

</body>
</html>