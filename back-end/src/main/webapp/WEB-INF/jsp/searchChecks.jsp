<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Search Checks</title>
	
	<style type="text/css">
		body {
			font-family: sans-serif;
		}
		.data, .data td {
			border-collapse: collapse;
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
	<script type="text/javascript" src="js/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="js/jquery.print.js"></script>
	
	<script type="text/javascript">
	function setCheckStatusCancelled(checkId) {
		if (confirm("Are you sure you want to cancel the check?")) {
			setCheckStatus(checkId, "Cancel");
		}
	}
	
	function setCheckStatus(checkId, status) {
	
	var statusApi = "order/setCheckStatus?checkId=" + checkId + "&status=" + status;

		$.ajax(statusApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + statusApi);
				console.log(JSON.stringify(data));
			}
			$("#cancelBtn"+checkId).hide();
			$("#cancelChecktd"+checkId).innerHTML = 'Cancelled';
		})
		.fail(function() { 
			console.log("ajax error: " + statusApi); 
		})
		.always(function() { 
			console.log("ajax complete: " + statusApi); 
		});
	}
	
	function printCheck(checkId) {
	var generateCheckApi = "order/generateCheckForPrint?templateName=saladdaysbill&checkId=" + checkId;

	$.ajax(generateCheckApi, {
		isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + generateCheckApi);
			console.log(JSON.stringify(data));
		}
		
	$("#printCheck" + checkId).append(data);
	//Popup($("#printCheck" + checkId).html());
	$("#printCheck" + checkId).print();
	//$("#printCheck" + checkId).hide();
	})
	.fail(function() { 
		console.log("ajax error: " + generateCheckApi); 
	})
	.always(function() { 
		console.log("ajax complete: " + generateCheckApi); 
	});
	
	}
	
	

    function Popup(data) 
    {
        var mywindow = window.open('', 'my div', 'height=400,width=600');
        mywindow.document.write('<html><head><title>my div</title>');
        /*optional stylesheet*/ //mywindow.document.write('<link rel="stylesheet" href="main.css" type="text/css" />');
        mywindow.document.write('</head><body >');
        mywindow.document.write(data);
        mywindow.document.write('</body></html>');

        mywindow.print();
        mywindow.close();

        return true;
    }
	</script>
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Query</h3>

<form id="checkQueryForm" action="order/searchChecks" method="post">
    <label> CheckId:</label>
    <input type="text" name="checkId" />
    <label>Or InvoiceId:</label>
    <input type="text" name="invoiceId" />
    <input type="submit" value="Search Checks" />
</form>
<hr/>
	
<h3>Checks</h3>
<c:if  test="${!empty checkList}">
<table class="data">
<tr>
	<th width="10%">ID</th>
	<th width="30%">Delivery Address</th>
	<th width="20%">Bill</th>
	<th width="20%">Delivery Time</th>
	<th width="10%">&nbsp;</th>
	<th width="10%">&nbsp;</th>
	<th width="10%">&nbsp;</th>
</tr>
<c:forEach items="${checkList}" var="check">
	<tr>
		<td width="10%">${check.id}</td>
		<td width="30%">${check.deliverAddress}</td>
		<td width="20%">${check.roundedOffTotal}</td>
		<td width="20%">${check.deliveryTime}</td>
		<td width="10%"><button type="button" onclick="window.location.href='order/editCheck/${check.id}';">edit</button></td>
		<td width="10%" id="cancelChecktd${check.id}">
		<c:choose>
			<c:when test="${check.status != 'Cancel'}">
				<button type="button" id="cancelBtn${check.id}" onclick="setCheckStatusCancelled(${check.id});">Cancel Check</button>
			</c:when>
			<c:otherwise>
			Cancelled
			</c:otherwise>
		</c:choose>
		</td>
		<td width="10%"><button type="button" onclick="printCheck(${check.id}); return(false);">Print Check</button></td>
		<div id="printCheck${check.id}" style="display:none;"></div>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
