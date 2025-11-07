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
</head>
<body>
<hr/>


<b>${menu.name}</b>
<br/>
<b>${menu.description}</b>
<hr/>

<c:forEach items="${menu.sections}" var="section">
		
<b>${section.name} ( ${section.description} )</b>
<table class="data">	
<c:forEach items="${section.dishes}" var="dish">
	<tr>
		<td>${dish.name}</td>
		<td>${dish.description}</td>
		<td>${dish.price}</td>
		
	</tr>

</c:forEach>
</table>	
</c:forEach>


</body>
</html>
