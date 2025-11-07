<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Tax Manager</title>
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
			if (confirm('Do you really want to delete this Tax')) {
				window.location.href = 'taxTypes/delete/' + Id;
			} 
			
		}
		function showTaxDiv(){
			if($('#override').is(':checked')){
				 $("#taxDiv").html($("#postTaxList").html());
				 $("#selectText").show();
			}
			else {
				$("#selectText").hide();
				$("#taxDiv").html("");
				}
			}

		  window.onload = function() {
	               var azimuth = 0;
	            	var value= "${taxType.overridden}";
	            	if(value!="" && value!=undefined){
	            		$('#override').prop('checked', true);
	            		showTaxDiv();
		            	}
	               
	      }
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<form:form  commandName="taxType">
<div id="postTaxList" hidden="true">
<form:select path="overridden" id="sel" >
<c:forEach items="${taxTypeList}" var="taxType">
		<c:if test="${taxType.dishType=='Default'}">
		<c:choose>
						<c:when test="${taxType.taxTypeId == taxType.overridden}">
							<td><form:option value="${taxType.taxTypeId}" selected="selected">${taxType.name}</form:option></td>
						</c:when>
						<c:otherwise>
							<td><form:option value="${taxType.taxTypeId}">${taxType.name}</form:option></td>
						</c:otherwise>
	    </c:choose>
		</c:if>
</c:forEach>
</form:select>
</div>
</form:form>

<h3>Add TaxType</h3>
<form:form method="post" action="taxTypes/add.html" commandName="taxType">
	
	<form:hidden path="taxTypeId" />
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name">name*</form:label></td>
		<td><form:input path="name" maxlength="45"  required="True"/></td> 
	</tr>
	<tr><td>Charge Type*</td>
			<td><select name="chargeType">
				<c:forEach items="${chargeTypes}" var="chargeType">
					<c:choose>
						<c:when test="${chargeType == taxType.chargeType}">
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
		<td><form:label path="taxValue">Value*</form:label></td>
		<td><form:input type="number" step="0.01"  path="taxValue" required="true" /></td> 
	</tr>
	<tr><td>Override</td><td><input type="checkbox" onclick="showTaxDiv()" id="override"/></td></tr>
	<tr><td id="selectText" style="display:none">Please select tax to override :</td><td id="taxDiv" ></td></tr>
	<tr>
	<td>Select Dish Type </td>
	<td><form:select path="dishType">
	<option value="Default" >Default</option>
				<c:forEach items="${dishType}" var="dishType">
					<c:choose>
						<c:when test="${dishType.name == taxType.dishType}">
							<option value="${dishType.name}" selected="selected">${dishType.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${dishType.name}" >${dishType.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="Add taxType"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>Tax Types</h3>
<c:if  test="${!empty taxTypeList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Type</th>
	<th>Value</th>
	<th>Dish Type</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>	
</tr>
<tr></tr>
<tr>
<th align="left">DEFAULT TAXES</th>
<th>&nbsp;</th>
<th>&nbsp;</th>	
<th>&nbsp;</th>
<th>&nbsp;</th>	
<th>&nbsp;</th>	
</tr>
<c:forEach items="${taxTypeList}" var="taxType">
	<c:if test="${taxType.dishType=='Default'}">
	<tr>
		<td>${taxType.name}</td>
		 <td>${taxType.chargeType}</td> 
		<td>${taxType.taxValue}</td>
		<td>${taxType.dishType}</td>
		<td><button type="Button"  onclick="deleteTax(${taxType.taxTypeId});">delete</button></td>
		<td><button type="Button" onclick="window.location.href='taxTypes/edit/${taxType.taxTypeId}';">edit</button></td>
	</tr>
	</c:if>
</c:forEach>
<tr><th align="left">OVERRIDDEN TAXES</th>
<th>&nbsp;</th>
<th>&nbsp;</th>	
<th>&nbsp;</th>
<th>&nbsp;</th>	
<th>&nbsp;</th>	
</tr>
<c:forEach items="${taxTypeList}" var="taxType">
	<c:if test="${taxType.dishType!='Default'}">
	<tr>
		<td>${taxType.name}</td>
		 <td>${taxType.chargeType}</td> 
		<td>${taxType.taxValue}</td>
		<td>${taxType.dishType}</td>
		<td><button type="Button"  onclick="deleteTax(${taxType.taxTypeId});">delete</button></td>
		<td><button type="Button" onclick="window.location.href='taxTypes/edit/${taxType.taxTypeId}';">edit</button></td>
	</tr>
	</c:if>
	</c:forEach>
</table>
</c:if>
</body>
<script>

<%-- $(function () {
	var val =$('#dropDownId :selected').text();
	       alert(val +"--"+$('#dropDownId :selected').val());
	    	    var s="<%=taxTypeList%>"; 
	    	    alert(s); 
	    	 
	   }); --%>

	 
</script>
</html>
