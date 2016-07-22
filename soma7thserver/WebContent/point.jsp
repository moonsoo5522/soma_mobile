<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<center><h2>포인트 목록입니다.</h2></center>
<hr>

	<table align="center" cellspacing="10">
	<tr>
		<td bgcolor="#BDBDBD">아이디</td>
		<td bgcolor="#BDBDBD">현재 포인트</td>
	</tr>
	<c:forEach var="i" items="${member}">
	<tr>
		<td>${i.id}</td>
		<td>${i.point}</td>
	</tr>
	</c:forEach>
	</table><br><br><br>
	
	<form ACTION="ManageServlet" METHOD="get">
			<input type="hidden" name="header" value="pointset" >
			<br>
            <select name="id" id="id">
				<c:forEach var="i" items="${member }" >
					<option value="${i.id }"> ${i.id } </option>
				</c:forEach>
			</select>
            <input TYPE="TEXT" NAME="point">
            <br>
            <input TYPE="SUBMIT" value="수정하기">
    </form>

</body>
</html>