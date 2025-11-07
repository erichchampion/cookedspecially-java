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
_emailCheck = comCookedSpeciallyApiPrefix + '/order/emailCheck?',
_getDeliveryBoy = comCookedSpeciallyApiPrefix + '/order/allDeliveryBoy.json?';
_getOrderByOrderId=comCookedSpeciallyApiPrefix+'/order/getOrderbyOrderId.json?'

var restaurantApi = comCookedSpeciallyApiPrefix + "/restaurant/getrestaurantinfo?restaurantId=" + restaurantId;
var menuApi = comCookedSpeciallyApiPrefix + "/menu/getallmenusjson/" + restaurantId;
var checkApi = comCookedSpeciallyApiPrefix + "/order/getCheckWithOrders.json?tableId=1&restaurantId=" + restaurantId;
var tableApi = comCookedSpeciallyApiPrefix + "/seatingTable/getRestaurantTables.json?restaurantId=" + restaurantId;
var allChecksApi = comCookedSpeciallyApiPrefix + "/order/allChecksWithOpenOrders.json?restaurantId=" + restaurantId;
var db;
var strConnectionAlert = "You appear to be online, but we have a connection problem. Please click Ok to try again.";

var changeCount = 0;
var orderCount = 0;
var chooseDeliveryBoy = "No Agent Selected";
var moneyIn;
var moneyOut;
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
var orderId=0;
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

function printDiv(divID) {
    //Get the HTML of div
	var content = $(divID).html();
	  var frame1 = $('<iframe />');
	  frame1[0].name = "frame1";
	  frame1.css({ "position": "absolute", "top": "-1000000px" });
      $("body").append(frame1);
      var frameDoc = frame1[0].contentWindow ? frame1[0].contentWindow : frame1[0].contentDocument.document ? frame1[0].contentDocument.document : frame1[0].contentDocument;
      frameDoc.document.open();
      frameDoc.document.write(content);
      frameDoc.document.close();
      setTimeout(function () {
          window.frames["frame1"].focus();
          window.frames["frame1"].print();
          frame1.remove();
      }, 500); 
}

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
		if(currentCheck.kitchenScreenId == kitchenScreenId) {
		var tableName = currentCheck.checkType;
		var tableId = currentCheck.tableId;
		var deliveryTime=currentCheck.deliveryTime;
		var deliveryAddress = currentCheck.deliveryAddress;
		var name = currentCheck.name;
		var phone =  currentCheck.phone;
		var bill = currentCheck.bill;
	/*	var additionalChargesValue1 = currentCheck.additionalChargesValue1;
		var additionalChargesValue2 = currentCheck.additionalChargesValue2;
		var additionalChargesValue3 = currentCheck.additionalChargesValue3;
		var outCircleDeliveryCharges = currentCheck.outCircleDeliveryCharges;*/
		var total=currentCheck.roundOffTotal;
		var tableStatus = "Busy";
		var customerId = currentCheck.customerId;
		var guests = "1";
		if(tableName!=null){
		if (tableName.toLowerCase() == "delivery" && currentCheck.deliveryArea != null) {
			tableName = tableName + ": " + currentCheck.deliveryArea;
		}}

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
    		currentOrder.deliveryAddress=deliveryAddress;
    		currentOrder.phone=phone;
    		currentOrder.total =total;
			allOrders.push(currentOrder);
		});}
		}else{
			
			var tableName = currentCheck.checkType;
			var tableId = currentCheck.tableId;
			var deliveryTime=currentCheck.deliveryTime;
			var deliveryAddress = currentCheck.deliveryAddress;
			var name = currentCheck.name;
			var phone =  currentCheck.phone;
			var bill = currentCheck.bill;
		/*	var additionalChargesValue1 = currentCheck.additionalChargesValue1;
			var additionalChargesValue2 = currentCheck.additionalChargesValue2;
			var additionalChargesValue3 = currentCheck.additionalChargesValue3;
			var outCircleDeliveryCharges = currentCheck.outCircleDeliveryCharges;*/
			var total=currentCheck.roundOffTotal;
			var tableStatus = "Busy";
			var customerId = currentCheck.customerId;
			var guests = "1";
			if(tableName!=null){
			if (tableName.toLowerCase() == "delivery" && currentCheck.deliveryArea != null) {
				tableName = tableName + ": " + currentCheck.deliveryArea;
			}}

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
	    		currentOrder.deliveryAddress=deliveryAddress;
	    		currentOrder.phone=phone;
	    		currentOrder.total =total;
				allOrders.push(currentOrder);
			});
			
		}
	});
	function sortAscending(a,b){
	    return (new Date(a.deliveryTime) - new Date(b.deliveryTime));
	}
	allOrders.sort(sortAscending);
	
	// iterate through data file and create orders in homepage
	$.each(allOrders, function() {
		var currentOrder = this;
		if (currentOrder.status.toLowerCase() != "delivered" && currentOrder.status.toLowerCase() != "delivered") {
			var myOrderArray = [];
			myOrderArray.push(this);
			addOrdersToHomePage("#table" + currentOrder.tableId, currentOrder.tableName, myOrderArray,currentOrder.deliveryTime,currentOrder.name,currentOrder.deliveryAddress,currentOrder.phone,currentOrder.total);
		}
	});
	// iterate through data file and create orders in homepage
	$.each(allOrders, function() {
		if (this.status.toLowerCase() == "delivered") {
			var tableName = this.tableName;
			var tableId = this.tableId;
			var tableStatus = this.tableStatus;
			var guests = this.guests;
			var deliveryTime=this.deliveryTime;
			var name = this.name;
			var deliveryAddress=this.deliveryAddress;
			var phone =this.phone;
			var total =this.total;
			$("#topMenu #table" + tableId).remove();
			var myOrderArray = [];
			myOrderArray.push(this);
			addOrdersToHomePage("#table" + tableId, tableName, myOrderArray,deliveryTime,name,deliveryAddress,phone,total);
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
var inAll = new Array();
var outAll = new Array();
var listValO1=0;
var listValO2=0;
var listValO3=0;
var listValI1=0;
var listValI2=0;
var listValI3=0;
function addOrdersToHomePage(tableId, tableName, data,deliveryTime,name,address,phone,total) {
	console.info("DEBUG > BEGIN addOrdersToHomePage: " + tableId); 
	
	$.each(data, function() {
		var orderId = this.orderId;
		var checkId = this.checkId;
		var restaurant  = this.restaurantId;
		var htmlOrderId = "#order" + orderId;
		var orderDate = new Date(this.createdTime);
		var orderTime = orderDate.toLocaleDateString() + " @ " + orderDate.toLocaleTimeString();
		var orderStatus = this.status.toLowerCase();
		var deliveryAgent = this.deliveryAgent;
		var moneyIn= this.moneyIn;
		var moneyOut=this.moneyOut;
		var paymentStatus=this.paymentStatus;
		var invoice=Math.round(total);
		if(deliveryAgent==null || deliveryAgent==undefined){
		deliveryAgent= "No Agent Selected"; }
		var moneyInGetVal= moneyOut/500;
		moneyInGetVal=Math.floor(moneyInGetVal);
	   if(moneyOut==0 && moneyIn==0 && orderStatus!="confirmdelivery"){
			moneyOut=moneyOut;
		}
		else if(moneyIn==0 && orderStatus=="confirmdelivery"){
			moneyOut=moneyOut;
			moneyIn=moneyIn;
		}
		var customerId = this.customerId;
		var time = new Date(deliveryTime);
		//var curretTime =time.getDate()+'-'+(time.getMonth()+1)+'-'+time.getFullYear()+' @ '+time.getHours()+':'+time.getMinutes()+':'+time.getSeconds();
		var curretTime =time.toLocaleString();
		var orderTimes=curretTime;
		if((orderStatus =="outdelivery" || orderStatus =="confirmdelivery") && (paymentStatus=="CASH" || paymentStatus=="COD")){
		var theOrder = "" + 
			'<ul id="' + tableId + '" data-role="listview" data-inset="true" class="ui-listview ui-listview-inset ui-shadow">' +
				'<li id="order' + orderId + '" class="ui-li ui-li-static ui-btn-up-c">' +
				'<div style="float:right; width: 27%" data-role="main" >'+	
				'<p id="links">' +
					'This Order: <a href="javascript:void(0);" onclick="$(\'#order' + orderId + '\').print();return(false);">Print</a>' +
					'<p id="link">This Delivery : <a href="javascript:void(0);" onclick="editMoneyOut(' + orderId + ');return(false);">Assign</a>&nbsp&nbsp'+
					'<a href="javascript:void(0);" onclick="editMoneyIn(' + orderId + ');return(false);">Collect Cash</a></p>'+
					'Delivery Time :<strong><font size="4">' + orderTimes +'</font></strong></p>'+
					'</div>'+
					'<div  style="float:right; width: 25% " data-role="main">'+
					'<tr><font size=2> Delivery Boy :</font><span id="deliveryAgent'+orderId+'">'+deliveryAgent +'</span></tr><br>'+
					'<tr><font size=2> Payment Status :</font><span id="paymentStatus'+orderId+'">'+paymentStatus+'</span></tr><br>'+
					'<tr><font size=2> Invoice Amount :</font><span id="invoice'+orderId+'">'+invoice+'</span></tr><br>'+
					'<tr><font size=2> Addition Change :</font><span id="moneyOut'+orderId+'">'+moneyOut+'</span></tr><br>'+
					'<tr><font size=2> Amount Received: :</font><span id="moneyIn'+orderId+'">'+moneyIn+'</span></tr><br>'+
					'</div>'+
					'<div style="float:right; width: 30%;"> '+
					'<h3 class="ui-li-heading">' +
					'' + tableName +
					'</h3>' +
					'<p id="order' + orderId + '-info" >' +
					'<strong>' + orderId + '</strong> (<span id="orderStatus">' + orderStatus + '</span>) Ordered on ' + orderTime + '<br>'+
					'<strong>Customer Information :</strong>'+'<br>'+
					''+name+'<br>'+
					''+address+'<br>'+
					''+phone+''+
					'</p>' +
					'</div>'+
					'<div  style="float:left; ">'+
					'</div>'+
				'</li>'+
			'</ul>';
			}
		$(theOrder).appendTo("#topMenu");
		/*if (orderStatus == "ready") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightblue, white 80%, indianred)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightblue, white 80%, indianred)");
			var theLink = '' +
				'<a href="javascript:void(0);" class="addpopupOut" onclick="setOrderStatusOD(' + orderId + ');return(false);">Out for Delivery</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}*/
		if (orderStatus == "outdelivery") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightblue, white 80%, indianred)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightblue, white 80%, indianred)");
			var theLink = '' +
				'<a href="javascript:void(0);"  class="addpopupIn" onclick="setOrderStatusDelivered(' + orderId + ');return(false);">Delivered</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}
		/*if (orderStatus == "confirmdelivery") {
			$("#order" + orderId).css("background", "linear-gradient(to right, lightblue, white 80%, indianred)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightblue, white 80%, indianred)");
			var theLink = '' +
				'<a href="javascript:void(0);"  onclick="setOrderStatusComplete(' + orderId + ');return(false);">Complete</a>' + 
				'<a href="javascript:void(0);" onclick="setOrderStatusCancelled(' + orderId + ');return(false);">Cancel</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
			}*/
		if(orderStatus == "delivered"){
			$("#order" + orderId).css("background", "linear-gradient(to right, lightblue, lightblue 80%, white)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(left, lightblue, lightblue 80%, white)");		
			}
		if (orderStatus == "cancelled") {
			$("#order" + orderId).css("background", "linear-gradient(to bottom, darkgray, lightgray 80%, white)");
			$("#order" + orderId).css("background", "-webkit-linear-gradient(top, darkgray, lightgray 80%, white)");
			var theLink = '' +
				'<a href="javascript:void(0);" onclick="setOrderStatusPending(' + orderId + ');return(false);">Reactivate</a>';
			$(theLink).appendTo(htmlOrderId + " p#links");
		}
		var theLink = '' +
			'<br/>This Check: <a href="javascript:void(0);" onclick="javascript:printDiv(\'#check' + checkId + '\');"">Print</a>';
		$(theLink).appendTo(htmlOrderId + " p#links");
		$.each(this.orderDishes, function() {
			var name = this.name;
			var quantity = this.quantity;
			var price = this.price;
			var instructions=this.instructions;
			var theItem = '' +
				'<p >' +
				'	<strong>' + name + '</strong><font size=4> ' + quantity + '</font>  @ ' + currency + price +
				'</p>';
			$(theItem).appendTo(htmlOrderId);
		});
		
		// getting delivery agent by json object
		
		
		//http://www.cookedspecially.com:8080/CookedSpecially/order/generateCheckForPrint?templateName=saladdaysbill&checkId=193
		
		if(restaurant==21){
			generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=saladdaysbill&checkId=" + checkId;
			}
			else if(restaurant==30){
				generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=gombeibill&checkId=" + checkId;
			}
			else{
				generateCheckApi = comCookedSpeciallyApiPrefix + "/order/generateCheckForPrint?templateName=saladdaysbill&checkId=" + checkId;

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
		})
		getCheck.complete(function() { 
			console.log("ajax complete: " + generateCheckApi); 
		});
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

function setOrderStatus(orderId,money,status,deliveryBoy,paymentStatus) {
	console.log("DEBUG > BEGIN setOrderStatus");
	var statusApi = comCookedSpeciallyApiPrefix + "/order/setOrderStatus?orderId=" + orderId + "&status=" + status+"&money="+money+"&deliveryBoy="+deliveryBoy+"&paymentType="+paymentStatus;
	$.ajax(statusApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: for status " + statusApi);
				console.log(JSON.stringify(data));
				
			}
			var param1var = getQueryVariable("KscreenId");
			updateAjaxDataRemote(param1var);
		})
		.fail(function() { 
			console.log("ajax error: for status " + statusApi); 
		})
		.always(function() { 
			console.log("ajax complete: status " + statusApi); 
		});
	}

function getDeliveryAgent(delBoy){
	var deliveryAgentInfoUrl = _getDeliveryBoy + "restaurantId=" + restaurantId;
	$.ajax({			
	  crossDomain: true,
	  type: 'GET',
	  url: deliveryAgentInfoUrl,
	  dataType: 'json'
     })
	.done (function (data) {
		var theOptions = '<option id="placeholder"  value="' + chooseDeliveryBoy + '">' + chooseDeliveryBoy + '</option>';
		var optionCount = 0;
		$('#select-delivery-agent').html('');
		$("#select-delivery-agent-button > span").text(delBoy);
	
		$.each(data, function() {
			theOptions = theOptions + '<option value="' + this.name + '">' + this.name + '</option>';
			optionCount ++;
		});
		if (optionCount > 1) {
			$(theOptions).appendTo("#select-delivery-agent");
		}
		else{
				$('#select-delivery-agent-button').css("display","none");
				$("#select-delivery-agent-button > span").text("");
		}
		 $("#select-delivery-agent option:contains(" +delBoy+ ")").attr('selected', 'selected'); 
	})
	.fail(function() {
		debugLog("AJAX: get delivery zone info FAILED. " + _getDeliveryBoy); 
	})
	.always(function() { 
		debugLog("AJAX: get delivery Agent zone info COMPLETED"); 
		//setTimeout(function(){ $.mobile.loading( "hide" );$(".ui-fixed-hidden").removeClass("ui-fixed-hidden");$( "#popupContact"  ).popup( 'open' ); },1000);
	});
	}


function setmoneyOutPopUP(invoice,mnyOut){
	var getval=invoice/500;
 	getval=Math.floor(getval);
	if((getval%2)==0){
		var ceil1 =invoice/500;              //example 240/500 = 0.48
		var ceilMulTiple=Math.ceil(ceil1); //  1
		var compareVal =ceilMulTiple*500;  //  1*500=500
		moneyOut =compareVal+500-invoice; 
		moneyOut=Math.round(moneyOut);     //  260,760(D)
		moneyIn=invoice+moneyOut;           //  260+240=1000;
		moneyIn=Math.round(moneyIn);	   // default should be 500,1000(D),0
		listValO1=moneyOut;
		listValO2=moneyOut-500;
		listValO3=0;
	}
	else if((getval%2)!=0){
		var ceil1 =invoice/1000;				// example 740/1000 = 0.74;
		var ceilMulTiple=Math.ceil(ceil1);	// 1
		var compareVal =ceilMulTiple*1000;  // 1*1000 = 1000;
		moneyOut=compareVal-invoice;          // 1000-740 = 260
		moneyOut=Math.round(moneyOut);		//260(D),760
		moneyIn=invoice+moneyOut;				// 740+260=1000;
		moneyIn=Math.round(moneyIn);  // default should be 1000(D),1500,0
		listValO1=moneyOut;
		listValO2=moneyOut+500;
		listValO3=0;
	}
	var sel= $("#select-payment-mode").val();
	if(sel=="CASH" || sel=="COD"){
	$("#moneyOut").show();
	$("#moneyOutVal1").removeAttr('disabled');
	$("#moneyOutVal2").removeAttr('disabled');
	$("#moneyOutVal3").removeAttr('disabled');
	if(mnyOut==undefined || mnyOut=="0"){
		$("#moneyOut").val(listValO1);
	}
	else{
		$("#moneyOut").val(mnyOut);
	}
	$("#moneyOutVal1").val(listValO1);
	$("#moneyOutVal2").val(listValO2);
	$("#moneyOutVal3").val(listValO3);
	}
	else{
		$("#moneyOut").hide();
		$("#moneyOutVal1").attr('disabled','disabled');
		$("#moneyOutVal2").attr('disabled','disabled');
		$("#moneyOutVal3").attr('disabled','disabled');
		$("#moneyOut").val("0");
	}
	}



function getval(sel){

	if(sel.value =="CASH" || sel.value=="COD"){
		$("#moneyOut").show();
		$("#moneyOutVal1").removeAttr('disabled');
		$("#moneyOutVal2").removeAttr('disabled');
		$("#moneyOutVal3").removeAttr('disabled');
		$("#moneyOut").val(listValO1);
}else{
	
	$("#moneyOut").hide();
	$("#moneyOutVal1").attr('disabled','disabled');
	$("#moneyOutVal2").attr('disabled','disabled');
	$("#moneyOutVal3").attr('disabled','disabled');
	$("#moneyOut").val("0");
	
	
}
}
function setMoneyInPopUp(moneyIn,mnyIn){
	
	if(mnyIn==undefined){
		$("#moneyIn").val(moneyIn);
	}
	else{
		$("#moneyIn").val(mnyIn);
	}
	$("#moneyInVal1").val(moneyIn);
	$("#moneyInVal2").val("0");
}
var setOrderId= new Array(1);
function setOrderStatusPending(orderId) {
	var money=0;
	setOrderStatus(orderId,money,"Pending","NOt set");
	orderId=0;
}
/*function setOrderStatusOD(orderId) {
	var delBoy=$('#deliveryAgent'+orderId).text();
	setOrderId[0]=orderId;	
	var mnyOut= $('#moneyOut'+orderId).text();
	var invoice= $('#invoice'+orderId).text();
	setmoneyOutPopUP(invoice);
	getDeliveryAgent(delBoy);
	$('#popupMoneyOut').popup();
	$('#popupMoneyOut').popup('open');
	$('#moneyOutVal1').on("click",function() {
		var moneyOutVal1=$('#moneyOutVal1').val();
		$('#moneyOut').val(moneyOutVal1);
	});
	$('#moneyOutVal2').on("click",function() {
		var moneyOutVal2=$('#moneyOutVal2').val();
		$('#moneyOut').val(moneyOutVal2);
	});
	$('#moneyOutVal3').on("click",function() {
		var moneyOutVal3=$('#moneyOutVal3').val();
		$('#moneyOut').val(moneyOutVal3);
	});
	$('#setMoneyOut').on("click",function() {
		var moneyOut=0;
		moneyOut=$("#moneyOut").val();
		var deliveryBoy =  $("#select-delivery-agent"+' option:selected').text();
		if(moneyOut!='' && deliveryBoy!=chooseDeliveryBoy && orderId==setOrderId[0]){
		setOrderStatus(setOrderId[0],moneyOut,"Outdelivery",deliveryBoy);
		orderId=0;
		}}); 
}*/
function setOrderStatusDelivered(orderId) {
	$('#popupMoneyIn').popup();
	$('#popupMoneyIn').popup('open');
	setOrderId[0] = orderId;
	moneyIn= $('#moneyIn'+orderId).text();
	var invoice= $('#invoice'+orderId).text();
	var mnyOut= $('#moneyOut'+orderId).text();
	var moneyInn=parseInt(invoice)+parseInt(mnyOut);
    $("#moneyIn").val(moneyInn);
	setMoneyInPopUp(moneyInn);
	$('#moneyInVal1').on("click",function() {
		var moneyInVal1=$('#moneyInVal1').val();
		$('#moneyIn').val(moneyInVal1);
	});
	$('#moneyInVal2').on("click",function() {
		var moneyInVal2=$('#moneyInVal2').val();
		$('#moneyIn').val(moneyInVal2);
	});
	$('#setMoneyIn').on("click",function() {
		var moneyIn=0;
		moneyIn=$("#moneyIn").val();
		if(moneyIn !=''&&orderId==setOrderId && setOrderId[0]==orderId){
			setOrderStatus(setOrderId[0],moneyIn,"Delivered","set");
			orderId=0;
		}});
}


function editMoneyOut(orderId){
	var count =0;
	setOrderId[0]=orderId;
	var mnyOut= $('#moneyOut'+orderId).text();
	var delBoy=$('#deliveryAgent'+orderId).text();
	var invoice= $('#invoice'+orderId).text();
	
	var sel = $("#paymentStatus"+orderId).text();
	 
	if(sel=="COD"){
		 $('option:selected', 'select[name="select-payment-mode"]').removeAttr('selected');
		$("#select-payment-mode-button > span").text("COD");
		$("#select-payment-mode option:contains(COD)").attr('selected', 'selected'); 
	}
	else{
		$("#select-payment-mode-button > span").text("CASH");
		$("#select-payment-mode option:contains(CASH)").attr('selected', 'selected'); 
		
	}
	setmoneyOutPopUP(invoice,mnyOut);
	getDeliveryAgent(delBoy);
	$('#popupMoneyOut').popup();
	$('#popupMoneyOut').popup('open');
	$('#moneyOutVal1').on("click",function() {
		var moneyOutVal1=$('#moneyOutVal1').val();
		$('#moneyOut').val(moneyOutVal1);
	});
	$('#moneyOutVal2').on("click",function() {
		var moneyOutVal2=$('#moneyOutVal2').val();
		$('#moneyOut').val(moneyOutVal2);
	});
	$('#moneyOutVal3').on("click",function() {
		var moneyOutVal3=$('#moneyOutVal3').val();
		$('#moneyOut').val(moneyOutVal3);
	});
	$('#setMoneyOut').on("click",function() {
		var moneyOut=0;
		moneyOut=$('#moneyOut').val();
		var deliveryBoy =$("#select-delivery-agent"+' option:selected').text();
		var paymentType =$("#select-payment-mode"+' option:selected').val();
		if(moneyOut!='' && deliveryBoy!=chooseDeliveryBoy && orderId==setOrderId[0] && paymentType!=''){
		setOrderStatus(setOrderId[0],moneyOut,"EDITMONEYOUT",deliveryBoy,paymentType);
		orderId=0;
		}}); 
}
function editMoneyIn(orderId){
	var invoice= $('#invoice'+orderId).text();
	var mnyOut= $('#moneyOut'+orderId).text();
	var mnyIn= $('#moneyIn'+orderId).text();
    setOrderId[0]=orderId;
	var moneyInn=parseInt(invoice)+parseInt(mnyOut);
	setMoneyInPopUp(moneyInn,mnyIn);
	$('#popupMoneyIn').popup();
	$('#popupMoneyIn').popup('open');
	$('#moneyInVal1').on("click",function() {
		var moneyInVal1=$('#moneyInVal1').val();
		$('#moneyIn').val(moneyInVal1);
	});
	$('#moneyInVal2').on("click",function() {
		var moneyInVal2=$('#moneyInVal2').val();
		$('#moneyIn').val(moneyInVal2);
	});
	$('#setMoneyIn').on("click",function() {
		 var moneyIn=0;
		 moneyIn=$('#moneyIn').val();
		if(moneyIn !=''&& orderId==setOrderId[0]){
		setOrderStatus(setOrderId[0],moneyIn,"editmoneyin");
		  orderId=0;
		}});
	  
}
/*function setOrderStatusComplete(orderId) {
	var money=0;
	setOrderStatus(orderId,money,"Delivered","set");
	orderId=0;
}*/

function setOrderStatusCancelled(orderId) {
	var money=0;
	if (confirm("Are you sure?")) {
		setOrderStatus(orderId,money,"Cancelled","lost");
		orderId=0;
	}
}
/***********
 * BEGIN setOrderStatus
 */

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
			
			for (var key in allChecksData) {

				if (allChecksData.hasOwnProperty(key)) {
						nativCounter++;
					}	
			}
			initHomePage(kitchenScreenId);
			
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
	})
	.fail(function() { 
		console.log("ajax error: " + restaurantApi); 
	})
	.always(function() { 
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


 
 
 	
