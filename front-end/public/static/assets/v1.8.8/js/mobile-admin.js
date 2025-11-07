var accessCode = '';
var tableId = '';
var active = 0;          
var restaurantTables = {};
var selectedTableId = '';



function keyDown(src, k){
    if (src == "accessCode") {
        if ( $(k).attr('value') != 'OK'){
            accessCode = accessCode + $(k).attr('value');
            $('#accessCodeField').html($('#accessCodeField').html() + '*');
        }else{
            checkAccessCode();
        }
        
    }
}

function checkAccessCode(k){
    if ( accessCode == "1984" ){
		$('#accessCodeField').html('');
        $('#adminLogin').hide();
	     
		initControlPanel();
		$('#controlPanel').show();
	
    }else{
        alert( 'Fail' );
    }
    accessCode = '';
	$('#accessCodeField').html(accessCode);

}



function initControlPanel(){
	getRestaurantTables();
}


function getRestaurantTables(){
	//alert ("getting tables for restaurant " + restaurantId );
	//var url = "http://www.bakedspecially.com/api/getRestaurantTables.php?restaurantId=1",
	var url = 'http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/gettablesjsonbyuname?username=axis@cookedspecially.com';

	$.ajax({
	  url: url,
	  dataType: 'json'
	})
	.done (function (data) {
		restaurantTables = data;
		//$('#showTableNumbers').append( '<li>restaurantTables.tables[i].name</li>');
		//$('#showTableNumbers').append('</ul>');
	
	})
	.fail(function() { 
		console.log("AJAX: getRestaurantTables() FAILED"); 
	})
	.always(function() { 
		console.log("AJAX: getRestaurantTables() COMPLETED"); 

	});

}


function showRestaurantTables(){
	$('#activeTableName').hide();
	//$('#restaurantTables').html('').css('display','inline-table');
	$('#restaurantTables').html('').show();

	$('#restaurantTables').append( '<div class="tableInfoHeader"><span class="tableName"> Name</span>' +
									'<span class="tableStatus">Status</span>' +
									'<span class="tableGuests">Guests</span>' +
									'</div>');
										
	for ( i=0; i < restaurantTables.tables.length; i++){

		var _tableID   = restaurantTables.tables[i].seatingTableId;
		var _tableName = restaurantTables.tables[i].name;
		var _tableGuests = 4;
		var _tableStatus = 'Available';
		
		
		$('#restaurantTables').append( '<div class="tableInfo" onclick="setActiveTable(\''+ _tableID +'\',\''+_tableName+'\')"><span class="tableName">' + 
										_tableName + '</span>' +
										'<span class="tableStatus">' + _tableStatus + '</span>' +
										'<span class="tableGuests">' + _tableGuests + ' Guests</span>' +
										'</div>');

	}
}


function setActiveTable(id, name){
	selectedTableId = id
	
	$('#restaurantTables').hide();
	$('#activeTableName').show().html(name);
	
	
}


function closeControlPanel(cmd){
	if ( cmd == 'save' ){
		// first check for existing open orders
		var newOrder = true; // default value
		for ( i=0;i<=order.length;i++){
			if ( typeof(order[i]) != "undefined" && order[i].table == selectedTableId ){
				alert ('Existing Order.\nTable number has been changed to: ' + order[i].table);
				newOrder = false;
				active = i;
			}
		}
		// not an existing order, create new one
		if ( newOrder ){
			active = order.length;
			order[active] = {number:'', table:selectedTableId, time:'',price:'', items:[]};   
			alert ('New Order.\nTable number has been set to: ' + name);
		}

		updateOrder();	
	}

	location.href = "#home";
	$('#controlPanel').hide();
	$('#adminLogin').show();
	

}



