<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Payment Type Manager</title>
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
<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
<script type="text/javascript">
function validate(){
	var selectedValue = $("#type option:selected").val();
	if(selectedValue=="selectType"){
		alert("Please select Type");
	}
	else {
		var frm = document.getElementById("paymentTy");
		frm.submit();
	}
}
</script>
<hr/>
<h3>Add Payment Type</h3>

<form:form method="post" action="organization/addPaymentType.html" id ="paymentTy" commandName="paymentType">
	
	<form:hidden path="paymentTypeId" />
	<form:hidden path="orgId" value='<%=request.getSession().getAttribute("parentRestaurantId")%>'/>
	<table>
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name"  maxlength="45" required="true"/></td> 
	</tr>
	
	<tr>
	<td>Select Type</td><td><select name="type" id="type">
	<option value="selectType" selected="selected">Please Select Type</option>
	<c:forEach items="${basePaymentType}" var="paymentTypes">
	<c:choose>
		<c:when test="${paymentTypes== paymentType.type}">
			<option value="${paymentTypes}" selected="selected">${paymentTypes}</option>
		</c:when>
		<c:otherwise>
			<option value="${paymentTypes}">${paymentTypes}</option>
		</c:otherwise>
	</c:choose>
	</c:forEach>
</select></td>
	</tr>
	
	<tr>
	<td>Status</td><td><select name="status">
	<c:forEach items="${statusTypes}" var="statusType">
	<c:choose>
		<c:when test="${statusType == paymentTy.status }">
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
			<input type="button" onclick="validate()" value="Add paymentType"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>paymentType</h3>
<c:if  test="${!empty paymentTypeList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Types</th>
	<th>Status</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${paymentTypeList}" var="paymentType">
	<tr>
		<td>${paymentType.name}</td>
		<td>${paymentType.type}</td>
		<td>${paymentType.status}</td>
		<td><a href="organization/deletePaymentType/${paymentType.paymentTypeId}">delete</a></td>
		<td><a href="organization/editPaymentType/${paymentType.paymentTypeId}">edit</a></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
