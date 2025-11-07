/* Script name: mobile-order.js
 * Description: Javascript code for handling orders
 * List of functions:
 * - addToOrder()
 * - removeFromOrder()
 * - updateOrder()
 * - showOrder()
 * - placeOrder()
 */



console.info ('LOADING >>> mobile-order.js...');

// Error messages
MSG_ERROR_NO_TABLE_ASSIGNED = 'Sorry!\nNo table number or tab has been assigned to this device.\n\nPlease contact your server.';


// Create order Object
var pendingOrder = {};
var count = 0; 
  

// addToOrder( menu, section, item)  
function addToOrder(m, s, i) {
    console.info ('EXECUTING >>> addToOrder()');
    
    // pendingOrder should be pre-populated by setTable
    // with the table and check info
    if ( ! $.isEmptyObject(pendingOrder) ){
        console.info ('DEBUG >>> addToOrder() item: ' + m + s + i);
        
        var item = globalData.menus[m].sections[s].items[i];
        pendingOrder.items[count] = {
            id: item.itemId, 
            name: item.name, 
            price: item.price,
            smallImageUrl: item.smallImageUrl
        };
        count++;
        
        // update order price
        pendingOrder.price =  Math.round(pendingOrder.price + item.price);
        
        // update pendingOrder summary
        $('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
        $('.orderNumOfItems .value').html( pendingOrder.items.length );
        $('.orderPrice .label').html('Amount: ');
        $('.orderPrice .value').html( currency + pendingOrder.price );
        $('.placeOrder').css('display','inline-block');
        // update pendingOrder items list
        var theItem = '<div class="orderItem">' + 
                      '  <div class="orderItemImg" ' +
                      '        style="background-image:url(' + comCookedSpeciallyImagePrefix + item.smallImageUrl + ')">' +
                      '    <div class="removeFromOrder"><a href="javascript:removeFromOrder(\'' +  item.itemId + '\')">x</a></div>' +
                      '  </div>' + 
                      '  <div class="orderItemName">' + item.name + '</div>' +
                      '</div>';
        $('.orderItems').append(theItem);

        //updateOrder();

        $('.view-order-button').fadeIn();
        $('.addToOrder').slideUp('fast').slideDown('fast');

    }else{
        alert( MSG_ERROR_NO_TABLE_ASSIGNED );
    }
    
}


function removeFromOrder(id) {
    var newOrder = { number:pendingOrder.number, table:pendingOrder.table, time:pendingOrder.time, price: '', items:[]};  
    var _removed = false;
    var newCount = 0;
    count = 0;
    
    $.each(pendingOrder.items, function(i, item) {
        if ( id == item.id && ! _removed ){
            console.info('Removed Item: ' +  item.id );
            _removed = true;
        }else{
            //alert('Coping item: ' + pendingOrder.items[count].id + " " + pendingOrder.items[count].smallImageUrl);    
            newOrder.items[newCount] = {
                id: pendingOrder.items[count].id, 
                name: pendingOrder.items[count].name, 
                price: pendingOrder.items[count].price,
                smallImageUrl: pendingOrder.items[count].smallImageUrl
            };
            newOrder.price =  Math.round(newOrder.price + newOrder.items[newCount].price);
            newCount++;
        }
        count++;
    });
    count = newCount;
    pendingOrder = newOrder;
    updateOrder();    
}


function updateOrder(){
    // update order summary    
    if (pendingOrder.items.length == 0 ){
        $('.orderNumOfItems .label').addClass('red').html('Empty' );
        $('.orderNumOfItems .value').html( '' );
        $('.orderPrice .label').html('');
        $('.orderPrice .value').html( '' );        
        $('.orderPrice .value').html( '' );        
        $('.placeOrder').css('display','none');
        $('.view-order-button').hide();
        pendingOrder.items = [];

    }else{
        $('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
        $('.orderNumOfItems .value').html( pendingOrder.items.length );
        $('.orderPrice .label').html('Amount: ');
        $('.orderPrice .value').html( currency + pendingOrder.price );
    }
    $('.orderItems').html('');
    
    //$('.placeOrder').css('display','inline-block');
    
    // update order items list
    $.each(pendingOrder.items, function(i, item) {
        var theItem = '<div class="orderItem">' + 
                  '  <div class="orderItemImg" ' +
                  '        style="background-image:url(' + comCookedSpeciallyImagePrefix + item.smallImageUrl + ')">' +
                  '    <div class="removeFromOrder"><a href="javascript:removeFromOrder(\'' +  item.id + '\')">x</a></div>' +
                  '  </div>' + 
                  '  <div class="orderItemName">' + item.name + '</div>' +
                  '</div>';
    
        $('.orderItems').append(theItem);
    });    

}


function showOrder(){
    $('.shade').toggle();
    $('.order').toggle();
}


function placeOrder(){
    var jsonOrder = JSON.stringify(pendingOrder);
    $.ajax({
        type: "POST",
        contentType:"application/json; charset=utf-8",
        url: _addToCheck,
        data: jsonOrder,
        success: function (data) {
            // expected return: 
            //{"orderId":1,"checkId":1,"restaurantId":1,"tableId":1,"error":"Error message","status":"Failed|Success"}
            if ( data.status == "Success"){
                // show my check button
                $('.view-check-button').fadeIn();
                getCheck(activeCheck.tableId);
                alert("Thank you! Your order has been sent."); // +
                //      "\nCheck Id: " + data.checkId +  " on Table Id: " + data.tableId );
            
            }else if ( data.status == "Failed"){
                alert("An Error has occurred: " + data.checkId + 
                      "\n Check Id: " + data.checkId +  
                      "\n Table Id: " + data.tableId );
            }else{
                alert("An Unknown Error has occurred.") 
            }
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
        }
    });

    //reset pendingOrder and count
    console.log("activeCheck.checkId: " + activeCheck.checkId);
    console.log("activeCheck.tableId: " + activeCheck.tableId);
    pendingOrder = {checkId:activeCheck.checkId, table:activeCheck.tableId, time:'',price:'', items:[]};
    count = 0;
    
    // update UI elements
    $('.itemDetail').fadeOut();
    updateOrder();
    $('.shade').fadeOut();
    $('.order').fadeOut();
    
}



