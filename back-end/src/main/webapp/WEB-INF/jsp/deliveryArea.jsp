<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Delivery Area Manager</title>
	
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
		function deleteDeliveryArea(deliveryAreaId,userCount) {
			if (confirm('Do you really want to delete this Delivery Area. You will lose '+userCount+' address as well. ')) {
				window.location.href = 'restaurant/deleteDeliveryArea/' + deliveryAreaId;
			}
		}

		function editDeliveryArea(deliveryAreaId,userCount){
			if(confirm('Do you really want to Edit this Delivery Area.On changing Name* field text you will lose mapping of '+userCount+' users to this Delivery Area.')){
			window.location.href='restaurant/editDeliveryArea/'+deliveryAreaId;
			}
			}
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Delivery Area</h3>

<form:form method="post" action="restaurant/addDeliveryArea.html" commandName="deliveryAreas">

	<form:hidden path="id"/>
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
		<tr>
			<td><form:label path="name"><spring:message code="label.name"/>* </form:label></td>
			<td><form:input path="name" maxlength="199" required="true" /></td> 
		</tr>

		<tr>
			<td><form:label path="city">City* </form:label></td>
			<td><form:input path="city" maxlength="50" required="true" /></td> 
		</tr>

		<tr>
			<td><form:label path="state">State* </form:label></td>
			<td><form:input path="state" maxlength="50"  required="true"  /></td> 
		</tr>
		
		<tr>
			<td><form:label path="distance">Distance Away* </form:label></td>
			<td><form:input path="distance" type="number" min="0" required="true"  /></td> 
		</tr>
		
		<tr>
			<td><form:label path="country">Country* </form:label></td>
			<td><form:input path="country" maxlength="50"  required="true"  /></td> 
		</tr>

		<tr>
			<td><form:label path="tomorrowOnly">Deliver by Tomorrow only</form:label></td>
			<td>
			<c:choose>
				<c:when test="${deliveryAreas.tomorrowOnly}"><input type="checkbox" id="tomorrowOnly" name="tomorrowOnly" checked /></c:when>
				<c:otherwise><input type="checkbox" id="tomorrowOnly" name="tomorrowOnly" /></c:otherwise>
			</c:choose>
			</td>
		</tr>
		
		
		<tr>
			<td><form:label path="minDeliveryTime">Minimum Delivery Time* </form:label></td>
			<td><form:select path="minDeliveryTime" required="true">
				<c:forEach items="${minDeliveryTime}" var="minDeliveryTime">
				<c:choose>
					<c:when test="${minDeliveryTime.key==deliveryAreas.minDeliveryTime}">
						<option value="${minDeliveryTime.key}" selected="selected"> ${minDeliveryTime.value} </option>
					</c:when>
					<c:otherwise>
						<option value="${minDeliveryTime.key}"> ${minDeliveryTime.value} </option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</form:select>
			</td> 
		</tr>
		<tr>
			<td><form:label path="deliveryTimeInterval">Delivery Time Interval* </form:label></td>
			<td><form:select path="deliveryTimeInterval" required="true">
				<c:forEach items="${deliveryTimeInterval}" var="deliveryTimeInterval">
				<c:choose>
					<c:when test="${deliveryTimeInterval.key==deliveryAreas.deliveryTimeInterval}">
						<option value="${deliveryTimeInterval.key}" selected="selected"> ${deliveryTimeInterval.value} </option>
					</c:when>
					<c:otherwise>
						<option value="${deliveryTimeInterval.key}"> ${deliveryTimeInterval.value} </option>
					</c:otherwise>
				</c:choose>
				</c:forEach>
			</form:select>
			</td> 
		</tr>
		
		<tr>
		<td><form:label path="posVisible">POS Visible Only</form:label></td>
		<td>
		<c:choose>
			<c:when test="${deliveryAreas.posVisible}"><input type="checkbox" id="posVisible" name="posVisible" checked /></c:when>
			<c:otherwise><input type="checkbox" id="posVisible" name="posVisible" /></c:otherwise>
		</c:choose>
		
		
		</td>
	</tr>
		<tr>
			<td><form:label path="deliveryCharges"><spring:message code="label.deliveryCharges"/>* </form:label></td>
			<td><form:input path="deliveryCharges" class="validate[required]" /></td> 
		</tr>
		
		<tr>
			<td><form:label path="minDeliveryThreshold"><spring:message code="label.minDeliveryThreshold"/>* </form:label></td>
			<td><form:input path="minDeliveryThreshold" class="validate[required]" /></td> 
		</tr>
		
		<tr><td>Select Kitchen Screen</td>
		<td><form:select path="fulfillmentCenterId">
				<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
					<c:choose>
						<c:when test="${kitchenScreen.id == deliveryAreas.fulfillmentCenterId}">
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
					<c:when test="${!empty deliveryArea.id}"><input type="submit" value="Save Delivery Area"/><button type="button" onclick="document.location.href='restaurant/deliveryAreas'">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add DeliveryArea"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
		</td>
		</tr>
	</table>	
</form:form>
<hr/>
	
<h3>Delivery Areas</h3>
<c:if  test="${!empty deliveryAreaList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>User Count </th>
	<th>Pos Visible Only </th>
	<th>Tomorrow delivery Only </th>
	<th>Kitchen Screen</th>
	<th>Distance Away</th>
	<th>Delivery Charges</th>
	<th>Minimum Delivery Threshold </th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${deliveryAreaList}" var="deliveryArea">
	<tr>
		<td style="width:20%;">${deliveryArea.name}</td>
		<c:forEach items="${userCount}" var="userCount">
		<c:if test ="${deliveryArea.id== userCount.key}">
		<td style="width:20%;">${userCount.value}</td>
		 <c:set var="custCount" value="${userCount.value}"/> 
		</c:if>
		</c:forEach>
        <td style="width:20%;">${deliveryArea.posVisible}</td>
        <td style="width:20%;">${deliveryArea.tomorrowOnly}</td>
		<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
       		<c:if test="${kitchenScreen.id==deliveryArea.fulfillmentCenterId}">
       		<td style="width:20%;">${kitchenScreen.name}</td>
       		<c:set var="addOnDup" value="${kitchenScreen.id}"/>
       		</c:if>
       	</c:forEach>
       	<c:if test="${deliveryArea.fulfillmentCenterId!=addOnDup}">
       	<td style="width:20%;">NA</td>
       	</c:if>
       	<td>${deliveryArea.distance}</td>
		<td style="width:20%;">${deliveryArea.deliveryCharges}</td>
	   <td style="width:20%;">${deliveryArea.minDeliveryThreshold}</td>
	   <c:if test="${custCount}==''">
	   <c:set var="custCount" value="0"></c:set>
	   </c:if>
		<td><button type="button" onclick="deleteDeliveryArea(${deliveryArea.id},${custCount});">delete</button></td>
		<td><button type="button" onclick="editDeliveryArea(${deliveryArea.id},${custCount});">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
	$(function() {
		$("#deliveryArea").validationEngine();
	});
</script>
</html>
