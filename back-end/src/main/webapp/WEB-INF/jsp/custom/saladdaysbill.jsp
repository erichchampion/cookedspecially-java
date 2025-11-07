<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<script>
!function(f,b,e,v,n,t,s){if(f.fbq)return;n=f.fbq=function(){n.callMethod?
n.callMethod.apply(n,arguments):n.queue.push(arguments)};if(!f._fbq)f._fbq=n;
n.push=n;n.loaded=!0;n.version='2.0';n.queue=[];t=b.createElement(e);t.async=!0;
t.src=v;s=b.getElementsByTagName(e)[0];s.parentNode.insertBefore(t,s)}(window,
document,'script','//connect.facebook.net/en_US/fbevents.js');
fbq('init', '696084253783864');
fbq('track', "PageView");
fbq('track', 'Purchase', {value: '1.00', currency: 'USD'});
</script>
</head>
<body>
 <h1 align="center">SALAD DAYS</h1>
<h5 align="center">Amicus Natural Products Pvt. Ltd.</h5>
<div align="center"><a href="http://www.saladdays.co" align="center">www.saladdays.co</a></div>
<div align="center"><a href="http://www.facebook.com/Saladdays.co" align="center">www.facebook.com/Saladdays.co</a></div>
<div align="center">9643 800 901/02/03</div>
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
<th width="34%" align="left">Item</th>
<th width="33%" align="center" >Quantity</th>
<th width="33%"  align="right">Amount</th>
</tr>
<c:if test="${!empty itemsMap}">
<c:forEach items="${itemsMap}" var="item">
	<tr>
	<c:if test="${item.value.dishSize !='Default'}">
	<td width="34%" align="left">${item.value.name} <c:if test="${item.value.dishSize !=''}"> <b> ( ${item.value.dishSize!=''? item.value.dishSize :''} ) </b> </c:if>  </td>
	</c:if>
	<c:if test="${item.value.dishSize =='Default'}">
	<td width="34%" align="left">${item.value.name}</td>
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
	<td width="34%" align="left"><b>Add-On :</b> ${itemAdd.name}</td>
	</c:if>
	<c:if test="${itemAdd.dishSize!='Default'}">
	<td width="34%" align="left"><b>Add-On :</b> ${itemAdd.name}  ${itemAdd.dishSize !=''? itemAdd.dishSize:''}</td>
	</c:if>
		<td width="33%" align="center" >X ${itemAdd.quantity}</td>
		<td width="33%" align="right" ><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${itemAdd.price * itemAdd.quantity}" /></td>
	</tr>
	</c:if>
	</c:forEach>
	<c:if test="${!empty checkRespone.couponCal}"> 
		<c:forEach items="${checkRespone.couponCal}" var="coupon">
				<c:if test="${coupon.key == item.value.id}">
					<tr>
						<td width="33%">${coupon.value.couponName}</td>
						<td width="33%" align="center" ></td>
			            <td width="33%" align="right" ><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${- coupon.value.calcAmount}" /></td>
					</tr>
				</c:if>
		</c:forEach>
	</c:if>
</c:forEach>


<c:if test = "${checkRespone.outCircleDeliveryCharges > 0}">
	<tr><td width="33%">Delivery Charges ${checkRespone.additionalChargeName1}</td> 
	<td width="33%" align="center" >-</td>
	<td width="33%" align="right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.outCircleDeliveryCharges}" />
</td></tr>
</c:if>
<c:if test="${checkRespone.waiveOffCharges > 0}">
	<tr><td width="33%">Waived off Delivery Charges </td> 
	<td width="33%" align="center" >-</td>
	<td width="33%" align="right">
	-<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.waiveOffCharges}" />
	</td>
	</tr>
</c:if>
</c:if>

</table>
<hr/>
<table id="checkData" align="center" width="100%">
<tr>
	<td width="33%">Sub Total</td>
	<td width="33%" align="center"></td> 
	<td width="33%" align="right">
	<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amount}" /></td>
	</tr>

<c:if test="${!empty checkRespone.chargeDetails}">	
	<c:forEach items="${checkRespone.chargeDetails}" var="list">
    <tr>
	<td width="33%">${list.key}</td>
	<td width="33%" align="center"></td> 
	<td width="33%" align="right">
	+<fmt:formatNumber type="number" maxFractionDigits="2" minFractionDigits="2" value="${list.value}" /></td>
	</tr> 
	</c:forEach>	
</c:if>
<c:if test="${!empty checkRespone.chargeDetails }">
	<tr>
		<td width="33%">Total after discount</td>  
		<td width="33%" align="center"></td> 
	    <td width="33%" align="right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amountAfterCharge}" /></td>
	</tr>
</c:if>

<c:if test="${!empty checkRespone.discountDetails}"> 
<c:forEach items="${checkRespone.discountDetails}" var="list">
<tr>
	<td width="33%">${list.key}</td>
	<td width="33%" align="center"></td> 
	<td width="33%" align="right">
	-<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${list.value}" /></td>
	</tr> 
	</c:forEach>
</c:if>	
	
<c:if test="${!empty checkRespone.discountDetails }">
	<tr>
		<td  width="33%" >Total after discount</td> 
		<td width="33%" align="center"></td> 
		<td width="33%" align="right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.amountAfterDiscountCharges}" /></td>
	</tr>
</c:if>

	
<c:if test="${checkRespone.discountAmount > 0}">
	<tr>
		<td width="33%">Discount</td>
		<td width="33%" align="center"></td> 
		<td width="33%" align="right">${checkRespone.discountAmount}</td></tr>
	<tr>
		<td width="33%">Total(-disc.)</td>
		<td width="33%" align="center"></td> 
		<td width="33%" align="right">${checkRespone.amountAfterDiscount}</td>
	</tr>
</c:if>

<tr>
<td>
<c:forEach items="${checkRespone.taxDetails}" var="item">
 <c:if test="${item.value !=null}">
<tr>
<td width="33%" >${item.key}</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${item.value}" /></td></tr>
</c:if></c:forEach> 
</td>
</tr>
<tr>
<tr>
<td  width="33%" >Grand Total</td> 
<td width="33%" align="center"></td> 
<td width="33%" align="right">
<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.total}" />
</td>
</tr>
</table>
<table id="checkData" width="100%">
<c:if  test="${checkRespone.checkCreditBalance>0}">
<hr/>
<tr>
<td width="33%">Previous Credit Balance</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.checkCreditBalance}"/></td></tr>
<c:set var="ccAmount" value="${checkRespone.checkCreditBalance}"></c:set>
</c:if>
<c:if  test="${checkRespone.checkCreditBalance<0}">
<tr>
<td width="33%" >Credit amount</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.checkCreditBalance}"/></td>
</tr>
<c:set var="ccAmount" value="${checkRespone.checkCreditBalance}"></c:set>
</c:if>
<c:if  test="${checkRespone.paymentMode=='CUSTOMER CREDIT' && checkRespone.checkCreditBalance>0}">
<tr>
<td width="33%" >Current Order Total</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.total}"/></td><tr>
</c:if>

<c:if  test="${checkRespone.paymentMode!='CUSTOMER CREDIT' && checkRespone.checkCreditBalance>0}">
<tr><td width="33%" >Current Order Total</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right"><fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.total}"/></td><tr>
</c:if>

<c:if test="${checkRespone.paymentMode=='CUSTOMER CREDIT' && checkRespone.checkCreditBalance>0}">
<tr><td width="33%">Round Off</td> 
	<td width="33%" align="center"></td> 
	<td width="33%" align="right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${ccAmount + checkRespone.total}" />
	</td>
	</tr>
	<tr><td width="33%" >Payable Amount</td> 
	<td width="33%" align="center"></td> 
	<td width="33%" align="right">0.00</td>
	</tr>
</c:if>

<c:if test="${checkRespone.paymentMode!='CUSTOMER CREDIT'}">
<tr><td width="33%" >Payable Amount</td> 
	<td width="33%" align="center"></td> 
	<td  id="roundOff" width="33%" align="right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.roundedOffTotal + ccAmount}" />
	</td>
	</tr>
</c:if>

<c:if test="${checkRespone.paymentMode=='CUSTOMER CREDIT'}">
<tr><td width="33%">Round Off</td> 
	<td width="33%" align="center"></td> 
	<td id="roundOff"  width="33%" align="right">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${checkRespone.roundedOffTotal}" />
	</td>
	</tr>
<tr><td width="33%" >Payable Amount</td> 
	<td width="33%" align="center"></td> 
	<td id="roundOff"  width="33%" align="right">0.00</td>
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
<td  width="33%">Amount you saved</td>
<td width="33%" align="center"></td> 
<td width="33%" align="right">${checkRespone.amountSaved}</td>
</tr>
</c:if>	
</table>

<hr/>
<c:if test = "${restaurant.tinNo !=null && restaurant.tinNo!='' && !checkRespone.toShowOldVat_ServiceTax}">
<div align="center">GSTIN No.: ${restaurant.tinNo}</div>
</c:if>
<c:if test = "${checkRespone.toShowOldVat_ServiceTax}">
	<c:if test = "${restaurant.restaurantId==21}">
			<div align="center">TIN No.: 06551835911</div>
	</c:if>
	<c:if test = "${restaurant.restaurantId==33}">
			<div align="center">TIN No.: 07406994308</div>
	</c:if>
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
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 976190997;
var google_conversion_language = "en";
var google_conversion_format = "3";
var google_conversion_color = "ffffff";
var google_conversion_label = "OlUYCOOb82QQlfy90QM";
var google_remarketing_only = false;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/976190997/?label=OlUYCOOb82QQlfy90QM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>
</body>
</html>
