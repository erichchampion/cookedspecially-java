
var count = 0;




// Create order Object
var order = {
                number : "",
                time :  "",
                price :  "",
                items: [],
            };   
            
            
            
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
					
					alert ( item.name + '\nhas been added to your order');
					
					count++;
					
					//$('#' + p + ' .orderItems').html('Number of Items: ' + order.items.length );
					//$('#' + p + ' .orderPrice').html('Order Total: ' + order.price );
					$('.orderItems').html('Number of Items: ' + order.items.length );
					$('.orderPrice').html('Order Total: ' + order.price );

					return true;
				}
			});
		});
	});
	
	
}