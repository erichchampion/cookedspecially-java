<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3
.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<title>My jQuery JSON Web Page</title>
<head>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/themes/base/jquery-ui.css" />

<base href="${pageContext.request.contextPath}/"/>

<!-- <script type="text/javascript" src="js/jquery.js"></script> -->
<script type="text/javascript" src="js/jquery.simple-dtpicker.js"></script>
<link rel="stylesheet" type="text/css" href="css/jquery.simple-dtpicker.css" />

<script type="text/javascript">
var order = {number:20, checkId:2, tableId:1, time:'',price:'20.3',items:[]};
var itemsCount = 0;
/*order.items[0]={id: "", name :"", price: ""};
order.items[1]={id: "", name :"", price: ""};
order.items[2]={id: "", name :"", price: ""};
order.items[0].id=1;
order.items[1].id=2;
order.items[2].id=3;
order.items[0].name='Shashank';
order.items[1].name='Shashank2';
order.items[2].name='Shashank 3';
order.items[0].price='10.1';
order.items[1].price='10.3';+
order.items[2].price='10.5';
*/
function initializeOrder(){
    var formValues = $('input[type=text]');
    order = {};
    itemsCount = 0;
    $.map(formValues, function(n, i) {
        order[n.name] = $(n).val();
    });
	order['items'] = [];
    console.log(JSON.stringify(order));
    alert('order initialized');
}

function addItemToOrder(){
    var formValues = $('input[type=text]');
    order.items[itemsCount] = {};
    
    $.map(formValues, function(n, i) {
    	order.items[itemsCount][n.name] = $(n).val();
    });
	itemsCount = itemsCount + 1;
    console.log(JSON.stringify(order));
    alert('item added to order');
}

SubmitOrderJSON = function() {

    var resultDiv = $("#resultDivContainer");

    $.ajax({
        url: "order/addToCheck.json",
        type: "POST",
        data: JSON.stringify(order),
        contentType:"application/json; charset=utf-8",
        dataType: "json",
        success: function (result) {
            switch (result) {
                case true:
                    processResponse(result);
                    break;
                default:
                    resultDiv.html(result);
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
<h1>My jQuery JSON Web Page</h1>
<form id="createOrderForm" action="#" method="post">
    <table>
        <tr><td> CheckId: </td><td><input type="text" name="checkId" id="checkId"/>  </td></tr>
        <tr><td> TableId </td><td><input type="text" name="tableId" id="tableId"/>  </td></tr>
        <tr><td> CustId </td><td><input type="text" name="custId" id="custId"/>  </td></tr>
        <tr><td> RestaurantId: </td><td><input type="text" name="restaurantId" id="restaurantId"/>  </td></tr>
        <tr><td> DeliveryArea: </td><td><input type="text" name="deliveryArea" id="deliveryArea"/>  </td></tr>
        <tr><td> DeliveryAddress: </td><td><input type="text" name="deliveryAddress" id="deliveryAddress"/>  </td></tr>
        <tr><td>  </td><td>  <input type="button" id="createOrder" value="Initialize Order" onclick="initializeOrder()" /></td></tr>

    </table>
</form>
<form id="addItemToOrderForm" action="#" method="post">
    <table>
        <tr><td> ItemId: </td><td><input type="text" name="id" id="id"/>  </td></tr>
        <tr><td> Name: </td><td><input type="text" name="name" id="name"/>  </td></tr>
        <tr><td> Price: </td><td><input type="text" name="price" id="price"/>  </td></tr>
        <tr><td> Quantity: </td><td><input type="text" name="quantity" id="quantity"/>  </td></tr>
        <tr><td> DishType: </td><td><input type="text" name="dishType" id="dishType"/>  </td></tr>
        <tr><td>  </td><td>  <input type="button" id="addItem" value="Add Item to Order" onclick="addItemToOrder()" /></td></tr>
    </table>
</form>

<div id="resultDivContainer"></div>
<button type="button" onclick="SubmitOrderJSON()">Submit Order JSON</button>
<form id="deliveryTimeForm" action="order/setDeliveryTime" method="post">
    <label> CheckId:</label>
    <input type="text" name="checkId" />
    <label>Delivery Time : </label>
    <input type="text" name="deliveryTime" class="date-picker" />
    <input type="submit" value="Add DeliveryTime" />
</form>

<form id="deliveryAreaForm" action="order/setDeliveryArea" method="post">
    <label> CheckId:</label>
    <input type="text" name="checkId" />
    <label>Delivery Area : </label>
    <input type="text" name="deliveryArea" />
    <input type="submit" value="Add Delivery Area" />
</form>

<form id="deliveryAddressForm" action="order/setDeliveryAddress" method="post">
    <label> CheckId:</label>
    <input type="text" name="checkId" />
    <label>Delivery Address : </label>
	
    <input type="text" name="deliveryAddress"/>
    
    <input type="submit" value="Add Delivery Address" />
</form>
<form id="deliveryAddressForm" action="order/setDeliveryDetails" method="post">
    <label> CheckId:</label>
    <input type="text" name="checkId" />
    <label>Delivery Address : </label>
    <input type="text" name="deliveryAddress"/>
    <label>Delivery Area : </label>
    <input type="text" name="deliveryArea" />
    <label>Delivery Time : </label>
	
    <input type="text" name="deliveryTime" class="date-picker" />
    <input type="submit" value="Add Delivery Address" />
</form>
<script>
$(function() {
    $('.date-picker').appendDtpicker({"minuteInterval": 15});
});

</script>
</body>
</html> 