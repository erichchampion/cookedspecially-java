<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Manage Employee</title>
	
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
	<link rel="stylesheet" href="css/style.css" />
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css">
	<link rel="stylesheet" type="text/css" href="themes/base/jquery.multiselect.css" />
	<link rel="stylesheet" type="text/css" href="themes/base/jquery.multiselect.filter.css" />
	<script src="js/jquery-1.9.0.js"></script>
	<script src="js/ui/jquery-ui.js"></script>
	<script type="text/javascript" src="js/jquery.multiselect.js"></script>
	<script type="text/javascript" src="js/jquery.multiselect.filter.js"></script>	
	<script type="text/javascript">
		function deleteUser(userId) {
			if (confirm('Do you really want to delete this User')) {
				window.location.href = 'organization/deleteEmployee/' + userId;
			} 
			
		}
	</script>
	
</head>
<body>

<form:form id="fulfillmentCenterCatch${user.userId}" hidden="true" title="Add fulfillment Center"  style="width:400px;">
<input type="hidden" id="userId" name="userId" value="${user.userId}" />
<input type="hidden" id="restaurantId" name="restaurantId" value="${user.restaurantId}" /> 
<input type="hidden" id="orgId" name="orgId" value="${user.orgId}" /> 
<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
					<c:choose>
						<c:when test="${user.userId!=0}">
							<td><input type="checkbox" name="kitchenId" value="${kitchenScreen.id}" checked="checked">${kitchenScreen.name}</td><br>
						</c:when>
						<c:otherwise>
							 <td><input type="checkbox" name="kitchenId" value="${kitchenScreen.id}" >${kitchenScreen.name}</td><br>
						</c:otherwise>
					</c:choose>
</c:forEach>
<input type="button" id="addKitchen" value="Add Section" />
</form:form>


<a href="index.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add Employee</h3>

<form:form method="post" action="organization/addEmployee.htmbl" commandName="user">

	<form:hidden path="userId"/>
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("parentRestaurantId")%>'/>
	<table>
		<tr>
			<td><form:label path="firstName"><spring:message code="label.name"/>* </form:label></td>
			<td><form:input path="firstName" maxlength="50" required="true" class="validate[required]" /></td> 
		</tr>
		<tr>
			<td><form:label path="dateOfBirth">Date of Birth* </form:label></td>
			<td><form:input type="Date" path="dateOfBirth" required="true" /></td> 
		</tr>
		<tr>
		<td>Date of Joining</td>
		<td><form:input path="dateOfJoining" required="true"></form:input></td>
		</tr>
		<tr>
			<td><form:label path="contactNo">Contact No*</form:label></td>
			<td><form:input path="contactNo" maxlength="50" required="true" /></td> 
		</tr>	
		
		<tr>
			<td><form:label path="address1">Address*</form:label></td>
			<td><form:textarea path="address1" maxlength="300"  required="true" class="validate[required]" /></td> 
		</tr>
		
		<tr>
			<td><form:label path="userName">userName*</form:label></td>
			<td><form:input path="userName" maxlength="300" required="true" class="validate[required]" /></td> 
		</tr>
		
		<tr><td>Select Restaurant <td>
		<td><form:select path="">
				<c:forEach items="${restaurantList}" var="restaurantList">
					<c:choose>
						<c:when test="${restaurantList.restaurantId == user.restaurantId}">
							<option value="${restaurantList.restaurantId}" selected="selected">${restaurantList.restaurantName}</option>
						</c:when>
						<c:otherwise>
							<option value="${restaurantList.restaurantId}" >${restaurantList.restaurantName}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
		</form:select></td></tr>
		<tr><td>Select Fulfillment Center</td>
		<td><form:select path="">
				<c:forEach items="${fulfillmentCenterList}" var="fulfillmentCenter">
					<c:choose>
						<c:when test="${fulfillmentCenter.id == user.fulfillmentCenterId}">
							<option value="${fulfillmentCenter.id}" selected="selected">${fulfillmentCenter.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${fulfillmentCenter.id}" >${fulfillmentCenter.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
		</form:select></td></tr>
		<tr><td>Add Fulfillment Center :</td><td><button type="button" onclick="addKitchen('fulfillmentCenterCatch${user.userId}');"> Add Fulfilment Center</button></td></tr>
		<tr>
		<td><label> Select User Roles </label></td>
		<td>
		<select name="">
				<c:forEach items="${userRoles}" var="userRole">
					<c:choose>
						<c:when test="${userRole.id == user.role.roleId}">
							<option value="${userRole.id}" selected="selected">${userRole.role}</option>
						</c:when>
						<c:otherwise>
							<option value="${userRole.id}">${userRole.role}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
		</tr>
		<%-- <tr>
			<td><form:label path="minDeliveryThreshold"><spring:message code="label.address"/>* </form:label></td>
			<td><form:input path="minDeliveryThreshold" class="validate[required]" /></td> 
		</tr> --%>
		<tr>
		<td colspan="2">
				<c:choose>
					<c:when test="${!empty user.userId}"><input type="submit" value="Save User"/><button type="button" onclick="document.location.href='organization/employee'">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add User"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
		</td>
		</tr>
	</table>	
</form:form>
<hr/>
<h3>User Details</h3>
<c:if  test="${!empty userList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Age</th>
	<th>Phone</th>
	<th>Address</th>
	<th>userName</th>
	<th>User Role</th>
	<th>Fulfillment Center</th>
<!-- 	<th>Additional Filfillment Center</th> -->
	<!-- <th>Address</th> -->
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${employeeList}" var="employee">
		<tr>
		<td style="width:10%;">${employee.name}</td>
		<td style="width:10%;">${employee.age}</td>
		<td style="width:10%;">${employee.phone}</td>
		<td style="width:20%;">${employee.address}</td>
		<td style="width:20%;">${employee.userName}</td>
		<c:forEach items="${userRoles}" var="userRoles">
       		<c:if test="${userRoles.id==employee.roleId}">
       		<td style="width:15%;">${userRoles.role}</td>
       		<c:set var="addOnDup" value="${userRoles.id}"/>
       		</c:if>
       	</c:forEach>
       	<c:if test="${employee.roleId!=addOnDup && employee.roleId!=''}">
       	  <td style="width:15%;">NA</td>
       	  </c:if>
		<c:forEach items="${kitchenScreenList}" var="kitchenScreen">
       		<c:if test="${kitchenScreen.id==userPortrayal.kitchenId && userPortrayal.userId==employee.userId}">
       		<td style="width:15%;">${kitchenScreen.name}</td>
       		<c:set var="addOnDup" value="${kitchenScreen.id}"/>
       		</c:if>
       	</c:forEach>
       	<c:if test="${user.fulfillmentCenterId!=addOnDup}">
       	<td style="width:15%;">NA</td>
       	</c:if>
	  <%--  <td style="width:20%;">${employee.minDeliveryThreshold}</td> --%>
      <%-- 	<td><button type="button" onclick="addKitchen('fulfillmentCenterCatch${user.userId}');"> Add Fulfilment Center</button></td> --%>
		<td><button type="button" onclick="deleteEmployee(${user.userId});">delete</button></td>
		<td><button type="button" onclick="window.location.href='organization/editEmployee/${user.userId}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
	$(function() {
		$("#employee").validationEngine();
	});

	function addKitchen(employeeUserId) {
		/* var sectionEL = sectionEditBtnEL.parent();
		var sectionELId = sectionEL.attr('id'); */
		/* $('#addSection').hide();
		$('#saveSection').attr("data-sectionCount", sectionELId);
		$('#saveSection').show();
	$('#sectionId').val(sectionEL.find('.sectionSectionId')[0].value);
		$('#name').val(sectionEL.find('.sectionName')[0].value);
		$('#price').val(sectionEL.find('.sectionPrice')[0].value);
		$('#description').val(sectionEL.find('.sectionDescription')[0].value);
		$('#header').val(sectionEL.find('.sectionHeader')[0].value);
		$('#footer').val(sectionEL.find('.sectionFooter')[0].value); */	
		
		$("#"+employeeUserId).dialog({
			width: 500,
			modal: true
			});
	}
</script>
</html>
