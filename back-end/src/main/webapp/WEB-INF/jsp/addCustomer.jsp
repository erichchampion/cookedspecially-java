<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<title>Add Customer info JSON page</title>
<head>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<base href="${pageContext.request.contextPath}/"/>
<script type="text/javascript">
var customer = {firstName:'Shreyash', lastName: 'Agarwal', address: 'Flat 808', city: 'Hyderabad', phone: '9989794670', email: 'shreyashagarwal0111@gmail.com'};

function initializeCustomer(){
    var formValues = $('input[type=text]');
    customer = {};
    $.map(formValues, function(n, i) {
        customer[n.name] = $(n).val();
    });
	
    console.log(JSON.stringify(customer));
    alert('customer initialized');
}


SubmitCustomerJSON = function() {

    var resultDiv = $("#resultDivContainer");

    $.ajax({
        url: "customer/setCustomerInfo.json",
        type: "POST",
        data: JSON.stringify(customer),
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

<h1>Add Customer JSON</h1>
<form id="createCustomerForm" action="#" method="post">
    <table>
        <tr><td> customerId: </td><td><input type="text" name="customerId" id="customerId"/>  </td></tr>
        <tr><td> RestaurantId: </td><td><input type="text" name="restaurantId" id="restaurantId"/>  </td></tr>
        <tr><td> First Name: </td><td><input type="text" name="firstName" id="firstName"/>  </td></tr>
        <tr><td> Last Name: </td><td><input type="text" name="lastName" id="lastName"/>  </td></tr>
        <tr><td> Email: </td><td><input type="text" name="email" id="email"/>  </td></tr>
        <tr><td> Phone: </td><td><input type="text" name="phone" id="phone"/>  </td></tr>
        <tr><td> address: </td><td><input type="text" name="address" id="address"/>  </td></tr>
        <tr><td> city: </td><td><input type="text" name="city" id="city"/>  </td></tr>
        <tr><td>  </td><td>  <input type="button" id="createCustomer" value="Initialize Customer" onclick="initializeCustomer()" /></td></tr>

    </table>
</form>

<div id="resultDivContainer"></div>

<button type="button" onclick="SubmitCustomerJSON()">Submit Customer JSON</button>

</body>
</html> 