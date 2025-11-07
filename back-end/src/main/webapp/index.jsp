<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="java.util.regex.*"%>
<html>
<head>
<meta name="google-site-verification"
	content="lVKJm-21o0luyMpMRbZKEWK-BhhBZiea6UYuWI5ZxfQ" />
<meta charset="utf-8" />
<title>CookedSpecially</title>

<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->

<link rel="stylesheet" href="css/style.css" />

<!-- scripts at bottom of page -->
</head>
<body class="homepage">
	<c:set var="sessionUserId"
		value='<%=request.getSession().getAttribute("userId")%>' />
	<c:set var="sessionRestaurantId"
		value='<%=request.getSession().getAttribute("restaurantId")%>' />
	<c:choose>
		<c:when test="${sessionRestaurantId == '33'}">
			<c:set var="sessionPosFilename" value='index-pos-delhi.html' />
		</c:when>
		<c:otherwise>
			<c:set var="sessionPosFilename" value='index-pos.html' />
		</c:otherwise>
	</c:choose>
	<c:set var="role"
		value='<%=request.getSession().getAttribute("role")%>' />
	<c:set var="isAdmin" value="${role == 'admin'}" />
	<c:set var="isRestManager" value="${role == 'restaurantManager'}" />
	<c:set var="isFFCManager" value="${role == 'fulfillmentCenterManager'}" />

	<style>
#form {
	display: none
}

#container {
	display: block
}
</style>

	<c:if test="${!(isAdmin || isRestManager) }">
		<jsp:forward page="/user/login" />
	</c:if>

	<div id="form">
		<form onsubmit="return false;">
			<input type="text" maxlength="50" name="theText" id="theText"
				value="" />
			<button id="continue" type="button" onclick="validate();">Continue</button>
		</form>
	</div>
	<div id="container">
		<div class="item">
			<c:if test='${!empty sessionUserId}'>
         Logged in as <%=request.getSession().getAttribute("username")%>(<%=request.getSession().getAttribute("role")%>) | <a
					href="j_spring_security_logout">Logout </a>
				<!-- <a href="user/edit">Edit</a> | <a href="user/logout">Logout</a> -->
			</c:if>
			<c:if test='${empty sessionUserId}'>
				<a href="user/login" id="login">Login</a>
			</c:if>
			<br>
			<c:if test='${empty sessionUserId}'>
				<a href="user/signup">SignUp</a>
			</c:if>
			<c:if test='${empty sessionUserId}'>
				<br>
				<a href="socialauth?id=facebook"><img src="images/images.jpg"
					alt="Facebook" title="Facebook" border="0"></img></a>
				<a href="socialauth?id=googleplus"><img
					src="images/sign-in-with-google.png" alt="Gmail" title="Gmail"
					border="0"></img></a>
			</c:if>
		</div>
		<c:if test="${isAdmin || isRestManager}">
			<c:if test='${!empty sessionUserId}'>
				<c:forEach items="${orgLink}" var="entry">
					<div class="item">
						<a href="${entry.key}">${entry.value}</a>
					</div>
				</c:forEach>
			</c:if>
			<div class="item">
						<a href="${entry.key}">${entry.value}</a>
			</div>
		</c:if>

		<div class="item">
			<c:if test='${!empty sessionUserId}'>
				<script type="text/javascript">
				var username = "<%=request.getSession().getAttribute("username")%>";
	  			var orgId = "<%=request.getSession().getAttribute("organisationId")%>";
					var usernameMatch = /(.+)\@(.+)\.(.+)/;
					var restaurantId = "<c:out value='${sessionRestaurantId}'/>";
					if (usernameMatch.test(username)) {
						//alert("match");
						var matchArray = usernameMatch.exec(username).slice();
						document.write('<div>');
						<c:if test="${isAdmin}">
						/* document.write('<a target="csmobile" href="/static/mobile/index.html?{%22restaurants%22:[' + restaurantId + ']}">Visit your mobile website<\/a>'); */
				<%-- http://stackoverflow.com/questions/2291085/how-to-check-if-external-url-content-loads-correctly-into-an-iframe-in-jsp-pag --%>
					<c:set var="androidAppUrl" value=""/>
				<%String androidAppUrl = "";
					Pattern pattern = Pattern.compile("(.+)\\@(.+)\\.(.+)");

					Matcher matcher = pattern.matcher(request.getSession().getAttribute("username").toString());

					while (matcher.find()) {
						pageContext.setAttribute("androidAppUrl", "/static/clients/" + matcher.group(3) + "/"
								+ matcher.group(2) + "/" + matcher.group(1) + ".apk");
					}%>
					/* <c:catch var="e">
						<c:import url="${androidAppUrl}" context="/" varReader="ignore"/>
						<c:out value="// Android App URL: ${androidAppUrl}" />
						</c:catch> */
						/* <c:choose>
						 <c:when test="${empty e}">
						 document.write('<hr>');
							document.write('&nbsp;|&nbsp;<a href="<c:out value="${androidAppUrl}" />">Download your Android App<\/a>');
						</c:when>
						 <c:otherwise>
							document.write('&nbsp;|&nbsp;<a href="mailto:akshay@cookedspecially.com">Ask about custom Android App development<\/a>');
						</c:otherwise> 
						</c:choose> */

						/* document.write('</div>');
						document.write('<hr>');
						document.write('<div>New Kitchen Screens <br>');
						document
								.write('<a target="csmobile" href="posDashboard.jsp">Point-of-Sale Page - New<\/a>');
						document
								.write('&nbsp;|&nbsp;<a target="csordersNew" href="kitchenDashboard.jsp">Manage orders from customers<\/a>');
						document
								.write('&nbsp;|&nbsp;<a target="csdeliveryBoy" href="deliveryDashboard.jsp">Dispatch Screen<\/a>');
						document
								.write('&nbsp;|&nbsp;<a target="tills" href="tillDashboard.jsp">Manage Tills<\/a>');
						document.write('</div>');
 */
                        document.write('<a target="csmobile" href="customerManagement.jsp" >Customer Management<\/a>');
						document.write('<div>Generic Website link<br>');
	                    document.write('<a target="csmobile" href="/newGenApp?orgId='+ orgId + '">Generic Website<\/a>');
	                    document.write('</div>');
						
						document.write('<div>Demo webapp link<br>');
						document
								.write('<a target="csmobile" href="/genericapp?orgId='
										+ orgId + '">webapp link<\/a>');
						document.write('</div>');

						document.write('<hr>');
						//document.write('<div>Dine-in Kitchen  Screens <br>');
						//document.write('<a target="cstable" href="/static/table/index.html?{%22restaurants%22:[' + restaurantId + ']}">Save your application using HTML5<\/a>');
						//document.write('&nbsp;|&nbsp;<a target="cschecks" href="checks.jsp#menus">Manage table status and print checks<\/a>');

					} else {
						//alert("no match");
					}
					</c:if>
				</script>
				<!-- <hr>
<div> Search Check : <a href="order/searchChecks">search check</a></div>
<hr/> -->
				<div>
					<b>Download Reports</b>
				</div>
				<c:choose>
					<c:when test="${isAdmin}">
						<div>
							<%-- <a href="reports/dailySalesSummaryNew.xls?restaurantId=${sessionRestaurantId}">Daily Sales Summary New</a> | <a href="reports/dailySalesSummary.xls?restaurantId=${sessionRestaurantId}">Daily Sales Summary</a> | <a href="reports/dailyInvoice.xls?restaurantId=${sessionRestaurantId}">Daily Invoice</a> |  --%>
							<a
								href="reports/customers.xls?restaurantId=${sessionRestaurantId}">Customers</a> | 
							<a  href="analysisAndReport/orderReport/" target="_blank">Report-Dashboard</a>
							<%-- | <a href="reports/topDishes.xls?restaurantId=${sessionRestaurantId}">Top Dishes</a> | <a href="reports/detailedInvoice.xls?restaurantId=${sessionRestaurantId}">Detailed Invoice</a>|  --%>
							<%-- <a href="reports/deliveryBoyOrdersDetaill.xls?restaurantId=${sessionRestaurantId}">DeliveryBoys Orders Detail</a> --%>
						</div>
						<hr />
						<!-- <a href="reports/">Reports with Date Range</a> -->
					</c:when>
				</c:choose>

			</c:if>
			<c:if test='${empty sessionRestaurantId}'>
				<!--a href="restaurant/resources/APK?restaurantName=axis">Download Android App for Axis</a-->
			</c:if>
		</div>
	</div>
</body>
<script type="text/javascript">
	/* window.history.forward();
	 function noBack() { window.history.forward(); } */

	window.onload = function() {
		document.getElementById("login").click();
	}

	<c:if test="${isAdmin}">
	var myTimeout;

	function validate() {
		if (document.getElementById('theText').value == 1984) {
			document.getElementById("container").style.display = "block";
			document.getElementById("form").style.display = "none";
		}
		myTimeout = setTimeout(function() {
			document.getElementById("container").style.display = "none";
			document.getElementById("form").style.display = "block";
		}, 600000);
		return false;
	}
	</c:if>

	window.fbAsyncInit = function() {
		FB.init({
			appId : '529381387164471',
			xfbml : true,
			version : 'v2.2'
		});
	};

	(function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id)) {
			return;
		}
		js = d.createElement(s);
		js.id = id;
		js.src = "//connect.facebook.net/en_US/sdk.js";
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));

	(function(i, s, o, g, r, a, m) {
		i['GoogleAnalyticsObject'] = r;
		i[r] = i[r] || function() {
			(i[r].q = i[r].q || []).push(arguments)
		}, i[r].l = 1 * new Date();
		a = s.createElement(o), m = s.getElementsByTagName(o)[0];
		a.async = 1;
		a.src = g;
		m.parentNode.insertBefore(a, m)
	})(window, document, 'script', '//www.google-analytics.com/analytics.js',
			'ga');

	ga('create', 'UA-44124437-1', 'bakedspecially.com');
	ga('send', 'pageview');
</script>
</script>
</html>

