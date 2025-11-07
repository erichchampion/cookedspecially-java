// Create order Object
var order = {number:'',time:'',price:'',items:[]};             
var count = 0; 

   
function addToOrder(m,s,i) {
	console.info ('DEBUG >>>> addToOrder() item: ' + m + s + i);
	
	var item = globalData.menus[m].sections[s].items[i];
	order.items[count] = {
		id: item.itemId, 
		name: item.name, 
		price: item.price,
		smallImageUrl: item.smallImageUrl
	};
	
	order.price =  Math.round(order.price + item.price);
	
	count++;
	
	// update order summary
	$('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
	$('.orderNumOfItems .value').html( order.items.length );
	$('.orderPrice .label').html('Order Total: ');
	$('.orderPrice .value').html( order.price );
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
}



function removeFromOrder(id) {
	var newOrder = { number:order.number, time:order.time, price: '', items:[]};  
	var _removed = false;
	var newCount = 0;
	count = 0;
	
	$.each(order.items, function(i, item) {
		if ( id == item.id && ! _removed ){
			console.info('Removed Item: ' +  item.id );
			_removed = true;
		}else{
			//alert('Coping item: ' + order.items[count].id + " " + order.items[count].smallImageUrl);	
			newOrder.items[newCount] = {
				id: order.items[count].id, 
				name: order.items[count].name, 
				price: order.items[count].price,
				smallImageUrl: order.items[count].smallImageUrl
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
	if (order.items.length == 0 ){
		$('.orderNumOfItems .label').addClass('red').html('Empty' );
		$('.orderNumOfItems .value').html( '' );
		$('.orderPrice .label').html('');
		$('.orderPrice .value').html( '' );		
		$('.placeOrder').css('display','none');
		$('.order-button').hide();
		order.items = [];

	}else{
		$('.orderNumOfItems .label').removeClass('red').html('Number of items: ' );
		$('.orderNumOfItems .value').html( order.items.length );
		$('.orderPrice .label').html('Order Total: ');
		$('.orderPrice .value').html( order.price );
	}
	$('.orderItems').html('');
	
	
	//$('.placeOrder').css('display','inline-block');
	
	// update order items list
	$.each(order.items, function(i, item) {
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