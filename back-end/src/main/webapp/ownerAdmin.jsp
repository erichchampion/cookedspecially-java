<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.regex.*"%>
<html>
<head>
<meta name="google-site-verification" content="lVKJm-21o0luyMpMRbZKEWK-BhhBZiea6UYuWI5ZxfQ" />
<meta charset="utf-8" />
<title>CookedSpecially.com</title>

  <!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
  
  <link rel="stylesheet" href="css/style.css" />
  
  <!-- scripts at bottom of page -->
</head>
<body class="homepage">
<c:set var="sessionUserId" value='<%=request.getSession().getAttribute("userId")%>'/>
<c:set var="sessionRestaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
<c:set var="role" value='<%=request.getSession().getAttribute("role")%>'/>
<!--c:set var="isAdmin" value="${role == 'admin'}"/-->
<c:set var="isAdmin" value="<%=true%>"/>
<style>
#form { display:none }
#container { display:block }
</style>
<c:if test="${isAdmin}">
<style>
#form { display:block }
#container { display:none }
</style>
</c:if>
<div id="form">
	<form onsubmit="return false;">
		<input type="text" maxlength="50" name="theText" id="theText" value="" />
		<button id="continue" type="button" onclick="validate();">Continue</button>
	</form>
</div>
<div id="container">
<div class="item">
<c:if test='${!empty sessionUserId}'>
Logged in as <%=request.getSession().getAttribute("username")%>(<%=request.getSession().getAttribute("role")%>) | <a href="user/logout">Logout</a> <!-- <a href="user/edit">Edit</a> | <a href="user/logout">Logout</a> -->
</c:if>
<c:if test='${empty sessionUserId}'>
<a href="user/login">Login</a>
</c:if><br>
<c:if test='${empty sessionUserId}'>
<a href="user/signup">SignUp</a>
</c:if>
<c:if test='${empty sessionUserId}'><br>
<a href="socialauth?id=facebook"><img src="images/images.jpg" alt="Facebook" title="Facebook" border="0"></img></a>
<a href="socialauth?id=googleplus"><img src="images/sign-in-with-google.png" alt="Gmail" title="Gmail" border="0"></img></a>
</c:if>				
</div>
<c:if test='${!empty sessionUserId}'>
	<c:if test="${isAdmin}">
 		<c:forEach items="${link}" var="entry">
		<div class="item"><a href="${entry.key}">${entry.value}</a></div>
		</c:forEach>
	</c:if>
</c:if>
<div class="item">
<c:if test='${!empty sessionUserId}'>
<!--a href="restaurant/resources/APK">Download Android App</a-->
 <script type="text/javascript">
	var username = "<%=request.getSession().getAttribute("username")%>";
	var usernameMatch = /(.+)\@(.+)\.(.+)/;
	var restaurantId = "<c:out value='${sessionRestaurantId}'/>";
	if (usernameMatch.test(username)) {
		//alert("match");
		var matchArray = usernameMatch.exec(username).slice();
		document.write('<a target="csmobile" href="/static/clients/com/saladdays/index-pos.html">Point-of-Sale Page<\/a>');
		document.write('&nbsp;|&nbsp;<a target="csmobile" href="posDashboard.jsp">Point-of-Sale Page - New<\/a>');
	<c:if test="${isAdmin}">
		document.write('&nbsp;|&nbsp;<a target="csmobile" href="/static/mobile/index.html?{%22restaurants%22:[' + restaurantId + ']}">Visit your mobile website<\/a>');
		document.write('&nbsp;|&nbsp;<a target="cstable" href="/static/table/index.html?{%22restaurants%22:[' + restaurantId + ']}">Save your application using HTML5<\/a>');

		<%-- http://stackoverflow.com/questions/2291085/how-to-check-if-external-url-content-loads-correctly-into-an-iframe-in-jsp-pag --%>
		<c:set var="androidAppUrl" value=""/>
		<%
			String androidAppUrl = "";
			Pattern pattern = 
			Pattern.compile("(.+)\\@(.+)\\.(.+)");

			Matcher matcher = 
			pattern.matcher(request.getSession().getAttribute("username").toString());

			while (matcher.find()) {
				pageContext.setAttribute("androidAppUrl", "/static/clients/" + matcher.group(3) + "/" + matcher.group(2) + "/" + matcher.group(1) + ".apk");
			}
		%>
		<c:catch var="e">
			<c:import url="${androidAppUrl}" context="/" varReader="ignore"/>
			<c:out value="// Android App URL: ${androidAppUrl}" />
		</c:catch>
		<c:choose>
		<c:when test="${empty e}">
			document.write('&nbsp;|&nbsp;<a href="<c:out value="${androidAppUrl}" />">Download your Android App<\/a>');
		</c:when>
		<c:otherwise>
			document.write('&nbsp;|&nbsp;<a href="mailto:akshay@cookedspecially.com">Ask about custom Android App development<\/a>');
		</c:otherwise>
		</c:choose>
		document.write('&nbsp;|&nbsp;<a target="cschecks" href="checks.jsp#menus">Manage table status and print checks<\/a>');
	</c:if>
		document.write('&nbsp;|&nbsp;<a target="csordersNew" href="kitchenDashboard.jsp">Manage orders from customers<\/a>');
		document.write('&nbsp;|&nbsp;<a target="csdeliveryBoy" href="orderDelivery.jsp#menus">Dispatch Screen<\/a>');
		document.write('&nbsp;|&nbsp;<a target="csdeliveryBoy" href="deliveryDashboard.jsp">Dispatch Screen<\/a>');
		document.write('&nbsp;|&nbsp;<a target="cscashCollection" href="cashCollection.jsp#menus">Cash Collection Screen<\/a>');
	}
	else {
		//alert("no match");
	}
</script>
<br/>
<div><a href="order/searchChecks">Search Check</a></div>
<hr/>
<div><b>Download Reports</b> </div>
<c:choose>
<c:when test="${isAdmin}">
	<div><a href="reports/dailySalesSummary.xls?restaurantId=${sessionRestaurantId}">Daily Sales Summary</a> | <a href="reports/dailyInvoice.xls?restaurantId=${sessionRestaurantId}">Daily Invoice</a> | <a href="reports/customers.xls?restaurantId=${sessionRestaurantId}">Customers</a>| <a href="reports/topDishes.xls?restaurantId=${sessionRestaurantId}">Top Dishes</a> | <a href="reports/detailedInvoice.xls?restaurantId=${sessionRestaurantId}">Detailed Invoice</a><%-- | <a href="reports/deliveryBoyOrdersDetaill.xls?restaurantId=${sessionRestaurantId}">DeliveryBoys Orders Detail</a> --%></div>
	<hr/>
	<a href="reports/">Reports with Date Range</a>
</c:when>
<c:otherwise>
	<!-- Salad Days -->
	<div><a href="reports/dailyInvoice.xls?restaurantId=${sessionRestaurantId}">Daily Invoice</a></div>
</c:otherwise>
</c:choose>

</c:if>
<c:if test='${empty sessionRestaurantId}'>
<!--a href="restaurant/resources/APK?restaurantName=axis">Download Android App for Axis</a-->
</c:if>
</div>
</div>
<!-- 
hello this is symbol
<link rel="stylesheet" type="text/css" href="http://cdn.webrupee.com/font">
<span class="WebRupee">Rs</span>
 -->
</body>
<script type="text/javascript">
<c:if test="${isAdmin}">
var myTimeout;

function validate() {
    if (document.getElementById('theText').value == 1984) {
		document.getElementById("container").style.display = "block";
		document.getElementById("form").style.display = "none";
    }
    myTimeout = setTimeout(
    	function(){ 
			document.getElementById("container").style.display = "none";
			document.getElementById("form").style.display = "block";
    	}, 
    	600000
    );
	return false;
}
</c:if>

window.fbAsyncInit = function() {
  FB.init({
    appId      : '529381387164471',
    xfbml      : true,
    version    : 'v2.2'
  });
};

(function(d, s, id){
   var js, fjs = d.getElementsByTagName(s)[0];
   if (d.getElementById(id)) {return;}
   js = d.createElement(s); js.id = id;
   js.src = "//connect.facebook.net/en_US/sdk.js";
   fjs.parentNode.insertBefore(js, fjs);
 }(document, 'script', 'facebook-jssdk'));


  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-44124437-1', 'bakedspecially.com');
  ga('send', 'pageview');

</script>
</html>

