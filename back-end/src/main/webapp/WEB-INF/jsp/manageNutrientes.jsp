<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Manage Nutritional Info</title>
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
	<script type="text/javascript" src="js/nicEdit.js"></script>
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
	<script type="text/javascript">
		function deleteNutrient(Id) {
			if (confirm('Do you really want to delete this Field')) {
				window.location.href = 'restaurant/deleteNutrientes/' + Id;
			} 
		}
		$(function() {
			var val= $("#addOnDishType option:selected").val();
			if(val!="pleaseSelect"){
				$('#types option[value=addOnDishType]').attr('selected','selected');
				}
			setSelect();
		});
		
		function setSelect(){
			var val= $("#types option:selected").val();
			if(val=="addOnDishType"){
				$("#colAddTypeListName").html("AddOn Dish Types");
				$("#colAddTypeList").html($("#addOnDishType").html());
			}
			else if(val=="dishTypeVal"){
				$("#colAddTypeListName").html("Dish Types");
				$("#colAddTypeList").html($("#dishTypeVal").html());
			}
		}

		function validate(){
			var valType= $("#types option:selected").val();
			if(valType=="addOnDishType"){
				document.forms["submitForm"].submit();
			}
			else {
				document.forms["submitForm"].submit();
				}
			}
		function get(){
			var e = document.getElementById("addOnDishTypes");
			console.log(e.selectedIndex);
			var strUser = e.options[e.selectedIndex].text;
			}

	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<h3>Add Nutrientes</h3>

<form:form method="post" action="restaurant/addNutrientes.html" commandName="Nutrient">

<div id="addOnDishType" hidden="true" >
	<form:select path="dishType" id="addOnDishTypes" onchange="get()">
			<option value="pleaseSelect" >Please select </option>
				<c:forEach items="${addOnDishType}" var="dishType">
					<c:choose>
						<c:when test="${dishType.name == Nutrient.dishType}">
							<option value="${dishType.name}" selected="selected">${dishType.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${dishType.name}" >${dishType.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></div>
			
<div id="dishTypeVal" hidden="true" >
	<form:select path="dishType">
				<c:forEach items="${dishType}" var="dishType">
					<c:choose>
						<c:when test="${dishType.name == Nutrient.dishType}">
							<option value="${dishType.name}" selected="selected">${dishType.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${dishType.name}" >${dishType.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select></div>

</form:form>

<form:form id="submitForm" method="post" action="restaurant/addNutrientes.html" commandName="Nutrient">
	
	<form:hidden path="id" />
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table>
	
	<tr>
		<td><form:label path="name">name*</form:label></td>
		<td><form:input path="name" maxlength="45"  required="True"/></td> 
	</tr>
	
	<tr><td>Please select Type</td><td><select id="types" onchange="setSelect()" >
	<option value="dishTypeVal">For Dishes</option>
	<option value="addOnDishType">For AddOn</option>
	 </select></td></tr>
	<tr><td id="colAddTypeListName"></td><td id="colAddTypeList" ></td></tr>
	<tr>
		<td colspan="2">
			<input type="button" onclick="validate()" value="Add Nutrients"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>	
<h3>Nutrientes Hub</h3>
<c:if  test="${!empty nutrientList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Dish Type</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>	
	
</tr>
<c:forEach items="${dishType}" var="dishType">
<c:forEach items="${nutrientList}" var="nutrientList">
	<c:if test="${dishType.name == nutrientList.dishType}">
	<tr>
		<td>${nutrientList.name}</td>
		<td>${nutrientList.dishType}</td>
		<td><button type="Button"  onclick="deleteNutrient(${nutrientList.id});">delete</button></td>
		<td><button type="Button" onclick="window.location.href='restaurant/editNutrientes/${nutrientList.id}';">edit</button></td>
	</tr>
	</c:if>
	</c:forEach>
</c:forEach>
</table>
<table class="data">
<tr>
	<th>Name</th>
	<th>AddOn Type</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>	
</tr>
<c:forEach items="${addOnDishType}" var="dishType">
<c:forEach items="${nutrientList}" var="nutrientList">
	<c:if test="${dishType.name == nutrientList.dishType}">
	<tr>
		<td>${nutrientList.name}</td>
		<td>${nutrientList.dishType}</td>
		<td><button type="Button"  onclick="deleteNutrient(${nutrientList.id});">delete</button></td>
		<td><button type="Button" onclick="window.location.href='restaurant/editNutrientes/${nutrientList.id}';">edit</button></td>
	</tr>
	</c:if>
	</c:forEach>
</c:forEach>
</table>
</c:if>
</body>
</html>
