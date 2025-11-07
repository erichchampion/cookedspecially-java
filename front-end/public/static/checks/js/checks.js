/**********
 * CONSTANTS
 */
var appVersion = '0.1';
var appLatestVersion = '0.1';
var comCookedSpeciallyImagePattern = /\/static\/\d+\//;
var comCookedSpeciallyImagePrefix = "http://www.bakedspecially.com";
var comCookedSpeciallyApiPrefix = "/CookedSpecially";
var restaurantApi = comCookedSpeciallyApiPrefix + "/restaurant/getrestaurantinfo?restaurantId=" + restaurantId;
var menuApi = comCookedSpeciallyApiPrefix + "/menu/getallmenusjson/" + restaurantId;
var checkApi = comCookedSpeciallyApiPrefix + "/order/getCheckWithOrders.json?tableId=1&restaurantId=" + restaurantId;
var tableApi = comCookedSpeciallyApiPrefix + "/seatingTable/getRestaurantTables.json?restaurantId=" + restaurantId;
var allChecksApi = comCookedSpeciallyApiPrefix + "/order/allOpenChecks.json?restaurantId=" + restaurantId;
var db;
var strConnectionAlert = "You appear to be online, but we have a connection problem. Please click Ok to try again.";

var changeCount = 0;
var orderCount = 0;

/* *********
 * GLOBALS
 */
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


/* **********
 * 1. BEGIN initHomePage
 * Iterates through the data to add orders to the home page
   Function Calls 
    - addCheckToHomePage() for each order
 */
function initHomePage() {
	console.log("DEBUG > BEGIN initHomePage");	
	console.log("About to iterate through the data");
	var orderIndex = 0;
		
	updateRestaurantInfo();
	
	// iterate through data file and create orders in homepage
	$.each(tableData.tables, function() {
		var tableName = this.name;
		var tableId = this.id;
		var tableStatus = this.status;
		var htmlTableId = "#table" + tableId;
		$("#topMenu div#table" + tableId).remove();
		var orderList = '' +
			'<div id="table' + tableId + '">' + 
			'<h3 class="ui-heading">' +
			'	' + tableName + ': <span id="tableStatus">' + tableStatus + '</span> ' + 
			'</h3>' +
			'<p id="tableLinks" class="ui-desc-small">Mark Table as: ' +
			'<a id="Available" href="javascript:void(0);" onclick="setTableStatusAvailable(\'' + tableId + '\',\'' + htmlTableId + '\',\'' + tableName + '\');return(false);"> Available </a>' + 
			'<a id="Busy" href="javascript:void(0);" onclick="setTableStatusBusy(\'' + tableId + '\',\'' + htmlTableId + '\',\'' + tableName + '\');return(false);"> Busy </a>' + 
			'<a id="Reserved" href="javascript:void(0);" onclick="setTableStatusReserved(\'' + tableId + '\',\'' + htmlTableId + '\',\'' + tableName + '\');return(false);"> Reserved </a>' + 
			'</p>'; 
			'<ul id="table' + tableId + '" data-role="listview" data-inset="true" class="ui-listview ui-listview-inset ui-shadow">' +
			'</ul>';
			'</div>';
		$(orderList).appendTo("#topMenu");
		if (tableStatus == "Available") {
			$(htmlTableId + " #tableLinks #Available").hide();
		}
		if (tableStatus == "Busy") {
			$(htmlTableId + " #tableLinks #Busy").hide();
		}
		if (tableStatus == "Reserved") {
			$(htmlTableId + " #tableLinks #Reserved").hide();
		}

		var checkApi = comCookedSpeciallyApiPrefix + "/order/getCheckWithOrders.json?tableId=" + tableId + "&restaurantId=" + restaurantId;
		$.ajax(checkApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + checkApi);
				checkDataString = JSON.stringify(data);
				console.log(checkDataString);
			}
			addCheckToHomePage(tableId, htmlTableId, tableName, data);
			checkData.push(JSON.parse(checkDataString));
			$("ul#table" + tableId + " span#tableStatus").html(tableStatus);
		})
		.fail(function() { 
			console.log("ajax error: " + checkApi); 
		})
		.always(function() { 
			console.log("ajax complete: " + checkApi); 
		});
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
 * 2. BEGIN addCheckToHomePage
 * Creates the home page with all the checks
    Parameters: 
 */
function addCheckToHomePage(tableId, htmlTableId, tableName, data) {
	console.info("DEBUG > BEGIN addCheckToHomePage: " + tableId); 
	
	var checkId = data.checkId;
	var invoiceId = data.invoiceId;
	var htmlCheckId = "#check" + checkId;
	var checkStatus = data.status;
	var guests = data.guests;
	var additionalChargesName1 = data.additionalChargesName1;
	var additionalChargesName2 = data.additionalChargesName2;
	var additionalChargesName3 = data.additionalChargesName3;
	//var total = data.bill + data.additionalChargesValue1 + data.additionalChargesValue2 + data.additionalChargesValue3;
	var obj = jQuery.parseJSON(data.taxJsonObject);
	var total = data.roundOffTotal;
	$(htmlTableId + " li#check" + checkId).remove();
	var theCheck = '' +
		'<li id="check' + checkId + '" class="ui-li ui-li-static ui-btn-up-c">' +
			'<p id="status" class="ui-li-desc">' +
			'	Check #' + checkId + ': ' + checkStatus +  
			//' | ' + guests + ' guest(s)' +
			'</p><br/>' +
		'</li>';
	$(theCheck).appendTo(htmlTableId);
	
	var theLink = '' +
		'<p id="links" class="ui-li-aside ui-li-desc">' +
		'</p>'; 
	$(theLink).prependTo(htmlCheckId);
	if (total != 0) {
		if (checkStatus != "Paid" && checkStatus != "PAID") {
			$("#check" + checkId).css("background", "linear-gradient(to right, white, indianred 80%, indianred)");
			$("#check" + checkId).css("background", "-webkit-linear-gradient(left, white, indianred 80%, indianred)");
			theLink = '' +
			'	<a href="javascript:void(0);" onclick="$(\'#check' + checkId + '\').print();return(false);">Print Check</a>' + 
			'	&nbsp;|&nbsp;Mark Check as: <a href="javascript:void(0);" onclick="setCheckStatusPaid(\'' + tableId + '\',\'' + htmlTableId + '\',\'' + tableName +'\','  + checkId + ');return(false);">Paid</a>';
			$(theLink).appendTo(htmlCheckId + " p#links");
		}
		if (checkStatus != "Unpaid" && checkStatus != "UNPAID") {
			$("#check" + checkId).css("background", "linear-gradient(to right, lightblue, lightblue 80%, white)");
			$("#check" + checkId).css("background", "-webkit-linear-gradient(left, lightblue, lightblue 80%, white)");
			theLink = '' +
			'	<a href="javascript:void(0);" onclick="$(\'#check' + checkId + '\').print();return(false);">Print Check</a>' + 
			'	&nbsp;|&nbsp;Mark Check as: <a href="javascript:void(0);" onclick="setCheckStatusUnpaid(\'' + tableId + '\',\'' + htmlTableId + '\',\'' + tableName +'\','  + checkId + ');return(false);">Unpaid</a>';
			$(theLink).appendTo(htmlCheckId + " p#links");
		}
	}
	var subTotal =0;
	$.each(data.orders, function() {
		$.each(this.orderDishes, function() {
			var name = this.name;
			var quantity = this.quantity;
			var price = this.price;
			var theItem = '' +
				'<p class="ui-li-desc">' +
				'	<strong>' + name + '</strong> ' + quantity + ' @ ' + currency + price + 
				'</p>';
			subTotal =subTotal +price*quantity;
			$(theItem).appendTo(htmlCheckId);
		});
	});
	var line='<hr>'+
	'<p class="ui-li-desc">' +
	'	<strong> <font size ="0.5" color="blue">Sub Total : </font></strong> <font size="2" color="black">'+currency+subTotal + '</font>'+
	'</p>';
	$(line).appendTo(htmlCheckId);
	var theAdditions = '<br/>';
	if(obj!=null){
	$.each(obj,function(key, value){
		var val=''+
		'<p id="additions" class="ui-li-desc"><font size="0.5" color="blue">' + key + ': </font> <font size="2" color="black">' + currency + value.toFixed(2); + '</font></p>';
		$(val).appendTo(htmlCheckId);
		val='';
	});
	
	}
	else{
	if (additionalChargesName1 && additionalChargesName1.length > 0) {
		theAdditions = theAdditions + '<p id="additions" class="ui-li-desc">' + additionalChargesName1 + ': ' + currency + data.additionalChargesValue1 + '</p>';
	}
	if (additionalChargesName2 && additionalChargesName2.length > 0) {
		theAdditions = theAdditions + '<p id="additions" class="ui-li-desc">' + additionalChargesName2 + ': ' + currency + data.additionalChargesValue2 + '</p>';
	}
	if (additionalChargesName3 && additionalChargesName3.length > 0) {
		theAdditions = theAdditions + '<p id="additions" class="ui-li-desc">' + additionalChargesName3 + ': ' + currency + data.additionalChargesValue3 + '</p>';
	}
	$(theAdditions).appendTo(htmlCheckId);
	}
/*
	var line='<hr>';
	$(line).appendTo(htmlCheckId);*/
	var theSummary = '<hr>' +
		'<p id="summary" class="ui-li-desc">' +
		'	<strong>Total: </strong> ' + currency + total + 
		'</p>'+
		'<font size="0.5" color="blue" >Invoice Id :</font><font size="1">'+invoiceId +'</font>';
	$(theSummary).appendTo(htmlCheckId);
	
	console.log("DEBUG > END addCheckToHomePage");	
}
/* 
 * END addCheckToHomePage
 **********/





 
 /* 
 *******************************************************
                     DATA FUNCTIONS
 ******************************************************* 
 */
var paymentCard =null;
var emcApprove =null;
var roomNo = null;
var paymenttype = null; 
var saveValue=null;
function setCheckStatusPaid(tableId, htmlTableId, tableName, checkId) {
	var paymenttype;
	$("#popupPaymentCard").popup('open');
	//$("#select-payment-card").parent().hide();
	//$("#roomNo").hide();
	//$("#emcApprove").hide();
	paymenttype =  $("#select-payment-type"+' option:selected').text();
	
	if(paymenttype=="CASH"){
		$("#select-payment-card").parent().hide();
		$("#roomNo").hide();
		$("#emcApprove").hide();
	}
	else if(paymenttype=="CREDIT" ){
		$("#select-payment-card").parent().show();
		$("#roomNo").hide();
		$("#emcApprove").hide();
		}
	else if(paymenttype=="NC"){
		$("#emcApprove").show();
		$("#roomNo").hide();
		$("#select-payment-card").parent().hide();
		
		}	
	else if(paymenttype=="ROOM"){
		$("#roomNo").show();
		$("#emcApprove").hide();
		$("#select-payment-card").parent().hide();					
		}
	
	$('#select-payment-type').change(function(){
		paymenttype =  $("#select-payment-type"+' option:selected').text();
		if(paymenttype=="CASH" ){
			$("#select-payment-card").parent().hide();
			$("#roomNo").hide();
			$("#emcApprove").hide();
		}
		else if(paymenttype=="CREDIT" ){
			
			$("#select-payment-card").parent().show();
			$("#roomNo").hide();
			$("#emcApprove").hide();
			}
		else if(paymenttype=="NC"){
			$("#emcApprove").show();
			$("#roomNo").hide();
			$("#select-payment-card").parent().hide();
			
			}	
		else if(paymenttype=="ROOM"){
			$("#roomNo").show();
			$("#emcApprove").hide();
			$("#select-payment-card").parent().hide();					
			}
	});
	$("#setPaymentDetails").on("click",function() {
		paymenttype =  $("#select-payment-type"+' option:selected').text();
		
		if(paymenttype=="CASH"){
			saveValue = "CASH";
			if(saveValue=="" || saveValue==undefined){
				return false;
			}
		}
		else if(paymenttype=="CREDIT"){
			saveValue = $("#select-payment-card"+' option:selected').text();
			if(saveValue=="" || saveValue==undefined){
				return false;
			}
		}
		else if(paymenttype=="NC"){
			saveValue = $("#emcApprove").val();
			if(saveValue=="" || saveValue==undefined){
				return false;
			}
		}
		else if(paymenttype=="ROOM"){
			saveValue = $("#roomNo").val();
			if(saveValue=="" || saveValue==undefined){
				return false;
			}
		} 
		
	if(paymenttype!=null && paymenttype!="undefiend" && saveValue!=null){
		
		$("#popupPaymentCard").popup('close');
		setCheckStatus(tableId, htmlTableId, tableName, checkId, "Paid",paymenttype,saveValue);
		$("#check" + checkId).css("background", "linear-gradient(to right, lightblue, lightblue 80%, white)");
		$("#check" + checkId).css("background", "-webkit-linear-gradient(left, lightblue, lightblue 80%, white)");
	}
	});
	return false;
}
function setCheckStatusReady(tableId, htmlTableId, tableName, checkId) {
	setCheckStatus(tableId, htmlTableId, tableName, checkId, "READYTOPAY");
}
function setCheckStatusUnpaid(tableId, htmlTableId, tableName, checkId) {
	setCheckStatus(tableId, htmlTableId, tableName, checkId, "Unpaid");
	$("#check" + checkId).css("background", "linear-gradient(to right, white, indianred 80%, indianred)");
	$("#check" + checkId).css("background", "-webkit-linear-gradient(left, white, indianred 80%, indianred)");
}
/***********
 * BEGIN setCheckStatus
 */
function setCheckStatus(tableId, htmlTableId, tableName, checkId, status,paymenttype,savedEntity) {
console.log("DEBUG > BEGIN setCheckStatus");	
///order/setCheckStatus?checkId=<checkId>&status=<Paid|Unpaid|READYTOPAY>
var statusApi = comCookedSpeciallyApiPrefix + "/order/setCheckStatus?checkId=" + checkId + "&status=" + status+ "&paymentType="+paymenttype+"&paymentDetail="+savedEntity;

	$.ajax(statusApi, {
		isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + statusApi);
			console.log(JSON.stringify(data));
		}
		//initHomePage();
		addCheckToHomePage(tableId, htmlTableId, tableName, data);
	})
	.fail(function() { 
		console.log("ajax error: " + statusApi); 
	})
	.always(function() { 
		console.log("ajax complete: " + statusApi); 
	});
}
/* 
 * END setCheckStatus
 **********/

function setTableStatusAvailable(tableId) {
	setTableStatus(tableId, "Available");
	$("#table" + tableId + " #tableLinks #Available").hide();
	$("#table" + tableId + " #tableLinks #Busy").show();
	$("#table" + tableId + " #tableLinks #Reserved").show();
}
function setTableStatusBusy(tableId) {
	setTableStatus(tableId, "Busy");
	$("#table" + tableId + " #tableLinks #Available").show();
	$("#table" + tableId + " #tableLinks #Busy").hide();
	$("#table" + tableId + " #tableLinks #Reserved").show();
}
function setTableStatusReserved(tableId) {
	setTableStatus(tableId, "Reserved");
	$("#table" + tableId + " #tableLinks #Available").show();
	$("#table" + tableId + " #tableLinks #Busy").show();
	$("#table" + tableId + " #tableLinks #Reserved").hide();
}
/***********
 * BEGIN setTableStatus
 */
function setTableStatus(tableId, status) {
console.log("DEBUG > BEGIN setTableStatus");	
//	http://www.bakedspecially.com:8080/CookedSpecially/seatingTable/setStatus?tableId=1&status=Available
var statusApi = comCookedSpeciallyApiPrefix + "/seatingTable/setStatus?tableId=" + tableId + "&status=" + status;

	$.ajax(statusApi, {
		isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + statusApi);
			console.log(JSON.stringify(data));
		}
		//initHomePage();
		$("div#table" + tableId + " span#tableStatus").html(status);
	})
	.fail(function() { 
		console.log("ajax error: " + statusApi); 
	})
	.always(function() { 
		console.log("ajax complete: " + statusApi); 
	});
}
/* 
 * END setTableStatus
 **********/


/***********
 * BEGIN updateAjaxDataRemote
 */
function updateAjaxDataRemote() {
//console.log("DEBUG > BEGIN updateAjaxDataRemote");	

	$.ajax(tableApi, {
		isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + tableApi);
			tableDataString = JSON.stringify(data);
			console.log(tableDataString);
		}
		tableData = JSON.parse(tableDataString);

		$.ajax(allChecksApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + allChecksApi);
				allChecksDataString = JSON.stringify(data);
				console.log(allChecksDataString);
			}
			allChecksData = JSON.parse(allChecksDataString);

			initHomePage();
		
		})
		.fail(function() { 
			console.log("ajax error: " + allChecksApi); 
		})
		.always(function() { 
			console.log("ajax complete: " + allChecksApi); 
		});
			
	})
	.fail(function() { 
		console.log("ajax error: " + tableApi); 
	})
	.always(function() { 
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

	$.ajax(restaurantApi, {
		isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + restaurantApi);
			var restaurantDataString = JSON.stringify(data);
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
	})
	.fail(function() { 
		console.log("ajax error: " + restaurantApi); 
	})
	.always(function() { 
		console.log("ajax complete: " + restaurantApi); 
	});
	
console.log("DEBUG > END updateRestaurantInfo");	
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
		updateAjaxDataRemote();
	});

} catch (error) {
	console.error("Your javascript has an error: " + error);
}


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
 
 
 
 
 	
