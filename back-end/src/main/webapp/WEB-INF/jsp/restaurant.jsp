<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Restaurant Manager</title>
	<base href="${pageContext.request.contextPath}/"/>
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
<h3>Add Restaurant</h3>

<form:form method="post" action="restaurant/add.html" commandName="restaurant">

	<form:hidden path="restaurantId"/>
	<form:hidden path="userId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name" maxlength="50" /></td> 
	</tr>
	<tr>
		<td><form:label path="restaurantName">Restaurant User Name</form:label></td>
		<td><form:input path="restaurantName"  maxlength="100" requred="true"/></td>
	</tr>
	<tr>
		<td><form:label path="address1">Address1</form:label></td>
		<td><form:input path="address1" maxlength="100" /></td>
	</tr>
	<tr>
		<td><form:label path="address2">Address2</form:label></td>
		<td><form:input path="address2" maxlength="100" /></td>
	</tr>
	
	<tr>
		<td><form:label path="phone">Phone</form:label></td>
		<td><form:input path="phone" maxlength="15" /></td>
	</tr>
	<tr>
		<td><form:label path="city">City</form:label></td>
		<td><form:input path="city" maxlength="100" /></td>
	</tr>
	<tr>
		<td><form:label path="state">State</form:label></td>
		<td><form:input path="state" maxlength="50" /></td>
	</tr>
	<tr>
		<td><form:label path="country">Country</form:label></td>
		<td><form:input path="country" maxlength="100"/></td>
	</tr>
	
	<tr>
		<td colspan="2">
				<c:choose>
					<c:when test="${!empty restaurant.restaurantId}"><input type="submit" value="Save Restaurant"/></c:when>
					<c:otherwise><input type="submit" value="Add Restaurant"/></c:otherwise>
				</c:choose>
		</td>
	</tr>
</table>	
</form:form>
<hr/>
	
<h3>Restaurants</h3>
<c:if  test="${!empty restaurantList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Address</th>
	<th>Phone</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${restaurantList}" var="restaurant">
	<tr>
		<td>${restaurant.name}</td>
		<td>${restaurant.address1}<br/> ${restaurant.address2} <br/> ${restaurant.city} <br/> ${restaurant.state} <br/> ${restaurant.country} </td>
		<td>${restaurant.phone}</td>
		<td><a href="restaurant/delete/${restaurant.restaurantId}">delete</a></td>
		<td><a href="restaurant/edit/${restaurant.restaurantId}">edit</a></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
