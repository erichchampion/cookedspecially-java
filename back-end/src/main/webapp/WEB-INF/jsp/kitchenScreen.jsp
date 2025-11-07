<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>fulfillmentcenter Manager</title>
	
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
		function deleteKitchenScreen(kitchenScreenId) {
			if (confirm('Do you really want to delete this Kitchen Screen')) {
				window.location.href = 'restaurant/deleteKitchenScreen/' + kitchenScreenId;
			} 
			
		}
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Fulfillmentcenter</h3>

<form:form method="post" action="restaurant/addKitchenScreen.html" commandName="kitchenScreen">

	<form:hidden path="id"/>
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
		<tr>
			<td><form:label path="name"><spring:message code="label.name"/>* </form:label></td>
			<td><form:input path="name" maxLength="45" required="true" class="validate[required]" /></td> 
		</tr>
		<tr>
			<td><form:label path="location"><span>Location*</span></form:label></td>
			<td><form:input path="location" maxLength="45" required="true" class="validate[required]" /></td> 
		</tr>
		<tr>
			<td><form:label path="address">Address*</form:label></td>
			<td><form:textarea path="address" maxLength="300" required="true" class="validate[required]" /></td> 
		</tr>
		<tr>
		<td colspan="2">
			
				<c:choose>
					<c:when test="${!empty kitchenScreen.id}"><input type="submit" value="Save Kitchen Screen"/><button type="reset" >Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add Kitchen Screen"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
			
		</td>
		</tr>
	</table>	
</form:form>
<hr/>
	
<h3>Kitchen Screen</h3>
<c:if  test="${!empty kitchenScreenList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Location</th>
	<th>Screen Links</th>
	<th>Micro-Kitchen Link</th> 
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
	<tr>
		<td style="width:20%;">${kitchenScreen.name}</td>
		<td style="width:20%;">${kitchenScreen.location}</td>
		<td style="width:20%;">
		<a target="cschecks" href="orders.jsp?KscreenId=${kitchenScreen.id}#menus" style="float: right;">Manage orders Screen</a><br>
		<a target="cschecks" href="orderDelivery.jsp?KscreenId=${kitchenScreen.id}#menus" style="float: right;">Manage orders Delivery Screen</a><br>
		<a target="cschecks" href="cashCollection.jsp?KscreenId=${kitchenScreen.id}#menus" style="float: right;">Manage cash Collection Screen</a><br>
		</td>
	    <td style="width:20%;">
	    <c:forEach items="${microScreen}" var="microScreen">
	    <c:choose >
	    <c:when test="${microScreen.kitchenId==kitchenScreen.id}">
	    <a target="cschecks" href="microKitchenOrders.jsp?KscreenId=${kitchenScreen.id}&MscreenId=${microScreen.id}#menus" style="float: right;">${microScreen.name} Screen</a><br>    
	    </c:when>
	    <c:otherwise>
	    <a></a>
	    </c:otherwise>
	    </c:choose>
	    </c:forEach> 
	    </td>
		<td><button type="button"  onclick="deleteKitchenScreen(${kitchenScreen.id});">delete</button></td>
		<td><button type="button" onclick="window.location.href='restaurant/editKitchenScreen/${kitchenScreen.id}';">edit</button></td>
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
