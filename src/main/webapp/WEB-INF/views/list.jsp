<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table border="1" >
		<tr>
			<td>id</td>
			<td>username</td>
			<td>userpass</td>
			<td>operate</td>
		</tr>
			<c:forEach items="${users}" var="user" varStatus="s">
				<tr>
					<td>${user.id }</td>
					<td>${user.username }</td>
					<td>${user.userpass }</td>
					<td>
						<a href="details?id=${user.id }">details</a>
						<a href="edit?id=${user.id }">edit</a>
						<a href="del?id=${user.id }">del</a>
					</td>
				</tr>
			</c:forEach>
	</table>
	<a href="edit">add</a>
</body>
</html>