<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Customer Manager</title>
	
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
	<base href="${pageContext.request.contextPath}/"/>
	
</head>
<body>
<a href="menu/" style="float: right;">Return to Menus</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Customer</h3>

<form:form method="post" action="customer/add.html" commandName="customer">

	<form:hidden path="customerId"/>
	<form:hidden path="restaurantId"/>
	<form:hidden path="numberOfOrders"/>
	<form:hidden path="createdTime"/>
	<form:hidden path="userId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="firstName">First Name</form:label></td>
		<td><form:input path="firstName" /></td> 
	</tr>
	<tr>
		<td><form:label path="lastName">Last Name</form:label></td>
		<td><form:input path="lastName" /></td>
	</tr>
	<tr>
		<td><form:label path="address">Address</form:label></td>
		<td><textarea id="address" name="address"  placeholder="Address">${customer.address}</textarea></td>
	</tr>
	
	<tr>
		<td><form:label path="city">City</form:label></td>
		<td><form:input path="city" /></td>
	</tr>
	
	<tr>
		<td><form:label path="phone">Phone</form:label></td>
		<td><form:input path="phone" /></td>
	</tr>
	
	<tr>
		<td><form:label path="email">E-mail</form:label></td>
		<td><form:input path="email" /></td>
	</tr>
	
	
	<tr>
		<td colspan="2">
			
				<c:choose>
					<c:when test="${!empty customer.customerId}"><input type="submit" value="Save Customer"/><button type="button" onclick="document.location.href='customer/'">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add Customer"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
			
		</td>
	</tr>
</table>	
</form:form>
<hr/>
	


</body>
</html>
