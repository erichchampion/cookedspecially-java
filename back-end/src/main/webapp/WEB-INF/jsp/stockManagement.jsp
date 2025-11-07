<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<html>
<head>
	<title>Stock Management</title>
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

		function showDialogBox(){
			var val = $('#ffc').val();
			var va = "<c:out value='${i}' />"
	
			for(var i=0;i<=va;i++){
				var exist =	document.getElementById("manageStockN["+i+"].fulfillmentCenterId");
				if(exist!=null){
					document.getElementById("manageStockN["+i+"].fulfillmentCenterId").value=val;	
					}
				else{
					alert("Please! add Dishes to manage stock");
					}
			}
			$("#stockSection").dialog({width:1000});
			}
		function showEditDialogBox(){
			$("#editStockSection").dialog({width:1100});
			}
	
		function deleteStockedDish(stockDishId) {
			if (confirm('Do you really want to delete this Stock ')) {
				window.location.href = 'dish/deleteStockedDish/' + stockDishId;
			}
			}

		function editStockedDish(stockDishId){
			$("#editForm").show();
			if(confirm('Do you really want to Edit this Stock')){
			window.location.href='dish/editStockedDish/'+stockDishId;
			}
			}

		function getValueSet(id){
			document.getElementById(id).type ="datetime-local";
			}

		function setValue(id){
			document.getElementById(id).type ="text";
			var getTime = document.getElementById(id).value;
			enteredTime = new Date(getTime);
			var currentDate =  new Date();
			if(enteredTime<currentDate){
				alert("Invalid date!! please enter valid date");
				document.getElementById(id).value=currentDate;
				document.getElementById(id).type ="datetime-local";
				}
			}
		
		function setTime(){
			var val = document.getElementById("dateTime").value;

			enteredTime = new Date(val);
			var currentDate =  new Date();
			if(enteredTime<currentDate){
				alert("Invalid date!! please enter valid date");
				document.getElementById(id).value=currentDate;
				document.getElementById(id).type ="datetime-local";
				return false;
				}
			var va = "<c:out value='${i}' />"
			for(var i=0;i<=va;i++){
				document.getElementById("manageStockN["+i+"].expireDate").type ="text";
				document.getElementById("manageStockN["+i+"].expireDate").value=val;
				}
			}
		function setFulfilmentCneter(){
			var val = $('#ffc').val();                       
			var va = "<c:out value='${i}' />"
			for(var i=0;i<=va;i++){
				document.getElementById("manageStockN["+i+"].fulfillmentCenterId").value=val;
				}
			}
		function validateStock(){
			var val = $('#ffc').val();
			alert(val);
			if(val!="undefined" && val!=""){
				document.forms["stockSection"].submit();
				}else{
					alert("Please select FFC ");
					}
			}
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;"><%= request.getParameter("errorMsg")==null?"":request.getParameter("errorMsg")%></div>
<h3>Manage Stock</h3>
 <input type="button" onclick="showDialogBox()" value="Add New Stock" />
 <input type="button" onclick="showEditDialogBox()" value="Edit Current Stock" />

 <!-- Add new Stock form -->
<form:form  id="stockSection" hidden="true" commandName="pojoForm" action="dish/addStock.html" method="POST">
<input type="text" style="border:none;font-weight:500;float:left;width:100px;"  readonly="true" value="Select FFC :">
<select style="float:left" onchange="setFulfilmentCneter()" id="ffc">
       <c:forEach items="${fulfillmentCenter}" var="fulfillmentCenter">
			<option value="${fulfillmentCenter.id}">${fulfillmentCenter.name}</option>
		</c:forEach>
    </select>
    <input type="text"  style="border:none;font-weight:500;text-align:right;height:20px;width:212px" readonly="true" value="Common Expire Date">
    <input type="datetime-local" onblur="setTime()" id="dateTime" />
    <br/>
    
 <input type="text" size="30" style="background: grey;" readonly="true" value="Name">
 <input type="text"  style="background: grey;" readonly="true" value="Alert">
 <input type="text"  style="background: grey;" readonly="true" value="Add Stock">
 <input type="text"  style="background: grey; margin:5px;" size="28" readonly="true" value="Expire Date"><br/>
     <c:set var="cnt" value="${0}"/>
     <c:forEach items="${stockedDishes}" var="stockedDishes"  varStatus="i">
     <c:set var="occure"  scope="session" value='0'></c:set>
     <c:if test="${stockedDishes.dishSize.size() >0}">
    
     <c:forEach items="${stockedDishes.dishSize}" var="size" >
        <form:input hidden="true"  path="manageStock[${cnt}].fulfillmentCenterId" id="manageStockN[${cnt}].fulfillmentCenterId"/>
        <form:input hidden="true" path="manageStock[${cnt}].id" />
        <form:input hidden="true" path="manageStock[${cnt}].dishId" value="${stockedDishes.dishId}${size.dishSizeId}" />
        <form:input hidden="true" path="manageStock[${cnt}].restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
        <form:input path="manageStock[${cnt}].dishName" size="30" readonly="true" value="${stockedDishes.name} ${size.name}" />
        <c:set var="occure"  scope="session" value='1'/>
        <c:forEach items="${listOfStockedDishes}" var="lsd">
        <c:if test="${lsd.dishId==stockedDishes.dishId}">
        	<c:if test="${occure=='1'}">
            <form:input path="manageStock[${cnt}].alertQuantity" value="${lsd.alertQuantity}" />	
            <c:set var="occure"  scope="session" value='0'></c:set>
        </c:if>
        </c:if>
        </c:forEach>
         <c:if test="${occure=='1'}">
        <form:input path="manageStock[${cnt}].alertQuantity"  required="true"/>	
        </c:if>
         <form:input path="manageStock[${cnt}].addQuantity" required="true"/>
        <form:input type="datetime-local" style="width:250px; text-align:center" min="${date.toString()}" onfocus="getValueSet('manageStockN[${cnt}].expireDate')" onblur="setValue('manageStockN[${cnt}].expireDate')" id="manageStockN[${cnt}].expireDate" path="manageStock[${cnt}].expireDate" /><br/>
		<c:set var="i" scope="session" value="${cnt}"></c:set> 
		<c:set var="cnt" value="${cnt+1}"/>
    </c:forEach>
     </c:if>
      <c:if test="${stockedDishes.dishSize.size()==0}">
        <form:input hidden="true"  path="manageStock[${cnt}].fulfillmentCenterId" id="manageStockN[${cnt}].fulfillmentCenterId"/>
        <form:input hidden="true" path="manageStock[${cnt}].id" />
        <form:input hidden="true" path="manageStock[${cnt}].dishId" value="${stockedDishes.dishId}" />
        <form:input hidden="true" path="manageStock[${cnt}].restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
        <form:input path="manageStock[${cnt}].dishName" size="30" readonly="true" value="${stockedDishes.name}" />
        <c:set var="occure"  scope="session" value='1'/>
        <c:forEach items="${listOfStockedDishes}" var="lsd">
        <c:if test="${lsd.dishId==stockedDishes.dishId}">
        	<c:if test="${occure=='1'}">
            <form:input path="manageStock[${cnt}].alertQuantity" value="${lsd.alertQuantity}" />	
            <c:set var="occure"  scope="session" value='0'></c:set>
        </c:if>
        </c:if>
        </c:forEach>
        <c:if test="${occure=='1'}">
        <form:input path="manageStock[${cnt}].alertQuantity"  required="true"/>	
        </c:if>
        <form:input path="manageStock[${cnt}].addQuantity" required="true"/>
        <form:input type="datetime-local" style="width:250px; text-align:center" min="${date.toString()}" onfocus="getValueSet('manageStockN[${cnt}].expireDate')" onblur="setValue('manageStockN[${cnt}].expireDate')" id="manageStockN[${cnt}].expireDate" path="manageStock[${cnt}].expireDate" /><br/>
		<c:set var="i" scope="session" value="${cnt}"></c:set> 
		<c:set var="cnt" value="${cnt+1}"/>
      </c:if>
    </c:forEach>
    
    <input type="button" onclick="validateStock()" value="Add Stock" />
</form:form>
<!-- form end  --> 

<!-- Edit form -->
<form:form  id="editStockSection" hidden="true" commandName="pojoForm" label="Edit Stock" action="dish/addStock.html" method="POST">
  <input type="text" style="background: grey" readonly="true" value="Name">
  <input type="text" size="15" style="background: grey" readonly="true" value="Alert">
  <input type="text" style="background: grey" size="17" readonly="true" value="FFC">
  <input type="text" size="15" style="background: grey" readonly="true" value="Available Stock">
  <input type="text" size="15" style="background: grey" readonly="true" value="Remove Stock">
  <input type="text" size="15" style="background: grey" readonly="true" value="Expire Date"><br/>
    <c:forEach items="${listOfStockedDishes}" var="stockedDishes"  varStatus="i">
        <form:input hidden="true" path="manageStock[${i.index}].id" value="${stockedDishes.id}" />
        <form:input hidden="true" path="manageStock[${i.index}].dishId" value="${stockedDishes.dishId}" />
        <form:input hidden="true" path="manageStock[${i.index}].restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
       <form:input path="manageStock[${i.index}].dishName" readonly="true" value="${stockedDishes.dishName}" />
        <form:input path="manageStock[${i.index}].alertQuantity" size="15" value="${stockedDishes.alertQuantity}" />
       <td><form:select path="manageStock[${i.index}].fulfillmentCenterId" required="true">
       <c:forEach items="${fulfillmentCenter}" var="fulfillmentCenter">
			<c:choose>
			<c:when test="${stockedDishes.fulfillmentCenterId==fulfillmentCenter.id}">
			<option value="${fulfillmentCenter.id}" selected="selected">${fulfillmentCenter.name}</option>
			</c:when>
			<c:otherwise>
			<option value="${fulfillmentCenter.id}">${fulfillmentCenter.name}</option>
			</c:otherwise>
			</c:choose>
		</c:forEach>
       </form:select>
        <form:input path="manageStock[${i.index}].remainingQuantity" size="15"  readonly="true" value="${stockedDishes.remainingQuantity}"/>
        <form:input path="manageStock[${i.index}].removeQuantity" size="15" />
        <form:input type="text" onfocus="getValueSet('manageStock[${i.index}].expireDate')" onblur="setValue('manageStock[${i.index}].expireDate')" path="manageStock[${i.index}].expireDate" id="manageStock[${i.index}].expireDate"  value="${stockedDishes.expireDate}"/>
		<br/>
    </c:forEach><br/>
    <input type="submit" style="align:middle"  value="Save Stock" />
</form:form>
<!-- form end  -->
<hr/>
	<% pageContext.setAttribute("newLineChar", "\n"); %> 
<h3>Stocked Dishes</h3>
 <c:if  test="${!empty listOfStockedDishes}">
<table class="data">
<tr>
	<th>Dish Name</th>
	<th>Fulfillment Center </th>
	<th>Remaining Stock-(Expire Date)</th>
	<th>Alert On Quantity </th>
</tr>
<c:forEach items="${fulfillmentCenter}" var="fulfCenter">
<tr><td colspan="5" style="color: white;font-weight: bold;background-color: grey;">${fulfCenter.name}</tr>
<c:forEach items="${stockedDishes}" var="stockDishes">
	<c:if test="${stockDishes.dishSize.size() >0}">
	<c:forEach items="${stockDishes.dishSize}" var="size" >
	<c:set var="flag" scope="request" value="${''}"></c:set> 
	<c:set var="dishName" scope="request" value="${''}"></c:set> 
	<c:set var="ffc" scope="request" value="${''}"></c:set>  
	<c:set var="remSandExp" scope="request" value="${''}"></c:set>
	<c:set var="qntyAlert" scope="request" value="${''}"></c:set> 
	<c:set var="id" scope="request" value="${''}"></c:set> 
	
	<c:forEach items="${listOfStockedDishes}" var="stockDishe"> 
	<c:set var="id" value="${stockDishes.dishId}${size.dishSizeId}">  </c:set>
	<c:if test="${id==stockDishe.dishId}"> 
	<c:if test="${fulfCenter.id==stockDishe.fulfillmentCenterId}">
	<c:set var="dishName" scope="request" value="${stockDishe.dishName}"></c:set> 
	<c:set var="ffc" scope="request" value="${fulfCenter.name}"></c:set>  
	<c:set var="remSandExp" scope="request" value="${remSandExp}  ${stockDishe.remainingQuantity} - (${stockDishe.expireDate}) <br/>"></c:set>
	<c:set var="qntyAlert" scope="request" value="${stockDishe.alertQuantity}"></c:set>   
	<c:set var="id" scope="request" value="${stockDishe.id}"></c:set> 
	<c:set var="flag" scope="request" value="${1}"></c:set> 
	</c:if>
	</c:if>
	</c:forEach>
	<c:if test="${flag==1}">
	<tr> 
	<td style="width:20%;">${dishName}</td>
	<td style="width:20%;">${ffc}</td>
	<td style="width:20%;">${remSandExp}</td>
	<td style="width:20%;">${qntyAlert}</td>
	</tr>
	</c:if>
	</c:forEach>
	</c:if> 
	<c:if test="${stockDishes.dishSize.size()==0}">
	<c:set var="flag" scope="request" value="${''}"></c:set> 
	<c:set var="dishName" scope="request" value="${''}"></c:set> 
	<c:set var="ffc" scope="request" value="${''}"></c:set>  
	<c:set var="remSandExp" scope="request" value="${''}"></c:set>
	<c:set var="qntyAlert" scope="request" value="${''}"></c:set> 
	<c:set var="id" scope="request" value="${''}"></c:set> 
	<c:forEach items="${listOfStockedDishes}" var="stockDishe"> 
	<c:if test="${stockDishes.dishId==stockDishe.dishId}"> 
	<c:if test="${fulfCenter.id==stockDishe.fulfillmentCenterId}">
	<c:set var="dishName" scope="request" value="${stockDishe.dishName}"></c:set> 
	<c:set var="ffc" scope="request" value="${fulfCenter.name}"></c:set>  
	<c:set var="remSandExp" scope="request" value="${remSandExp}  ${stockDishe.remainingQuantity} - (${stockDishe.expireDate}) <br/>"></c:set>
	<c:set var="qntyAlert" scope="request" value="${stockDishe.alertQuantity}"></c:set>   
	<c:set var="id" scope="request" value="${stockDishe.id}"></c:set> 
	<c:set var="flag" scope="request" value="${1}"></c:set> 
	</c:if>
	</c:if>
	</c:forEach>
	<c:if test="${flag==1}">
	<tr> 
	<td style="width:20%;">${dishName}</td>
	<td style="width:20%;">${ffc}</td>
	<td style="width:20%;">${remSandExp}</td>
	<td style="width:20%;">${qntyAlert}</td>
	</tr>
	</c:if>
	</c:if>
	</c:forEach>
	</c:forEach>
</table>
</c:if> 
</body>
</html>
