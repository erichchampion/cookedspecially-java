<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>List Coupon</title>
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
	
	function filterCoupon(filter) {			
			
			var dropdownIndex = document.getElementById('item').selectedIndex;
            var x = document.getElementById('item')[dropdownIndex].value;
			window.location.href = 'coupon/listCoupon/' + x;
		
	}	
	
	
	</script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
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
		#user td{
		padding-bottom:10px;
		}
		h3{color:#FFBB43}
		.underline {
    text-decoration: underline;
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

<h3>Coupons
<select id="item" onchange="filterCoupon();" >
				<option value="Enabled|Disabled" ${filterValue == 'Enabled|Disabled'? 'selected="selected"' : ''}>All (Active)</option>
				<option value="Enabled" ${filterValue == 'Enabled'? 'selected="selected"' : ''}>Enabled</option>
				<option value="Disabled" ${filterValue == 'Disabled'? 'selected="selected"' : ''}>Disabled</option>
				<option value="NonActive" ${filterValue == 'NonActive'? 'selected="selected"' : ''}>Non Active</option>							
</select>

</h3>



<c:if  test="${!empty couponList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>Coupon Code</th>
	<th>&nbsp;</th>	
	<th>&nbsp;</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${couponList}" var="coupon">
<%-- <c:if test="${coupon.state != 'NonActive'}"> --%>
	<tr>
		<td class="underline" style="width:30%;"><a href="coupon/view/${coupon.coupanId}">${coupon.couponName}</a></td>
		<td style="width:30%;">${coupon.description}</td>
		<td style="width:10%;">${coupon.couponCode}</td>
		
	<td><button ${coupon.state == 'NonActive'? 'disabled="disabled"' : ''} type="button" onclick="window.location.href='coupon/disableEnable/${coupon.coupanId}';">
		<c:choose>
			<c:when test="${coupon.state == 'Disabled'}">
 				Enable 
			</c:when>
			<c:otherwise>
		 		Disable 
			</c:otherwise>
		</c:choose>
	</button></td>
		
		<td><button ${coupon.state == 'NonActive'? 'disabled="disabled"' : ''} type="button" onclick="window.location.href='coupon/edit/${coupon.coupanId}';">Edit</button></td>
		<td><button type="button" onclick="window.location.href='coupon/copy/${coupon.coupanId}';">Copy</button></td>
		<td><button ${coupon.state == 'NonActive'? 'disabled="disabled"' : ''} type="button" onclick="deleteCoupon(${coupon.coupanId});">Delete</button></td>
	</tr>
<%-- </c:if> --%>
</c:forEach>
</table>
</c:if>

</div>

</body>
<script>
	$(function() {
		$("#coupon").validationEngine();
	});

	
		
</script>

</html>
