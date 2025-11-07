<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Micro Kitchen Screens Manager</title>
	
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
			text-align:center;
			vertical-align:middle;
		}
		.data th {
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
	</style>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
	<script type="text/javascript">
		function deleteMicroKitchenScreen(microKitchenScreenId) {
			if (confirm('Do you really want to delete this Kitchen Screen')) {
				window.location.href = 'restaurant/deleteMicroKitchenScreen/' + microKitchenScreenId;
			} 
			
		}
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Micro-Kitchen Screen</h3>

<form:form method="post" action="restaurant/addMicroKitchenScreen.html" commandName="microKitchenScreen">

	<form:hidden path="id"/>
	 <form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
		<tr>
			<td><form:label path="name"><spring:message code="label.name"/>* </form:label></td>
			<td><form:input path="name" required="true" maxlength="45" class="validate[required]" /></td> 
		</tr>
		<tr>
		<td>Select Fulfillment Center*  </td>
		<td><form:select path="kitchenId">
				<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
					<c:choose>
						<c:when test="${kitchenScreen.id == microKitchenScreen.kitchenId}">
							<option value="${kitchenScreen.id}" selected="selected">${kitchenScreen.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${kitchenScreen.id}" >${kitchenScreen.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></td></tr>
		<tr>
		<td colspan="2">
			
				<c:choose>
					<c:when test="${!empty microKitchenScreen.id}"><input type="submit" value="Save Micro Kitchen Screen"/><button type="reset">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add Miccro-Kitchen Screen"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
			
		</td>
		</tr>
		
		
			
	</table>	
</form:form>
<hr/>
	
<h3>Micro-Kitchen Screen</h3>
<c:if  test="${!empty microKitchenScreenList}">
<table class="data">
<tr>
	<th>Micro-Kitchen </th>
	<th>Fulfillment Center </th>
	<th>Screen Link</th>
	
	<!-- <th>Address</th> -->
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${microKitchenScreenList}" var="microKitchenScreen">
	<tr>
		<td style="width:20%;">${microKitchenScreen.name}</td>
		
		<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
					<c:choose>
						<c:when test="${kitchenScreen.id == microKitchenScreen.kitchenId}">
						<td style="width:20%;">	${kitchenScreen.name}</td>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</c:forEach>
		<c:if test="${microKitchenScreen.kitchenId==0}">
		${microKitchenScreen.name}</td>
		</c:if></td>
	    <td><c:if test="${microKitchenScreen.id!=0}">
	    <a  target="cschecks" href="microKitchenOrders.jsp?MscreenId=${microKitchenScreen.id}#menus" style="float: right;">${microKitchenScreen.name} Screen</a><br>    
	    </c:if>
	    </td>
		<td><button type="button" onclick="deleteMicroKitchenScreen(${microKitchenScreen.id});">delete</button></td>
		<td><button type="button" onclick="window.location.href='restaurant/editMicroKitchenScreen/${microKitchenScreen.id}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
	$(function() {
		$("#KitchenScreen").validationEngine();
	});
</script>
</html>
