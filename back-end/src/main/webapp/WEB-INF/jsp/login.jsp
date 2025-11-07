<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
	<title>Login Manager</title>
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
<h3>Login </h3>
<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
      <font color="red">
        <c:out value="Invalid Username and Password!"/>
      </font>
    </c:if>
  <form name='form' action='j_spring_security_check' method='POST'>
	 <table>
	    <tr>
	      <td>User Name:</td>
	      <td><input type='text' name='j_username' value=''></td>
	    </tr>
	    <tr>
	      <td>Password:</td>
	      <td><input type='password' name='j_password'/></td>
	    </tr>
	    <tr>
		  <td>Remember Me: </td>
			 <td><input type="checkbox" name="remember-me" /></td>
		  </tr>
	    <tr>
	      <td colspan='2'>
	        <input name="submit" type="submit" value="Login"/></td>
	    </tr>
	  </table>
  </form>
<a href="user/signup">Signup</a>
<a href="user/forgotPassword"> Forgot Password</a><br>
<a href="socialauth?id=facebook"><img src="images/images.jpg" alt="Facebook" title="Facebook" border="0"></img></a>
<a href="socialauth?id=googleplus"><img src="images/sign-in-with-google.png" alt="Gmail" title="Gmail" border="0"></img></a>