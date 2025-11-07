<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<body>
 <h1 align="center">${restaurant.restaurantName}</h1>
<h5 align="center"></h5>
<div align="center"><a href="" align="center"></a></div>
<div align="center"><a href="" align="center"></a></div>
<div align="center"></div>
<div align="center">${restaurant.address1}<!-- <span id='date-time'></span>  --></div>
<div align="center">${checkDate}<!-- <span id='date-time'></span>  --></div>

<table align="center" width="100%">
<c:if  test="${!empty customer}">
<tr><td>Name: ${checkRespone.name}</td></tr>
<tr>
<c:if test="${!empty checkRespone.deliverAddress}">
	<td>Address: ${checkRespone.deliverAddress}
	</td>
	</c:if>

</tr>
	<c:choose>
		<c:when test="${!empty checkRespone.deliveryArea}">
		<tr>
			<td>Delivery Area: ${checkRespone.deliveryArea}</td>
		</tr><br/>
		</c:when>
	</c:choose>
	<c:choose>
	<c:when test="${!empty checkRespone.deliveryInst}">
		<tr>
			<td>Delivery Instruction: ${checkRespone.deliveryInst}</td>
		</tr><br/>
		</c:when>
	</c:choose>
<tr><td>Email Id: ${customer.email}</td></tr>
<tr><td>Phone No.: ${checkRespone.phone}</td></tr>
<c:choose>
		<c:when test="${checkRespone.checkType=='TakeAway'}">
		
		<c:if test="${!empty checkRespone.deliveryDateTime}">
		<tr>
			<td>TakeAway Time : ${checkRespone.deliveryDateTime}</td>
		</tr><br/>
		</c:if>
		</c:when>
		<c:otherwise>
			<tr>
			<td>Delivery Time : ${checkRespone.deliveryDateTime}</td>
		</tr><br/>
		</c:otherwise>
</c:choose>
 <tr><td>Order Source: <b> ${checkRespone.orderSource}</b></td></tr>
 <tr><td>Payment Mode: <b> ${checkRespone.paymentMode}</b></td></tr> 
</c:if>
<c:if test="${!empty tableId }">
<tr><td>Table ID: ${tableId}</td></tr> 
</c:if>
</table>
<table align="center" width="100%">
<tr>
<th width="34%"><p style="float:left">Item</p></th>
<th width="33%"><p style="text-align:center">Quantity</p></th>
<th width="33%"><p style="float:right">Amount</p></th>
</tr>
<c:if test="${!empty itemsMap}">
<c:forEach items="${itemsMap}" var="item">
	<tr>
	<c:if test="${item.value.dishSize !='Default'}">
	<td width="34%">${item.value.name}  ${item.value.dishSize!=''? item.value.dishSize :''}  </td>
	</c:if>
	<c:if test="${item.value.dishSize =='Default'}">
	<td width="34%">${item.value.name}</td>
	</c:if>	
		<td width="33%" align="center" >X ${item.value.quantity}</td>
		<td width="33%" align="right" >
		<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${item.value.price}" />
		</td>
	</tr>
	 <c:forEach items="${item.value.addOns}" var="itemAdd"> 
	<c:if test="${item.value.addOns !=null && itemAdd.dishId == item.value.id}" >
	<tr>
	<c:if test="${itemAdd.dishSize =='Default'}">
	<td width="34%"><b>Add-On :</b> ${itemAdd.name}</td>
	</c:if>
	<c:if test="${itemAdd.dishSize!='Default'}">
	<td width="34%"><b>Add-On :</b> ${itemAdd.name}  ${itemAdd.dishSize !=''? itemAdd.dishSize:''}</td>
	</c:if>
		<td width="33%" align="center" >X ${itemAdd.quantity}</td>
		<td width="33%" align="right" >
		<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${itemAdd.price * itemAdd.quantity}" />
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
	<td style="float:right;text-align:right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amount}" /></td>
	</tr>

<c:if test="${!empty checkRespone.chargeDetails}">	
	<c:forEach items="${checkRespone.chargeDetails}" var="list">
    <tr>
	<td style="float:right">${list.key}</td>
	<td style="float:right">+<fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${list.value}" /></td>
	</tr> 
	</c:forEach>	
</c:if>
<c:if test="${!empty checkRespone.chargeDetails }">
	<tr>
		<td >Total after discount</td>  <td style="float:right;text-align:right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amountAfterCharge}" /></td>
	</tr>
</c:if>

<c:if test="${!empty checkRespone.discountDetails}"> 
<c:forEach items="${checkRespone.discountDetails}" var="list">
<tr>
	<td style="float:right;">${list.key}</td>
	<td style="float:right;text-align:right">-<fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${list.value}" /></td>
	</tr> 
	</c:forEach>
</c:if>	
	
<c:if test="${!empty checkRespone.discountDetails }">
	<tr>
		<td >Total after discount</td>  <td style="float:right;text-align:right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amountAfterDiscountCharges}" /></td>
	</tr>
</c:if>

	
<c:if test="${checkRespone.discountAmount > 0}">
	<tr>
		<td>Discount</td><td style="float:right;text-align:right">${checkRespone.discountAmount}</td></tr>
	<tr>
		<td >Total(-disc.)</td>  <td style="float:right;text-align:right">${checkRespone.amountAfterDiscount}</td>
	</tr>
</c:if>

<tr>
<td>
<c:forEach items="${checkRespone.taxDetails}" var="item">
 <c:if test="${item.value !=null}">
<tr><td>${item.key}</td><td style="float:right;text-align:right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${item.value}" /></td></tr>
</c:if></c:forEach> 
</td>
</tr>

<c:if test = "${checkRespone.outCircleDeliveryCharges > 0}">
<tr><td >Delivery Charges</td> 
<td style="float:right;text-align:right">
<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.outCircleDeliveryCharges}" />
</td></tr>
</c:if>

<c:if test="${checkRespone.waiveOffCharges > 0}">
	<tr>
		<td>Waived off Delivery Charges</td><td style="float:right;text-align:right">-<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.waiveOffCharges}" /></td></tr>
</c:if>

<tr>
<tr><td >Grand Total</td> 
<td style="float:right;text-align:right">
<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.total}" />
</td></tr>

<c:if  test="${checkRespone.checkCreditBalance>0}">
<tr><td >Your total pending credit amount</td><td style="float:right;text-align:right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.checkCreditBalance}"/></td></tr>
<c:set var="ccAmount" value="${checkRespone.checkCreditBalance}"></c:set>
</c:if>
<c:if  test="${checkRespone.checkCreditBalance<0}">
<tr><td >Credit amount</td><td style="float:right;text-align:right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.checkCreditBalance}"/></td><tr>
<c:set var="ccAmount" value="${checkRespone.checkCreditBalance}"></c:set>
</c:if>
<c:if test="${checkRespone.paymentMode=='CUSTOMER CREDIT'}">
<tr><td>Payable</td> 
	<td id="roundOff" style="float:right;text-align:right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${ccAmount}" />
	</td>
	</tr>
</c:if>
<c:if test="${checkRespone.paymentMode!='CUSTOMER CREDIT'}">
<tr><td>Payable</td> 
	<td id="roundOff" style="float:right;text-align:right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.roundedOffTotal + ccAmount}" />
	</td>
	</tr>
</c:if>
<%-- <c:if test="${customer.credit.creditBalance<0}">
<tr><td>Remaining Credit balance</td> 
	<td id="roundOff" style="float:right;text-align:right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${customer.credit.creditBalance}" />
	</td>
	</tr>
</c:if> --%>
<c:if test = "${checkRespone.amountSaved > 0}">
<tr>
<td>Amount you saved</td>
<td style="float:right;text-align:right">${checkRespone.amountSaved}</td>
</tr>
</c:if>	
	
	</table>
<hr/>
<c:if test = "${restaurant.tinNo !=null && restaurant.tinNo!=''}">
<div align="center">TIN No.: ${restaurant.tinNo}</div>
</c:if>
<c:if test = "${restaurant.serviceTaxNo !=null && checkRespone.additionalChargeName1!=null}">
<div align="center">Service Tax No.: ${restaurant.serviceTaxNo}</div>
</c:if>
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
<!-- Google Code for Website Conversion Conversion Page -->
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/976190997/?label=OlUYCOOb82QQlfy90QM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>
</body>
</html>
