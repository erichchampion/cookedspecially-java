<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forbidden</title>
</head>
<body>
<h1 style="background-color: #E82F3A; font:bold; font-style: italic;">Forbidden!</h1>
<h2 style="font: bold;">You do not have permission to access the requested resource.</h2>
<br>
<table align="center">
<tr>
<td>
<button type="button" style="background-color: #E1E6F6; width: 170px; height: 30px;" name="back" onclick="location.href='index.jsp'">Go Back</button>
</td>
</tr>
</table>
<script>
function goBack() {
    window.history.back();
}
</script>
</body>
</html>