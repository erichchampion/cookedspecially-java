<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Social Connectors</title>
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
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/ui/jquery-ui.js"></script>

<script type="text/javascript">
 function validate(){
	 var isEditabe = "${editable}";
	if(isEditabe=="false"){
		var value = $("#name").val();
			var connectorArrayString = "${connectorsList}";
			var arrayLength  = connectorArrayString.split(",");
			var isMatched=false;
			 for(var i=0;i<arrayLength.length;i++){
				 var itrVal="";
				 if(i==0){
			   		  itrVal = "${connectorsList[0].name}"
				 }else if(i==1){
			   		  itrVal = "${connectorsList[1].name}"
				 }else if(i==2){
			   		  itrVal = "${connectorsList[2].name}"
				 } else if(i==3){
			   		  itrVal = "${connectorsList[3].name}"
				 }else if(i==4){
			   		  itrVal = "${connectorsList[4].name}"
				 }else if(i==5){
			   		  itrVal = "${connectorsList[5].name}"
				 }

				 if(itrVal==value){
					 alert(value+" is already added");
						isMatched=true;
						return false;
					 }
				} 
	}
			//return false;
			document.getElementById('connectorForm').submit();			
	 }
 </script>
 
</head>
<body>
<a href="index.jsp"  id="adminPage" style="float:right">Return to Home</a>
<hr/>
<h3>Add Social Connectors </h3>
<form:form method="post" id="connectorForm" action="socialConnector/add.html" commandName="connector">

	<form:hidden path="id" />
	<form:hidden path="organizationId" value='<%=request.getSession().getAttribute("organisationId")%>'/>
	<table>
	<c:if test="${connectorTypes !=null}">
	<tr><td>Select Connector :</td><td><select id="name" name="name">
	
	<c:forEach items="${connectorTypes}" var="type">
	<c:choose>
		<c:when test="${type == connector.name }">
			<option value="${type}" selected="selected">${type}</option>
		</c:when>
		<c:otherwise>
			<option value="${type}">${type}</option>
		</c:otherwise>
	</c:choose>
	</c:forEach>
	</select></td></tr>
		<tr>
	<td>App Id </td>
	<td><form:input type="text"  maxlength="150" required="true" path="appId" /></td>
	
	</tr>
	<tr><td>Status </td><td><select name="status">
	<c:forEach items="${statusTypes}" var="statusType">
	<c:choose>
		<c:when test="${statusType == connector.status }">
			<option value="${statusType}" selected="selected">${statusType}</option>
		</c:when>
		<c:otherwise>
			<option value="${statusType}">${statusType}</option>
		</c:otherwise>
	</c:choose>
	</c:forEach>
	</select></td></tr>
	<tr>
		<td colspan="2">
			<input type="button" onclick="validate()" value="Add Connector"/>
		</td>
	</tr>
	</c:if>
</table>	
</form:form>
<hr/>	
<h3>Social Connectors</h3>
<c:if  test="${!empty connectorsList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>App Id</th>
	<th>Status</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${connectorsList}" var="socialCon">
	<tr>
		<td>${socialCon.name}</td>
		<td>${socialCon.appId}</td>
		<td>${socialCon.status}</td>
		<td><a href="socialConnector/delete/${socialCon.id}">delete</a></td>
		<td><a href="socialConnector/edit/${socialCon.id}">edit</a></td>
	</tr>
</c:forEach>
</table>
</c:if>

</body>
</html>
