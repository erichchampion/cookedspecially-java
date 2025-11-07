<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Password Reset</title>
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
<h3>Password Reset</h3>

<form:form method="post" action="updatePassword.html">

	<input type="hidden" name="userId" value="${userId}"/>
	<input type="hidden" name="code" value="${code}"/>
	
	New Password:  <input type="password" name="newPassword" /> <br/>

	<input type="submit" value="Update Password"/>


</form:form>
