<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Add On DishType Manager</title>
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
	<link rel="stylesheet" href="css/style.css" />
	<script src="js/jquery-1.9.0.js"></script>
	<script src="js/ui/jquery-ui.js"></script>

</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<h3>Add AddOn DishType</h3>
<form:form method="post" action="addOnDishTypes/add.html" commandName="dishType">

	<form:hidden path="dishTypeId" />
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name"  maxlength="100" required="true"/></td> 
	</tr>
	<tr>
	<td><form:label path=""></form:label></td>
	
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="Add Type"/>
		</td>
	</tr>
</table>	
</form:form>
<hr/>	
<h3>Add-On Types</h3>
<c:if  test="${!empty dishTypeList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${dishTypeList}" var="dishType">
	<tr>
		<td>${dishType.name}</td>
		<td><a href="addOnDishTypes/delete/${dishType.dishTypeId}">delete</a></td>
		<td><a href="addOnDishTypes/edit/${dishType.dishTypeId}">edit</a></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
