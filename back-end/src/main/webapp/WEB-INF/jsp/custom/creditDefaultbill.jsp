<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<body>
<h1 align="center">Amicus Natural Products Pvt. Ltd</h1>
<h5 align="center"></h5>
<div align="center"><a href="" align="center"></a></div>
<div align="center"><a href="" align="center"></a></div>
<div align="center"></div>
<div align="center">${statementDate}<!-- <span id='date-time'></span>  --></div>

<div style="float:left;text-align:left">
</br>
<table align="left" width="100%">
<tr><td>Name: ${customerCreditBill.name}</td></tr>
<tr><td>Phone No.: ${customerCreditBill.mobileNo}</td></tr>
<tr><td>Email Id: ${customerCreditBill.email}</td></tr>
<tr><td>Billing Address: ${customerCreditBill.billingAddress}</td></tr>
 <!-- <tr><td>Payment Mode: <b>COD</b></td></tr>  -->
</table>
</div>
</br>
<div style="float:right;text-align:left">
<table align="right" width="100%">
<tr><td>Date: ${statementDate}</td></tr>
<tr><td>Total Amount: ${customerCreditBill.totalPurchases}</td></tr>
</table>

</div>
<table width="100%">
<tr><th>Previous Amount Due</th><th>Payment</th><th>Purchase</th><th>Total Amount Due</th></tr>
<tr><td id="roundOff" style="text-align:center;" width="25%">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.openingBanalce}" />
	</td>
	<td id="roundOff" style="text-align:center;"  width="25%">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.paymentReceived}" />
	</td>
	<td id="roundOff" style="text-align:center;"  width="25%">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.totalPurchases}" />
	</td>
	<td id="roundOff" style="text-align:center;"  width="25%">
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.outStandingBalance}" />
	</td>
</tr>
</table>
<table width="100%">
<tr>
	<td>Credit Account</td> 
	<td style="float:center;text-align:right">${customerCreditBill.creditName}</td>
	</tr>
	<c:if test="${customerCreditBill.creditType !='ONE_OFF'}">
	<%-- <tr>
	<td>Maximum Limit</td> 
	<td style="float:center;text-align:right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.maxLimit}" /></td>
	</tr>
	<tr>
	<td>Balance Limit</td> 
	<td style="float:center;text-align:right"><fmt:formatNumber type="number" 
            maxFractionDigits="2" minFractionDigits="2" value="${customerCreditBill.availableCredit}" /></td>
	</tr> --%>
	</c:if>
	
</table>
<hr/>
<table width="100%">
<tr>
	<th width="25%">Transaction Date</th> 
	<td width="50%" style="color:bold;float:center;text-align:center"><fmt:formatDate type="date" value="${customerCreditBill.fromDate}"/>  to  <fmt:formatDate type="date" value="${customerCreditBill.toDate}"/> </td>
	<td width="25%" style="float:center;text-align:right">     </td>
	</tr>
</table>
<hr/>
<table width="100%">
<tr><th>Date</th><th>InvoiceId</th><th>Description</th><th>Amount</th></tr>
<c:if test="${!empty customerCreditBill.transactions}">
<c:forEach items="${customerCreditBill.transactions}" var="transaction">
	<tr>
	<td width="25%" align="center">${transaction.transactionDate}</td>
	<td width="25%" align="center">${transaction.invoiceId}</td>
	<td width="25%" align="center" >${transaction.description}</td>
	<td width="25%" align="center" >
	<fmt:formatNumber type="number"  maxFractionDigits="2" minFractionDigits="2" value="${transaction.amount}" />
	</td>
	</tr>
	</c:forEach>
</c:if>
</table>
<hr/>
<%-- <table width="100%">
<tr>
	<td>Total Amount</td> 
	<td style="float:center;text-align:right">${customerCreditBill.totalPurchases}</td>
	</tr>
</table> --%>
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
