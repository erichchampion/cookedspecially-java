<html>
<head>
<title>CookedSpecially Gateway redirect test</title>

</head>
<body bgcolor="#FFFFFF">
<!--stage url   https://pguat.paytm.com/oltp-web/processTransaction   -->
<!-- production url  https://secure.paytm.in/oltp-web/processTransaction   -->

 <form id="notice" action="${redirectURL}" method="post" style="text-align:center">
<input type=hidden name="ORDER_ID" value="${ORDER_ID}"><br>
<input type=hidden name="REQUEST_TYPE" value="${REQUEST_TYPE}"><br>
<input type=hidden name="MID" value="${MID}"><br>
<input type=hidden name="CUST_ID" value="${CUST_ID}"><br>
<input type=hidden name="TXN_AMOUNT" value="${TXN_AMOUNT}"><br>
<input type=hidden name="CHANNEL_ID" value="${CHANNEL_ID}"><br>
<input type=hidden name="INDUSTRY_TYPE_ID" value="${INDUSTRY_TYPE_ID}"><br>
<input type=hidden name="WEBSITE" value="${WEBSITE}"><br>
<input type=hidden name="CHECKSUMHASH" value="${CHECKSUMHASH}"><br><br><br>
<!-- <input type=submit value="Proceed to secure server"><br> -->
</form>
<script type="text/javascript">
function onLoad () {
	   var frm = document.getElementById("notice");
		frm.submit();
}
window.onload = onLoad();
</script>

</body>
</html>



