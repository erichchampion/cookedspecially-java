<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Category Manager</title>
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
<h3>Add Category</h3>

<form:form method="post" action="/CookedSpecially/category/add.html" commandName="category">
	
	<form:hidden path="categoryId" />
	<form:hidden path="userId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name"  /></td> 
	</tr>
	<tr>
		<td><form:label path="description"><spring:message code="label.description"/></form:label></td>
		<td><form:input path="description" /></td>
	</tr>
	
	<tr>
		<td colspan="2">
			<input type="submit" value="<spring:message code="label.addcategory"/>"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>Categories</h3>
<c:if  test="${!empty categoryList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${categoryList}" var="category">
	<tr>
		<td>${category.name}</td>
		<td>${category.description}</td>
		<td><a href="/CookedSpecially/categpry/delete/${category.categoryId}">delete</a></td>
		<td><a href="/CookedSpecially/category/edit/${category.categoryId}">edit</a></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
