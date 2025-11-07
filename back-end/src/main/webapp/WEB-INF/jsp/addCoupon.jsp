<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Add Coupon</title>
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
	function enable_text(ckbox)
	{
		if(ckbox.checked)
			{
				document.getElementById('maxCountInput').disabled=ckbox.checked;
				document.getElementById('maxCountInput').style.backgroundColor="grey";
			}
		else
			
			{
			document.getElementById('maxCountInput').disabled=ckbox.checked;
			document.getElementById('maxCountInput').style.backgroundColor="white";
			}
	}
	function enable_duration(ckbox)
	{
		var startDate = document.getElementById('startDate');
		var endDate = document.getElementById('endDate');
		startDate.disabled = !ckbox.checked;
		endDate.disabled = !ckbox.checked;
		
		if(!ckbox.checked)
			{	
				startDate.style.backgroundColor="grey";
				startDate.removeAttribute('required');
				
				endDate.style.backgroundColor="grey";
				endDate.removeAttribute('required');
			}
		else
			{
				startDate.style.backgroundColor="white";
				startDate.setAttribute('required','required');
				
				endDate.style.backgroundColor="white";
				endDate.setAttribute('required','required');
				
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
		}	
		
		.leftPad{
		padding-left: 8px;
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
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>

<nav id="myNav">
  <ul>
    <li><a href="coupon/createCoupon">Create Coupon</a></li>
    <li><a href="coupon/listCoupon">List Coupons</a></li>    
  </ul>
</nav>


<div id="mainDiv">

<div style="color:red;">${errorMsg}</div>
<h3>Add Coupon</h3>

<form:form name="f1" method="post" action="coupon/addCoupon" commandName="coupon">
<form:hidden path="restaurantID" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
<form:hidden path="coupanId"/>
<form:hidden path="couponRuleID"/>
<form:hidden path="flatRules.coupanRuleId"/>
	
	<hr/>
	
	
	<table class="data">	
	    <tr>     
		<td><form:label path="couponName">Name*</form:label></td>
		<td class="leftPad"><form:input path="couponName" required="true" maxlength="50"/></td>
	</tr>
	<tr>     
		<td><form:label path="description">Description</form:label></td>
		<td class="leftPad"><form:input path="description" maxlength="150"/></td>
	</tr>
	<tr>     
		<td><form:label path="couponCode">Coupon Code*</form:label></td>
		<td class="leftPad"><form:input style="text-transform: uppercase" path="couponCode" required="true" maxlength="50"/></td>
		<td class="leftPad"><form:errors path="couponCode"  style="color: red; padding-left: 5px;"/></td>
	</tr>
	</table>		
	
	<hr/>
	<h3>Validity</h3>	    
    
	
	<table class="data">
	
	
	<tr>
	<td>
	<form:checkbox id="chkBox_duration" path="flatRules.isDurationRequired" value="false" onclick="enable_duration(this)"/>Duration (optional)
	</td>
	</tr> 
	<tr>   
	 
		<td><label >Start Date*</label></td>
		<td><form:input  id="startDate"  path="flatRules.startDate" style="width:173px" value="" readonly="readonly" /></td>
		
		
		<td class="leftPad"><label>End Date*</label>
		<form:input  id="endDate" path="flatRules.endDate" style="width:173px" value="" readonly="readonly" /></td>
	</tr>

	</table>		
	
	<hr/>
	<h3>Condition</h3>	    
    
	
	<table class="data">
	
	<tr>
		<td>Order Source</td>
		<td class="leftPad">
			<form:select path="flatRules.orderSource" items="${orderSourceTypes}" >
			
			
		</form:select></td>
	</tr>
	<tr>
		<td>Payment Type</td>
		<td class="leftPad">
			<form:select path="flatRules.paymentMode" items="${paymentModeTypes}" >		
		</form:select></td>
		
	</tr>
	 <tr>     
		<td><label >Minimum Order</label></td>
		<td class="leftPad"> <form:input path="flatRules.minOrderPayment" required="false" maxlength="150"/></td>
	</tr> 
	
	<tr>
			<td><form:label path="flatRules.deliveryAreas">Delivery Areas</form:label></td>
				<td class="leftPad">
					<form:select path="flatRules.deliveryAreas" multiple="true">
						


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
		<td><label >Discounted Value</label></td>
		<td>		
			<form:select path="flatRules.isAbsoluteDiscount" >
				<%-- <form:option value="true">Absolute</form:option> --%>
				<form:option value="false">Percentage</form:option>			
			</form:select>
		
		<form:input path="flatRules.discountValue" required="false" maxlength="150"/></td>
	</tr>
	
	</table>		
	
	<hr/>
	<h3>Limit</h3>	    
    
	
	<table class="data">	
	<tr>
				<td>Usage :</td>
				<td class="leftPad"><form:radiobutton path="flatRules.isUsedOncePerCustomer" value="true" />Once per customer
				<form:radiobutton path="flatRules.isUsedOncePerCustomer" value="false" />Multiple times per customer</td>
				<td><form:errors path="flatRules.isUsedOncePerCustomer" cssClass="error" /></td>
	</tr>
	
	
	<tr>
		<td>Max Count :</td>
		 <td class="leftPad">	
			<form:input id="maxCountInput" name="maxCountInput" path="flatRules.maxCount"/>
			<form:checkbox id="chkBox_maxCount" path="flatRules.isMaxCountNoLimit" value="true" onclick="enable_text(this)"/>No Limit
		</td>
	</tr>	
	
	
	</table>
		
	<hr/>	
	<table class="data">
	<tr>
		<td colspan="2">
			<button name="saveSubmitbtn" type="submit" value="Save Coupon" style="background-color:#8DC360"
			${(editMode == 1) ? 'hidden' : ''}>Save Coupon</button>
			<button name="saveSubmitbtn" type="submit" value="Edit Coupon" style="background-color:#8DC360"
			${(editMode == 1) ? '' : 'hidden'}>Save Coupon</button>
			
			<button type="button" style="background-color:#FEE580" onclick="document.location.href='#manageRestaurant.jsp'">Cancel</button>
		</td>
	</tr>
</table><label id="errorMsg" style="display:none"></label>	
<c:set var="error.couponName.exists"><form:errors path="couponName"/></c:set>
</form:form>
<hr/>


</div>

</body>
<script>
	 $(function() {
		var v = document.getElementById('chkBox_maxCount');
		enable_text(v);	
		
		var dur = document.getElementById('chkBox_duration');
		enable_duration(dur);	
		
		
		
		
		$("#coupon").validationEngine();
	}); 
  $(function(){
		$("#endDate").datepicker({
		    dateFormat: 'yy-mm-dd',
		    maxDate:"100Y",
			minDate:"0Y",
			changeYear:true,
			changeMonth:true
		})
	})
	$(function(){
		$("#startDate").datepicker({
		    dateFormat: 'yy-mm-dd',
		    maxDate:"100Y",
			minDate:"0Y",
			changeYear:true,
			changeMonth:true
		})
	})
		
</script>

</html>
