<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
	<title>Edit a check</title>
	
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
	
</head>
<body>
<a href="order/searchChecks" style="float: right;">Return to SearchCheck</a>
<hr/>

<h3>Edit Check</h3>

<form id="updateCheckForm" action="order/updateCheck" method="post">
    <input type="hidden" name="checkId" value="${checkResponse.id}"/> <br/>
    <label>Discount Percentage : </label>
    <input type="text" name="discountPercent" value="${checkResponse.discountPercent}"/><br/>
    <label>Discount Amount (will be ignored if percentage is non zero) : </label>
    <input type="text" name="discountAmount" value="${checkResponse.discountAmount}"/><br/>
    <label>Status</label>
	<select name="status">
		<c:forEach items="${statusTypes}" var="statusType">
			<c:choose>
				<c:when test="${statusType == checkResponse.status}">
					<option value="${statusType}" selected="selected">${statusType}</option>
				</c:when>
				<c:otherwise>
					<option value="${statusType}">${statusType}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
    <input type="submit" value="Update Check" />
</form>


<hr/>
	
<table align="center" width="100%">
<tr>
<th width="33%">Item</th>
<th width="33%">Quantity</th>
<th width="33%">Amount</th>
</tr>
<c:if test="${!empty itemsMap}">
<c:forEach items="${itemsMap}" var="item">
	<tr>
		<td width="33%" align="center">${item.value.name}</td>
		<td width="33%" align="center">X ${item.value.quantity}</td>
		<td width="33%" align="center">${item.value.price}</td>
	</tr>
</c:forEach>
</c:if>
</table>
<hr/>
<div style="float:left">Sub Total</div>  <div style="float:right">${checkResponse.amount}</div><br/>
<div style="float:left">Discount</div>  <div style="float:right">${checkResponse.discountAmount}</div><br/>
<div style="float:left">Total After Discount</div>  <div style="float:right">${checkResponse.amountAfterDiscount}</div><br/>
<c:if test = "${!empty checkResponse.additionalChargeName1}">
<div style="float:left">${checkResponse.additionalChargeName1}</div> 
<div style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkResponse.additionalCharge1}" />
</div><br/>
</c:if>
<c:if test = "${!empty checkResponse.additionalChargeName2}">
<div style="float:left">${checkResponse.additionalChargeName2}</div> 
<div style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkResponse.additionalCharge2}" />
</div><br/>
</c:if>
<c:if test = "${!empty checkResponse.additionalChargeName3}">
<div style="float:left">${checkResponse.additionalChargeName3}</div> 
<div style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkResponse.additionalCharge3}" />
</div><br/>
</c:if>
<c:forEach items="${checkRespone.taxDetails}" var="item">
 <c:if test="${item.value !=null}">
<div style="float:left">${item.key}</div>
<div style="float:right"><fmt:formatNumber type="number" pattern="###.##" value="${item.value}" /></div>
</c:if>
</c:forEach> 

<c:if test = "${checkRespone.outCircleDeliveryCharges > 0}">
<div style="float:left">Delivery Charges</div> 
<div style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkRespone.outCircleDeliveryCharges}" />
</div>
</c:if>

<div style="float:left">Grand Total</div> 
<div style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkResponse.total}" />
</div><br/>
<div style="float:left">Rounded-off Total</div> <div style="float:right">${checkResponse.roundedOffTotal}</div><br/>
<hr/>

<div align="center">Invoice No.: ${checkResponse.invoiceId}</div>
<br/>



</body>
</html>
