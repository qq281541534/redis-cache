<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>edit</title>
</head>
<body>
	<form action="save" method="post">
		<input type="hidden" name="id" value="${user.id }">
		<table border="1">
			<tr>
			
				<td>username</td>
				<td><input type="text" name="username" value="${user.username }"/></td>
			</tr>
			<tr>
				<td>userpass</td>
				<td><input type="text" name="userpass" value="${user.userpass} "/></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" value="提交">
				</td>
			</tr>
		</table>

	</form>
</body>
</html>