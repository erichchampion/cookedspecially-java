<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Order Source Manager</title>
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
	
</head>
<body>
<a href="index.jsp" style="float: right;">Return to Home</a>
<hr/>
<h3>Add Order Source</h3>

<form:form method="post" action="organization/addOrderSource.html" commandName="orderSource">
	<form:hidden path="sourceId" />
	<form:hidden path="orgId" value='<%=request.getSession().getAttribute("parentRestaurantId")%>'/>
	<table>
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name"  maxlength="45" required="true"/></td> 
	</tr>
	<tr>
		<td><form:label path="pointbooster">Points Booster</form:label></td>
		<td><form:input path="pointbooster"  type="number" step="any" maxlength="45" required="true"/></td> 
	</tr>
	<tr>
	<td>Status</td><td><select name="status">
	<c:forEach items="${statusTypes}" var="statusType">
	<c:choose>
		<c:when test="${statusType == orderSource.status }">
			<option value="${statusType}" selected="selected">${statusType}</option>
		</c:when>
		<c:otherwise>
			<option value="${statusType}">${statusType}</option>
		</c:otherwise>
	</c:choose>
	</c:forEach>
</select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="Add Order Source"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>Order Sources</h3>

<table class="data">
<tr>
	<th>Name</th>
	<th>Booster</th>
	<th>Status</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:if  test="${!empty orderSourceList}">
<c:forEach items="${orderSourceList}" var="orderSource">
	<tr>
		<td>${orderSource.name}</td>
		<td>${orderSource.pointbooster}</td>
		<td>${orderSource.status}</td>
		<td><a href="organization/deleteOrderSource/${orderSource.sourceId}">delete</a></td>
		<td><a href="organization/editOrderSource/${orderSource.sourceId}">edit</a></td>
	</tr>
</c:forEach>
</c:if>
</table>

</body>
</html>
