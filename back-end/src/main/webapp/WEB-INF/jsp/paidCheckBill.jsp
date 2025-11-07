<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"> <meta>
	<meta name="apple-mobile-web-app-capable" content="yes" > <meta>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black" > <meta>
	<link rel="apple-touch-icon" href="icons/57px_Bookmark-01.jpg"/>  
	<link rel="apple-touch-icon" sizes="72x72" href="icons/72px_Bookmark-01.jpg"/>  
	<link rel="apple-touch-icon" sizes="114x114" href="icons/114px_Bookmark-01.jpg"/>  
	<link rel="apple-touch-icon" sizes="144x144" href="icons/144px_Bookmark-01.jpg"/>  
	<link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400' rel='stylesheet' type='text/css' />
	<script type="text/javascript" src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="https://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
	<link rel="stylesheet" href="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" />
    <script type="text/javascript"  src="https://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
     <title>Salad Days</title>
    
 <style type="text/css">
    html { background-color: light-gray; }
	@media only screen and (min-width: 1024px){
		.header-content, #restaurants-content, #closed-content, #order-content, #check-content, #menu-content, .footer-content {
			max-width: 1024px !important;
			margin: 0 auto !important;
		}
	}
		#popupContact form, #popupNameAddress form, #popupEmail form{
		padding:10px 20px;
	}
	
	#check-content {
		font-family: courier;
		font-size: smaller;
	}
	.controlSeparator {
		cursor: pointer;
		font-weight: bold;
		float: right;
	}
	#email-check {
		background: url("shared/css/images/at.png") no-repeat scroll center 0 rgba(0, 0, 0, 0);
		height: auto;
		padding: 25px;
		text-align: center;
		z-index: 100;
	}
	.header-content .right-controlgroup .ui-controlgroup-controls a:last-child {
		padding-right:0;
	}
	body {
	  font-family: "Lucida Sans Unicode", "Lucida Grande", "sans-serif";
	  font-size: 14px;
	  line-height: 2em;
	  letter-spacing: 0px;
	  text-align: left;
	  font-weight: normal;
	  font-style: normal;
	  background-color: light-gray;
	}

	#email-check a {
		text-decoration: none;
	}

	h3.sectionTitle, .itemContent .title {
		font-size: 18px;
		font-weight: bold;
	}
	h3.sectionTitle {
		text-transform:uppercase;
	}
	.ui-footer-fixed {
		height: 46px;
		clear:both;
	}
	.fb-like, .g-plus-container, .pin-it, .tweet {
		float:left;
		padding-right:6px;
		display:none;
	}
	.g-plus-container, .pin-it, .tweet {
		padding-top:8px;
	}
	.poweredBy {
		vertical-align: bottom;
	}
	.restaurantName img, .poweredBy img {
		height:20px;
		width:20px;
		vertical-align: text-bottom;
	}
	.restaurantName 
	{
		padding: 1em 0 1em 0;
		line-height: 2em;
		float:left;
		font-weight:800;
		cursor:pointer;
	}
	/* JQM theme overrides */
	.ui-page .ui-content {
		padding-left:1em;
		padding-right:1em;
	}
	.ui-page .ui-collapsible-content {
		padding-left:0;
		padding-right:0;
	}
	.ui-header, .ui-footer, .ui-popup-container {
		background:white;
	}
	.ui-header, .ui-footer, .ui-header-fixed, .ui-footer-fixed {
		border: none;
		border-style: none;
		border-width: 0;
	}
	
	* {
		-webkit-column-rule-color: rgb(0, 0, 0);
		-webkit-text-emphasis-color: rgb(0, 0, 0);
		-webkit-text-fill-color: rgb(0, 0, 0);
		-webkit-text-stroke-color: rgb(0, 0, 0);
		border-bottom-color: rgb(0, 0, 0);
		border-left-color: rgb(0, 0, 0);
		border-right-color: rgb(0, 0, 0);
		border-top-color: rgb(0, 0, 0);
		color: rgb(0, 0, 0);
		outline-color: rgb(0, 0, 0);
		}
	.ui-page .ui-corner-all {
		-webkit-border-radius: 0em;
		border-radius: 0em;
		border: none
	}
	.ui-popup .ui-btn-icon-notext.ui-btn-corner-all, .ui-popup .ui-btn-icon-notext.ui-corner-all {
		-webkit-border-radius: 1em;
		border-radius: 1em
	}
	.ui-popup .ui-corner-all {
		-webkit-border-radius: .3125em;
		border-radius: .3125em
	}
	.restaurantName img, .poweredBy img {
		height:20px;
		width:20px;
		vertical-align: text-bottom;
	}
	
	.header-content .right-controlgroup .ui-controlgroup-controls a:last-child {
		padding-right:0;
	}
	
	.right-controlgroup {
		float:right;
	}
	
	#email-check {
		background: url("shared/css/images/at.png") no-repeat scroll center 0 rgba(0, 0, 0, 0);
		height: auto;
		padding: 25px;
		text-align: center;
		z-index: 100;
	}
	.ui-shadow {
		-webkit-box-shadow: 0 1px 0 rgba(0, 0, 0, .15);
		-moz-box-shadow: 0 1px 0 rgba(0, 0, 0, .15);
		box-shadow: 0 1px 0px rgba(0, 0, 0, .15);
	}
    
    /* #### Mobile Phones Portrait #### */
	@media screen and (max-device-width: 480px) and (orientation: portrait){
		.restaurantName, #delivery-btn {
			display:none;
		}
		#splash {
			background-image: url('images/SD_IphoneSplash-01.jpg');
		}
	}

	/* #### Mobile Phones Landscape #### */
	@media screen and (max-device-width: 640px) and (orientation: landscape) {
		#splash {
			background-image: url('images/SD_IphoneSplash-02.jpg');
		}
	}

	/* #### iPhone 4+ Portrait #### */
	@media screen and (max-device-width: 480px) and (-webkit-min-device-pixel-ratio: 2) and (orientation: portrait) {
	.restaurantName, #delivery-btn {
			display:none;
		}
		#splash {
			background-image: url('images/SD_IphoneSplash-01.jpg');
		}
	}

	/* #### iPhone 4+ Landscape #### */
	@media screen and (max-device-width: 480px) and (-webkit-min-device-pixel-ratio: 2) and (orientation: landscape) {
		#splash {
			background-image: url('images/SD_IphoneSplash-02.jpg');
		}
	}

	/* #### Tablets Portrait #### */
	@media screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: portrait) {
		#splash {
			background-image: url('images/SD_TabletSplash-01.jpg');
		}
	}

	/* #### Tablets Landscape #### */
	@media screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: landscape) {
		#splash {
			background-image: url('images/SD_TabletSplash-02.jpg');
		}
	}

	 /* #### Desktops ####  */
	@media screen and (min-width: 1024px){
		#splash {
			background-image: url('images/SD_TabletSplash-02.jpg');
		}
	}
	/* #### Tablets and Desktops #### */
	@media screen and (max-device-width: 768px) {
		.shortDescription { 
			display: block;
		}
		.longDescription { 
			display: none;
		}
	}
	/* #### Desktops #### */
	/* The large images don't display properly on a Galaxy Note with a resolution of 1280x800 */
	@media screen and (min-device-width: 1024px) {
		.shortDescription { 
			display: none;
		}
		.longDescription { 
			display: block;
		}
		li {
			margin-top: 16px;
			margin-bottom: 16px;
		}
		li img {
			height: 200px;
			min-height: 200px;
			width:200px;
			min-width: 200px;
		}
		.itemContent {
			padding-left: 120px;
			min-height: 200px;
		}
		.itemDetailContent {
			width: 200px;
		}
		.ui-listview img, .ui-li-static img {
			width: 200px;
			height: 200px;
		}
		
	}
	.print {display:none}
	@media print {  
		.section {
			float:left;
			display:inline;
			width:600px;
			margin-right:10px;
		}
		br {display:none}
		.shortDescription { 
			display: none;
		}
		.longDescription { 
			display: block;
		}
		li {
			margin-top: 16px;
			margin-bottom: 16px;
		}
		li img {
			height: 200px;
			min-height: 200px;
			width:200px;
			min-width: 200px;
		}
		.itemContent {
			padding-left: 120px;
			min-height: 200px;
		}
		.itemDetailContent {
			width: 200px;
		}
		.ui-listview img, .ui-li-static img {
			width: 200px;
			height: 200px;
		}
		#order-button {display:none}
		.print {display:inline} 
		.ui-listview, .ui-li-static {-webkit-region-break-inside: avoid;page-break-inside: avoid;page-break-after: always;}
		h3.sectionTitle {page-break-before: always;}
		.addItemToOrder {display:none}
		.ui-footer {display:none}
	}  
	[contenteditable="plaintext-only"] { outline: 1px dashed #CCC; }
	[contenteditable="plaintext-only"]:hover { outline: 1px dashed #0090D2; }
    
</style>
<script type="text/javascript">
var  restaurantId =21;
$(function() {
        $( "#popupOrderConfirmation" ).popup( "open" );
		});
	

 </script> 
</head>
<body>
<div data-role="page" data-theme="d" id="check">
<div data-role="header" data-id="globalHeader" data-theme="d" data-position="fixed">
			<div class="header-content">
			<div class="restaurantName"><span class="restaurantLink"><a  href="http://www.saladdays.co"><img alt="Powered by CookedSpecially" src="../images/SD_Graphic.png"/></a></span> 
			<span class="poweredBy"><a target="cs" href="https://www.cookedspecially.com"><img alt="Powered by CookedSpecially" src="../images/cs-button.png"/></a></span></div>
			<div class="right-controlgroup" id="options" data-role="controlgroup" data-type="horizontal" data-mini="true">
			<a id="done-button" href="http://www.saladdays.co/our-menu.html" data-role="button" data-inline="true" data-icon="home">Done</a> 
			<!-- <a id="emailcheck-button" href="#popupEmail" data-rel="popup" data-role="button" data-inline="true" data-icon="check" data-position-to="window">Email Check</a>  -->
			</div>
			</div>
</div>

<div data-role="popup" id="popupOrderConfirmation" data-theme="d" class="ui-corner-all" data-dismissible="false" data-history="false">
			<a href="#" onclick="$('#popupOrderConfirmation').popup('close');" data-role="button" data-theme="d" data-icon="delete" data-iconpos="notext" class="ui-btn-left">Close</a>
			<div id="orderConfirmation-content" data-role="content">
				<p>Your check has been sent.</p>
				<p>Treat Yourself!</p>
			</div>
		</div>
<div  data-theme="d"  id="check-content" data-role="content"> 
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
<th width="34%">Item</th>
<th width="33%">Quantity</th>
<th width="33%"  align="right">Amount</th>
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
		<td width="33%" align="right" >${item.value.price}</td>
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
		<td width="33%" align="right" >${itemAdd.price * itemAdd.quantity}</td>
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
<tr><td >Delivery Charges ${checkRespone.additionalChargeName1}</td> 
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
<tr>
	<td >Total Amount</td> 
	<td id="roundOff" style="float:right;text-align:right">${checkRespone.roundedOffTotal}</td>
	</tr>
<c:if test = "${checkRespone.amountSaved > 0}">
<tr>
<td>Amount you saved</td>
<td style="float:right;text-align:right">${checkRespone.amountSaved}</td>
</tr>
</c:if>	
	
	</table>
<hr/>
<div align="center">TIN No.: ${restaurant.tinNo}</div>
<c:if test = "${restaurant.serviceTaxNo !=null && checkRespone.additionalChargeName1!=null}">
<div align="center">Service Tax No.: ${restaurant.serviceTaxNo}</div>
</c:if>
<div align="center">Invoice No.: ${checkRespone.invoiceId}</div>
<div align="center">Order No.: ${checkRespone.orderId}</div>
<br/>
<br/>
<div align="center">You're Awesome!</div>
<div align="center">-------</div>
</div>
</div>
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
</body>
</html>