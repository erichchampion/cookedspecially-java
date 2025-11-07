<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3
.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<title>My JSON Web Page for Share Dish</title>
<head>
  
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/base/jquery-ui.css" />

<base href="${pageContext.request.contextPath}/"/>
<script type="text/javascript">
var shareDish = {
			restaurantId:1, 
			dishId:2, 
			fbAppId:"226697114202871", 
			ogType: "restaurant.menu_item", 
			ogUrl:"https://www.cookedspecially.com/static/clients/com/saladdays/index-beta.html",
			address: "U 60/37 Dlf city phase 3",
			postalCode: 122002,
			locality: "Gurgaon, Haryana",
			country: "India",
			longitude: "28.4700",
			latitude: "77.0300"
		};
		
SubmitShareDishJSON = function() {

    $.ajax({
        url: "dish/shareDish.json",
        type: "POST",
        data: JSON.stringify(shareDish),
        contentType:"application/json; charset=utf-8",
        dataType: "html",
        success: function (result) {
            switch (result) {
                case true:
                    processResponse(result);
                    break;
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
        alert(xhr.status);
        alert(thrownError);
        }
    });
};
</script>
</head>
<body>
<button type="button" onclick="SubmitShareDishJSON()">Submit Share Dish JSON</button>
</body>
</html>