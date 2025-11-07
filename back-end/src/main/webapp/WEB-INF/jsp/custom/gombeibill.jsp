<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 <h1 align="center">Gombei</h1>
<h3 align="center">Japanese Restaurant</h3>
<!-- <div align="center"><a href="#" align="center">www.Gombei.in</a></div>
<div align="center"><a href="http://www.facebook.com/Saladdays.co" align="center">www.facebook.com/Saladdays.co</a></div> -->
<!-- <div align="center">9643 800 901/02/03</div> -->
<div align="center">${checkDate}<!-- <span id='date-time'></span>  --></div>
<table align="center" width="100%">
<c:if  test="${!empty customer}">
<tr><td>Name: ${customer.firstName} ${customer.lastName}</td></tr>
<tr><td>Address:
	<c:choose>
	<c:when test="${!empty checkRespone.deliverAddress}">
		${checkRespone.deliverAddress}
	</c:when>
	<c:otherwise>
		${customer.address}
	</c:otherwise>
	</c:choose>
	</td>
</tr>
	<c:choose>
		<c:when test="${!empty checkRespone.deliveryArea}">
		<tr>
			<td>Delivery Area: ${customer.deliveryArea}</td>
		</tr><br/>
		</c:when>
	</c:choose>
<tr><td>Email Id: ${customer.email}</td></tr>
<tr><td>Phone No.: ${customer.phone}</td></tr>
 <tr><td>Delivery Time.: ${checkRespone.deliveryDateTime}</td></tr>
 <tr><td>Payment Status.: <b> ${checkRespone.status}</b></td></tr>
 <tr><td>Payment Mode: <b> ${checkRespone.paymentMode}</b></td></tr> 
</c:if>
<c:if test="${!empty tableId }">
<tr><td>Table ID: ${tableId}</td></tr> 
</c:if>
</table>
<table align="center" width="100%">
<tr>
<th width="34%">Item</th>
<th width="33%">Quantity</th>
<th width="33%">Amount</th>
</tr>
<c:if test="${!empty itemsMap}">
<c:forEach items="${itemsMap}" var="item">
	<tr>
		<td width="34%">${item.value.name}</td>
		<td width="33%" align="center" >X ${item.value.quantity}</td>
		<td width="33%" align="center" >${item.value.price}</td>
	</tr>
	 <c:forEach items="${item.value.addOns}" var="itemAdd"> 
	<c:if test="${item.value.addOns !=null && itemAdd.dishId == item.value.id}" >
	<tr>
	<td width="34%"><b>Add-On :</b> ${itemAdd.name}</td>
		<td width="33%" align="center" >X ${item.value.quantity}</td>
		<td width="33%" align="center" >${itemAdd.price * item.value.quantity}</td>
	</tr>
	</c:if>
	</c:forEach>
</c:forEach>
</c:if>
</table>
<hr/>
<table id="checkData"  width="100%">
<tr>
	<td>Sub Total</td> 
	<td style="float:right">${checkRespone.amount}</td>
	</tr> 
<c:if test="${checkRespone.discountAmount > 0}">
	<tr>
		<td>Discount</td><td style="float:right">${checkRespone.discountAmount}</td></tr>
	<tr>
		<td >Total(-disc.)</td>  <td style="float:right">${checkRespone.amountAfterDiscount}</td>
	</tr>
</c:if>
<tr>
<td>
<c:forEach items="${checkRespone.taxDetails}" var="item">
 <c:if test="${item.value !=null}">
<tr><td>${item.key}</td><td style="float:right"><fmt:formatNumber type="number" pattern="###.##" value="${item.value}" /></td></tr>
</c:if></c:forEach> 
</td>
</tr>
<tr>
<c:if test = "${checkRespone.outCircleDeliveryCharges > 0}">
<tr><td >Delivery Charges</td> 
<td style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkRespone.outCircleDeliveryCharges}" />
</td></tr>
</c:if>
<tr><td >Grand Total</td> 
<td style="float:right">
<fmt:formatNumber type="number" pattern="###.##" value="${checkRespone.total}" />
</td></tr>
<tr>
	<td >Rounded-off Total</td> 
	<td id="roundOff" style="float:right">${checkRespone.roundedOffTotal}</td>
	</tr>
	</table>
<hr/>
<!-- <div align="center">TIN No.: 06551835911</div> -->
<div align="center">Invoice No.: ${checkRespone.invoiceId}</div>
<div align="center">Order No.: ${checkRespone.orderId}</div>
<br/>
<br/>
<div align="center">You're Awesome!</div>
<div align="center">-------</div>
<script type="text/javascript">


function zeroFill( number, width )
{
  width -= number.toString().length;
  if ( width > 0 )
  {
    return new Array( width + (/\./.test( number ) ? 2 : 1) ).join( '0' ) + number;
  }
  return number + ""; // always return a string
}

//$(document).ready(function () {
	
//	var now = new Date();
//	document.getElementById ('date-time').innerHTML = zeroFill(now.getDate(), 2) + '/' + zeroFill((now.getMonth()+1),2) + '/' + now.getFullYear() + ' ' + zeroFill(now.getHours(),2) + ':' + zeroFill(now.getMinutes(),2);
//});
</script> 
