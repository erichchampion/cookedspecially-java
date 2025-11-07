// APIs
// Avoid `console` errors in browsers that lack a console.
// http://stackoverflow.com/questions/7585351/testing-for-console-log-statements-in-ie
(function() {
    var method;
    var noop = function () {};
    var methods = [
        'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
        'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
        'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
        'timeStamp', 'trace', 'warn'
    ];
    var length = methods.length;
    var console = (window.console = window.console || {});

    while (length--) {
        method = methods[length];

        // Only stub undefined methods.
        if (!console[method]) {
            console[method] = noop;
        }
    }
}());

/**********
 * CONSTANTS
 */
//var restaurantId = 21;
var debug = true;
var appVersion = '0.1';
var appLatestVersion = '0.1';
var comCookedSpeciallyImagePattern = /\/static\/\d+\//;
var comCookedSpeciallyImagePrefix = "http://www.bakedspecially.com";
var comCookedSpeciallyApiPrefix = "/CookedSpecially";
//var comCookedSpeciallyApiPrefix = "http://localhost:8080/CookedSpecially";
/*var _getTables  = '/CookedSpecially/seatingTable/getRestaurantTables.json?', 
_setTable   = '/CookedSpecially/seatingTable/setStatus?', 
_custInfo   = '/CookedSpecially/customer/getCustomerInfo.json?', 
_getCheck   = '/CookedSpecially/order/getCheckWithOrders.json?', 
_addToCheck = '/CookedSpecially/order/addToCheck.json?', 
_emailCheck = '/CookedSpecially/order/emailCheck?';	*/

var _getTables  = comCookedSpeciallyApiPrefix + '/seatingTable/getRestaurantTables.json?', 
_setTable   = comCookedSpeciallyApiPrefix + '/seatingTable/setStatus?', 
_custInfo   = comCookedSpeciallyApiPrefix + '/customer/getCustomerInfo.json?', 
_getCheck   = comCookedSpeciallyApiPrefix + '/order/getCheckWithOrders.json?', 
_addToCheck = comCookedSpeciallyApiPrefix + '/order/addToCheck.json?', 
_emailCheck = comCookedSpeciallyApiPrefix + '/order/emailCheck?';

var restaurantApi = comCookedSpeciallyApiPrefix + "/restaurant/getrestaurantinfo?restaurantId=" + restaurantId;
var menuApi = comCookedSpeciallyApiPrefix + "/menu/getallmenusjson/" + restaurantId;
var checkApi = comCookedSpeciallyApiPrefix + "/order/getCheckWithOrders.json?tableId=1&restaurantId=" + restaurantId;
var tableApi = comCookedSpeciallyApiPrefix + "/seatingTable/getRestaurantTables.json?restaurantId=" + restaurantId;
var allChecksApi = comCookedSpeciallyApiPrefix + "/order/allChecksWithOpenOrders.json?restaurantId=" + restaurantId;
var db;
var strConnectionAlert = "You appear to be online, but we have a connection problem. Please click Ok to try again.";

var changeCount = 0;
var orderCount = 0;

/* *********
 * GLOBALS
 */
var money=0;
var restaurantDataString = "";
var restaurantData = {};
var menuDataString = "";
var menuData = {};
var checkDataString = "";
var checkData = [];
var tableDataString = "";
var tableData = {};
var allChecksDataString = "";
var allChecksData = {};
var startTime;
var endTime;
var loadingTime;
var currency = '&#x20B9;'; // Indian rupee (&#x20B9;)
var tabelIdConfig;
var nameDelivery="";
var nameCustomerInfo="";
var nameInstruction="";
var setStatus="";
var paymentStatusText="";
var slashMark ="";
/***********
 * BEGIN debugLog
*/
function debugLog(msg){

	if (debug) {
		console.log(msg);
	}
}
/* 
 * END debugLog
 **********/

/* **********
 * 1. BEGIN initHomePage
 * Iterates through the data to add orders to the home page
   Function Calls 
    - addOrderToHomePage() for each order
 */
function initHomePage(kitchenScreenId) {
	console.log("DEBUG > BEGIN initHomePage");	
	console.log("About to iterate through the data");
	var orderIndex = 0;
	var allOrders = [];

	$("#topMenu").empty();
	
	// iterate through data file and create orders in homepage
	$.each(allChecksData, function() {
		var currentCheck = this;
		if(kitchenScreenId !=null & kitchenScreenId!=0 & kitchenScreenId!=undefined) {
		if(kitchenScreenId == currentCheck.kitchenScreenId){
		var tableName = currentCheck.checkType;
		var tableId = currentCheck.tableId;
		tabelIdConfig=tableId;
		nameInstruction="Instructions :";
		if(tabelIdConfig>0){
		 	var deliveryTime="";
			var deliveryAddress ="";
			var name ="";
			var phone ="";
			var	paymnetStatus="";
			paymentStatusText="";
			nameCustomerInfo="";
			nameDelivery="";
			slashMark="";
		}else{
		var deliveryTime=currentCheck.deliveryTime;
		var deliveryAddress = currentCheck.deliveryAddress;
		var name = currentCheck.name;
		var phone = currentCheck.phone;
		var	paymnetStatus=currentCheck.status;
		nameDelivery="Delivery Time :";
		nameCustomerInfo="Customer Information :";
		paymentStatusText="Payment Status :";
		slashMark =' / ';
		}
		var tableStatus = "Busy";
		var customerId = currentCheck.customerId;
		var guests = "1";
	
		if(tableName!=null){
		if (tableName.toLowerCase() == "delivery" && currentCheck.deliveryArea != null) {
			tableName = tableName + ": " + currentCheck.deliveryArea;
		}
		}

		// look up the table info for the order
		$.each(tableData.tables, function() {
			if (currentCheck.tableId == this.id) {
				var currentTable = this;
				tableName = currentTable.name;
				tableId = currentTable.id;
				tableStatus = currentTable.status;
				guests = currentTable.guests;
				
			}
		});
		
		// iterate through data file and create orders in homepage
		$.each(currentCheck.orders, function() {
			var currentOrder = this;
			currentOrder.tableName = tableName;
			currentOrder.tableId = tableId;
			currentOrder.tableStatus = tableStatus;
			currentOrder.guests = guests;
			currentOrder.customerId = currentCheck.customerId;
    		currentOrder.deliveryTime=deliveryTime;
    		currentOrder.name=name;
    		currentOrder.phone=phone;
    		currentOrder.paymnetStatus=paymnetStatus;
    		currentOrder.deliveryAddress=deliveryAddress;
			allOrders.push(currentOrder);
		});
		}
		}
		else{
			var tableName = currentCheck.checkType;
			var tableId = currentCheck.tableId;
			tabelIdConfig=tableId;
			nameInstruction="Instructions :";
			if(tabelIdConfig>0){
			 	var deliveryTime="";
				var deliveryAddress ="";
				var name ="";
				var phone ="";
				var	paymnetStatus="";
				paymentStatusText="";
				nameCustomerInfo="";
				nameDelivery="";
				slashMark="";
			}else{
			var deliveryTime=currentCheck.deliveryTime;
			var deliveryAddress = currentCheck.deliveryAddress;
			var name = currentCheck.name;
			var phone = currentCheck.phone;
			var	paymnetStatus=currentCheck.status;
			
			nameDelivery="Delivery Time :";
			nameCustomerInfo="Customer Information :";
			paymentStatusText="Payment Status :";
			slashMark =' / ';
			}
			var tableStatus = "Busy";
			var customerId = currentCheck.customerId;
			var guests = "1";
			if(tableName!=null){
			if (tableName.toLowerCase() == "delivery" && currentCheck.deliveryArea != null) {
				tableName = tableName + ": " + currentCheck.deliveryArea;
			}
			}
			// look up the table info for the order
			$.each(tableData.tables, function() {
				if (currentCheck.tableId == this.id) {
					var currentTable = this;
					tableName = currentTable.name;
					tableId = currentTable.id;
					tableStatus = currentTable.status;
					guests = currentTable.guests;
					
				}
			});
			
			// iterate through data file and create orders in homepage
			$.each(currentCheck.orders, function() {
				var currentOrder = this;
				currentOrder.tableName = tableName;
				currentOrder.tableId = tableId;
				currentOrder.tableStatus = tableStatus;
				currentOrder.guests = guests;
				currentOrder.customerId = currentCheck.customerId;
	    		currentOrder.deliveryTime=deliveryTime;
	    		currentOrder.name=name;
	    		currentOrder.phone=phone;
	    		currentOrder.paymnetStatus=paymnetStatus;
	    		currentOrder.deliveryAddress=deliveryAddress;
				allOrders.push(currentOrder);
			});
		}
	});
	function sortAscending(a,b){
	    return (new Date(a.createdTime) - new Date(b.createdTime));
	}
	allOrders.sort(sortAscending);
	
	// iterate through data file and create orders in homepage
	$.each(allOrders, function() {
		var currentOrder = this;
		if (currentOrder.status.toLowerCase() != "ready" && currentOrder.status.toLowerCase() != "ready") {
			var myOrderArray = [];
			myOrderArray.push(this);
			addOrdersToHomePage("#table" + currentOrder.tableId, currentOrder.tableName, myOrderArray,currentOrder.deliveryTime,currentOrder.name,currentOrder.deliveryAddress,currentOrder.phone,currentOrder.paymnetStatus);
		}
	});
	// iterate through data file and create orders in homepage
	$.each(allOrders, function() {
		if (this.status.toLowerCase() == "ready") {
			var tableName = this.tableName;
			var tableId = this.tableId;
			var tableStatus = this.tableStatus;
			var guests = this.guests;
			var deliveryTime=this.deliveryTime;
			var name = this.name;
			var phone = this.phone;
			var deliveryAddress=this.deliveryAddress;
			var paymnetStatus=this.paymnetStatus;
			
			$("#topMenu #table" + tableId).remove();
			var myOrderArray = [];
			myOrderArray.push(this);
			addOrdersToHomePage("#table" + tableId, tableName, myOrderArray,deliveryTime,name,deliveryAddress,phone,paymnetStatus);
		}
	});

	$("#app-loader").css({ "display": "none" });
	$("#allPages").css({ "visibility": "visible" });
	$("#home").on( "pagebeforeshow", function(event, ui) {
		// do something before navigating to a new page
	});
	$("#home").on( "pageshow", function(event, ui) {
		// do something when the home screen is shown
	});
	$.mobile.changePage("#menus");
	console.log("orderCount: " + orderIndex);
		
	console.log("DEBUG > END initHomePage");	
}
/* 
 * END initHomePage
 **********/

 
 

/* 
 *******************************************************
                     UI FUNCTIONS
 ******************************************************* 
 */
 
/* **********
 * 2. BEGIN addOrdersToHomePage
 * Creates the home page with all the orders
    Parameters: 
 */
function addOrdersToHomePage(tableId, tableName, data,deliveryTime,name,address,phone,paymnetStatus) {
	console.info("DEBUG > BEGIN addOrdersToHomePage: " + tableId); 
	
	var MscreenId = getQueryVariable("MscreenId");
	$.each(data, function() {
		var orderId = this.orderId;
		var checkId = this.checkId;
		var restaurant  = this.restaurantId;
		var htmlOrderId = "#order" + orderId;
		var orderDate = new Date(this.createdTime);
		var orderTime = orderDate.toLocaleDateString() + " @ " + orderDate.toLocaleTimeString();
		var orderStatus = this.status.toLowerCase();
		var customerId = this.customerId;
		var paymentMode=this.paymentStatus;
		var microKitchenId=this.microKitchenId;
		var statusComplete;
		var time = new Date(deliveryTime);
		//var curretTime =time.getDate()+'-'+(time.getMonth()+1)+'-'+time.getFullYear()+'  @ '+time.getHours()+':'+time.getMinutes()+':'+time.getSeconds();
		var curretTime =time.toLocaleString();
		var orderTimes =curretTime;
		if(tabelIdConfig>0){
		orderTimes ="";
		paymentMode="";
		paymnetStatus="";
		statusComplete ="confirmdelivery";
		}
		if(MscreenId==microKitchenId){
		if(orderStatus == "new" || orderStatus == "pending" || orderStatus==statusComplete ){
		var theOrder = "" + 
			'<ul id="' + tableId + '" data-role="listview" data-inset="true" class="ui-listview ui-listview-inset ui-shadow">' +
				'<li id="order' + orderId + '" class="ui-li ui-li-static ui-btn-up-c">' +
				'<div style="float:right; width: 30%">'+	
				'<p id="links">' +
					'This Order: <a href="javascript:void(0);" onclick="$(\'#order' + orderId + '\').print();return(false);">Print</a>' + 
					'</p>' +
					'</div>'+
					'<div style="float:right; width: 25%;"> ' +
					'<h3 class="ui-li-heading">' +
					'	' + tableName +
					'</h3>' +
					'<p id="order' + orderId + '-info" >' +
					'<strong>' + orderId + '</strong> (<span id="orderStatus">' + orderStatus + '</span>) Ordered on ' + orderTime + '<br>'+
					'<strong>'+ nameCustomerInfo +'</strong>'+'<br>'+
					''+name+'<br>'+
					''+address+'<br>'+
					''+phone+'<br>'
					'</p>' +
					'</div>'+
					'<div  style="float:left; width: 25%" ">'+
					'</div>'+
				'</li>'+
			'</ul>';
		}
		$(theOrder).appendTo("#topMenu");
		if (orderStatus == "new") {
			$("#order" + orderId).css("background", "linear-gradient(to right,indianred , indianred 80%, indianred)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, indianred, indianred 80%, indianred)");
			var theLink = '' +
				'<a href="javascript:void(0);" onclick="setOrderStatusPending(' + orderId + ');return(false);">Claim</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}
		if (orderStatus == "pending") {
			$("#order" + orderId).css("background", "linear-gradient(to right, yellow, white 20%, yellow)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, yellow, white 20%, yellow)");
			var theLink = '' +
				'<a href="javascript:void(0);" onclick="setOrderStatusReady(' + orderId + ');return(false);">Mark as Ready for Delivery</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}
		if (orderStatus == "ready") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightred, lightred 80%, white)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightred, lightred 80%, white)");
		}
		if(tabelIdConfig>0){
			if (orderStatus == "confirmdelivery") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightgreen, lightgreen 80%, lightgreen)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightgreen, lightgreen 80%, lightgreen)");
			var theLink = '' +
				'<a href="javascript:void(0);"  onclick="setOrderStatusComplete(' + orderId + ');return(false);">Complete</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
			}
		}
		/*if (orderStatus == "delivered") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightblue, lightblue 80%, white)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightblue, lightblue 80%, white)");
		}*/
		if (orderStatus == "cancelled") {
			$("#order" + orderId).css("background", "linear-gradient(to bottom, darkgray, lightgray 80%, white)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(top, darkgray, lightgray 80%, white)");
			var theLink = '' +
				'<a href="javascript:void(0);" onclick="setOrderStatusPending(' + orderId + ');return(false);">Reactivate</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}
		var theLink = '' +
			'<br/>This Check: <a href="javascript:void(0);" onclick="$(\'#order' + orderId + ' #check' + checkId + '\').print();return(false);">Print</a>'+'<br>'+'<I>'+nameDelivery+'</I> <strong><font size="4">' + orderTimes +'</font></strong><br><I>'+paymentStatusText+'</I><Strong><font size="3">'+paymnetStatus+slashMark+paymentMode+'</font></strong> <br>';
		$(theLink).appendTo(htmlOrderId + " p#links");
		$.each(this.orderDishes, function() {
			
			var name = this.name;
			var quantity = this.quantity;
			var price = this.price;
			
			var  instructions;
			
			if(this.instructions==null){
				instructions="";
			}
			else{
				instructions=this.instructions;
			}
			var id=this.orderDishId;
			setStatus="Ready";
			
			
			if(tabelIdConfig>0){
			setStatus="confirmdelivery";
			}
			/* theItem = '' +
				'<p id="orders'+this.orderDishId+'">' +
				'	<strong>' + name + '</strong><font size=4> ' + quantity + '</font>  @ ' + currency + price +
				'</p>';*/
			var theItem = '' +
			'<p  id="orders'+this.orderDishId+'" style="white-space:pre-wrap; width:95ex">' +
			'<strong>' + name + '</strong><font size=4> ' + quantity + '</font>  @ ' + currency + price + '<br><I> '+nameInstruction+' </I><B>' + instructions +'</B>'+
			'</p>';
			 $(theItem).appendTo(htmlOrderId);
			if(this.orderAddOn!=""||this.orderAddOn!=null){
			 $.each(this.orderAddOn, function() {
				var names=this.name;
				var prices =this.price;
				var dishIds=this.dishId;
				var theItems = '' +
				  '<p>' + 
					'	<strong> Add-On :' + names + '</strong>@ ' + currency + prices +
					'</p>';
				$(theItems).appendTo("#orders"+id);
			 });
			}
		});
		var generateCheckApi ;
		//http://www.cookedspecially.com:8080/CookedSpecially/order/generateCheckForPrint?templateName=saladdaysbill&checkId=193
		if(restaurant==21){
		generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=saladdaysbill&checkId=" + checkId;
		}
		else if(restaurant==30){
			generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=gombeibill&checkId=" + checkId;
		}
		else{
			generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=defaultbill&checkId=" + checkId;

		}
		var getCheck = $.ajax(generateCheckApi, {
			isLocal: false
		})
		getCheck.then(function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + generateCheckApi);
				console.log(JSON.stringify(data));
			}
			var theCheck = '' +
				'<div class="checkWrapper" style="display:none"><div id="check' + checkId + '">' +
				'</div></div>';
			$(theCheck).appendTo(htmlOrderId);
			$(data).appendTo(htmlOrderId + " div#check" + checkId);
		},function() { 
			console.log("ajax error: " + generateCheckApi); 
		});
		getCheck.complete(function() { 
			console.log("ajax complete: " + generateCheckApi); 
		});
		}
		});
	console.log("DEBUG > END addOrdersToHomePage");	
}
/* 
 * END addOrderToHomePage
 **********/





 
 /* 
 *******************************************************
                     DATA FUNCTIONS
 ******************************************************* 
 */

function setOrderStatusPending(orderId) {
	setOrderStatus(orderId,money,"Pending");
}
function setOrderStatusReady(orderId) {
	setOrderStatus(orderId,money,setStatus);
}
function setOrderStatusCancelled(orderId) {
	if (confirm("Are you sure?")) {
		setOrderStatus(orderId,money,"Cancelled");
	}
}
function setOrderStatusComplete(orderId) {
	setOrderStatus(orderId,money,"delivered");
}

/***********
 * BEGIN setOrderStatus
 */




function setOrderStatus(orderId,money,status) {
console.log("DEBUG > BEGIN setOrderStatus");	
//status=<NEW|Pending|Ready|Delivered>
var statusApi = comCookedSpeciallyApiPrefix + "/order/setOrderStatus?orderId=" + orderId + "&status=" + status+"&money=" +money;;

	var setOrderStatus = $.ajax(statusApi, {
		isLocal: false
	})
	setOrderStatus.then(function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + statusApi);
			console.log(JSON.stringify(data));
		}
		var param1var = getQueryVariable("KscreenId");
		updateAjaxDataRemote(param1var);
	},function() { 
		console.log("ajax error: " + statusApi); 
	});
	setOrderStatus.complete(function() { 
		console.log("ajax complete: " + statusApi); 
	});
}
/* 
 * END setOrderStatus
 **********/
/***********
 * BEGIN updateAjaxDataRemote
 */
 var nativCounter;
function updateAjaxDataRemote(kitchenScreenId) {
//console.log("DEBUG > BEGIN updateAjaxDataRemote");	
    nativCounter=0;
	var updateAjaxDataRemote = $.ajax(tableApi, {
		isLocal: false
	})
	updateAjaxDataRemote.then(function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + tableApi);
			tableDataString = JSON.stringify(data);
			console.log(tableDataString);
		}

		tableData = JSON.parse(tableDataString);

		var allchecksApi = $.ajax(allChecksApi, {
			isLocal: false
		})
		allchecksApi.then(function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + allChecksApi);
				allChecksDataString = JSON.stringify(data);
				console.log(allChecksDataString);
			}
			allChecksData = JSON.parse(allChecksDataString);
			
			for (var key in allChecksData) {

				if (allChecksData.hasOwnProperty(key)) {
						nativCounter++;
					}	
			}
			initHomePage(kitchenScreenId);
		},function() { 
			console.log("ajax error: " + allChecksApi); 
			$.mobile.pageContainer.pagecontainer ("change", '#closed', {changeHash: true});
			$("#app-loader").css({ "display": "none" });
			$("#allPages").css({ "visibility": "visible" });
			$.mobile.loading( "hide" );
		});
		allchecksApi.complete(function() { 
			console.log("ajax complete: " + allChecksApi); 
		});
			
	},function() { 
		console.log("ajax error: " + tableApi); 
	});
	updateAjaxDataRemote.complete(function() { 
		console.log("ajax complete: " + tableApi); 
	});
	
//console.log("DEBUG > END updateAjaxDataRemote");	
}
/* 
 * END updateAjaxDataRemote
 **********/

/***********
 * BEGIN updateRestaurantInfo
 */
function updateRestaurantInfo() {
console.log("DEBUG > BEGIN updateRestaurantInfo");	

	var updateRestaurantInfo = $.ajax(restaurantApi, {
		isLocal: false
	})
	updateRestaurantInfo.then(function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + restaurantApi);
			restaurantDataString = JSON.stringify(data);
			console.log(restaurantDataString);
		}

		restaurantData = JSON.parse(restaurantDataString);
		
		if (restaurantData.currency == "INR") {
			currency = '₹'; 
		}
		if (restaurantData.currency == "USD") {
			currency = '$';
		}
		if (restaurantData.country == "GBP") {
			currency = '£'; 
		}
		if (restaurantData.country == "EUR") {
			currency = '€'; 
		}
		if (restaurantData.country == "JPY") {
			restaurantData.country = "日本";
			currency = '¥'; 
		}

		$("#menus .footer #restaurantName").html(restaurantData.businessName);
	},function() { 
		console.log("ajax error: " + restaurantApi); 
	});
	updateRestaurantInfo.complete(function() { 
		console.log("ajax complete: " + restaurantApi); 
	});
	
console.log("DEBUG > END updateRestaurantInfo");	
}

function getQueryVariable(variable) {
	  var query = window.location.search.substring(1);
	  var vars = query.split("&");
	  for (var i=0;i<vars.length;i++) {
	    var pair = vars[i].split("=");
	    if (pair[0] == variable) {
	      return pair[1];
	    }
	  } 
	}
/* 
 * END updateRestaurantInfo
 **********/

 /* 
 *******************************************************
                     MAIN PROGRAM
 ******************************************************* 
 */

try {
	var startTime = new Date().getTime();
	
	//http://stackoverflow.com/questions/13986182/how-can-i-improve-the-page-transitions-for-my-jquery-mobile-app/13986390#13986390
	$(document).one("mobileinit", function () {
		$.mobile.ajaxEnabled = true;
		$.mobile.allowCrossDomainPages = true;
		$.mobile.autoInitializePage = true;
		$.mobile.defaultPageTransition = "none";
		$.mobile.loader.prototype.options.text = "loading";
		$.mobile.loader.prototype.options.textVisible = true;
		$.mobile.mobile.touchOverflowEnabled = true;
		$.mobile.pageContainer = $("#allPages");
		$.mobile.phonegapNavigationEnabled = true;
	});
	

	$(document).ready(function(){
		// When the document is ready, Cordova isn't
		var param1var = getQueryVariable("KscreenId");
		updateRestaurantInfo();
		updateAjaxDataRemote(param1var);
	});
} catch (error) {
	console.error("Your javascript has an error: " + error);
}
	 /*$(document).ready(
       function() {
                setInterval(function() {
		$.ajax(allChecksApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + allChecksApi);
				allChecksDataString = JSON.stringify(data);
				console.log(allChecksDataString);
			}
			allChecksData = JSON.parse(allChecksDataString); // code for checking number of customer updated in database 
			for (var key in allChecksData) {
				if (allChecksData.hasOwnProperty(key)) {
						
					}		
			}
		})
		.fail(function() { 
			console.log("ajax error: " + allChecksApi); 
			$.mobile.pageContainer.pagecontainer ("change", '#closed', {changeHash: true});
			$("#app-loader").css({ "display": "none" });
			$("#allPages").css({ "visibility": "visible" });
			$.mobile.loading( "hide" );
		})
		.always(function() { 
			console.log("ajax complete: " + allChecksApi); 
		});	
			   },30000);
			   })*/

/**********
 * BEGIN showAppVersion
 */
function showAppVersion() {
    var appInfo = 'About Axis Tablet App\n\n' +
                  'Restaurant: Axis Cafe' + '\n' +
                  'AppVersion: Beta ' + appVersion + '\n\n' +
                  'Loading Time: ' + loadingTime + ' miliseconds\n' +
                  ''; //'Latest Version: Beta ' + appLatestVersion;
    alert(appInfo);


}
/*
 * END showAppVersion
 ***********/
setTimeout(function(){$("div#update").html('<a href="javascript:location.reload();" style="color:red;" class="ui-link"><strong>Please Reload this Page to See If New Orders Have Arrived!</strong></a>');},60000);


 
 
 	
