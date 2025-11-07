<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title>Marketing Email</title>
	
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
	<script type="text/javascript" src="js/nicEdit.js"></script>
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript">
		bkLib.onDomLoaded(function() { 
			//nicEditors.allTextAreas()
			var nicEditorInstance = new nicEditor({fullPanel : false, iconsPath: 'images/nicEditorIcons.gif', buttonList : ['bold','italic','underline','left','center','right', 'justify', 'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat', 'indent', 'outdent', 'hr', 'forecolor', 'bgcolor', 'fontSize', 'fontFamily', 'fontFormat']});
			nicEditorInstance.panelInstance('emailBody');
		});
	</script>
	
</head>
<body>
<form method="post" action="order/sendMarketingEmail">

	<table>
	<tr>
		<td>Mail from RestaurantId</td>
		<td><input id="restaurantId" name="restaurantId" type="text"/></td>
	</tr>
	<tr>
		<td>To Email</td>
		<td><input id="email" name="email" type="text"/></td>
	</tr>
	<tr>
		<td>Subject</td>
		<td><input id="subject" name="subject" type="text"/></td>
	</tr>
	<tr>
		<td>Email Body</td>
		<td><textarea id="emailBody" name="emailBody"  placeholder="Email Content" style="width:680px;" ></textarea></td>
	</tr>
	<tr>
		<td><input type="submit" value="Send Email"/></td>
	</tr>
	</table>
</form>
</body>
</html>