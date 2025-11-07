
// Error messages
MSG_ERROR_NO_TABLE_ASSIGNED = 'Sorry!\nNo table number or tab has been assigned to this device.\n\nPlease contact your server.';



// Create order Object
var pendingOrder = {};
var count = 0; 



   
   
function addToOrder(m,s,i) {

	//if ( typeof(pendingOrder) != "undefined" && typeof(pendingOrder.table) != "undefined" ){
	if ( ! $.isEmptyObject(pendingOrder) ){
		console.info ('DEBUG >>>> addToOrder() item: ' + m + s + i);
		
		var item = globalData.menus[m].sections[s].items[i];
		pendingOrder.items[count] = {
			id: item.itemId, 
			name: item.name, 
			price: item.price,
			smallImageUrl: item.smallImageUrl
		};
		
		pendingOrder.price =  Math.round(pendingOrder.price + item.price);
		
		count++;
		
		// update pendingOrder summary
		$('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
		$('.orderNumOfItems .value').html( pendingOrder.items.length );
		$('.orderPrice .label').html('Order Total: ');
		$('.orderPrice .value').html( pendingOrder.price );
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
	order = newOrder;
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
		$('.orderPrice .label').html('Order Total: ');
		$('.orderPrice .value').html( pendingOrder.price );
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
	$('.view-order-button').fadeOut();
	$('.itemDetail').fadeOut();
	$('.shade').fadeOut();
	$('.order').fadeOut();
}



function showCheck(){
	window.console && console.log('in showCheck()');
	$('#check').toggle();
}

