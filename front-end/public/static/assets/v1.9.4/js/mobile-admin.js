/* Script name: mobile-admin.js
 * Description: Javascript code for the admin control panel
 * List of functions:
 * - keyDown()
 * - checkAccessCode()
 * - initControlPanel()
 * - showRestaurantTables()
 * - getRestaurantTables()
 * - selectActiveTable()
 * - getCheckPreview()
 * - closeControlPanel()
 * - setActiveTable()
 * - getCheck()
 * - updateCheck()
 * - showCheck()
 * - clearCheck()
 */

window.console & console.log("[] LOADING >>>  mobile-admin.js");

// variables
var accessCode       = '',    // admin panel access code
    tableId          = '',    // used only for the ajax calls
    selectedTable    = '',    // index for restaurantTables.tables[]
    tmpSelectedTable = '',    // index for restaurantTables.tables[] in control panel
    restaurantTables = {},    // tables object
    activeCheck      = {},    // active check object
    newCheck         = {},    // temporary object in control panel
    currency         = '&#x20B9;'; // Indian rupee (&#x20B9;)

// APIs
var _getTables  = 'http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/getRestaurantTables.json?',
    _setTable   = 'http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/setStatus?',
    _getCheck   = 'http://www.bakedspecially.com:8080/CookedSpecially/order/getCheckWithOrders.json?',
    _addToCheck = 'http://www.bakedspecially.com:8080/CookedSpecially/order/addToCheck.json?';

// temporary API hack
var serverName = location.hostname + ":" + location.port;
if ( serverName.indexOf("bakedspecially.com:8080") == -1 ){
    _getTables  = '/api/getTables.php?',  // Get the list of tables
    _setTable   = '/api/setTable.php?',   // Used by the Save button in the admin panel
    _getCheck   = '/api/getCheck.php?',   // Gets the table's check data
    _addToCheck = '/api/addToCheck.php?';   // Used to add order to a check
}


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
        alert( 'Failed' );
    }
    accessCode = '';
    $('#accessCodeField').html(accessCode);

}


function initControlPanel(){
    $('#tableCheckDetails').html('');
    
    if ( restaurantTables.length > 0 ){
        $('#activeTableName').show().html( restaurantTables.tables[selectedTable].name );
		getCheckPreview(restaurantTables.tables[selectedTable].id);
    }
}


function showRestaurantTables(){
    
    $('#controlPanelLoading').show();
    $('#activeTableName').hide();
    $('#activeTableInfo').hide();
    $('#viewModeBtn').hide();
    $('#restaurantTables').html('').show();

    $('#restaurantTables').append( '<div class="tableInfoHeader"><span class="tableName"> Name</span>' +
                                    '<span class="tableStatus">Status</span>' +
                                    '<span class="tableGuests">Capacity</span>' +
                                    '</div>');
    getRestaurantTables();
}


function getRestaurantTables(){
    window.console & console.log("in getRestaurantTables()"); 

    var url = _getTables + "restaurantId=1";
    
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
            '<div class="tableInfo '     + restaurantTables.tables[i].status + '" onclick="selectActiveTable(\''+ i +'\')">'+ 
            '<span class="tableName">'   + restaurantTables.tables[i].name   + '</span>' +
            '<span class="tableStatus">' + restaurantTables.tables[i].status + '</span>' +
            '<span class="tableGuests">' + restaurantTables.tables[i].seats  + ' Guests</span>' +
            '</div>');
        }
        $('#controlPanelLoading').fadeOut();
		if (selectedTable != undefined && selectedTable.length > 0) {
			tmpSelectedTable = selectedTable;
			getCheckPreview(restaurantTables.tables[selectedTable].id);
		}

    })
    .fail(function() { 
        console.log("AJAX: getRestaurantTables() FAILED"); 
    })
    .always(function() { 
        console.log("AJAX: getRestaurantTables() COMPLETED"); 
    });

}


function selectActiveTable(i){
    // temporary variable
    tmpSelectedTable = i;
    
    $('#restaurantTables').hide();
    $('#activeTableName').show().html( restaurantTables.tables[i].name );
    $('.activeTableDescription').show().html( restaurantTables.tables[i].description );
    $('#activeTableInfo').show();
    $('#viewModeBtn').show();

    // get check data for preview
    getCheckPreview(restaurantTables.tables[i].id);
}


function getCheckPreview(id){
    var url = _getCheck + "restaurantId=1&tableId=" + id;
    $('#tableCheckDetails').html('');
    $.ajax({
      url: url,
      dataType: 'json'
    })
    .done (function (data) {
        newCheck = data;
        $('#tableCheckDetails').append('<tt>' +
            'Table#: '  + data.tableId + '</br>' +
            'Check#: '  + data.checkId      + '</br>' +
            'Amount:  ' + currency + data.bill  + '</br>' +
            'Status:  ' + data.status  + '</br>');
        if (data.items){
            $('#tableCheckDetails').append('<tt>Ordered Items:  ' + data.items.length + '</tt>'); 
        }
        $('#tableCheckDetails').append('</tt>');
        newCheck = data;
        console.log("newCheck.checkId " + newCheck.checkId); 
        console.log("newCheck.tableId " + newCheck.tableId); 
    })
    .fail(function() { 
        console.log("AJAX: getCheckPreview() FAILED"); 
    })
    .always(function() { 
        console.log("AJAX: getCheckPreview() COMPLETED"); 
    });
}


function closeControlPanel(cmd){
    
    if ( cmd == 'save' ){
        console.log("closeControlPanel(): save"); 
        
        // set selected table index
        selectedTable= tmpSelectedTable;
		
		console.log("activeCheck.checkId " + activeCheck.checkId); 
        // set new active check if changed    
        if ( newCheck != undefined && activeCheck.checkId != newCheck.checkId ){
            console.log("closeControlPanel(): newCheck"); 

            // set active check
            // Don't do this. JavaScript just sets the object pointers to the same data behind the scenes
            //activeCheck = newCheck;
            // Do this to really create a copy of the Object
			console.log("newCheck.checkId " + newCheck.checkId); 
			console.log("newCheck.tableId " + newCheck.tableId); 
            activeCheck = $.extend(true, {}, newCheck);
			console.log("activeCheck.checkId " + activeCheck.checkId); 
			console.log("activeCheck.tableId " + activeCheck.tableId); 

            // reset temp object
            newCheck = {};
        
            // reset pending order
            pendingOrder = {checkId:activeCheck.checkId, table:activeCheck.tableId, time:'',price:'', items:[]};   
          
            // user feedback //restaurantTables.tables[activeCheck.tableId-1].name
            //alert ('\nDevice configured for table: \n\n' + restaurantTables.tables[selectedTable].name );
            
            // create or clear check 
            if ( activeCheck.orders && activeCheck.orders.length > 0 ){
                updateCheck();    
            }else{
                clearCheck();
            }
            
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
            $('#activeTableName').show().html("Select...");
            $('.activeTableDescription').show().html("");
        }else{
            $('#activeTableName').show().html(restaurantTables.tables[selectedTable].name);
            $('.activeTableDescription').show().html(restaurantTables.tables[selectedTable].description);
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
    console.info("EXECUTING >>> setActiveTable()"); 
    
    var url = _setTable + "restaurantId=1&status=Busy&tableId=" + restaurantTables.tables[selectedTable].id ;
    console.info(url); 

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
    clearCheck();
    selectedTableId = '';
    $('#activeTableName').show().html("Select...");
    $('#tableCheckDetails').html('');
    $('.addToOrder').hide();
    closeControlPanel('view');
}


function getCheck(id){
    console.info('getCheck()' + id);
    var url = _getCheck + "restaurantId=1&tableId=" + id;
    $('#tableCheckDetails').html('');
    $.ajax({
      url: url,
      dataType: 'json'
    })
    .done (function (data) {
        activeCheck = data;
    })
    .fail(function() { 
        console.log("AJAX: getCheck() FAILED"); 
    })
    .always(function() { 
        console.log("AJAX: getCheck() COMPLETED"); 
    });
}


function updateCheck(){
    // show my check button
    $('.view-check-button').fadeIn();
    
    itemsList = '<p style="margin:8px 0 12px;border-top:1px solid #aaa; padding-top: 12px;"><b>Ordered Items</b></p>';
    
    for (o=0;o<activeCheck.orders.length;o++){
        console.log("Looping through orders: " + o);
        for (i=0;i<activeCheck.orders[o].orderDishes.length;i++){
            console.log(activeCheck.orders[o].orderDishes[i].name);
            //itemsList += '<div class="left">' +activeCheck.orders[o].orderDishes[i].name  + '</div>' ;
            //itemsList += '<div class="right">' + activeCheck.orders[o].orderDishes[i].quantity + " @ " + currency + activeCheck.orders[o].orderDishes[i].price  + '</div>' ;
            
            var computedPrice = activeCheck.orders[o].orderDishes[i].quantity * activeCheck.orders[o].orderDishes[i].price; 
            itemsList += '<div class="left">' + activeCheck.orders[o].orderDishes[i].quantity + " "+ activeCheck.orders[o].orderDishes[i].name  + '</div>' ;
            itemsList += '<div class="right">'  + currency + computedPrice  + '</div>' ;

            itemsList += '<br clear="both"/>';
        }
    }
    
    $('#check_content').html(
        '<p class="center" style="margin-bottom:18px;">' +
        '<b>Axis Cafe and Restaurant</b>' +
        '<br/>' +
        '123 Some Street - Any City' +
        '<br/>' +
        'Menu Application Check Viewer' +
        '</p>' +            
        '<div class="onethird center" style="display:inline-block;width:33%"> Check#: ' + activeCheck.checkId + '</div>' +
        '<div class="onethird left" style="display:inline-block;width:33%"> Table: ' + activeCheck.tableId + '</div>' +
        '<div class="onethird right" style="display:inline-block;width:33%"> Guests: ' + activeCheck.guests + '</div>' +

        itemsList +
        
        '<div class="left"><br/><br/><b>Total:</b></div>' +
        '<div class="right"><br/><br/>' + currency + '<b>' + activeCheck.bill  + '</b></div>' + 
        
        '<br clear="both"/>' + 
        '<div id="check_footer"><p class="center">THANK YOU</p>' +
        '<p class="center">Axis Cafe and Restaurant<br/>' +

        '123 Some Street - Any City</p></div>'
    );

}


function showCheck(){
    window.console && console.log('in showCheck() new...');
    $('#check_content').html('Loading...');
    updateCheck();
    $('#check').show();
}


function clearCheck(){
	//Don't reset the activeCheck object just because there're aren't any orders. 
	//The activeCheck.checkId and activeCheck.tableId values should be preserved.
    //activeCheck = {};
    $('.view-check-button').hide();
    $('#check_content').html('');
}



