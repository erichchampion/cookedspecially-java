<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
	<title>Error</title>
</head>
<body>
	<h1>Ops! Something went wrong</h1>
	<c:if test="${not empty errMsg}">
    		<h4>${errMsg}</h4>
    </c:if>
</body>
</html>