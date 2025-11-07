<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Password Reset</title>
    <base href="${pageContext.request.contextPath}/"/> 
	<link rel="stylesheet" href="css/style.css" />
	<style type="text/css">
		body {
			font-family: sans-serif;
		}
		.data, .data td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.data th {
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
	</style>
</head>
<body>
<hr/>
<h3>Forgot Password </h3>
<c:if test="${!empty error}"><font color=red> ${error}</font> </c:if>
<c:if test="${!empty message}"><font color=green> ${message}</font> </c:if>
<form:form method="post" action="user/forgotPassword.html">
	
	UserName : <input type="text" name="username" />
	<br/>
	<input type="submit" value="Forgot Password"/>

</form:form>
