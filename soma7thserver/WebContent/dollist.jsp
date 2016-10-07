<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>
</head>
<body>
<a href="http://52.40.227.27:8080/soma7th/management.jsp">home</a>

<center><h2>오목알 목록입니다.</h2></center>
<hr>

	<table align="center" cellspacing="10">
	<tr>
		<td bgcolor="#BDBDBD">아이디</td>
		<td bgcolor="#BDBDBD">소지중인 오목알</td>
	</tr>
	<c:forEach var="i" items="${member}">
	<tr>
		<td>${i.id}</td>
		<td>${i.dol}</td>
	</tr>
	</c:forEach>
	</table><br><br><br>
	
	<center>
		추가할 오목알 번호와 유저 아이디를 선택하세요.
	<form ACTION="ManageServlet" METHOD="get">
			<input type="hidden" name="header" value="dolset" >
			<br>
            <select name="id" id="id">
				<c:forEach var="i" items="${member }" >
					<option value="${i.id }"> ${i.id } </option>
				</c:forEach>
			</select>
            <select name="dol" id="dol">
				<c:forEach var="i" begin="3" end="8" >
					<option value="${i}"> ${i} </option>
				</c:forEach>
			</select>
            <br>
            <input TYPE="SUBMIT" value="수정하기">
    </form>
    </center>

</body>
</html>