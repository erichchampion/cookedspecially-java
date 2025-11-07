<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>View Coupon</title>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css" />	
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript" src="js/nicEdit.js"></script> 
	<script type="text/javascript" src="js/ui/jquery-ui.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<script type="text/javascript">
	function deleteCoupon(couponId) {
		if (confirm('Do you really want to delete this coupon')) {
			window.location.href = 'coupon/delete/' + couponId;
		} 
	}
	</script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
	<style type="text/css">
		body {
			font-family: sans-serif;
		}
		.data td{
		border:none;
		margin-left: 50	px;		
		color:#399ace;	
		
		}	
		.data .value{
		border:none;
		margin-left: 50	px;	
		padding-left: 8px;
		color:#ffffff;
		
		}
		
		.checkData, .checkData td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.checkData th {
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
		
		h3{color:#FFBB43}
		
		h2 {
    display: block;
    font-size: 1.2em;
    margin-top: 0.83em;
    margin-bottom: 0.83em;
    margin-left: 0;
    margin-right: 0;
    font-weight: bold;
}		

nav {
    float: left;
    max-width: 160px;
    margin: 0;
    padding: 1em;
}

nav ul {
    list-style-type: none;
    padding: 0;
}
   
nav ul a {
    text-decoration: none;
}

#mainDiv {
    margin-left: 170px;
    border-left: 1px solid gray;
    padding: 1em;
    overflow: hidden;
}
	</style>
	
	<script type="text/javascript">

	bkLib.onDomLoaded(function() { 
		//nicEditors.allTextAreas()
		var nicEditorInstance = new nicEditor({fullPanel : false, iconsPath: 'images/nicEditorIcons.gif', buttonList : ['bold','italic','underline','left','center','right', 'justify', 'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat', 'indent', 'outdent', 'hr', 'forecolor', 'bgcolor', 'fontSize', 'fontFamily', 'fontFormat']});
		nicEditorInstance.panelInstance('closedText');
		//nicEditorInstance.panelInstance('description');
	}); 
	



	</script>
</head>
<body>
<a href="coupon/listCoupon" style="float: right;">Return to Coupons</a>

<nav id="myNav">
  <ul>
    <li><a href="coupon/createCoupon">Create Coupon</a></li>
    <li><a href="coupon/listCoupon">List Coupons</a></li>    
  </ul>
</nav>


<div id="mainDiv">

<div style="color:red;">${errorMsg}</div>



<form:form commandName="coupon">
<form:hidden path="restaurantID" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
<form:hidden path="coupanId"/>
<form:hidden path="couponRuleID"/>
<form:hidden path="flatRules.coupanRuleId"/>
	
<table class="data">
	<tr>
		<td colspan="2">
			<button type="button" onclick="window.location.href='coupon/copy/${coupon.coupanId}';" style="background-color:#8DC360">Copy</button>
			<button type="button" onclick="window.location.href='coupon/edit/${coupon.coupanId}';" style="background-color:#8DC360" ${(coupon.state == "NonActive") ? 'disabled' : ''}>Edit</button>
			<button  type="button" onclick="window.location.href='coupon/disableEnable/${coupon.coupanId}';" style="background-color:#8DC360" ${(coupon.state == "NonActive") ? 'disabled' : ''}>
				<c:choose>
					<c:when test="${coupon.state == 'Disabled'}">
		 				Enable 
					</c:when>
					<c:otherwise>
				 		Disable 
					</c:otherwise>
				</c:choose>
			</button>
			<button  type="button" onclick="deleteCoupon(${coupon.coupanId});" style="background-color:#8DC360" ${(coupon.state == "NonActive") ? 'disabled' : ''}>Delete</button>
			
			
		</td>
	</tr>
</table>
<hr/>
	
	<h3>Coupon Details</h3>
	<table class="data">	
	    <tr>     
		<td><label>Name:</label></td>
		<td class="value"><label>${coupon.couponName}</label></td>
		
	</tr>
	<tr>     
		<td><label>Description:</label></td>
		<td class="value"><label>${coupon.description}</label></td>		
	</tr>
	<tr>
	
	     <td><label>Code:</label></td>
		<td class="value"><label>${coupon.couponCode}</label></td>		
		
	</tr>
	<tr>
	
	     <td><label>State:</label></td>
		<td class="value"><label>${coupon.state}</label></td>		
		
	</tr>
	</table>
	<hr/>  		
	<h3>Validity</h3>
	<table class="data">		
		<tr>
			<td>Is Duration Applicable:</td>			
			<td class="value">
					<label>
						<c:choose>
							<c:when test="${coupon.flatRules.isDurationRequired == 'true'}">
				 				Yes
							</c:when>
							<c:otherwise>
						 		No 
							</c:otherwise>
						</c:choose>
					</label>
				</td>
		</tr>			 
		<tr>   	 
			<td><label >Start Date:</label></td>
			<td class="value"><label>${coupon.flatRules.startDate }</label></td>
		</tr>
		<tr>		
			<td><label>End Date:</label>
			<td class="value"><label>${coupon.flatRules.endDate }</label></td>		
		</tr>
    </table>
    
    <hr/>  		
	<h3>Condition</h3>
    <table class="data">	
	<tr>
		<td>Order Source:</td>
		<td class="value"><label>${coupon.flatRules.orderSource }</label></td>		
	</tr>
	<tr>
		<td>Payment Type:</td>
		<td class="value"><label>${coupon.flatRules.paymentMode }</label></td>
		
	</tr>
	 <tr>     
		<td><label >Minimum Order:</label></td>
		<td class="value"><label>${coupon.flatRules.minOrderPayment }</label></td>		
	</tr> 
	
	<tr>
			<td><form:label path="flatRules.deliveryAreas">Delivery Areas</form:label></td>
				<td>
					<form:select disabled='true' path="flatRules.deliveryAreas" multiple="true">
						


							<c:forEach items="${deliveryArealist}" var="rol">
								<c:forEach items="${selDeliveryArealist}" var="currentRole">
				                   <c:if test="${currentRole.id == rol.id}">
				                       <c:set var="selected" value="true"/>
				                   </c:if>
				               </c:forEach>
				               
				               <option label="${rol.name}" value="${rol.id}"
					             <c:if test="${selected}">selected="selected"</c:if>
					             >${rol.name}
					              
					           </option>
					           <c:remove var="selected"/>          
								
							</c:forEach>


						</form:select>
				</td>
	</tr>
	</table>
    
    <hr/>  		
	<h3>Discount</h3>
	<table class="data">		
	<tr>     
		<td><label >Discounted Value:</label></td>
		<td class="value"><label>${coupon.flatRules.discountValue }</label></td>
		<td class="value">
			<label>
				<c:choose>
					<c:when test="${coupon.flatRules.isAbsoluteDiscount == 'true'}">
		 				(Absolute) 
					</c:when>
					<c:otherwise>
				 		(Percentage) 
					</c:otherwise>
				</c:choose>
			</label>
		</td>			
	</tr>
	</table>
    
    <hr/>  		
	<h3>Limit</h3>
	<table class="data">	
	<tr>
				<td>Usage :</td>
				<td class="value">
					<label>
						<c:choose>
							<c:when test="${coupon.flatRules.isUsedOncePerCustomer == 'true'}">
				 				Once Per Customer 
							</c:when>
							<c:otherwise>
						 		Multiple times per customer 
							</c:otherwise>
						</c:choose>
					</label>
				</td>
	</tr>
	
	
	<tr>
		<td>Maximum Redemption Limit:</td>
		<td class="value">
			<label>
				<c:choose>
					<c:when test="${coupon.flatRules.isMaxCountNoLimit == 'true'}">
		 				No Limit 
					</c:when>
					<c:otherwise>
				 		${coupon.flatRules.maxCount } 
					</c:otherwise>
				</c:choose>
			</label>
		</td>		
	</tr>	
	
	
	</table>
		
	<hr/>	

<h3>Associated Checks (Count ${coupon.check_used.size()})</h3>
<c:if test="${!empty coupon.check_used}">
	<table class="checkData">
		<tr>
			<th>Check ID</th>
			<th>CustomerID</th>	
		</tr>
		<c:forEach items="${coupon.check_used}" var="check">
		
			<tr>		
				<td style="width:30%;">${check.checkId}</td>
				<td style="width:10%;">${check.customerId}</td>		
			</tr>
		
		</c:forEach>
	</table>
</c:if>

</form:form>
<hr/>


</div>

</body>
<script>
	 $(function() {				
		
		$("#coupon").validationEngine();
	}); 
 
		
</script>

</html>
