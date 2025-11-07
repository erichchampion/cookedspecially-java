<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Receipt</title>
	
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
	
</head>
<body>
<hr/>
	
<h3>Receipt</h3>
Check Id: ${checkResp.id} <br />
Table Id: ${checkResp.tableId} <br />
Customer Id: ${checkResp.customerId} <br />
<c:if  test="${!empty checkResp.items}">

<table class="data">
<tr>
	<th>Name</th>
	<th>Price</th>
</tr>
<c:forEach items="${checkResp.items}" var="item">
	<tr>
		<td>${item.name}</td>
		<td>${item.price}</td>
	</tr>
</c:forEach>
</table>
</c:if>

<table class="data">
<tr>
	<td>Total</td>
	<td>${checkResp.amount }</td>
</tr>
<c:if test = "${!empty checkResp.additionalChargeName1}">
<tr>
	<td>${checkResp.additionalChargeName1}</td>
	<td>${checkResp.additionalCharge1}</td>
</tr>
</c:if>
<c:if test = "${!empty checkResp.additionalChargeName2}">
<tr>
	<td>${checkResp.additionalChargeName2}</td>
	<td>${checkResp.additionalCharge2}</td>
</tr>
</c:if>
<c:if test = "${!empty checkResp.additionalChargeName3}">
<tr>
	<td>${checkResp.additionalChargeName3}</td>
	<td>${checkResp.additionalCharge3}</td>
</tr>
</c:if>
<tr>
	<td>Grand Total</td>
	<td>${checkResp.total}</td>
</tr>

<tr>
	<td>${item.name}</td>
	<td>${item.price}</td>
</tr>

</table>

</body>
</html>
