<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Manage Employees</title>
	
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

		/* function addEmployee(){
			var restaurnatId = 	document.querySelector('input[name="restaurantId"]:checked').value;
			window.location.href ='organization/addEmployee';
			} */
	</script>
	
</head>
<body>


<form:form id="restaurantAdmin${user.userId}" hidden="true" title="Add Restaurant"  style="width:400px;">
<input type="hidden" id="userId" name="userId" value="${user.userId}" />
<input type="button" id="addRestaurant" onclick ="closeDialog('restaurantCatch${user.userId}')" value="Add Resaturant" />
</form:form>
<div id="fulfillmentCenterDiv" hidden="true" title="Add fulfillment Center">
<c:forEach items="${fulfillmentCenterList}" var="kitchenScreen">
			<c:set var="addOnDup" value="${true}"/>
					<c:forEach items="${user.kitchenId}" var="kitchenId">
						<c:if test="${kitchenId==kitchenScreen.id}">
							<c:set var="addOnDup" value="${false}"/>
							<td><input type="checkbox" name="kitchenId" id="kitchenId"  checked value="${kitchenScreen.id}" >${kitchenScreen.name}</td><br>
						</c:if>
					</c:forEach>
					 <c:if test="${addOnDup}">
					 <td><input type="checkbox" name="kitchenId" id="kitchenId" value="${kitchenScreen.id}" >${kitchenScreen.name}</td><br>
					 </c:if>
					 
</c:forEach>
</div>

<div id="restaurantDiv" hidden="true" title="Add Restaurant ">
<c:forEach items="${restaurantList}" var="restList">
					<c:set var="addOnDup" value="${true}"></c:set>
					<c:forEach items="${user.restaurantId}" var="restId">
 					<c:if test="${restId==restList.restaurantId}">
 					<c:set var="addOnDup" value="${false}"></c:set>
						<td><input type="checkbox" name="restaurantId" id="restaurantId" checked value="${restList.restaurantId}" >${restList.restaurantName}</td><br>
					</c:if>					
					</c:forEach>
				    <c:if test="${addOnDup}">
					 <td><input type="checkbox" name="restaurantId" id="restaurantId" value="${restList.restaurantId}" >${restList.restaurantName}</td><br>
					</c:if>
</c:forEach>
</div>

<div id="microKitchenDiv" hidden="true" title="Add Micro Kitchen ">
<c:forEach items="${microKitchenScreenList}" var="micrKitchenList">
					<c:set var="addOnDup" value="${true}"></c:set>
					<c:forEach items="${user.microKitchenId}" var="mkId">
						<c:if test="${mkId==micrKitchenList.id}">
						<c:set var="addOnDup" value="${false}"></c:set>
							<td><input type="checkbox" name="microKitchenId" id="microKitchenId" checked value="${micrKitchenList.id}" >${micrKitchenList.name}</td><br>
						</c:if>
					</c:forEach>
					 <c:if test="${addOnDup}">
						<td><input type="checkbox" name="microKitchenId" id="microKitchenId" value="${micrKitchenList.id}" >${micrKitchenList.name}</td><br>
					</c:if> 
</c:forEach>
</div>
<a href="index.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add User</h3>
<form:form method="get" action="organization/addEmployee.html" commandName="user">
	<form:hidden path="userId"/>
	<%-- <form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/> --%>
	<form:hidden path="orgId" value='<%=request.getSession().getAttribute("parentRestaurantId")%>'/>
	<table>
		<tr>
			<td style="width: 180px;"><form:label path="firstName">First Name* </form:label></td>
			<td><form:input path="firstName" required="true" maxlength="50" class="validate[required]"  /></td> 
		</tr>
		<tr>
			<td><form:label path="lastName">Last Name </form:label></td>
			<td><form:input path="lastName"  maxlength="50" class="validate[required]" /></td> 
		</tr>
		<tr>
			<td><form:label path="contact">Contact No*</form:label></td>
			<td><form:input path="contact" maxlength="10" required="true"  /></td> 
		</tr>	
		<tr>
			<td><form:label path="address1">Address</form:label></td>
			<td><form:textarea path="address1" maxlength="100"  class="validate[required]" style="width:173px" /></td> 
		</tr>
		<tr>
			<td><form:label path="userName">Email Address*</form:label></td>
			<td><form:input id="userName" path="userName" required="true" maxlength="50" onblur="validateEmail()" /></td>
			<td><form:errors path="userName"  style="color: red; padding-left: 5px;"/></td>
		</tr>
		<tr>
			<td><form:label path="passwordHash">Password*</form:label></td>
			<td><form:input path="passwordHash" required="true" maxlength="100" class="validate[required]" type="password" /></td>
		</tr>
		<tr>
			<td><form:label path="dateOfBirth">Date of Birth </form:label></td>
            <td><form:input path="dateOfBirth"  id="dateOfBirth" name="dateOfBirth" style="width:173px" value="" readonly="readonly"/>
		</tr>
		<tr>
		    <td><form:label path="dateOfJoinning">Date of Joining </form:label></td>
            <td><form:input path="dateOfJoinning"  id="dateOfJoinning" name="dateOfJoinning" style="width:173px" value="" readonly="readonly"/>
		</tr>
		<tr>
		<td><label> Select User Roles* </label></td>
		<td>
		<select name="role" id="role" onchange="setFulillmentCenter()" style="width:173px"  class="validate[required]">
		<!-- <option value="" disabled selected style="display:none;">Select Role</option> -->
				<c:forEach items="${userRoles}" var="userRole">
					<c:choose>
						<c:when test="${userRole.id == user.role.id}">
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
		<tr><td id="colAddCenterLabel"></td><td id="colAddCenterButton"></td></tr>
		<tr>
		<td colspan="2">
				<c:choose>
					<c:when test="${!empty user.userId}"><input type="submit"  value="Save User" onclick="form.action='organization/updateEmployee';" onsubmit="validateEmail()"/><button type="button" onclick="document.location.href='organization/employee'">Cancel</button></c:when>
					<c:otherwise><input type="submit"  onclick="validateEmail()" value="Add User"/><input type="reset" value="Cancel"></c:otherwise>
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
	<th>Contact No</th>
	<th>userName</th>
	<th>User Role</th>
	<th>Fulfillment Center</th>
	<th>Restaurant</th>
<!-- 	<th>Additional Filfillment Center</th> -->
	<!-- <th>Address</th> -->
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${userList}" var="user">
		<tr>
		<td style="width:10%;">${user.firstName}   ${user.lastName}</td>
		<td style="width:10%;">${user.contact}</td>
		<td style="width:20%;">${user.userName}</td>
		<c:forEach items="${userRoles}" var="userRoles">
       		<c:if test="${userRoles.id==user.role.id}">
       		<td style="width:15%;">${userRoles.role}</td>
       		<c:set var="addOnDup" value="${userRoles.id}"/>
       		</c:if>
       	</c:forEach>
       	<c:if test="${user.role.id!=addOnDup && user.role.id!=''}">
       	  <td style="width:15%;">NA</td>
       	 </c:if>
		<td style="width:15%;"><c:forEach items="${fulfillmentCenterList}" var="kitchenScreen">
			<c:forEach items="${user.kitchenId}" var="userKitchen">
				<c:if test="${kitchenScreen.id==userKitchen }">
					${kitchenScreen.name} <br>
					<c:set var="addOnDup" value="${kitchenScreen.id}"/>
				</c:if>
			</c:forEach>
       	</c:forEach>
       	<c:if test="${!user.kitchenId.contains(addOnDup)}">
       	NA
       	</c:if></td>
       	<td style="width:15%;"><c:forEach items="${restaurantList}" var="restList">
			<c:forEach items="${user.restaurantId}" var="restaurantId">
				<c:if test="${restList.restaurantId==restaurantId }">
					${restList.restaurantName} <br>
					<c:set var="addOnDup" value="${restList.restaurantId}"/>
				</c:if>
			</c:forEach>
       	</c:forEach>
       	<c:if test="${!user.restaurantId.contains(addOnDup)}">
       	NA
       	</c:if></td>
       	
		<td><button type="button" onclick="deleteUser(${user.userId});">delete</button></td>
		<td><button type="button" onclick="window.location.href='organization/editEmployee/${user.userId}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
	$(function() {
		/* $("#employee").validationEngine(); */
		var role = $("#role option:selected").text();
		if(role=="fulfillmentCenterManager" || role=="deliveryManager" || role=="deliveryBoy" || role=="headChef"){
			$("#colAddCenterLabel").html("Add To Fulfillment Center :");
			$("#colAddCenterButton").html($("#fulfillmentCenterDiv").html());
		}
		else if(role=="microKitchenManager"|| role=="chef" || role=="headChef"){
			$("#colAddCenterLabel").html("Add Micro Kitchen Center :");
			$("#colAddCenterButton").html("<button type=\"button\" id=\"MKCenter\" style=\"width:173px\" onclick=\"addMKCenter('microKitchen${user.userId}');\">Add To Micro Kitchen</button>");
			$("#colAddCenterButton").html($("#microKitchenDiv").html());
			}
		else if(role=="restaurantManager"){
			$("#colAddCenterLabel").html("Add To Restaurant :");
			$("#colAddCenterButton").html($("#restaurantDiv").html());
			}
		else{
			$("#colAddCenterLabel").html("");
			$("#colAddCenterButton").html("");
		}
		});
	
	function closeDialog(dialogId){
		$("#"+dialogId).dialog('close');
		}
	function setFulillmentCenter(){
		var role = $("#role option:selected").text();
		if(role=="fulfillmentCenterManager" || role=="deliveryManager" || role=="deliveryBoy" || role=="headChef"){
			$("#colAddCenterLabel").html("Add To Fulfillment Center :");
			$("#colAddCenterButton").html($("#fulfillmentCenterDiv").html());
		}
		else if(role=="microKitchenManager"|| role=="chef" || role=="headChef"){
			$("#colAddCenterLabel").html("Add Micro Kitchen Center :");
			$("#colAddCenterButton").html("<button type=\"button\" id=\"MKCenter\" style=\"width:173px\" onclick=\"addMKCenter('microKitchen${user.userId}');\">Add To Micro Kitchen</button>");
			$("#colAddCenterButton").html($("#microKitchenDiv").html());
			}
		else if(role=="restaurantManager"){
			$("#colAddCenterLabel").html("Add To Restaurant :");
			$("#colAddCenterButton").html($("#restaurantDiv").html());
				}
		
		else{
			$("#colAddCenterLabel").html("");
			$("#colAddCenterButton").html("");
		}
		}
	
	function addKitchen(employeeUserId){
		$("#"+employeeUserId).dialog({
			width: 500,
			modal: true
			});
	}

	function addMKCenter(employeeUserId){
		$("#"+employeeUserId).dialog({
			width: 500,
			modal: true
			});
	}
	
	function addResataurnt(employeeUserId){
			$("#"+employeeUserId).dialog({
				width: 500,
				modal: true
				});
	}
   function addAdmin(employeeUserId){
				$("#"+employeeUserId).dialog({
					width: 500,
					modal: true
	});
	}
  function validateEmail(){
	  var emailId = document.getElementById("userName");
      var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
		   if (reg.test(emailId.value) == false) 
		      {
		         alert('Invalid Email Address');
		         return false;
		      }
		    return true;
    }
  $(function(){
		$("#dateOfBirth").datepicker({
		    dateFormat: 'yy-mm-dd',
			yearRange:"-70: -14",
			maxDate:"-14Y",
			minDate:"-70Y",
			changeYear:true,
			changeMonth:true
		})
	})
	$(function(){
		$("#dateOfJoinning").datepicker({
		    dateFormat: 'yy-mm-dd',
			maxDate:"0Y",
			minDate:"-40Y",
			changeYear:true,
			changeMonth:true
		})
	})
	
	$("#dateOfJoinning").val($.datepicker.formatDate("yy-mm-dd", new Date()));
    $("#userName").value="";
    $("#passwordHash").value="";
    
</script>
</html>
