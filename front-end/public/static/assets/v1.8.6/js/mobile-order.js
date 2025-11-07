
// Error messages
MSG_ERROR_NO_TABLE_ASSIGNED = 'Sorry!\nNo table number or tab has been assigned to this device.\n\nPlease contact your server.';



// Create order Object
var order = [];
var active = 0;          
var count = 0; 



function setActiveTable(table){
	// first check for existing open orders
	var newOrder = true; // default value
	for ( i=0;i<=order.length;i++){
		if ( typeof(order[i]) != "undefined" && order[i].table == table ){
			alert ('Existing Order.\nTable number has been changed to: ' + order[i].table);
			newOrder = false;
			active = i;
			
		}
	}
	// not an existing order, create new one
	if ( newOrder ){
		active = order.length;
		order[active] = {number:'', table:table, time:'',price:'', items:[]};   
		alert ('New Order.\nTable number has been set to: ' + table);
	}
	updateOrder();
}
   
   
   
function addToOrder(m,s,i) {

	if ( typeof(order[active]) != "undefined" && typeof(order[active].table) != "undefined" ){
		console.info ('DEBUG >>>> addToOrder() item: ' + m + s + i);
		
		var item = globalData.menus[m].sections[s].items[i];
		order[active].items[count] = {
			id: item.itemId, 
			name: item.name, 
			price: item.price,
			smallImageUrl: item.smallImageUrl
		};
		
		order[active].price =  Math.round(order[active].price + item.price);
		
		count++;
		
		// update order summary
		$('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
		$('.orderNumOfItems .value').html( order[active].items.length );
		$('.orderPrice .label').html('Order Total: ');
		$('.orderPrice .value').html( order[active].price );
		$('.placeOrder').css('display','inline-block');
		// update order items list
		var theItem = '<div class="orderItem">' + 
					  '  <div class="orderItemImg" ' +
					  '        style="background-image:url(' + comCookedSpeciallyImagePrefix + item.smallImageUrl + ')">' +
					  '    <div class="removeFromOrder"><a href="javascript:removeFromOrder(\'' +  item.itemId + '\')">x</a></div>' +
					  '  </div>' + 
					  '  <div class="orderItemName">' + item.name + '</div>' +
					  '</div>';
		$('.orderItems').append(theItem);

		//updateOrder();

		$('.order-button').fadeIn();
		$('.addToOrder').slideUp('fast').slideDown('fast');
	
	}else{
		alert( MSG_ERROR_NO_TABLE_ASSIGNED );
	}
}



function removeFromOrder(id) {
	var newOrder = { number:order[active].number, table:order[active].table, time:order[active].time, price: '', items:[]};  
	var _removed = false;
	var newCount = 0;
	count = 0;
	
	$.each(order[active].items, function(i, item) {
		if ( id == item.id && ! _removed ){
			console.info('Removed Item: ' +  item.id );
			_removed = true;
		}else{
			//alert('Coping item: ' + order.items[count].id + " " + order.items[count].smallImageUrl);	
			newOrder.items[newCount] = {
				id: order[active].items[count].id, 
				name: order[active].items[count].name, 
				price: order[active].items[count].price,
				smallImageUrl: order[active].items[count].smallImageUrl
			};
			newOrder.price =  Math.round(newOrder.price + newOrder.items[newCount].price);
			newCount++;
		}
		count++;
	});
	count = newCount;
	order[active] = newOrder;
	updateOrder();	
}




function updateOrder(){
	// update order summary	
	if (order[active].items.length == 0 ){
		$('.orderNumOfItems .label').addClass('red').html('Empty' );
		$('.orderNumOfItems .value').html( '' );
		$('.orderPrice .label').html('');
		$('.orderPrice .value').html( '' );		
		$('.placeOrder').css('display','none');
		$('.order-button').hide();
		order[active].items = [];

	}else{
		$('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
		$('.orderNumOfItems .value').html( order[active].items.length );
		$('.orderPrice .label').html('Order Total: ');
		$('.orderPrice .value').html( order[active].price );
	}
	$('.orderItems').html('');
	
	
	//$('.placeOrder').css('display','inline-block');
	
	// update order items list
	$.each(order[active].items, function(i, item) {
		var theItem = '<div class="orderItem">' + 
				  '  <div class="orderItemImg" ' +
				  '        style="background-image:url(' + comCookedSpeciallyImagePrefix + item.smallImageUrl + ')">' +
				  '    <div class="removeFromOrder"><a href="javascript:removeFromOrder(\'' +  item.id + '\')">x</a></div>' +
				  '  </div>' + 
				  '  <div class="orderItemName">' + item.name + '</div>' +
				  '</div>';
	
		$('.orderItems').append(theItem);
	});	

	//$('.order-button').fadeIn();
	//$('.addToOrder').slideUp('fast').slideDown('fast');
	//$('.addToOrder').animate({width:'105px',height:'22px'},500).animate({width:'95px',height:'19px'},500);

}



function showOrder(){
	$('.shade').toggle();
	$('.order').toggle();
}




function placeOrder(){
	order = {number:'',time:'',price:'',items:[]}; 
	count = 0;
	alert ('Your order has been placed...');	
	
	$('.orderNumOfItems').addClass('red').html(' Empty.');
	$('.orderPrice').html('');
	$('.orderItems').html('');
	$('.placeOrder').css('display','none');
	$('.order-button').fadeOut();
	$('.itemDetail').fadeOut();
	$('.shade').fadeOut();
	$('.order').fadeOut();
}