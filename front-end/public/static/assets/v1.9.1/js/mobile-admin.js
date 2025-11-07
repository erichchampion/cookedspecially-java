/* Script name: mobile-admin.js
 * Description: Javascript code for the admin control panel
 * List of functions:
 * - keyDown()
 * - checkAccessCode()
 * - initControlPanel()
 * - showRestaurantTables()
 * - getRestaurantTables()
 * - preSelectActiveTable()
 * - getCheckDetails()
 * - closeControlPanel()
 * - setActiveTable()
 * - viewMode()
 */


// variables
var accessCode       = '',    // admin panel access code
    tableId          = '',    // used in ajax calls
    selectedTableId  = '',    // Selected table id from DB
    selectedTable    = '',    // index for restaurantTables.tables[]
    restaurantTables = {},    // tables object
    activeCheck      = {},    // active check object
    newCheck         = {};    // temporary object in control panel


// APIs
var getTables  = 'http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/getRestaurantTables.json?',
	setTable   = 'http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/setStatus.json?',
	getCheck   = 'http://www.bakedspecially.com:8080/CookedSpecially/order/getCheck.json?',
	addToOrder = 'http://www.bakedspecially.com:8080/CookedSpecially/order/addToCheck.json';


// temporary hack
var serverName = location.hostname + ":" + location.port;
if ( serverName.indexOf("bakedspecially.com:8080") == -1 ){
	getTables  = '/api/getTables.php?',  // Get the list of tables
	setTable   = '/api/setTable.php?',   // Used by the Save button in the admin panel
	getCheck   = '/api/getCheck.php?',   // Gets the table's check data
	addToOrder = '/api/addToOrder.php?'; // Used to add order to a check
}



window.console & console.log("in mobile-admin.js");


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
    if ( accessCode == "1984" || accessCode == ""){
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
    //getRestaurantTables();
    $('#tableCheckDetails').html('');
    
    if ( restaurantTables.length > 0 ){
        $('#activeTableName').show().html( restaurantTables.tables[selectedTable].name );
	}

    if ( typeof(activeCheck.id) != "undefined" ){
    	$('#tableCheckDetails').append('<tt>' +
          'Table#: '  + activeCheck.tableId + '</br>' +
          'Check#: '  + activeCheck.id      + '</br>' +
          'Status:  ' + activeCheck.status  + '</br>');
    }
}


function showRestaurantTables(){
    
    $('#controlPanelLoading').show();
    $('#activeTableName').hide();
    $('#activeTableInfo').hide();
    $('#viewModeBtn').hide();
    //$('#restaurantTables').html('').css('display','inline-table');
    $('#restaurantTables').html('').show();

    $('#restaurantTables').append( '<div class="tableInfoHeader"><span class="tableName"> Name</span>' +
                                    '<span class="tableStatus">Status</span>' +
                                    '<span class="tableGuests">Capacity</span>' +
                                    '</div>');
                                    
                                    
                                    
                                        
    getRestaurantTables();

}


function getRestaurantTables(){
    window.console & console.log("in getRestaurantTables()"); 

    var url = getTables + "restaurantId=1";
    
    // generate ramdom String to prevent caching
    /* NOT NEEDED
    var ramdomStr = "";
    var letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for( var i=0; i < 3; i++ ){
        ramdomStr += letters.charAt(Math.floor(Math.random() * letters.length));
    }
    var url = "/api/getTables.php?restaurantId=1&rs=" + ramdomStr;
    console.log("getRestaurantTables(): " + url );
    */
        
    $.ajax({
      url: url,
      dataType: 'json'
    })
    .done (function (data) {
        restaurantTables = data;
        for ( i=0; i < restaurantTables.tables.length; i++){
            $('#restaurantTables').append( 
            '<div class="tableInfo '     + restaurantTables.tables[i].status + '" onclick="preSelectActiveTable(\''+ i +'\')">'+ 
            '<span class="tableName">'   + restaurantTables.tables[i].name   + '</span>' +
            '<span class="tableStatus">' + restaurantTables.tables[i].status + '</span>' +
            '<span class="tableGuests">' + restaurantTables.tables[i].seats  + ' Guests</span>' +
            '</div>');
        }
        $('#controlPanelLoading').fadeOut();

    })
    .fail(function() { 
        console.log("AJAX: getRestaurantTables() FAILED"); 
    })
    .always(function() { 
        console.log("AJAX: getRestaurantTables() COMPLETED"); 

    });

}


function preSelectActiveTable(i){
    preSelectedTable   = i;
    preSelectedTableId = restaurantTables.tables[i].id ;
    
    $('#restaurantTables').hide();
    $('#activeTableName').show().html( restaurantTables.tables[i].name );
    $('.activeTableDescription').show().html( restaurantTables.tables[i].description );
    $('#activeTableInfo').show();
    $('#viewModeBtn').show();

    // show check details
    getCheckDetails();
}

function selectActiveTableNOTUSED(i){
    selectedTable   = i;
    selectedTableId = restaurantTables.tables[i].id ;
    
    $('#restaurantTables').hide();
    $('#activeTableName').show().html( restaurantTables.tables[i].name );
    $('.activeTableDescription').show().html( restaurantTables.tables[i].description );
    $('#activeTableInfo').show();
    $('#viewModeBtn').show();

    // show check details
    getCheckDetails();
}


function getCheckDetails(){

        var url = getCheck + "restaurantId=1&tableId=" + preSelectedTableId;
        
        $('#tableCheckDetails').html('');
        $.ajax({
          url: url,
          dataType: 'json'
        })
        .done (function (data) {
            newCheck = data;
            $('#tableCheckDetails').append('<tt>' +
                'Table#: '  + data.tableId + '</br>' +
                'Check#: '  + data.id      + '</br>' +
                'Status:  ' + data.status  + '</br>');
            
            if (data.items){
                $('#tableCheckDetails').append('<tt>Ordered Items:  ' + data.items.length + '</tt>'); 
            }
            $('#tableCheckDetails').append('</tt>');
            
            newCheck = data;
        
        })
        .fail(function() { 
            console.log("AJAX: getCheck() FAILED"); 
        })
        .always(function() { 
            console.log("AJAX: getCheck() COMPLETED"); 

        });

}


function closeControlPanel(cmd){
    
    if ( cmd == 'save' ){
		console.log("closeControlPanel(): save"); 
		
		selectedTable = preSelectedTable;
    	selectedTableId = preSelectedTableId;

        // set new active check if changed    
        if ( activeCheck != newCheck ){
			console.log("closeControlPanel(): newCheck"); 

            itemsList = '';
            
            // set active check
            activeCheck = newCheck;
            
            // reset temp object
            newCheck    = {};
        
            // reset pending order
            pendingOrder = {number:'', table:selectedTableId, time:'',price:'', items:[]};   
            
            //alert ('\nDevice configured for table: \n\n' + restaurantTables.tables[selectedTable].name );
            
            
            // Show myCheck button
            if ( activeCheck.items && activeCheck.items.length > 0 ){
            	$('.view-check-button').show();
            	
            	itemsList = '<p><b>Ordered Items:</b>';
            	
            	for (i=0;i<activeCheck.items.length;i++){
            		console.log(activeCheck.items[i].item);
            		itemsList += '<div class="left">' +activeCheck.items[i].name  + '</div>' ;
            		itemsList += '<div class="right">' +activeCheck.items[i].price  + '</div>' ;
            		itemsList += '<br clear="both"/>';
            	}
            	
            	
            }else{
            	$('.view-check-button').hide();
            }

                
                

            $('#check_content').html(
				'<p class="center">' +
                '<b>Axis Cafe and Restaurant</b>' +
                '<br/>' +
                '123 Some Street - Any City' +
                '<br/>' +
                'Menu Application Check Viewer' +
                '</p>' +            
            	'<p></p>' +
            	'<p>' +
            	'Check: ' + activeCheck.id + '<br/>' +
            	'Table: ' + activeCheck.tableId + '<br/>' +
            	'Guests: ' + activeCheck.guests + '<br/><br/>' +
            	'</p>' +


            	itemsList +
            	
            	'<div class="left"><br/><br/><b>Total:</b></div>' +
            	'<div class="right"><br/><br/><b>' + activeCheck.amount  + '</b></div>' + 
            	
            	'<br clear="both"/>' + 
            	'<div id="check_footer"><p class="center">THANK YOU</p>' +
            	'<p class="center">Axis Cafe and Restaurant<br/>' +

                '123 Some Street - Any City</p></div>'
            );

            	            
            
            // update UI elements
            $('.addToOrder').show();
            updateOrder();
            
            // set active table in database
            setActiveTable();
        
        }



	// this should be moved this to it's own function
    }else if (cmd == 'cancel'){
		console.log("closeControlPanel(): cancel"); 
		if (selectedTable == ""){
			$('#activeTableName').show().html(  "Select..." );
		    $('.activeTableDescription').show().html("");
		}else{
			$('#activeTableName').show().html(  restaurantTables.tables[selectedTable].name  );
			$('.activeTableDescription').show().html( restaurantTables.tables[selectedTable].description );
    	}


   
	// this should be moved this to it's own function
    }else if (cmd == 'view'){
		console.log("closeControlPanel(): view"); 
        alert ('Device set to "View Mode"');
        active = '';
        $('.order-button').hide();
        $('.order').hide();
        $('.activeTableDescription').html('');
    }

    location.href = "#home";
    $('#controlPanel').hide();
    $('#adminLogin').show();
}



function setActiveTable(){
    console.log("setActiveTable(): "); 
    
    var url = setTable + "restaurantId=1&status=Busy&tableId=" + restaurantTables.tables[selectedTable].id ;
    console.log(url); 

	$.ajax({
    	url: url,
    	dataType: 'json'
    })
    .done (function (data) {
        console.log("AJAX: setActiveTable() SUCCESS"); 
    })
    .fail(function() { 
        console.log("AJAX: setActiveTable() FAILED"); 
    })
    .always(function() { 
        console.log("AJAX: setActiveTable() COMPLETED"); 
    });    
}


function viewMode(){
    selectedTableId = '';
    $('#activeTableName').show().html("Select...");
    $('.addToOrder').hide();
    closeControlPanel('view');
}

