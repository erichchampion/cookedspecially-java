<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="sessionRestaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
<c:choose>
<c:when test='${!empty sessionRestaurantId}'>
<!doctype html>
<html>
	<head>
		<!-- meta http-equiv="refresh" content="60;URL='#menus'" /-->
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black" />
		<link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400' rel='stylesheet' type='text/css' />
		<link rel="stylesheet" href="https://code.jquery.com/mobile/1.4.3/jquery.mobile-1.4.3.min.css" />
		<script type="text/javascript"  src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
		<script type="text/javascript"  src="https://code.jquery.com/mobile/1.4.3/jquery.mobile-1.4.3.min.js"></script>
		<title>Dispatch Screen</title>
		<script type="text/javascript" charset="utf-8" src="/static/checks/js/json2.js">
		</script>
	<!-- 	<script type="text/javascript" charset="utf-8" src="https://code.jquery.com/jquery-migrate-1.2.1.min.js">
		</script> -->
		<script type="text/javascript" charset="utf-8" src="https://code.jquery.com/ui/1.10.3/jquery-ui.js">
		</script>


		<script type="text/javascript" charset="utf-8" src="/static/checks/js/zepto.onpress.js">
		</script>

		<script type="text/javascript">
			var restaurantId = "<c:out value='${sessionRestaurantId}'/>";
		</script>
		<script type="text/javascript" charset="utf-8" src="/static/checks/js/ordersDelivery.js">
		</script>
		<link rel="stylesheet" type="text/css" href="/static/checks/css/mobile.css" />
		<link rel="stylesheet" type="text/css" href="/static/checks/css/style.css" />
		<link rel="stylesheet" type="text/css" href="/static/checks/css/admin.css" />
		<link rel="stylesheet" type="text/css" href="/static/checks/css/jqm-survival-kit.css" />
		<style>
			p#links a {
				margin-left: 1em;
			}
		</style>
		<script type="text/javascript" src="/static/checks/js/jquery.print.js"></script>
	</head>
	<body>		
		<div id="app-loader">
				<div id="loading">
				  <img src="/static/checks/css/images/app-loader.gif"/>
				</div>
		</div>
		<!-- http://stackoverflow.com/questions/13986182/how-can-i-improve-the-page-transitions-for-my-jquery-mobile-app/13986390#13986390 -->
		<div id="allPages" style="visibility:hidden;">
			
			<!-- Home -->
			<div data-role="page" data-theme="d" id="home">
				<a href="#menus" data-transition="slideup">
				<h1 style="color:#a00">Welcome</h1>
				<p>Please touch the screen to start</p>
				</a>
				<!-- a href="javascript:appSettings()" id="appSettings"><img src="/static/checks/images/settings-black.png"/></a -->
				<a href="#admin" id="appSettings"><img src="/static/checks/images/settings-black.png"/></a>
			</div>
			<!-- Menus -->
			<div data-role="page" data-theme="d" id="menus">
				<div data-theme="a" data-role="header" data-position="fixed">
					
					<img id="homeBtn" style="height:50px" data-role="button" onclick='location.href="#home"' src="/static/checks/images/axis-logo.png" />
					<div id="update" style="display:; float:right">
					   &nbsp;
					</div>
					<div id="nav">
					</div>
				</div>
				<div id="topMenu" style="width:100%; height:100%;  vertical-align:center; text-align:justify; overflow: hidden; list-style: inside;">
				</div>
				<div data-role="popup"  id="popupMoneyOut" data-theme="d" style=background:white class="ui-corner-all" data-dismissible="false">
		    <a href="#" onclick="$('#popupMoneyOut').popup('close');"  data-role="button" data-theme="d" data-icon="delete" data-iconpos="notext" class="ui-btn-left">Close</a>
			<form onsubmit="return false;">
			<div data-role="content" data-width="100%" >
			<select id="select-delivery-agent"  style="min-width:90%;" name="select-delivery-agent"  >
			</select>
			<select id="select-payment-mode" onchange="getval(this);"  style="min-width:90%;" name="select-payment-mode"  >
			<option value="CASH" selected="selected">CASH</option>
			<option disabled>___________________</option>
			<option value="COD" >COD</option>
			<option disabled>___________________</option>
			<option value="SUBSCRIPTION">SUBSCRIPTION</option>
			<option disabled>___________________</option>
			<option value="CREDIT">CREDIT</option>
			<option disabled>___________________</option>
			<option value="EMC">EMC</option>
			<option disabled>___________________</option>
			<option value="ROOM">ROOM</option>
			<option value="PG" disabled></option>
			</select>
			 <input type="button" data-theme="d" class="ui-btn ui-corner-all ui-shadow ui-btn-d ui-btn-icon-left ui-icon-check"  id="moneyOutVal1">
			 <input type="button" data-theme="d"  id="moneyOutVal2"  class="ui-btn ui-corner-all ui-shadow ui-btn-d ui-btn-icon-left ui-icon-check">
			 <input type="button" data-theme="d"  id="moneyOutVal3"  class="ui-btn ui-corner-all ui-shadow ui-btn-d ui-btn-icon-left ui-icon-check">
			 <Input type="number" name="moneyOut" id="moneyOut" maxlength="7" data-theme="d"  placeholder="Money Out " required> 
			<button id="setMoneyOut" data-theme="d"  type="submit" >Submit</button>
			</div>
			</form>
			</div>
		<div data-role="popup" id="popupMoneyIn" data-theme="d" style=background:white class="ui-corner-all" data-dismissible="false">
		<a href="#" onclick="$('#popupMoneyIn').popup('close');"  data-role="button" data-theme="d" data-icon="delete" data-iconpos="notext" class="ui-btn-left">Close</a>
			<form onsubmit="return false;">
			<div  data-role="content" data-width="100%" >
			 <input type="button" data-theme="d"  id="moneyInVal1"  class="ui-btn ui-corner-all ui-shadow ui-btn-d ui-btn-icon-left ui-icon-check">
			  <input type="button" data-theme="d"  id="moneyInVal2" class="ui-btn ui-corner-all ui-shadow ui-btn-d ui-btn-icon-left ui-icon-check">
			 <Input type="number" name="moneyIn" id="moneyIn" maxlength="7" data-theme="d"  placeholder="Money In " required > 
			<button id="setMoneyIn" data-theme="d" type="submit" >Submit</button>
			</div>
			</form>
		</div>	
				<br clear="both"/>
				<div class="footer">
					<b><span id="restaurantName"></span></b><br/>
					&copy; 2013 Cooked Specially. All rights reserved. 
				</div>
			</div>	
			
			<!-- No data -->
			<div data-role="page" data-theme="d" id="closed">
				<div data-theme="a" data-role="header" data-position="fixed">
					<img id="homeBtn" style="height:50px" data-role="button" onclick='location.href="#home"' src="/static/checks/images/axis-logo.png" />
					<div id="update" style="display:; float:right">
					   &nbsp;
					</div>
					<div id="nav">
					</div>
				</div>
				<div id="closed-content" data-role="content">
					<p>
						There are no open orders at this time.
					</p>
				</div>
				<br clear="both"/>
				<div class="footer">
					<b><span id="restaurantName"></span></b><br/>
					&copy; 2013 Cooked Specially. All rights reserved. 
				</div>
			</div>
		</div>	
	</body>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-44124437-1', 'bakedspecially.com');
  ga('send', 'pageview');

</script>
</html>
</c:when>
<c:otherwise>
<c:redirect url="index.jsp"/>
</c:otherwise>
</c:choose>
