

// Create order Object
var order = {number:'',time:'',price:'',items:[]};             
var count = 0; 

   
function addToOrder(p, id) {
	console.info ('DEBUG >>>> addToOrder() item: ' + id);
	
	$.each(globalData.menus, function(i, menu) {
		$.each(menu.sections, function(i, section) {
			$.each(section.items, function(i, item) {
				itemId = "item" + item.itemId;			
				if ( id == itemId ){
					 
					order.items[count] = {id:"", name:"", price:""};
					order.items[count].id = item.itemId;
				 	order.items[count].name = item.name;
					order.items[count].price = item.price;
					
					order.price =  Math.round(order.price + item.price);
					
					//alert ( item.name + '\nhas been added to your order');
					
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
								  '    <div class="removeFromOrder"><a href="javascript:removeFromOrder(\'' +  id + '\')">x</a></div>' +
								  '  </div>' + 
								  '  <div class="orderItemName">' + item.name + '</div>' +
								  '</div>';
					
					
					$('.orderItems').append(theItem);
					
					
					$('.order-button').fadeIn();
					
					$('.addToOrder').slideUp('fast').slideDown('fast');
					//$('.addToOrder').animate({width:'105px',height:'22px'},500).animate({width:'95px',height:'19px'},500);
					
					
					return true;
				}
			});
		});
	});
	
	
	
	

}


function removeFromOrder(id) {
	var _newOrder = {number:order.number, time:order.time, price: order.price, items:[]};  
	var _id = id.replace("item","");
	var _removed = false;
	var _skip = false;
	//alert( 'remove from order: ' + _id );
	
	$.each(order.items, function(i, item) {
		
		if ( _id == item.id && ! _removed ){
			alert('Removed item: ' +  item.name );
			_removed = true;
		}else{
			alert('adding item: ' +  item.id );	
		}
		
	});
	
	
	
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