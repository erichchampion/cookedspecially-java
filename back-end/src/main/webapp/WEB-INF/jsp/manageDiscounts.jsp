<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Discount/Charge Manager</title>
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
	<script type="text/javascript">
		function deleteDC(Id) {
			if (confirm('Do you really want to delete this Field')) {
				window.location.href = 'restaurant/deleteDC/' + Id;
			} 
			
		}

	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<h3>Manage Charge/Discount</h3>

<form:form method="post" action="restaurant/addDC.html" commandName="dcType">
	
	<form:hidden path="id" />
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name">name*</form:label></td>
		<td><form:input path="name" maxlength="45"  required="True"/></td> 
	</tr>
		
	<tr><td>Charge Type*</td>
			<td><select name="type">
				<c:forEach items="${chargeTypes}" var="chargeType">
					<c:choose>
						<c:when test="${chargeType == dcType.type}">
							<option value="${chargeType}" selected="selected">${chargeType}</option>
						</c:when>
						<c:otherwise>
							<option value="${chargeType}">${chargeType}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select></td>
	</tr>
	<tr>
		<td><form:label path="value">Value*</form:label></td>
		<td><form:input type="number" step="0.01"  path="value" required="true" /></td> 
	</tr>
	<tr>
	<td>Select Category</td>
	<td><form:select path="category">
				<c:forEach items="${category}" var="categoryType">
					<c:choose>
						<c:when test="${categoryType == dcType.category}">
							<option value="${categoryType}" selected="selected">${categoryType}</option>
						</c:when>
						<c:otherwise>
							<option value="${categoryType}" >${categoryType}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="Add Discount/Charges"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>Discount/Charges Types</h3>
<c:if  test="${!empty dcTypeList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Type</th>
	<th>Value</th>
	<th>Category</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>	
</tr>
<tr></tr>
<c:forEach items="${dcTypeList}" var="dcTypeList">
	<tr>
		<td>${dcTypeList.name}</td>
		 <td>${dcTypeList.type}</td> 
		<td>${dcTypeList.value}</td>
		<td>${dcTypeList.category}</td>
		<td><button type="Button"  onclick="deleteDC(${dcTypeList.id});">delete</button></td>
		<td><button type="Button" onclick="window.location.href='restaurant/editDC/${dcTypeList.id}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>
</body>
</html>
