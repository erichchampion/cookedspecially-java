<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Menu Card</title>
	<base href="${pageContext.request.contextPath}/"/>
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
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css" />
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$('.addDish').click(function() { 
			var dishId = $(this).attr('data-dishId');
			//alert(currentcheckId);
			$('#dishCheck'+dishId).click();
			//alert(currentcheckId);
		        
		});
	});
	</script>	
</head>
<body>
<hr/>


<b>${menu.name}</b>
<br/>
<b>${menu.description}</b>
<hr/>
<c:set var="lastCategoryId" value="0"/>
<c:set var="tabOpen" value="0"/>
<c:forEach items="${menu.dishes}" var="dish">
	
		<c:if test="${lastCategoryId!=dish.category.categoryId}">
			<c:if test="${lastCategoryId!=0 && tabOpen!=0}">
				
				<c:set var="tabOpen" value="0"/>
				
				
				</table>
			</c:if>
			<c:set var="lastCategoryId">${dish.category.categoryId}</c:set>

			<b>${dish.category.name} ( ${dish.category.description} )</b>
			<table class="data">
			<c:set var="tabOpen" value="1" />			
		</c:if>
	
	<tr>
		<td>${dish.name}</td>
		<td>${dish.description}</td>
		<td>${dish.price}</td>
		<td id="dish_check_${dish.dishId}">
			<span class="addDish" id="adddishCheck${dish.dishId}" data-dishId="${dish.dishId}">Add </span>
			<span class="removeDish" id="removedishCheck${dish.dishId}" data-dishId="${dish.dishId}" hidden="true">Remove </span>
		</td>
	</tr>
	
	
</c:forEach>
<c:if test="${tabOpen!=0}">
</table>
</c:if>

<form method="post" action="order/save.html">
<c:forEach items="${menu.dishes}" var="dish">
 <input type="checkbox" id="dishCheck${dish.dishId}" name="dishIds" value="${dish.dishId}" />
</c:forEach> 
</form>
<!-- <table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>Category</th>
</tr>

<c:forEach items="${menu.dishes}" var="dish">
	<tr>
		<td>${dish.name}</td>
		<td>${dish.description}</td>
		<td>${dish.category.name} - ${dish.category.description}</td>
	</tr>
</c:forEach>
</table>
-->



</body>
</html>
