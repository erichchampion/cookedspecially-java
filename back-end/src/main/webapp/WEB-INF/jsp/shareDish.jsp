<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml" prefix="og: http://ogp.me/ns# fb: http://www.facebook.com/2008/fbml">
<head>
<meta id="og_title" property="og:title" content="${dish.name}"> <meta>
<meta id="og_description" property="og:description" content="${dish.description}"> <meta>
<meta id="og_image" property="og:image" content="https://www.cookedspecially.com${dish.imageUrl}"> <meta>
<c:choose>
	<c:when test="${!empty shareDishJson}">
		<meta property="fb:app_id"                            content="${shareDishJson.fbAppId}" />
		<meta property="og:type"                              content="${shareDishJson.ogType}" />
		<meta property="og:url"                               content="${shareDishJson.ogUrl}" />
		<meta property="business:contact_data:street_address" content="${shareDishJson.address}" />
		<meta property="business:contact_data:locality"       content="${shareDishJson.locality}" />
		<meta property="business:contact_data:postal_code"    content="${shareDishJson.postalCode}" />
		<meta property="business:contact_data:country_name"   content="${shareDishJson.country}" />
		<meta property="place:location:latitude"              content="${shareDishJson.longitude}" />
		<meta property="place:location:longitude"             content="${shareDishJson.latitude}" />
	</c:when>
	<c:otherwise>
		<meta property="fb:app_id"                            content="226697114202871" />
		<meta property="og:type"                              content="restaurant.menu_item" />
		<meta property="og:url"                               content="https://www.cookedspecially.com/static/clients/com/saladdays/index-beta.html" />
		<c:choose>
			<c:when test="${!empty address}">
				<meta property="business:contact_data:street_address" content="${address}" />
				
			</c:when>
			<c:otherwise>
				<meta property="business:contact_data:street_address" content="U 60/37 Dlf city phase 3" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${!empty city}">
				<meta property="business:contact_data:street_address" content="${city}" />
			</c:when>
			<c:otherwise>
				<meta property="business:contact_data:locality"       content="Gurgaon, Haryana" />
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${!empty zip}">
				<meta property="business:contact_data:street_address" content="${zip}" />
			</c:when>
			<c:otherwise>
				<meta property="business:contact_data:postal_code"    content="122002" />
			</c:otherwise>
		</c:choose>
		<meta property="business:contact_data:country_name"   content="India" />
		<meta property="place:location:latitude"              content="28.4700" />
		<meta property="place:location:longitude"             content="77.0300" />
	</c:otherwise>
</c:choose>

</head>
<body>
</body>
</html>