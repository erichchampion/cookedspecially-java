<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Reporting Manager</title>
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
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript" src="js/jquery.simple-dtpicker.js"></script>
	<link rel="stylesheet" type="text/css" href="css/jquery.simple-dtpicker.css" />
	<c:set var="sessionUserId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<script type="text/javascript">

		
		function generateReportUrlsWithDateRange(restaurantId) {
			var startDate = new Date($('input[name=startDate]').val());
			var endDate = new Date($('input[name=endDate]').val());
			var oneDay = 1000*60*60*24;
			var difference_in_day = Math.round(Math.abs((endDate.getTime() - startDate.getTime())/(oneDay)));
			if(difference_in_day > 31 ){
                alert("date range must not be greater than 31 days!");
				}
			else{
				var startDate = startDate.toISOString().slice(0,10);
				var endDate = endDate.toISOString().slice(0,10);
			
			$("#dailySalesSummaryNewReport").attr("href", "reports/dailySalesSummaryNew.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#dailySalesSummaryReport").attr("href", "reports/dailySalesSummary.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#dailyInvoiceReport").attr("href", "reports/dailyInvoice.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#customersReport").attr("href", "reports/customers.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#topDishesReport").attr("href", "reports/topDishes.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#detailedInvoiceReport").attr("href", "reports/detailedInvoice.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#salesSummaryReport").attr("href","reports/salesSummary.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			$("#salesRegisterReport").attr("href", "reports/salesRegisterReport.xls?restaurantId=" + restaurantId +"&startDate=" + startDate + "&endDate=" + endDate);
			
			alert("Generated URLs");
			}
			
		}
	</script>
</head>
<body>
<a href="manageRestaurant.jsp"  style="float: right;">Return to Home</a>
<form id="deliveryAddressForm" action="#" method="post">	
     <input type="text" name="startDate" class="date-picker" />
   <!-- <input type="month" id="seletedMonth"> -->
    <input type="text" name="endDate" class="date-picker" />
    <input type="button" id="generateUrls" value="Generate Report Urls" onclick="generateReportUrlsWithDateRange('<%=request.getSession().getAttribute("restaurantId")%>')" />
</form>
<div id="reportUrlContainer">
<a id="dailySalesSummaryNewReport" href="reports/dailySalesSummaryNew.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>"> Daily Sales Summary  New</a>  | <a id="dailySalesSummaryReport" href="reports/dailySalesSummary.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Daily Sales Summary</a> | <a id="dailyInvoiceReport" href="reports/dailyInvoice.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Daily Invoice</a> | <a id="customersReport" href="reports/customers.xls?restaurantId=${sessionUserId}">Customers</a>| <a id="topDishesReport" href="reports/topDishes.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Top Dishes</a>| <a id="salesRegisterReport" href="reports/salesRegisterReport.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Transaction Report</a>  | <a id="detailedInvoiceReport" href="reports/detailedInvoice.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Detailed Invoice</a> | <a id="salesSummaryReport" href="reports/salesSummary.xls?restaurantId=<%=request.getSession().getAttribute("restaurantId")%>">Sales Summary Report</a>
</div>
<script>
function current() {
    var tDay = new Date();
	var tMonth = tDay.getMonth()+1;
	var m = tMonth > 9 ? "" + tMonth: "0" + tMonth;
	var currentDate=""+tDay.getFullYear()+"-"+m
	document.getElementById("seletedMonth").setAttribute("value", currentDate);
}
</script>
</body>
<script>
$(function() {
    $('.date-picker').appendDtpicker({"dateOnly": true});
});
</script>

</html>
