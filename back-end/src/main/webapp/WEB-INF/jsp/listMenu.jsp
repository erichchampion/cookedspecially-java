<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Menu Manager</title>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css" />
	<style type="text/css">
		.data, .data td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.data th {
			font-weight: bold;
			background-color: #808080;
			color: white;
		}
	</style>

<script type="text/javascript">
function deleteMenu(menuId) {
	if (confirm('Do you really want to delete this Menu')) {
		window.location.href = 'menu/delete/' + menuId;
	}
}
</script>
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<a href="menu/create">Create Menu</a><br>
<c:set var="sessionUserId" value='<%=request.getSession().getAttribute("userId")%>'/>
<c:if test='${!empty sessionUserId}'>
Logged in as <%=request.getSession().getAttribute("username")%> <a href="user/logout" style="float: right;">Logout</a>
</c:if>

<hr/>

<h3>Menus</h3>
<c:if  test="${!empty menuList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${menuList}" var="menu">
	<tr>
		<td>${menu.name}</td>
		<td>${menu.description}</td>
		<td><button type="button" onclick="deleteMenu(${menu.menuId})">Delete</button></td>
		<td><button type="button"  onclick="window.location.href='menu/edit/${menu.menuId}';">Edit</button></td>
		<td><button type="button"  onclick="window.location.href='menu/show/${menu.menuId}';">Show</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
