<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Delivery Manager</title>
	
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
		function deleteDeliveryBoy(deliveryBoyId) {
			if (confirm('Do you really want to delete this Delivery Person')) {
				window.location.href = 'restaurant/deleteDeliveryBoy/' + deliveryBoyId;
			} 
			
		}
	</script>
	
</head>
<body>
<a href="index.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Delivery Person</h3>

<form:form method="post" action="restaurant/addDeliveryBoy.html" commandName="deliveryBoy">

	<form:hidden path="id"/>
	<form:hidden path="userId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<table>
		<tr>
			<td><form:label path="name"><spring:message code="label.name"/>* </form:label></td>
			<td><form:input path="name" required="true" class="validate[required]" /></td> 
		</tr>
		<tr>
			<td><form:label path="age"><spring:message code="label.age"/>* </form:label></td>
			<td><form:input path="age" required="true" class="validate[required]" /></td> 
		</tr>
				<tr><td>Select Area Kitchen Screen</td>
		<td><form:select path="kitchenScreenId">
				<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
					<c:choose>
						<c:when test="${kitchenScreen.id == deliveryAreas.kitchenScreenId}">
							<option value="${kitchenScreen.id}" selected="selected">${kitchenScreen.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${kitchenScreen.id}" >${kitchenScreen.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></td></tr>
			
		<%-- <tr>
			<td><form:label path="minDeliveryThreshold"><spring:message code="label.address"/>* </form:label></td>
			<td><form:input path="minDeliveryThreshold" class="validate[required]" /></td> 
		</tr> --%>
		<tr>
		<td colspan="2">
			
				<c:choose>
					<c:when test="${!empty deliveryBoy.id}"><input type="submit" value="Save Delivery Boy"/><button type="button" onclick="document.location.href='restaurant/deliveryBoys'">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add DeliveryBoy"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
			
		</td>
		</tr>
	</table>	
</form:form>
<hr/>
	
<h3>Delivery Person</h3>
<c:if  test="${!empty deliveryBoyList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Age</th>
	<th>Kitchen Screen</th>
	<!-- <th>Address</th> -->
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${deliveryBoyList}" var="deliveryBoy">
	<tr>
		<td style="width:20%;">${deliveryBoy.name}</td>
		<td style="width:20%;">${deliveryBoy.age}</td>
		<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
       		<c:if test="${kitchenScreen.id==deliveryBoy.kitchenScreenId}">
       		<td style="width:20%;">${kitchenScreen.name}</td>
       		<c:set var="addOnDup" value="${kitchenScreen.id}"/>
       		</c:if>
       	</c:forEach>
       	<c:if test="${deliveryBoy.kitchenScreenId!=addOnDup}">
       	<td style="width:20%;">NA</td>
       	</c:if>
	  <%--  <td style="width:20%;">${deliveryBoy.minDeliveryThreshold}</td> --%>
		<td><button type="button" onclick="deleteDeliveryBoy(${deliveryBoy.id});">delete</button></td>
		<td><button type="button" onclick="window.location.href='restaurant/editDeliveryBoy/${deliveryBoy.id}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
	$(function() {
		$("#deliveryBoy").validationEngine();
	});
</script>
</html>
