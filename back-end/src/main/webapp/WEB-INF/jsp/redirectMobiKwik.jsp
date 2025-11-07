<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
</head>
<body>	
	<form name="myform" id="demo_form" action="${actionUrl}" method="post">
			<input class="required amount" size="50" name="amount" type="hidden" value="${amount}" />
			<input class="required number" size="50" maxlength="10" minlength="10" name="cell" type="hidden" value="${cell}" />
			<input size="30" name="orderid" type="hidden" value="${orderId}" />
			<input size="30" name="merchantname" type="hidden"  value="${merchantName}" />
			<input size="30" name="mid" type="hidden" id="mid" value="${mid}" />
			<input	type="hidden" name="redirecturl" value="${returnUrl}" />
			<input	type="hidden" name="checksum" value="${checksum}" />
						<input	type="hidden" name="version" value="2" />
			
	</form>
    <script type="text/javascript">
        document.myform.submit();
</script>
	</body>
	</html>
