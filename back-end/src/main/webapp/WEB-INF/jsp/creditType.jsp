<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Credit Type Manager</title>
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
	<script type="text/javascript">
		 function deleteTax(Id) {
			if (confirm('Do you really want to delete this Credit Type')) {
				window.location.href = 'organization/deleteCustomerCreditType/' + Id;
			} 
			
		} 
		
	</script>
	
</head>
<body>
<a href="index.jsp" style="float: right;">Return to Home</a>
<hr/>


<h3>Add Credit Type</h3>
<form:form method="post" action="organization/addCreditType" commandName="creditType">
	
	<form:hidden path="id" />
	<form:hidden path="orgId" value='<%=request.getSession().getAttribute("parentRestaurantId")%>'/>
	<c:set var="result" value='<%=request.getParameter("result")%>'></c:set>
	<c:set var="message" value='<%=request.getParameter("message")%>'></c:set>
	<table>
	 <c:if test="${result == 'ERROR'}">
	<tr>
	<td colspan="2"><label id="errorMessage" style="color:red">${message}</label></td>
	</tr>
	</c:if>
	<c:if test="${result== 'SUCCESS'}">
	<tr>
	<td colspan="2"><label id="errorMessage" style="color:green">${message}</label></td>
	</tr>
	</c:if> 
	
	<tr>
		<td><form:label path="name">Name*</form:label></td>
		<td><form:input path="name" maxlength="45"  onclick="removeLabel()" required="True"/></td> 
	</tr>
	<tr>
		<td><form:label path="banner">Banner*</form:label></td>
		<td><form:input path="banner" maxlength="45"  onclick="removeLabel()" required="True"/></td> 
	</tr>
	<tr>
		<td><form:label path="maxLimit">Max Limit*(credit limit per customer)</form:label></td>
		<td><form:input path="maxLimit" class="validate[required]" required="True"/></td> 
	</tr>
	<tr><td>Billing Cycle*</td>
			<td><select name="billingCycle">
				<c:forEach items="${billingCycles}" var="billingCycle">
					<c:choose>
						<c:when test="${billingCycle.key == creditType.billingCycle}">
							<option value="${billingCycle.key}" selected="selected">${billingCycle.value}</option>
						</c:when>
						<c:otherwise>
							<option value="${billingCycle.key}">${billingCycle.value}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="Add credit Type"/>
		</td>
	</tr>
</table>	
</form:form>
<hr/>	
<h3>Credit Type List</h3>
<c:if  test="${!empty listCreditType}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Banner</th>
	<th>Billing Cycle </th>
	<th>Max Limit</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>	
</tr>
<c:forEach items="${listCreditType}" var="creditType">
	<tr>
		<td style="width:30%">${creditType.name}</td>
		<td style="width:30%">${creditType.banner}</td>
		 <c:forEach items="${billingCycles}" var="billingCycle">
			<c:if test="${billingCycle.key == creditType.billingCycle}">
				<td style="width:40%">${billingCycle.value}</td>
			</c:if>				
		</c:forEach>
		<td style="width:10%">${creditType.maxLimit}</td> 
		<td style="width:10%"><button type="Button" onclick="deleteTax(${creditType.id});">delete</button></td>
		<td style="width:10%"><button type="Button" onclick="window.location.href='organization/editCreditType/${creditType.id}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>
</body>
<script>
function removeLabel(){
	$("#errorMessage").hide();
	$("#errorMessage").property("hidden");
}
</script>
</html>
